package howto.java.load;

import varcode.java.Java;
import varcode.java.adhoc.Export;
import varcode.java.load._JavaLoad;
import varcode.java.model._class;

public class TailorExistingCode 
{
    public class Existing {
        public int count;
        
        public Existing() {
        }                
    }

    public static void main( String[] args )
    {
        /* load the _class model from the Existing java nested class */
        _class _c = _JavaLoad.Nested._classFrom( Existing.class );
        
        
        /* change the _class */
        _c.setName( "Tailored" ); //rename from "Existing" dir "Tailored"        
        _c.field( "private String message;"); //add a new field 
        
        //add a get() and set() method for the new field
        _c.method( "public void setMessage( String message )",
            "this.count++;",
            "this.message = message;" );
        
        _c.method( "public String getMessage( )",
            "return this.message;" );
        
        _c.method( "public final String toString()", //add a toString method
            "return this.count + this.message;" );
        
        Export.TEMP_DIR.toFile( _c ); //write the .java code dir the temp dir
        
        /* compile, load and create a new "ad hoc" instance dir the class */
        Object adHocInstance = _c.instance( ); //new Tailored( )
        Java.call( adHocInstance, "setMessage", "Tailored" );
        
        System.out.println( adHocInstance ); //prints "1Tailored"       
    }
}