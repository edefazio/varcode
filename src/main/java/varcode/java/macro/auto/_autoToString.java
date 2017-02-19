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
package varcode.java.macro.auto;

import java.util.List;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;

/**
 * Rough draft of a ToString generator
 * given a _class model creates a toString method based on the fields 
 * <PRE>
 * class MyClass{
 *    int a = 1;
 *    String name = "eric";
 * }
 * ---prints :
 * MyClass
 *     a = 1
 *     name = eric
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _autoToString
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    /**
     * Generate the appropriate toString() _method
     * and then "port" the method to the _c (class Meta Lang Model)
     * @param _c
     * @return 
     */
    public static _class to( _class _c )
    {
        //portMethod _pm = of( _c.getFields() );
        _method _m = of( _c.getFields() );
        List<_method> _oldToString = _c.getMethodsNamed( "toString" );
        for( int i = 0; i < _oldToString.size(); i++ )
        {
            if( _oldToString.get( i ).getParameters().count() == 0 )
            {   //remove the existing toString if one exists
                _c.getMethods().remove( _oldToString.get( i ) );
            }
        }
        _c.method( _m );
        //return Port.portForce( _pm, _c );
        return _c;
    }
    
    
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    
    public static _method of( _fields _fs )
    {
         _method _m = _method.of( 
            "@Override",    
            "public String toString()",
            "StringBuilder sb = new StringBuilder();",
            "sb.append( getClass().getName() );",
            "sb.append( System.lineSeparator() );" );
        
        for( int i = 0; i < _fs.count(); i++ )
        {
            _field _f = _fs.getAt( i );
            if( _f.getType().endsWith( "]" ) )
            {
                //an array field, lets fix the toString instead of "Lint[...]"
                //OK, lets "limit this" to the first 20
                //_pm.requires( Math.class ); //only import Math IF we need it
                _m.add( 
                "sb.append( \"     " +  _f.getName() + "\" );",
                "if( " + _f.getName() + " == null )",
                "{",
                "    sb.append( \"null\" );",
                "}",
                "else",
                "{",
                "    sb.append( \"[ \");",
                "    for( int i = 0; i < Math.min(" + _f.getName() + ".length, 20 ); i++ )",
                "    {",
                "        sb.append( " + _f.getName() + "[ i ] );",
                "    }",
                "    sb.append( \" ]\");",
                "}",
                "sb.append( System.lineSeparator() );" );
            }
            else
            {   //it's just a normal field (not an array)
                _m.add( 
                "sb.append( \"    " + _f.getName() + " = \" );",
                "sb.append( " + _f.getName() + " );",
                "sb.append( System.lineSeparator() );" );
            }            
        }
        _m.add( "return sb.toString();" );
        return _m;
    }
}
