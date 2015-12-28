package com.github.alessandrocolantoni.mom.objectsQuery.javaObjectsQuery.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.github.alessandrocolantoni.mom.objectsQuery.javaObjectsQuery.JavaObjectsQuery;

public class JavaObjectsQueryImpl implements JavaObjectsQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * Elements of collection must have a field named as the parameter field.
     * Will be returned a collection with all values of the property field of all elements of collection.
     * NOTE that no distinct is applied.
     * if collection is null an empty collection will be returned
	 * @param collection
	 * @param field
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Override
    public <E,T> List<E> selectFieldFromCollection(Collection<T> collection, String field) throws Exception {
		List<E> result = new ArrayList<E>();
        
        PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        if (collection!=null) {
            Iterator<T> iterator= collection.iterator();
            while (iterator.hasNext()){
            	@SuppressWarnings("unchecked")
				E item = (E) propertyUtilsBean.getProperty(iterator.next(),field);
                result.add(item);
            }
        }
        
        return result;
    }

}
