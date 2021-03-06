/*
  Copyright (C) 2010-2015 Modeling Virtual Environments and Simulation
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

import java.util.concurrent.ArrayBlockingQueue;

public class SerialExecutor implements  Runnable
{
  final ArrayBlockingQueue<Runnable> tasks = new ArrayBlockingQueue<Runnable>(1000);

  public SerialExecutor()
  {
    Thread t = new Thread(this);
    t.setPriority(Thread.NORM_PRIORITY);
    t.setName("SerialScoringExecutor");
    t.start();
  }
  public void execute(Runnable r)
  {
    try {
      tasks.put(r);
    }
    catch (InterruptedException e) {
      System.out.println("InterruptedException in SerialExecutor.execute()");
    }
  }
  public void run()
  {
    try {
      Runnable r = tasks.take();
      r.run();
    }
    catch (InterruptedException e) {
      System.out.println("InterruptedException in SerialExecutor.run()");
    }    
  }
}
