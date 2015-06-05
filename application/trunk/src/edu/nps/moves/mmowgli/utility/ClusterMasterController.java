/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
 */

package edu.nps.moves.mmowgli.utility;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

public interface ClusterMasterController
{
  public boolean amIMaster();

  // Alternative implementations of this interface:

  /* Always return true, for non-clustered implementations */
  public static class SingleDeployment implements ClusterMasterController
  // *********************************************************************
  {
    @Override
    public boolean amIMaster()
    {
      return true;
    }
  }

  /* Get the name of the master from the web.xml config file */
  public static class WebXmlParameterReader implements ClusterMasterController
  // **************************************************************************
  {
    Boolean imMaster = null;

    @Override
    public boolean amIMaster()
    {
      if (imMaster != null)
        return imMaster;

      AppMaster mas = AppMaster.instance();
      String masterCluster = mas.getInitParameter(WEB_XML_CLUSTERMASTER_NAME_KEY);
      String myClusterNode = mas.getServerName();
      MSysOut.println(REPORT_LOGS, "  master (from web.xml) is " + masterCluster);
      MSysOut.println(REPORT_LOGS, "  this one (from InetAddress.getLocalHost().getAddress() is " + myClusterNode);

      imMaster = myClusterNode.contains(masterCluster) || masterCluster.contains(myClusterNode);
      return imMaster;
    }
  }

  /* Uses an installed Zookeeper to coordinate the selection of the master */
  public static class ZookeeperLeaderElection implements ClusterMasterController
  // ************************************************************************************
  {
    public static String ZOOKEEPER_DEFAULT_ADDR = "web4:2181";
    public static String ZOOKEEPER_LEADER_PATH1 = "/mmowgli/";
    public static String ZOOKEEPER_LEADER_PATH2 = "/leaderselection";

    private RetryPolicy retryPolicy;
    private CuratorFramework client;
    private LeaderSelector selector;

    private String myGameDeploymentName;
    private String myClusterName;
    private String zookeeperZnode;
    private String zookeeperAddr;

    private boolean iAmLeader = false;

    public ZookeeperLeaderElection()
    {
      AppMaster mas = AppMaster.instance();
      zookeeperAddr = mas.getInitParameter(WEB_XML_ZOOKEEPER_ADDRESS_KEY);
      if (zookeeperAddr == null) {
        MSysOut.println(ERROR_LOGS, " ZookeeperLeaderElection does not find " + WEB_XML_ZOOKEEPER_ADDRESS_KEY + " in web.xml; using default");
        zookeeperAddr = ZOOKEEPER_DEFAULT_ADDR;
      }
      myClusterName = mas.getServerName();
      myGameDeploymentName = mas.getInitParameter(WEB_XML_DEPLOYMENT_KEY);
      zookeeperZnode = ZOOKEEPER_LEADER_PATH1 + myGameDeploymentName + ZOOKEEPER_LEADER_PATH2;

      MSysOut.println(SYSTEM_LOGS, "  zookeeper address: " + zookeeperAddr);
      MSysOut.println(SYSTEM_LOGS, "  zookeeper znode for leader selection = " + zookeeperZnode);

      start();
    }

    private void start()
    {
      retryPolicy = new ExponentialBackoffRetry(1000, 3);
      client = CuratorFrameworkFactory.newClient(zookeeperAddr, retryPolicy);
      client.start();

      selector = new LeaderSelector(client, zookeeperZnode, new LeaderListener());
      selector.autoRequeue(); // (curator note: not required, but this is behavior that you will probably expect)
      selector.start();
    }

    class LeaderListener extends LeaderSelectorListenerAdapter
    {
      public void takeLeadership(CuratorFramework client) throws Exception
      {
        // This callback will get called when you are the leader.
        // Do whatever leader work you need to and only exitthis method when you want to relinquish leadership.

        // So on entry, we are now the leader, or "cluster master"--the term we use in mmowgli.

        MSysOut.println(SYSTEM_LOGS, "Zookeeper has appointed this node, " + myClusterName + " as cluster master.");
        iAmLeader = true;
        AppMaster.instance().handleClusterMaster(true);
        
        synchronized (ZookeeperLeaderElection.this) {
          try {
            ZookeeperLeaderElection.this.wait();
            // Mmowgli uses Zookeeper to test for a killed clustermaster;  we don't purposely relinquish the cluster master role;
            // so we normally never arrive at this point.
            // If we were to explicitly relinquish, we'd awake in the catch clause.
          }
          catch (InterruptedException e) {
            MSysOut.println(SYSTEM_LOGS, "LeaderListener has been interrupted, relinquishing cluster master role.");
          }
          
          iAmLeader = false;
          AppMaster.instance().handleClusterMaster(false);
        }
      }
    }

    public void relinquishLeaderRole()
    {
      synchronized (ZookeeperLeaderElection.this) {
        ZookeeperLeaderElection.this.notify();
      }
    }

    @Override
    public boolean amIMaster()
    {
      return iAmLeader;
    }
  }

  /* Used file locking on a shared file. Note: this doesn't work as-is on Samba shared filesystems */
  public static class SharedFileLockGetter implements ClusterMasterController
  // *************************************************************************
  {
    public static String LOCKFILE_NAME = "masterLock";
    FileLock locked = null;

    @Override
    public boolean amIMaster()
    {
      if (locked != null)
        return true;

      String masterLockDirPath = AppMaster.instance().getMasterLockPath();
      if (masterLockDirPath == null) {
        MSysOut.println(ERROR_LOGS, "No master lock specified and SharedFileLockGetter called!");
        return false;
      }

      try {
        Path dirPath = Paths.get(masterLockDirPath);
        if (!dirPath.toFile().exists())
          dirPath.toFile().mkdirs();

        File f = new File(dirPath.toFile(), LOCKFILE_NAME);
        f.createNewFile();
        Path toFile = Paths.get(f.getAbsolutePath());

        FileChannel fileChannel = FileChannel.open(toFile, StandardOpenOption.READ, StandardOpenOption.WRITE);
        locked = fileChannel.tryLock(); // exclusive lock
        if (locked != null) {
          MSysOut.println(SYSTEM_LOGS, "Exclusive file lock acquired on " + toFile.toString() + ": this node is AppMaster");
          return true;
        }
        else {
          MSysOut.println(SYSTEM_LOGS, "Exclusive file lock NOT acquired on " + toFile.toString() + ": this node is NOT AppMaster");
          return false;
        }
      }
      catch (IOException ex) {
        MSysOut.println(ERROR_LOGS, "Exception in SharedFileLockGetter.amIMaster: " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
        return false;
      }
    }
  }

  /*
   * This would use the servlet in this package to coordinate. To work, it needs to be deployed once, separately, at a common url. It would need to be reset at
   * the top of the multiple nodes deploy script.
   */

  public static class FirstInServletReader implements ClusterMasterController
  // *************************************************************************
  {
    @Override
    public boolean amIMaster()
    {
      String myName = AppMaster.instance().getServerName();

      String servletUrl = AppMaster.instance().getAppUrlString() + "/" + GetSetMasterServlet.deployment;// http://mmowgli.nps.edu/gamename/_getSetMasterNode
      String nameParam = GetSetMasterServlet.nameParameter; // nodeName

      HttpGet get = new HttpGet(servletUrl + "?" + nameParam + "=" + myName);

      HttpClient client = HttpClients.createDefault();
      try {
        HttpResponse resp = client.execute(get);
        HttpEntity ent = resp.getEntity();

        if (ent.getContentLength() > 0) {
          InputStream inps = ent.getContent();

          Reader rdr = new InputStreamReader(inps);
          char[] ca = null;
          while (rdr.ready()) {
            ca = new char[(int) ent.getContentLength()];
            rdr.read(ca);
          }
          String result = new String(ca);
          return result.equals(myName); // this is the test
        }
        else {
          MSysOut.println(ERROR_LOGS, "Got null back from GetSetMasterServlet");
        }
      }
      catch (IOException ex) {
        MSysOut.println(ERROR_LOGS, "Exception in FirstInServletReader: " + ex.getClass() + ": " + ex.getLocalizedMessage());
      }

      return false;
    }
  }
}
