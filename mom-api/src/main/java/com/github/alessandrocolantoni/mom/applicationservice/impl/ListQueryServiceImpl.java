package com.github.alessandrocolantoni.mom.applicationservice.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.github.alessandrocolantoni.mom.applicationservice.ListQueryService;

public class ListQueryServiceImpl implements ListQueryService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 720774440078839268L;
	
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
