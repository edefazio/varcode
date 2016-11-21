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
package varcode.java.lang.cs;

import varcode.java.lang._class;
import varcode.java.lang._fields;
import varcode.java.lang._fields._field;
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
{    
    public static _class of ( _class classModel )
    {
        _class classWithToString = _class.cloneOf( classModel );
        classWithToString.method( 
            ofAllFields( classWithToString.getFields() ) );
        classWithToString.imports( StringBuilder.class );
        
        return classWithToString;
    }
    
    /*
    public static class ToString //extends _Method
    {
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append( getClass().getName() );
            sb.append( System.lineSeparator() );
            /*{{+:
            sb.append( "    {+fieldName+} = " );
            sb.append( fieldToString( {+fieldName+} );
            sb.append( System.lineSpearator() );            
            +}}
            return sb.toString();
        }
    }
    */
    
    /*
    public static class FieldToString
        //extends _Method
    {
        private static String fieldToString( Object value )
        {
            if( value == null )
            {
                return "null";
            }
            StringBuilder sb = new StringBuilder();
            if( value.getClass().isArray() )
            {
                int len = Array.getLength( value );
                
                sb.append( "[ " );
                for( int i = 0; i < len; i++ )
                {
                    sb.append( Array.get(value, i ) );
                    if( i < len - 1 )
                    {
                        sb.append( ", " );
                    }
                }
                sb.append( " ]" );                    
            }
            else
            {
                sb.append( "    ");
                sb.append( value );
            }            
            return sb.toString();
        }
    }
*/
    
    public static _method ofAllFields( _fields fields )
    {
        _method toString = _method.of("public String toString()") 
            .body( "StringBuilder sb = new StringBuilder();",
                "sb.append( getClass().getName() );",
                "sb.append( System.lineSeparator() );");
        
        toString.annotate("@Override");
        
        for( int i = 0; i < fields.count(); i++ )
        {
            _field f = fields.getAt( i );
            if( i > 0 )
            {
                 toString.addToBody( "sb.append( System.lineSeparator() );");
            }
            if( f.getType().endsWith( "]" ) )
            {   //it's an array                
                toString.addToBody(
                    "sb.append( \"    " + f.getName() + "\" );",    
                    _if.is( f.getName() + " == null",
                        "sb.append( \" = null\" );")
                    ._else(
                        "sb.append( \" = [\" );",                                  
                        _for.of( "int i = 0", "i < " + f.getName() + ".length", "i++")                                
                            .body(
                                "sb.append( " + f.getName() + "[ i ] );",
                                _if.is( "i < ( " + f.getName() + ".length - 1 ) ",  
                                     "sb.append(\", \");" )                                    
                            ),
                        "sb.append(\"]\");"    
                    ) 
                );
                
            }
            else
            {
                toString.addToBody( 
                    "sb.append( \"    " + f.getName() + "\" );",
                    "sb.append( \" = \" );",
                    "sb.append( " + f.getName() + " );" );
            
            }
        }
        toString.addToBody("return sb.toString();");
     
        return toString;
    }    
}
