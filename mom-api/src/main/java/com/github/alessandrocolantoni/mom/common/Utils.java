package com.github.alessandrocolantoni.mom.common;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {
	
	
	private static final Logger log = LoggerFactory.getLogger(Utils.class.getName());
	private static final String UTILS_EXCEPTION = ":::UTILS_EXCEPTION:::";
	
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
        try{
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
        } catch (Exception e) {
            throw new Exception(UTILS_EXCEPTION,e);
        }
        return result;
    }

}
