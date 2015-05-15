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

package edu.nps.moves.mmowgli.hibernate;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;
import static org.hibernate.cfg.AvailableSettings.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.internal.EventListenerRegistryImpl;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import edu.nps.moves.mmowgli.utility.SysOut;

/**
 * Class to initialize, configure and manage single SessionFactory instance.  Global across all users sessions and servlets in the same context.  That
 * means this is shared by any other entry points, like mmowgliMobile.  This is built as a singleton object, but could theoretically be written as a pure
 * static class, with a private constructor so it is never instantiated (might be difficult to garbage collect it that way).
 */
public abstract class AbstractVHib// implements SessionManager
{
  // private constructor
  protected AbstractVHib()
  { }
  
  public abstract Class<?> getExampleMappedClass();
  
//@formatter:off  
  public static final String DB_DRIVER                     = "com.mysql.jdbc.Driver";
  public static final String DB_DIALECT                    = org.hibernate.dialect.MySQLDialect.class.getName();
  public static final String DB_AUTOCOMMIT                 = "false";
  public static final String DB_CACHE_PROVIDER             = "org.hibernate.cache.NoCacheProvider";
  public static final String DB_HBM2DDL_AUTO_CREATE_DROP   = "create-drop"; // When using this, the OLD TABLES WILL BE DROPPED each run
  public static final String DB_HBM2DDL_AUTO_VALIDATE      = "validate"; // When using this, the OLD TABLES WILL BE BE RETAINED each run
  public static final String DB_SHOW_SQL                   = "false";
  public static final String DB_TRANSACTION_STRATEGY       = "org.hibernate.transaction.JDBCTransactionFactory"; //"org.hibernate.transaction.JTATransactionFactory";
  public static final String DB_CURRENT_SESSION_CONTEXT_CLASS = "thread";
  //public static final String DB_HIBERNATE_CONNECTION_ISOLATION = "1"; //todo understand
  
  // Hibernate search properties
  public static final String HIB_SEARCH_SOURCEBASE_PROPERTY    = "hibernate.search.default.sourceBase";  // where master index is stored
  public static final String HIB_FS_SEARCH_INDEXBASE_PROPERTY  = "hibernate.search.default.indexBase";   // where local index is stored
  public static final String HIB_SEARCH_PROVIDER_PROPERTY      = "hibernate.search.default.directory_provider"; // what kind of dir: fs, mem, slave, etc.
  public static final String HIB_SEARCH_REFRESH_PROPERTY       = "hibernate.search.default.refresh"; // in secs
  public static final String HIB_SEARCH_ANALYZER               = org.hibernate.search.Environment.ANALYZER_CLASS;//"hibernate.search.analyzer";
  public static final String HIB_SEARCH_WORKER_BACKEND         = "hibernate.search.worker.backend";
  public static final String HIB_SEARCH_WORKER_JMS_QUEUE       = "hibernate.search.worker.jms.queue";
  public static final String HIB_SEARCH_WORKER_JMS_CONNECTION_FACTORY = "hibernate.search.worker.jms.connection_factory";
    
  // Hibernate search values
  public static final String HIB_RAM_SEARCH_PROVIDER      = "org.hibernate.search.store.impl.RAMDirectoryProvider";
  public static final String HIB_FS_SEARCH_PROVIDER       = "org.hibernate.search.store.impl.FSDirectoryProvider";
  public static final String HIB_SLAVE_PROVIDER           = "filesystem-slave";
  public static final String HIB_MASTER_PROVIDER          = "filesystem-master";
  public static       String hib_fs_local_path            = ""; // specified in web.xml"/tmp/mmowgliLucene";   // a file system path if FS provider is used
  public static final String HIB_ANALYZER                 = "edu.nps.moves.mmowgli.hibernate.MmowgliSearchAnalyzer"; // "org.apache.lucene.analysis.standard.StandardAnalyzer";
  public static final String HIB_JMS_BACKEND              = "jms";
  public static final String HIB_JMS_FACTORY              = "/ConnectionFactory";
  public static final String HIB_JMS_QUEUE                = "queue/hibernatesearch";
  public static final String HIB_REFRESH                  = "60";  // refresh every minute
  public static final String HIB_SHARED_MASTER_INDEX      = "/mnt/mastervolume/lucenedirs/mastercopy";  // need a nfs directory here
//@formatter:on  
  protected static final int HIBERNATE_TRANSACTION_TIMEOUT_IN_SECONDS = 10;
  private ServiceRegistry sr;
  
  private SessionFactory sf = null;
  
  protected SessionFactory _getSessionFactory()
  {
    return sf;
  }
  
  private Configuration  cnf;
  private boolean initted1=false,initted2=false;;
  
  protected void init1(ServletContext ctx)
  {
    if(initted1)
      return;
    initted1=true;
//@formatter:off    
    hib_fs_local_path       = ctx.getInitParameter(WEB_XML_HIBERNATE_SEARCH_KEY);
    MSysOut.println(HIBERNATE_LOGS,hib_fs_local_path+" read from web.xml param "+WEB_XML_HIBERNATE_SEARCH_KEY);
    String dbUrl            = ctx.getInitParameter(WEB_XML_DB_URL_KEY);
    String dbName           = ctx.getInitParameter(WEB_XML_DB_NAME_KEY);
    String dbUser           = ctx.getInitParameter(WEB_XML_DB_USER_KEY);
    String dbPassword       = ctx.getInitParameter(WEB_XML_DB_PASSWORD_KEY);
    String dbDropAndCreateS = ctx.getInitParameter(WEB_XML_DB_DROPCREATE_KEY);
    
    c3p0Params c3 = new c3p0Params();
    c3.maxSize          = ctx.getInitParameter(WEB_XML_C3P0_MAX_SIZE);          MSysOut.println(HIBERNATE_LOGS,"db c3p0 max: "+c3.maxSize);
    c3.minSize          = ctx.getInitParameter(WEB_XML_C3P0_MIN_SIZE);          MSysOut.println(HIBERNATE_LOGS,"db c3p0 min: "+c3.minSize);
    c3.acquireIncrement = ctx.getInitParameter(WEB_XML_C3P0_ACQUIRE_INCREMENT); MSysOut.println(HIBERNATE_LOGS,"db c3p0 acquire incr: "+c3.acquireIncrement);
    c3.timeout          = ctx.getInitParameter(WEB_XML_C3P0_TIMEOUT);           MSysOut.println(HIBERNATE_LOGS,"db c3p0 timeout: "+c3.timeout);
    c3.idleTestPeriod   = ctx.getInitParameter(WEB_XML_C3P0_IDLE_TEST_PERIOD);  MSysOut.println(HIBERNATE_LOGS,"db c3p0 idletest: "+c3.idleTestPeriod);
//@formatter:on
    try {
      cnf = new Configuration();

      if (!dbUrl.endsWith("/"))
        dbUrl = dbUrl + "/";
      String dbPath = dbUrl + dbName;

      boolean dbDropAndCreate = Boolean.parseBoolean(dbDropAndCreateS);

      // Here are the db properties gotten from web.xml:
      cnf.setProperty(URL, dbPath); // "jdbc:mysql://localhost:3306/mmowgliOne"
      cnf.setProperty(USER, dbUser);
      cnf.setProperty(PASS, dbPassword);

      // Constants gotten from the static imports of org.hibernate.cfg.Environment and edu.nps.moves.mmowgli.mmowgliOne.ApplicationConstants

      cnf.setProperty(DRIVER, DB_DRIVER);
      cnf.setProperty(DIALECT, DB_DIALECT);

      // Omitting this enables c3p0 below.
      //cnf.setProperty(POOL_SIZE, DB_POOL_SIZE);
      
      cnf.setProperty(AUTOCOMMIT, DB_AUTOCOMMIT);
     //todo V7 not reqd? no-cache cnf.setProperty(CACHE_PROVIDER, DB_CACHE_PROVIDER);

      // c3p0 connection pooler
      cnf.setProperty(CONNECTION_PROVIDER , c3.providerClass);
      cnf.setProperty(C3P0_MAX_SIZE, c3.maxSize); // * Maximum size of C3P0 connection pool
      cnf.setProperty(C3P0_MIN_SIZE, c3.minSize); // * Minimum size of C3P0 connection pool
      cnf.setProperty(C3P0_ACQUIRE_INCREMENT, c3.acquireIncrement); // * Number of connections acquired when pool is exhausted
      
      cnf.setProperty(C3P0_TIMEOUT, c3.timeout); // max idle time
      cnf.setProperty(C3P0_IDLE_TEST_PERIOD, c3.idleTestPeriod); // Idle time before a C3P0 pooled connection is validated

      //dbDropAndCreate=true; // dangerous!

      if (dbDropAndCreate)
        cnf.setProperty(HBM2DDL_AUTO, DB_HBM2DDL_AUTO_CREATE_DROP); // When using this, the OLD TABLES WILL BE DROPPED each run
      else
        cnf.setProperty(HBM2DDL_AUTO, DB_HBM2DDL_AUTO_VALIDATE); // When using this, the OLD TABLES WILL BE BE RETAINED each run

      cnf.setProperty(SHOW_SQL,   DB_SHOW_SQL);
      cnf.setProperty(FORMAT_SQL, DB_SHOW_SQL);
      
      cnf.setProperty(TRANSACTION_STRATEGY, DB_TRANSACTION_STRATEGY);
      cnf.setProperty(CURRENT_SESSION_CONTEXT_CLASS, DB_CURRENT_SESSION_CONTEXT_CLASS);
      
      // the following config will kill startup on our deployed games:
      //Feb 18, 2015 2:46:14 PM org.hibernate.engine.jdbc.spi.SqlExceptionHelper logExceptions
      //ERROR: Cannot execute statement: impossible to write to binary log since BINLOG_FORMAT = STATEMENT and at least one table
      //uses a storage engine limited to row-based logging. InnoDB is limited to row-logging when transaction isolation level is 
      //READ COMMITTED or READ UNCOMMITTED.

      //cnf.setProperty(ISOLATION, DB_HIBERNATE_CONNECTION_ISOLATION);  //todo understand implications of this; this setting fixed some of our problems
    }
    catch(Throwable t) {
      commonInitCatch(t);       
    }
  }
  protected Configuration getConfiguration()
  {
    return cnf;
  }
  
  protected void init2()
  {
    if (initted2)
      return;
    initted2 = true;

    try {
      // Set up the mapping
      addAnnotatedClasses(getExampleMappedClass(), cnf);

      StandardServiceRegistryBuilder srb = new StandardServiceRegistryBuilder();
      srb.applySettings(cnf.getProperties());
      srb.addService(EventListenerRegistry.class, new EventListenerRegistryImpl()); // have to add manually
      sr = srb.build();

      sf = cnf.buildSessionFactory(sr); 
    }
    catch (Throwable ex) {
      commonInitCatch(ex);
    }
  }

  private void commonInitCatch(Throwable t)
  {
    // Make sure you log the exception, as it might be swallowed
    System.err.println("Initial SessionFactory creation failed." + t);
    t.printStackTrace(System.err);
    throw new ExceptionInInitializerError(t);
  }

  DatabaseListeners dbLis;

  protected void _installDataBaseListeners()// AppMaster apMas)
  {
    DatabaseListeners dlis = new DatabaseListeners(sr);
    MSysOut.println(HIBERNATE_LOGS,"Installing db listeners");
    EventListenerRegistry registry = ((SessionFactoryImpl) sf).getServiceRegistry().getService(EventListenerRegistry.class);
    registry.addDuplicationStrategy(new DuplicationStrategy()
    {
      @Override
      public boolean areMatch(Object listener, Object original)
      {
        return false;
      }
      @Override
      public Action getAction()
      {
        return null;
      }
    });
    
    if(dlis.getSaveListener() != null)
      registry.getEventListenerGroup(EventType.SAVE).appendListener(dlis.getSaveListener());
    if(dlis.getUpdateListener() != null)
      registry.getEventListenerGroup(EventType.UPDATE).appendListener(dlis.getUpdateListener());
    if(dlis.getSaveOrUpdateListener() != null)
      registry.getEventListenerGroup(EventType.SAVE_UPDATE).appendListener(dlis.getSaveOrUpdateListener());
    if(dlis.getDeleteListener() != null)
      registry.getEventListenerGroup(EventType.DELETE).appendListener(dlis.getDeleteListener());
    if(dlis.getPostInsertListener() != null)
      registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(dlis.getPostInsertListener());
    if(dlis.getPostUpdateListener() != null)
      registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(dlis.getPostUpdateListener());

    MSysOut.println(HIBERNATE_LOGS,"db listeners installed");
    
    dlis.enableListeners(true); // may have to be moved later
  }
 
  protected void configureHibernateSearch()
  {
    cnf.setProperty(HIB_SEARCH_PROVIDER_PROPERTY, HIB_FS_SEARCH_PROVIDER); // use hibernate search (lucene) and use a filesystem dir
    new File(hib_fs_local_path).mkdirs();
    cnf.setProperty(HIB_FS_SEARCH_INDEXBASE_PROPERTY, hib_fs_local_path); // "/tmp/mmowgliLucene/blah";
    cnf.setProperty(HIB_SEARCH_ANALYZER, HIB_ANALYZER);
    cnf.setProperty("hibernate.search.lucene_version",org.apache.lucene.util.Version.LUCENE_36.toString());    
    SysOut.println(HIB_FS_SEARCH_INDEXBASE_PROPERTY+" set to "+hib_fs_local_path);
  }
 
  protected Session _getVHSession()
  {
    return sf.getCurrentSession();
  }
  
  protected Session _openSession()
  {
    return sf.openSession();
  }
  
  private static List<Class<?>> addAnnotatedClasses(Class<?> exampleClass, Configuration cnf)
  {
    // Any class in the db package will be added to Hibernate config
    ArrayList<Class<?>> list = new ArrayList<Class<?>>();
    try {
      ClassLoader cl = exampleClass.getClassLoader();
      String pkg = exampleClass.getPackage().getName();
      URL url = cl.getResource(pkg.replace('.', '/'));
      File dbFiles = new File(url.toURI());
      File[] files = dbFiles.listFiles();
      for (File f : files) {
        String nm = f.getName();
        if (nm.endsWith(".class") && nm.indexOf('$')==-1) {
          String full = pkg + '.' + nm;
          try {
            Class<?> c = Class.forName(full.substring(0, full.lastIndexOf('.')));
            cnf.addAnnotatedClass(c);
            MSysOut.println(HIBERNATE_LOGS,nm+" annotated Hibernate class handled");
            list.add(c);
          }
          catch (Exception ex) {
            System.err.println(ex.getClass().getSimpleName()+" thrown when handling "+nm+" Hibernate class");
          }
        }
      }
    }
    catch(URISyntaxException ex) {
      MSysOut.println(ERROR_LOGS, "Program error in AbstractVHib.addAnnotatedClasses(): "+ex.getLocalizedMessage());
    }

    return list;
  }
  
  public static class c3p0Params
  {
    public String maxSize;
    public String minSize;
    public String acquireIncrement;
    public String timeout;
    public String idleTestPeriod;
    public String providerClass = "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";
  }
  
  public static String getHib_fs_local_path()
  {
    return hib_fs_local_path;
  }

}
