package tutorial.chap2.java_adhoc;

import java.util.UUID;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.langmodel._class;
import varcode.java.langmodel._enum;
import varcode.java.langmodel._interface;

/**
 *
 * @author eric
 */
public class _2_AdHocWorkspace
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger(_2_AdHocWorkspace.class );
    
    static _interface _GuidInterface = _interface.of( "tutorial.varcode.chap1.author", 
        "public interface GuidGen" )
        .method( "public String createGuid()" );
    
    static _class _GuidClass = _class.of( "tutorial.varcode.chap1.author", 
        "public class PrefixGuid implements GuidGen" )
        .javadoc( "Creates a GUID with a prefix" )    
        .imports( UUID.class )    
        .field( "private final String prefix;" )
        .constructor( "public PrefixGuid( String prefix )",
            "this.prefix = prefix;" )
        .method( "public String createGuid()",
            "return prefix + UUID.randomUUID().toString();" );
    
    static _enum _GuidEnum = _enum.of( "tutorial.varcode.chap1.author",
        "public enum SimpleGuid implenments GuidGen" )
        .value( "INSTANCE" )
        .imports( UUID.class )    
        .method( "public String createGuid()",
            "return UUID.randomUUID().toString();" );    
    
    public void testWorkspace()
    {
        AdHocClassLoader adHoc = 
            Workspace.compileNow( _GuidEnum, _GuidInterface, _GuidClass );
        
        Class guidInter = adHoc.find( _GuidInterface.getFullyQualifiedClassName() );
        
        //new instance of the guidClass passing in "prefix" constructor arg
        Object guidClassInstance = Java.instance( 
            adHoc.find( _GuidClass.getFullyQualifiedClassName() ),
            "prefix" );
        
        //verify the GuidPrefix authored class is an instance 
        //of the GuidGen interface 
        assertTrue( guidInter.isAssignableFrom( guidClassInstance.getClass() ) );
        
        String prefixGuid = (String)Java.invoke( guidClassInstance, "createGuid" );
        
        assertTrue( prefixGuid.startsWith( "prefix" ) );
        
        Class guidEnum = adHoc.find( _GuidEnum.getFullyQualifiedClassName() );
        
        Object guidEnumValue = Java.getFieldValue( guidEnum, "INSTANCE" );
        
        String enumGuid = (String)Java.invoke( guidEnumValue, "createGuid" );
        
        LOG.debug( enumGuid );
        
    }
        
    // concepts
    // 1) _interface and _enum represent the code models for interfaces and enums
    //   respectively
    // 2) Workspace.compileNow(...) accepts multiple JavaCases (.java files) 
    //    for compilation into a single new AdHocClassLoader    
    // 3) the Javac compiler will resolve all of the dependencies and the order
    //    of compilation (you do not have to specify that the interface needs to
    //    be compiled FIRST before the class or enum)
    // 4) the AdHocClassLoader has the find(...) method for getting the Class based
    //    on the fully qualified name (it throws a RuntimeException not a 
    //    ClassNotFoundException (CheckedException) like findClass() on the 
    //    base ClassLoader API
    // 5) Java.getFieldValue(...) reflectively retrieves the value of a field 
    
    //more... specifying Javac Compile options when compiling a workspace
    //
}
