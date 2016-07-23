package com.github.alessandrocolantoni.mom.common;


import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;



public class FieldComparator<T> implements Comparator<T>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1372033447644172456L;
	
	
	private String[] fieldArray;
    
	public FieldComparator (String[] fieldArray){
        this.fieldArray=fieldArray;
    }
    public FieldComparator (String field) throws Exception{
        this.fieldArray =new String[1];
        this.fieldArray[0]=field;
    }
    public FieldComparator (List<String> fieldList) throws Exception{
        fieldArray=new String[fieldList.size()];
        for(int i=0;i<fieldList.size();i++){
            fieldArray[i]=fieldList.get(i);
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public int compare(T o1, T o2) {
    	try {
			PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
			Comparable propertyValueO1;
			Comparable propertyValueO2;
			
			int compare = 0;
			for (int i=0;i<fieldArray.length && compare == 0;i++){
				
				try {
					propertyValueO1=(Comparable)propertyUtilsBean.getProperty(o1,fieldArray[i]);
				} catch (NestedNullException e) {
					propertyValueO1=null;
				}
				
			    try {
					propertyValueO2=(Comparable)propertyUtilsBean.getProperty(o2,fieldArray[i]);
				} catch (NestedNullException e) {
					propertyValueO2=null;
				}
			    
			    if(propertyValueO1==null && propertyValueO2==null) {
			        compare=0;
			    }else if(propertyValueO1==null){//null is the biggest value;
			        compare=1;
			    }else if(propertyValueO2==null){//null is the biggest value;
			        compare=-1;
			    }else{

			        compare =  propertyValueO1.compareTo(propertyValueO2);
			    }
			    

			}
			return compare;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
    }

    
	
}
