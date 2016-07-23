package com.github.alessandrocolantoni.mom.common;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtilsBean;



public class  BeanFieldComparator<T> implements Comparator<T>,java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1037935921515759768L;
	
	
	private Class<T> beanClass;
    private String[] fieldArray;
    public BeanFieldComparator (String[] fieldArray, Class<T> beanClass){
        this.fieldArray=fieldArray;
        this.beanClass = beanClass;
    }
    public BeanFieldComparator (String field, Class<T> beanClass)throws Exception{
        this.fieldArray =new String[1];
        this.fieldArray[0]=field;
        this.beanClass = beanClass;
    }
    public BeanFieldComparator (Collection<String> fieldList, Class<T> beanClass)throws Exception{
        fieldArray=new String[fieldList.size()];
        int i=0;
        for(String field:fieldList){
        	fieldArray[i] = field;
        	i++;
        }
        this.beanClass = beanClass;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public int compare(Object o1, Object o2) {

        try{
        	
        	T bean = null;
        	Object fieldValue = null;
        	if(beanClass.isInstance(o1)){
        		bean = (T) o1;
        		fieldValue = o2;
        	}else if (beanClass.isInstance(o2)){
        		bean = (T) o2;
        		fieldValue =o1;
        	}
        	
        	PropertyUtilsBean propertyUtilsBean = BeanUtilsBean.getInstance().getPropertyUtils();
        	Comparable propertyValue;
        	int compare = 0;
        	
        	
        	if(Object[].class.isInstance(fieldValue)){
	            Object[] fieldValueArray = (Object[])fieldValue;
	            
	            
	            
	            for (int i=0;i<fieldArray.length && compare == 0;i++){
	            	try{
	            		propertyValue=(Comparable)propertyUtilsBean.getProperty(bean,fieldArray[i]);
	            	}catch (NestedNullException e) {
	            		propertyValue=null;
					}
	                if(propertyValue==null && fieldValueArray[i]==null) {
	                    compare=0;
	                }else if(propertyValue==null){//null is the biggest value;
	                    compare=1;
	                }else if(fieldValueArray[i]==null){//null is the biggest value;
	                    compare=-1;
	                }else{
	                    compare =  propertyValue.compareTo((Comparable)fieldValueArray[i]);
	                }
	                
	            }
            }else{
            	propertyValue=(Comparable)propertyUtilsBean.getProperty(bean,fieldArray[0]);
                if(propertyValue==null && fieldValue==null) {
                    compare=0;
                }else if(propertyValue==null){//null is the biggest value;
                    compare=1;
                }else if(fieldValue==null){//null is the biggest value;
                    compare=-1;
                }else{
                    compare =  propertyValue.compareTo((Comparable)fieldValue);
                }
                
            	
            }
        	return compare;
        }catch(Exception e){
        	throw new RuntimeException(e);
        }
        
    }

    
}