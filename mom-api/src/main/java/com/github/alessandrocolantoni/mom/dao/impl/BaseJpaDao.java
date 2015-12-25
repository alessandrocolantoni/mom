package com.github.alessandrocolantoni.mom.dao.impl;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
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
import com.github.alessandrocolantoni.mom.query.LogicCondition;

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
			List<E> temp  = findCollectionByTemplate(entity, 0, 1, null,null);
			
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
	public <E> E findObjectByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        
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
	public <E> E findObjectByQueryString(String queryString) throws DataAccessException {
		
		E result;
		try {
			Query query = getEntityManager().createQuery(queryString);
			result = jpaManager.getSingleResult(query);
		
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
		return result;
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
			//queryString = translateInAll(queryString, parameters);
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
	public <E> List<E> findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults, String orderingField, Boolean asc) throws DataAccessException{
        
		
        try {
        	PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        	
        	
        	
			Map<String, Object> m = propertyUtilsBean.describe(entity);
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			Class<E> entityClass = jpaManager.getEntityClass(entity);
			
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
		    
		    queryString = buildOrderBy(queryString,  orderingField,  asc);
		    
		    
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
        
		return  findCollectionByTemplate(entity, null, null, null,null);
    }
	
	
	@Override
    public <E> List<E>  findCollectionByTemplate(E entity, String orderingField, Boolean asc) throws DataAccessException{
        
    	return  findCollectionByTemplate(entity, null, null, orderingField,asc);
    }
    
    
	@Override 
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
		List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(null, null, realClass,  logicCondition, null,  null);;
        	
        	result = jpaManager.getResultList(query);

        }catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        return result;
    }
	
	@Override
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass,LogicCondition logicCondition,String orderingField, Boolean asc,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result = null;
        
        try {
//			String orderBy = null;
//			if(orderingField!=null && !orderingField.trim().equals("")){
//				if(asc!=null){
//					if(asc){
//						orderBy=orderingField+" ASC";
//					}else{
//						orderBy=orderingField+" DESC";
//					}
//				}
//			}
//			result = findCollectionByLogicCondition(realClass, logicCondition, orderBy, firstResult, maxResults);
        	orderingField = buildAsc(orderingField, asc);
			result = findCollectionByLogicCondition(realClass, logicCondition, orderingField, firstResult, maxResults);
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
		}
        return result;
    }
	
	
	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(null, selectFields, realClass,  logicCondition, null,  null);
        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E>  result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(null, selectFields, realClass,  logicCondition, orderBy,  null);
        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E> result;
        try{
            Query query = logicConditionJqlBuilder.createQuery(null, null, realClass,  logicCondition, orderBy,  null);
            
        	result = jpaManager.getResultList(query);
        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(distinct, selectFields, realClass,  logicCondition, orderBy,  null);
        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	/**
	 * findCollectionByLogicCondition con todos los parametros: 
	 * @param distinct
	 * @param selectFields
	 * @param realClass
	 * @param logicCondition
	 * @param orderBy
	 * @param groupBy
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws DataAccessException
	 */
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,String[] groupBy, Integer firstResult, Integer maxResults) throws DataAccessException{
        List<E> result;
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
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result;
        try{
        	Query query = logicConditionJqlBuilder.createQuery(distinct, selectFields, realClass,  logicCondition, orderBy,  null);
        	
            jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
        	result =jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
        List<E> result;
        try{
        	Query query = logicConditionJqlBuilder.createQuery(null, selectFields, realClass,  logicCondition, orderBy,  null);
        	
            
        	jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);

        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result;
        try{
        	Query query = logicConditionJqlBuilder.createQuery(null, null, realClass,  logicCondition, orderBy,  null);
        	
            
        	jpaManager.setFirstAndMaxResults(query, firstResult, maxResults);
        	
        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(distinct, null, realClass,  logicCondition, orderBy,  null);
        	
        	result =jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(distinct, null, realClass,  logicCondition, null,  null);
        	
        	result = jpaManager.getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = logicConditionJqlBuilder.createQuery(distinct, selectFields, realClass,  logicCondition, null,  null);
        	
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
	
	public <E> List<E> findCollectionByQueryString(String queryString, Integer firstResult, Integer maxResults) throws DataAccessException {
		return findCollectionByQueryString( queryString,  new HashMap<String,Object>(),  firstResult,  maxResults);
	}
	
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		return  findCollectionByQueryString(queryString,  parameterName,  parameterValue, null, null);
	}
	
	
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		return findCollectionByQueryString( queryString,  parameters, null, null);
	}
	
	
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue, Integer firstResult, Integer maxResults) throws DataAccessException {
		List<E> result;
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
	
	
	
	
	
	private String buildAsc( String orderingField, Boolean asc)throws  Exception{
		if(orderingField!=null && !orderingField.trim().equals("")){
	    	if(asc!=null){
				
				if(asc){
					orderingField=orderingField+" ASC";
				}else{
					orderingField=orderingField+" DESC";
				}
				
			}
	    	
	    }
		return orderingField;
	}
	private String buildOrderBy(String queryString, String orderingField, Boolean asc) throws  Exception{
		
		if(orderingField!=null && !orderingField.trim().equals("")){
			orderingField = buildAsc(orderingField, asc);
			queryString += " ORDER BY "+ orderingField;
		}
		return queryString;
	}
	
}
