/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.complex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Generated;

/**
 * Here I create a complex UGLY (but compileable) interface to
 * ensure that the conversion from:
 * 
 * compileable String(code) -> AST -> _interface
 * 
 * ...works. 
 * 
 * Verify that things like:
 * 
 * annotations on paramenters
 * Generic Type declarations
 * default methods
 * varargs
 * 
 * etc. works
 * 
 * @author Eric
 */
@Deprecated
@Generated("Blah")
public interface ComplexInterface<K>
{
    void simple();
    
    /**
     * Nested Inner Class
     */
    static class NestedClass
    {
        @Deprecated
        public int ID = 100;
        public String rand = UUID.randomUUID().toString();
        
    }
    /* Temporarily commented out to compile in Java 6
    @Generated("example")    
    @Deprecated
    default <E extends Enum> String aDefaultMethod( 
        @InnerAnn final int a, 
        Map<String, List<Integer>>gen, 
        @ParamAnn @InnerAnn final String...varArgs )
        throws IOException, FileNotFoundException
    {
        System.out.println( "Some Code Here");
         comment 
        //comment
        System.out.println("Well, I gotta return somethign" );
        return "E";
    }
    */
    
    @interface ParamAnn
    {
        
    }
    @interface InnerAnn
    {
        
    }    
}
