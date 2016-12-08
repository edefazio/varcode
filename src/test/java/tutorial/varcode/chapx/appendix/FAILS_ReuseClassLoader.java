package tutorial.varcode.chapx.appendix;

import junit.framework.TestCase;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.metalang._class;
import varcode.java.metalang._interface;

/**
 * TODO At the moment, this  FAILS
 * 
 * i.e. Incremental Compilation
 *  
 * (i.e. it will fail at step 3
 * 1) call the Workspace Javac compiler (with the .java source for Class A
 * 2) build another AdHoc Class (Class B) that depends on Class A
 * 3) Compile Class B (into the same AdHocClassLoader that created Class A)
 * 
 * 
 * //alternatively YOU CAN:
 * 1) create _class A 
 * 2) create _class B that depends on _class A
 * 3) compile a Workspace containing _class A AND _class B
 * 
 * Illustrate how to populate / reuse an AdHocClassLoader.
 * 
 * Author code (TheInterface) then compile code into a ClassLoader 
 * 
 * Author a class that depends on TheInterface and Compile it into the
 * existing AdHocClassLoader
 * 
 * @author Eric
 */
public class FAILS_ReuseClassLoader 
    extends TestCase
{
    public void testReuseClassLoader()
    {
        _interface _i = _interface.of(
             "howto.java.javac", "public interface MyInterface" );
        
        System.out.println( _i );
        
        //load the class (in a new AdHocClassLoader)
        Class interfaceClass = _i.loadClass();
        
        AdHocClassLoader adHocClassLoader =
            (AdHocClassLoader) interfaceClass.getClassLoader();
        
        assertEquals( 1, adHocClassLoader.countLoadedClasses() );
        
        _class _unrelated = _class.of( "howto.java.javac", "public class A" );
        
        Object inst = _unrelated.instance( adHocClassLoader );
        
        assertEquals( 2, adHocClassLoader.countLoadedClasses() );
        
        assertEquals( inst.getClass().getClassLoader(), adHocClassLoader ); 
        
        
        
        _class _impl = _class.of("howto.java.javac", 
            "public class TheImpl implements MyInterface" )
            .imports("howto.java.javac.MyInterface");
        
        System.out.println( _impl.author() );
        
        //Pass in the AdHocClass Loader (since we need the interface class
        // to compile _impl
        Object anInstance = _impl.instance( adHocClassLoader );
        
        assertEquals( 3, adHocClassLoader.countLoadedClasses() );
               
        
        
    }
}
