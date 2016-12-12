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
package varcode.java.macro;

import varcode.java.lang._class;
import varcode.java.lang._fields;
import varcode.java.lang._fields._field;
import varcode.java.lang._imports;
import varcode.java.lang._methods._method;

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
public class _autoToString
    implements JavaMacro.Generator, JavaMacro.Mutator
{       
    
    /**
     * Generate the appropriate toString() _method
     * and then "port" the method to the _c (class Meta Lang Model)
     * @param _c
     * @return 
     */
    public static _class to( _class _c )
    {
        _portableMethod _pm = of( _c.getFields() );
        return _Port.portForce( _pm, _c );
    }
    
    //create me a _portableMethod that will 
    // "stringify" the fields 
    public static _portableMethod of( _fields _fs )
    {        
        _method _m = _method.of( 
            "@Override",    
            "public String toString()",
            "StringBuilder sb = new StringBuilder();",
            "sb.append( getClass().getName() );",
            "sb.append( System.lineSeparator() );" );
        
        _portableMethod _pm = 
            _portableMethod.of( _m )
            .requires( _imports.of( StringBuilder.class ) );
        
        for( int i = 0; i < _fs.count(); i++ )
        {
            _field _f = _fs.getAt( i );
            if( _f.getType().endsWith( "]" ) )
            {
                //an array field, lets fix the toString instead of "Lint[...]"
                //OK, lets "limit this" to the first 20
                _pm.requires( Math.class ); //only import Math IF we need it
                _m.add( 
                "sb.append( \"     " +  _f.getName() + "\" );",
                "if( "+_f.getName() + " == null )",
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
                "    sb.append( System.lineSeparator() );" );
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
        return _pm;
    }              
}
