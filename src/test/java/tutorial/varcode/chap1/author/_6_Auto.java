package tutorial.varcode.chap1.author;

import java.util.Date;
import junit.framework.TestCase;
import varcode.java.JavaCase;
import varcode.java.code._class;
import varcode.java.code.auto._autoDto;
import varcode.java.code.auto._autoEnum;
import varcode.java.code.auto._autoExternalizable;
import varcode.java.code.auto._autoToString;

/**
 * _auto... classes obviate the need for repetitive (boilerplate) type coding.
 * <UL>
 *   <LI>_autoToString generates a ToString method for printing the state of an object
 *   <LI>_autoGetter generates Getter Methods for fields on a class /enum
 *   <LI>_autoSetter generates Setter methods for fields on a class /enum
 *   <LI>_autoDto simplifies the creation of DataTransferObjects 
 *     (private fields, public getters and setters)
 *   <LI>_autoExternalizable causes a class to implement Externalizable and implements
 *     the appropriate readExernal/ writeExternal methods (and imports the appropriate
 *     classes)
 * </UL>
 * or generating a ToString() method for 
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class _6_Auto
    extends TestCase
{
    public void testAutoDtoToStringExternalizable()
    {
        _class person = 
            _autoDto.of( "tutorial.varcode.chap1.author.MyDto" )
            .property( String.class, "name" )
            .property( "public int[] numbers = {1,2,3,4,5};")        
            .property( Date.class, "dob" )     
            .toClassModel();
        
        person = _autoToString.of( person );
        person = _autoExternalizable.of( person );
        
        System.out.println( person );
        System.out.println( person.instance( ) );        
    }
    
    public void testAutoEnum()
    {
        _autoEnum ae = new _autoEnum( "tutorial.varcode.chap1", 
            "MyColorPalette" );
        ae.property("private final String name");
        ae.property("private final int ARGB");
        
        ae.value("RED", "\"red\"", 0x00FF0000 );
        ae.value("GREEEN", "\"green\"", 0x0000FF00 );
        ae.value("BLUE", "\"blue\"", 0x000000FF );
        
        JavaCase jc = ae.toJavaCase( );
        
        jc.loadClass( );        
    }
    
    //concepts    
    //1) _autoDto simplifies the creation of Data Transfer Objects (it generates
    //   private fields, and public getter/setter methods)
    //2) _autoToString creates a toString method for printing out the state of
    //   an object (it will also print out an array as "[1,2,3,4,5]" and not "Lint[]...")
    //3) _autoExternalizable will update the class and create the appropriate 
    //   readExternal and writeExternal methods
    //4) _autoEnum is similar to _autoDto, but is focused on simplifiying the 
    //   creation of _enums rather than DTO _classes.
}
