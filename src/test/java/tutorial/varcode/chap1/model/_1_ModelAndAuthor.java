
package tutorial.varcode.chap1.model;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;

/**
 * Create a "_class, _enum, _interface" Model and have the model "author" 
 * its .java source code.
 * 
 * _class, _enum, _interface objects model entities at the source code level.  
 * They provide APIs for manipulating and building source code (Instead
 * of maintaining a large in memory String to represent the source code).  
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class _1_ModelAndAuthor
    extends TestCase
{    
    private static final Logger LOG = 
        LoggerFactory.getLogger( _1_ModelAndAuthor.class );
    
    static _interface _idInterface = _interface.of(
        "tutorial.varcode.chap1.model",
        "public interface CreatesId extends Serializable" )
        .imports( Serializable.class )
        .method( "String createId()" );
    
    public void testAuthorInterface()
    {
        LOG.debug( _idInterface.author( ) );        
    }
    
    static _class _prefixIdClass = _class.of( 
        "tutorial.varcode.chap1.model",
        "public class PrefixId implements CreatesId" )
        .imports( UUID.class )
        .field( "public final String prefix;" )
        .constructor( "public PrefixId( String prefix )",
            "this.prefix = prefix;" )    
        .method( "public String createId()",
            "return this.prefix + ", 
            "    UUID.randomUUID().toString();" );
        
    public void testAuthorClass()
    {
        LOG.debug( _prefixIdClass.author( ) );
    }
    
    static _enum _idEnum = _enum.of( 
        "tutorial.varcode.chap1.model", 
        "public enum IdEnum implements CreatesId" )
        .field( "public static final Random RANDOM = new Random();" )                
        .value( "INSTANCE" )
        .method( "public String createId()",
            "UUID.randomUUID().toString();" ) 
        .method( "public static final int randomInt()", 
            "return RANDOM.nextInt();" )
        .imports( Random.class, UUID.class );
    
    public void testAuthorEnum()
    {
        LOG.debug( _idEnum.author( ) );        
    }
    
    
    public void testInterfaceWithNestedClass()
    {
        //create a clone of the static instance
        _interface _topInterface = _interface.cloneOf( _idInterface );
        
        //nest the prefixIdClass "inside" the interface
        _class _nestedClass = _class.cloneOf( _prefixIdClass );
        
        //set the nested class to be public static
        _nestedClass.getSignature().setModifiers( Modifier.PUBLIC, Modifier.STATIC );
        _topInterface.nest( _nestedClass );
        LOG.debug( _topInterface.author( ) );
    }
    
    //concepts:
    // 1) we use the "_" prefix for "model" of an entity
    //    _class : the model of a class
    //    _enum : the model of an enum
    //    _interface : the model of an interface
    // 1) JavaCase is the source code of a _class/_interface/_enum model
    // 2) _class represents the code model for a Java class, the string passed 
    //    in the constructor("public class Authored") is parsed, and the _class 
    //    _signature is created with the name and modifiers. 
    // 3) .method adds a _method to the _class model, it parses the _method
    //    signature from the first line: "public static String createId()": 
    //     (the modifiers, returnType, name, parameters)
    //    any lines after the first are the "body" of the method.
    // 4) .imports(), will add import statement(s) to the _class model
    // 5) calling System.out.println( javacase ); will print the .java code 
    
}
