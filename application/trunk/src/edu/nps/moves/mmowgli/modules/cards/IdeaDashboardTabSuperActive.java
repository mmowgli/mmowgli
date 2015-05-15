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

package edu.nps.moves.mmowgli.modules.cards;

import java.util.*;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import edu.nps.moves.mmowgli.AppMaster;
import edu.nps.moves.mmowgli.Mmowgli2UI;
import edu.nps.moves.mmowgli.MmowgliSessionGlobals;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.markers.HibernateSessionThreadLocalConstructor;

/**
 * IdeaDashboardTabSuperActive.java
 * Created on Feb 8, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class IdeaDashboardTabSuperActive extends IdeaDashboardTabPanel
{
  private static final long serialVersionUID = -6081016302351560195L;
  private CardChainTree tree;
  private boolean initted = false;
  
  @HibernateSessionThreadLocalConstructor
  public IdeaDashboardTabSuperActive()
  {
    super();
  }
  
  @Override
  public void initGui()
  {
    Label leftLabel = new Label(
        "Super-active chains are sets of cards that have two or more authors "+
        "and four or more follow-on cards at two levels.");
    this.getLeftLayout().addComponent(leftLabel, "top:0px;left:0px"); 

    Panel pan = new Panel();
    getRightLayout().addComponent(pan, "top:0px;left:0px");
    pan.setSizeUndefined();
    pan.setStyleName(Reindeer.PANEL_LIGHT);
    
    VerticalLayout tableLay = new VerticalLayout();
    pan.setContent(tableLay);
    tableLay.setMargin(false); // default comes back from panel w/ margins
    tableLay.setSizeUndefined();

    tree = new CardChainTree(null, true); // no cards at first
    tree.setWidth("680px");
    tree.setHeight("730px");
    tree.addStyleName("m-greyborder");
    tableLay.addComponent(tree);
  }
  
  @Override
  public void setVisible(boolean yn)
  {
    super.setVisible(yn);
    if(yn)
      if(!initted) {
        loadTreeTL();
        initted = true;
      }
  }

  private void loadTreeTL()
  {
    MmowgliSessionGlobals globs = Mmowgli2UI.getGlobals();
    List<Card> list = AppMaster.instance().getMcache().getSuperActiveChainRoots();
    User me = globs.getUserTL();
    ArrayList<Card> arLis = new ArrayList<Card>();
    
    for(Card c : list) {
      if(Card.canSeeCardTL(c, me))
        arLis.add(c);
    }
    tree.addChains(arLis);
  }
  
  class TallyPkt {HashSet<Long> authors=new HashSet<Long>(); int numFourCardLevs=0;}
  
  @SuppressWarnings("unused")
  private List<Card> findSuperActiveChains2(List<Card> ideaCardList)
  {
    ArrayList<Card> aLis = new ArrayList<Card>();

    for(Card child : ideaCardList) {
     TallyPkt pkt = new TallyPkt();
     checkOneRoot(child,pkt);
     if(isSupAct(pkt))
       aLis.add(child);
    }
    return aLis;
  }
  
  private boolean isSupAct(TallyPkt pkt)
  {
    if(pkt.numFourCardLevs >= 2)
      if(pkt.authors.size() >= 2)
        return true;
    return false;
  }
  
  private void checkOneRoot(Card c, TallyPkt pkt)
  {
    for(Card child : c.getFollowOns())
      pkt.authors.add(child.getAuthor().getId());
    
    if(c.getFollowOns().size() >= 4)
      pkt.numFourCardLevs++;
    
    // We don't need to check further if we pass
    if(isSupAct(pkt))
      return;
    for(Card child : c.getFollowOns())
      checkOneRoot(child,pkt);
  }
  
  Vector<ArrayDeque<Card>> chains;
  
  @SuppressWarnings("unused")
  private List<ArrayDeque<Card>> findSuperActiveChains(List<Card> ideaCards)
  {
    chains = new Vector<ArrayDeque<Card>>();
    
    for(Card c : ideaCards) {
      ArrayDeque<Card> ad = new ArrayDeque<Card>();
      ad.add(c);
      decompose(ad);
    }
    // now all the recursion should be done; have a look at chains   
    filterChains();
    // now the filtering is done
    return chains;
  }
  
  private void filterChains()
  {
    Vector<ArrayDeque<Card>> v = new Vector<ArrayDeque<Card>>();
    
    HashSet<Long> authors = new HashSet<Long>();
    Iterator<ArrayDeque<Card>> itr = chains.iterator();
    
    while(itr.hasNext()) {
      ArrayDeque<Card> ad = itr.next();
      if(ad.size()<4)
        continue;
      authors.clear();
      Iterator<Card> cItr = ad.iterator();
      while(cItr.hasNext()) {
        authors.add(cItr.next().getAuthor().getId());  // will not duplicate
      }
      if(authors.size()>=2)
        v.add(ad); // save this chain, it's an active one
    }
    chains = v;
  }
  
  private void saveChain(ArrayDeque<Card> lis)
  {
    chains.add(lis);
  }
    
  private void decompose(ArrayDeque<Card> clis)
  {
    Card c = clis.getLast();
    if(c.getFollowOns() == null || c.getFollowOns().size()<=0) {
      saveChain(clis);
      return;
    }    

    for(Card ch : c.getFollowOns()) {
      if(!isGameMaster && ch.isHidden())
        continue;
      ArrayDeque<Card> working = (ArrayDeque<Card>)clis.clone();
      working.addLast(ch);
      decompose(working);
    }
  }
  
  public List<Card> getCardList()
  {
    return null;   
  }
  
  @Override
  public boolean confirmCard(Card c)
  {
    return true;
  }

}
