package tutorial.varcode.chapx.appendix;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.lang._class;
import varcode.java.lang._methods._method;
import varcode.java.load.JavaMetaLangLoader;
//import varcode.markup.codeml.code._Method;
public class _5_Method_Model
    extends TestCase
{
    private static class _MethodTemplate //extends _Method
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
        _class _c = _Java._classFrom( _MethodTemplate.class );
        _method _m = _c.getMethodNamed( "nextRandomLong" );
        
        //_method m = new _MethodTemplate().compose( );
        assertEquals( "nextRandomLong",_m.getName() );
        assertTrue( _m.getSignature().getModifiers().containsAll("public","static") );
        assertEquals( "long", _m.getSignature().getReturnType() );
        assertEquals( 1, _m.getSignature().getParameters().count() );        
    }
    
    
}
