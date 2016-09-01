package varcode.buffer;

import java.util.Collection;

import varcode.buffer.TranslateBuffer.Translator;

/**
 * Instead of printing a collection, translate it to an array
 * and print it as an array (however the array translator)
 *   
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CollectionToArrayTranslate 
	implements Translator
{
    INSTANCE;
    	
    public Object translate( Object source ) 
	{
    	if( source instanceof Collection )
        {
        	Collection<?> coll = (Collection<?>)source;
        	return coll.toArray( new Object[ 0 ] );
        }
		return source;
	}    	
}
    
    