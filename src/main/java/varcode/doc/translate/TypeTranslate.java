package varcode.doc.translate;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

/**
 * Translates java.lang.reflect.Types to Strings
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum TypeTranslate
    implements Translator
{
    INSTANCE;
    	
    @Override
    public Object translate( Object source ) 
	{
        
    	if( source instanceof AnnotatedType )
        {            
            AnnotatedType t = (AnnotatedType)source;
            return t.getType().getTypeName();
        }
  
        if( source instanceof Type )
        {
            //System.out.println( "TYPE " );
            return ((Type)source).getTypeName();
        }
		return source;
	}       
}
