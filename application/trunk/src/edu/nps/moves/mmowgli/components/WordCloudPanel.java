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

package edu.nps.moves.mmowgli.components;

import java.util.*;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

import edu.nps.moves.mmowgli.Mmowgli2UI;

/**
 * WordCloudPanel.java
 * Created on Aug 11, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class WordCloudPanel extends Panel
{
  private static final long serialVersionUID = -4148691192678918606L;
  
  private AbstractLayout meat;
  private ClickListener listener;
  private Scaler scaler;
  private final int IE7MAXCOLCOUNT = 7;
  private final int NUMLEVELS = 5;
  public enum WordOrder {FREQUENCY,ALPHA, RANDOM, NONE};
  
  //private int[] mapper = new int[]{12,16,20,24,28};
  private String[] styles = new String[] {     
      "m-cloud-size28",
      "m-cloud-size24",
      "m-cloud-size20",
      "m-cloud-size16",
      "m-cloud-size12"
  };
  //private boolean randomize = false;
  private WordOrder ordering = WordOrder.NONE;
  private boolean isIE7 = false;
  
  public WordCloudPanel(ClickListener lis)
  {
    super("Word root cloud");
    listener = lis;
    scaler = new Scaler(NUMLEVELS);
    //scaler.setLevelMapper(mapper);
    scaler.setStyleMapper(styles);
    isIE7 = Mmowgli2UI.getGlobals().isIE7();
    if(!isIE7)
      setContent(meat = new CssLayout());
    else
      setContent(meat = new VerticalLayout());
  }
  
  public void setWordData(List<Word> set, WordOrder order) //boolean randomizeList)
  {
    //this.randomize = randomizeList;
    this.ordering = order;
    setWordData(set);
  }
  
  // This data come in in order, highest freq first
  public void setWordData(List<Word> set)
  {
    meat.removeAllComponents();
    scaler.reset();

    // deep copy
    List<Word> lis = new ArrayList<Word>();

    for(Word w : set) { 
      if(Character.isDigit(w.text.charAt(0)))
        continue;
      if(w.text.length()<=1)
        continue;
      if(w.text.equals("on"))  // screwy on
        continue;
      else if(w.text.equals("ha"))
        continue;
      else if(w.text.equals("be"))
        continue;
   //   if(w.text.length()<=4)
   //     continue;
      //System.out.println("WordCloudPanel.setWordData: "+w.text+" "+w.freq);
      lis.add(new Word(w.text,w.freq)); // clone 
      scaler.tabulate(w.freq);
    }
    set = lis;
     
    if(ordering == WordOrder.RANDOM) {
      Collections.shuffle(set);
    }
    else if(ordering == WordOrder.ALPHA) {
      Collections.sort(set,new Comparator<Word>()
      {
        @Override
        public int compare(Word w1, Word w2)
        {
          return w1.text.compareToIgnoreCase(w2.text);
        }        
      });
    }

    for(Word w : set) {
      WordButton wb;
      //System.out.println("Adding button: "+w.text+" "+w.freq);
      addAButton(wb = new WordButton(w, listener));     
      wb.addStyleName(scaler.getStyle(w.freq));
    }
  }
  
  private void addAButton(WordButton butt)
  {
    if(!isIE7)
      meat.addComponent(butt);
    else {
      VerticalLayout vLay = (VerticalLayout)meat;
      int numLines = vLay.getComponentCount();
      HorizontalLayout hL;
      if(numLines == 0) {
        hL = new HorizontalLayout();
        vLay.addComponent(hL);
        vLay.setComponentAlignment(hL, Alignment.MIDDLE_CENTER);
        numLines = 1;
      }
      hL = (HorizontalLayout)vLay.getComponent(numLines-1);
      hL.addComponent(butt);
      if(hL.getComponentCount() >= IE7MAXCOLCOUNT) {
        vLay.addComponent(hL=new HorizontalLayout());
        vLay.setComponentAlignment(hL, Alignment.MIDDLE_CENTER);
      }
    }
  }
  
  public static class Word
  {
    public String text;
    public int freq;
    public Word(String txt, int freq)
    {
      this.text = txt;
      this.freq = freq;
    }
  }
  
  @SuppressWarnings("serial")
  public class WordButton extends NativeButton
  {
    public Word word;
    public WordButton(Word w, ClickListener lis)
    {
      super(w.text,lis);
      this.word = w;
      addStyleName(BaseTheme.BUTTON_LINK);
      
      // wierd bug
      /*
      if(w.text.endsWith("i")){
        char[] ca = w.text.toCharArray();
        ca[ca.length-1] = 'y';
        this.setCaption(new String(ca));
      }
      */
    }
  }
  
  private class Scaler
  {
    int min,max;
    int[] mapper;
    String[] styles;
    
    public Scaler(int numLevels)
    {
      mapper = new int[numLevels];
      Arrays.fill(mapper, -1);
      reset();
    }
    public void reset()
    {
      min = Integer.MAX_VALUE;
      max = Integer.MIN_VALUE; 
    }
    
//    public void setLevelMapper(int[] mapper)
//    {
//      this.mapper = mapper;
//    }
    
    public void setStyleMapper(String[] styles)
    {
      this.styles = styles;
    }
    public void tabulate(int i)
    {
      if( i < min)
        min = i;
      if(i > max)
        max = i;
    }
    
    public String getStyle(int i)
    {
      try {
      return styles[getGroup(i)];
      }
      catch(Throwable t) {
        System.err.println("error in WordCloudPanel");
        return "";
      }
    }
    
    int [] grpBounds;
    private int getGroup(int i)
    {
      if(grpBounds == null)
        figureBounds();
      
      if(i > max) {
        i = max;
        System.err.println("WordCloudPanel.getLevel() out of range: "+i);
      }
      if(i < min) {
        i = min;
        System.err.println("WordCloudPanel.getLevel() out of range: "+i);
      }
      // edge case
      if(i == max)
        i = max-1;
      
      for(int n = 0; n<grpBounds.length; n++)
        if(i < grpBounds[n]) {
          return grpBounds.length - n -1 ;  // flip array
        }
      
      System.err.println("program error getGroup?");
      return grpBounds.length-1;
    }
    private void figureBounds()
    {
      int range = max-min;
      grpBounds = new int[] { (int)(min+.05f*range), (int)(min+.15f*range), (int)(min+.30f*range), (int)(min+.60f*range), max};
    }
    /*
    private int oldgetGroup(int i)
    {
      if(i > max) {
        i = max;
        System.err.println("WordCloudPanel.getLevel() out of range: "+i);
      }
      if(i < min) {
        i = min;
        System.err.println("WordCloudPanel.getLevel() out of range: "+i);
      }
      // edge case
      if(i == max)
        i = max-1;
      
      int groupSize = findGroupSize();
      int gNum = (i-min)/groupSize;
      if(gNum >= numLevels)
        return numLevels-1;
      else
        return gNum;
    }
   
    private int findGroupSize()
    {
      int nLevs = numLevels;
      int groupSize = 0;
      while(nLevs > 0) {
        groupSize=(max-min)/nLevs--;
        if(groupSize >0)
          return groupSize;
      }
      return Integer.MAX_VALUE;   // one group
    }
    */
//    public int getLevel(int i)
//    {
//      int lev = getGroup(i);
//      if(mapper != null) 
//        lev = mapper[lev];
//      return lev;
//    }
  }
}
