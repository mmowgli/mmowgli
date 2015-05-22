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

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.UI;

/*
 * As of Vaadin 7.4.5, the focus call is flaky for widgets in the styled mmowgli dialogs.
 * This class simply spawns a thread which waits a half second, then pushes the focus.
 * Works, but shouldn't have to do it.
 */
public class FocusHack
{
	public static void focus(final AbstractField<?> field)
	{
		new Thread(new Runnable() {
			@Override
			public void run()
			{
//@formatter:off
				try {Thread.sleep(500l);}catch (InterruptedException ex) {}
				UI.getCurrent().access(new _fieldFocus(field));
//@formatter:on
			}
		}).start();
	}

	static class _fieldFocus implements Runnable
	{
		AbstractField<?> field;

		public _fieldFocus(AbstractField<?> field)
		{
			this.field = field;
		}

		@Override
		public void run()
		{
			field.focus();
			UI.getCurrent().push();
		}
	}
}
