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

public enum LowerCase
    implements VarScript
{
    INSTANCE;

    public static Object doLowercase( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
            return ((String)var).toLowerCase();
        }
        if( var.getClass().isArray() )
        {   //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] lower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    lower[ i ] = idx.toString().toLowerCase();
                }
                else
                { //watch out for NPEs!
                    lower[ i ] = null;
                }
            }
            return lower;
        }
        if( var instanceof Collection )
        {
            Object[] arr = ((Collection<?>)var).toArray();
            int len = arr.length;
            String[] lower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    lower[ i ] = idx.toString().toLowerCase();
                }
                else
                { //watch out for NPEs!
                    lower[ i ] = null;
                }
            }
            return lower;
        }
        /*
        Object[] jsArray =             
            JSArrayToArrayTranslate.getJSArrayAsObjectArray( var );
        if( jsArray != null )
        {
            int len = jsArray.length;
            String[] lower = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = jsArray[ i ];
                if( idx != null )
                {
                    lower[ i ] = idx.toString().toLowerCase();
                }
                else
                { //watch out for NPEs!
                    lower[ i ] = null;
                }
            }
            return lower;
        }
        */
        return var.toString().toLowerCase();
    }

    @Override
    public Object eval(
        Context context, String input )
    {
        return doLowercase( context.resolveVar( input ) );
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
