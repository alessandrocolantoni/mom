package com.github.alessandrocolantoni.mom.query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;



public class LogicSqlCondition extends LogicCondition{

    public LogicSqlCondition() {
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute {@link #simpleCondition},  with the input parameter <code>simpleCondition</code>.<br/>
     * With this constructor the new intance of LogicSqlCondition will be actually as a {@link SimpleCondition}.<br/>
     * Of course using the setter methods it can be transformed in a more complex logic condition.
     * <br/>
     * @param simpleCondition instance of {@link SimpleCondition} used to set the attribute {@link #simpleCondition}
     */
    public LogicSqlCondition(SimpleCondition simpleCondition) {
        this.simpleCondition=simpleCondition;
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute <code>simpleCondition</code>, with the
     * new instance of {@link SimpleCondition} that will be created using the input parameter <code>parameter</code> and <code>operator</code>,
     * see {@link SimpleCondition#SimpleCondition(Collection parameter,String operator)}.<br/>
     * Resuming this constructor creates a new instance of {@link Operator} setting {@link Operator#name} with the input string <code>operator</code>,
     * (see {@link Operator#Operator(String name)}), will then create a new instance  of {@link SimpleCondition} setting {@link SimpleCondition#operator} with the
     * newly created instance of {@link Operator} and {@link SimpleCondition#parameter} with the input collection <code>parameter</code>; at the end set {@link #simpleCondition} with
     * the  created instance  of {@link SimpleCondition}.<br/>
     * <br/>
     * With this constructor the new instance  of LogicSqlCondition will be actually as a {@link SimpleCondition}.<br/>
     *
     * @param parameter parameters of the instance of {@link SimpleCondition} {@link #simpleCondition}
     * @param operator name of the {@link Operator} of the instance of {@link SimpleCondition} {@link #simpleCondition}
     */
    public LogicSqlCondition(Collection<Object> parameter,String operator) {
        this.simpleCondition= new SimpleCondition(parameter,operator);
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute {@link #simpleCondition}, with the
     * new instance of {@link SimpleCondition} that will be created using the input parameter <code>field</code>, <code>operator</code> and <code>value</code>,
     * using the {@link SimpleCondition} constructor {@link SimpleCondition#SimpleCondition(String field,String operator, Object value)}<br/>
     * Resuming this constructor creates a new instance of {@link Operator} setting {@link Operator#name} with the input string <code>operator</code>,
     * (see {@link Operator#Operator(String name)}), then will create a collection with two elements, the first one will be the input string <code>field</code> and
     * the second one will be the input object <code>value</code>; then will create  a new instance  of {@link SimpleCondition} setting {@link SimpleCondition#operator} with the
     * newly created instance of {@link Operator} and {@link SimpleCondition#parameter} with the created collection; at the end set {@link #simpleCondition} with
     * the  created instance  of {@link SimpleCondition}.<br/>
     * <br/>
     * With this constructor the new instance of LogicSqlCondition will be actually as a {@link SimpleCondition}.<br/>
     *
     * @param field first element of the collection the will be used as {@link SimpleCondition#parameter} of the attribute {@link #simpleCondition}<br/>
     * @param operator name of the {@link Operator} of the {@link SimpleCondition}
     * @param value second element of the collection the will be used as {@link SimpleCondition#parameter} of the attribute {@link #simpleCondition}<br/>
     */
    public LogicSqlCondition(String field,String operator, Object value) {
        this.simpleCondition= new SimpleCondition(field,operator,value);
    }

    public LogicSqlCondition(String field,String operator) {
        this.simpleCondition= new SimpleCondition(field,operator);
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute {@link #simpleCondition},  with the input parameter <code>simpleCondition</code>, and
     * the nested attribute {@link #logicCondition} with the input parameter <code>logicCondition</code>.<br/>
     * The attribute {@link #andOr} is kept unspecified, and must be set with the setter method {@link #setAndOr(String andOr)} to make the created instance
     * express properly a logic condition.<br/>
     * <br/>
     * @param simpleCondition instance of {@link SimpleCondition} used to set the attribute {@link #simpleCondition}
     * @param logicCondition  instance of {@link LogicCondition} used to set the nested attribute {@link #logicCondition}
     */
    public LogicSqlCondition(SimpleCondition simpleCondition,LogicCondition logicCondition) {
        this.simpleCondition=simpleCondition;
        this.logicCondition=logicCondition;
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute {@link #simpleCondition},  with the input parameter <code>simpleCondition</code>,
     * the attribute {@link #andOr} with the input string <code>andOr</code>,  and
     * the nested attribute {@link #logicCondition} with the input parameter <code>logicCondition</code>.<br/>
     * The expressed logic condition is : <br/>
     * <code>simpleCondition andOr (logicCondition)</code>.<br/>
     * 
     *
     * @param simpleCondition simpleCondition instance of {@link SimpleCondition} used to set the attribute {@link #simpleCondition}
     * @param andOr string that should assume just the values "AND" or "OR" to express if the {@link #simpleCondition} is in AND or in OR with the {@link #logicCondition}
     * @param logicCondition instance of {@link LogicCondition} used to set the nested attribute {@link #logicCondition}
     */
    public LogicSqlCondition(SimpleCondition simpleCondition,String andOr,LogicCondition logicCondition) {
        this.simpleCondition=simpleCondition;
        this.logicCondition=logicCondition;
        this.andOr=andOr;
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute <code>simpleCondition</code>, with the
     * new instance of {@link SimpleCondition} that will be created using the input parameter <code>parameter</code> and <code>operator</code>,
     * see {@link SimpleCondition#SimpleCondition(Collection parameter,String operator)}.<br/>
     * Resuming this constructor creates a new instance of {@link Operator} setting {@link Operator#name} with the input string <code>operator</code>,
     * (see {@link Operator#Operator(String name)}), will then create a new instance  of {@link SimpleCondition} setting {@link SimpleCondition#operator} with the
     * newly created instance of {@link Operator} and {@link SimpleCondition#parameter} with the input collection <code>parameter</code>; then set {@link #simpleCondition} with
     * the  created instance  of {@link SimpleCondition}.<br/>
     * Then will set the attribute {@link #andOr} with the input string <code>andOr</code>,  and
     * the nested attribute {@link #logicCondition} with the input parameter <code>logicCondition</code>.<br/>
     * The expressed logic condition is : <br/>
     * <code>simpleCondition andOr (logicCondition)</code>.<br/>
     * <br/>
     *
     * @param parameter parameters of the instance of {@link SimpleCondition} {@link #simpleCondition}
     * @param operator name of the {@link Operator} of the instance of {@link SimpleCondition} {@link #simpleCondition}
     * @param andOr string that should assume just the values "AND" or "OR" to express if the {@link #simpleCondition} is in AND or in OR with the {@link #logicCondition}
     * @param logicCondition instance of {@link LogicCondition} used to set the nested attribute {@link #logicCondition}
     */
    public LogicSqlCondition(Collection<Object> parameter,String operator,String andOr,LogicCondition logicCondition) {
        this.simpleCondition= new SimpleCondition(parameter,operator);
        this.logicCondition=logicCondition;
        this.andOr=andOr;
    }

    /**
     * Create a new instance of LogicSqlCondition setting the attribute {@link #simpleCondition}, with the
     * new instance of {@link SimpleCondition} that will be created using the input parameters <code>field</code>, <code>operator</code> and <code>value</code>,
     * using the {@link SimpleCondition} constructor {@link SimpleCondition#SimpleCondition(String field,String operator, Object value)}<br/>
     * In other words will be  created a new instance of {@link Operator} setting {@link Operator#name} with the input string <code>operator</code>,
     * (see {@link Operator#Operator(String name)}), then will create a collection with two elements, the first one will be the input string <code>field</code> and
     * the second one will be the input object <code>value</code>; then will create  a new instance  of {@link SimpleCondition} setting {@link SimpleCondition#operator} with the
     * newly created instance of {@link Operator} and {@link SimpleCondition#parameter} with the created collection; then set {@link #simpleCondition} with
     * the  created instance  of {@link SimpleCondition}.<br/>
     * Then will set the attribute {@link #andOr} with the input string <code>andOr</code>,  and
     * the nested attribute {@link #logicCondition} with the input parameter <code>logicCondition</code>.<br/>
     * The expressed logic condition is : <br/>
     * <code>simpleCondition andOr (logicCondition)</code>.<br/>
     * <br/>
     * An example is :<br/>
     * <code>LogicCondition logicCondition = new LogicSqlCondition("age","<=",new Integer(35),"AND", new LogicSqlCondition("salary",">=", new Integer(35000)))</code><br/>
     * <code>logicCondition</code> will represent: <br/>
     * <code>age <= 35 AND (salary >= 35000)</code>
     *
     * @param field: first element of the collection the will be used as {@link SimpleCondition#parameter} of the attribute {@link #simpleCondition}<br/>
     * @param operator: name of the {@link Operator} of the {@link SimpleCondition}.
     * @param value: second element of the collection the will be used as {@link SimpleCondition#parameter} of the attribute {@link #simpleCondition}<br/>
     * @param andOr: string that should assume just the values "AND" or "OR" to express if the {@link #simpleCondition} is in AND or in OR with the {@link #logicCondition}
     * @param logicCondition: instance of {@link LogicCondition} used to set the nested attribute {@link #logicCondition}
     */
    public LogicSqlCondition(String field,String operator, Object value,String andOr,LogicCondition logicCondition) {
        this.simpleCondition= new SimpleCondition(field,operator,value);
        this.logicCondition=logicCondition;
        this.andOr=andOr;
    }

    public LogicSqlCondition(String field,String operator,String andOr,LogicCondition logicCondition) {
        this.simpleCondition= new SimpleCondition(field,operator);
        this.logicCondition=logicCondition;
        this.andOr=andOr;
    }

    public  void setSimpleCondition(SimpleCondition simpleCondition){
        this.simpleCondition=simpleCondition;
    }
    public  SimpleCondition getSimpleCondition(){
        return simpleCondition;
    }
    public  void setLogicCondition(LogicCondition logicCondition){
        this.logicCondition=logicCondition;
    }
    public  LogicCondition getLogicCondition(){
        return logicCondition;
    }

    public void setAndOr(String andOr){
        this.andOr=andOr;
    }

    public String getAndOr(){
        return andOr;
    }
    
    public LogicSqlCondition(List<SimpleCondition> simpleConditions) throws Exception{
    	this( simpleConditions,  "AND");
    }
    
    public LogicSqlCondition(List<SimpleCondition> simpleConditions, String andOr) throws Exception{
    	buildLogicSqlCondition(simpleConditions,andOr);
    }
    
    private void buildLogicSqlCondition(List<SimpleCondition> simpleConditions, String andOr) throws Exception{
    	if(simpleConditions==null || simpleConditions.isEmpty()) throw new Exception("Error in  LogicSqlCondition.buildLogicSqlCondition(List<SimpleCondition> simpleConditions, String andOr); simpleConditions can't be null or empty");
    	try {
    		if(andOr==null) andOr="AND";
			for (SimpleCondition simpleCondition :simpleConditions){
				if(getSimpleCondition()==null){
					setSimpleCondition(simpleCondition);
				}else if(getLogicCondition()==null){
					logicCondition = new LogicSqlCondition(simpleCondition);
					this.andOr=andOr;
				}else{
					logicCondition = new LogicSqlCondition(simpleCondition,getAndOr(),logicCondition);
				}
			}
		} catch (Exception e) {
			throw new Exception("Error in  LogicSqlCondition.buildLogicSqlCondition(List<SimpleCondition> simpleConditions, String andOr): " + e.toString(),e);
		}
    }
    
    public LogicSqlCondition(String[] fields, Object[] values) throws Exception{
    	if(fields==null || fields.length==0) throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values); fields can't be null or empty");
    	if(values==null || values.length==0) throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values); values can't be null or empty");
    	if(values.length!=fields.length)     throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values); fields and values must have the same lenght");
    	try {
			List<SimpleCondition> simpleConditions = new ArrayList<SimpleCondition>();
			for (int i=0;i<fields.length;i++){
				SimpleCondition simpleCondition = new SimpleCondition(fields[i],"==", values[i]);
				simpleConditions.add(simpleCondition);
			}
			buildLogicSqlCondition(simpleConditions,"AND");
		} catch (Exception e) {
			throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values): " + e.toString(),e);
		}
    }
    
    public LogicSqlCondition(String[] fields, Object[] values, String andOr, String operator) throws Exception{
    	if(fields==null || fields.length==0) throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values, String andOr, String operator); fields can't be null or empty");
    	if(values==null || values.length==0) throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values, String andOr, String operator); values can't be null or empty");
    	if(values.length!=fields.length)     throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values, String andOr, String operator); fields and values must have the same lenght");
    	try {
    		if(StringUtils.isEmpty(andOr)){
    			andOr="AND";
    		}
    		if(StringUtils.isEmpty(operator)){
    			operator="==";
    		}
			List<SimpleCondition> simpleConditions = new ArrayList<SimpleCondition>();
			for (int i=0;i<fields.length;i++){
				SimpleCondition simpleCondition = new SimpleCondition(fields[i],operator, values[i]);
				simpleConditions.add(simpleCondition);
			}
			buildLogicSqlCondition(simpleConditions,operator);
		} catch (Exception e) {
			throw new Exception("Error in  LogicSqlCondition.LogicSqlCondition(String[] fields, Object[] values, String andOr, String operator): " + e.toString(),e);
		}
    }

	
    
    
}