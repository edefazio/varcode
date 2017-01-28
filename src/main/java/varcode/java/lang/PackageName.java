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
package varcode.java.lang;

import varcode.java.JavaException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class PackageName
{
    public static String validate( String packageName )
        throws JavaException
    {
        String[] split = splitToParts( packageName );
        for( int i = 0; i < split.length; i++ )
        {
            validatePart( split[ i ], packageName );
        }
        return packageName;
    }

    private static String[] splitToParts( String packageName )
    {
        int charPointer = 0;
        List<String> parts = new ArrayList<String>();
        int nextDot = packageName.indexOf( '.', charPointer );
        while( nextDot >= 0 )
        {
            String part = packageName.substring( charPointer, nextDot );
            parts.add( part );
            charPointer = nextDot + 1;
            nextDot = packageName.indexOf( '.', charPointer );
        }
        if( charPointer < packageName.length() )
        {
            String part = packageName.substring( charPointer );
            parts.add( part );
        }
        return parts.toArray( new String[ parts.size() ] );
    }

    private static String validatePart( String part, String fullName )
    {
        //if( Arrays.binarySearch( RESERVED_WORDS, part ) >= 0 )
        if( ReservedWords.contains( part ) )    
        {
            throw new JavaException(
                "package name part \"" + part
                + "\" of package name : \"" + fullName
                + "\" is a reserved word, invalid for package name" );
        }
        if( !Character.isJavaIdentifierStart( part.charAt( 0 ) ) )
        {
            throw new JavaException( "first character \"" + part.charAt( 0 )
                + "\" of package part \"" + part + "\" of package name \""
                + fullName + "\" is invalid" );
        }
        char[] chars = part.toCharArray();
        for( int i = 1; i < chars.length; i++ )
        {
            if( !Character.isJavaIdentifierPart( chars[ i ] ) )
            {
                throw new JavaException(
                    "character in part \"" + part + "\" at char [" + i
                    + "] of package name : \"" + fullName + "\" is invalid " );
            }
        }
        return part;
    }

    public static String toPath( String packageName )
    { //validate that it is a valid package name
        validate( packageName );
        return packageName.replace( ".", File.separator ) + File.separator;
    }
} //Package
