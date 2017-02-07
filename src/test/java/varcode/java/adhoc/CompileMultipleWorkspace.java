package varcode.java.adhoc;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;

/**
 * Projects can author/tailor multiple source units 
 * (classes, enums, interfaces) that must be compiled
 * together.  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CompileMultipleWorkspace 
    extends TestCase
{
    public static _interface _IDGEN = _interface.of( 
        "use.java.langmodel", "public interface IdGen extends Serializable" )
        .imports( Serializable.class )
        .method( "String genId();" );
    
    //Class dependent on the IdGen interface
    public static _class _UUIDGEN = _class.of( 
        "use.java.langmodel", "public class UUIDGen implements IdGen" )
        .imports( UUID.class )
        .method( "public String genId()", 
            "return UUID.randomUUID().toString();" );
    
    //Enum dependent on the IdGen interface
    public static _enum _RANDOMGEN = _enum.of( 
        "use.java.langmodel", "public enum RandomGen implements IdGen" )            
        .constant( "INSTANCE" )
        .imports( Random.class, Math.class )
        .field( "private final Random rand;")
        .constructor( "private RandomGen()",
            "this.rand = new Random();" )
        .method( "public String genId()", 
            "return \"\" + Math.abs( this.rand.nextLong());" );
    
    public void testCompileMultiple() throws ClassNotFoundException
    {
        //compile all (3) classes into an new AdHocClassLoader
        AdHocClassLoader adHocClassLoader = 
            AdHoc.compile( _IDGEN, _UUIDGEN, _RANDOMGEN );       
        
        Object uuidGenInstance = //create instance of the class
            Java.instance( adHocClassLoader.findClass( _UUIDGEN ) );
        
        System.out.println( "ID = " + Java.call( uuidGenInstance, "genId" ) );
        
        Class randomGenEnum = adHocClassLoader.findClass( _RANDOMGEN );
        //get the INSTANCE field/constant
        Object enumINSTANCE = Java.getFieldValue( randomGenEnum, "INSTANCE" );
        //
        System.out.println( "ID = " + Java.call( enumINSTANCE, "genId" ) );        
    }
}
