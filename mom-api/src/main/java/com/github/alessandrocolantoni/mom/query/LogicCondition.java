package com.github.alessandrocolantoni.mom.query;



/**
 * This class represents a chain of {@link SimpleCondition} in AND/OR between them.
 * This class is made of a {@link SimpleCondition} in AND/OR recursively  with a LogicCondition.
 * This class has just three attributes:<br/>
 * an instance of {@link SimpleCondition} called <code>simpleCondition</code>, a String <code>andOr</code> which should assume just the values "AND" or "OR", specifying
 * if the <code>simpleCondition</code> is in AND or in OR with the recursive LogicCondition that is the third attribute. <br/>
 * <br/>
 * So for example an instance of this class could be used to represent the following logic condition:<br/>
 * <code>logicCondition1 = simpleCondition1 AND (logicCondition2)</code>.<br/>
 * Note that each nested logic condition should be considered include between parenthesis.
 * The above <code>logicCondition1</code> could be:  <br/>
 * <code>simpleCondition1 AND (simpleCondition2 OR (simpleCondition3 OR (simpleCondition4 AND simpleCondition5)))</code>.<br/>
 *
 */
public abstract class LogicCondition{

    protected LogicCondition logicCondition = null;

    protected SimpleCondition simpleCondition;

    protected String andOr;

    public abstract void setSimpleCondition(SimpleCondition simpleCondition);
    public abstract SimpleCondition getSimpleCondition();
    public abstract void setLogicCondition(LogicCondition logicCondition);
    public abstract LogicCondition getLogicCondition();
    public abstract String getAndOr();
    public abstract void setAndOr(String andOr);
}