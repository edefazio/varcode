package varcode.buffer;

import varcode.buffer.TranslateBuffer.Translator;

/**
 * Translates a Java Class to be Serialized to be just a Simple name
 * 
 * For Example:
 * <UL>
 *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( int.class ); // = int
 *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( String.class ); // = String
 *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( java.util.HashMap.class ); // = java.util.HashMap
 *   <LI> JavaSimpleClassTranslator.INSTANCE.translate( io.varcode.Lang.class ); // = Lang
 * </UL>  
 */
public enum ClassToStringTranslate
    implements Translator
{
    INSTANCE;
        
    public Object translate( Object source )
    {
    	if( source == null )
    	{
    		return "";
        }
        if( source instanceof Class )
        {
        	Class<?> clazz = (Class<?>)source;
                
            if( !clazz.isPrimitive()
                && clazz.getPackage() != null	
                && clazz.getPackage().getName().equals( "java.lang" ) )
            {
                return clazz.getSimpleName();
            }
            return clazz.getCanonicalName();
        }
        return source;
    }            
}
