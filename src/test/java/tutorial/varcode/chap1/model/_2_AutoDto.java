package tutorial.varcode.chap1.model;

import java.util.Date;
import junit.framework.TestCase;
import varcode.java.model._class;
import varcode.java.model.auto._autoDto;
import varcode.java.model.auto._autoExternalizable;
import varcode.java.model.cs._autoToString;

/**
 * _auto... classes create another abstraction layer on top of existing
 * components (_class, _enum, _interface). This simplifies the amount of
 * wiring code needed to model and author "stereotype"-type classes like "DTOs"
 * or data transfer objects.
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
        
        System.out.println( _personDto.author( ) );
    }
    
    //concepts    
    //1) _autoDto simplifies the creation of Data Transfer Objects (it generates
    //   private fields, and public getter/setter methods)
    //2) _autoToString creates a toString method for printing out the state of
    //   an object (it will also print out an array as "[1,2,3,4,5]" and not "Lint[]...")
    //3) _autoExternalizable will update the class and create the appropriate 
    //   readExternal and writeExternal methods
}
