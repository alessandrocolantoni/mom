package com.github.alessandrocolantoni.mom.dao.logicConditionJqlBuilder;

import java.io.Serializable;

import javax.persistence.Query;

import com.github.alessandrocolantoni.mom.dao.DataAccessException;
import com.github.alessandrocolantoni.mom.query.LogicCondition;

public interface LogicConditionJqlBuilder extends Serializable {

	@Deprecated
	public <E> Query createQuery(Boolean distinct, String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy) throws DataAccessException;

	@Deprecated
	public <E> Query createQuery(Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;

	public <E> Query createQuery(Boolean distinct, String[] selectFields, Class<E> realClass, LogicCondition logicCondition, String orderBy, String[] groupBy) throws DataAccessException;

	@Deprecated
	public <E>  Query createQuery(String[] selectFields, Class<E> realClass, LogicCondition logicCondition) throws DataAccessException;

}
