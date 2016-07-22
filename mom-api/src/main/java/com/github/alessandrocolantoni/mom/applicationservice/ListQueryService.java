package com.github.alessandrocolantoni.mom.applicationservice;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface ListQueryService extends Serializable {

	/**
	 * Elements of collection must have a field named as the parameter field.
     * Will be returned a collection with all values of the property field of all elements of collection.
     * NOTE that no distinct is applied.
     * if collection is null an empty collection will be returned
	 * @param collection
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public <E,T> List<E> selectFieldFromCollection(Collection<T> collection, String field) throws Exception;

}
