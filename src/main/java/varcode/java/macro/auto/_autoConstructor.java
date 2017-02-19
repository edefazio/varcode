/*
 * Copyright 2016 eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.macro.auto;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._fields;
import varcode.java.model._fields._field;

/**
 * Given fields on an Object, creates a constructor
 * that accepts all final non-initialized fields.
 * ...
 * 
 * so if I have the following class:
 * <PRE>
 * _class _c = _class.of("MyBean")
 *     .field("public int a;")
 *     .field("private final int b = 100;")
 *     .field("private final int c;");
 * 
 * //create a constructor with _autoConstructor...()
 * _c.constructor( _autoConstructor.of( _c ) );
 * 
 * //will create the constructor:
 * public MyBean( int c )
 * {
 *     this.c = c;
 * }
 * 
 * //NOTE: a constructor param for "a" is not required since "a" is NOT FINAL
 * //NOTE: a constructor param for "b" is not required since "b" FINAL but ALREADY INITIALIZED 
 * 
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoConstructor    
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    public static _class to( _class _c )
    {
        return _c.constructor( of( _c ) );
    }
    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    /**
     * Returns a constructor based on which <B>final, non-initialized _fields</B>
     * in _c
     * @param _c the model for the class
     * @return a constructor
     */
    public static _constructor of( _class _c )
    {
        return of( _c.getName(), _c.getFields() );
    }
    
    /**
     * Returns a constructor based on which <B>final, non-initialized _fields</B>
     * in _e
     * @param _e the model for the enum
     * @return a constructor for the _enum
     */
    public static _constructor of( _enum _e )
    {
        return of( _e.getName(), _e.getFields() );
    }
    
    
    /**
     * builds and returns a default no arg constructor
     * @param _c the class
     * @return a no-arg constructor for the class
     */
    public static _constructor defaultNoArg( _class _c )
    {
        return _constructor.of( "public " + _c.getName() + "()",
            "// default no arg constructor" );
    }
    
    public static _class defaultNoArgTo( _class _c )
    {
        return _c.constructor( defaultNoArg( _c ) );
    }
    
    public static _constructor of( String name, _fields _fs )
    {
        //we need a constructor with all the uninitialized final fields
        //verify that all fields are either
        // nonfinal and they are final With an initializer
        List<_field>uninitializedFinalFields = 
            new ArrayList<_field>();
        
        String[] fieldNames = _fs.getFieldNames();
        
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _fields._field _f = _fs.getByName( fieldNames[ i ] );
            if( _f.getModifiers().contains( Modifier.FINAL ) && 
                !_f.hasInit() )
            {
                uninitializedFinalFields.add( _f );
            }
        }
        
        return ofFields( name, uninitializedFinalFields );
        /*
        String paramList = "";
        
        //this will be the set the field
        _code _constructorBody = new _code();
        for( int i = 0; i < uninitializedFinalFields.size(); i++ )
        {
            _field f = uninitializedFinalFields.get( i );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            
            //initialize the final fields 
            _constructorBody.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );
        }
            
        String constructorSig = 
            "public " + name + "( " + paramList + " )";
        
        return _constructor.of( constructorSig, _constructorBody );   
        */
    }    
    
    public static _constructor ofFields( String name, List<_field> fields )
    {
        String paramList = "";
        _code _constructorBody = new _code();
        for( int i = 0; i < fields.size(); i++ )
        {
            _field f = fields.get( i );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            
            //initialize the final fields 
            _constructorBody.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );
        }
            
        String constructorSig = 
            "public " + name + "( " + paramList + " )";
        
        return _constructor.of( constructorSig, _constructorBody );   
    }
}
