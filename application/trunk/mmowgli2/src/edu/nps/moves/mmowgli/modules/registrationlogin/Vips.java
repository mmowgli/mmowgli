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

package edu.nps.moves.mmowgli.modules.registrationlogin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.nps.moves.mmowgli.db.pii.VipPii;
import edu.nps.moves.mmowgli.hibernate.VHibPii;

/**
 * Vips.java
 * Created on Nov 9, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Vips
{  
  public static boolean isVip(String s)
  {
    Session sess = VHibPii.getASession();
    boolean ret = isVip(s,sess);
    sess.close();
    return ret;
  }
  
  public static boolean isBlackListed(String s)
  {
    Session sess = VHibPii.getASession();
    boolean ret = isBlackListed(s,sess);
    sess.close();
    return ret;
  }
  public static boolean isBlackListed(String s, Session sess)
  {
    Criteria crit = sess.createCriteria(VipPii.class);
    crit.add(Restrictions.eq("type", VipPii.VipType.EMAIL_BLACKLIST));
    @SuppressWarnings("unchecked")
    List<VipPii> lis = crit.list();
    
    for(VipPii v : lis) {
      if(v.getEntry().equalsIgnoreCase(s))
        return true;
    }
    return false;        
  }
  
  public static boolean isVip(String s, Session sess)
  {
    return getVip(s,sess) != null;
  }
  
  private static VipPii getVip(String s, Session sess)
  {
    Criteria crit = sess.createCriteria(VipPii.class);
    crit.add(Restrictions.eq("type", VipPii.VipType.EMAIL));
    @SuppressWarnings("unchecked")
    List<VipPii> lis = crit.list();
    
    for(VipPii v : lis) {
      if(v.getEntry().equalsIgnoreCase(s))
        return v;
    }
    return null;    
  }
  
  public static boolean isVipDomain(String s)
  {
    Session sess = VHibPii.getASession();
    boolean ret = isVipDomain(s, sess);
    sess.close();
    return ret;
  }
  
  public static boolean isVipDomain(String s, Session sess)
  {
    s = s.toLowerCase();
    
    Criteria crit = sess.createCriteria(VipPii.class);
    crit.add(Restrictions.eq("type", VipPii.VipType.DOMAIN));
    @SuppressWarnings("unchecked")
    List<VipPii> lis = crit.list();
    
    for(VipPii v : lis) {
      if(s.endsWith(filterDomainString(v.getEntry())))
        return true;
    }
    return false;
  }
  
  private static String filterDomainString(String s)
  {
    String ret = s;
    while ( ret.length()>0 &&
           ( ret.startsWith(".") || ret.startsWith("*")) ) {
      ret = ret.substring(1);
    }
    return ret.toLowerCase();
  }
  
  public static boolean isVipOrVipDomainAndNotBlackListed(String s)
  {
    return isVipOrVipDomainAndNotBlackListed(s,VHibPii.getASession());
  }
  
  public static boolean isVipOrVipDomainAndNotBlackListed(String s, Session sess)
  {
    return isVipOrVipDomain(s,sess) && !isBlackListed(s,sess);
  }
  
  public static boolean isVipOrVipDomain(String s)
  {
    return isVipOrVipDomain(s,VHibPii.getASession());
  }
  
  public static boolean isVipOrVipDomain(String s, Session sess)
  {
    return (isVip(s,sess) || isVipDomain(s,sess));
  }

  /**
   * If the email would be permitted by an existing domain, we're good
   * If the email would be permitted by and existing entry, we're good
   * Else add email entry
   * does not commit transaction
   */
  public static void addEmail(String email, Session sess)
  {
    if(isVipOrVipDomain(email,sess))
        return;
    VipPii vp = new VipPii(email,VipPii.VipType.EMAIL);
    sess.save(vp);    
  }

  /**
   * If the email would not be permitted by domain or email, we're good.
   * If the email would be permitted by a current entry, remove it.
   * If the email would by permitted by domain, add a blacklist
   * does not commit transaction
   */
  public static void blackListEmail(String email, Session sess)
  {
    boolean dom   = isVipDomain(email,sess);
    VipPii vipPii = getVip(email,sess);
    boolean entry = vipPii != null;
    
    if(!dom && !entry)
      return;
    
    if(entry)
      sess.delete(vipPii);
    
    if(dom)
      sess.save(new VipPii(email,VipPii.VipType.EMAIL_BLACKLIST));   
  }
}

