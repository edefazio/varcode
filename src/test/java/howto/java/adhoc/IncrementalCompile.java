
package howto.java.adhoc;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocClassPublisher;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class IncrementalCompile
    extends TestCase
{    
    static _class _A = _class.of( "package ex.iter;","public class A" )
            .field("public static final int base = 100;" );
    static _class _B = _class.of("package ex.iter;", 
            "public class B extends A")
            .field( "public static final int derived = 101;" );
    
    /** Verify that we can compile classes _A and _B at the same time */
    public void testCompileAtSameTime()
    {
        AdHocClassLoader adHocCL = AdHoc.compile( _A, _B );
        Class A = adHocCL.findClass( _A );
        Class B = adHocCL.findClass( _B );
        assertNotNull( A );
        assertNotNull( B );
        
        assertEquals( 100, Java.get( A, "base" ) );
        assertEquals( 101, Java.get( B, "derived" ) );        
        
        //
        AdHocClassPublisher.publishToParent( adHocCL );
        
        Class Aprime = adHocCL.findClass(  _A );
        Class Bprime = adHocCL.findClass(  _B );
        
        assertNotNull( Aprime );
        assertNotNull( Bprime );
        
        assertEquals( 100, Java.get( Aprime, "base" ) );
        assertEquals( 101, Java.get( Bprime, "derived" ) );        
        
        assertNotNull( Aprime.getPackage() );
        assertNotNull( Bprime.getPackage() );
        
    }
    
    static _class _C = _class.of( "package ex.iter;","public class C" )
            .field("public static final int base = 100;" );
    static _class _D = _class.of("package ex.iter;", 
            "public class D extends C")
            .field( "public static final int derived = 101;" );
    
    public void testIncrementalCompile()
    {
        AdHocClassLoader adHoc = AdHoc.compile( _C );
        Class c = adHoc.findClass( _C );
        assertNotNull( c );
        
        Package p = c.getPackage(); 
        assertNotNull( p ); 
        assertEquals( "ex.iter", p.getName() );
        
        AdHocClassLoader stage2 = AdHoc.compile( adHoc, _D );
        
        //AdHoc.compile( adHoc, javaFiles, compilerOptions )
    }
    
    public static void main( String[] args )
    {        
        //I should be able to compile both at the same time        
        //compile staggered
        AdHocClassLoader adHocCl = AdHoc.compile( _A );
        
        AdHoc.compile( adHocCl, _B);
        //2) create a subclass of "A"

        //compile using the "A"s classLoader
        //AdHoc.compile( adHocCl, _b );
        
        Class A = adHocCl.findClass( _A );
        Class B = adHocCl.findClass( _B );
        
        Java.get( A, "base" );
        Java.get( B, "base" );
        Java.get( B, "derived" );        
    }
 
    /*
    public void testCompilIntoExistingPackage()
    {
        _class _a = _class.of("package varcode.java;", "public class Ab")
            .method( "public static void callJava()",
                "System.out.println( Java.call( \" \", \"trim\" ) );" );
        AdHocClassLoader adHoc = AdHoc.compile( _a );
        System.out.println( adHoc );
        Class c = adHoc.findClass( _a );
        Java.call( c, "callJava" );
    }
    
    */
}
