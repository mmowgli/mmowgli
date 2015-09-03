package edu.nps.moves.mmowgli.utility;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.ui.*;

public class Instrumentation
{
	private static String dbg =
			"<div id='triggerDbugDiv' "+
      "onMouseDown='triggerDbugDivMouseDown();' "+
  		"></div>";

  public static void addInstrumentation(VerticalLayout vLay)
  {
  	try {
  		Component comp;
  	  vLay.addComponent(comp = new CustomLayout(new ByteArrayInputStream(dbg.getBytes())));
  	  comp.setHeight("10px");
  	  comp.setWidth("10px");
  	}
  	catch(IOException ex) {
  		System.out.println("Can't instrument verticalLayout.  Exception caught, trace:");
  		ex.printStackTrace();
  	} 	
  }
  /*
  public static void addInstrumentation(AbsoluteLayout absLay)
  {
  	try {
  		Component comp;
  	  absLay.addComponent(comp = new CustomLayout(new ByteArrayInputStream(dbg.getBytes())),"top:0px;left:0px");
  	  comp.setHeight("10px");
  	  comp.setWidth("10px");
  	}
  	catch(IOException ex) {
  		System.out.println("Can't instrument absoluteLayout.  Exception caught, trace:");
  		ex.printStackTrace();
  	} 	
  }
  */
}
