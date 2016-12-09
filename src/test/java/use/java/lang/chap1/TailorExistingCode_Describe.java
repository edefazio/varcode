package use.java.lang.chap1;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.lang._class;
import varcode.java._Java;

/**
 * Tailoring Java source code is "reading in" existing source code 
 * (into a _class, _interface, _enum langmodel) and changing the langmodel.
 * 
 * This testcase will: 
 * <OL>
 * <LI>"read in" the source / langmodel _class for the Class at runtime 
 * <LI>it will "tailor" or modify the _class and make these changes:
 *  <OL>
 *    <LI>change the class name
 *    <LI>add imports to the class
 *    <LI>add a static LOG field (a Logger)
 *    <LI>add a member "count" field
 *    <LI>add a new static method "staticMessage"
 *    <LI>add a new instance method "countMessage"
 *    <LI>modify the body of the "getCount()" method
 *  </OL>
 * <LI>"author" or write out the .java source code
 * <LI>load an adHocClass for the _class
 * <LI>invoke a static method on the loaded class
 * <LI>create a new adHoc instance of the AdHoc Class
 * <LI>invoke an instance method on the adHocInstance
 * </OL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class TailorExistingCode_Describe
    extends TestCase
{    
    public int getCount()
    {        
        return 100; //tailor this to return a member field value
    }
    
    public void testTailorAndUseAdHoc_class()
    {   
        /* 1: Load the _class for the Class at runtime */
        _class _c = _Java._classFrom(TailorExistingCode_Describe.class );
        
        /* 2: change the _class name */
        _c.setName( "ChangeThyself" );
        
        // 2a: add imports
        _c.imports( Logger.class, LoggerFactory.class );        
        
        // 2b: add a LOG static field
        _c.field( "public static final Logger LOG = " + 
            "LoggerFactory.getLogger( ChangeThyself.class );" );
        
        // 2c: add a member field
        _c.field( "private int messagesThisInstance = 0;" );
        
        // 2d: add a new static method
        _c.method( "public static String staticMessage( String message )", 
            "LOG.info( message );", 
            "return message;" );
        
        // 2e: add a new instance method
        _c.method( "public int countMessage( String message )", 
            "LOG.info( message );",
            "return ++messagesThisInstance;" );
        
        // 2f: change the body of the getCount() instance method
        _c.getMethodNamed( "getCount" )
            .setBody( "return messagesThisInstance;" );
        
        /* 3: Author .java source code after changing the _class */
        System.out.println( _c.author() );
        
        /* 4: Load an AdHocClass based on the modified _class langmodel */
        Class adHocClass = _c.loadClass();
        
        /* 5: Invoke a static method on the adHocClass  */
        _Java.invoke( adHocClass, "staticMessage", "Hello" );
        
        /* 6: Compile/load/instantiate a new adHocInstance from the _class */
        Object adHocInstance = _c.instance();
        
        /* 7: invoke an instance method on the AdHocInstance */
        assertEquals( 1, _Java.invoke( adHocInstance, "countMessage", "Msg1" ) );
        assertEquals( 1, _Java.invoke( adHocInstance, "getCount" ) );
        assertEquals( 2, _Java.invoke( adHocInstance, "countMessage", "Msg2" ) );
        assertEquals( 2, _Java.invoke( adHocInstance, "getCount" ) );
    }    
}
