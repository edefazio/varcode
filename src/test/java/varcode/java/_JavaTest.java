package varcode.java;

import java.lang.reflect.Method;
import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.Dom;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacOptions;
import varcode.markup.bindml.BindML;

public class _JavaTest
    extends TestCase
{
    public void testComposeAndLoadClassNoMarks()
    {
	//System.out.println( _Java.describeEnvironment() );
	Dom emptyMarks = BindML.compile( "public class A { }" );
	AdHocJavaFile javaCode = 
            _Java.author( "A", emptyMarks, VarContext.of( ) );
		
	Class<?> clazz = _Java.loadClass( javaCode );
	assertEquals( "A", clazz.getName() );
		
	//now try with some compiler options (compile with Java 1.3 compatibility)
	clazz = _Java.loadClass( 
            javaCode, 
            JavacOptions.Flags.ALL_DEBUG_INFORMATION,  
            JavacOptions.SourceVersion.MajorVersion._1_3 );
            assertEquals( "A", clazz.getName() );		
    }
    
    
    public static String simpleNoArgMethod()
    {
	return "HEY";
    }
	
    public static String simpleOneArgMethod( String arg)
    {
	return "HEY" + arg;
    }
	
    public int instanceMethod()
    {
	return 1;
    }
	
    public int instanceMethodWithArg( int arg )
    {
	return arg + 10;
    }
	
    public String instanceMethodWithVarArgs(String...args)
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < args.length; i++ )
        {
            sb.append( args[ 0 ] );
        }
        return sb.toString();
    }
    
	public void testGetMethod()
	{
            Method m = _Java.getMethod( 
		this.getClass().getMethods(), "instanceMethod" );
		
            assertNotNull( m );
		
            m = _Java.getMethod( 
		this.getClass().getMethods(), "instanceMethodWithArg", 10 );
		
            assertNotNull( m );
	}
    
    /*
    public void testInvokeVarArgs()
    {
        assertEquals( "A", Java.invoke(  this, "instanceMethodWithVarArgs", "A" ) );
    }
    */
    
    public void testInvokeMethod()
    {
	assertNotNull( JavaReflection.describeEnvironment() );
		
	assertEquals( "HEY", 
            _Java.invokeStatic( _JavaTest.class, "simpleNoArgMethod" ) );
		
	assertEquals( "HEYarg", 
            _Java.invokeStatic( _JavaTest.class, "simpleOneArgMethod", "arg" ) );
		
	assertEquals( 1, 
            _Java.invoke( this, "instanceMethod" ) );
		
        assertEquals( 11, 
            _Java.invoke( this, "instanceMethodWithArg", 1 ) );
    }	
}

