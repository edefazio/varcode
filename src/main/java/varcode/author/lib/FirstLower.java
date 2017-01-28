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
package varcode.author.lib;

import java.lang.reflect.Array;
import java.util.Collection;
import varcode.context.Context;

import varcode.context.VarScript;
//import varcode.translate.JSArrayToArrayTranslate;

public enum FirstLower
    implements VarScript
{
    INSTANCE;

    /**
     * Given a String capitalize the first character and return
     *
     * @param string the target string
     * @return
     * <UL>
     * <LI>null if string is null
     * <LI>"" if string is ""
     * <LI>"FirstCap" if the string is "firstCap"
     * </UL>
     */
    private static String lowercaseFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toLowerCase() + string.substring( 1 );
    }

    public static Object doFirstLower( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
            return lowercaseFirstChar( ((String)var) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstLower[ i ]
                        = lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        if( var instanceof Collection )
        {
            Object[] arr = ((Collection<?>)var).toArray();
            int len = arr.length;
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    firstLower[ i ]
                        = lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        /*
        Object[] jsArray = JSArrayToArrayTranslate.getJSArrayAsObjectArray( var );
        if( jsArray != null )
        {
            int len = jsArray.length;
            String[] firstLower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = jsArray[ i ];
                if( idx != null )
                {
                    firstLower[ i ] = lowercaseFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstLower[ i ] = null;
                }
            }
            return firstLower;
        }
        */
        return lowercaseFirstChar( var.toString() );
    }

    @Override
    public Object eval( Context context, String input )
    {
        return doFirstLower( context.resolveVar( input ) );
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

}
