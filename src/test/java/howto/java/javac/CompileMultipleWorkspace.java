package howto.java.javac;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;

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
        .method( "String genId()" );
    
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
    
    public void testCompileWorkspace() throws ClassNotFoundException
    {
        //compile all (3) classes into an new AdHocClassLoader
        AdHocClassLoader adHocClassLoader = 
            Workspace.compileNow( _IDGEN, _UUIDGEN, _RANDOMGEN );       
        
        Object uuidGenInstance = //create instance of the class
            _Java.instance( adHocClassLoader.find( _UUIDGEN ) );
        
        System.out.println( "ID = " + _Java.invoke( uuidGenInstance, "genId" ) );
        
        Class randomGenEnum = adHocClassLoader.find( _RANDOMGEN );
        //get the INSTANCE field/constant
        Object enumINSTANCE = _Java.getFieldValue( randomGenEnum, "INSTANCE" );
        //
        System.out.println( "ID = " + _Java.invoke(enumINSTANCE, "genId" ) );        
    }
}
