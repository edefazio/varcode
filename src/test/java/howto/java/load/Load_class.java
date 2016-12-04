package howto.java.load;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class Load_class 
    extends TestCase
{
    /** Create a _class (metalang model) from a String, great for testing */
    public void test_classFromString()
    {
        _class _c = _Java._classFrom(
            "public class A { public int count = 100;}" );
        
        assertEquals( "A", _c.getName() );
    }
    
    /** Load a _class  based on a runtime Java class */
    public void test_class()
    {
        _class _c = _Java._classFrom( Load_class.class );
        assertEquals( _c.getPackageName(), getClass().getPackage().getName() );
    }
    
    public void test_classCustomSourceLoader()
    {
        _class _c = _Java._classFrom( 
            DirectorySourceLoader.of( 
                System.getProperty( "user.dir" ) + "/src/test/java" ),
            Load_class.class );
        
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
        _class _c = _Java._classFrom( NestedClass.class );
        
        assertTrue( _c.getModifiers().containsAll( "private" ) );
        // NOTE: when you load a nested/inner class 
        // it retains the package and ALL imports from the top-level class
        assertEquals( _c.getPackageName(), getClass().getPackage().getName() );
        assertTrue( _c.getImports().containsAll( 
            TestCase.class, _Java.class, _class.class ) );
    }
}
