package com.github.alessandrocolantoni.mom.query;



import java.util.ArrayList;
import java.util.Collection;


/**
 * An instance of this class can be viewed as the representation of a condition of every kind, made of an operator and a collection of arguments.
 * Examples of simple conditions could be: <code>a<b</code>,  <code>a==3</code>, <code>!a</code>, or <code>a between b and c</code>.<br/>
 * A simple condition can have any number of arguments, as <code>a==3</code> has two, <code>!a</code> has one and <code>a between b and c</code> has three<br/>
 * Arguments can be of any kind of type or class; a simple condition can have just one operator.
 * This class just represents the simple condition, not its interpretation, nor its value (true or false).
 * Its interpretation must be performed by an other class, so to maintain the general purpose of this one.
 * For example this simple condition could be used as condition to use in the where clause of sql query. The condition: <br/>
 * <code>price < 10000</code>  <br/>
 * could be easily represented by this class, and could be one of the conditions of the where clause of  an sql query.<br/>
 * <br/>
 * The operator is represented by the class {@link Operator}, and  the arguments  are represented by the collection <code>parameter</code>
 *
 */
public class SimpleCondition {


    protected Operator operator;
    protected Collection<Object> parameter;

    public SimpleCondition(){

    }

    /**
     * Create a new simple condition, specifying directly which are his arguments and which is his operator.
     * For example if you want to represent the simple condition that the variable with name 'price' is less than the Integer 100 you could do:<br/>
     * <code>Vector parameter = new Vector();</code><br/>
     * <code>parameter.add("price");</code><br/>
     * <code>parameter.add(new Integer(100);</code><br/>
     * <code>Operator operator = new Operator("<");</code><br/>
     * <code>SimpleCondition simpleCondition = new SimpleCondition(parameter,operator);</code><br/>
     *
     * @param parameter collection representing the arguments of the simple condition
     * @param operator represents the operator (see {@link Operator}) of the simple condition.
     *
     */
    public SimpleCondition(Collection<Object> parameter,Operator operator) {
        this.operator = operator;
        this.parameter = parameter;
    }

    /**
     * Create a new  simple condition  specifying directly which are his arguments and which is the name of his operator. This constructor will create
     * a new instance of the class {@link Operator} with the name <code>operator</code> (see {@link Operator#Operator(String name)}) and will use as the protected field operator.
     * For example if you want to represent the simple condition that the variable with name price is less than the Integer 100 you could do:<br/>
     * <code>Vector parameter = new Vector();</code><br/>
     * <code>parameter.add("price");</code><br/>
     * <code>parameter.add(new Integer(100);</code><br/>
     * <code>SimpleCondition simpleCondition = new SimpleCondition(parameter,"<");</code><br/>
     *
     * @param parameter collection representing the arguments of the simple condition
     * @param operator name of the operator, (see {@link Operator}) of the simple condition
     */
    public SimpleCondition(Collection<Object> parameter,String operator) {
        this.operator = new Operator(operator);
        this.parameter = parameter;
    }

    /**
     * Create a new SimpleCondition with two operands, specified in the input, the first of which is a String and the second is a generic object; the operator
     * name is specified in input too. This constructor will create
     * a new instance of the class {@link Operator} with the name <code>operator</code> (see {@link Operator(String)}) and will use as the protected field operator.<br/>
     * The arguments will be the two input parameters <code>field</code> and <code>value</code>
     * Creates a new SimpleCondition where the operator.name is operator, the first parameter is field and the second is value.<br/>
     * So if you would express the condition price < 10000 you just should do:<br/>
     * <code>SimpleCondition simpleCondition = new SimpleCondition("price","<", new Integer(10000));</code><br/>
     * @param field: first parameter of the new SimpleCondition
     * @param operator: operator name of the new SimpleCondition
     * @param value: second parameter of the new SimpleCondition
     */
    public SimpleCondition(String field,String operator, Object value) {
        this.operator = new Operator(operator);
        this.parameter = new ArrayList<Object>();
        parameter.add(field);
        parameter.add(value);
//        ArrayList<Object> vectorParameter = new ArrayList<Object>();
//        vectorParameter.add(field);
//        vectorParameter.add(value);
//        this.parameter = vectorParameter;
    }

    public SimpleCondition(String field,String operator) {
        this.operator = new Operator(operator);
        this.parameter = new ArrayList<Object>();
        parameter.add(field);
        
//        Vector<Object> vectorParameter = new Vector<Object>();
//        vectorParameter.add(field);
//        this.parameter = vectorParameter;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Collection<Object> getParameter() {
        return parameter;
    }

    public void setParameter(Collection<Object> parameter) {
        this.parameter = parameter;
    }
}