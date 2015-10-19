package com.github.alessandrocolantoni.mom.dao;




/**
 * 
 * 
 * @author alessandro.colantoni
 *
 * Exception thrown from Dao tier
 *
 */
public class DataAccessException extends Exception {
    
	private static final long serialVersionUID = 2924352008463522999L;
	
	
	
    
	/** Creates a new instance of DataAccessException */
    public DataAccessException() {
        super();
    }
    
    /** Creates a new instance of DataAccessException */
    public DataAccessException(String exceptionMsg) {
        super(exceptionMsg);

    }
    
    public DataAccessException(String exceptionMsg, Throwable exception){
       super(exceptionMsg, exception);
    }
    
    
}