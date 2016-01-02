package com.github.alessandrocolantoni.mom.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationTypeMaps implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     *  map for oneToOne and MToOne
     */
    Map<String,List<String>> directReferenceMap = new HashMap<String,List<String>>(); 
    
    Map<String,List<String>> embeddedIdMap = new HashMap<String,List<String>>(); 
    
    Map<String,List<String>> oneToManyReferenceMap = new HashMap<String,List<String>>();

    Map<String,List<String>> manyToManyReferenceMap = new HashMap<String,List<String>>();
    
    Map<String,List<String>> manyToManyInverseReferenceMap = new HashMap<String,List<String>>();

	public Map<String, List<String>> getDirectReferenceMap() {
		return directReferenceMap;
	}

	public void setDirectReferenceMap(Map<String, List<String>> directReferenceMap) {
		this.directReferenceMap = directReferenceMap;
	}

	

	public Map<String, List<String>> getEmbeddedIdMap() {
		return embeddedIdMap;
	}

	public void setEmbeddedIdMap(Map<String, List<String>> embeddedIdMap) {
		this.embeddedIdMap = embeddedIdMap;
	}

	public Map<String, List<String>> getOneToManyReferenceMap() {
		return oneToManyReferenceMap;
	}

	public void setOneToManyReferenceMap(Map<String, List<String>> oneToManyReferenceMap) {
		this.oneToManyReferenceMap = oneToManyReferenceMap;
	}

	public Map<String, List<String>> getManyToManyReferenceMap() {
		return manyToManyReferenceMap;
	}

	public void setManyToManyReferenceMap(Map<String, List<String>> manyToManyReferenceMap) {
		this.manyToManyReferenceMap = manyToManyReferenceMap;
	}

	public Map<String, List<String>> getManyToManyInverseReferenceMap() {
		return manyToManyInverseReferenceMap;
	}

	public void setManyToManyInverseReferenceMap(Map<String, List<String>> manyToManyInverseReferenceMap) {
		this.manyToManyInverseReferenceMap = manyToManyInverseReferenceMap;
	}

	
  
    
    

    

}
