package com.github.alessandrocolantoni.mom.applicationservice.impl;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.github.alessandrocolantoni.mom.applicationservice.ListQueryService;
import com.github.alessandrocolantoni.mom.common.FieldComparator;

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
	
	@Override
	public <T> List<T> selectDistinct(Collection<T> collection, String[] properties) throws Exception{
        List<T> result = new ArrayList<T>();
		if (collection!=null){
        	TreeSet<T> treeSet = new TreeSet<T>(new FieldComparator<T>(properties));
            Iterator<T> iterator =  collection.iterator();
            while(iterator.hasNext()){
            	treeSet.add(iterator.next());
            }
            result.addAll(treeSet);
        }
        return (List<T>) result;
    }
	
	@Override
	public <T> void sortCollection(List<T> in_collection, String in_property) throws Exception {
		Collections.sort(in_collection, new FieldComparator<T>(in_property));
    }

	@Override
	public <T> void sortCollection(List<T> in_collection, String[] in_properties) throws Exception {
		Collections.sort(in_collection, new FieldComparator<T>(in_properties));
    }
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> selectWhereFieldsNotEqualsTo(Collection<T> collection, String[] properties, Object[] values) throws Exception{
        List<T> result = new ArrayList<T>();
        
        if(collection!=null) {
            PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
            Iterator<T> iterator =  collection.iterator();
            
            while(iterator.hasNext()){
                T collectionElement = iterator.next();
                boolean found = true;
                for (int i=0; i<properties.length && found; i++){
                    
                	Comparable collectionElementField;
                	try {
						collectionElementField = ((Comparable)propertyUtilsBean.getProperty(collectionElement,properties[i]));
					} catch (NestedNullException e) {
						collectionElementField=null;
					}
                    
                	if ((collectionElementField==null && values[i]==null)) {
                        found = false;
                    }
                    if(collectionElementField!=null && values[i]!=null && collectionElementField.compareTo(values[i])==0){
                        found = false;
                    }
                }
                if (found)result.add(collectionElement);
            }
        }
        
        return result;
    }
	
	@Override
	public <T> List<T> selectWhereFieldNotEqualsTo(Collection<T> collection, String property, Object value) throws Exception{
        return selectWhereFieldsNotEqualsTo(collection, new String[]{property}, new Object[]{value});
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> selectWhereFieldsEqualsTo(Collection<T> collection, String[] properties, Object[] values) throws Exception{
		List<T> result = new ArrayList<T>();
        
		if(collection!=null) {
            PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
            Iterator<T> iterator =  collection.iterator();
            
            while(iterator.hasNext()){
                
                T collectionElement  = iterator.next();
                boolean found = true;
                for (int i=0; i<properties.length && found; i++){
                	Comparable collectionElementField;
                	try{
                		collectionElementField = ((Comparable)propertyUtilsBean.getProperty(collectionElement,properties[i]));
                	} catch (NestedNullException e) {
						collectionElementField=null;
					}
                	if ((collectionElementField!=null && values[i]==null)||(collectionElementField==null && values[i]!=null)) {
                        found = false;
                    }
                    if(collectionElementField!=null && values[i]!=null && collectionElementField.compareTo(values[i])!=0){
                        found = false;
                    }
                }
                if (found)result.add(collectionElement);
            }
        }
        return result;
	}
	
	@Override
	public <T> List<T> selectWhereFieldEqualsTo(Collection<T> collection, String property, Object value) throws Exception{
		return selectWhereFieldsEqualsTo(collection, new String[]{property}, new Object[]{value});
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> selectWhereFieldsGreaterThan(Collection<T> collection, String[] properties, Object[] values) throws Exception{
		List<T> result = new ArrayList<T>();
		if(collection!=null){
            PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
            Iterator<T> iterator =  collection.iterator();
            
            while(iterator.hasNext()){
                T collectionElement = iterator.next();
                boolean found = true;
                for (int i=0; i<properties.length && found; i++){
                	Comparable collectionElementField;
                    
                	
                	try {
						collectionElementField = ((Comparable)propertyUtilsBean.getProperty(collectionElement,properties[i]));
					} catch (NestedNullException e) {
						collectionElementField=null;
					}
                    
                	
                	if(collectionElementField!=null && values[i]!=null && collectionElementField.compareTo(values[i])<=0){
                        found = false;
                    }
                }
                if (found)result.add(collectionElement);
            }
        }
        return result;
    }
	
	@Override
	public <T> List<T> selectWhereFieldGreaterThan(Collection<T> collection, String property, Object value) throws Exception{
		return selectWhereFieldsGreaterThan(collection, new String[]{property}, new Object[]{value});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> List<T> selectWhereFieldIn(Collection<T> collection, String property, Object[] values) throws Exception{
		List<T> result = new ArrayList<T>();
       
        try {
            if(collection!=null && values != null){
            
	            PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
	            Iterator<T> iterator =  collection.iterator();
	
	            while(iterator.hasNext()){
	            	T collectionElement = iterator.next();
	                
	                Comparable collectionElementField;
	                try {
	                	collectionElementField = ((Comparable)propertyUtilsBean.getProperty(collectionElement,property));
	                } catch (NestedNullException e) {
						collectionElementField=null;
					}
	                boolean found = false;
	                for (int i=0; i<values.length && ! found; i++){
	                    if((collectionElementField==null && values[i]==null )||(collectionElementField!=null && values[i]!=null && collectionElementField.compareTo(values[i])==0)){
	                        found = true;
	                    }
	                }
	                if (found)result.add(collectionElement);
	            }
            }
        } catch(Exception e) {
            throw new Exception("Error in Utils.selectWhereFieldIn(Collection collection, String property, Object[] values) ",e);
        }
        return result;
    }
}
