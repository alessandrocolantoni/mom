package com.github.alessandrocolantoni.mom.dao.logicConditionJqlBuilder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;

import com.github.alessandrocolantoni.mom.common.Cursor;
import com.github.alessandrocolantoni.mom.common.Utils;
import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.dao.jpaManager.JpaManager;
import com.github.alessandrocolantoni.mom.dao.logicConditionJqlBuilder.LogicConditionJqlBuilder;
import com.github.alessandrocolantoni.mom.query.LogicCondition;
import com.github.alessandrocolantoni.mom.query.Operator;
import com.github.alessandrocolantoni.mom.query.SimpleCondition;

@Dependent
public  class LogicConditionJqlBuilderImpl implements LogicConditionJqlBuilder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private JpaManager jpaManager;
	
	private EntityManager entityManager;
	
	private transient Logger logger;
	
	
	private final String ERROR = ":::::Error:::::";
	private final String DATACCESSEXCEPTION = ":::DataAccessException:::";
	
	
	private final String  joinPrefix ="bbb";
	
	
	
	
	
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
	
	@Deprecated
	@Override
	public <E> Query createQuery(Boolean distinct, String[] selectFields, Class<E> realClass,  LogicCondition logicCondition, String orderBy) throws DataAccessException{
		
		return createQuery(distinct, selectFields, realClass,  logicCondition, orderBy,  null);
		
	}
	
	
	@Deprecated
	@Override
	public <E> Query createQuery(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException{
		
		return createQuery(null, null, realClass,  logicCondition,null);
		
	}
	@Deprecated
	@Override
	public <E> Query createQuery(String[] selectFields, Class<E> realClass,  LogicCondition logicCondition) throws DataAccessException{
		return createQuery(null, selectFields, realClass,  logicCondition, null);
	}
	
	@Override
	public <E> Query createQuery(Boolean distinct, String[] selectFields, Class<E> realClass,  LogicCondition logicCondition, String orderBy, String[] groupBy) throws DataAccessException{
		
		Query query = null;
		try {
			Map<String,Object> parametersMap = new HashMap<String,Object>();
			String queryString = createQueryString ( distinct,  selectFields,  realClass,   logicCondition,  orderBy, groupBy,parametersMap);
			query = getEntityManager().createQuery(queryString);
			jpaManager.setQueryParameters(query, parametersMap);
		} catch (Exception e) {
			getLogger().error(ERROR);
            throw new DataAccessException(DATACCESSEXCEPTION ,e);	
		}
		return query;
	}


	
	
	private <E> String createQueryString(Boolean distinct, String[] selectFields, Class<E> realClass,  LogicCondition logicCondition, String orderBy, String[] groupBy, Map<String,Object> parametersMap) throws  Exception{
		
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
	
	private <E> HashMap<String,String> createJoinMap(Class<E> realClass,LogicCondition logicCondition, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		while(logicCondition!=null){
			SimpleCondition simpleCondition = logicCondition.getSimpleCondition();
			String fieldName= (String) simpleCondition.getParameter().iterator().next();
			joinMap.putAll(createJoinMap(realClass,fieldName, joinIndex));
			logicCondition = logicCondition.getLogicCondition();
		}
		
		return joinMap;
		
	}
	
	private <E> HashMap<String,String> createJoinMap(Class<E> realClass,String[] selectFields, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		if(selectFields!=null){
			for(String selectField:selectFields){
				joinMap.putAll(createJoinMap(realClass,selectField,joinIndex));
			}
		}
		
		return joinMap;
	}
	
	
	private <E> HashMap<String,String> createJoinMap(Class<E> realClass,String fieldName, Cursor joinIndex) throws Exception{
		HashMap<String,String> joinMap = new HashMap<String, String>();
		
		if(fieldName!=null  && !isFunction(fieldName)){
			fieldName=fieldName.split(" ")[0];
			String[] firstAttributeNameAndRemainingPath = Utils.getFirstAttributeNameAndRemainingPath(fieldName);
			String firstAttributeName=firstAttributeNameAndRemainingPath[0];
			String remainingPath=firstAttributeNameAndRemainingPath[1];
			
			StringBuffer currentPath=new   StringBuffer("");
			
			Class<?> currentClass=realClass;
			while (!firstAttributeName.trim().equals("")){
				
				if (jpaManager.isReferenceCollection(currentClass, firstAttributeName)){
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
	
	private boolean isFunction(String fieldName) throws Exception{
		
		return fieldName!=null && fieldName.contains("("); 
		
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
	
	private String prefixNotJoinFieldName(String fieldName) throws Exception{
		String prefixedFieldName=fieldName;
		
		if(prefixedFieldName!=null && !prefixedFieldName.startsWith(joinPrefix) && !isFunction(prefixedFieldName)){
			prefixedFieldName=" a."+prefixedFieldName;
		}
		
		return prefixedFieldName;
		
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
	
	private <E> String translateLogicCondition(Class<E> realClass,LogicCondition logicCondition,Map<String,Object> parametersMap) throws Exception{
		return translateLogicCondition(0, realClass, logicCondition, parametersMap);
	}
	
	private <E> String translateLogicCondition(int progr, Class<E> realClass,LogicCondition logicCondition,Map<String,Object> parametersMap) throws Exception{
		
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
	
	
	private <E> String translateSimpleCondition(int progr, Class<E> realClass, SimpleCondition simpleCondition, Map<String, Object> parametersMap) throws Exception {

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
		
			fieldLastToken = fieldLastToken.replaceAll("(|)", "a") + "A" + progr;
		
		}

		/********************** operators mapping ********************************************/
		boolean staticCondition = false;
		if (operator.getName().equalsIgnoreCase("==") || operator.getName().equalsIgnoreCase("=")) {
		
			condition = field + "= :" + fieldLastToken;
			
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("isNull")) {
		
			condition = field + " IS NULL";
			// TODO add the OR empty string
		} else if (operator.getName().equalsIgnoreCase("isNotNull")) {
		
			condition = field + " IS NOT NULL";
		} else if (operator.getName().equalsIgnoreCase("IN")) { // added // 29/08/2005
		
			if (value == null || ((Collection<?>) value).isEmpty()) {
				condition = " 1 = 2"; // false condition
				staticCondition = true;
			} else {
				condition = field + " IN (:" + fieldLastToken + ")";
			
				parametersMap.put(fieldLastToken, value);
			}
		
		} else if (operator.getName().equalsIgnoreCase("NOT IN")|| operator.getName().equalsIgnoreCase("NOTIN")) {
		
			if (value == null || ((Collection<?>) value).isEmpty()) {
				condition = " 1 = 1"; // true condition
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
		} else if (operator.getName().equalsIgnoreCase("!=") || operator.getName().equalsIgnoreCase("<>")) {
		
			condition = field + "<> :" + fieldLastToken;
		
			parametersMap.put(fieldLastToken, value);
		} else if (operator.getName().equalsIgnoreCase("LEFTLIKE")) {
		
			condition = "lower(a."+field + ") LIKE lower(concat('%',:" + fieldLastToken + "))";
			staticCondition = true;
			parametersMap.put(field, value);
		
		} else if (operator.getName().equalsIgnoreCase("RIGHTLIKE")) {
		
			condition = "lower(a."+field + ") LIKE lower(concat(:" + fieldLastToken + ",'%'))";
			staticCondition = true;
			
			parametersMap.put(fieldLastToken, value);
		
		} else if (operator.getName().equalsIgnoreCase("LIKE") || operator.getName().equalsIgnoreCase("BOTHLIKE")) {
		
			condition = "lower(a."+field + ") LIKE lower(concat('%',:" + fieldLastToken + ",'%'))";
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
	


	
	
	
	
	
	
	
	
}
