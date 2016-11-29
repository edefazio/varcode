package tutorial.chap3.markup;

import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang._methods._method;
import varcode.java.load.JavaMetaLangLoader;
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
        _class _c = JavaMetaLangLoader._Class.from( _MethodTemplate.class );
        _method _m = _c.getMethodNamed( "nextRandomLong" );
        
        //_method m = new _MethodTemplate().compose( );
        assertEquals( "nextRandomLong",_m.getName() );
        assertTrue( _m.getSignature().getModifiers().containsAll("public","static") );
        assertEquals( "long", _m.getSignature().getReturnType() );
        assertEquals( 1, _m.getSignature().getParameters().count() );        
    }
    
    
}
