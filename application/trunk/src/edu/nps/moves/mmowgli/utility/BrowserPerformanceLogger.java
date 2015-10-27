package edu.nps.moves.mmowgli.utility;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.sql.*;
import java.util.concurrent.LinkedBlockingQueue;

import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;

import edu.nps.moves.mmowgli.AppMaster;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

public class BrowserPerformanceLogger
{
  private static LinkedBlockingQueue<JsonArray> statsQ = new LinkedBlockingQueue<>();
  private static QReader statsQReader=null;
  
  public static String BROWSERPERFORMANCELOGGER_CALLBACK_NAME = "edu_nps_moves_mmowgli_utility_browserperformancelogger_loadstatscallback";
  
  private static String connectionString = null; // "jdbc:mysql://localhost/piracydup?user=mmowgli&password=gwtservelet";
  private static Connection connection = null;
  
  private static String gameHandle = null;
  private static String sqlStatement = "INSERT into ResourcePerformance "
      + "(game,username,clientip, clientlocation,clientbrowser,postdatetime,resourceurl,resourceduration) " + "values(?,?,?,?,?,?,?,?)";
  private static int C_GAME = 1;
  private static int C_UNAME = 2;
  private static int C_IP = 3;
  private static int C_LOC = 4;
  private static int C_BROW = 5;
  private static int C_DATE = 6;
  private static int C_URL = 7;
  private static int C_DUR = 8;

  private static AppMaster appmaster;
  static {
    appmaster = AppMaster.instance();
    String dbUrl = appmaster.getInitParameter(WEB_XML_DEBUG_DB_URL_KEY); // e.g jdbc:mysql://web3:3306/
    String dbName = appmaster.getInitParameter(WEB_XML_DEBUG_DB_NAME_KEY); // e.g. piracytest
    String dbUser = appmaster.getInitParameter(WEB_XML_DEBUG_DB_USER_KEY);
    String dbPassword = appmaster.getInitParameter(WEB_XML_DEBUG_DB_PASSWORD_KEY);

    if (dbUrl == null || dbName == null || dbUser == null || dbPassword == null)
      System.out.println("No debug database parameters found.  Resource loading logging disabled");
    else {
      if (!dbUrl.endsWith("/"))
        dbUrl = dbUrl + "/";
      connectionString = dbUrl + dbName + "?user=" + dbUser + "&password=" + dbPassword;
      
      statsQReader = new QReader();
      statsQReader.start();
    }
  }

  // Strictly a global static class
  private BrowserPerformanceLogger()
  {
  }

  public static void registerCurrentPage()
  {
    JavaScript.getCurrent().addFunction(BROWSERPERFORMANCELOGGER_CALLBACK_NAME, loadStatsCallback);
  }

  public static void unregisterCurrentPage()
  {
    JavaScript.getCurrent().removeFunction(BROWSERPERFORMANCELOGGER_CALLBACK_NAME);
  }

  @SuppressWarnings("serial")
  public static JavaScriptFunction loadStatsCallback = new JavaScriptFunction()
  {
    @Override
    public void call(JsonArray param)
    {
      System.out.println("loadStatsCallback() entered from browser");
      statsQ.add(param);
    }
  };
  
  private static class QReader extends Thread
  {
    boolean killed = false;
    
    QReader()
    {
      setName("ResourcePerformanceLogger");
      setPriority(Thread.NORM_PRIORITY-1);
      setDaemon(true);
    }

    public void run()
    {
      while (!killed && connectionString!=null) {
        JsonArray param;
        try {
          param = statsQ.take();
          // System.out.println(param.toJson());
          if (param.length() == 1) {
            JsonObject jobj = param.get(0);
            String clientip = clamp(32, jobj.getString("clientip"));
            String location = clamp(128, jobj.getString("location"));
            String browser = clamp(255, jobj.getString("browser"));
            String postdatetime = clamp(32, jobj.getString("postdatetime"));
            JsonArray arr = jobj.getArray("resources");
            int count = arr.length();
            if (gameHandle == null)
              gameHandle = trim(AppMaster.getUrlString());
            for (int i = 0; i < count; i++) {
              JsonObject line = arr.getObject(i);
              String url = line.getString("url");
              int duration = (int) Math.round(line.getNumber("duration"));

              logIt(gameHandle, "unknown", clientip, location, browser, postdatetime, duration, clamp(255, url), i == count - 1);
            }
          }
        }
        catch (InterruptedException e) {
        }
      }
    }
    
    public void kill()
    {
      killed = true;     
    }
  };
/*
  @SuppressWarnings("serial")
  public static JavaScriptFunction xloadStatsCallback = new JavaScriptFunction()
  {
    @Override
    public void call(JsonArray param)
    {
      System.out.println("loadStatsCallback() entered from browser");
      System.out.println(param.toJson());
      if (param.length() == 1) {
        JsonObject jobj = param.get(0);
        String clientip = clamp(32, jobj.getString("clientip"));
        String location = clamp(128, jobj.getString("location"));
        String browser = clamp(255, jobj.getString("browser"));
        String postdatetime = clamp(32, jobj.getString("postdatetime"));
        JsonArray arr = jobj.getArray("resources");
        int count = arr.length();
        if(gameHandle == null)
          gameHandle = trim(AppMaster.getUrlString());
        for (int i = 0; i < count; i++) {
          JsonObject line = arr.getObject(i);
          String url = line.getString("url");
          int duration = (int) Math.round(line.getNumber("duration"));

          logIt(gameHandle,"unknown",clientip, location, browser, postdatetime, duration, clamp(255, url),i==count-1);
        }
      }
    }
  };
*/
  private static String trim(String s)
  {
    if (s == null)
      return "";
    int lastSlash = s.lastIndexOf('/');
    if(lastSlash <= 0)
      return clamp(64,s);
    if(lastSlash == (s.length()-1))
      return clamp(64,s);
    
    return clamp(64,s.substring(lastSlash+1));    
  }
  
  private static String clamp(int len, String s)
  {
    if (s == null)
      return "";
    if (s.length() > len)
      return s.substring(0, len);
    return s;
  }

  private static void logIt(String game, String username, String clientip, String location, String browser, String postdatetime, int duration, String url, boolean flush)
  {
    if(connectionString == null)
      return;
    
    PreparedStatement statement = null;
    try {
      if(connection == null) {
        Class.forName("com.mysql.jdbc.Driver"); // this will load it
        DriverManager.setLoginTimeout(10);
        connection = DriverManager.getConnection(connectionString);
        connection.setAutoCommit(false);
      }
      statement = connection.prepareStatement(sqlStatement);
      statement.setString(C_GAME, game);
      statement.setString(C_UNAME, username);
      statement.setString(C_IP, clientip);
      statement.setString(C_LOC, location);
      statement.setString(C_BROW, browser);
      statement.setString(C_DATE, postdatetime);
      statement.setString(C_URL, url);
      statement.setInt(C_DUR, duration);

      statement.executeUpdate();
      if(flush)
        connection.commit();
    }
    catch (ClassNotFoundException | SQLException ex) {
      System.out.println("JDBC error in BrowserPerformanceLogger when excuting query: "+ex.getClass()+": "+ex.getLocalizedMessage());
      connectionString=null; // for next time
      if(statsQReader != null) {
        statsQReader.kill();
        statsQReader.interrupt();
      }
    }
    finally {
      try {
       // if (connection != null)
       //   connection.close();
        if (statement != null)
          statement.close();
      }
      catch (SQLException ex) {
        System.out.println("JDBC error in BrowserPerformanceLogger when closing statement: "+ex.getClass()+": "+ex.getLocalizedMessage());
      }
    }
  }
}
