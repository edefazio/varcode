package tutorial.chap3.markup;

import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._methods._method;
import varcode.java.load._JavaLoader;
//import varcode.markup.codeml.code._Method;

public class _5_Method_Model
    extends TestCase
{
    public static class _MethodTemplate //extends _Method
    {
        public static long nextRandomLong( long seed )
        {
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            return seed;
        }
    }
    
    public void testMethod()
    {
        _class _c = _JavaLoader._Class.from( _MethodTemplate.class );
        _method m = _c.getMethodNamed( "nextRandomLong" );
        
        //_method m = new _MethodTemplate().compose( );
        assertEquals( "nextRandomLong",m.getName());
        assertTrue( m.getSignature().getModifiers().containsAll("public","static") );
        assertEquals( "long", m.getSignature().getReturnType() );
        assertEquals( 1, m.getSignature().getParameters().count() );        
    }
    
    
}
