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
import varcode.java.JavaException.NameException;

/**
 * Defines the constraints of naming for Java identifiers
 *
 * @author Eric
 */
public enum IdentifierName
{
    ; //singleton enum idiom
    
    public static String validate( String identifierName )
        throws JavaException
    {
        if( (identifierName == null) || identifierName.length() < 1 )
        {
            throw new NameException(
                "Invalid identifier name : \"" + identifierName
                + "\" Standard Identifiers must be at least 1 character" );
        }
        if( !Character.isJavaIdentifierStart( identifierName.charAt( 0 ) ) )
        {
            throw new NameException(
                "Invalid Java identifier name: \"" + identifierName
                + "\" Standard Identifiers must start with a letter{a-z, A-Z} "
                + "or '_', '$'" );
        }
        for( int i = 1; i < identifierName.length(); i++ )
        {   //look through all characters and verify appropriate for Java identifier
            if( !Character.isJavaIdentifierPart( identifierName.charAt( i ) ) )
            {
                throw new NameException(
                    "Invalid Java identifier name: \"" + identifierName
                    + "\" Standard Identifiers cannot contain the character '"
                    + identifierName.charAt( i ) + "' at [" + i + "]" );
            }
        }
        
        if( ReservedWords.contains( identifierName ) )
        {
            throw new NameException(
                "Invalid Identifier name \"" + identifierName
                + "\" is a reserved word" );
        }

        return identifierName;
    }
}
