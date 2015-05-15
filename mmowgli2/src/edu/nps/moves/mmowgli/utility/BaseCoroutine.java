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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Bailey, jmbailey@nps.edu
 * 
 * This is a class which implements a kind of "coroutine", a type of subroutine with "multiple
 *  entry points for suspending and resuming execution at certain locations."  Use it by
 *  1. subclassing
 *  2. implementing at least step1 and step2, and others, similarly named, if needed
 *  3. execute through the "run()" method.
 *
 *  Example:
 *  ....
 *  MyCoroutine coor = new MyCoroutine();
 *  coor.run();
 *  ...
 *  
 *  class MyCoroutine extends BaseCoroutine
 *  {
 *    public MyCoroutine() {}
 *    
 *    public void step1()
 *    {
 *      // domain logic including inner asynch handler, which must run to complete coroutine
 *      // this method does not block
 *      asyncHandler()
 *      {
 *        // domain logic
 *        this.run();    // continue with the coroutine (step 2);
 *      }
 *    }
 *    
 *    public void step2()
 *    {
 *      // domain logic
 *      // if further steps in coroutine, this.run(); else...
 *      this.resetCoroutine(); // only if this coroutine instance will be reused.
 *    }
 *  }
 *  
 * @version	$Id$
 * @since $Date$
 * @copyright	Copyright (C) 2011
 */
public abstract class BaseCoroutine
{
  abstract public void step1();
  abstract public void step2();
  // others are found by reflection
  
  private int nextStep;
  private int lastStep;
  HashMap<Integer,Method> stepList;
  
  /**
   * Couroutine execution point
   */
  public void run()
  {
    try {
      Method m = stepList.get(nextStep);
      if (m == null)
        throw new CoroutineExecutionExhausted("Step " + nextStep + " does not exist");
      lastStep = nextStep;
      nextStep = lastStep + 1;
      
      m.invoke(this, (Object[]) null);
    }
    catch (Exception ex) { // CoroutineExecutionExhausted,InvocationTargetException,IllegalAccessException
      throw new RuntimeException("Coroutine exception: "+ ex.getClass().getSimpleName() + " / " + ex.getLocalizedMessage());
    }
  }
  
  public boolean isUnfinished()
  {
    return stepList.containsKey(nextStep);
  }
  
  private void initializeSteps()
  {
    nextStep = 1;
    lastStep = -1;
    Method[] meths = getClass().getDeclaredMethods();
    stepList = new HashMap<Integer,Method>(meths.length+1);

    Pattern pat = Pattern.compile("step(\\d+)");
    
    for (Method m : meths) {
      Matcher mat = pat.matcher(m.getName());
      if(mat.matches()) {
        m.setAccessible(true);
        String num = mat.group(1);
        int n = Integer.parseInt(num);
        stepList.put(n,m);         
      }
    }
  }
  
  @SuppressWarnings("serial")
  public static class CoroutineExecutionExhausted extends Exception
  {
    CoroutineExecutionExhausted(String s)
    {
      super(s);
    }
  }
  
  public BaseCoroutine()
  {
    resetCoroutine();
  }
  
  public void resetCoroutine()
  {
    initializeSteps();
  }
  
  protected void doNotAdvanceSteps()
  {
    lastStep = nextStep-1;
    nextStep--;
  }
}
