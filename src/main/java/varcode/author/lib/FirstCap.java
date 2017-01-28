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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import varcode.context.Context;
import varcode.context.VarScript;
//import varcode.translate.JSArrayToArrayTranslate;

public enum FirstCap
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
    public static String capitalizeFirstChar( String string )
    {
        if( string == null )
        {
            return null;
        }
        if( string.length() == 0 )
        {
            return "";
        }
        return string.substring( 0, 1 ).toUpperCase() + string.substring( 1 );
    }

    public static Object doFirstCaps( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var instanceof String )
        {
            return capitalizeFirstChar( ((String)var) );
        }
        if( var.getClass().isArray() )
        { //need to "firstCap" each element within the array
            int len = Array.getLength( var );
            String[] firstCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( var, i );
                if( idx != null )
                {
                    firstCaps[ i ]
                        = capitalizeFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        if( var instanceof Collection )
        {
            //List<String> l = (List<?>)var;

            Object[] arr = ((Collection<?>)var).toArray();
            int len = arr.length;
            String[] firstCaps = new String[ len ];

            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    //l.set(i, capitalizeFirstChar( idx.toString() ) );

                    firstCaps[ i ]
                        = capitalizeFirstChar( idx.toString() );

                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        /*
        Object[] jsArray = JSArrayToArrayTranslate.getJSArrayAsObjectArray( var );
        if( jsArray != null )
        {
            int len = jsArray.length;
            String[] firstCaps = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = jsArray[ i ];
                if( idx != null )
                {
                    firstCaps[ i ] = capitalizeFirstChar( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    firstCaps[ i ] = null;
                }
            }
            return firstCaps;
        }
        */
        return capitalizeFirstChar( var.toString() );
    }

    @Override
    public Object eval( Context context, String input )
    {
        Object resolved
            = context.resolveVar( input );
        return doFirstCaps( resolved );
    }

    public Object parse( Context context, String scriptInput )
    {
        if( scriptInput != null
            && scriptInput.startsWith( "$" )
            && scriptInput.endsWith( ")" )
            && scriptInput.indexOf( '(' ) > 0 )
        {
            //I first need to 
            // {#id:$quote($uuid())#}
            //             $uuid()

            //               (
            int openIndex = scriptInput.indexOf( '(' );
            String scriptName = scriptInput.substring( 1, openIndex );
            //VarScript innerScript = context.getVarScript( scriptName );
            VarScript innerScript = context.resolveScript( scriptName, scriptInput );
            String innerScriptInput
                = scriptInput.substring(
                    openIndex + 1,
                    scriptInput.length() - 1 );
            Object innerScriptResult
                = innerScript.eval( context, innerScriptInput );
            return innerScriptResult;
        }
        return context.resolveVar( scriptInput );
    }

    public Set<String> getAllVarNames( String input )
    {
        if( input != null )
        {
            Set<String> s = new HashSet<String>();
            s.add( input.replace( "$(", "" ).replace( ")", "" ) );
            return s;
        }
        return Collections.emptySet();
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
