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
public class _1_Load_Mod_Compile_InvokeStatic
    extends TestCase
{    
    public static class Skeleton
    {
        public static void main( String[] args )
        {
            
        }
    }    
    
    public void testLoadModel_Mod_Compile_Invoke()
    {
        _class _c = _Load._Model._classOf( Skeleton.class );
        
        _c.setName( "Authored" ); //rename class        
        _c.setModifiers( "public" ); //reset modifiers
        
        //set the main method body
        _c.getMethodNamed( "main" )
            .body( "System.out.println( \"Hello World!\" );");
        
        //compile & load a new AdHocClass from the modified _class model 
        Class adHocClass= _c.loadClass();
        
        //invoke the static main method on the ad hoc class
        Java.invoke( adHocClass, "main", (Object)new String[0] );            
    }
}
