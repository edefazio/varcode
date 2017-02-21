/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java.macro;

import java.util.ArrayList;
import java.util.List;
import varcode.author.Author;
import varcode.markup.Template;
import varcode.java.naming.Primitive;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.markup.bindml.BindML;

/**
 * This uses the JDK7 Equals method
 * 
 * @author Eric
 */
public enum _autoEquals
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    public static Template PRIMITIVE_EQUAL = 
        BindML.compile( "this.{+fieldName+} == test.{+fieldName+}" );
    
    
    public static Template OBJECT_EQUAL = 
        BindML.compile( "java.util.Objects.equals( this.{+fieldName+}, test.{+fieldName+} )" );
                
    /**
     * This is the static "PROTOTYPE" equals method
     * we clone and customize this
     */
    private static final _method EQUALS_METHOD = 
        _method.of(
            "@Override",
            "public boolean equals( Object o)",
            "if( o == this ) return true;", 
            "if( ! ( o instanceof $className$ ) )",
            "{",
            "    return false;",
            "}", 
            "$className$ test = ($className$)o;");
    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static _class to( _class _c )
    {
        return _c.method(  of( _c ) );
    }
    
    public static _method of( _class _c )
    {
        _fields _fs = _c.getFields();
        List<_field> eqFields = new ArrayList<_field>();
        for( int i = 0; i < _fs.count(); i++ )
        {
            if( !_fs.getAt( i ).getModifiers().contains( "static" ) )
            {
                eqFields.add( _fs.getAt( i ) );
            }
        }
        return of(_c.getName(), eqFields.toArray( new _field[ 0 ] ) );
    }

    public static _method of( String className, _field... fields )
    {
        //create a clone 
        _method _equals = _method.cloneOf( EQUALS_METHOD );
        
        System.out.println( "EQ "+ _equals );
        
        _equals.replace( "$className$", className );
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        for( int i = 0; i < fields.length; i++ )
        {
            _field _f = fields[ i ];
            if( i > 0 )
            {
                sb.append( System.lineSeparator() );
                sb.append("    && " );
            }
            if( Primitive.contains(  _f.getType() ) )
            {
                sb.append(
                    Author.toString( PRIMITIVE_EQUAL, "fieldName", _f.getName() ) );              
            }
            else
            {
                sb.append( 
                    Author.toString(  OBJECT_EQUAL, "fieldName", _f.getName() ) );
            }            
        }
        sb.append( ";" ); 
        _equals.getBody().addTailCode( sb.toString() );
        return _equals;
    }
    
    public static _method of( String className, _fields _fs )
    {
        return of( className, _fs.asArray() );
    }
   
}
