package howto.java.author;

import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.Workspace;
import varcode.java.metalang._class;
import varcode.java.metalang._code;
import varcode.java.metalang._interface;
import varcode.java.metalang._methods;
import varcode.java.metalang._methods._method;
import varcode.java.metalang._modifiers;

/**
 *
 * @author Eric
 */
public class AuthorJavaClass 
    extends TestCase
{
    public void testAuthor()
    {
        _class _c = _class.of(
            "howto.java.author",
            "public abstract class BaseClass" );
        
        //adding methods to the _class
        // the method(...) operation will parse the string(s) to
        // create a _method and add it to the _class
        
        //abstract (no body method)
        _c.method( "public int getCount();" );
        
        //method that throws exception (s)
        _c.method( "String getId( int count ) throws IOException" );
        
        _c.method( 
            "int count( ) throws FileNotFoundException, ReflectiveOperationException" );
        
        //method with body
        _c.method("public String toString()",
            "return \"String\";" );
        
        //method with mulitple body lines
        _c.method("public String append()",
            "int i = 100;", //each body line
            "String s = \"ERic\";",
            "return s + \" \" + i;");
        
        //method with a javadoc and body
        _c.method( "MethodComment", 
                "public void sayHi()", 
            _code.of( "System.out.println( \"Hello World\");" ) );
        
        // there is a custom mainMethod method, to easily create 
        // public static void main( String[] args ) {...}
        _c.mainMethod( "System.out.println(\"Hello Main Method!\");" );
        
        
        //or you can create an "independent" method
        _method _m = _method.of( "public abstract int getNumber()" );        
        _c.method( _m ); //and manually add it to the class
        
        
        //adding int fields
        _c.field( "int a;" ); //type / name
        _c.field( "public int b;" ); // modifiers type name
        _c.field( "public static int c;" ); //static fields
        _c.field( "protected final int d = 100;" ); //field w/ initializer
        
        _c.field( "field comment",  "int e;" ); //field with comment
        
        _c.instance( );
        
    }
    
    //a "group" of methods is stored in a _methods
        //_methods _ms = new _methods();
        //_ms.addMethod( _m );
        
        //add fields
    /*
    _interface _anInterface = _interface.of(
            "howto.java.author",
            "public interface AnInterface" );
        
        _class _theClass = _class.of(
            "howto.java.author",
            "public class AuthorClass extends BaseClass implements AnInterface" );
        
        AdHocClassLoader cl = 
            Workspace.compileNow( _baseClass, _anInterface, _theClass );        
    */
    
}
