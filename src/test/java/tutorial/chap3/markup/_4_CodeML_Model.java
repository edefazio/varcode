package tutorial.chap3.markup;
/*{-*/
import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.JavaCase;
import varcode.java.metalang._class;
import varcode.java.metalang._code;
import varcode.java.metalang._enum;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._interface;
import varcode.java.metalang._methods._method;/*-}*/
/*{$$condenseBlankLines$$}*/

/*{- uses a class marked up with CodeML and 
{@code varcode.java.model._method}
to insert a new method into an existing class
-}*/
public class /*{+className*/_4_CodeML_Model/*+}*/ /*{-*/extends TestCase/*-}*/
{
/*{+$>(addFieldHere)+}*/    
/*{+$>(addMethodHere)+}*/
/*{+$>(addNestedClassHere)+}*/
/*{+$>(addNestedEnumHere)+}*/
/*{+$>(addNestedInterfaceHere)+}*/    
    /*{-*/
    public void testAddMethod()
    {
        _method _m = _method.of(
            "Simple ToString method ", 
            "public String toString()", 
            _code.of("return this.getClass().getName().toString();") );
            
        JavaCase thisCase = JavaCase.of( _4_CodeML_Model.class, 
            "ex.varcode.java.code.AddMethodClass", 
            "addMethodHere", _m );
            
        System.out.println( thisCase.toString() );
        assertEquals( "ex.varcode.java.code.AddMethodClass", 
            thisCase.instance( ).toString() );        
    }   
    
    public void testAddField()
    {
        _field _f = _field.of( "public static final int VERSION = 1;" );
        JavaCase thisCase = JavaCase.of(_4_CodeML_Model.class, 
            "ex.varcode.java.code.AddFieldClass", 
            "addFieldHere", _f );
        
        Object instance = thisCase.instance( );
        assertEquals( 1, _Java.getFieldValue( instance, "VERSION" ) ); 
    }
    
    public void testAddNestedClass()
    {
        _class _nestedClass = 
            _class.of( "public static class NestedClass" )
               .field( "private final int id;" )
               .constructor(
                    "public NestedClass(int id)", 
                    "this.id = id;" )
                .method( "public int getId()", "return this.id;" );
        
        JavaCase thisCase = JavaCase.of(_4_CodeML_Model.class, 
            "ex.varcode.java.code.AddNestedClass", 
            "addNestedClassHere", _nestedClass );
        
        //System.out.println( thisCase );
        
        Class theClass = thisCase.loadClass();
        Class[] declaredClasses = theClass.getDeclaredClasses();
        
        //get the declared nested inner class
        assertEquals( 1, declaredClasses.length );
        
        //create an instance of the declared nested class 
        //(pasing in 321 in constructor)
        Object nestedInstance = _Java.instance( declaredClasses[ 0 ], 321 );
        
        //verify getId returns the value passed in via the consturctor
        assertEquals( 321, _Java.invoke( nestedInstance, "getId" ) );                
    }
    
    public void testAddNestedInterface()
    {
        _interface _nestedInterface = 
            _interface.of( "public interface NestedInterface" )
               .field( "public final double PI = Math.PI;" );
        
        JavaCase thisCase = JavaCase.of(_4_CodeML_Model.class, 
            "ex.varcode.java.code.AddNestedInterface", 
            "addNestedInterfaceHere", _nestedInterface );
        
        Class theClass = thisCase.loadClass();
        Class[] declaredClasses = theClass.getDeclaredClasses();
        
        //get the declared nested inner class
        assertEquals( 1, declaredClasses.length );
        
        //create an instance of the declared nested class 
        //(pasing in 321 in constructor)
        //Object nestedInstance = _Java.instance( declaredClasses[ 0 ], 321 );
        
        //verify getId returns the value passed in via the consturctor
        assertEquals( Math.PI, 
            _Java.getFieldValue( declaredClasses[ 0 ], "PI" ) );                
    }
    
    public void testAddNestedEnum()
    {
        _enum _nestedEnum = 
            _enum.of( "public enum Colors" )
                .field( "public final int argb;" )
                .constructor(
                    "Colors( int argb )", 
                    "this.argb = argb;" )
                .value("RED",   0xFF0000 )
                .value("GREEN", 0x00FF00 )
                .value("BLUE",  0x0000FF );
        JavaCase jc = JavaCase.of(_4_CodeML_Model.class,                 
            "ex.varcode.java.code.NestedEnumClass",            
            "addNestedEnumHere", _nestedEnum );
        
        Class topClass = jc.loadClass();
        assertEquals( 1, topClass.getDeclaredClasses().length );
        assertEquals( 3, topClass.getDeclaredClasses()[0].getEnumConstants().length );
    }
    /*-}*/    
}
   /*{-*/
   // concepts:
   // 1) CodeML is based on BindML Marks, but CodeML Marks exist WITHIN 
   //    compile-able (Java,C,C++) code as comments. (this allows the compiler
   //    to ignore the CodeML Marks and compile the code as-is)
   // 2) the /* {- */  /* -} */ marks signify that all content within is 
   //    "cut" from the Dom (and not included in the composed document
   // 3) "/ * { - ... - } * /" is an inline variant of the "cut" tag 
   // 4) "/*{ $ $ <directive> $ $ }*/" represents a pre or post processing directive.
   //    the "standard library directives" are available in "varcode.context.BootStrap"
   //    Preprocessing Directives are run before binding data into the document
   //    PostProcessing Directives are run AFTER all data is bound into the 
   //    composed document
   // 5) "/ * { +<name> * /.../ * + } */" Mark will REPLACE the "..." contents
   //    with the content bound to "<name>"
   // 6) "/ * { + $ <scriptName>(<scriptParameters>) + } * /" Mark will invoke
   //    the script <scriptName> passing in the values <scriptParameters>
   /*-}*/