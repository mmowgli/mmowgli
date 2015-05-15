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

package edu.nps.moves.mmowgli.utility;

/**
 * SysOut.java
 * Created on Feb 23, 2012
 *
 * MOVES Institute
 * Naval Postgraduate School, Monterey, CA, USA
 * www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 * 
 * A synchronizer for System.out.println message;
 * (is this really necessary?  seemed like a good idea)
 */
public class SysOut
{
  private static Object syncher = new Object();
  //@formatter:off  
  public static void print(boolean b)   {synchronized(syncher) {System.out.print(b);}}
  public static void print(char c)      {synchronized(syncher) {System.out.print(c);}}
  public static void print(char[] s)    {synchronized(syncher) {System.out.print(s);}}
  public static void print(double d)    {synchronized(syncher) {System.out.print(d);}}
  public static void print(float f)     {synchronized(syncher) {System.out.print(f);}}
  public static void print(int i)       {synchronized(syncher) {System.out.print(i);}}
  public static void print(long l)      {synchronized(syncher) {System.out.print(l);}}
  public static void print(Object obj)  {synchronized(syncher) {System.out.print(obj);}}
  public static void print(String s)    {synchronized(syncher) {System.out.print(s);}}
  public static void println()          {synchronized(syncher) {System.out.println();}}
  public static void println(boolean b) {synchronized(syncher) {System.out.println(b);}}
  public static void println(char c)    {synchronized(syncher) {System.out.println(c);}}
  public static void println(char[] ca) {synchronized(syncher) {System.out.println(ca);}}
  public static void println(double d)  {synchronized(syncher) {System.out.println(d);}}
  public static void println(float f)   {synchronized(syncher) {System.out.println(f);}}
  public static void println(int i)     {synchronized(syncher) {System.out.println(i);}}
  public static void println(long l)    {synchronized(syncher) {System.out.println(l);}}
  public static void println(Object obj){synchronized(syncher) {System.out.println(obj);}}
  public static void println(String s)  {synchronized(syncher) {System.out.println(s);}}
  //@formatter:on
}
