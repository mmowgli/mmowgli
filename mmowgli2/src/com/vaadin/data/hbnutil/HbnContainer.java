/*
 * Copyright 2012, Gary Piercey, All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vaadin.data.hbnutil;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

import com.google.gwt.thirdparty.guava.common.cache.CacheBuilder;
import com.google.gwt.thirdparty.guava.common.cache.CacheLoader;
import com.google.gwt.thirdparty.guava.common.cache.LoadingCache;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.hbnutil.HbnContainer.EntityItem.EntityProperty;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import edu.nps.moves.mmowgli.hibernate.HSess;
// 11 Feb 2014 added here because of error with getContainerFilter in sources
public class HbnContainer<T> implements Container, Container.Indexed, Container.Sortable,
    Container.Filterable, Container.Hierarchical, Container.ItemSetChangeNotifier, Container.Ordered
{
  private static final long serialVersionUID = -6410337120924382057L;
  private ApplicationLogger logger = new ApplicationLogger(HbnContainer.class);

  private SessionFactory sessionFactory;
  private ClassMetadata classMetadata;
  private Class<T> entityType;
  private String parentPropertyName = null;
  private static final int ROW_BUF_SIZE = 100;
  private static final int ID_TO_INDEX_MAX_SIZE = 300;
  private boolean normalOrder = true;
  private List<T> ascRowBuffer;
  private List<T> descRowBuffer;
  private Object lastId;
  private Object firstId;
  private List<T> indexRowBuffer;
  private int indexRowBufferFirstIndex;
  private final Map<Object, Integer> idToIndex = new LinkedHashMap<Object, Integer>();
  private boolean[] orderAscendings;
  private Object[] orderPropertyIds;
  private Integer size;
  private LinkedList<ItemSetChangeListener> itemSetChangeListeners;
  private HashSet<ContainerFilter> filters;
  private final Map<String, Class<?>> addedProperties = new HashMap<String, Class<?>>();
  private final LoadingCache<Object, EntityItem<T>> cache;
  private final HashMap<Object, Boolean> embeddedPropertiesCache = new HashMap<Object, Boolean>();

  /**
   * Item wrappping a Hibernate mapped entity object. EntityItems are generally instantiated automatically by
   * HbnContainer.
   */
  @SuppressWarnings("hiding")
  public class EntityItem<T> implements Item
  {

    private static final long serialVersionUID = -2847179724504965599L;

    /**
     * Reference to hibernate mapped entity that this Item wraps.
     */
    protected T pojo;

    /**
     * Instantiated properties of this EntityItem. May be either EntityItemProperty (hibernate field) or manually
     * added container property (MethodProperty).
     */
    protected Map<Object, Property<?>> properties = new HashMap<Object, Property<?>>();

    @SuppressWarnings("unchecked")
    public EntityItem(Serializable id)
    {
      logger.executionTrace();
      Object key = HSess.checkInit();

      pojo = (T) HSess.get().get(entityType, id);
      // add non-hibernate mapped container properties
      for (String propertyId : addedProperties.keySet())
      {
        addItemProperty(propertyId, new MethodProperty<Object>(pojo, propertyId));
      }
      HSess.checkClose(key);
    }

    /**
     * @return the wrapped entity object.
     */
    public T getPojo()
    {
      logger.executionTrace();
      return pojo;
    }

    @SuppressWarnings("rawtypes")
    public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException
    {
      logger.executionTrace();

      properties.put(id, property);
      return true;
    }

    public Property<?> getItemProperty(Object id)
    {
      logger.executionTrace();

      Property<?> p = properties.get(id);
      if (p == null)
      {
        p = new EntityProperty(id.toString());
        properties.put(id, p);
      }
      return p;
    }

    public Collection<?> getItemPropertyIds()
    {
      logger.executionTrace();

      return getContainerPropertyIds();
    }

    public boolean removeItemProperty(Object id) throws UnsupportedOperationException
    {
      logger.executionTrace();

      Property<?> removed = properties.remove(id);
      return removed != null;
    }

    /**
     * EntityItemProperty wraps one Hibernate controlled field of the pojo used by EntityItem. For common fields the
     * field value is the same as Property value. For relation fields it is the identifier of related object or a
     * collection of identifiers.
     * 
     * The Property is a simple data object that contains one typed value. This interface contains methods to
     * inspect and modify the stored value and its type, and the object's read-only state.
     * 
     * The Property also defines the events ReadOnlyStatusChangeEvent and ValueChangeEvent, and the associated
     * listener and notifier interfaces.
     * 
     * The Property.Viewer interface should be used to attach the Property to an external data source. This way the
     * value in the data source can be inspected using the Property interface.
     * 
     * The Property.editor interface should be implemented if the value needs to be changed through the implementing
     * class.
     */
    @SuppressWarnings("rawtypes")
    public class EntityProperty implements Property, Property.ValueChangeNotifier
    {
      private static final long serialVersionUID = -4086774943938055297L;
      private List<ValueChangeListener> valueChangeListeners;
      private String propertyName;

      /**
       * Default Constructor.
       */
      public EntityProperty(String propertyName)
      {
        logger.executionTrace();

        this.propertyName = propertyName;
      }

      /**
       * This method gets the value that is stored by the property. The returned object is compatible with the
       * class returned by getType().
       */
      @SuppressWarnings("unchecked")
      @Override
      public Object getValue()
      {
        logger.executionTrace();
        Object key = HSess.checkInit();
        
        final Session session = HSess.get(); //sessionFactory.getCurrentSession();
        final SessionImplementor sessionImplementor = (SessionImplementor) session;

        if (!session.contains(pojo))
          pojo = (T) session.get(entityType, (Serializable) getIdForPojoTL(pojo));

        if (propertyInEmbeddedKey(propertyName))
        {
          final ComponentType identifierType = (ComponentType) classMetadata.getIdentifierType();
          final String[] propertyNames = identifierType.getPropertyNames();

          for (int i = 0; i < propertyNames.length; i++)
          {
            String name = propertyNames[i];

            if (name.equals(propertyName))
            {
              final Object id = classMetadata.getIdentifier(pojo, sessionImplementor);
              HSess.checkClose(key);
              return identifierType.getPropertyValue(id, i, EntityMode.POJO);
            }
          }
        }

        final Type propertyType = getPropertyType();
        final Object propertyValue = classMetadata.getPropertyValue(pojo, propertyName);

        if (!propertyType.isAssociationType()) {
          HSess.checkClose(key);
          return propertyValue;
        }
        if (propertyType.isCollectionType())
        {
          if (propertyValue == null) {
            HSess.checkClose(key);
            return null;
          }
          final HashSet<Serializable> identifiers = new HashSet<Serializable>();
          final Collection<?> pojos = (Collection<?>) propertyValue;

          for (Object object : pojos)
          {
            if (!session.contains(object))
              object = session.merge(object);

            identifiers.add(session.getIdentifier(object));
          }
          HSess.checkClose(key);
          return identifiers;
        }

        if (propertyValue == null) {
          HSess.checkClose(key);
          return null;
        }
        final Class<?> propertyTypeClass = propertyType.getReturnedClass();
        final ClassMetadata metadata = sessionFactory.getClassMetadata(propertyTypeClass);
        final Serializable identifier = metadata.getIdentifier(propertyValue, sessionImplementor);

        return identifier;
      }

      /**
       * This method tests if the Property is in read-only mode. In read-only mode calls to the method setValue
       * will throw ReadOnlyException and will not modify the value of the Property.
       */
      @Override
      public boolean isReadOnly()
      {
        logger.executionTrace();

        return false;
      }

      /**
       * This method sets the property's read-only mode to the specified status. This functionality is optional,
       * but all properties must implement the isReadOnly mode query correctly.
       * 
       * HbnContainer does not implement this functionality and will throw an UnsupportedOperationException of
       * this method is called.
       */
      @Override
      public void setReadOnly(boolean newStatus)
      {
        throw new UnsupportedOperationException();
      }

      /**
       * This method sets the value of the property.
       * 
       * Implementing this functionality is optional. If the functionality is missing, one should declare the
       * Property to be in read-only mode and throw Property.ReadOnlyException in this function.
       * 
       * Note : Since Vaadin 7.0, setting the value of a non-String property as a String is no longer supported.
       */
      @Override
      public void setValue(Object newValue) throws ReadOnlyException, ConversionException
      {
        logger.executionTrace();

        try
        {
          HSess.init();
          final Session session = HSess.get(); //sessionFactory.getCurrentSession();
          final SessionImplementor sessionImplementor = (SessionImplementor) session; //sessionFactory.getCurrentSession();

          Object value;

          try
          {
            if (newValue == null || getType().isAssignableFrom(newValue.getClass()))
            {
              value = newValue;
            }
            else
            {
              final Constructor<?> constr = getType().getConstructor(new Class[] { String.class });
              value = constr.newInstance(new Object[] { newValue.toString() });
            }

            if (propertyInEmbeddedKey(propertyName))
            {
              final ComponentType identifierType = (ComponentType) classMetadata.getIdentifierType();
              final String[] propertyNames = identifierType.getPropertyNames();

              for (int i = 0; i < propertyNames.length; i++)
              {
                String name = propertyNames[i];

                if (name.equals(propertyName))
                {
                  final Object identifier = classMetadata.getIdentifier(pojo, sessionImplementor);
                  final Object[] values = identifierType.getPropertyValues(identifier,
                      EntityMode.POJO);

                  values[i] = value;
                  identifierType.setPropertyValues(identifier, values, EntityMode.POJO);
                }
              }
            }
            else
            {
              final Type propertyType = classMetadata.getPropertyType(propertyName);

              if (propertyType.isCollectionType())
              {
                final Field declaredField = entityType.getDeclaredField(propertyName);
                final java.lang.reflect.Type genericType = declaredField.getGenericType();
                final java.lang.reflect.Type[] actualTypeArguments =
                    ((ParameterizedType) genericType).getActualTypeArguments();
                final java.lang.reflect.Type assosiatedType = actualTypeArguments[0];
                final String typestring = assosiatedType.toString().substring(6);

                // Reuse existing persistent collection if possible so Hibernate may optimize queries
                // properly.

                @SuppressWarnings("unchecked")
                Collection<Object> pojoCollection = (Collection<Object>) classMetadata
                    .getPropertyValue(pojo, propertyName);

                if (pojoCollection == null)
                {
                  pojoCollection = new HashSet<Object>();
                  classMetadata.setPropertyValue(pojo, propertyName, pojoCollection);
                }

                final Collection<Object> orphans = new HashSet<Object>(pojoCollection);
                final Collection<?> identifiers = (Collection<?>) value;

                for (Object id : identifiers)
                {
                  final Object object = session.get(typestring, (Serializable) id);
                  if (!pojoCollection.contains(object))
                  {
                    pojoCollection.add(object);
                  }
                  else
                  {
                    orphans.remove(object);
                  }
                }

                pojoCollection.removeAll(orphans);
              }
              else if (propertyType.isAssociationType())
              {
                final Class<?> referencedType = classMetadata
                    .getPropertyType(propertyName)
                    .getReturnedClass();

                final Object object = HSess.get() //sessionFactory
                    //.getCurrentSession()
                    .get(referencedType, (Serializable) value);

                classMetadata.setPropertyValue(pojo, propertyName, object);
                //sessionFactory.getCurrentSession().merge(object);
                //sessionFactory.getCurrentSession().saveOrUpdate(pojo);
                HSess.get().merge(object);
                HSess.get().saveOrUpdate(pojo);
             }
              else
              {
                classMetadata.setPropertyValue(pojo, propertyName, value);
              }
            }

            @SuppressWarnings("unchecked")
            T newPojo = (T) session.merge(pojo);
            pojo = newPojo;

            fireValueChange();
          }
          catch (Exception e)
          {
            logger.error(e);
            HSess.close();
            throw new ConversionException(e);
          }
        }
        catch (HibernateException e)
        {
          logger.error(e);
        }
        HSess.close();
      }

      /**
       * This method registers a new value change listener for this property.
       */
      @Override
      public void addListener(ValueChangeListener listener)
      {
        logger.executionTrace();

        if (valueChangeListeners == null)
          valueChangeListeners = new LinkedList<ValueChangeListener>();

        if (!valueChangeListeners.contains(listener))
          valueChangeListeners.add(listener);
      }

      /**
       * This method removes a previously registered value change listener.
       */
      @Override
      public void removeListener(ValueChangeListener listener)
      {
        logger.executionTrace();

        if (valueChangeListeners != null)
          valueChangeListeners.remove(listener);
      }

      /**
       * This method registers a new value change listener for this property.
       */
      @Override
      public void addValueChangeListener(ValueChangeListener listener)
      {
        logger.executionTrace();

        addListener(listener);
      }

      /**
       * This method removes a previously registered value change listener.
       */
      @Override
      public void removeValueChangeListener(ValueChangeListener listener)
      {
        logger.executionTrace();

        removeListener(listener);
      }

      /**
       * This method returns a string representation of the object. In general, the toString method returns a
       * string that "textually represents" this object. The result should be a concise but informative
       * representation that is easy for a person to read. It is recommended that all subclasses override this
       * method.
       * 
       * The toString method for class Object returns a string consisting of the name of the class of which the
       * object is an instance, the at-sign character `@', and the unsigned hexadecimal representation of the hash
       * code of the object. In other words, this method returns a string equal to the value of:
       * 
       * getClass().getName() + '@' + Integer.toHexString(hashCode())
       */
      @Override
      public String toString()
      {
        logger.executionTrace();

        final Object value = getValue();
        return (value != null) ? value.toString() : null;
      }

      /**
       * This method returns a reference to the containing EntityItem.
       */
      public EntityItem<T> getEntityItem()
      {
        logger.executionTrace();

        return EntityItem.this;
      }

      /**
       * This method returns a reference to the associated pojo.
       */
      public T getPojo()
      {
        logger.executionTrace();

        return pojo;
      }

      /**
       * This method returns the raw type of this property.
       */
      private Type getPropertyType()
      {
        logger.executionTrace();

        return classMetadata.getPropertyType(propertyName);
      }

      /**
       * Returns the type of the Property. The methods getValue and setValue must be compatible with this type:
       * one must be able to safely cast the value returned from getValue to the given type and pass any variable
       * assignable to this type as an argument to setValue.
       */
      public Class<?> getType()
      {
        logger.executionTrace();

        if (propertyInEmbeddedKey(propertyName))
        {
          final ComponentType idType = (ComponentType) classMetadata.getIdentifierType();
          final String[] propertyNames = idType.getPropertyNames();

          for (String name : propertyNames)
          {
            if (name.equals(propertyName))
            {
              try
              {
                final String identifierName = classMetadata.getIdentifierPropertyName();
                final Field identifierField = entityType.getDeclaredField(identifierName);
                final Field propertyField = identifierField.getType().getDeclaredField(propertyName);
                return propertyField.getType();
              }
              catch (NoSuchFieldException e)
              {
                logger.error(e);
                throw new RuntimeException("Failed to find the type of the container property.", e);
              }
            }
          }
        }

        final Type propertyType = getPropertyType();

        if (propertyType.isCollectionType())
        {
          final Class<?> returnedClass = propertyType.getReturnedClass();
          return returnedClass;
        }

        if (propertyType.isAssociationType())
        {
          // For association the the property value type is the type of referenced types identifier.
          final ClassMetadata metadata = sessionFactory.getClassMetadata(
              classMetadata.getPropertyType(propertyName).getReturnedClass());

          return metadata.getIdentifierType().getReturnedClass();
        }

        return classMetadata.getPropertyType(propertyName).getReturnedClass();
      }

      /**
       * Implements a value change event.
       */
      private class HbnPropertyValueChangeEvent implements Property.ValueChangeEvent
      {
        private static final long serialVersionUID = 166764621324404579L;

        public Property<?> getProperty()
        {
          return EntityProperty.this;
        }
      }

      /**
       * This method is used to fire a value change event.
       */
      private void fireValueChange()
      {
        logger.executionTrace();
        if (valueChangeListeners != null)
        {
          final HbnPropertyValueChangeEvent event = new HbnPropertyValueChangeEvent();
          final Object[] array = valueChangeListeners.toArray();

          for (int i = 0; i < array.length; i++)
          {
            final ValueChangeListener listener = (ValueChangeListener) array[i];
            listener.valueChange(event);
          }
        }
      }
    }
  }

  /**
   * Constructor creates a new instance of HbnContainer.
   */
  public HbnContainer(Class<T> entityType, SessionFactory sessionFactory)
  {
    logger.executionTrace();

    this.entityType = entityType;
    this.sessionFactory = sessionFactory;
    this.classMetadata = sessionFactory.getClassMetadata(entityType);

    this.cache = CacheBuilder.newBuilder()
        .expireAfterAccess(2, TimeUnit.MINUTES)
        .maximumSize(10000)
        .recordStats()
        .weakValues()
        .build(new CacheLoader<Object, EntityItem<T>>()
        {
          @Override
          public EntityItem<T> load(Object entityId) throws Exception
          {
            try
            {
              return loadEntity((Serializable) entityId);
            }
            catch (Exception e)
            {
              logger.error(e);
              throw e;
            }
          }
        });
  }

  /**
   * This method is used to load an entity from the database. This method is called automatically by the cache loader
   * when it needs to load an entity into the cache but it can be called manually if necessary.
   */
  protected EntityItem<T> loadEntity(Serializable entityId)
  {
    logger.executionTrace();

    EntityItem<T> entity = null;

    if (entityId != null)
      entity = new EntityItem<T>(entityId);

    return entity;
  }

  /**
   * This method is used to save an entity to the database and in the process it will fire an item set change event.
   */
  public Serializable saveEntity(T entity)
  {
    logger.executionTrace();
    HSess.init();
    final Session session = HSess.get(); //sessionFactory.getCurrentSession();
    final Object entityId = session.save(entity);

    clearInternalCache();
    fireItemSetChange();
    HSess.close();
    return (Serializable) entityId;
  }

  /**
   * This method is used to update an entity in the database, update the cache and fire value change events when
   * necessary.
   */
  public Serializable updateEntity(T entity)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    final Session session = HSess.get(); //sessionFactory.getCurrentSession();
    session.update(entity);

    final Object entityId = getIdForPojoTL(entity);
    final EntityItem<T> cachedEntity = cache.getIfPresent(entityId);

    cache.refresh(entityId);

    if (cachedEntity != null)
    {
      for (Object propertyId : cachedEntity.getItemPropertyIds())
      {
        Property<?> cachedProperty = cachedEntity.getItemProperty(propertyId);
        if (cachedProperty instanceof EntityItem.EntityProperty)
        {
          @SuppressWarnings("rawtypes")
          EntityProperty entityProperty = (EntityProperty) cachedProperty;
          entityProperty.fireValueChange();
        }
      }
    }
    HSess.checkClose(key);
    return (Serializable) entityId;
  }

  /**
   * This method adds a new property to all items in the container. The property id, data type and default value of
   * the new Property are given as parameters. HbnContainer automatically adds all fields that are mapped by Hibernate
   * to the database. With this method we can add a property to the container that is contained in the pojo but not
   * Hibernate mapped.
   */
  @Override
  public boolean addContainerProperty(Object propertyId, Class<?> classType, Object defaultValue)
      throws UnsupportedOperationException
  {
    logger.executionTrace();

    boolean propertyExists = true;

    try
    {
      new MethodProperty<Object>(this.entityType.newInstance(), propertyId.toString());
    }
    catch (Exception e)
    {
      logger.debug("Note: this is not an error: " + e);
      propertyExists = false;
    }

    addedProperties.put(propertyId.toString(), classType);
    return propertyExists;
  }

  /**
   * Creates a new Item into the Container and assigns it an automatic ID. The new ID is returned, or null if the
   * operation fails. After a successful call you can use the getItemmethod to fetch the Item. This functionality is
   * optional.
   */
  @Override
  public Object addItem() throws UnsupportedOperationException
  {
    logger.executionTrace();

    try
    {
      final T entity = entityType.newInstance();
      return saveEntity(entity);
    }
    catch (Exception e)
    {
      logger.error(e);
      return null;
    }
  }

  /**
   * Creates a new Item with the given ID in the Container. The new Item is returned, and it is ready to have its
   * Properties modified. Returns null if the operation fails or the Container already contains a Item with the given
   * ID. This functionality is optional.
   * 
   * Note that in this implementation we are expecting auto-generated identifiers so this method is not implemented.
   */
  @Override
  public Item addItem(Object entityId) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Tests if the Container contains the specified Item. Filtering can hide items so that they will not be visible
   * through the container API, and this method should respect visibility of items (i.e. only indicate visible items
   * as being in the container) if feasible for the container.
   */
  @Override
  public boolean containsId(Object entityId)
  {
    logger.executionTrace();

    try
    {
      final EntityItem<T> entity = cache.get(entityId);
      return (entity != null);
    }
    catch (Exception e)
    {
      logger.error(e);
      return false;
    }
  }

  /**
   * Gets the Property identified by the given entityId and propertyId from the Container. If the Container does not
   * contain the item or it is filtered out, or the Container does not have the Property, null is returned.
   */
  @Override
  public Property<?> getContainerProperty(Object entityId, Object propertyId)
  {
    logger.executionTrace();

    try
    {
      EntityItem<?> entity = cache.get(entityId);
      Property<?> property = entity.getItemProperty(propertyId);
      return property;
    }
    catch (Exception e)
    {
      logger.error(e);
      return null;
    }
  }

  /**
   * Gets the ID's of all Properties stored in the Container. The ID's cannot be modified through the returned
   * collection.
   */
  @Override
  public Collection<String> getContainerPropertyIds()
  {
    logger.executionTrace();

    Collection<String> propertyIds = getSortableContainerPropertyIds();
    propertyIds.addAll(addedProperties.keySet());
    return propertyIds;
  }

  /**
   * This is an HbnContainer specific utility method that is used to retrieve the list of embedded property key
   * identifiers.
   */
  private Collection<String> getEmbeddedKeyPropertyIds()
  {
    logger.executionTrace();

    final ArrayList<String> embeddedKeyPropertyIds = new ArrayList<String>();
    final Type identifierType = classMetadata.getIdentifierType();

    if (identifierType.isComponentType())
    {
      final ComponentType idComponent = (ComponentType) identifierType;
      final String[] propertyNameArray = idComponent.getPropertyNames();

      if (propertyNameArray != null)
      {
        final List<String> propertyNames = Arrays.asList(propertyNameArray);
        embeddedKeyPropertyIds.addAll(propertyNames);
      }
    }

    return embeddedKeyPropertyIds;
  }

  /**
   * Gets the Item with the given Item ID from the Container. If the Container does not contain the requested Item,
   * null is returned. Containers should not return Items that are filtered out.
   */
  @Override
  public EntityItem<T> getItem(Object entityId)
  {
    logger.executionTrace();

    try
    {
      return cache.get(entityId);
    }
    catch (ExecutionException e)
    {
      logger.error(e);
      return null;
    }
  }

  /**
   * Gets the ID's of all visible (after filtering and sorting) Items stored in the Container. The ID's cannot be
   * modified through the returned collection. If the container is Container.Ordered, the collection returned by this
   * method should follow that order. If the container is Container.Sortable, the items should be in the sorted order.
   * Calling this method for large lazy containers can be an expensive operation and should be avoided when practical.
   * 
   * Create an optimized query to return only identifiers. Note that this method does not scale well for large
   * database. At least Table is optimized so that it does not call this method.
   */
  @Override
  public Collection<?> getItemIds()
  {
    logger.executionTrace();
    Object key = HSess.checkInit(); 

    // TODO: BUG: does not preserve sort order!
    final Criteria criteria = getCriteriaTL();
    criteria.setProjection(Projections.id());
    Collection<?> reto = criteria.list();
    
    HSess.checkClose(key);
    return reto;
  }

  /**
   * Get numberOfItems consecutive item ids from the container, starting with the item id at startIndex.
   * 
   * Implementations should return at most numberOfItems item ids, but can contain less if the container has less
   * items than required to fulfill the request. The returned list must hence contain all of the item ids from the
   * range:
   * 
   * startIndex to max(startIndex + (numberOfItems-1), container.size()-1).
   */
  @Override
  public List<?> getItemIds(int startIndex, int count)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();

    final List<?> entityIds = (List<?>) getItemIds();
    List<?> retObj = entityIds.subList(startIndex, startIndex + count);
    
    HSess.checkClose(key);
    return retObj;
  }

  /**
   * Gets the data type of all Properties identified by the given Property ID. This method does pretty much the same
   * thing as EntityItemProperty#getType()
   */
  public Class<?> getType(Object propertyId)
  {
    logger.executionTrace();

    // TODO: refactor to use same code as EntityItemProperty#getType()
    // This will also fix incomplete implementation of this method (for association types). Not critical as
    // Components don't really rely on this methods.

    if (addedProperties.keySet().contains(propertyId))
      return addedProperties.get(propertyId);

    if (propertyInEmbeddedKey(propertyId))
    {
      final ComponentType idType = (ComponentType) classMetadata.getIdentifierType();
      final String[] propertyNames = idType.getPropertyNames();

      for (int i = 0; i < propertyNames.length; i++)
      {
        String name = propertyNames[i];
        if (name.equals(propertyId))
        {
          String idName = classMetadata.getIdentifierPropertyName();
          try
          {
            Field idField = entityType.getDeclaredField(idName);
            Field propertyField = idField.getType().getDeclaredField((String) propertyId);
            return propertyField.getType();
          }
          catch (NoSuchFieldException ex)
          {
            throw new RuntimeException("Could not find the type of specified container property.", ex);
          }
        }
      }
    }

    Type propertyType = classMetadata.getPropertyType(propertyId.toString());
    return propertyType.getReturnedClass();
  }

  /**
   * Removes all Items from the Container. Note that Property ID and type information is preserved. This functionality
   * is optional.
   */
  @Override
  public boolean removeAllItems() throws UnsupportedOperationException
  {
    logger.executionTrace();
    HSess.init();
    try
    {
      final Session session = HSess.get(); //sessionFactory.getCurrentSession();
      final Query query = session.createQuery("DELETE FROM " + entityType.getSimpleName());

      final int deleted = query.executeUpdate();
      cache.invalidateAll();

      if (deleted > 0)
      {
        clearInternalCache();
        fireItemSetChange();
      }
      HSess.close();
      return (size() == 0);
    }
    catch (Exception e)
    {
      logger.error(e);
      HSess.close();
      return false;
    }
  }

  /**
   * Removes a Property specified by the given Property ID from the Container. Note that the Property will be removed
   * from all Items in the Container. This functionality is optional.
   */
  @Override
  public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException
  {
    logger.executionTrace();

    final Class<?> removed = addedProperties.remove(propertyId);
    return (removed != null);
  }

  /**
   * Removes the Item identified by entityId from the Container. Containers that support filtering should also allow
   * removing an item that is currently filtered out. This functionality is optional.
   * 
   * Note that this method recursively removes all children of this entity before removing this entity.
   */
  private static int recursed = 0;
  @Override
  public boolean removeItem(Object entityId) throws UnsupportedOperationException
  {
    logger.executionTrace();
    if(recursed==0) {
      HSess.init();
      recursed++;
    }
    for (Object id : getChildren(entityId))
      removeItem(id);

    final Session session = HSess.get(); //sessionFactory.getCurrentSession();
    final Object entity = session.load(entityType, (Serializable) entityId);

    session.delete(entity);
    cache.invalidate(entityId);

    clearInternalCache();
    fireItemSetChange();
    
    if(recursed-- == 0)
      HSess.close();
    return true;
  }

  /**
   * Gets the number of visible Items in the Container. Filtering can hide items so that they will not be visible
   * through the container API.
   */
  @Override
  public int size()
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    
    size = ((Number) getBaseCriteriaTL()
        .setProjection(Projections.rowCount())
        .uniqueResult())
        .intValue();

    HSess.checkClose(key);
    return size.intValue();
  }

  /**
   * Adds a new item after the given item. Adding an item after null item adds the item as first item of the ordered
   * container. Note that we can't implement properly for database backed container like this so it is unsupported.
   */
  @Override
  public Object addItemAfter(Object previousEntityId) throws UnsupportedOperationException
  {
    logger.executionTrace();

    throw new UnsupportedOperationException();
  }

  /**
   * Adds a new item after the given item. Adding an item after null item adds the item as first item of the ordered
   * container. Note that we can't implement properly for database backed container like this so it is unsupported.
   */
  @Override
  public Item addItemAfter(Object previousEntityId, Object newEntityId) throws UnsupportedOperationException
  {
    logger.executionTrace();

    throw new UnsupportedOperationException();
  }

  /**
   * Gets the ID of the first Item in the Container.
   */
  @Override
  public Object firstItemId()
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    
    final Object firstPojo = getCriteriaTL()
        .setMaxResults(1)
        .setCacheable(true)
        .uniqueResult();

    firstId = getIdForPojoTL(firstPojo);
    idToIndex.put(firstId, normalOrder ? 0 : size() - 1);
    
    HSess.checkClose(key);
    return firstId;
  }

  /**
   * Tests if the Item corresponding to the given Item ID is the first Item in the Container.
   */
  @Override
  public boolean isFirstId(Object entityId)
  {
    logger.executionTrace();
    return entityId.equals(firstItemId());
  }

  /**
   * Tests if the Item corresponding to the given Item ID is the last Item in the Container.
   */
  @Override
  public boolean isLastId(Object entityId)
  {
    logger.executionTrace();
    return entityId.equals(lastItemId());
  }

  /**
   * Gets the ID of the last Item in the Container.
   */
  @Override
  public Object lastItemId()
  {
    logger.executionTrace();

    if (lastId == null)
    {
      normalOrder = !normalOrder;
      lastId = firstItemId();
      normalOrder = !normalOrder;
    }

    return lastId;
  }

  /**
   * Gets the ID of the Item following the Item that corresponds to entityId. If the given Item is the last or not
   * found in the Container, null is returned.
   * 
   * This is a simple method but it contains a lot of code. The complicated logic is needed to avoid:
   * 
   * - a large number of database queries - scrolling through a large query result
   * 
   * This way this container can be used with large data sets.
   */
  @Override
  public Object nextItemId(Object entityId)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    
    EntityItem<T> entity = null;
    List<T> rowBuffer = null;

    try
    {
      entity = cache.get(entityId);
      rowBuffer = getRowBuffer();
    }
    catch (Exception e)
    {
      logger.error(e);
      HSess.checkClose(key);
      return null;
    }

    try
    {
      int index;
      if ((index = rowBuffer.indexOf(entity.getPojo())) != -1)
      {
        final T nextEntity = rowBuffer.get(index + 1);
        Object reto = getIdForPojoTL(nextEntity);
        HSess.checkClose(key);
        return reto;
      }
    }
    catch (Exception e) // entityId is not in rowBuffer, suppress the exception
    {
    }

    int currentIndex = indexOfId(entityId);
    int size = size();

    int firstIndex = (normalOrder)
        ? currentIndex + 1
        : size - currentIndex;

    if (firstIndex < 0 || firstIndex >= size) {
      HSess.checkClose(key);
      return null;
    }
    final Criteria criteria = getCriteriaTL()
        .setFirstResult(firstIndex)
        .setMaxResults(ROW_BUF_SIZE);

    @SuppressWarnings("unchecked")
    final List<T> newRowBuffer = criteria.list();

    if (newRowBuffer.size() > 0)
    {
      setRowBuffer(newRowBuffer, firstIndex);
      final T nextPojo = newRowBuffer.get(0);
      Object reto = getIdForPojoTL(nextPojo);
      HSess.checkClose(key);
      return reto;
    }
    HSess.checkClose(key);
    return null;
  }

  /**
   * Gets the ID of the Item preceding the Item that corresponds to entityId. If the given Item is the first or not
   * found in the Container, null is returned.
   */
  @Override
  public Object prevItemId(Object entityId)
  {
    logger.executionTrace();

    normalOrder = !normalOrder;
    Object previous = nextItemId(entityId);
    normalOrder = !normalOrder;
    return previous;
  }

  /**
   * Not supported in HbnContainer. Indexing/order is controlled by underlying database.
   */
  @Override
  public Object addItemAt(int index) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported in HbnContainer. Indexing/order is controlled by underlying database.
   */
  @Override
  public Item addItemAt(int index, Object newEntityId) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Get the item id for the item at the position given by index.
   */
  @Override
  public Object getIdByIndex(int index)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    if (indexRowBuffer == null)
      resetIndexRowBufferTL(index);

    int indexInCache = index - indexRowBufferFirstIndex;

    if (!(indexInCache >= 0 && indexInCache < indexRowBuffer.size()))
    {
      resetIndexRowBufferTL(index);
      indexInCache = 0;
    }

    final T pojo = indexRowBuffer.get(indexInCache);
    final Object id = getIdForPojoTL(pojo);

    idToIndex.put(id, new Integer(index));

    if (idToIndex.size() > ID_TO_INDEX_MAX_SIZE)
      idToIndex.remove(idToIndex.keySet().iterator().next());
    HSess.checkClose(key);
    return id;
  }

  /**
   * Gets the index of the Item corresponding to the entityId. The following is true for the returned index: 0 <=
   * index < size(), or index = -1 if there is no visible item with that id in the container.
   * 
   * Note! Expects that getIdByIndex is called for this entityId. Otherwise it will be potentially rather slow
   * operation with large tables. When used with Table, this shouldn't be a problem.
   */
  @Override
  public int indexOfId(Object entityId)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();
    
    Integer index = idToIndex.get(entityId);

//    return (index == null)
//        ? slowIndexOfId(entityId)
//        : index;
    
    if(index==null)
      index = slowIndexOfIdTL(entityId);
    
    HSess.checkClose(key);     
    return index;    
  }

  /**
   * Gets the container property IDs which can be used to sort the items.
   */
  @Override
  public Collection<String> getSortableContainerPropertyIds()
  {
    logger.executionTrace();

    final String[] propertyNames = classMetadata.getPropertyNames();
    final LinkedList<String> propertyIds = new LinkedList<String>();

    propertyIds.addAll(Arrays.asList(propertyNames));
    propertyIds.addAll(getEmbeddedKeyPropertyIds());

    return propertyIds;
  }

  /**
   * Sort method. Sorts the container items. Sorting a container can irreversibly change the order of its items or
   * only change the order temporarily, depending on the container.
   * 
   * HbnContainer does not actually sort anything here, just clearing cache will do the thing lazily.
   */
  @Override
  public void sort(Object[] propertyId, boolean[] ascending)
  {
    logger.executionTrace();

    clearInternalCache();
    orderPropertyIds = propertyId;
    orderAscendings = ascending;
  }

  /**
   * Remove all active filters from the container.
   */
  @Override
  public void removeAllContainerFilters()
  {
    logger.executionTrace();

    if (filters != null)
    {
      filters = null;
      clearInternalCache();
      fireItemSetChange();
    }
  }

  /**
   * HbnContainer only supports old style addContainerFilter(Object, String, boolean booblean) API and
   * {@link SimpleStringFilter}. Support for this newer API maybe in upcoming versions.
   * 
   * Also note that for complex filtering it is possible to override {@link #getBaseCriteria()} method and add filter
   * so the query directly.
   */
  // TODO support new filtering api properly
  @Override
  public void addContainerFilter(Filter filter) throws UnsupportedFilterException
  {
    logger.executionTrace();

    if (!(filter instanceof SimpleStringFilter))
    {
      final String message = "HbnContainer only supports old style addContainerFilter(Object, String, boolean booblean) API";
      throw new UnsupportedFilterException(message);
    }

    final SimpleStringFilter sf = (SimpleStringFilter) filter;
    final String filterString = sf.getFilterString();
    final Object propertyId = sf.getPropertyId();
    final boolean ignoreCase = sf.isIgnoreCase();
    final boolean onlyMatchPrefix = sf.isOnlyMatchPrefix();

    addContainerFilter(propertyId, filterString, ignoreCase, onlyMatchPrefix);
  }

  /**
   * Finds the identifiers for the children of the given item. The returned collection is unmodifiable.
   */
  @Override
  public Collection<?> getChildren(Object entityId)
  {
    logger.executionTrace();

    final ArrayList<Object> children = new ArrayList<Object>();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
        return children;

      for (Object id : getItemIds())
      {
        EntityItem<T> entity = cache.get(id);
        Property<?> property = entity.getItemProperty(parentPropertyName);
        Object value = property.getValue();

        if (entityId.equals(value))
          children.add(id);
      }
    }
    catch (Exception e)
    {
      logger.error(e);
    }

    return children;
  }

  /**
   * Gets the identifier of the given item's parent. If there is no parent or we are unable to infer the name of the
   * parent property this method will return null.
   */
  @Override
  public Object getParent(Object entityId)
  {
    logger.executionTrace();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
      {
        logger.warn("failed to find a parent property name; hierarchy may be incomplete.");
        return null;
      }

      final EntityItem<T> entity = cache.get(entityId);
      final Property<?> property = entity.getItemProperty(parentPropertyName);
      final Object value = property.getValue();

      return value;
    }
    catch (Exception e)
    {
      logger.error(e);
      return null;
    }
  }

  /**
   * Gets the IDs of all Items in the container that don't have a parent. Such items are called root Items. The
   * returned collection is unmodifiable.
   */
  @Override
  public Collection<?> rootItemIds()
  {
    logger.executionTrace();

    final ArrayList<Object> rootItems = new ArrayList<Object>();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
      {
        logger.warn("failed to find a parent property name; hierarchy may be incomplete.");
        return rootItems;
      }

      final Collection<?> allItemIds = getItemIds();

      for (Object id : allItemIds)
      {
        EntityItem<T> entity = cache.get(id);
        Property<?> property = entity.getItemProperty(parentPropertyName);
        Object value = property.getValue();

        if (value == null)
          rootItems.add(id);
      }
    }
    catch (Exception e)
    {
      logger.error(e);
    }

    return rootItems;
  }

  /**
   * Sets the parent of an Item. The new parent item must exist and be able to have children. (
   * areChildrenAllowed(Object) == true ). It is also possible to detach a node from the hierarchy (and thus make it
   * root) by setting the parent null. This operation is optional.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public boolean setParent(Object entityId, Object newParentId)
  {
    logger.executionTrace();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
      {
        logger.warn("failed to find a parent property name; unable to set the parent.");
        return false;
      }

      final EntityItem<T> item = cache.get(entityId);
      final Property property = item.getItemProperty(parentPropertyName);

      property.setValue(newParentId);
      final Object value = property.getValue();

      return (value.equals(newParentId));
    }
    catch (Exception e)
    {
      logger.error(e);
      return false;
    }
  }

  /**
   * Tests if the Item with given ID can have children.
   */
  @Override
  public boolean areChildrenAllowed(Object entityId)
  {
    logger.executionTrace();

    if ((parentPropertyName = getParentPropertyName()) != null)
      return containsId(entityId);

    return false;
  }

  /**
   * Sets the given Item's capability to have children. If the Item identified with entityId already has children and
   * areChildrenAllowed(Object) is false this method fails and false is returned.
   * 
   * The children must be first explicitly removed with setParent(Object entityId, Object newParentId)or
   * com.vaadin.data.Container.removeItem(Object entityId).
   * 
   * This operation is optional. If it is not implemented, the method always returns false.
   */
  @Override
  public boolean setChildrenAllowed(Object entityId, boolean areChildrenAllowed)
  {
    logger.executionTrace();

    return false;
  }

  /**
   * Tests if the Item specified with entityId is a root Item. The hierarchical container can have more than one root
   * and must have at least one unless it is empty. The getParent(Object entityId) method always returns null for root
   * Items.
   */
  @Override
  public boolean isRoot(Object entityId)
  {
    logger.executionTrace();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
      {
        logger.warn("failed to find a parent property name; hierarchy may be incomplete.");
        return false;
      }

      final EntityItem<T> item = cache.get(entityId);
      final Property<?> property = item.getItemProperty(parentPropertyName);
      final Object value = property.getValue();

      return (value == null);
    }
    catch (Exception e)
    {
      logger.error(e);
      return false;
    }
  }

  /**
   * Tests if the Item specified with entityId has child Items or if it is a leaf. The getChildren(Object entityId)
   * method always returns null for leaf Items.
   * 
   * Note that being a leaf does not imply whether or not an Item is allowed to have children.
   */
  @Override
  public boolean hasChildren(Object entityId)
  {
    logger.executionTrace();

    try
    {
      parentPropertyName = getParentPropertyName();

      if (parentPropertyName == null)
      {
        logger.warn("failed to find a parent property name; hierarchy may be incomplete.");
        return false;
      }

      for (Object id : getItemIds())
      {
        EntityItem<T> item = cache.get(id);
        Property<?> property = item.getItemProperty(parentPropertyName);
        Object value = property.getValue();

        if (entityId.equals(value))
          return true;
      }

      return false;
    }
    catch (Exception e)
    {
      logger.error(e);
      return false;
    }
  }

  /**
   * Adds an Item set change listener for the object.
   */
  @Override
  public void addItemSetChangeListener(ItemSetChangeListener listener)
  {
    logger.executionTrace();

    if (itemSetChangeListeners == null)
      itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();

    itemSetChangeListeners.add(listener);
  }

  /**
   * Removes the Item set change listener from the object.
   */
  @Override
  public void removeItemSetChangeListener(ItemSetChangeListener listener)
  {
    logger.executionTrace();
    
    if (itemSetChangeListeners != null)
      itemSetChangeListeners.remove(listener);
  }

  /**
   * Adds an Item set change listener for the object. This method is deprecated. You should use
   * addItemSetChangeListener() instead.
   */
  @Override
  @Deprecated
  public void addListener(ItemSetChangeListener listener)
  {
    logger.executionTrace();
    addItemSetChangeListener(listener);
  }

  /**
   * Removes the Item set change listener from the object. This method is deprecated. You should use
   * addItemSetChangeListener() instead.
   */
  @Override
  @Deprecated
  public void removeListener(ItemSetChangeListener listener)
  {
    logger.executionTrace();
    removeItemSetChangeListener(listener);
  }

  //
  // UTILITY METHODS
  //

  /**
   * This method was added mainly to allow unit tests to be written to cover the filter add/remove methods.
   */
  public Set<ContainerFilter> getTheseContainerFilters()
  {
    return filters;
  }

  @Override
  public Collection<Filter> getContainerFilters()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * This method was added mainly to allow unit tests to be written to cover the listener add/remove methods.
   */
  public List<ItemSetChangeListener> getItemSetChangeListeners()
  {
    return itemSetChangeListeners;
  }

  /**
   * This is an internal HbnContainer utility method. Determines if a property is contained within an embedded key.
   */
  private boolean propertyInEmbeddedKey(Object propertyId)
  {
    logger.executionTrace();

    if (embeddedPropertiesCache.containsKey(propertyId))
      return embeddedPropertiesCache.get(propertyId);

    final Type identifierType = classMetadata.getIdentifierType();

    if (identifierType.isComponentType())
    {
      final ComponentType componentType = (ComponentType) identifierType;
      final String[] idPropertyNames = componentType.getPropertyNames();
      final List<String> idPropertyNameList = Arrays.asList(idPropertyNames);
      return idPropertyNameList.contains(propertyId);
    }

    return false;
  }

  /**
   * This is an internal HbnContainer utility method. This method triggers events associated with the
   * ItemSetChangeListener.
   */
  private void fireItemSetChange()
  {
    logger.executionTrace();

    if (itemSetChangeListeners != null)
    {
      final Object[] changeListeners = itemSetChangeListeners.toArray();

      final Container.ItemSetChangeEvent changeEvent = new Container.ItemSetChangeEvent()
      {
        private static final long serialVersionUID = -3002746333251784195L;

        public Container getContainer()
        {
          return HbnContainer.this;
        }
      };

      for (int i = 0; i < changeListeners.length; i++)
      {
        ItemSetChangeListener changeListener = (ItemSetChangeListener) changeListeners[i];
        changeListener.containerItemSetChange(changeEvent);
      }
    }
  }

  /**
   * This is an internal HbnContainer utility method. Gets a base listing using current ordering criteria.
   */
  private Criteria getCriteriaTL()
  {
    logger.executionTrace();

    final Criteria criteria = getBaseCriteriaTL();
    final List<Order> orders = getOrder(!normalOrder);

    for (Order order : orders)
    {
      criteria.addOrder(order);
    }

    return criteria;
  }

  /**
   * This is an internal HbnContainer utility method. Return the ordering criteria in the order in which they should
   * be applied. The composed order must be stable and must include {@link #getNaturalOrder(boolean)} at the end.
   */
  protected final List<Order> getOrder(boolean flipOrder)
  {
    logger.executionTrace();

    final List<Order> orders = new ArrayList<Order>();
    orders.addAll(getDefaultOrder(flipOrder));
    orders.add(getNaturalOrder(flipOrder));
    return orders;
  }

  /**
   * This is an internal HbnContainer utility method. Returns the ordering to use for the container contents. The
   * default implementation provides the {@link Container.Sortable} functionality. Can be overridden to customize item
   * sort order.
   */
  protected List<Order> getDefaultOrder(boolean flipOrder)
  {
    logger.executionTrace();

    final List<Order> orders = new ArrayList<Order>();

    if (orderPropertyIds != null)
    {
      for (int i = 0; i < orderPropertyIds.length; i++)
      {
        String propertyId = orderPropertyIds[i].toString();

        if (propertyInEmbeddedKey(propertyId))
          propertyId = classMetadata.getIdentifierPropertyName() + "." + propertyId;

        boolean ascending = (flipOrder)
            ? !orderAscendings[i]
            : orderAscendings[i];

        Order order = (ascending)
            ? Order.asc(propertyId)
            : Order.desc(propertyId);

        orders.add(order);
      }
    }

    return orders;
  }

  /**
   * This is an internal HbnContainer utility method. Creates the base criteria for entity class and add possible
   * restrictions to query. This method is protected so developers can add their own custom criteria.
   */
  protected Criteria getBaseCriteriaTL()
  {
    // This is assumed to be called with an active Thread Local session   
    logger.executionTrace();
    final Session session = HSess.get();
    Criteria criteria = session.createCriteria(entityType);

    if (filters != null)
    {
      for (ContainerFilter filter : filters)
      {
        String idName = null;

        if (propertyInEmbeddedKey(filter.getPropertyId()))
          idName = classMetadata.getIdentifierPropertyName();

        criteria = criteria.add(filter.getCriterion(idName));
      }
    }
    return criteria;
  }

  /**
   * This is an internal HbnContainer utility method. Natural order is the order in which the database is sorted if
   * container has no other ordering set. Natural order is always added as least significant order to queries. This is
   * needed to keep items stable order across queries. The default implementation sorts entities by identifier column.
   */
  protected Order getNaturalOrder(boolean flipOrder)
  {
    logger.executionTrace();

    final String propertyName = getIdPropertyName();

    return (flipOrder)
        ? Order.desc(propertyName)
        : Order.asc(propertyName);
  }

  /**
   * This is an internal HbnContainer utility method to detect identifier of given entity object.
   */
  private Object getIdForPojoTL(Object pojo)
  {
    logger.executionTrace();

    final Session session = HSess.get(); //sessionFactory.getCurrentSession();
    Object retObj = classMetadata.getIdentifier(pojo, (SessionImplementor) session);

    return retObj;
  }

  /**
   * This is an internal HbnContainer utility method. RowBuffer stores a list of entity items to avoid excessive
   * number of DB queries.
   */
  private List<T> getRowBuffer()
  {
    logger.executionTrace();

    return (normalOrder) ? ascRowBuffer : descRowBuffer;
  }

  /**
   * This is an internal HbnContainer utility method. RowBuffer stores some pojos to avoid excessive number of DB
   * queries. Also updates the idToIndex map.
   */
  private void setRowBuffer(List<T> list, int firstIndex)
  {
    logger.executionTrace();
    Object key = HSess.checkInit();

    if (normalOrder)
    {
      ascRowBuffer = list;

      for (int i = 0; i < list.size(); ++i)
      {
        idToIndex.put(getIdForPojoTL(list.get(i)), firstIndex + i);
      }
    }
    else
    {
      descRowBuffer = list;
      final int lastIndex = size() - 1;

      for (int i = 0; i < list.size(); ++i)
      {
        idToIndex.put(getIdForPojoTL(list.get(i)), lastIndex - firstIndex - i);
      }
    }
    HSess.checkClose(key);

  }

  /**
   * This is an internal HbnContainer utility method that gets the property name of the identifier.
   */
  private String getIdPropertyName()
  {
    logger.executionTrace();

    return classMetadata.getIdentifierPropertyName();
  }

  /**
   * This is an internal HbnContainer utility method to query new set of entity items to cache from given index.
   */
  @SuppressWarnings("unchecked")
  private void resetIndexRowBufferTL(int index)
  {
    logger.executionTrace();

    indexRowBufferFirstIndex = index;
    indexRowBuffer = getCriteriaTL().setFirstResult(index).setMaxResults(ROW_BUF_SIZE).list();
  }

  /**
   * This is an internal HbnContainer utility method that gets the index of the given identifier.
   */
  private int slowIndexOfIdTL(Object entityId)
  {
    logger.executionTrace();

    final Criteria criteria = getCriteriaTL().setProjection(Projections.id());
    final List<?> list = criteria.list();
    return list.indexOf(entityId);
  }

  /**
   * This is an internal HbnContainer utility method. Adds container filter for hibernate mapped property. For
   * property not mapped by Hibernate.
   */
  public void addContainerFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix)
  {
    logger.executionTrace();

    addContainerFilter(new StringContainerFilter(propertyId, filterString, ignoreCase, onlyMatchPrefix));
  }

  /**
   * This is an internal HbnContainer utility method that adds a container filter.
   */
  public void addContainerFilter(ContainerFilter containerFilter)
  {
    logger.executionTrace();

    if (addedProperties.containsKey(containerFilter.getPropertyId()))
    {
      final String message = "HbnContainer does not support filtering properties not mapped by Hibernate";
      throw new UnsupportedOperationException(message);
    }

    if (filters == null)
      filters = new HashSet<ContainerFilter>();

    filters.add(containerFilter);

    clearInternalCache();
    fireItemSetChange();
  }

  /**
   * This is an internal HbnContainer utility method that removes container filters for the given property identifier.
   */
  public void removeContainerFilters(Object propertyId)
  {
    logger.executionTrace();

    if (filters != null)
    {
      for (Iterator<ContainerFilter> iterator = filters.iterator(); iterator.hasNext();)
      {
        ContainerFilter containerFilter = iterator.next();

        if (containerFilter.getPropertyId().equals(propertyId))
          iterator.remove();
      }

      clearInternalCache();
      fireItemSetChange();
    }
  }

  /**
   * This is an internal HbnContainer utility method that removes the given container filter.
   */
  @Override
  public void removeContainerFilter(Filter filter)
  {
    logger.executionTrace();

    // TODO support new filtering api properly
    // TODO the workaround for SimpleStringFilter works wrong, but hopefully will be good enough for now

    if (filter instanceof SimpleStringFilter)
    {
      final SimpleStringFilter sf = (SimpleStringFilter) filter;
      final Object propertyId = sf.getPropertyId();
      removeContainerFilters(propertyId);
    }
  }

  /**
   * This is an internal HbnContainer utility method that infers the name of the parent field belonging to the current
   * property based on type.
   */
  private String getParentPropertyName()
  {
    logger.executionTrace();

    // TODO: make this a little more robust, there are a number of cases where this will fail.

    if (parentPropertyName == null)
    {
      String[] propertyNames = classMetadata.getPropertyNames();

      for (int i = 0; i < propertyNames.length; ++i)
      {
        String entityTypeName = entityType.getName();
        String propertyTypeName = classMetadata.getPropertyType(propertyNames[i]).getName();

        if (entityTypeName.equals(propertyTypeName))
        {
          parentPropertyName = propertyNames[i];
          break;
        }
      }
    }

    return parentPropertyName;
  }

  /**
   * This is an internal HbnContainer utility method to clear all cache fields.
   */
  protected void clearInternalCache()
  {
    logger.executionTrace();

    idToIndex.clear();
    indexRowBuffer = null;
    ascRowBuffer = null;
    descRowBuffer = null;
    firstId = null;
    lastId = null;
    size = null;
    embeddedPropertiesCache.clear();
  }
}
