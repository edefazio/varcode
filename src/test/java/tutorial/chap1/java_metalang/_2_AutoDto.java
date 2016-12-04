package tutorial.chap1.java_metalang;

import java.util.Date;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.metalang._class;
import varcode.java.metalang.macro._autoDto;
import varcode.java.metalang.macro._autoExternalizable;

/**
 * _auto... abstractions are "macro"-like abstractions used to "automate"  
 * more complex interactions within MetaLang models (_class, _enum, _interface). 
 * 
 * _auto... classes obviate the need for repetitive (boilerplate) type coding.
 * <UL>
 *   <LI>_autoToString generates a ToString method for printing the state of an object
 *   <LI>_autoGetter generates Getter Methods for fields on a class / enum
 *   <LI>_autoSetter generates Setter methods for fields on a class / enum
 *   <LI>_autoDto simplifies the creation of DataTransferObjects 
 *     (private fields, public getters and setters)
 *   <LI>_autoExternalizable causes a class to implement Externalizable and implements
 *     the appropriate readExernal/ writeExternal methods (and imports the appropriate
 *     classes)
 * </UL>
 * @author Eric DeFazio eric@varcode.io
 */
public class _2_AutoDto
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger( _2_AutoDto.class );
    
    public void testAutoDto()
    {
        _autoDto _dto = 
            _autoDto.of( "tutorial.varcode.chap1.author.MyDto" )
            .property( String.class, "name" )
            .property( "public int[] numbers = {1,2,3,4,5};")        
            .property( Date.class, "dob" );     
        
        //create/return the _class model for this dto
        _class _personDto = _dto.toClassModel();    
               
        //create Externalizable method for the dto (readExternal, writeExternal)
        _personDto = _autoExternalizable.of( _personDto );
        
        LOG.debug( _personDto.author( ) );
    }
    
    //concepts    
    //1) _autoDto simplifies the creation of Data Transfer Objects (it generates
    //   private fields, and public getter/setter methods)
    //2) _autoToString creates a toString method for printing out the state of
    //   an object (it will also print out an array as "[1,2,3,4,5]" and not "Lint[]...")
    //3) _autoExternalizable will update the class and create the appropriate 
    //   readExternal and writeExternal methods
}
