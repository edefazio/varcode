package varcode.doc.translate;

/**
 * Translates a Java Class to be Serialized to be just a Simple name
 * 
 * For Example:
 * <UL>
 *   <LI> ClassToStringTranslator.INSTANCE.translate( int.class ); // = int
 *   <LI> ClassToStringTranslator.INSTANCE.translate( String.class ); // = String
 *   <LI> ClasstoStringTranslator.INSTANCE.translate( java.util.HashMap.class ); // = java.util.HashMap
 *   <LI> ClassToStringTranslator.INSTANCE.translate( io.varcode.Lang.class ); // = io.varcode.Lang
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
