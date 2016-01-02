package com.github.alessandrocolantoni.mom.dao.jpaManager.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.dao.RelationTypeMaps;
import com.github.alessandrocolantoni.mom.dao.jpaManager.JpaManager;

public class JpaManagerImpl implements JpaManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	
	
	private EntityManager entityManager;
	private transient Logger logger;
	
	
	private final String ERROR = ":::::Error:::::";
	private final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
	
	
	
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
	public Class<?> getEntityClass(Object entity) throws DataAccessException {   
		
		
		try {
			if(entity==null){
				throw new Exception("Error: entity is null" );
			}
			
			Class<?> entityClass=   entity.getClass();
			
			if (entity instanceof HibernateProxy) {   
				
				entityClass = ((HibernateProxy)entity).getHibernateLazyInitializer().getPersistentClass();
				
			} 
			
			
			return entityClass;
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param realClass
	 * @param path
	 * @return
	 * @throws Exception this method doesn't have to be responsible to do a rollback
	 */
	@Override
	public <E> Class<?> getClassFromPath(Class<E> realClass, String path) throws Exception{
		
		Class<?> classFromPath;
		Class<?> nextClassOnPath;
		if (path==null || path.trim().equals("")){
			classFromPath = realClass;
		}else{
			String[] firstAttributeNameAndRemainingPath =Utils.getFirstAttributeNameAndRemainingPath(path);
			String firstAttributeName = firstAttributeNameAndRemainingPath[0];
			String remainingPath = firstAttributeNameAndRemainingPath[1];
			Field field= realClass.getDeclaredField(firstAttributeName);
			
			if(Collection.class.isAssignableFrom(field.getType()) || List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())){
        		
				nextClassOnPath = Utils.getGenericClass(field.getGenericType());
        		if(nextClassOnPath==null){
        			getLogger().error(ERROR);
                    throw new Exception("Bags types attributes have to be generic types. class: "+realClass.getName()+" field: "+field.getName());
        		}else{
        			getLogger().trace(" class:"+realClass.getName()+" field: "+field.getName());
        		}
        	}else{
        		/**
        		 *  oneToOne or ManyToOne
        		 */
        		nextClassOnPath=field.getType();
        	}
			
			classFromPath =  getClassFromPath(nextClassOnPath, remainingPath);
		}
        return classFromPath;
    }
	
	@Override
	public boolean isInitialized (Object pInstance, String pAttributeName ) throws Exception{
		PersistenceUnitUtil unitUtil = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
		
		return unitUtil.isLoaded(pInstance, pAttributeName);
		
	}
	
	public <E> int getRelationType(Class<E> realClass, String pAttributeName) throws Exception{
		int relationType;
		
		ManyToMany manyToMany = (ManyToMany) Utils.getAnnotation(realClass, pAttributeName, ManyToMany.class);
		OneToMany oneToMany = null;
		OneToOne oneToOne = null;
		ManyToOne manyToOne = null ;
		EmbeddedId embeddedId = null;
		
		
		if(manyToMany==null){
			oneToMany = (OneToMany) Utils.getAnnotation(realClass, pAttributeName, OneToMany.class);   
			if(oneToMany==null){
				manyToOne = (ManyToOne) Utils.getAnnotation(realClass, pAttributeName, ManyToOne.class);
				if(manyToOne == null){
					oneToOne = (OneToOne) Utils.getAnnotation(realClass, pAttributeName, OneToOne.class);
					if(oneToOne == null){
						embeddedId = (EmbeddedId)Utils.getAnnotation(realClass, pAttributeName, EmbeddedId.class);
						if(embeddedId==null){
							throw new Exception("Error: pAttributeName  is no ManyToOne, no OneToMnay, no OneToOne, no ManyToMany, no EmbeddedId " );
						}else{
							relationType=EMBEDDED_ID;
						}
					}else{
						relationType=ONE_TO_ONE;
					}
				}else{
					relationType=M_TO_ONE;
				}
			}else{
				relationType=ONE_TO_N;
			}
		}else{
			if(StringUtils.isEmpty(manyToMany.mappedBy())){
				//relationType = M_TO_N;
				relationType = M_TO_N_INVERSE;
			}else{
				//relationType = M_TO_N_INVERSE;
				relationType = M_TO_N;
			}
		}
		return relationType;
	}
	
	@Override
	public String getInverseManyToManyField(Object pInstance, String pAttributeName) throws Exception{
    	String inverseManyToManyField=null;
    	Class<?> pInstanceClass=getEntityClass(pInstance);
		ManyToMany manyToMany = (ManyToMany) Utils.getAnnotation(pInstanceClass,pAttributeName, ManyToMany.class);
		if(manyToMany!=null){
			if(!StringUtils.isEmpty(manyToMany.mappedBy())){
				inverseManyToManyField = manyToMany.mappedBy();
			}else{
				Class<?> targetEntityClass = manyToMany.targetEntity();
				if(targetEntityClass==null){
					targetEntityClass = Utils.getGenericClass(pInstanceClass.getDeclaredField(pAttributeName).getGenericType());
				}
				Field[] declaredFields = targetEntityClass.getDeclaredFields();
				if(declaredFields!=null){
					for(int i=0;i<declaredFields.length && inverseManyToManyField ==null;i++){
						
						ManyToMany inverseManyToMany =declaredFields[i].getAnnotation(ManyToMany.class);
						if(inverseManyToMany!=null){
							Class<?> inverseManyToManyClass =  Utils.getGenericClass(declaredFields[i].getGenericType());
							if(pInstanceClass.equals(inverseManyToManyClass)){
								inverseManyToManyField =declaredFields[i].getName();
							}
							
						}
					}
				}
			}
		}
    	return inverseManyToManyField;
    }
	
	@Override
	public RelationTypeMaps buildRelationTypeMaps(Object parent, Collection<String> paths, boolean  pathsHasToBeSorted) throws Exception{
		RelationTypeMaps relationTypeMaps =  new RelationTypeMaps();
		
		String[] pathsArray = new String[paths.size()];
        if(pathsHasToBeSorted){
            List<String> pathsList = Collections.list(Collections.enumeration(paths));
            Collections.sort(pathsList);
            pathsArray = pathsList.toArray(pathsArray);
        }  else{
            pathsArray = paths.toArray(pathsArray);
        }
        int index = 0;

        /**
         *  map for oneToOne and MToOne
         */
        Map<String,List<String>> directReferenceMap = relationTypeMaps.getDirectReferenceMap(); 
        
        Map<String,List<String>> embeddedIdMap = relationTypeMaps.getEmbeddedIdMap(); 
        
        Map<String,List<String>> oneToManyReferenceMap = relationTypeMaps.getOneToManyReferenceMap();

        Map<String,List<String>> manyToManyReferenceMap = relationTypeMaps.getManyToManyReferenceMap();
        
        Map<String,List<String>> manyToManyInverseReferenceMap = relationTypeMaps.getManyToManyInverseReferenceMap();
      

        Class<?> parentClass = getEntityClass(parent);



        while (index<pathsArray.length){
            String path = pathsArray[index];
            if(path!=null && !path.trim().equals("")){
                String[] firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(path);
                String firstAttributeName=firstAttributeNameAndRemainingPath[0];
                ArrayList<String> subPaths = new ArrayList<String> ();

                
                int relationType = getRelationType(parentClass, firstAttributeName) ;


                if(!firstAttributeNameAndRemainingPath[1].trim().equals("")){
                	subPaths.add(firstAttributeNameAndRemainingPath[1]);
                }
                
                
                index++;
                boolean matchingFirstAttributeName = true;
                while (index < pathsArray.length  && matchingFirstAttributeName){
                    String path2 = pathsArray[index];
                    if(path2!=null && !path2.trim().equals("")){
                        String[] firstAttributeNameAndRemainingPath2 = Utils.getFirstAttributeNameAndRemainingPath(path2);
                        String firstAttributeName2=firstAttributeNameAndRemainingPath2[0];
                        if(firstAttributeName.equals(firstAttributeName2)){
                            if(!firstAttributeNameAndRemainingPath2[1].trim().equals("")) subPaths.add(firstAttributeNameAndRemainingPath2[1]);
                            index++;
                        }else{
                            matchingFirstAttributeName=false;
                        }
                    } else{
                        index++;
                    }
                }

               
                switch (relationType){
                    case ONE_TO_ONE : 			directReferenceMap.put(firstAttributeName,subPaths); break;
                    case M_TO_ONE : 			directReferenceMap.put(firstAttributeName,subPaths); break;
                    case EMBEDDED_ID : 			embeddedIdMap.put(firstAttributeName,subPaths); break;
                    case ONE_TO_N: 				oneToManyReferenceMap.put(firstAttributeName,subPaths);break;
                    case M_TO_N:  				manyToManyReferenceMap.put(firstAttributeName,subPaths);break;
                    case M_TO_N_INVERSE:  		manyToManyInverseReferenceMap.put(firstAttributeName,subPaths);break;
                }

            } else {   
                index++;
            }
        }
		return relationTypeMaps;
	}
	
}
