package usecase.load;

import example.ExEnum;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._enum;
import varcode.load.DirectorySourceLoader;

/**
 *
 * @author Eric
 */
public class Load_enum 
    extends TestCase
{
    /** Create _interface (metalang model) from a String, great for testing */
    public void test_enumFromString()
    {
        _enum _e = Java._enumFrom(
            "public enum A { INSTANCE; public static int count = 100; }" );
        
        assertEquals( "A", _e.getName() );
    }
    
    /** Load a _enum based on a runtime Java class */
    public void test_enumFromRuntimeClass()
    {
        _enum _e = Java._enumFrom( ExEnum.class );
        assertEquals(_e.getPackageName(), 
            ExEnum.class.getPackage().getName() );
    }
    /** Load a _interface from runtime class using a custom SourceLoader */ 
    public void test_enumUsingCustomSourceLoader()
    {
        _enum _e = Java._enumFrom(DirectorySourceLoader.of( 
                System.getProperty( "user.dir" ) + "/src/test/java" ),
            ExEnum.class );
        
        assertEquals(  _e.getPackageName(), ExEnum.class.getPackage().getName() );
    }
    
    /** enum Javadoc comment */
    public enum NestedEnum
    {
        INSTANCE;
        
        public int getCount()
        {
            return 100;
        }
    }
    
    /** Test loading an _interface from a nested interface */
    public void testNested_enum()
    {
        _enum _e = Java._enumFrom( NestedEnum.class );
        
        assertTrue( _e.getModifiers().containsAll( "public" ) );
        // NOTE: when you load a nested/inner class 
        // it retains the package and ALL imports from the top-level class
        assertEquals( _e.getPackageName(), getClass().getPackage().getName() );
        assertTrue(_e.getImports().containsAll(TestCase.class, Java.class, _enum.class ) );
    }
}
