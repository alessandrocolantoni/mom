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

	
	/**
	 * Elements of collection must have fields with all names in properties.
     * Return all elements of collection where just one is taken with the same values for fields whose names are in properties.
     * If more elements in collection have the same values for properties, the first one iterating is taken.
     * If collection is null an empty collection will be returned.
     * There's no side effect on collection
     * 
	 * @param collection: Collection which elements have to be filtered to have just one elements with the same values of properties
     * @param properties: fields names of elements of collection to use to filter
     * @return all elements of collection where just one is taken with the same values for fields whose names are in properties. 
     *
	 * @throws Exception
	 */
	public <T> List<T> selectDistinct(Collection<T> collection, String[] properties) throws Exception;

	
	public <T> List<T> selectDistinct(Collection<T> collection, String property) throws Exception;


	/**
	 * Elements of collection must have a field named <code>in_property</code>.</br>
     * This method will sort <code>in_collection</code> comparing the values of <code>in_property</code>.</br>
     * @param in_collection collection to sort
	 * @param in_property property of elements of  <code>in_collection</code> to compare to sort
	 * @throws Exception
	 */
	public <T> void sortCollection(List<T> in_collection, String in_property) throws Exception;


	/**
	 * Elements of in_collection must have fields with all names in properties.
     * The method will sort in_collection comparing the values of in_properties in the order they appear in the array
     * @param in_collection: collection to sort
     * @param in_properties: properties of elements of  in_collection to use to sort
     * @throws Exception
	 */
	public <T> void sortCollection(List<T> in_collection, String[] in_properties) throws Exception;


	/**
	 * 
	 * Elements of collection must have fields with all names in properties.
     * Will be returned all elements of collection which no one of its properties[i] has value equal to values[i] for each i.
     * If both property[i] value and values[i] are null they are considered equal.
     * @param collection: collection whose elements have to be returned if all properties[i] have value not equal to values[i]
     * @param properties: properties to be compared to values
     * @param values:  values to be compared to properties.
     * @return all elements of collection which no one of its properties[i] has value equal to values[i] for each i or null if collection is null
     * @throws Exception
	 */
	public <T> List<T> selectWhereFieldsNotEqualsTo(Collection<T> collection, String[] properties, Object[] values) throws Exception;


	/**
	 * 
	 * Acts as  selectWhereFieldsNotEqualsTo(Collection collection, String[] properties, Object[] values) with just one property and one value.
     * @param collection:collection whose elements have to be returned if property has value not equal to value. If both null are considered equal
     * @param property: property of elements of collection to be compared to value
     * @param value: value to be compared to property of elements of collection
     * @return all elements of collection which  property has value not equal to value or empty if collection is null
     * @throws Exception
	 */
	public <T> List<T> selectWhereFieldNotEqualsTo(Collection<T> collection, String property, Object value) throws Exception;


	/**
    * Elements of collection must have fields with all names in properties.
    * Will be returned all elements of collection which all of its properties[i] has value equal to values[i] for each i.
    * If both property[i] value and values[i] are null they are considered equal.
    * if collection is null returns empty;
    * @param collection: collection whose elements have to be returned if all properties[i] have value equal to values[i]
    * @param properties: properties to be compared to values
    * @param values:  values to be compared to properties.
    * @return all elements of collection which all of its properties[i] has value equal to values[i] for each i or empty if collection is null
    * @throws Exception
    */
	public <T> List<T> selectWhereFieldsEqualsTo(Collection<T> collection, String[] properties, Object[] values) throws Exception;


	 /**
     * Acts as selectWhereFieldsEqualsTo(Collection collection, String[] properties, Object[] values) with just one property and one value
     * @param collection: collection whose elements have to be returned if property has value  equal to value. If both null are considered equal
     * @param property: property of elements of collection to be compared to value
     * @param value: value to be compared to property of elements of collection
     * @return  all elements of collection which  property has value  equal to value, or empty if collection is null
     * @throws Exception
     */
	public <T> List<T> selectWhereFieldEqualsTo(Collection<T> collection, String property, Object value) throws Exception;

	/**
     * Elements of collection must have fields with all names in properties.
     * Will be returned all elements of collection which all of its properties[i] has value greater than values[i] for each i.
     * If one of value of property[i] and values[i] is null element will be returned
     * if collection is null returns null;
     * todo if value of property[i] is null element should'nt be returned
     * @param collection: collection whose elements have to be returned if property has value  greater than value.
     * @param properties: properties to be compared to values
     * @param values: value to be compared to property of elements of collection
     * @return  all elements of collection which all of its properties[i] has value greater than values[i] for each i.
     * @throws Exception
     */
	public <T> List<T> selectWhereFieldsGreaterThan(Collection<T> collection, String[] properties, Object[] values) throws Exception;

	
	public <T> List<T> selectWhereFieldGreaterThan(Collection<T> collection, String property, Object value) throws Exception;


	/**
	 * 
	 * @param collection
	 * @param properties
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public <T> List<T>  selectWhereFieldsLessThan(Collection<T> collection, String[] properties, Object[] values) throws Exception;
	
	/**
	 * 
	 * @param collection
	 * @param property
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> selectWhereFieldLessThan(Collection<T> collection, String property, Object value) throws Exception;

	
	/**
     * This method returns a new collection holding the elements of <code>collection</code> which attribute <code>property</code> assumes one of the
     * values in the input array parameter <code>values</code>. So each element of <code>collection</code> must have an attribute with name as specified by
     * the input string parameter <code>property</code>,
     * and will be returned all elements of <code>collection</code>  which attribute <code>property</code> has value equals to at least one of <code>values[i]</code> for each <code>i</code>.
     * If the <code>property</code> value of an element of <code>collection</code> is null, and some item of <code>values</code> is null too, the element of <code>collection</code>
     * will be added to the collection to return.</br>
     * If  <code>collection</code> is null, emoty is returned; if <code>values</code> is null, an empty collection will be returned.</br>
     *
     * @param collection collection to extract elements from
     * @param property atrtibute name of the elements of <code>collection</code> that has to assume one of the values in <code>values</code>
     * @param values array of values one of which at least must be equals to the <code>property</code> value of an element of <code>collection</code>, to add such element to the collection to return.
     * @return  a new collection holding the elements of <code>collection</code> which attribute <code>property</code> assumes one of the values in the input array parameter <code>values</code>
     * @throws Exception - if some element of <code>collection</code> don't have an attribute named  <code>property</code>
     */
	public <T> List<T> selectWhereFieldIn(Collection<T> collection, String property, Object[] values) throws Exception;

	/**
     * Return an element of collection whose property named field has value value. If more than such an element exists it's no determined which one is returned
     * if such an element doesn't exist return null;
     * If collection is null or empty return null.
     * @param <E>
     * @param collection: collection where the element will be searched in
     * @param field: property of colelction's element that has to have value value
     * @param value: value of property field that must have the collection's element to be returned
     * @return an element of collection whose property named field has value value
     * @throws IllegalAccessException
     */
	public <T, E> T findInCollection(Collection<T> collection, Class<T> beanClass, String field, E value) throws Exception;

	
	/**
	 * 
	 * @param list
	 * @param beanClass
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public <T, E> T findInOrderedCollection(List<T> list, Class<T> beanClass, String field, E value) throws Exception;


	

	
	

	
	
}
