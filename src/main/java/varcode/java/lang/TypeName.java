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

import java.util.HashSet;
import java.util.Set;
import varcode.java.JavaException;

/**
 * Conventions for validating a Java Type Name within .java source code
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum TypeName
{
    ;
    
    public static String validate( String typeName )
    {
        return validate( typeName, true );
    }

    public static String validate( String typeName, boolean voidAllowed )
        throws JavaException
    {
        if( (typeName == null) || typeName.length() < 1 )
        {
            throw new JavaException(
                "Invalid type name : \"" + typeName
                + "\" Standard Identifiers must be at least 1 character" );
        }
        if( !Character.isJavaIdentifierStart( typeName.charAt( 0 ) ) )
        {
            throw new JavaException(
                "Invalid Java type name: \"" + typeName
                + "\" types must start with a letter{a-z, A-Z} "
                + "or '_', '$'" );
        }
        //I need to separate any < > generic notations

        if( typeName.endsWith( ">" ) )
        {
            int genericStart = typeName.indexOf( '<' );
            if( genericStart < 0 )
            {
                throw new JavaException(
                    "typeName ends with '>' but does not have a corresponding '<'" );
            }
            //the generic part of the type
            //String genericPart = 
            //	typeName.substring( genericStart + 1, typeName.length() -1 );
            String initialPart
                = typeName.substring( 1, genericStart );

            for( int i = 0; i < initialPart.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
                {
                    throw new JavaException(
                        "Invalid Java type name: \"" + typeName
                        + "\" types cannot contain the character '"
                        + typeName.charAt( i ) + "' at [" + (i + 1) + "]" );
                }
            }
            //TODO validate Generic Part??
            return typeName;
        }
        //type array like "String[]" or Matrix[][][]
        if( typeName.endsWith( "]" ) )
        {
            int arrayStart = typeName.indexOf( "[" );
            if( arrayStart < 0 )
            {
                throw new JavaException(
                    "Invalid Type, expected \"[\" to match \"]\"" );
            }
            String braces = typeName.substring( arrayStart );

            braces = braces.replace( " ", "" );
            if( !Array.arrayDimensions.contains( braces ) )
            {
                throw new JavaException(
                    "Invalid Type, ArrayDimensions \""
                    + braces + "\" invalid" );
            }
            for( int i = 1; i < arrayStart; i++ )
            {
                if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
                {
                    throw new JavaException(
                        "Invalid Java type name: \"" + typeName
                        + "\" types cannot contain the character '"
                        + typeName.charAt( i ) + "' at [" + (i + 1) + "]" );
                }
            }
            return typeName;
        }

        for( int i = 1; i < typeName.length(); i++ )
        {
            if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
            {
                throw new JavaException(
                    "Invalid Java type name: \"" + typeName
                    + "\" types cannot contain the character '"
                    + typeName.charAt( i ) + "' at [" + i + "]" );
            }
        }
        //if( Arrays.binarySearch( ReservedWorlds, typeName ) > 0 )
        if( ReservedWords.contains( typeName ) )
        {   //its a reserved word 
            //if( Arrays.binarySearch( PRIMITIVE_TYPES, typeName ) < 0 )
            if( Primitive.contains( typeName ) )
            {	//...but its not a primitive type
                if( voidAllowed && typeName.equals( "void" ) )
                {

                }
                else
                {
                    throw new JavaException(
                        "Invalid Java type name \"" + typeName
                        + "\" is a reserved word" );
                }
            }
        }
        return typeName;
    }

    public static class Array
    {
        public static Set<String> arrayDimensions = new HashSet<String>();

        static
        {
            arrayDimensions.add( "[]" );
            arrayDimensions.add( "[][]" );
            arrayDimensions.add( "[][][]" );
        }
    }
}
