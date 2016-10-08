package varcode.doc.translate;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author eric
 */
public class TypeTranslateTest
    extends TestCase
{
    public static class AClass
    {
        public int doNothing( @IHateTheseThings String s )
        {
            return 1;
        }
        
        public List<String> gen( @Deprecated int a)
        {
            return null;
        }        
    }
    
    public void testTranslate() throws NoSuchMethodException
    {        
        assertEquals( "java.lang.String", (String) TypeTranslate.INSTANCE.translate( 
            AClass.class.getMethod( "doNothing", String.class ).getAnnotatedParameterTypes()[0] ) );
        
        
        assertEquals( "int", (String) TypeTranslate.INSTANCE.translate( 
            AClass.class.getMethod( "doNothing", String.class ).getAnnotatedReturnType() ) );
        

        
        assertEquals( "int", (String) TypeTranslate.INSTANCE.translate( 
            AClass.class.getMethod( "doNothing", String.class ).getReturnType() ) );
        
        assertEquals( "int", (String) TypeTranslate.INSTANCE.translate( 
            AClass.class.getMethod( "doNothing", String.class ).getGenericReturnType() ) );
        
        
        assertEquals("java.util.List<java.lang.String>", (String) TypeTranslate.INSTANCE.translate( 
            AClass.class.getMethod( "gen", int.class ).getGenericReturnType() ) );
        
    }
    
}
