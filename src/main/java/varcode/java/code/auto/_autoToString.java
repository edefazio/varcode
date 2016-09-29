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
package varcode.java.code.auto;

import varcode.java.code._class;
import varcode.java.code._fields;
import varcode.java.code._fields._field;
import varcode.java.code._for;
import varcode.java.code._if;
import varcode.java.code._methods._method;

/**
 * creates a toString method for a class 
 * 
 * i.e. 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoToString
{
    public static _class of ( _class classModel )
    {
        _class classWithToString = _class.cloneOf( classModel );
        classWithToString.method( ofAllFields( classWithToString.getFields() ) );
        classWithToString.imports( StringBuilder.class );
        
        
        return classWithToString;
    }
    
    public static _method ofAllFields( _fields fields )
    {
        _method toString = _method.of("public String toString()") 
            .body( "StringBuilder sb = new StringBuilder();");
        
        toString.annotate("@Override");
        
        for( int i = 0; i < fields.count(); i++ )
        {
            _field f = fields.getAt( i );
            if( i > 0 )
            {
                 toString.addToBody( "sb.append( System.lineSeparator() );");
            }
            if( f.getType().endsWith( "]" ) )
            {
                //it's an array                
                toString.addToBody(
                    "sb.append( \"" + f.getName() + "\" );",    
                    _if.is( f.getName() + " == null",
                        "sb.append( \" = null\" );")
                    ._else(
                        "sb.append( \" = [\" );",                                
                        //todo    
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
            //TODO need to handle arrays
                toString.addToBody( 
                    "sb.append( \"" + f.getName() + "\" );",
                    "sb.append( \" = \" );",
                    "sb.append( " + f.getName() + " );" );
            
            }
        }
        toString.addToBody("return sb.toString();");
     
        return toString;
    }
    
    public static void main( String[] args )
    {
        _autoDto a = _autoDto.of("ex.varcode.MyDto")
            .property( "public int count = 1;" )
            .property( "public String name = \"Default\";" )
            .property( "public int[] values = new int[]{1,2,3,4}");
        
        _class toStrung = of( a.toClassModel() );
        
        System.out.println( toStrung );
        
        Object inst = toStrung.instance( );
        System.out.println ( inst );
        
            
    }
}
