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

package edu.nps.moves.mmowgli.db;

import java.io.Serializable;

import javax.persistence.*;

import edu.nps.moves.mmowgli.hibernate.DB;

/**
 * Badges are awards that may span games. Eg, for being a good boy in the
 * piracy game, you may get a Gold Badge awarded to you, and that will carry
 * over to the next game, Global Thermonuclear War.
 * 
 * @author DMcG
 */
@Entity
public class Badge implements Serializable
{
   private static final long serialVersionUID = -8157473734919098126L;
   
   public static long BADGE_ONE_ID   = 1; // played innov and defend
   public static long BADGE_TWO_ID   = 2; // played each kind
   public static long BADGE_THREE_ID = 3; // played root of super-active
   public static long BADGE_FOUR_ID  = 4; // played super-interesting
   public static long BADGE_FIVE_ID  = 5; // played a favorite
   public static long BADGE_SIX_ID   = 6; // accepted authorship invite
   public static long BADGE_AP_AUTHOR = 6;
   public static long BADGE_SEVEN_ID = 7; // ranked in top 50
   public static long BADGE_EIGHT_ID = 8; // logged in each day

    /** Primary key, auto-generated */
    long badge_pk;

    /** The name of the badge */
    String badgeName;
    
    /** A short description of the badge */
    String description;

    /** An icon associated with the badge */
    Media media;

    public static Badge getTL(Object id)
    {
      return DB.getTL(Badge.class, id);
    }
    
    /**
     * Primary key, auto-generated
     * @return the badge_pk
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable = false)
    public long getBadge_pk() {
        return badge_pk;
    }

    /**
     * Primary key, auto-generated
     * @param badge_pk the badge_pk to set
     */
    public void setBadge_pk(long badge_pk) {
        this.badge_pk = badge_pk;
    }

    /**
     * The name of the badge
     * @return the badgeName
     */
    @Basic
    public String getBadgeName() {
        return badgeName;
    }

    /**
     * The name of the badge
     * @param badgeName the badgeName to set
     */
    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }

    /**
     * A short description of the badge
     * @return the description
     */
    @Basic
    public String getDescription() {
        return description;
    }

    /**
     * A short description of the badge
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public Media getMedia()
    {
      return media;
    }

    public void setMedia(Media media)
    {
      this.media = media;
    }
}
