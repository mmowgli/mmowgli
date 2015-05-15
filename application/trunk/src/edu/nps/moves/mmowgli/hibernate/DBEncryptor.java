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

import java.io.InputStream;
import java.util.Properties;

import edu.nps.moves.mmowgli.MmowgliConstants;

/**
 * DBEncryptor.java
 * Created on Jul 20, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class DBEncryptor
{
  private static String pwFileName = "databaseEncryptionPassword.properties";
  private static String pwFileParent = "edu/nps/moves/mmowgli/";
  
  public static String noFileError       = "Fatal error: No database encryption password file found ("            +pwFileParent+pwFileName+")";
  public static String noPwError         = "Fatal error: No database encryption password specified in "           +pwFileParent+pwFileName;
  public static String usingDefaultError = "Fatal error: Default database encryption password must be changed in "+pwFileParent+pwFileName;
  
  private static String actualError = null;  
  private static String password = null;
  
  static {
    try {
      InputStream istr = DBEncryptor.class.getResourceAsStream("databaseEncryptionPassword.properties");
      Properties prop = new Properties();
      prop.load(istr);
      password = prop.getProperty("databaseEncryptionPassword");
    }
    catch(Throwable t) {
      actualError = noFileError;
    }
    
    if(password==null) {
      if(actualError==null)
        actualError = noPwError;
    }
    else if(password.equals(MmowgliConstants.DUMMY_DATABASE_ENCRYPTION_PASSWORD))
      actualError = usingDefaultError;
    
    if(actualError != null)
      throw new RuntimeException(actualError);
  }
  
  public static String getSimplePBEPassword()
  {
    return password;
  }
}
