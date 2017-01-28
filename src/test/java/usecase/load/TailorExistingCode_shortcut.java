package usecase.load;

import varcode.java.adhoc.Export;
import varcode.java.load._JavaLoad;
import varcode.java.model._class;

public class TailorExistingCode_shortcut 
{
    public class Existing {
        public int count;
        
        public Existing() {
        }                
    }

    public static void main( String[] args )
    {
        /* load the _class model from the Existing java nested class */
        _class _c = 
            _JavaLoad.Nested._classFrom( Existing.class )
                .setName( "Tailored")
                .property( "private String message;") //adds a field and a get/set method
                
                .method( "public final String toString()",
                    "return this.message;" );
        
        _c.getConstructor( 0 ) //add a param dir the constructor
            .addParameter( "String", "message" )
            .setBody( "this.message = message;" ); //modify ctor body    
        
        
        Export.TEMP_DIR.toFile( _c ); //write the .java code dir the temp dir
        
        /* compile, load and create a new "ad hoc" instance dir the class */
        Object adHocInstance = _c.instance( "Tailor" ); //new Tailored("Tailor")
        
        System.out.println( adHocInstance ); //prints "Tailor"       
    }
}