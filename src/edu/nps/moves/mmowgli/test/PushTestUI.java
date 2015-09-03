package edu.nps.moves.mmowgli.test;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import edu.nps.moves.mmowgli.utility.Instrumentation;

@SuppressWarnings("serial")
@JavaScript ({
  "../../VAADIN/instrument/mmowgli1.js"
})
@Theme("mmowgli2")
@Push(value=PushMode.MANUAL,transport=Transport.LONG_POLLING)
public class PushTestUI extends UI implements Runnable, ClickListener
{
  Label freqLab,lab;
  Button butt, slowButt;

  @Override
  protected void init(VaadinRequest request)
  {
    VerticalLayout vl = new VerticalLayout();
    HorizontalLayout hl = new HorizontalLayout();
    setContent(hl);
    hl.addComponent(vl);
    hl.setWidth("100%");
    hl.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
    vl.setSizeUndefined();
    vl.setSpacing(true);
    
    vl.addComponent(freqLab = new Label(freqDisplay()));
    freqLab.setSizeUndefined();
    vl.setComponentAlignment(freqLab, Alignment.MIDDLE_CENTER);
    
    vl.addComponent(lab = new Label("Results here"));
    lab.setSizeUndefined();
    vl.setComponentAlignment(lab, Alignment.MIDDLE_CENTER);
    lab.addStyleName("m-greyborder");
    lab.addStyleName("m-font-bold14");
    
    vl.addComponent(butt = new Button("faster"));
    vl.setComponentAlignment(butt, Alignment.MIDDLE_CENTER);
    
    vl.addComponent(slowButt = new Button("slower"));
    vl.setComponentAlignment(slowButt,Alignment.MIDDLE_CENTER);

    butt.addClickListener(this);
    slowButt.addClickListener(this);
    
    Instrumentation.addInstrumentation(vl);
    startThread();
  }

  Thread thread;  // null means thread to be killed
  int sleeptime;
  int pushPerSec = 2;
  private int count=1;
  private void startThread()
  {
    if(thread != null)
      stopThread();
    thread = new Thread(this);
    thread.setPriority(Thread.NORM_PRIORITY);
    thread.start();
  }
  private long sleepMs()
  {
    return 1000/pushPerSec; 
  }
  
  private String freqDisplay()
  {
    return ""+pushPerSec+" pushes per second";
  }
  private String countDisplay()
  {
    return ""+count++;
  }
  @Override
  public void run()
  {
    while (thread != null) {
      try {
        doPush();
        Thread.sleep(sleepMs());
      } catch (InterruptedException ex) {
        // just loop
      }
    }
    // return kills thread
  }

  private void doPush()
  {
    UI ui = UI.getCurrent();
    if(ui!=null)
      ui.access(new Runnable()
    {
      @Override
      public void run() 
      {
        freqLab.setValue(freqDisplay());
        lab.setValue(countDisplay());
        UI.getCurrent().push();
      }
    });
  }

  @Override
  public void buttonClick(ClickEvent event)
  {
    if(event.getButton() == slowButt) {
      pushPerSec--;
      if(pushPerSec <=1) {
        pushPerSec = 1;
        slowButt.setEnabled(false);
      }
      else
        slowButt.setEnabled(true);
    }
    else {
      pushPerSec++;
      slowButt.setEnabled(true);
    }
    if(thread!= null)
      thread.interrupt();
  }

  private void stopThread()
  {
    Thread t = thread;
    thread = null;
    if(t != null)
      t.interrupt();    
  }
  
  @Override
  public void attach()
  {
    super.attach();
    startThread();
  }
  
  @Override
  public void detach()
  {
    super.detach();
    stopThread();
  }
  
}