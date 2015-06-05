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
  along with Mmowgli, in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/

package edu.nps.moves.mmowgli;

import static edu.nps.moves.mmowgli.MmowgliConstants.*;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.hibernate.criterion.Restrictions;

import com.vaadin.server.VaadinRequest;

import edu.nps.moves.mmowgli.db.Game;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;
import edu.nps.moves.mmowgli.utility.MiscellaneousMmowgliTimer.MSysOut;

/**
 * CACManager.java created on Jun 1, 2015
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */

public class CACManager
{
  private static Pattern p = Pattern.compile("([0-9])"); // to find digits
   
  public static CACData findCAC(VaadinRequest req)
  {    
    CACData cData = new CACData();
    String val = req.getHeader(CAC_CLIENT_VERIFY_HEADER);
    String client;
    String cert;

    if (val != null) {
      if (val.equals(VERIFY_SUCCESS)) {
        client = req.getHeader(CAC_CLIENT_DN_HEADER);
        if (client != null) {
          cert = req.getHeader(CAC_CERT_HEADER);
          if (cert != null) {
            cData.isCACPresent = true;  // will be reset on parse error
            parseCert(cert, cData);
          }
        }
      }
    }
    return cData;
  }
  
  // This is quick test where we need CAC for both tasks (login/reg...haven't yet figured out which) and there's no CAC
  public static boolean canProceed(CACData cData)
  {
    HSess.init();
    Game game = Game.getTL();
    HSess.close();
    return !(game.isRequireCACregistration() && game.isRequireCAClogin() && !cData.isCACPresent );    
  }
  
  public static boolean canRegisterTL(CACData cData)
  {
    Game game = Game.getTL();
    return !game.isRequireCACregistration() || cData.isCACPresent;
  }
  
  public static boolean canLoginTL(CACData cData)
  {
    Game game = Game.getTL();
    return !game.isRequireCAClogin() || cData.isCACPresent;    
  }
  
  public static boolean canQuickLoginTL(CACData cData)
  {
    Game game = Game.getTL();
    return game.isUseCAClogin() && cData.isCACPresent;       
  }

  public static String getEmail(CACData cData)
  {
    return cData.userEmail;
  }
  
  public static String getFirstName(CACData cData)
  {
    return cData.userFirst;
  }
  
  public static String getLastName(CACData cData)
  {
    return cData.userLast;
  }
  
  public static Object getCacId(CACData cData)
  {
    return cData.cacId;
  }
  
  public static boolean isCacPresent(CACData cData)
  {
    return cData.isCACPresent;
  }

  public static List<User> getUserWithCACTL(CACData cData)
  {
    @SuppressWarnings("unchecked")
    List<User> list = (List<User>) HSess.get().createCriteria(User.class).add(Restrictions.eq("cacId", cData.cacId.toString())).list();
    return list.size()>0 ? list : null;      
  }
  
  private static void parseCert(String cert, CACData data)
  {
    cert = cert.replace(' ','\r');
    cert = cert.replace("BEGIN\rCERTIFICATE","BEGIN CERTIFICATE");
    cert = cert.replace("END\rCERTIFICATE", "END CERTIFICATE");
    PEMParser pr = new PEMParser(new StringReader(cert));
    try {
      Object o = pr.readObject();
      pr.close();
      if(o instanceof X509CertificateHolder) {
        X509CertificateHolder x509 = (X509CertificateHolder)o;        
        X500Name x500name = x509.getSubject();
        RDN cnRdns[] = x500name.getRDNs(BCStyle.CN);
        
        String cn = IETFUtils.valueToString(cnRdns[0].getFirst().getValue());
        parseCN(cn,data);       
        
        GeneralNames gns = GeneralNames.fromExtensions(x509.getExtensions(), Extension.subjectAlternativeName);
        if (gns != null) {
          GeneralName[] subjectAltNames = gns.getNames();
          for (GeneralName gn : subjectAltNames) {
            if (gn.getTagNo() == GeneralName.rfc822Name) { // check for email
              String s = DERIA5String.getInstance(gn.getName()).getString();
              if (s.contains("@")) {
                data.userEmail = s;
                break;
              }
            }
          }
        }
        
       // Create the unique card identifier (issuer+serial) which when hashed goes into the database for quick login
        String uniqueCertId = x509.getIssuer().toString()+" "+x509.getSerialNumber().toString();

        MessageDigest md = MessageDigest.getInstance("SHA-256");        
        md.update(uniqueCertId.getBytes("UTF-8")); // or UTF-16
        byte[] digest = md.digest();
        data.cacId = Hex.encodeHexString(digest);

        /* Alternatively, this will do a salted hash, but the output is not the same for the same input; better security
         * but the login performance would be bad since the user list has to be polled instead of indexed
         try {
           data.cacId = PasswordHash.createHash(uniqueCertId);
         }
         catch(Exception ex) {
           MSysOut.println(MmowgliConstants.SYSTEM_LOGS,"Program error, could not create CAC hash; auto-login disabled");
           data.cacId = null;
         }
         System.out.println("data cacId: "+data.cacId); */

      }
    }
    catch(IOException | NoSuchAlgorithmException ex) {
      MSysOut.println(MmowgliConstants.SYSTEM_LOGS,ex.getClass().getSimpleName()+": Program error, could not parse CAC");
      data.cacId = null;
      data.isCACPresent = false;
    }
        
  
        // Some informational stuff
   /* this gives same info as the x509 methods below  
        RDN rdns[] = x500name.getRDNs();
        for(RDN rdn : rdns) {
           AttributeTypeAndValue[] tandV = rdn.getTypesAndValues();
           for(AttributeTypeAndValue tv : tandV) {
             System.out.println(tv.getType());
             System.out.println(IETFUtils.valueToString(tv.getType()));
             System.out.println(tv.getValue());
             System.out.println(IETFUtils.valueToString(tv.getValue()));
           }
        }
        */ 
        /*
        System.out.println("X509 version: "+x509.getVersionNumber());
        System.out.println("X509 Serial num: "+x509.getSerialNumber());
        System.out.println("X509 Sig algo: "+x509.getSignatureAlgorithm().getAlgorithm().toASN1Primitive());
        System.out.println("X509 Issuer: "+x509.getIssuer());
        System.out.println("X509 Not before: "+x509.getNotBefore());
        System.out.println("X509 Not after: "+x509.getNotAfter());
        System.out.println("X509 Subject: "+x509.getSubject());
        System.out.println("X509 Subject Public Key Info: "+x509.getSubjectPublicKeyInfo().getAlgorithm().getAlgorithm());
        */
       /* 
        System.out.println("CriticalExtensionOIDs: ");
        Set<?> set = x509.getCriticalExtensionOIDs();
        Iterator<?> itr = set.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid).getParsedValue());
        }
          
        System.out.println("NonCriticalExtensionOIDs: ");
        set = x509.getNonCriticalExtensionOIDs();
        itr = set.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid).getParsedValue());
        }
        
        System.out.println("Other api: getExtensionOIDs");
        List<?> lis = x509.getExtensionOIDs();
        itr = lis.iterator();
        while(itr.hasNext()) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)itr.next();
          System.out.println(oid.toString()+" : "+x509.getExtension(oid).getParsedValue());
        }
       
        System.out.println("From the extensions \"block\"");
        Extensions exts = x509.getExtensions();
        ASN1ObjectIdentifier[] ids = exts.getExtensionOIDs();
        for(ASN1ObjectIdentifier oid : ids) {
          org.bouncycastle.asn1.x509.Extension ext = exts.getExtension(oid);
          System.out.println(oid.toString()+": "+IETFUtils.valueToString(ext.getParsedValue()));
        }
   //     */
  }

  /*
   * Name is in the form last.first.mi.number
   */
  private static void parseCN(String s, CACData data)
  {
    String[] sa = s.split("\\.");
    int cnLen = sa.length;
    if(cnLen > 0) {
      data.userLast = sa[0];
      data.userFirst = concatNonDigits(1,sa);
      //System.out.println("parseCN, CACData: "+data.toString());
    }
  }
  
  /*
   * Put the first + mi together with space instead of period
   */
  private static String concatNonDigits(int st, String[] sa)
  {
    StringBuffer sb = new StringBuffer();
    for(int i = st;i<sa.length;i++) {
      String s = sa[i];
      Matcher m = p.matcher(s);

      if(m.find()) { // found a digit
        break;
      }
      sb.append(s);
      sb.append(' ');
      
    }
    return sb.toString().trim();
  }
  
  /*
   * Return the first all number field
   */
  /*
  private static String findAllDigitsField(String[] sa)
  {
    for(int i=0;i<sa.length;i++) {
      try {
        Integer.parseInt(sa[i]);
        return sa[i]; // found it
      }
      catch(Exception ex) {
        // not it
      }
    }
    return null;
  }
  */
  public static class CACData
  {
    private boolean isCACPresent = false;

    
    private String  userFirst = null;
    private String  userLast = null;
    private String  userEmail = null;
    private Object  cacId = null;
    
    @Override
    public String toString()
    {
      return "First: "+ userFirst + " Last: "+userLast + " Email: "+userEmail +" cacID: "+cacId.toString();
    }
  }
}
