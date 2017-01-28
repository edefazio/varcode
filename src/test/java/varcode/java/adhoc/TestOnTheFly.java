package varcode.java.adhoc;

import java.util.Random;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 * varcode makes it easy to dyanmically build/compile/use and Test dynamically
 * constructed code.  This example shows how to: 
 * <OL>
 *   <LI>author java code using a _class (metalanguage model) (_class ->.java)
 *   <LI>compile the dynamically built code (.java -> .class)
 *   <LI>load the .class (bytes) into a AdHocClassLoader at runtime
 *   <LI>construct an instance of an "AdHocClass" at runtime (passing in args)
 *   <LI>test/invoke static methods/ instance methods on the AdHocClass
 * </OL>
 * @author Eric
 */
public class TestOnTheFly 
   extends TestCase
{
    public void testStaticMethod()
    {
        _class _c = _class.of( "howto.java.author", 
            "public class StaticRandom")
            .imports( Random.class )                
            .field( "public static final int MAX = 100;")    
            .field( "public static final Random RANDOM = new Random();" )
            .method( "public static int getRandom()", 
                "return RANDOM.nextInt( MAX ) ;" );
        
        //compile & load an "AdHoc" StaticClass into a new AdHocClassLoader
        Class clazz = _c.loadClass();
        
        assertTrue( clazz.getClassLoader() instanceof AdHocClassLoader );
        
        //now test the results of the static getRandom() method
        for( int i = 0; i < 10000; i++ )
        {   //invoke getRandom() static method on AdHocClass
            int res = (int)Java.call( clazz, "getRandom" );
            assertTrue( res >= 0 && res < 100 );
        }            
    }
    
    public void testInstanceMethod()
    {
        _class _c = _class.of( "howto.java.author", 
            "public class MinMaxRandom")
            .imports( Random.class )                
            .field( "public static final Random RANDOM = new Random();" )
            .field( "public final int min;" )
            .field( "public final int max;" )
            .constructor("public MinMaxRandom( int min, int max )",
                "this.min = min;",
                "this.max = max;" )            
            .method( "public int getRandom()", 
                "return RANDOM.nextInt( this.max - this.min ) + this.min;" );
        
        int min = 1;
        int max = 100;
        //compile & load MinMaxRandom, then call the constructor w/ min, max
        Object oneTo100 = _c.instance( min, max );
        
        //test the result of the instance method
        for( int i = 0; i < 10000; i++ )
        {   //invoke the getRandom instance method on oneTo100
            int res = (int)Java.call( oneTo100, "getRandom" );
            assertTrue( res >= min && res < max );
        }          
    }
}
