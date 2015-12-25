package com.github.alessandrocolantoni.mom.common;



import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.alessandrocolantoni.mom.dao.DataAccessException;

public final class Utils {
	
	
	private static final Logger log = LoggerFactory.getLogger(Utils.class.getName());
//	private static final String UTILS_EXCEPTION = ":::UTILS_EXCEPTION:::";
	
	/**
     * This method splits in two part a dot separated list of tokens, that is the input string <code>path</code>.</br>
     * The first part is the same <code>path</code> except the last token, and the second part is the last token.
     * If <code>path</code> is null a null is returned.
     * @param path dot separated list of tokens
     * @return an array of two strings, where the first element is a string that is the dot separated list of tokens of input except the last token,
     *          and the second element is the last token. If <code>path</code> is just one token the first element is an empty string, and the second one
     *          is the same <code>path</code>. If <code>path</code> is null a null is returned.</br>
     * @throws Exception
     */
    public static String[] getExceptLastTokenAndLastToken(String path) throws Exception {
        String[] result = null;
        
        if (path==null) return null;
        result = new String[2];
        String exceptLastToken="";
        String lastToken;
        
        int lastIndexOf=path.lastIndexOf("."); 
        if (lastIndexOf==-1) {
            lastToken=path;
        } else{
            exceptLastToken = path.substring(0,lastIndexOf) ;
            lastToken=path.substring(lastIndexOf+1);
        }
        
        log.trace("::::::::::::::::::::: path="+path);
        log.trace("::::::::::::::::::::: exceptLastToken="+exceptLastToken);
        log.trace("::::::::::::::::::::: lastToken="+lastToken);
        
        result[0]= exceptLastToken;
        result[1]= lastToken;
        
        return result;
    }
    
    
    public static String[] getFirstAttributeNameAndRemainingPath(String path) throws Exception {
        String[] result = null;
        
        if (path==null) return null;
        result = new String[2];
        String firstAttributeName;
        String remainingPath="";
        int indexOf=path.indexOf(".");
        if (indexOf==-1) {
            firstAttributeName=path;
        } else{
            firstAttributeName=path.substring(0,indexOf);
            remainingPath=path.substring(indexOf+1);
        }
        log.trace("getFirstAttributeNameAndRemainingPath(String path): path="+path);
        log.trace("getFirstAttributeNameAndRemainingPath(String path): firstAttributeName="+firstAttributeName);
        log.trace("getFirstAttributeNameAndRemainingPath(String path): remainingPath="+remainingPath);
        result[0]= firstAttributeName;
        result[1]= remainingPath;
        
        return result;
    }
    
    @SuppressWarnings("rawtypes")
	public static Class getGenericClass(Type type) throws Exception{
    	Class genericClass = null;
    	try {
			if(type instanceof ParameterizedType){
			    ParameterizedType parameterizedType = (ParameterizedType) type;
			    Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
			    for(Type fieldArgType : fieldArgTypes){
			    	genericClass = (Class) fieldArgType;
			        log.debug("genericClass = " + genericClass);
			    }
			}
		} catch (Exception e) {
			throw new Exception("Exception thrown in Utils.getGenericClass(Type type): " + e.toString(),e);
		}
    	return genericClass;

    }
    
    /**
     * 
     * @param realClass class holding the pAttributeName
     * @param pAttributeName attribute that holds the annotation on pAttributeName or on its getter
     * @param annotationClass
     * @return null if realClass does not have a field pAttributeName
     * @throws Exception
     */
    public static <T extends Annotation, E> T  getAnnotation(Class<E> realClass, String pAttributeName, Class<T> annotationClass) throws Exception{
		
		T annotation =null;
		
		Field field=null;
		try {
			field = realClass.getDeclaredField(pAttributeName);
		} catch (NoSuchFieldException e) {
			/**
			 * Nothing. pAttributeName is not a field of realClass
			 * null will be returned
			 */
			//getLogger().warn(pAttributeName + "is not a field of "+realClass.toString());
		}
		
		
		if(field!=null){
			annotation = field.getAnnotation(annotationClass);
			if(annotation ==null){
				Method getter = getGetter(realClass,pAttributeName);
				annotation = getter.getAnnotation(annotationClass);
			}
		}
		
        return annotation;
	}

    
    private static <E> Method getGetter(Class<E> realClass, String pAttributeName) throws Exception{
		if(realClass==null || pAttributeName==null || pAttributeName.trim().equals("")){
			throw new Exception("Error ::: realClass is null or pAttributeName is null or empty string " );
		}
	
		Method getter = realClass.getDeclaredMethod("get"+pAttributeName.substring(0,1).toUpperCase()+pAttributeName.substring(1));
		return getter;
	}
}
