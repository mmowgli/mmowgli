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

import org.hibernate.Session;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;
import static edu.nps.moves.mmowgli.MmowgliConstants.*;

/**
 * Sess.java
 * Created on Jun 18, 2012
 *
 * A class to help debug Hibernate errors
 * 
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Sess
{
  public static boolean PRINT_UPDATE_CALLER = true;
  public static boolean PRINT_SAVE_CALLER = true;
  
  private static String UPSTRING = "Hib: session update called with ";
  private static String SVSTRING = "Hib: session save called with ";
  
  private enum sType {SAVE,UPDATE,MERGE};
  
  
  public static void sessUpdateTL(Object o)
  {
    Session sess = HSess.get();
    printIt(PRINT_UPDATE_CALLER,UPSTRING,sess,o,sType.UPDATE);
    sess.update(o);
  }
    
  public static void sessSaveTL(Object o)
  {
    Session sess = HSess.get();
    printIt(PRINT_SAVE_CALLER,SVSTRING,sess,o,sType.SAVE);
    sess.save(o);
  }
  
  private static void printIt(boolean yn, String title, Session sess, Object o, sType typ)
  {
    if(yn) {
      StackTraceElement callingFrame = Thread.currentThread().getStackTrace()[4];
      StackTraceElement callingFrame1= Thread.currentThread().getStackTrace()[3];
      String objName = o.getClass().getSimpleName();
      String clsName = callingFrame. getClassName().substring(callingFrame. getClassName().lastIndexOf('.')+1);
      String clsName1= callingFrame1.getClassName().substring(callingFrame1.getClassName().lastIndexOf('.')+1);
      String mthName = callingFrame. getMethodName();
      String mthName1= callingFrame1.getMethodName();
      int lnNum = callingFrame. getLineNumber();
      int lnNum1= callingFrame1.getLineNumber();
      MSysOut.println(HIBERNATE_LOGS, title+objName+" from "+clsName+ "."+mthName+ "/"+lnNum+
                                            ","+clsName1+"."+mthName1+"/"+lnNum1+"("+AppMaster.instance().getServerName()+")");
    }
  }
}
