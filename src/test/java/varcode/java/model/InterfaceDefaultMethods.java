/*
 * To change this license header, choose License Headers in JavaSourceFolder Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.JavaSourceFolder;
import varcode.java.model._class;
import varcode.java.model._interface;

/**
 * This is an integration Test that tries to test out 
 * features and real-world situations for 
 * using varcode to authoring and using AdHoc code.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class InterfaceDefaultMethods
     extends TestCase
{
    /** Interface with a Default method and abstract method */
    public static _interface THE_INTERFACE = _interface.of(
        "howtojava.author.detail", 
        "public interface TheInterface" )
        .field( "public static final String INTERFACE_FIELD = \"INTERFACE_FIELD\";" )
        .imports( UUID.class )
        .defaultMethod( 
            "public default String getID()", 
            "return UUID.randomUUID().toString();")
        .method( "public void printMessage()" );
    
    /** Class implementing the interface and implementing printMethod() */
    public static _class THE_IMPLEMENTER = _class.of(
        "howtojava.author.detail", 
        "public class Implementer implements TheInterface")
        .method( 
            "public void printMessage()", 
            "System.out.println (\"HELLO \"+ getID() );" )
        .constructor(
            "public Implementer()", 
            "System.out.println(\"in Implementer Constructor\");" );
    
    /**
     */
    public void testImplementingAnInterfaceAndCallingDefaultMethod()
    {
        //compile the interface with default method 
        //and the implementer of the interface
        AdHocClassLoader adHocClassLoader = 
            AdHoc.compile( THE_INTERFACE, THE_IMPLEMENTER );
            //Workspace.compileNow( THE_INTERFACE, THE_IMPLEMENTER );
        
        Class implc = adHocClassLoader.findClass( THE_IMPLEMENTER );
        Object implObject = Java.instance( implc );
        //calls the default method on the implementation (defined on the interface)
        assertNotNull( Java.call( implObject, "getID" ) );        
    }
    
    /** 
     * Class implementing the interface 
     * OVERRIDING the DEFAULT METHOD getID()
     */
    public static _class THE_IMPLEMENTER_OVERRIDE = _class.of(
        "howtojava.author.detail", 
        "public class ImplementerOverride implements TheInterface")
        .method( //must implement this method 
            "public void printMessage()", 
            "System.out.println (\"HELLO \"+ getID() );" )
        .method( //override the default getID() method
            "public String getID()",
            "return \"OVERRIDE\";")    
        .constructor(
            "public ImplementerOverride()", 
            "System.out.println(\"in ImplementerOverride Constructor\");" );
    
    /** 
     */
    public void testOverrideDefaultMethod()
    {
        //compile the interface with default method 
        //and the implementer of the interface
        AdHocClassLoader adHocClassLoader = 
            AdHoc.compile( THE_INTERFACE, THE_IMPLEMENTER_OVERRIDE );
        
        Class implc = adHocClassLoader.findClass( THE_IMPLEMENTER_OVERRIDE );
        Object implObject = Java.instance( implc );
        //calls the overridden method on the implementation (defined on the interface)
        assertEquals( "OVERRIDE", Java.call( implObject, "getID" ) );        
    }
    
    
    
    public static void main( String[]args )
    {
        new InterfaceDefaultMethods().testImplementingAnInterfaceAndCallingDefaultMethod();
        
        new InterfaceDefaultMethods().testOverrideDefaultMethod();
        
    }
}