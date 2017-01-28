package usecase.load;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class Load_class 
    extends TestCase
{
    /** Create a _class (metalang model) from a String, great for testing */
    public void test_fromString()
    {
        _class _c = Java._classFrom(
            "public class A { public int count = 100;}" );
        
        assertEquals( "A", _c.getName() );
    }
    
    /** Load a _class  based on a runtime Java class */
    public void test_class()
    {
        _class _c = Java._classFrom( Load_class.class );
        assertEquals( _c.getPackageName(), getClass().getPackage().getName() );
    }
    

    
    /** Class Javadoc comment */
    private class NestedClass 
    {
        public int count;
        
        public int getCount()
        {
            return count;
        }
    }
    
    public void testNested_class()
    {
        _class _c = Java._classFrom( NestedClass.class );
        
        assertTrue( _c.getModifiers().containsAll( "private" ) );
        // NOTE: when you load a nested/inner class 
        // it retains the package and ALL imports from the top-level class
        assertEquals( _c.getPackageName(), getClass().getPackage().getName() );
        assertTrue(_c.getImports().containsAll(TestCase.class, Java.class, _class.class ) );
    }
}
