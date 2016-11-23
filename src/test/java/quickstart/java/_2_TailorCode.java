package quickstart.java;

import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.lang._class;
import varcode.java.load._java;

/**
 * "Tailoring" .java code in varcode involves
 * <OL>
 * <LI>loading a _class model from existing .java source
 * <LI>mutating the _class model
 * <LI>write or load the _class model as .java source or a dynamic Class 
 * </OL>
 * 
 * @author Eric DeFazio 
 */
public class _2_TailorCode
    extends TestCase
{    
    public static class SourceClass
    {
        public static final String ID = 
            UUID.randomUUID().toString();
        
        public static String greetings( )
        {
            return "class " + SourceClass.class.getName() + " id " + ID;
        }
    }    
    
    public void testLoadModel_Mod_Compile_Invoke()
    {
        //Skeleton is just a garden variety Java Class
        SourceClass s = new SourceClass();
        SourceClass.greetings();

        // 1) find the .java source and load the _class for SourceClass
        //_class _c = _Load._classOf( SourceClass.class );
        //_class _c = _JavaLoader._Class.from( SourceClass.class );
        _class _c = _java._classFrom( SourceClass.class );
        
        // 2) mutate the 
        _c.imports( UUID.class );
        _c.setName( "Tailored" ); //rename class        
        _c.setModifiers( "public" ); //reset class modifiers
        
        //modify the greetings method
        _c.getMethodNamed( "greetings" )
            .body( "return \"Tailored Greeting!\";");
        
        //.property adds a field AND getter/setter 
        _c.property( "private String name;" );
        _c.property( "private int count;" );
        
        //compile & load "Tailored" AdHocClass from the new _class model 
        Class adHocClass = _c.loadClass();
        
        //invoke the static greetings method on the "Tailored" ad hoc class
        assertEquals( "Tailored Greeting!", 
            Java.invoke( adHocClass, "greetings" ) );        
        
        //create a new instance of the "Tailored" class
        Object instance = _c.instance();
        
        //set the name and count on the instance
        Java.invoke( instance, "setName", "Eric" );
        Java.invoke( instance, "setCount", 10 );
        
        //verify the name and count on the instance
        assertEquals( 10, Java.invoke( instance,"getCount" ) );
        assertEquals( "Eric", Java.invoke( instance,"getName" ) );
        
    }
}
