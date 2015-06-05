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

package edu.nps.moves.security;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * EmailConfirmationServlet.java Created on Sep 6, 2012
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
@WebServlet(value = "/confirm/*", asyncSupported = true)
public class EmailConfirmationServlet extends HttpServlet
{
  private static final long serialVersionUID = 6700377842138123676L;

  private String connection = null; // "jdbc:mysql://localhost/piracydup?user=mmowgli&password=gwtservelet";

  private String queryBase = "SELECT * from EmailConfirmation WHERE confirmationCode = ";
  private String URI_PARAM = "uid";
  private String USER_ID_COL = "user_id";
  private String uqueryBase = "select * from User where id= ? ; ";
  private String setQueryBase = "update User SET emailConfirmed = 1 where id = ?;";
  private String gameTitleQuery = "select * from Game WHERE id = 1 ;";
  private String troubleListEmailQuery = "select * from GameLinks WHERE id = 1 ;"; 
  private String GAMETITLE_COL = "title";
  private String ACRONYM_COL = "acronym";
  private String TROUBLEMAILTO_COL = "troubleMailto";

  enum MyResponse
  {
    GOOD, ERROR, BADURL;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    String error = null;

    ServletContext ctx = getServletContext();
    String dbUrl = ctx.getInitParameter(WEB_XML_DB_URL_KEY); // e.g jdbc:mysql://web3:3306/
    String dbName = ctx.getInitParameter(WEB_XML_DB_NAME_KEY); // e.g. piracytest
    String dbUser = ctx.getInitParameter(WEB_XML_DB_USER_KEY);
    String dbPassword = ctx.getInitParameter(WEB_XML_DB_PASSWORD_KEY);
    if (!dbUrl.endsWith("/"))
      dbUrl = dbUrl + "/";

    String gamename = "";
    String acronym = "";
    String troubleEmail = "";
    
    connection = dbUrl + dbName + "?user=" + dbUser + "&password=" + dbPassword;

    MyResponse myresp = null;
    Integer userId = null;
    Object uid = req.getParameter(URI_PARAM);

    if (uid == null) {
      System.out.println(error = "URL does not include uid parameter");
      myresp = MyResponse.BADURL;
    }
    else {
      // This uses direct jdbc instead of hibernate so it could potentially be
      // easier to make run on its own

      String query = queryBase + "'" + uid + "';"; // can't get statement.setString(1,uuid); to work
      Connection connect = null;
      ResultSet resultSet = null;
      PreparedStatement statement = null;
      try {
        Class.forName("com.mysql.jdbc.Driver"); // this will load it
        connect = DriverManager.getConnection(connection);
        connect.setAutoCommit(true);
        statement = connect.prepareStatement(query);

        resultSet = statement.executeQuery();

        while (resultSet.next()) {
          userId = resultSet.getInt(USER_ID_COL);
          break;
        }
        if (userId == null)
          req.getSession().getServletContext().log(error = "EmailConfirmation entry not found with uid = " + uid);
        else {
          statement.close();
          statement = connect.prepareStatement(uqueryBase);
          statement.setInt(1, userId);
          resultSet.close();
          resultSet = statement.executeQuery();
          while (resultSet.next()) {
            Integer id = resultSet.getInt("id");
            statement.close();
            statement = connect.prepareStatement(setQueryBase);
            statement.setInt(1, id);
            int ret = statement.executeUpdate();
            if (ret == 0) {
              req.getSession().getServletContext().log(error = "Could not update User table with emailConfirmed = true");
              break;
            }
            // Trouble list email
            statement.close();
            statement = connect.prepareStatement(troubleListEmailQuery);
            resultSet.close();
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
              troubleEmail = resultSet.getString(TROUBLEMAILTO_COL);
              break;
            }
            // Now return the game name
            statement.close();
            statement = connect.prepareStatement(gameTitleQuery);
            resultSet.close();
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
              gamename = resultSet.getString(GAMETITLE_COL);
              acronym = resultSet.getString(ACRONYM_COL);
              break;
            }
            break;
          }
        }
      }
      catch (ClassNotFoundException ex) {
        req.getSession().getServletContext().log(error = ex.getLocalizedMessage(), ex);
      }
      catch (SQLException ex) {
        req.getSession().getServletContext().log(error = ex.getLocalizedMessage(), ex);
      }
      finally {
        try {
          if (connect != null)
            connect.close();
          if (statement != null)
            statement.close();
          if (resultSet != null)
            resultSet.close();
        }
        catch (SQLException ex) {
          req.getSession().getServletContext().log(error = ex.getLocalizedMessage(), ex);
        }
      }
    }

    // if error == null, we're good
    if (error == null) {
      myresp = MyResponse.GOOD;
      MSysOut.println(NEWUSER_CREATION_LOGS,"Email confirmationServlet positive result, userID: "+userId);
    }
    else {
      if (myresp == null) {
        myresp = MyResponse.ERROR;
        MSysOut.println(NEWUSER_CREATION_LOGS,"Email confirmationServlet NEGATIVE result, userID: "+userId);
      }
    }

    doResponse(req, resp, myresp, error, gamename, acronym, PORTALWIKI_URL, troubleEmail);
  }

  private void doResponse(HttpServletRequest req, HttpServletResponse resp, MyResponse myresp,
                          String error, String gamename, String acronym, String portalUrl, String troubleList)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 ");
    sb.append("Transitional//EN\">\n");
    sb.append("<HTML>\n");
    sb.append("<HEAD><TITLE>mmowgli Email Confirmation</TITLE></HEAD>\n");
    sb.append("<BODY>\n");

    switch (myresp) {
    case GOOD:
      sb.append("<center><font face='Helvetica'>");
      sb.append("<img src='http://test.mmowgli.nps.edu/mmowgli.png'/><br/>");
      sb.append("Your email address is confirmed. Thanks for joining <i>");
      sb.append(acronym);
      sb.append(" mmowgli"); // / ");
      //sb.append(gamename);
      sb.append("</i>&nbsp;!<br/><br/>");
      sb.append("If you have the <i>mmowgli</i> \"Email Confirmation\" dialog still visible in your browser,<br/>");
      sb.append("you may click the \"Check confirmation status\" button to begin game play.<br/><br/>");
      sb.append("Alternatively, visit ");
      sb.append("<a href='");
      sb.append(getBaseUrl(req));
      sb.append("'>");
      sb.append(getBaseUrl(req));
      sb.append("</a><br/>to login with the player name and password you chose.<br/><br/>");
      sb.append("Problems may always be reported on the<br/>");
      sb.append("<a href='http://mmowgli.nps.edu/trouble'>MMOWGLI Trouble Report</a> ");
      sb.append("page at <a href='http://mmowgli.nps.edu/trouble'>mmowgli.nps.edu/trouble</a>,<br/>");
      //sb.append("or by email to <a href='mailto:mmowgli-trouble@nps.edu'>mmowgli-trouble@nps.edu</a><br/>");
      sb.append("or by email to <a href='mailto:");
      sb.append(troubleList);
      sb.append("'>");
      sb.append(troubleList);
      sb.append("</a><br/>");

      sb.append("<br/>More information is also available on the <a href='");
      sb.append(portalUrl);
      sb.append("'>MMOWGLI Portal</a>.<br/>");
      sb.append("<br/>Thanks for your interest in playing <i>");
      sb.append(acronym);
      sb.append(" mmowgli</i>.  Play the game, change the game!");
      sb.append("</font>");

      break;
    case ERROR:
      sb.append("<center>There has been an error confirming your email address.<br/>");
      if (error != null)
        sb.append(error).append("<br/>");
      sb.append("Please visit <a href='http://portal.mmowgli.nps.edu/trouble'>");
      sb.append("http://portal.mmowgli.nps.edu/trouble</a> to report registration problems.</center>");
      break;
    case BADURL:
      sb.append("<center>Please visit <a href='");
      sb.append(getBaseUrl(req));
      sb.append("'>");
      sb.append(getBaseUrl(req));
      sb.append("</a>  to play mmowgli.");
      break;
    }

    sb.append("</center></BODY></HTML>");

    resp.setContentType("text/html");
    try {
      PrintWriter outp = resp.getWriter();
      outp.print(sb.toString());
    }
    catch (IOException ex) {
      System.err.println("Error in EmailConfirmation servlet: " + ex.getLocalizedMessage());
    }
  }

  private String getBaseUrl(HttpServletRequest req)
  {
    String url = req.getRequestURL().toString();
    String spth = req.getServletPath();
    return url.substring(0, url.length() - spth.length());
  }
}
