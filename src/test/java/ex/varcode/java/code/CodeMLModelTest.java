package ex.varcode.java.code;
/*{-*/
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.JavaCase;
import varcode.java.code._class;
import varcode.java.code._enum;
import varcode.java.code._fields._field;
import varcode.java.code._interface;
import varcode.java.code._methods._method;/*-}*/
/*{$$condenseBlankLines$$}*/

/*{- uses a class marked up with CodeML and 
{@code varcode.java.model._method}
to insert a new method into an existing class
-}*/
public class /*{+className*/CodeMLModelTest/*+}*/ /*{-*/extends TestCase/*-}*/
{
/*{+$>(addFieldHere)+}*/    
/*{+$>(addMethodHere)+}*/
/*{+$>(addNestedClassHere)+}*/
/*{+$>(addNestedEnumHere)+}*/
/*{+$>(addNestedInterfaceHere)+}*/    
    /*{-*/
    public void testAddMethod()
    {
        _method m = _method.of(
            "Simple ToString method ", 
            "public String toString()", 
            "return this.getClass().getName().toString();" );
            
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.code.AddMethodClass", 
            "addMethodHere", m );
            
        System.out.println( thisCase.toString() );
        assertEquals( "ex.varcode.java.code.AddMethodClass", 
            thisCase.instance( ).toString() );        
    }   
    
    public void testAddField()
    {
        _field f = _field.of( "public static final int VERSION = 1;" );
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.code.AddFieldClass", 
            "addFieldHere", f );
        
        Object instance = thisCase.instance( );
        assertEquals( 1, Java.getFieldValue( instance, "VERSION" ) ); 
    }
    
    public void testAddNestedClass()
    {
        _class nestedClass = 
            _class.of( "public static class NestedClass" )
               .field( "private final int id;" )
               .constructor(
                    "public NestedClass(int id)", 
                    "this.id = id;" )
                .method( "public int getId()", "return this.id;" );
        
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.code.AddNestedClass", 
            "addNestedClassHere", nestedClass );
        
        //System.out.println( thisCase );
        
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
    
    public void testAddNestedInterface()
    {
        _interface nestedInterface = 
            _interface.of( "public interface NestedInterface" )
               .field( "public final double PI = Math.PI;" );
        
        JavaCase thisCase = JavaCase.of(
            CodeMLModelTest.class, 
            "ex.varcode.java.code.AddNestedInterface", 
            "addNestedInterfaceHere", nestedInterface );
        
        Class theClass = thisCase.loadClass();
        Class[] declaredClasses = theClass.getDeclaredClasses();
        
        //get the declared nested inner class
        assertEquals( 1, declaredClasses.length );
        
        //create an instance of the declared nested class 
        //(pasing in 321 in constructor)
        //Object nestedInstance = Java.instance( declaredClasses[ 0 ], 321 );
        
        //verify getId returns the value passed in via the consturctor
        assertEquals( Math.PI, 
            Java.getFieldValue( declaredClasses[ 0 ], "PI" ) );                
    }
    
    public void testAddNestedEnum()
    {
        _enum nestedEnum = 
            _enum.of( "public enum Colors" )
                .field( "public final int argb;" )
                .constructor(
                    "Colors( int argb )", 
                    "this.argb = argb;" )
                .value("RED",   0xFF0000 )
                .value("GREEN", 0x00FF00 )
                .value("BLUE",  0x0000FF );
        JavaCase jc = JavaCase.of( 
            CodeMLModelTest.class,                 
            "ex.varcode.java.code.NestedEnumClass",            
            "addNestedEnumHere", nestedEnum );
        
        Class topClass = jc.loadClass();
        assertEquals( 1, topClass.getDeclaredClasses().length );
        assertEquals( 3, topClass.getDeclaredClasses()[0].getEnumConstants().length );
    }
    /*-}*/    
}
