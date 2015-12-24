package com.github.alessandrocolantoni.mom.query;
import java.util.Collection;

/**
 * Created by Alessandro Colantoni
 *
 */
public class Where {
    private Collection<LogicCondition> orConditions;

    public Where() {
    }

    public Where(Collection<LogicCondition> orConditions) {
        this.orConditions = orConditions;
    }



    public Collection<LogicCondition> getOrConditions() {
        return orConditions;
    }

    public void setOrConditions(Collection<LogicCondition> orConditions) {
        this.orConditions = orConditions;
    }
}
