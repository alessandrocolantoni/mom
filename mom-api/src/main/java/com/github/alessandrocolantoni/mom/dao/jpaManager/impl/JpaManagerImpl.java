package com.github.alessandrocolantoni.mom.dao.jpaManager.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.dao.jpaManager.JpaManager;

public class JpaManagerImpl implements JpaManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	
	
	//private EntityManager entityManager;
	private transient Logger logger;
	
	
	private final String ERROR = ":::::Error:::::";
	private final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
//	private EntityManager getEntityManager() {
//		return entityManager;
//	}
//	public void setEntityManager(EntityManager entityManager) {
//		this.entityManager = entityManager;
//	}
	private Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> List<E> getResultList(Query query) throws DataAccessException{
		try {
			
			return (List<E>)query.getResultList();
			
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> E getSingleResult(Query query) throws DataAccessException{
		try {
			
			return (E) query.getSingleResult();
			
		} catch (NoResultException e) {
			return  null;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
	}

	@Override
	public void setQueryParameters(Query query, Map<String, Object> parameters) throws DataAccessException{
	       
		try {
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
	
	@Override
	public void setFirstAndMaxResults(Query query,  Integer firstResult, Integer maxResults) throws DataAccessException{
		try {
			if(firstResult!=null){
				query.setFirstResult(firstResult.intValue());
			}
			if(maxResults!=null){
				query.setMaxResults(maxResults.intValue());
			}
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
	}
	
	
	@Override
	public <E> boolean isReferenceCollection(Class<E> pInstanceClass, String pAttributeName) throws DataAccessException{
		
		try {
			return  Utils.getAnnotation(pInstanceClass,pAttributeName, OneToMany.class)!=null || Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class)!=null;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
		
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public <E> Class<E> getEntityClass(E entity) throws DataAccessException {   
		
		
		try {
			if(entity==null){
				throw new Exception("Error: entity is null" );
			}
			
			Class<E> entityClass=   (Class<E>) entity.getClass();
			
			if (entity instanceof HibernateProxy) {   
				
				entityClass = ((HibernateProxy)entity).getHibernateLazyInitializer().getPersistentClass();
				
			} 
			
			
			return entityClass;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
	}
	
	
}
