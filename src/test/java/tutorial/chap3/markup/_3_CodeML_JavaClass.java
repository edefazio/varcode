package tutorial.chap3.markup;

/*{-*/
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.JavaCase;/*-}*/
/*{+imports+}*/

public class /*{+className*/_3_CodeML_JavaClass/*+}*/ /*{-*/extends TestCase/*-}*/
{
    public static String genId = "{+genId*+}";
    
    
    /*{-*/
    //everything inside of this markis Not part of the template
    public void testCodeML()
    {
        JavaCase thisCase = JavaCase.of(_3_CodeML_JavaClass.class, 
            "tutorial.varcode.chap2.markup.AddNestedClass", 
            "className", "AddNestedClass",
            "genId", UUID.randomUUID() );
        
        Object instance = thisCase.instance( );
        
        assertNotNull( Java.getFieldValue( instance, "genId" ) );
        
    }
    
    public static void main( String[]args )
    {
        new _3_CodeML_JavaClass().testCodeML();
    }
    /*-}*/        
}
