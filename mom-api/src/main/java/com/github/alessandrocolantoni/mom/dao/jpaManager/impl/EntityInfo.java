package com.github.alessandrocolantoni.mom.dao.jpaManager.impl;



import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class  EntityInfo<E> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	private static Logger log = LoggerFactory.getLogger(EntityInfo.class.getName());


	

	private Class<E> entityClass;
	
	private String[] pkNames;
	
	private ArrayList<String> listPkNames;

	/**
	 * if idField has the @Id annotation 
	 */
	private boolean idAnnotated = false;
	
	/**
	 * if idField has the @EmbeddedId annotation 
	 */
	private boolean embeddedIdAnnotated = false;
	
	/**
	 * if entityClass has the @Embeddable annotation
	 */
	private boolean embeddable = false;
	
	
	private EmbeddedId embeddedId;
	
	private Id id;
	
	private Field idField = null;
	
	
	
	
	
	
	
	public EntityInfo(Class<E> entityClass) throws Exception {
		this.entityClass = entityClass;
		
		if(entityClass.isAnnotationPresent(Embeddable.class)){
			embeddable=true;
		}else{
			/******* start looking for EmbeddedId or Id annotations on fields*******/
			boolean  found = lookForEmbeddedIdOrIdAnnotationsOnFields();
			if(!found){
				/******* start looking for EmbeddedId or Id annotations on getters*******/
				found = lookForEmbeddedIdOrIdAnnotationsOnGetters();
				
				if(!found){
					throw new Exception("Error in in constructor EntityInfo(Class<E> entityClass): No EmbeddedId nor Id presente and not Embeddable class");
				}
			}
		}
	}

	
	
	/**
	 * Looks for  EmbeddedId or Id annotations on fields
	 * 
	 * @return true if it is found a field annotated with @Id or @EmbeddedId
	 * @throws Exception
	 */
	private boolean lookForEmbeddedIdOrIdAnnotationsOnFields() throws Exception{
		boolean found = false;
		Field[] fields = entityClass.getDeclaredFields();
		for(int i=0; i<fields.length; i++){
			
			if(fields[i].isAnnotationPresent(EmbeddedId.class)){
				embeddedId = fields[i].getAnnotation(EmbeddedId.class);
				embeddedIdAnnotated=true;
				idField=fields[i];
				found = true;
				break;
			}else if(fields[i].isAnnotationPresent(Id.class)){
				id = fields[i].getAnnotation(Id.class);
				idAnnotated=true;
				idField=fields[i];
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Looks for  EmbeddedId or Id annotations on getter methods
	 * @return true if it is found a getter method annotated with @Id or @EmbeddedId
	 * @throws Exception
	 */
	private boolean lookForEmbeddedIdOrIdAnnotationsOnGetters() throws Exception{
		boolean found = false;
		Method[] methods = entityClass.getMethods();
		
		for(int i=0; i<methods.length; i++){
			if( methods[i].isAnnotationPresent(EmbeddedId.class)){
				embeddedId = methods[i].getAnnotation(EmbeddedId.class);
				embeddedIdAnnotated=true;
				idField=entityClass.getDeclaredField(methods[i].getName().substring(3,3).toLowerCase()+methods[i].getName().substring(4));
				found = true;
				break;
			}else if( methods[i].isAnnotationPresent(Id.class)){
				id  = methods[i].getAnnotation(Id.class);
				idAnnotated=true;
				idField=entityClass.getDeclaredField(methods[i].getName().substring(3,3).toLowerCase()+methods[i].getName().substring(4));
				found = true;
				break;
			}
		}
		return found;
	}
	
	public boolean isEmbeddable() {
		return embeddable;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public ArrayList<String> getListPkNames() throws Exception {
		if(listPkNames==null){
			listPkNames = new ArrayList<String>();
			
			if(isEmbeddable()){
				Field[] fields = entityClass.getDeclaredFields();
				for (int i=0; i<fields.length;i++){
					if(!fields[i].getType().isAnnotationPresent(Transient.class) && !Modifier.isStatic(fields[i].getModifiers())){
						listPkNames.addAll(getFieldPkNames(fields[i]));
						 
					}
				}
			}else if(isIdAnnotated()||isEmbeddedIdAnnotated()){
				listPkNames.addAll(getFieldPkNames(idField));
			}
		}
		return listPkNames;
	}
	
	
	private List<String> getFieldPkNames(Field field) throws Exception{
		
		List<String> fieldPkNames = new ArrayList<String>();
		Class<? extends Object> fieldClass=field.getType();
		if(fieldClass.isAnnotationPresent(Entity.class) || fieldClass.isAnnotationPresent(Embeddable.class)){
			EntityInfo<? extends Object> idFieldEntityInfo = new EntityInfo<>(fieldClass);
			ArrayList<String> idFieldListPkNames =idFieldEntityInfo.getListPkNames();
			Iterator<String> idFieldListPkNamesIterator = idFieldListPkNames.iterator();
			while(idFieldListPkNamesIterator.hasNext()){
				fieldPkNames.add(field.getName()+"."+idFieldListPkNamesIterator.next());
			}
		}else{
			fieldPkNames.add(field.getName());
		}
		return fieldPkNames;
		
	}

	public String[] getPkNames() throws Exception {
		if(pkNames==null){
			pkNames=getListPkNames().toArray(new String[0]);
			
		}
		return pkNames;
	}

	public boolean isIdAnnotated() {
		return idAnnotated;
	}

	public boolean isEmbeddedIdAnnotated() {
		return embeddedIdAnnotated;
	}

	public EmbeddedId getEmbeddedId() {
		return embeddedId;
	}

	public Id getId() {
		return id;
	}

	public Field getIdField() {
		return idField;
	}

}
