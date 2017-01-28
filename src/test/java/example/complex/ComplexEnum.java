package example.complex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 * This ugly monstrosity exists to show How a complex class with crazy 
 * features can be loaded via 
 */
@Deprecated
@Generated("varcode")
public enum ComplexEnum /** 
 * NOTE:  <A HREF="http://openjdk.java.net/jeps/301"> Genericized Enums in Java 9</A> */
{
    A( "A", new HashMap<Integer,List<String>>(), "a", "b", "c" );
    
    @Deprecated
    private Object a;
    
    /** Field Javadoc */
    @ParamAnnotation
    @Deprecated
    private Object b;
    
    /** Javadoc*/
    private Object c;
    
    @NestedAnnotation
    @SafeVarargs
    private <E extends Enum> ComplexEnum( 
        //generic
        @ParamAnnotation String n, 
        @ParamAnnotation Map<Integer,List<String>>b, 
        @ParamAnnotation String...c )
    {
        this.a = n;
        this.b = b;
        this.c = c;
    }
    
    @interface ParamAnnotation
    {
        
    }
    @interface NestedAnnotation
    {
        
    }
    
    static
    {
        System.out.println( "Hello World from Static block" );
    }
}
