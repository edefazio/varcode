package tutorial.varcode.chap2.markup;

import junit.framework.TestCase;
import varcode.java.model._methods._method;
import varcode.markup.codeml.code._Method;

public class _5_Method_Model
    extends TestCase
{
    public static class _MethodTemplate
        extends _Method
    {
        /*$*/
        public static long nextRandomLong( long seed )
        {
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            return seed;
        }
        /*$*/
    }
    
    public void testMethod()
    {
        _method m = new _MethodTemplate().compose( );
        assertEquals( "nextRandomLong",m.getName());
        assertTrue( m.getSignature().getModifiers().containsAll("public","static") );
        assertEquals( "long", m.getSignature().getReturnType() );
        assertEquals( 1, m.getSignature().getParameters().count() );
        
        
    }
    
    
}
