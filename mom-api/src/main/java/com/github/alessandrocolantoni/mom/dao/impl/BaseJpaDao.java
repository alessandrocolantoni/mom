package com.github.alessandrocolantoni.mom.dao.impl;







import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.Dao;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.dao.jpaManager.JpaManager;
import com.github.alessandrocolantoni.mom.dao.logicConditionJqlBuilder.LogicConditionJqlBuilder;
import com.github.alessandrocolantoni.mom.objectsQuery.javaObjectsQuery.JavaObjectsQuery;
import com.github.alessandrocolantoni.mom.query.LogicCondition;
import com.github.alessandrocolantoni.mom.query.LogicSqlCondition;

@Dependent
public class BaseJpaDao implements Dao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 297679211022307601L;
	

	@Inject 
	private LogicConditionJqlBuilder logicConditionJqlBuilder;
	
	@Inject 
	private JpaManager jpaManager;
	
	@Inject
	private JavaObjectsQuery javaObjectsQuery;
	
//	protected abstract Logger getLogger();
//	protected abstract EntityManager getEntityManager(); 
	private EntityManager entityManager;
	private transient Logger logger;
	
	
	
	
	private EntityManager getEntityManager() {
		return entityManager;
	}
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	private Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	private final String ERROR = ":::::Error:::::";
	private final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
	
	private final static int ONE_TO_ONE = 0;
	private final static int ONE_TO_N = 1;
	private final static int M_TO_N = 2;
	private final static int M_TO_ONE = 3;  
	private final static int M_TO_N_INVERSE = 4;  
	private final static int EMBEDDED_ID = 5; 
    
    
    
   
	
	@Override
	public <E> E findByPrimaryKey(Class<E> realClass, Object pkValue) throws DataAccessException{
        
        E result = null;
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
			List<E> temp  = findCollectionByTemplate(entity, 0, 1, null);
			
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
	public <E> Object findObjectByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        
        E result= null;
        try{
            

        	result = findObjectByLogicCondition(null, realClass,  logicCondition, null);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        
        return result;
    }
	
	@Override
	public <E,T> E findObjectByLogicCondition(String[]selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        
        E result=null;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(null, selectFields, realClass,  logicCondition, orderBy,  null);
        	
        	result = jpaManager.getSingleResult(query);
        }catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        
        return result;
    }
	
	@Override
	public <E,T> E findObjectByLogicCondition(String[]selectFields, Class<T> realClass, LogicCondition logicCondition) throws DataAccessException{
        
        E result=null;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(null, selectFields, realClass,  logicCondition, null,  null);
        	
        	result = jpaManager.getSingleResult(query);
        }catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        
        return result;
    }
	
	@Override
	public <E> E findObjectByQueryString(String queryString) throws DataAccessException {
		return findObjectByQueryString(queryString, new HashMap<String,Object>());
	}
	
	@Override
	public <E> E findObjectByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		E result;
		try {
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(parameterName, parameterValue);
			result = findObjectByQueryString(queryString, parameters);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	
	@Override
	public <E> E findObjectByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		try {
			Query query = getEntityManager().createQuery(queryString);
			jpaManager.setQueryParameters(query, parameters);
			E result = jpaManager.getSingleResult(query);
			return result;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
	}
	
	
	@Override
	public <E> E findObjectByNativeQueryString(String queryString) throws DataAccessException {
		return findObjectByNativeQueryString(queryString, new HashMap<String,Object>());
	}
	
	@Override
	public <E> E findObjectByNativeQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		E result;
		try {
			
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(parameterName, parameterValue);
			result = findObjectByNativeQueryString(queryString, parameters);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	
	@Override
	public <E> E findObjectByNativeQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		try {
			Query query = getEntityManager().createNativeQuery(queryString);
			jpaManager.setQueryParameters(query, parameters);
			E result = jpaManager.getSingleResult(query);
			return result;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
	}
	
	
	
	@Override
	public <E> List<E> findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults, String orderBy) throws DataAccessException{
        
		
        try {
        	PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        	
        	

			Map<String, Object> m = propertyUtilsBean.describe(entity);
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			Class<?> entityClass = jpaManager.getEntityClass(entity);
			
			String queryString = "SELECT c FROM " + jpaManager.getEntityClass(entity).getSimpleName() + " c ";
			String whereCondition ="";
			int i=0;
		    for (Object key : m.keySet()) { 
		        
		    	//if (!key.equals("class") && Utils.getAnnotation(getEntityClass(entity), key.toString(), Transient.class)==null) {
		    	if (!key.equals("class") && Utils.getAnnotation(entityClass, key.toString(), Transient.class)==null) {
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
		    
		    queryString = addOrderBy(queryString,  orderBy);
		    
		    
		    Query query = getEntityManager().createQuery(queryString);
			
			jpaManager.setQueryParameters(query, parameters);
			jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
			
			return jpaManager.getResultList(query);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
       
       
    }
	
	@Override
	public <E> List<E> findCollectionByTemplate(E entity) throws DataAccessException{
        
		return  findCollectionByTemplate(entity, null, null, null);
    }
	
	
	@Override
    public <E> List<E>  findCollectionByTemplate(E entity, String orderBy) throws DataAccessException{
        
    	return  findCollectionByTemplate(entity, null, null, orderBy);
    }
	
	@Override
    public <E> List<E>  findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults) throws DataAccessException{
        
    	return  findCollectionByTemplate(entity, firstResult, maxResults, null);
    }
   
	
	@Override
	public  <E> List<E> findCollectionByNullFields(Class<E> realClass, String[] nullFields) throws DataAccessException{
         try {
			String queryString = "SELECT c FROM " + realClass.getSimpleName() + " c "; 
			String whereCondition="";
			if(nullFields!=null){
				for (int i = 0; i< nullFields.length; i++){
					whereCondition += " c." + nullFields[i] + " IS  NULL AND"; 
				}
			}
			if(!whereCondition.equals("")){
				whereCondition = whereCondition.substring(0, whereCondition.lastIndexOf("AND"));
				queryString += " WHERE "+whereCondition;
			}	
			Query query = getEntityManager().createQuery(queryString);
			return jpaManager.getResultList(query);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);		
        }

    }
	
	
    
	@Override 
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        return findCollectionByLogicCondition(null, null, realClass, logicCondition, null, null, null, null);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(String[]selectFields, Class<T> realClass, LogicCondition logicCondition) throws DataAccessException{
        return findCollectionByLogicCondition(null, selectFields, realClass, logicCondition, null, null, null, null);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(String[]selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        return findCollectionByLogicCondition(null, selectFields, realClass, logicCondition, orderBy, null, null, null);
    }
	
	@Override
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        return findCollectionByLogicCondition(null, null, realClass, logicCondition, orderBy, null, null, null);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        return findCollectionByLogicCondition(distinct, selectFields, realClass, logicCondition, orderBy, null, null, null);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
        return findCollectionByLogicCondition(distinct, selectFields, realClass, logicCondition, orderBy, null, firstResult, maxResults);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(String[] selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
        return findCollectionByLogicCondition(null, selectFields, realClass, logicCondition, orderBy, null, firstResult, maxResults);
    }
	
	@Override
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
        return findCollectionByLogicCondition(null, null, realClass, logicCondition, orderBy, null, firstResult, maxResults);
    }

	@Override
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        return findCollectionByLogicCondition(distinct, null, realClass, logicCondition, orderBy, null, null, null);
    }
	
	@Override
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
       return findCollectionByLogicCondition(distinct, null,  realClass,  logicCondition, null, null, null, null);
    }
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<T> realClass, LogicCondition logicCondition) throws DataAccessException{
        return findCollectionByLogicCondition(distinct, selectFields,  realClass,  logicCondition, null, null, null, null);
    }
	
	
	@Override
	public <E,T> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<T> realClass, LogicCondition logicCondition, String orderBy,String[] groupBy, Integer firstResult, Integer maxResults) throws DataAccessException{
        List<E> result = new ArrayList<E>();
        try{
        	Query query = logicConditionJqlBuilder.createQuery(distinct,selectFields, realClass, logicCondition, orderBy, groupBy);
        	jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
         	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	//////////////////////////
	
	
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString) throws DataAccessException {
		return findCollectionByQueryString( queryString,  new HashMap<String,Object>(),  null,  null);
	}
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString, Integer firstResult, Integer maxResults) throws DataAccessException {
		return findCollectionByQueryString( queryString,  new HashMap<String,Object>(),  firstResult,  maxResults);
	}
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		return  findCollectionByQueryString(queryString,  parameterName,  parameterValue, null, null);
	}
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		return findCollectionByQueryString( queryString,  parameters, null, null);
	}
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue, Integer firstResult, Integer maxResults) throws DataAccessException {
		List<E> result = new ArrayList<E>();
		try {
			
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(parameterName, parameterValue);
			result = findCollectionByQueryString(queryString, parameters, firstResult, maxResults);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters, Integer firstResult, Integer maxResults) throws DataAccessException {
		
		List<E> result;
		try {
			
			Query query = getEntityManager().createQuery(queryString);

			jpaManager.setQueryParameters(query, parameters);
			jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
			result = jpaManager.getResultList(query);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
		return result;
	}
	
	////////////////////////////////
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString) throws DataAccessException {
		return findCollectionByNativeQueryString( queryString,  new HashMap<String,Object>(),  null,  null);
	}
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Integer firstResult, Integer maxResults) throws DataAccessException {
		return findCollectionByNativeQueryString( queryString,  new HashMap<String,Object>(),  firstResult,  maxResults);
	}
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		return  findCollectionByNativeQueryString(queryString, parameterName, parameterValue, null, null);
	}
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		return findCollectionByNativeQueryString(queryString, parameters, null, null);
	}
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString, String parameterName, Object parameterValue, Integer firstResult, Integer maxResults) throws DataAccessException {
		List<E> result;
		try {
			
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(parameterName, parameterValue);
			result = findCollectionByNativeQueryString(queryString, parameters,firstResult,maxResults);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);		}
		return result;
	}
	
	@Override
	public <E> List<E> findCollectionByNativeQueryString(String queryString, Map<String,Object> parameters,Integer firstResult, Integer maxResults) throws DataAccessException {
		
		List<E> result;
		try {
			Query query = getEntityManager().createNativeQuery(queryString);

			jpaManager.setQueryParameters(query, parameters);
			jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
			result = jpaManager.getResultList(query);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);		
		}
		return result;
	}
	
	
	
	private String addOrderBy(String queryString, String orderingString) throws  Exception{
		
		if(orderingString!=null && !orderingString.trim().equals("")){
			queryString += " ORDER BY "+ orderingString;
		}
		return queryString;
	}
	
	@Override
	public <E,T> List<E> findCollectionByOrValues(Class<E> realClass,String pAttributeName,List<T> valuesCollection) throws DataAccessException{
		List<E> result = new ArrayList<E>();
        
    	try {
			if(valuesCollection != null && !valuesCollection.isEmpty()){
				Map<String,Object> parameters = new HashMap<String,Object>();
				String queryString = "SELECT c FROM " + realClass.getSimpleName() + " c  WHERE "; 
				
				int i=0;
				Iterator<T> iterator = valuesCollection.iterator();
			    while (iterator.hasNext()){
			        
			       
			        T value = iterator.next();
			        if (value!=null){
			        	String parameter ="parameter"+i; 
			            queryString += " c." + pAttributeName + " = :" + parameter + " OR"; 
			        	parameters.put(parameter,value);
			        	i++;
			        }else{
			        	 queryString += " c." + pAttributeName + "IS NULL OR";
			        }
			    }
			    queryString = queryString.substring(0, queryString.lastIndexOf("OR")); 
			    
			    Query query = getEntityManager().createQuery(queryString);
				
			    jpaManager.setQueryParameters(query, parameters);
			    result = jpaManager.getResultList(query);
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);	
        }
        return result;
    }
	
	@Override
	public <E,T> List<E> findCollectionByFieldInCollection(Class<E> realClass,String pAttributeName, List<T> valuesCollection) throws DataAccessException{
		
		List<E> result = new ArrayList<E>();
		try {
			if(valuesCollection!=null && !valuesCollection.isEmpty()){
				Map<String,Object> parameters = new HashMap<String,Object>();
				String queryString = "SELECT c FROM " + realClass.getSimpleName() + " c "; 
				queryString += " WHERE c." +pAttributeName+" IN (:parameter)";
				parameters.put("parameter",valuesCollection);
				Query query = getEntityManager().createQuery(queryString);
				jpaManager.setQueryParameters(query, parameters);
				result = jpaManager.getResultList(query);
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        return result;
    }
	
	@Override
	public <E> List<E> searchValueInFields(Class<E> realClass, String[] pAttributeNames, Object value) throws DataAccessException{
		List<E> result = new ArrayList<E>();
        try {
			if (pAttributeNames!=null && pAttributeNames.length>0  && value!=null) {
				
				Map<String,Object> parameters = new HashMap<String,Object>();
				parameters.put("parameter", value);
				
				String queryString = "SELECT c FROM " + realClass.getSimpleName() + " c  WHERE "; 

				
			    for (int i=0; i<pAttributeNames.length;i++ ){
			    	queryString += " c." + pAttributeNames[i] + " LIKE concat('%',:parameter,'%') OR ";
			    }
			    
			    queryString = queryString.substring(0, queryString.lastIndexOf("OR"));
				Query query = getEntityManager().createQuery(queryString);
				jpaManager.setQueryParameters(query, parameters);
				result = jpaManager.getResultList(query);
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
         }
        return result;
    }
	
	@Override
	public <E> List<E> getCollectionOfStoredItemsNotInBean(Object pInstance, String pAttributeName) throws DataAccessException{
		List<E> result = new ArrayList<E>();
        try {
        	PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        	
        	@SuppressWarnings("unchecked")
			Collection<E> pAttributeCollection = (Collection<E>) propertyUtilsBean.getProperty(pInstance, pAttributeName);
        	
        	
    		Class<?> pInstanceClass =  jpaManager.getEntityClass(pInstance);
        	
        	LogicSqlCondition logicSqlCondition = new LogicSqlCondition(pAttributeName,"NOT IN",pAttributeCollection); 
        	
        	result = findCollectionByLogicCondition(new String[]{pAttributeName}, pInstanceClass, logicSqlCondition);
 
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        return result;
	}
	
	
	@Override
	public <E> List<E>  getCollectionOfStoredItemsInBean(Object pInstance, String pAttributeName) throws DataAccessException{
		List<E> result = new ArrayList<E>();
        try {
        	PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        	
        	@SuppressWarnings("unchecked")
			Collection<E> pAttributeCollection = (Collection<E>) propertyUtilsBean.getProperty(pInstance, pAttributeName);
        	
        	
    		Class<?> pInstanceClass =  jpaManager.getEntityClass(pInstance);
        	
        	LogicSqlCondition logicSqlCondition = new LogicSqlCondition(pAttributeName,"IN",pAttributeCollection); 
        	
        	result = findCollectionByLogicCondition(new String[]{pAttributeName}, pInstanceClass, logicSqlCondition);
        	
        	//result=getCollectionOfStoredItemsInOrNotInBean( pInstance,  pAttributeName, false);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
        return result;
	 }
	
	
	@Override
	public  <E> List<E>  getStoredCollection(Object pInstance, String pAttributeName) throws DataAccessException{
		List<E> result = new ArrayList<E>();
        
        try {
        	String queryString = getStoredSingleObjectOrCollectionQueryString(pInstance, pAttributeName);
			
			result = findCollectionByQueryString(queryString, "param", pInstance);
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
        
        return result;
    }
	
	@Override
	public  <E> E  getStoredSingleObject(Object pInstance, String pAttributeName) throws DataAccessException{
		E result = null;
        
        try {
        	String queryString = getStoredSingleObjectOrCollectionQueryString(pInstance, pAttributeName);
			
			result = findObjectByQueryString(queryString, "param", pInstance);
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
        
        return result;
    }
	
	private String getStoredSingleObjectOrCollectionQueryString(Object pInstance, String pAttributeName) throws Exception{
		if(pAttributeName==null || pAttributeName.trim().equals("")){
		    getLogger().error(ERROR);
			throw new Exception("pAttributeName can't be null or empty or blank chacarcters string");
		}
		
		Class<?> pInstanceClass = jpaManager.getEntityClass(pInstance);
		
		String queryString ="SELECT bbb FROM "+pInstanceClass.getSimpleName()+" a join a."+pAttributeName+" bbb  WHERE a= :param " ;
		
		return queryString;
	}
	
	@Override
	public Object refresh(Object refreshVO) throws DataAccessException {
		try {

			if(refreshVO!=null)getEntityManager().refresh(refreshVO);

		} catch (EntityNotFoundException e) {
			refreshVO=null;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return refreshVO;
	}
	
	
	@Override
	public void refreshReference(Object pInstance, String pAttributeName) throws DataAccessException{
        
        try {
        	Class<?> pInstanceClass= jpaManager.getEntityClass(pInstance);
			
			PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
			
			Object retrievedReference;
			
			if(Utils.getAnnotation(pInstanceClass,pAttributeName, OneToOne.class)!=null || Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToOne.class)!=null){
				
				retrievedReference = getStoredSingleObject(pInstance, pAttributeName);
				
			}else  if(Utils.getAnnotation(pInstanceClass,pAttributeName, OneToMany.class)!=null || Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class)!=null){
				
				retrievedReference = getStoredCollection(pInstance, pAttributeName);
				
			}else{
				throw new DataAccessException("no annotation OneToOne, ManyToOne, OneToMany, ManyToMany found on "+pInstanceClass.getName()+"."+pAttributeName );
			}
			
			propertyUtilsBean.setProperty(pInstance, pAttributeName,retrievedReference);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	@Override
	public void refreshAllReferences(Object pInstance) throws DataAccessException{
        
        try {
			PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
			
			Class<?> pInstanceClass=jpaManager.getEntityClass(pInstance);
			
			
			Map<String, Object> describe = propertyUtilsBean.describe(pInstance);
			Iterator<String> iterator = describe.keySet().iterator();
			while (iterator.hasNext()){
				String pAttributeName = iterator.next();
				
				
				if(	Utils.getAnnotation(pInstanceClass,pAttributeName, OneToMany.class)!=null ||
						Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class)!=null ||
								Utils.getAnnotation(pInstanceClass,pAttributeName, OneToOne.class)!=null || 
										Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToOne.class)!=null){
					
					refreshReference(pInstance, pAttributeName);
				}
				
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	@Override
	public void refreshAllReferencesInCollection(Collection<? extends Object> valueObjectsCollection) throws DataAccessException{
         try {
			if(valueObjectsCollection!=null){
				Iterator<? extends Object> iterator = valueObjectsCollection.iterator();
				while(iterator.hasNext()) {
					refreshAllReferences(iterator.next());
				}
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	@Override
	public <E> void retrieveReferenceInCollection(Collection<E> valueObjectsCollection, String pAttributeName) throws DataAccessException{
        try {
			if(valueObjectsCollection!=null){
				Iterator<E> iterator = valueObjectsCollection.iterator();
				while(iterator.hasNext()) {
					refreshReference(iterator.next(),pAttributeName);
				}
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	/**
	 * 
	 * @param pInstance
	 * @param pAttributeName
	 * @throws Exception An Exception doesn't do rollback
	 */
	@Override
	public void retrieveUninitializedReference(Object pInstance, String pAttributeName) throws DataAccessException{
		try {
			if (!jpaManager.inInitialized(pInstance,pAttributeName)){
			    refreshReference(pInstance,pAttributeName);
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	/**
	 * 
	 * @param pInstance
	 * @throws Exception An Exception doesn't do rollback
	 */
	@Override
	public void retrieveAllUninitializedReferences(Object pInstance) throws DataAccessException{
		try {
			PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
			Class<?> pInstanceClass=jpaManager.getEntityClass(pInstance);

			Map<String, Object> describe = propertyUtilsBean.describe(pInstance);
			Iterator<String> iterator = describe.keySet().iterator();
			while (iterator.hasNext()){
				String pAttributeName = (String) iterator.next();
				if(	Utils.getAnnotation(pInstanceClass,pAttributeName, OneToMany.class)!=null ||
						Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class)!=null ||
								Utils.getAnnotation(pInstanceClass,pAttributeName, OneToOne.class)!=null || 
										Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToOne.class)!=null){
					
					retrieveUninitializedReference(pInstance, pAttributeName);
				}
				
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
    }
	
	@Override
	public void retrievePathReference(Object valueobjectOrCollection, String path) throws DataAccessException{
        try {
			if (path!=null && !path.trim().equals("") && valueobjectOrCollection!=null){
			    if (Collection.class.isInstance(valueobjectOrCollection)){ 
			        @SuppressWarnings("unchecked")
					Iterator<? extends Object> iterator = ((Collection<? extends Object>) valueobjectOrCollection).iterator();
			        while (iterator.hasNext()){
			            Object valueobjectOrCollectionItem = iterator.next();
			            retrievePathReferenceOnBean(valueobjectOrCollectionItem,  path);
			        }
			    } else {
			    	Object valueobjectOrCollectionItem = valueobjectOrCollection;
			    	retrievePathReferenceOnBean(valueobjectOrCollectionItem,  path);
			    }
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
    }
	
	
	private void retrievePathReferenceOnBean(Object pInstance, String path) throws Exception{
        
		if (path!=null && !path.trim().equals("") && pInstance!=null){
		    PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
		    String[] firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(path);
		    String firstAttributeName=firstAttributeNameAndRemainingPath[0];
		    String remainingPath=firstAttributeNameAndRemainingPath[1];
		    	
	    	Class<?> pInstanceClass = jpaManager.getEntityClass(pInstance);
	    	if(Utils.getAnnotation(pInstanceClass, firstAttributeName, EmbeddedId.class)==null){
            	refreshReference(pInstance, firstAttributeName);
            }
	    	Object firstAttributeValue = propertyUtilsBean.getProperty(pInstance,firstAttributeName);
	    	retrievePathReference(firstAttributeValue,remainingPath);
		}
    }
	
	@Override
	public void retrieveUninitializedPathReference(Object valueobjectOrCollection, String path) throws DataAccessException{
        
		try {
			if (path!=null && !path.trim().equals("") && valueobjectOrCollection!=null){
			    if (Collection.class.isInstance(valueobjectOrCollection)){ 
			        @SuppressWarnings("unchecked")
					Iterator<? extends Object> iterator = ((Collection<? extends Object>) valueobjectOrCollection).iterator();
			        while (iterator.hasNext()){
			            Object valueobjectOrCollectionItem = iterator.next();
			            retrieveUninitializedPathReferenceOnBean(valueobjectOrCollectionItem,  path);
			        }
			    } else {
			    	Object valueobjectOrCollectionItem = valueobjectOrCollection;
			    	retrieveUninitializedPathReferenceOnBean(valueobjectOrCollectionItem,  path);
			    }
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }

    }
	
	
	private void retrieveUninitializedPathReferenceOnBean(Object pInstance, String path) throws Exception{
        
		if (path!=null && !path.trim().equals("") && pInstance!=null){
		    PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
		    String[] firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(path);
		    String firstAttributeName=firstAttributeNameAndRemainingPath[0];
		    String remainingPath=firstAttributeNameAndRemainingPath[1];
		    	
	    	Class<?> pInstanceClass = jpaManager.getEntityClass(pInstance);
	    	if(Utils.getAnnotation(pInstanceClass, firstAttributeName, EmbeddedId.class)==null){
            	retrieveUninitializedReference(pInstance, firstAttributeName);
            }
	    	Object firstAttributeValue = propertyUtilsBean.getProperty(pInstance,firstAttributeName);
	    	retrieveUninitializedPathReference(firstAttributeValue,remainingPath);
		}
    }
	
}
