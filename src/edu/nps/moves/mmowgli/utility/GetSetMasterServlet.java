package edu.nps.moves.mmowgli.utility;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a simple way to implement a distributed, first-in token getter. We use it to determine will be the master node in a cluster of identical
 * applications. Usage:
 * 
 *    In a deploy script, the token must be reset before applications are launched:
 *       curl http://mmowgli.nps.edu/gamename/_getSetMasterNode (no parameter)
 *       
 *    Within the startup code for each deployed application:
 *       HttpResponse resp = httpclient.execute(new HttpGet("http://mmowgli.nps.edu/gamename/_getSetMasterNode?nodeName="+myName));
 *       if(getReturnedContentFromResponse(resp).equals(myName)) {
 *         // I'm the master, do my duty
 *       }
 */
// comment out until implemented @WebServlet("/_getSetMasterNode")
public class GetSetMasterServlet extends HttpServlet
{
  private static final long serialVersionUID = -8114889576081053294L;

  private static LinkedBlockingQueue<String> q = new LinkedBlockingQueue<String>(1);
  public static String nameParameter = "nodeName";
  public static String deployment = "_getSetMasterNode";  // match attribute
  
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String myName = request.getParameter(nameParameter);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("text/plain");

    synchronized (q) {
      if (myName == null) {
        if (!q.isEmpty())
          q.remove();
        response.setContentLength(0);
      }
      else {
        if (q.isEmpty()) {
          try { q.put(myName);  } catch (InterruptedException ex) { System.out.println("InterruptedException putting " + myName); }
        }
        String peek = q.peek();
        assert peek != null;
        response.setContentLength(peek.length());
        response.getWriter().append(peek);
      }
    }
  }
}
