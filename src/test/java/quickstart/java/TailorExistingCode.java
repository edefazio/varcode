package quickstart.java;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.Java;
import varcode.java.langmodel._class;
import varcode.java.load._java;

/**
 * Tailoring Java source code is "reading in" existing source code 
 * (into a _class, _interface, _enum langmodel) and applying some changes.
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class TailorExistingCode
    extends TestCase
{    
    public int getCount()
    {        
        return 100; //tailor this to return a member field value
    }
    
    /**
     * loads the {@code _class} for _1_KnowThyself (this class)
     * at runtime... Verifies the _class model, 
     */
    public void testTailorAndUseAdHoc_class()
    {   
        /* 1: Load the _class for the Class at runtime */
        _class _c = _java._classFrom( TailorExistingCode.class );
        
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
        Java.invoke( adHocClass, "staticMessage", "Hello" );
        
        /* 6: Compile/load/instantiate a new adHocInstance from the _class */
        Object adHocInstance = _c.instance();
        
        /* 7: invoke an instance method on the AdHocInstance */
        assertEquals( 1, Java.invoke( adHocInstance, "countMessage", "Msg1" ) );
        assertEquals( 1, Java.invoke( adHocInstance, "getCount" ) );
        assertEquals( 2, Java.invoke( adHocInstance, "countMessage", "Msg2" ) );
        assertEquals( 2, Java.invoke( adHocInstance, "getCount" ) );
    }    
}
