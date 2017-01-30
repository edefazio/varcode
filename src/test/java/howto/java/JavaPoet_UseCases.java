package howto.java;

import varcode.author.Author;
import varcode.java.model._anonClass;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._fields;
import varcode.java.model._interface;
import varcode.java.model._literal;
import varcode.java.model._methods._method;
import varcode.java.model._parameters;

/**
 * JavaPoet is an open source model-based Java code generation API by 
 * square, (which is synonymous to varcode.java.model) 
 * 
 * we extracted JavaPoet the Use Cases defined from here:
 * <A HREF="https://github.com/square/javapoet">JavaPoet</A>
 * and adapted them in "varcode.java.model" to test the intuitiveness of the API 
 * 
 */
public class JavaPoet_UseCases
{
    
    public static void main( String[] args )
    {
        System.out.println(  HelloWorld() );
        
        constructor_Model();
        interface_Model();
        abstractMethod_Model();
        enum_Model();
        method_Model();
        //methodParameterized_Model();
        
        parametersMethod_Model();
        
        fields_Model();
    }
    
    
    public static _class HelloWorld()
    {
        return _class.of("package com.example.helloworld;",
            "public final class HelloWorld")
            .mainMethod( "System.out.println( \"Hello varcode!\" );" );
    }
    
    public static _fields fields_Model()
    {
        _fields _fs = _fields.of(
            "int a;",
            "String name;",
            "Map<String,Integer> nameCountMap;",
            "int[] countArray;",
            "private final String android = \"Lollipop v.\" + 5.0;",
            "private final String robot;" );
        
        return _fs;        
    }
    
    public static _class constructor_Model( )
    {
        _class _c = _class.of( "public class HelloWorld" )
            .field( "public String greeting;" )
            .constructor( "public HelloWorld( String greeting )",
                "this.greeting = greeting;" );
        return _c;
        //System.out.println( _c );
    }
    
    public static _interface interface_Model()
    {
        _interface _i =  _interface.of( 
            "public interface HelloWorld")
            .field( "String ONLY_THING_THAT_IS_CONSTANT = \"change\";" )
            .method( "void beep();" );        
        //System.out.println( _i );
        return _i;
    }
    
    public static _enum enum_Model()
    {
        _enum _e = _enum.of("public enum Roshambo")
            .constant( "ROCK" ) 
            .constant( "PAPER" ) 
            .constant( "SCISSORS" );
        return _e;
    }
    
    public static _parameters parametersModel()
    {
        return _parameters.of("final String android, final String robot" );
    }
    
    
    public static _method parametersMethod_Model( )
    {
        _method _m = 
            _method.of( 
                "void welcomeOverlords(final String android, final String robot)" );
        return _m;
    }
 
    
    
    /**
     * An alternative to directly building the model
     * @return 
     */
    public static _method method_Model()
    {
        _method _m = _method.of( "public void main()",
            "int total = 0;",                
            "for( int i = 0; i < 10; i++ )",
            "{",
            "    total +=i;",
            "}" );
        
        //System.out.println( _m );
        return _m;
    }
    
    public static _anonClass anonymousClass_Model()
    {
        _anonClass _ac = _anonClass.of( "AbsClass<String>")
            .args( _literal.of("A"), 1 )
            .fields( "protected String a;",
                "protected int b;" )
            .method( "public void doIt()",
                "System.out.println(\"Hello\");" );
        
        //System.out.println( _ac );
        return _ac;
    }
    
    public static _class abstractMethod_Model( )
    {
        _class _c = _class.of("public abstract class HelloWorld")
            .method("public abstract void flux();" );
        
        //System.out.println( _c );
        return _c;
    }

}
