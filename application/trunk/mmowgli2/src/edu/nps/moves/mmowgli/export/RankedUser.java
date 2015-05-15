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

package edu.nps.moves.mmowgli.export;

import java.util.Arrays;
import java.util.Comparator;

import edu.nps.moves.mmowgli.db.User;

/**
 * RankedUser.java Created on Dec 12, 2013
 * 
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 * 
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class RankedUser
{
  public User u;
  
  public int combinedScoreRank = 0;
  public int basicScoreRank = 0;
  public int innovScoreRank = 0;
  public int[] combinedRankByMove;
  public int[] basicRankByMove;
  public int[] innovRankByMove;
  
  public RankedUser(User u, int numMoves)
  {
    this.u= u;
    combinedRankByMove = new int[numMoves];
    basicRankByMove = new int[numMoves];
    innovRankByMove = new int[numMoves];
    Arrays.fill(combinedRankByMove, 0);
    Arrays.fill(basicRankByMove, 0);
    Arrays.fill(innovRankByMove, 0);
  }

  public static Comparator<RankedUser> combinedComparer = new Comparator<RankedUser>()
  {
    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedBasicScore() + u0.u.getCombinedInnovScore();
      float f1 = u1.u.getCombinedBasicScore() + u1.u.getCombinedInnovScore();
      return Float.compare(f1,f0);
    }
  };
  
  public static Comparator<RankedUser> basicComparer = new Comparator<RankedUser>()
  {

    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedBasicScore();
      float f1 = u1.u.getCombinedBasicScore();
      return Float.compare(f1,f0);
    }
  };
  
  public static Comparator<RankedUser> innovComparer = new Comparator<RankedUser>()
  {

    @Override
    public int compare(RankedUser u0, RankedUser u1)
    {
      float f0 = u0.u.getCombinedInnovScore();
      float f1 = u1.u.getCombinedInnovScore();
      return Float.compare(f1,f0);
    }
  };

  public static Comparator<RankedUser> getCombinedMoveComparator(int movenum)
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getBasicScoreMoveX(movenum)+u0.u.getInnovationScoreMoveX(movenum);
        float f1 = u1.u.getBasicScoreMoveX(movenum)+u1.u.getInnovationScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };
  }
 
  public static Comparator<RankedUser> getBasicMoveComparator(int movenum) 
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getBasicScoreMoveX(movenum);
        float f1 = u1.u.getBasicScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };   
  }
  
  public static Comparator<RankedUser> getInnovMoveComparator(int movenum) 
  {
    return new MoveComparator(movenum) 
    {
      @Override
      public int compare(RankedUser u0, RankedUser u1)
      {
        float f0 = u0.u.getInnovationScoreMoveX(movenum);
        float f1 = u1.u.getInnovationScoreMoveX(movenum);
        return Float.compare(f1,f0);
      }      
    };  
  }
 
  static abstract class MoveComparator implements Comparator<RankedUser>
  {
    int movenum;
    public MoveComparator(int movenum)
    {
      this.movenum = movenum;
    }
  }
}
