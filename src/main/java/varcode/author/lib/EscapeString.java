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

/**
 * Escapes a String
 *
 * i.e.
 * <UL>
 * <LI>tabs as "\t",
 * <LI>unicode characters as "\u0359"
 * <LI>quotes as "\""
 * </UL>
 *
 * when we take a String and want to insert the literal String into Java Code we
 * need to escape the string.
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum EscapeString
    implements VarScript
{
    INSTANCE;

    public static Object doEscapeString( Object target )
    {
        if( target == null )
        {
            return null;
        }
        if( target instanceof String )
        {
            return "\"" + target + "\"";
        }
        if( target.getClass().isArray() )
        { //need to "escape" each element within the array
            int len = Array.getLength( target );
            String[] escaped = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = Array.get( target, i );
                if( idx != null )
                {
                    escaped[ i ] = escapeJavaString( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    escaped[ i ] = null;
                }
            }
            return escaped;
        }
        if( target instanceof Collection )
        {
            Object[] arr = ((Collection<?>)target).toArray();
            int len = arr.length;
            String[] escaped = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = arr[ i ];
                if( idx != null )
                {
                    escaped[ i ]
                        = escapeJavaString( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    escaped[ i ] = null;
                }
            }
            return escaped;
        }
        /*
        Object[] jsArray = JSArrayToArrayTranslate.getJSArrayAsObjectArray( target );
        if( jsArray != null )
        {
            int len = jsArray.length;
            String[] escaped = new String[ len ];
            for( int i = 0; i < len; i++ )
            {
                Object idx = jsArray[ i ];
                if( idx != null )
                {
                    escaped[ i ] = escapeJavaString( idx.toString() );
                }
                else
                { //watch out for NPEs!
                    escaped[ i ] = null;
                }
            }
            return escaped;
        }
        */
        return escapeJavaString( target.toString() );
    }

    /**
     * <A HREF="http://unicode-table.com/en/#control-character">Unicode
     * Characters</A>
     *
     * @param source
     * @return
     */
    public static String escapeJavaString( String source )
    {
        if( source == null )
        {
            return null;
        }

        //just build the string up character by character
        StringBuilder sb = new StringBuilder();

        for( int i = 0; i < source.length(); i++ )
        {
            char c = source.charAt( i );
            //these are the most typical characters
            if( c > '\"' && c <= '~' )
            {
                if( c == '\\' )
                {
                    sb.append( '\\' );
                    sb.append( '\\' );
                }
                else
                {
                    sb.append( c );
                }
            }
            else
            {
                //'\t', '\b', '\n', '\r', '\f', '\'', '\"', '\\'

                // these characters are specifically spelled out in Java 
                // as 
                //<A HREF="https://docs.oracle.com/javase/tutorial/java/data/characters.html">
                //control characters
                switch( c )
                {
                    case ' ':
                        sb.append( ' ' );
                        break;
                    case '\"':
                        sb.append( "\\\"" );
                        break;
                    case '\t':
                        sb.append( "\\t" );
                        break;
                    case '\b':
                        sb.append( "\\b" );
                        break;
                    case '\n':
                        sb.append( "\\n" );
                        break;
                    case '\r':
                        sb.append( "\\r" );
                        break;
                    case '\f':
                        sb.append( "\\f" );
                        break;
                    default:
                        // i.e.          '\u03A9' (greek Omega character)					
                        sb.append( "\\u" );
                        sb.append( String.format( "%04X", (int)c ) );
                        break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Object eval( Context context, String input )
    {
        Object resolved
            = context.resolveVar( input );
        return doEscapeString( resolved );
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

}
