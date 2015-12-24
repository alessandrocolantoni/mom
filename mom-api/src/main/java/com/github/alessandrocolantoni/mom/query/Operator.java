package com.github.alessandrocolantoni.mom.query;



/**
 * This class <code>Operator</code> represents an operator.<br/>
 * The concept is abstract, (not the class that is not), so it can be used for math, logic operators or other.<br/>
 * <br/>
 * This class has four attributes: <code>name</code>, <code>shortName</code>, <code>mathSymbol</code>,<code>identifier</code> <br/>
 * Examples of instance of <code>Operator</code> could be the logic operator <code>AND</code> that could be so defined :<br/>
 * <code>Operator operator = new Operator("AND");<code><br/>
 * or the lessThan (<) operator: <br/>
 * <code>Operator operator = new Operator();<code><br/>
 * <code>operator.setName("lessThan");<code><br/>
 * <code>operator.setMathSymbol("<");<code><br/>
 * <code>operator.setShortName("LT");<code><br/>
 * <br/>
 * The four attributes are what really represent the operator, and they can be used as you want, as this class just represent the operator and do not
 * perform any operation, any interpretation , and don't consider any semantic value of the four attributes.<br/>
 * So you can represent the operator lessThan just doing:<br/>
 * <code>Operator operator = new Operator();<code><br/>
 * <code>operator.setName("<");<code><br/>
 * if for your purpose is best. <br/>
 * How the four attributes (not all of them are necessary, as for your purpose just one could be enough) will be used will depend by methods of other
 * classes that will use it.
 *
 */
public class Operator{
    protected String name ;
    protected String shortName ;
    protected String mathSymbol ;
    protected String identifier ;

    public  Operator(){
    }

    /**
     * Create a new <code>Operator</code> with <code>name</code>.</br>
     * For example if you do  <code>new Operator("<");<code>, you create an instance of this class that represents the operator lessThan
     *
     * @param name
     */
    public  Operator(String name){
        setName(name);
    }

    public void setName(String name){
        this.name = name;
    }


    public String getName(){
        return name;
    }


    public void setShortName(String shortName){
        this.shortName = shortName;
    }


    public String getShortName(){
        return shortName;
    }


    public void setMathSymbol(String mathSymbol){
        this.mathSymbol = mathSymbol;
    }


    public String getMathSymbol(){
        return mathSymbol;
    }


    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }


    public String getIdentifier(){
        return identifier;
    }
    
}