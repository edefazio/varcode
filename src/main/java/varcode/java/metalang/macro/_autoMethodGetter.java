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

import varcode.java.metalang._code;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoMethodGetter
    implements _javaMacro
{   
    /**
     * Creates and returns a _method based on a _field
     * @param field
     * @return 
     */
    public static _method of( _field field )
    {
        return of( field.getType(), field.getName() );
    }
    
    private static String firstCaps( String string )
    {
        return Character.toUpperCase( string.charAt( 0 ) ) 
            + string.substring( 1 );
    }
    public static _method of( Object type, String fieldName )
    {
        //System.out.println( "HERE" );
        return _method.of( 
            "public " + type + " get" + firstCaps( fieldName) +"( )",
            _code.of( "return this." + fieldName + ";" ) );            
    }   
}
