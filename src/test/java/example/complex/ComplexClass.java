/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.complex;

import javax.annotation.Generated;
import static java.lang.System.*; //static method
import java.util.ArrayList;
import java.util.List;
import varcode.VarException;
import varcode.java.model._javadoc;

/**
 * This ugly monstrosity exists to show How a complex class with crazy 
 * features can be loaded via 
 */
@Deprecated
@Generated("varcode")
public class ComplexClass<K,V>
{
    @NestedAnnotation
    public static volatile int a;
    
    /** Field JavaDoc */
    public volatile String name;
    
    /** Double brace initialization */
    public int[][] points = {{1,2}, {3,4}, {5,6}};
    
    @interface NestedAnnotation
    {
        
    }
    
    @SafeVarargs
    public static void varArgs( @NestedAnnotation @Deprecated int count, String...varargs )
        throws VarException
    {
        for( int i = 0; i < varargs.length; i++ )
        {
            System.out.println( count + " " + varargs[ i ] );
        }
        List<String> values = new ArrayList<String>();
        values.add("a");
        values.add("z");
        values.add("x");
        
        //lambda
        //long c = values.stream().filter( (v) -> v.startsWith( "a" ) ).count();
        
        //System.out.println( c );
        
        //forEach
        for( String v: values ) 
        {
            System.out.println( v );
        }        
    }
    
    static
    {
        /* static block */
        a = new java.util.Random().nextInt();
        varArgs( a, "A", "B" );
    }
    
    /*
    public static void main( String[] args ) 
    {
        out.println( "HI" );
        out.println( a );
        
        try
        {
            _javadoc jd  =
                (_javadoc)Class.forName( "varcode.java.meta._javadoc" ).newInstance();
        }        
        catch( ClassNotFoundException 
            | InstantiationException  
            | IllegalAccessException ex )
        {
            System.out.println( ex );
        }        
    }
*/
}
