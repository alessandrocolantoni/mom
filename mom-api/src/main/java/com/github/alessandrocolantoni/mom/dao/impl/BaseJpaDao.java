package com.github.alessandrocolantoni.mom.dao.impl;





import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.Dao;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;

public abstract class BaseJpaDao implements Dao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 297679211022307601L;
	

	
	protected abstract Logger getLogger();
	protected abstract EntityManager getEntityManager(); 
	
	protected final String ERROR = ":::::Error:::::";
	protected final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
	
	
	@Override
	public <T> T findByPrimaryKey(Class<T> realClass, Object pkValue) throws DataAccessException{
        
        T result = null;
        try{
            
            try {
				result =  getEntityManager().find(realClass, pkValue);
			} catch (EntityNotFoundException e) {
				result=null;
			}
            
            
        }catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        } 
        return result;
    }
	
	@Override
	public <E> E findObjectByTemplate(E entity) throws DataAccessException{
		
		E result= null;
		
		try {
			Collection<E> temp  = findCollectionByTemplate(entity, 0, 1, null);
			
			if (temp!=null && ! temp.isEmpty()) {
				result = temp.iterator().next();
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
        
        return result;
	}

	@Override
	public <E> Collection<E> findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults, String orderingField) throws DataAccessException{
        
        Collection<E> result= null;
        
        PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        
        try {
			Map<String, Object> m = propertyUtilsBean.describe(entity);
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			String queryString = "SELECT c FROM " + getEntityClass(entity).getSimpleName() + " c ";
			String whereCondition ="";
			if(m!=null){
				int i=0;
			    for (Object key : m.keySet()) { 
			        
			    	if (!key.equals("class") && getAnnotation(getEntityClass(entity), key.toString(), Transient.class)==null) {
			            Object value = m.get(key); 
 
			            if (value != null) { 
			                String lastToken = Utils.getExceptLastTokenAndLastToken((String)key)[1];
			                    
			                whereCondition += " c." + key + " = :" + lastToken+i + " AND"; 
			            	parameters.put(lastToken+i,value);
			            	i++;
			            } 
			        } 
			    } 
			    if(!whereCondition.trim().equals("")){
			    	whereCondition = whereCondition.substring(0, whereCondition.lastIndexOf("AND")); 
			    	queryString +=" WHERE "+whereCondition;
			    }
			    if(orderingField!=null && !orderingField.trim().equals("")){
			    	queryString += " ORDER BY "+ orderingField;
			    }
			    
			    Query query = getEntityManager().createQuery(queryString);
				
				setQueryParameters(query, parameters);
				if(firstResult!=null)query.setFirstResult(firstResult.intValue());
				if(maxResults!=null)query.setMaxResults(maxResults.intValue());
				
				
				
				result = getResultList(query);
			    
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
        
        return result;
    }
	
	@Override
	public <E> Collection<E> findCollectionByQueryString(String queryString) throws DataAccessException {
		
		Collection<E> result;
		try {
			//queryString = translateIn(queryString);// added alessandro
													
			Query query = getEntityManager().createQuery(queryString);
			result = getResultList(query);
		
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
		return result;
	}
	
	
	@SuppressWarnings({ "unchecked" })
	private <E> Collection<E> getResultList(Query query) throws DataAccessException{
		try {
			
			return query.getResultList();
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
	}
	
	
	@SuppressWarnings({ "unchecked" })
	private Class<? extends Object> getEntityClass(Object entity) throws Exception {   
		
		
		if(entity==null){
			throw new Exception("Error: entity is null" );
		}
		
		Class<? extends Object> entityClass=   entity.getClass();
		
		if (entity instanceof HibernateProxy) {   
			
			entityClass = ((HibernateProxy)entity).getHibernateLazyInitializer().getPersistentClass();
			
		} 
		
		
		return entityClass;
	}
	
	
	private void setQueryParameters(Query query, Map<String, Object> parameters) throws DataAccessException{
       
        try{
        	
        	Iterator<String> iterator = parameters.keySet().iterator();
        	while(iterator.hasNext()){
        		Object key = iterator.next();
        		
        		
        		if(Integer.class.isInstance(key)){
        			query.setParameter(((Integer)key).intValue(), parameters.get(key));
        		}else{
        			query.setParameter((String) key, parameters.get(key));
        		}
        	}
        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        
    }
	
	
	private <C> Method getGetter(Class<C> realClass, String pAttributeName) throws DataAccessException{
		Method getter=null;
		try {
			if(realClass==null || pAttributeName==null || pAttributeName.trim().equals("")){
				throw new DataAccessException("Error ::: realClass is null or pAttributeName is null or empty string " );
			}
		
			getter = realClass.getDeclaredMethod("get"+pAttributeName.substring(0,1).toUpperCase()+pAttributeName.substring(1));
			
		}catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);		}
		return getter;
	}
	
	private <T extends Annotation, C> T  getAnnotation(Class<C> realClass, String pAttributeName, Class<T> annotationClass) throws DataAccessException{
		
		T annotation =null;
		
		try {
			
			Field field=null;
			try {
				field = realClass.getDeclaredField(pAttributeName);
			} catch (NoSuchFieldException e) {
				getLogger().warn(pAttributeName + "is not a field of "+realClass.toString());
			}
			
			
			if(field!=null){
				annotation = field.getAnnotation(annotationClass);
				if(annotation ==null){
					Method getter = getGetter(realClass,pAttributeName);
					annotation = getter.getAnnotation(annotationClass);
				}
			}
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);		
        }
		
        return annotation;
	}
	
	
}
