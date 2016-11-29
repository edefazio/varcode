package tutorial.varcode.chapx.appendix;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.metalang._class;
import varcode.java.metalang._interface;

/**
 *
 * @author Eric DeFazio
 */
public class Chap1_ModelAndAuthorNested 
    extends TestCase
{
    public void testInterfaceWithNestedClass()
    {
        _interface _genId = _interface.of( "tutorial.varcode.chap1.author", 
            "public interface GenId extends Serializable" )
            .defaultMethod( "public default String genId()",
                "return UUID.randomUUID().toString();" )
            .imports( UUID.class, Serializable.class );
        
        _class _genIdImpl = _class.of( "it.doesnt.matter",
            "public class GenIdImpl implements GenId" )
            .field("public static final GenDate = new Date();")
            .imports( Date.class );
        
        //an interface with a nested class (impl)
        _genId.nest( _genIdImpl );
        
        
        Class interfaceClass = 
            _genId.toJavaCase( ).loadClass();
        
        Class nestedImplClass = 
            interfaceClass.getDeclaredClasses()[ 0 ];
        
        //create an instance of the nested class
        Object instance = _Java.instance( nestedImplClass );
        
        //call the genId() default method on the nested Impl class
        String id = (String)_Java.invoke( instance, "genId" );
        
        assertNotNull( id );
        System.out.println( id );
    }
    // concepts
    // 1) defaultMethod(...) creates a default method on an interface, to call
    //    a defaultMethod you must have an implementation class, and compile
    //    with java 8+ compatibility
    // 2) .cloneOf() creates and mutable copy of an _interface/_class/_enum
    // 3) .nest() accepts any _class/_interface/ _enum model. 
    //     the "package declaration" of the nest is repressed, 
    //     any imports for a nest (or any nested decendents) are aggregated 
    //     at the top level _class/_interface or _enum             
}
