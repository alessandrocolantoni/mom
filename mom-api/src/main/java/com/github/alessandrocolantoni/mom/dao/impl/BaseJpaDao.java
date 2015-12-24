package com.github.alessandrocolantoni.mom.dao.impl;



import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Cursor;
import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.Dao;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.query.LogicCondition;
import com.github.alessandrocolantoni.mom.query.Operator;
import com.github.alessandrocolantoni.mom.query.SimpleCondition;

public abstract class BaseJpaDao implements Dao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 297679211022307601L;
	

	
	protected abstract Logger getLogger();
	protected abstract EntityManager getEntityManager(); 
	
	protected final String ERROR = ":::::Error:::::";
	protected final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
	
	protected final static int ONE_TO_ONE = 0;
	protected final static int ONE_TO_N = 1;
    protected final static int M_TO_N = 2;
    protected final static int M_TO_ONE = 3; // added alessandro 19-03-2011
    protected final static int M_TO_N_INVERSE = 4; // added alessandro 09-08-2012
    protected final static int EMBEDDED_ID = 5; // added alessandro 10-08-2012
    
    protected Integer inLimit=1000;
    
    protected final String  joinPrefix ="bbb";
	
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
	
	
	public <E> E findObjectByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        
        E result=null;
        try{
            
        	Query query = createQuery(null,selectFields, realClass, logicCondition, orderBy);
        	result = getSingleResult(query);
        }catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        
        return result;
    }
	
	@Override
	public <E> List<E> findCollectionByTemplate(E entity, Integer firstResult, Integer maxResults, String orderingField) throws DataAccessException{
        
		List<E> result= null;
        
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
	
public <E> E findObjectByQueryString(String queryString) throws DataAccessException {
		
		E result;
		try {
			Query query = getEntityManager().createQuery(queryString);
			result = getSingleResult(query);
		
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
		return result;
	}
	
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
	
	
	
	
	
	public <E> E findObjectByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		E result;
		try {
			//queryString = translateInAll(queryString, parameters);
			Query query = getEntityManager().createQuery(queryString);

			setQueryParameters(query, parameters);
			result = getSingleResult(query);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	
	
	
	
	
	 
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
		List<E> result;
        try{
            
        	Query query = createQuery(realClass, logicCondition);
        	result = getResultList(query);

        }catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
        return result;
    }
	
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass,LogicCondition logicCondition,String orderingField, Boolean asc,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result = null;
        
        try {
			String orderBy = null;
			if(orderingField!=null && !orderingField.trim().equals("")){
				if(asc!=null){
					if(asc){
						orderBy=orderingField+" ASC";
					}else{
						orderBy=orderingField+" DESC";
					}
				}
			}
			result = findCollectionByLogicCondition(realClass, logicCondition, orderBy, firstResult, maxResults);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
		}
        return result;
    }
	
	
	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = createQuery(selectFields, realClass, logicCondition);
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E>  result;
        try{
            
        	Query query = createQuery(null,selectFields, realClass, logicCondition, orderBy);
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E> result;
        try{
            Query query = createQuery(null, null, realClass, logicCondition, orderBy);
        	result = getResultList(query);
        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = createQuery(distinct,selectFields, realClass, logicCondition, orderBy);
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,String[] groupBy, Integer firstResult, Integer maxResults) throws DataAccessException{
        List<E> result;
        try{
        	Query query = createQuery(distinct,selectFields, realClass, logicCondition, orderBy, groupBy);
            
        	if(firstResult!=null)
        		query.setFirstResult(firstResult.intValue());
        	if(maxResults!=null)
        		query.setMaxResults(maxResults.intValue());

        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result;
        try{
        	Query query = createQuery(distinct,selectFields, realClass, logicCondition, orderBy);
            
        	if(firstResult!=null)
        		query.setFirstResult(firstResult.intValue());
        	if(maxResults!=null)
        		query.setMaxResults(maxResults.intValue());

        	result =getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
        List<E> result;
        try{
        	Query query = createQuery(null,selectFields, realClass, logicCondition, orderBy);
            
        	if(firstResult!=null)
        		query.setFirstResult(firstResult.intValue());
        	if(maxResults!=null)
        		query.setMaxResults(maxResults.intValue());

        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	
	public <E> List<E> findCollectionByLogicCondition(Class<E> realClass, LogicCondition logicCondition, String orderBy,Integer firstResult, Integer maxResults) throws DataAccessException{
		List<E> result;
        try{
        	Query query = createQuery(null, null, realClass, logicCondition, orderBy);
            
        	if(firstResult!=null){
        		query.setFirstResult(firstResult.intValue());
        	}
        	if(maxResults!=null){
        		query.setMaxResults(maxResults.intValue());
        	}
        	
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }

	public <E> List<E> findCollectionByLogicCondition(Boolean distinct,Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException{
		List<E> result;
        try{
            
        	Query query = createQuery(distinct,null, realClass, logicCondition, orderBy);
        	result =getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = createQuery(distinct,null, realClass, logicCondition, null);
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	public <E> List<E> findCollectionByLogicCondition(Boolean distinct, String[]selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
        List<E> result;
        try{
            
        	Query query = createQuery(distinct,selectFields, realClass, logicCondition,null);
        	result = getResultList(query);

        } catch (Exception e) {
        	getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);    
        }
        return result;
    }
	
	//////////////////////////
	
	
	
	@Override
	public <E> List<E> findCollectionByQueryString(String queryString) throws DataAccessException {
		
		List<E> result;
		try {
			Query query = getEntityManager().createQuery(queryString);
			result = getResultList(query);
		
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
		return result;
	}
	
	public <E> List<E> findCollectionByQueryString(String queryString, String parameterName, Object parameterValue) throws DataAccessException {
		List<E> result;
		try {
			
			Map<String,Object> parameters = new HashMap<String,Object>();
			parameters.put(parameterName, parameterValue);
			result = findCollectionByQueryString(queryString, parameters);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	
	public <E> List<E> findCollectionByQueryString(String queryString,  Integer firstResult, Integer maxResults) throws DataAccessException {
		return findCollectionByQueryString( queryString,  new HashMap<String,Object>(),  firstResult,  maxResults);
	}
	
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters, Integer firstResult, Integer maxResults) throws DataAccessException {
		
		List<E> result;
		try {
			/*
			queryString = translateInAll(queryString, parameters);
			
			*/
			Query query = getEntityManager().createQuery(queryString);

			setQueryParameters(query, parameters);
			if (firstResult != null)
				query.setFirstResult(firstResult.intValue());
			if (maxResults != null)
				query.setMaxResults(maxResults.intValue());
			result = getResultList(query);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
        }
		return result;
	}
	public <E> List<E> findCollectionByQueryString(String queryString, Map<String,Object> parameters) throws DataAccessException {
		List<E> result;
		try {
			//queryString = translateInAll(queryString, parameters);
			Query query = getEntityManager().createQuery(queryString);

			setQueryParameters(query, parameters);
			result = getResultList(query);

		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		return result;
	}
	///////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////
	
	
	
	@SuppressWarnings("unchecked")
	private <E> List<E> getResultList(Query query) throws DataAccessException{
		try {
			
			return (List<E>)query.getResultList();
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	private <E> E getSingleResult(Query query) throws DataAccessException{
		try {
			
			return (E) query.getSingleResult();
			
		} catch (NoResultException e) {
			return  null;
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
	
	
	private <C> Query createQuery(Boolean distinct, String[] selectFields, Class<C> realClass,  LogicCondition logicCondition, String orderBy) throws Exception{
		
		return createQuery(distinct, selectFields, realClass,  logicCondition, orderBy,  null);
		
	}
	
	
	private <C> Query createQuery(Boolean distinct, String[] selectFields, Class<C> realClass,  LogicCondition logicCondition, String orderBy, String[] groupBy) throws Exception{
		
		Query query = null;
		Map<String,Object> parametersMap = new HashMap<String,Object>();
		String queryString = createQueryString ( distinct,  selectFields,  realClass,   logicCondition,  orderBy, groupBy,parametersMap);
		query = getEntityManager().createQuery(queryString);
		setQueryParameters(query, parametersMap);
		return query;
	}
	
	private <E> Query createQuery(Class<E> realClass, LogicCondition logicCondition) throws Exception{
		
		return createQuery(null, null, realClass,  logicCondition,null);
		
	}
	
	private <E> Query createQuery(String[] selectFields, Class<E> realClass,  LogicCondition logicCondition) throws Exception{
		return createQuery(null, selectFields, realClass,  logicCondition, null);
	}
	
	
	private <C> String createQueryString(Boolean distinct, String[] selectFields, Class<C> realClass,  LogicCondition logicCondition, String orderBy, String[] groupBy, Map<String,Object> parametersMap) throws  Exception{
		
		String queryString = null;
		Cursor joinIndex = new Cursor();
		HashMap<String,String> joinMap = createJoinMap(realClass, selectFields, joinIndex);
		getLogger().trace("joinIndex = "+joinIndex);
		joinMap.putAll(createJoinMap(realClass,logicCondition,joinIndex));
		getLogger().trace("joinIndex = "+joinIndex);
		joinMap.putAll(createJoinMap(realClass,groupBy,joinIndex));
		getLogger().trace("joinIndex = "+joinIndex);
		
		String[] orderByArray=buildOrderByArray(orderBy);
		joinMap.putAll(createJoinMap(realClass,orderByArray,joinIndex));
		getLogger().trace("joinIndex = "+joinIndex);
		
		
		
		replanceFieldsWithJoinMap( selectFields, joinMap);
		
		
		String selectString = createSelectString(distinct, selectFields);
		
		String fromString= createFromString(realClass, joinMap);
		

		queryString=selectString+" "+fromString+" ";
		
		replanceLogicConditionFieldsWithJoinMap(logicCondition, joinMap);
		
		queryString = queryString + createWhereString( realClass,   logicCondition,   parametersMap);

		replanceFieldsWithJoinMap( groupBy, joinMap);
		
		queryString = queryString + createGroupByString(groupBy);
		
		
		queryString = queryString + createOrderByString(orderByArray,joinMap);
		
		getLogger().trace("queryString is "+ queryString);
		
		return queryString;
	}
	
	
	private String createSelectString(Boolean distinct, String[] selectFields) throws Exception{
		return createSelectString( distinct,  selectFields,false); 
	}
	
	
	private <C> String createFromString(Class<C> realClass, Map<String,String> joinMap) throws Exception{
		
		String fromString=null;
		fromString=" FROM "+realClass.getSimpleName() +" a ";
		if(joinMap!=null && !joinMap.isEmpty()){
			Iterator<String> iterator = joinMap.keySet().iterator();
			while(iterator.hasNext()){
				String join=iterator.next();
				String joinAlias= joinMap.get(join);
				//fromString+=" JOIN "+join+" "+joinAlias+" ";
				fromString+=" JOIN "+prefixNotJoinFieldName(join)+" "+joinAlias+" ";
			}
		}
		return fromString;
	}
	
	
	
	private <C> HashMap<String,String> createJoinMap(Class<C> realClass,LogicCondition logicCondition, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		while(logicCondition!=null){
			SimpleCondition simpleCondition = logicCondition.getSimpleCondition();
			String fieldName= (String) simpleCondition.getParameter().iterator().next();
			joinMap.putAll(createJoinMap(realClass,fieldName, joinIndex));
			logicCondition = logicCondition.getLogicCondition();
		}
		
		return joinMap;
		
	}
	
	private <C> HashMap<String,String> createJoinMap(Class<C> realClass,String[] selectFields, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		if(selectFields!=null){
			for(String selectField:selectFields){
				joinMap.putAll(createJoinMap(realClass,selectField,joinIndex));
			}
		}
		
		return joinMap;
	}
	
	
	private <C> HashMap<String,String> createJoinMap(Class<C> realClass,String fieldName, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		if(fieldName!=null  && !isFunction(fieldName)){
			fieldName=fieldName.split(" ")[0];
			String[] firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(fieldName);
			String firstAttributeName=firstAttributeNameAndRemainingPath[0];
			String remainingPath=firstAttributeNameAndRemainingPath[1];
			
			StringBuffer currentPath=new   StringBuffer("");
			
			Class<?> currentClass=realClass;
			while (!firstAttributeName.trim().equals("")){
				
				if (isReferenceCollection(currentClass, firstAttributeName)){
					currentPath.append(firstAttributeName);
					String joinAlias =joinMap.get(currentPath.toString());
					if(joinAlias==null){
						joinAlias = joinPrefix+joinIndex.getValue();
						joinMap.put(currentPath.toString(), joinAlias);
						joinIndex.increment();
					}
					currentClass = Utils.getGenericClass(currentClass.getDeclaredField(firstAttributeName).getGenericType());
					
					
					currentPath=new   StringBuffer(joinAlias+".");
					
				}else{
					currentPath.append(firstAttributeName+".");
					currentClass=currentClass.getDeclaredField(firstAttributeName).getType();
				}
				firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(remainingPath);
				firstAttributeName=firstAttributeNameAndRemainingPath[0];
				remainingPath=firstAttributeNameAndRemainingPath[1];
			}
		}
			
		
		
		return joinMap;
	}
	
	private void replanceLogicConditionFieldsWithJoinMap(LogicCondition logicCondition, Map<String,String> joinMap) throws Exception{
		if(joinMap!=null){
			while(logicCondition!=null){
				SimpleCondition simpleCondition = logicCondition.getSimpleCondition();
				Collection<Object> replacedParameters = new ArrayList<Object>();
				Iterator<?> iterator= simpleCondition.getParameter().iterator();
				String fieldName= (String) iterator.next();
				fieldName=replanceFieldWithJoinMap(fieldName, joinMap);
				replacedParameters.add(fieldName);
				while(iterator.hasNext()){
					replacedParameters.add(iterator.next());
				}
				simpleCondition.setParameter(replacedParameters);
				logicCondition = logicCondition.getLogicCondition();
			}
		}
	}
	
	private <C> String createWhereString(Class<C> realClass,  LogicCondition logicCondition,  Map<String,Object> parametersMap) throws Exception{
		
		String whereString = "";
		if(logicCondition!=null){
			
			whereString = " WHERE "+ translateLogicCondition(realClass, logicCondition, parametersMap);
			
		}
		getLogger().trace("whereString is "+ whereString);
		
		return whereString;
		
		
	}
	
	private String createGroupByString(String[] groupBy) throws Exception{
		
		
		String groupByString = "";
		
		if(groupBy!=null && groupBy.length>0){
			groupByString = " GROUP BY ";
			for(int i=0; i<groupBy.length; i++){
				groupByString += prefixNotJoinFieldName(groupBy[i])+", ";
			}
			groupByString = groupByString.substring(0, groupByString.lastIndexOf(","));
		}
		
		return groupByString;
	}
	
	
	private String createOrderByString(String[] orderByArray, Map<String,String> joinMap) throws Exception{
		
		String orderByString = "";
		
		if(orderByArray!=null){
			String finalOrderBy="";
			for (int k=0; k<orderByArray.length;k++){
				String[] splitOrderBy=orderByArray[k].split(" ");
				//finalOrderBy=finalOrderBy+prefixNotJoinFieldName(replanceFieldWithJoinMap(splitOrderBy[0],joinMap))+" "+splitOrderBy[1];
				/**
				 * TODO verificar bien este cambio
				 */
				finalOrderBy=finalOrderBy+prefixNotJoinFieldName(replanceFieldWithJoinMap(splitOrderBy[0],joinMap))+" "+(splitOrderBy.length>1 ? splitOrderBy[1] : "");
				if(k<orderByArray.length-1)finalOrderBy=finalOrderBy+",";
			}
			orderByString = orderByString+" ORDER BY " +finalOrderBy;
		}
		
		return orderByString;
	}
	private String[] buildOrderByArray(String orderBy) throws Exception{
		String[] orderByArray=null;
		if(orderBy!=null && !orderBy.trim().equals("")){
			orderByArray= orderBy.split(",");
		}
		return orderByArray;
	}
	
	
	private void replanceFieldsWithJoinMap(String[] fields, Map<String,String> joinMap) throws Exception{
		if(fields!=null){
			for(int i=0;i<fields.length;i++){
				fields[i]=replanceFieldWithJoinMap(fields[i], joinMap);
			}
		}
	}
	
	private String replanceFieldWithJoinMap(String field, Map<String,String> joinMap) throws Exception{
		String replacedField=field;
		
		if(joinMap!=null && replacedField!=null){
			boolean fullReplaced=false;
			while(!fullReplaced){
			Iterator<String> iterator = joinMap.keySet().iterator();
				boolean replaced=false;
				while(iterator.hasNext() && !replaced){
					String join =iterator.next();
					if(replacedField.contains(join)){
						replacedField=replacedField.replace(join, joinMap.get(join));
						replaced=true;
					}
				}
				if(!replaced)fullReplaced=true;
			}
		}
		return replacedField;
	}
	
	private boolean isFunction(String fieldName) throws Exception{
		
		return fieldName!=null && fieldName.contains("("); 
		
	}
	
	
	private String createSelectString(Boolean distinct, String[] selectFields, boolean isCount) throws Exception{
		
		String selectString = "";
		//String selectString ="";
		if(selectFields!=null && selectFields.length>0){
			for(int i=0;i<selectFields.length;i++){
				//selectString=selectString+"a."+selectFields[i];
				selectString=selectString+prefixNotJoinFieldName(selectFields[i]);
				if(i<selectFields.length-1)selectString=selectString+",";
			}
		}else{
			selectString ="a";
		}
		
		if(distinct!=null && distinct.booleanValue()) {
			if(!isCount){
				selectString = "SELECT DISTINCT " + selectString+" ";
			}else{
				selectString = "SELECT COUNT (DISTINCT " + selectString+") ";
			}
		}else{
			if(!isCount){
				selectString = "SELECT " + selectString+" ";
			}else{
				selectString = "SELECT COUNT (" + selectString+") ";
			}
		}
		return selectString;
		
		
	}
	
	
	private String prefixNotJoinFieldName(String fieldName) throws Exception{
		String prefixedFieldName=fieldName;
		
		if(prefixedFieldName!=null && !prefixedFieldName.startsWith(joinPrefix) && !isFunction(prefixedFieldName)){
			prefixedFieldName=" a."+prefixedFieldName;
		}
		
		return prefixedFieldName;
		
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
	
	
	private <C> boolean isReferenceCollection(Class<C> pInstanceClass, String pAttributeName) throws DataAccessException{
		
		return  getAnnotation(pInstanceClass,pAttributeName, OneToMany.class)!=null || getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class)!=null;
		
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
	
	
	private <T> String translateLogicCondition(Class<T> realClass,LogicCondition logicCondition,Map<String,Object> parametersMap) throws Exception{
		return translateLogicCondition(0, realClass, logicCondition, parametersMap);
	}
	
	private <T> String translateLogicCondition(int progr, Class<T> realClass,LogicCondition logicCondition,Map<String,Object> parametersMap) throws Exception{
		
		String condition="";
		int newProgr = progr+1;
        condition = translateSimpleCondition(progr,realClass,logicCondition.getSimpleCondition(),parametersMap);
    	if (logicCondition.getLogicCondition()!=null){
            if (logicCondition.getAndOr().equalsIgnoreCase("AND")){
            	condition = condition +" AND (" +translateLogicCondition(newProgr,realClass,logicCondition.getLogicCondition(),parametersMap) +" )";
            }else if (logicCondition.getAndOr().equalsIgnoreCase("OR")){
            	condition = condition +" OR (" +translateLogicCondition(newProgr,realClass,logicCondition.getLogicCondition(),parametersMap) +" )";
            } else throw new Exception("Error ::: AND/OR missing");
        }
        
        return condition;
    }
	
	private <T> String translateSimpleCondition(int progr, Class<T> realClass,
												SimpleCondition simpleCondition, Map<String, Object> parametersMap) throws Exception {
		
		String condition = "";
		String field = null;
		Object value = null;
		Operator operator = simpleCondition.getOperator();
		Collection<Object> parameters = simpleCondition.getParameter();
		Iterator<Object> iterator = parameters.iterator();
		if (iterator.hasNext()) {
			field = (String) iterator.next();
		}
		if (iterator.hasNext()) {
			value = iterator.next();
		}
		String fieldLastToken = null;
		if (field != null) {
			fieldLastToken = Utils.getExceptLastTokenAndLastToken(field)[1];
			
			fieldLastToken = fieldLastToken.replaceAll("(|)", "a") + "A"
					+ progr;

		}

		/********************** operators mapping ********************************************/
		boolean staticCondition = false;
		if (operator.getName().equalsIgnoreCase("==")
				|| operator.getName().equalsIgnoreCase("=")) {
			
			condition = field + "= :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("isNull")) {
			
			condition = field + " IS NULL";
			// TODO add the OR empty string
		} else if (operator.getName().equalsIgnoreCase("isNotNull")) {
			
			condition = field + " IS NOT NULL";
		} else if (operator.getName().equalsIgnoreCase("IN")) { // added
																// 29/08/2005
			
			if (value == null || ((Collection<?>) value).isEmpty()) {
				condition = " 1 = 2"; // false condition
				staticCondition = true;
			} else if (((Collection<?>) value).size() > this.inLimit) {
				
				List<List<?>> splitIn =  (List<List<?>>) splitIn( (Collection<?>) value);
				
				condition = orOfInsCollection(prefixNotJoinFieldName(field),fieldLastToken, splitIn, parametersMap);
				staticCondition = true;
			} else {
				condition = field + " IN (:" + fieldLastToken + ")";
				
				parametersMap.put(fieldLastToken, value);
			}

		} else if (operator.getName().equalsIgnoreCase("NOT IN")|| operator.getName().equalsIgnoreCase("NOTIN")) {
			
			if (value == null || ((Collection<?>) value).isEmpty()) {
				condition = " 1 = 1"; // true condition
				staticCondition = true;
			} else if (((Collection<?>) value).size() > this.inLimit) {
				List<List<?>> splitIn = splitIn((Collection<?>) value);

				
				String nuevoField = ((field!=null && !field.trim().equals(""))?prefixNotJoinFieldName(field):"a ");
				condition = " NOT ("+ orOfInsCollection(nuevoField, fieldLastToken, splitIn, parametersMap) + " ) ";
				staticCondition = true;
			} else {
				condition = field + " NOT IN (:" + fieldLastToken + ")";
				
				parametersMap.put(fieldLastToken, value);
			}
		} else if (operator.getName().equalsIgnoreCase("<=")) {
			
			condition = field + "<= :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase(">=")) {
			
			condition = field + ">= :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("<")) {
			
			condition = field + "< :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase(">")) {
			
			condition = field + "> :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("!=")
				|| operator.getName().equalsIgnoreCase("<>")) {
			
			condition = field + "<> :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("LEFTLIKE")) {
			
			condition = "lower(a."+field + ") LIKE lower(concat('%',:" + fieldLastToken
					+ "))";
			staticCondition = true;
			parametersMap.put(field, value);
			
		} else if (operator.getName().equalsIgnoreCase("RIGHTLIKE")) {
			
			condition = "lower(a."+field + ") LIKE lower(concat(:" + fieldLastToken
					+ ",'%'))";
			staticCondition = true;
			
			parametersMap.put(fieldLastToken, value);
			
		} else if (operator.getName().equalsIgnoreCase("LIKE")
				|| operator.getName().equalsIgnoreCase("BOTHLIKE")) {
			
			condition = "lower(a."+field + ") LIKE lower(concat('%',:" + fieldLastToken
					+ ",'%'))";
			staticCondition = true;
			
			parametersMap.put(fieldLastToken, value);
			
		}
		
		if (!staticCondition) {
			if (field != null && !field.trim().equals("")) { 

				condition = prefixNotJoinFieldName(condition);
			} else {
				condition = "a" + condition;
			}
		}

		/********************** end operators mapping ********************************************/
		
		return condition;
	}
	
	

	
	/**
	 * Iste metodo devuelve una lista de listas, que son la particion de la lista data en ingreso por inLimit
	 * @param <T>
	 * @param <T>
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private <T> List<List<?>> splitIn(Collection<T> in) throws Exception{
		
		List<List<?>> limitedIns = new ArrayList<List<?>>();
		if(in !=null && ! in.isEmpty()){
			
			List<T> toSplitList= new ArrayList<T>();
			toSplitList.addAll(in);
			
			int size=toSplitList.size();
			
			// mod to know how much elements are inputted in last clause IN
			final int mod = (size % this.inLimit);
			// calculate how much INs are injected
			final int numberOfIn = (size / this.inLimit) + (mod == 0 ? 0 : 1);
				
			for (int i = 0; i < numberOfIn-1; i++) {
				List<T> limitedIn = toSplitList.subList(i*this.inLimit, (i+1)*this.inLimit);
				limitedIns.add(limitedIn);
			}
			List<T> limitedIn = toSplitList.subList((numberOfIn-1)*this.inLimit, size);
			limitedIns.add(limitedIn);
		}
		return limitedIns;
	}
	
	
	
	
	/**
	 * Este metodo quita del parameters el elemento con key parameter, y a�ade uno para cada lista en splitIn.
	 * Devuelve la string IN (:split_0_paramemeter) OR IN (:split_1_paramemeter)...OR IN (:split_N_paramemeter)
	 * @param <T>
	 * @param entityIdentifier
	 * @param parameter
	 * @param splitIn
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private  String orOfInsCollection(String entityIdentifier,String parameter, List<List<?>> splitIn, Map<String, Object> parameters) throws Exception{
		
		String orOfIns= "";
		if(splitIn!=null){
			parameters.remove(parameter);
			Iterator<List<?>> splitInIterator = splitIn.iterator();
			int count=0;
			if (splitInIterator.hasNext()){
				List<?> in = splitInIterator.next();
				String splitParameter= "split_"+count+"_"+parameter.toString();
				
				orOfIns = " "+entityIdentifier+" IN (:"+splitParameter+" ) ";
				parameters.put(splitParameter, in);
			}
			while (splitInIterator.hasNext()){
				count++;
				List<?> in = splitInIterator.next();
				String splitParameter= "split_"+count+"_"+parameter.toString();
				
				orOfIns = orOfIns +" OR "+entityIdentifier+" IN (:"+splitParameter+" ) ";
				parameters.put(splitParameter, in);
			}
		}
		
		return orOfIns;
		
	}
}
