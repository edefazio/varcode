package ex.varcode.java.model;
/*{-*/
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.model._class;
import varcode.java.model._fields.field;
import varcode.java.model._methods._method;/*-}*/
/*{$$condenseBlankLines$$}*/

/*{- uses a class marked up with CodeML and 
{@code varcode.java.model._method}
to insert a new method into an existing class
-}*/
public class /*{+className*/CodeMLModelTest/*+}*/    
    /*{-*/extends TestCase/*-}*/
{
/*{+$>(addFieldHere)+}*/    
/*{+$>(addMethodHere)+}*/
/*{+$>(addNestedClassHere)+}*/
    /*{-*/
    public void testAddMethod()
    {
        _method m = _method.of(
            "Simple ToString method ", 
            "public String toString()", 
            "return this.getClass().getName().toString();" );
            
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.model.AddMethodClass", 
            "addMethodHere", m );
            
        assertEquals("ex.varcode.java.model.AddMethodClass", 
            thisCase.instance( ).toString() );        
    }   
    
    public void testAddField()
    {
        field f = field.of( "public static final int VERSION = 1;" );
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.model.AddFieldClass", 
            "addFieldHere", f );
        
        Object instance = thisCase.instance( );
        assertEquals( 1, Java.getFieldValue( instance, "VERSION" ) ); 
    }
    
    public void testAddNestedClass()
    {
        _class nestedClass = 
            _class.of("public static class NestedClass")
               .field("private final int id;")
               .constructor(
                    "public NestedClass(int id)", 
                    "this.id = id;" )
                .method("public int getId()", "return this.id;");
        
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.model.AddNestedClass", 
            "addNestedClassHere", nestedClass );
        
        Class theClass = thisCase.loadClass();
        Class[] declaredClasses = theClass.getDeclaredClasses();
        
        //get the declared nested inner class
        assertEquals( 1, declaredClasses.length );
        
        //create an instance of the declared nested class 
        //(pasing in 321 in constructor)
        Object nestedInstance = Java.instance( declaredClasses[ 0 ], 321 );
        
        //verify getId returns the value passed in via the consturctor
        assertEquals(321, Java.invoke( nestedInstance, "getId" ) );                
    }
    /*-}*/    
}
