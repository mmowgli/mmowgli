package edu.nps.moves.mmowgliMobile.data;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

import com.vaadin.data.Container;

import edu.nps.moves.mmowgli.db.ActionPlan;
import edu.nps.moves.mmowgli.db.Card;
import edu.nps.moves.mmowgli.db.User;
import edu.nps.moves.mmowgli.hibernate.HSess;

/**
 * A folder can contain other folders or messages. A folder cannot contain both folders and subfolders.
 */
public class Folder extends AbstractPojo
{

  private static final long serialVersionUID = 1L;

  private Container container;
  private Class<?> pojoClass;
  private HashMap<String, Object> params = new HashMap<String, Object>();

  private/* public */Folder(String name)
  {
    this.name = name;
  }

  public Folder(String name, Container cntr, Class<?> pojoClass)
  {
    this(name);
    this.container = cntr;
    this.pojoClass = pojoClass;
  }

  public void addParam(String key, Object o)
  {
    params.put(key, o);
  }

  public Object getParam(String key)
  {
    return params.get(key);
  }

  private ArrayList<AbstractPojo> childList;

  public List<AbstractPojo> getChildren()
  {
    if (childList == null) {
      @SuppressWarnings("unchecked")
      Collection<Serializable> coll = (Collection<Serializable>) container.getItemIds();
      childList = new ArrayList<AbstractPojo>(coll.size());
      handleType(coll);
    }
    return childList;
  }

  private void handleType(Collection<Serializable> coll)
  {
    Object key = HSess.checkInit();
    AbstractPojo apojo;
    Session sess = HSess.get();
    for (Serializable ser : coll) {
      if (this.pojoClass == Card.class)
        apojo = new CardListEntry(Card.get(ser, sess));
      else if (pojoClass == ActionPlan.class)
        apojo = new ActionPlanListEntry(ActionPlan.get(ser, sess));
      else
        // if(pojoClass == User.class)
        apojo = new UserListEntry(User.get(ser, sess));
      apojo.setParent(this);
      childList.add(apojo);
    }
    HSess.checkClose(key);
  }

  public void setContainer(Container c)
  {
    this.container = c;
  }

  public Container getContainer()
  {
    return container;
  }

  public void setPojoClass(Class<?> c)
  {
    this.pojoClass = c;
  }

  public Class<?> getPojoClass()
  {
    return pojoClass;
  }
}
