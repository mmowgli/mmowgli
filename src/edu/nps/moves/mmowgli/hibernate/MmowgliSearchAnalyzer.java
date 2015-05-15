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

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.MmowgliEnglishAnalyzer;
import org.apache.lucene.util.Version;

/**
 * MmowgliSearchAnalyzer.java
 * This is an attempt to use a different analyzer which goes for roots of words and had a default
 * ignore set (stop set);  EnglishAnalyzer didn't have a default analyzer, which is needed by the hib search config system
 * Created on Aug 23, 2011
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class MmowgliSearchAnalyzer extends Analyzer
{
  private MmowgliEnglishAnalyzer impl;
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public MmowgliSearchAnalyzer()
  {
    super();
    // Used default stop set for English Analyser...with a few additions
    impl = new MmowgliEnglishAnalyzer(Version.LUCENE_33);  // used our own class just to handle underbars
    Set vSet = new VarArgsHashSet(stopWords);
    vSet.addAll(impl.getStopwordSet());   
    impl = new MmowgliEnglishAnalyzer(Version.LUCENE_33,vSet);
  }
  
  @Override
  public TokenStream tokenStream(String arg0, Reader arg1)
  {
    return impl.tokenStream(arg0, arg1);
  }
  
  @SuppressWarnings("serial")
  class VarArgsHashSet extends HashSet<String>
  {
    public VarArgsHashSet(String... args)
    {
      super();
      for(String s : args)
        add(s);
    }
  }
  
  private String[] stopWords = {
      "would","should","other","don't","about","which","could","think",
      "don't","won't","can't","didn't","isn't","wasn't",
      "re","a",
      
      "us",// 667
      "have",//  456
      "we",//  437
      "ysu",//  409
      "what",//  393
      "from",//  357
      "them",//  329
      "can",//  325
      "do",//  315
      "epic",//  307
      "more",//  307
      "how",//  287
      "you",//  268
      "i",//  257
      "make",//  249
      "need",//  233
      "all",//  209
      "like",//  203
      "get",//  186
      "so",//  163
      "out",//  158
      "than",//  156
      "just",//  154
      "ha",//  152
      "up",//  147
      "on",//  134
      "take",//  131
      "also",//  131
      "who",//  127
      "new",//  117
      "some",//  116
      "go",//  113
      "our",//  109
      "see",//  108
      "time",//  108
      "when",//  105
      "good",//  104
      "un",//  99
      "allow",//  97
      "most",//  97 
      
      "on",//  134
      "work",//  119
      "help",//  114
      "does",//  101      
      "etc",//  91      
      "why",//  87      
      "long",//  85      
      "want",//  84     
      "mean",//  82     
      "through",//  78
      "even",//  78
      "too",//  77     
      "high",//  77
      "need",//  76  
      
      "those",
      "where",
      "off",
      "any",
      "may",
      "pay",
      "already",
      "be"
  };
 
}
