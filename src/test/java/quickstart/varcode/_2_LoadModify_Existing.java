package quickstart.varcode;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.model.load._Load;

/**
 * the easiest way to get started  with varcode is to
 * <UL>
 * <LI>write a simple class, 
 * <LI>load the _class model from the class
 * <LI>"mutate" the _class model
 * <LI>compile & load the _class model to a new AdHocClass
 * <LI>invoke static methods on the AdHocClass
 * </UL>
 * @author Eric DeFazio
 */
public class _2_LoadModify_Existing
    extends TestCase
{    
    public static class Skeleton
    {
        @Deprecated
        public static void sayHello( )
        {
            
        }
    }    
    
    public void testLoadModel_Mod_Compile_Invoke()
    {
        //loads the model for the Skeleton class
        _class _c = _Load._Model._classOf( Skeleton.class );
        
        _c.setName( "Authored" ); //rename class        
        _c.setModifiers( "public" ); //reset class modifiers
        
        //set the main method body
        _c.getMethodNamed( "sayHello" )
            .body( "System.out.println( \"Hello World!\" );");
        
        //.property adds a field AND getter/setter 
        _c.property( "private String name;" );
        _c.property( "private int count;" );
        
        //compile & load a new AdHocClass from the modified _class model 
        Class adHocClass = _c.loadClass();
        
        //invoke the static sayHello method on the ad hoc class
        Java.invoke( adHocClass, "sayHello" );        
        
        //create a new instance of the class
        Object instance = _c.instance();
        
        //set the name and count on the instance
        Java.invoke( instance, "setName", "Eric" );
        Java.invoke( instance, "setCount", 10 );
        
        //verify the name and count on the instance
        assertEquals( 10, Java.invoke( instance,"getCount" ) );
        assertEquals( "Eric", Java.invoke( instance,"getName" ) );
        
    }
}
