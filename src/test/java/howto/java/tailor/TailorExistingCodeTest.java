package howto.java.tailor;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;

/**
 * varcode can load and change the .java source code for existing Classes at 
 * runtime. In short we use the term "tailoring" to mean: <BLOCKQUOTE>
 * "loading and modifying the source code for a class, enum, interface".
 * </BLOCKQUOTE> 
 * 
 * After loading a _class with {@code _Java._classFrom}, 
 * we can change it's name, modifiers, fields, methods, etc., 
 * and author / write the modified source, and/or create a new 
 * instance that can be used in an ad hoc manner.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class TailorExistingCodeTest 
    extends TestCase
{
    private static class Existing {
        private final int count;
        
        public Existing(int count) {
            this.count = count;
        }                
    }
    
    public void testTailorClass()
    {   /* load the _class model from an existing Java class */
        _class _c = Java._classFrom( Existing.class );
        
        /* change the _class */
        _c.setName( "Tailored" ); //rename class to "Tailored"
        _c.setModifiers( Modifier.PUBLIC ); //set Tailored as public, non-static
        _c.getField( "count" ) //change count to be private, non-final
            .setModifiers( Modifier.PRIVATE ); 
        _c.method( "public void setCount(int count)", //add a setter for count
            "this.count = count;" );
        _c.method( "public int getCount( )", //add a getter for count
            "return this.count;" );
        
        /* write Java code as String */
        System.out.println( _c.author() ); 
        
        /* compile/load & instantiate a new "Tailored" with 100 as count */
        Object tailoredObj = _c.instance( 100 ); 
        
        // call instance method "getCount" on the Tailored instance 
        assertEquals( 100, Java.call( tailoredObj, "getCount" ) );
        
        // call "setCount" on the Tailored instance to set count to 200
        Java.call( tailoredObj, "setCount", 200 );
        
        //call "getCount" again to verify count is 200
        assertEquals( 200, Java.call( tailoredObj, "getCount" ) );
    }
}
