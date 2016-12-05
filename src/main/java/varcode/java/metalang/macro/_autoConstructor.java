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
package varcode.java.metalang.macro;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.java.metalang._class;
import varcode.java.metalang._code;
import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._enum;
import varcode.java.metalang._fields;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._javaComponent;

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
 * //now create a constructor with _autoConstructor...()
 * 
 * _c.constructor( _autoConstructor.of( _c ) );
 * 
 * 
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoConstructor
    implements _javaMacro
{    
    /**
     * Returns a constructor based on which <B>final, non-initialized _fields</B>
     * in _c
     * @param _c the model for the class
     * @return a constructor
     */
    public static _constructor of( _class _c )
    {
        return of( _c, _c.getFields() );
    }
    
    /**
     * Returns a constructor based on which <B>final, non-initialized _fields</B>
     * in _e
     * @param _e the model for the enum
     * @return a constructor for the _enum
     */
    public static _constructor of( _enum _e )
    {
        return of( _e, _e.getFields() );
    }
    
    public static _constructor of( _enum _e, _fields _fs )
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
            "private " + _e.getName() + "( " + paramList + " )";
        
        return _constructor.of(constructorSig, _constructorBody );        
    }
    
    /**
     * @param _c the java _class (_class, 
     * @param className the name of the class to construct
     * @param fs member fields of the class
     * @return constructor with all final non-initialized fields being set
     */
    public static _constructor of( _class _c, _fields fs )
    {
         //we need a constructor with all the uninitialized final fields
        //verify that all fields are either
        // nonfinal and they are final With an initializer
        List<_field>uninitializedFinalFields = new ArrayList<_field>();
        
        String[] fieldNames = fs.getFieldNames();
        
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _field f = fs.getByName( fieldNames[ i ] );
            if( f.getModifiers().contains( Modifier.FINAL ) && 
                !f.hasInit() )
            {
                uninitializedFinalFields.add( f );
            }
        }
        
        String paramList = "";
        _code finalInitCode = new _code();
        
        for( int i = 0; i < uninitializedFinalFields.size(); i++ )
        {
            _field f = uninitializedFinalFields.get( i );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            
            finalInitCode.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );
        }
            
        String constructorSig = 
            "public " + _c.getName() + "( " + paramList + " )";
        
        return _constructor.of( constructorSig, finalInitCode );        
    }
}
