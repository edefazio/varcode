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

public enum AllCap
    implements VarScript
{
    INSTANCE;

    public static Object doAllCaps( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
            return ((String)var).toUpperCase();
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] allCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    allCaps[ i ] = idx.toString().toUpperCase();
                }
                else
                {   //watch out for NPEs!
                    allCaps[ i ] = null;
                }
            }
            return allCaps;
        }
        if( var instanceof Collection )
        {
            Object[] arr = ((Collection<?>)var).toArray();
            int len = arr.length;
            String[] allCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    allCaps[ i ] = idx.toString().toUpperCase();
                }
                else
                {   //watch out for NPEs!
                    allCaps[ i ] = null;
                }
            }
            return allCaps;
        }

        /*
        Object[] jsArray = JSArrayToArrayTranslate.getJSArrayAsObjectArray( var );
        if( jsArray != null )
        {
            int len = jsArray.length;
            String[] allCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = jsArray[ i ];
                if( idx != null )
                {
                    allCaps[ i ] = idx.toString().toUpperCase();
                }
                else
                { //watch out for NPEs!
                    allCaps[ i ] = null;
                }
            }
            return allCaps;
        }
*/
        return var.toString().toUpperCase();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

    @Override
    public Object eval( Context context, String input )
    {
        return doAllCaps( context.resolveVar( input ) );
        //getInputParser().parse( context, input ) );
    }

}
