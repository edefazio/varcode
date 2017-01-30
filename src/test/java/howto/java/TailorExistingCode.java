package howto.java;

import varcode.java.Java;
import varcode.java.model._class;

public class TailorExistingCode 
{
    private static class Existing {
        public int count;
        
        public Existing(int count) {
            this.count = count;
        }                
    }

    public static void main( String[] args )
    {
        /* load the _class model from the Existing java nested class */
        _class _c = Java._classFrom( Existing.class );
        
        /* change the _class model */
        _c.setName( "Tailored" ); //rename class from "Existing" to "Tailored"
        _c.setModifiers( "public" ); //set Tailored as public, non-static
        _c.getField( "count" ) //change the count field ... 
            .setModifiers( "private", "final" ) //to be private and final
            .setName( "message" ) //rename "count" to "message"    
            .setType( "String" ); //change "message" type to be type String                
        _c.getConstructors().getAt( 0 ) //get first/ only constructor
            .setBody( "this.message = message;" ) //change the constructor body
            .getParameters().getAt( 0 ) //get first parameter
            .setType( "String" ) //change constructor parameter to be String     
            .setName( "message" ); //change param name from "count" to "message"     
            
        _c.method( "public final String toString()", //add a toString
            "return this.message;" );
        
        System.out.println( _c.author() ); //write the code 
        
        Object tailoredObj = _c.instance( "Tailor" ); //new Tailored("Tailor")
        
        System.out.println( tailoredObj ); //prints "Tailor"       
    }
}
