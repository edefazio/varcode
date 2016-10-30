package varcode.doc.translate;

import java.util.Collection;

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
    	
    @Override
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
    
    