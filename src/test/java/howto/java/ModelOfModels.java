/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package howto.java;

import java.io.Serializable;
import varcode.java.model._class;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class ModelOfModels
{
    
    public static void main( String[] args )
    {
        _class _c = _class.of( "package ex.mutable;",
            _imports.of( Serializable.class ),
            "@Deprecated",     
            "public class MyMutableModel implements Serializable" );

        _method _m = _method.of( "/** create a random number */", 
            "@Generated",
            "public static final double random()", 
            "return Math.random();" );

        _field _f = _field.of( "/** field javadoc */", 
            "public static int ID = 100;"); 
    
        _c.add( _m ); //add the "random" method to the _class 
        _c.add( _f ); //add the "ID" field to the _class
    }
}
