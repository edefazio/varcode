/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.varcode.java.code;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.code._class;
import varcode.java.code._interface;

/**
 * This is an integration Test that tries to test out 
 * features and real-world situations for 
 * using varcode to authoring and using AdHoc code.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class ModelFeatureTest
     extends TestCase
{
    /** Interface with a Default method and abstract method */
    public static JavaCase THE_INTERFACE = _interface.of(
        "ex.varcode.java.model", 
        "public interface TheInterface" )
        .field( "public static final String INTERFACE_FIELD = \"INTERFACE_FIELD\";" )
        .imports( UUID.class )
        .defaultMethod( 
            "public default String getID()", 
            "return UUID.randomUUID().toString();")
        .method( "public void printMessage()" )
        .toJavaCase();
    
    /** Class implementing the interface and implementing printMethod() */
    public static JavaCase THE_IMPLEMENTER = _class.of(
        "ex.varcode.java.model", 
        "public class Implementer implements TheInterface")
        .method( 
            "public void printMessage()", 
            "System.out.println (\"HELLO \"+ getID() );" )
        .constructor(
            "public Implementer()", 
            "System.out.println(\"in Implementer Constructor\");" )
        .toJavaCase( );
    
    /**
     */
    public void testImplementingAnInterfaceAndCallingDefaultMethod()
    {
        //compile the interface with default method 
        //and the implementer of the interface
        AdHocClassLoader adHocClassLoader = 
            Workspace.compileNow( THE_INTERFACE, THE_IMPLEMENTER );
        
        Class implc = adHocClassLoader.findClass( THE_IMPLEMENTER.getClassName() );
        Object implObject = Java.instance( implc );
        //calls the default method on the implementation (defined on the interface)
        assertNotNull( Java.invoke( implObject, "getID" ) );        
    }
    
    /** 
     * Class implementing the interface 
     * OVERRIDING the DEFAULT METHOD getID()
     */
    public static JavaCase THE_IMPLEMENTER_OVERRIDE = _class.of(
        "ex.varcode.java.model", 
        "public class ImplementerOverride implements TheInterface")
        .method( //must implement this method 
            "public void printMessage()", 
            "System.out.println (\"HELLO \"+ getID() );" )
        .method( //override the default getID() method
            "public String getID()",
            "return \"OVERRIDE\";")    
        .constructor(
            "public ImplementerOverride()", 
            "System.out.println(\"in ImplementerOverride Constructor\");" )
        .toJavaCase( );
    
    /** 
     */
    public void testOverrideDefaultMethod()
    {
        //compile the interface with default method 
        //and the implementer of the interface
        AdHocClassLoader adHocClassLoader = 
            Workspace.compileNow( THE_INTERFACE, THE_IMPLEMENTER_OVERRIDE );
        
        Class implc = adHocClassLoader.findClass( THE_IMPLEMENTER_OVERRIDE.getClassName() );
        Object implObject = Java.instance( implc );
        //calls the overridden method on the implementation (defined on the interface)
        assertEquals( "OVERRIDE", Java.invoke( implObject, "getID" ) );        
    }
    
    
    
    public static void main( String[]args )
    {
        new ModelFeatureTest().testImplementingAnInterfaceAndCallingDefaultMethod();
        
        new ModelFeatureTest().testOverrideDefaultMethod();
        
    }
}