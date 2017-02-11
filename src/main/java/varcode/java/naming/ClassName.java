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
package varcode.java.naming;

import java.io.File;
import varcode.java.JavaException;

/**
 *
 * @author Eric
 */
public enum ClassName
{
    ;

    public static String fromPath( String pathName )
    {
        String className = simpleClassNameFromPathName( pathName );
        validateSimpleName( className );
        return className;
    }

    /**
     * Turn the Canonical name of the class, i.e.<BR>
     *
     * <PRE>"io.varcode.tailor.ClassTailor" </PRE>
     *
     * ...into a "Path" to source path of the class<BR>
     *
     * <PRE>"io\\codemark\\tailor\\ClassTailor.java" </PRE>
     *
     * @param clazz the class
     * @return
     */
    public static String toSourcePath( java.lang.Class<?> clazz )
    {
        return toSourcePath( clazz.getCanonicalName() );
    }

    /**
     * Given a Fully Qualified Class Name:<BR>
     * "io.varcode.Code"<BR>
     * returns a String Resource Path to the Class:<BR>
     * "io\\codemark\\Code.java"<BR>
     *
     * @param canonicalClassName
     * @return
     * @throws JavaException
     */
    public static String toSourcePath( String canonicalClassName )
        throws JavaException
    {
        String[] packageAndClass = parsePackageAndClassName( canonicalClassName );

        String packagePath = packageAndClass[ 0 ].replace(
            '.', File.separatorChar );
        if( packagePath.length() > 0 )
        {
            packagePath += File.separatorChar;
        }
        if( packageAndClass[ 1 ].endsWith( ".java" ) )
        {
            return packagePath + packageAndClass[ 1 ];
        }
        return packagePath + packageAndClass[ 1 ] + ".java";
    }
    
    public static String toJavaClassPath( String canonicalClassName )
    {
        String[] packageAndClass = parsePackageAndClassName( canonicalClassName );

        String packagePath = packageAndClass[ 0 ].replace(
            '.', File.separatorChar );
        if( packagePath.length() > 0 )
        {
            packagePath += File.separatorChar;
        }
        if( packageAndClass[ 1 ].endsWith( ".class" ) )
        {
            return packagePath + packageAndClass[ 1 ];
        }
        return packagePath + packageAndClass[ 1 ] + ".class";
    }
    

    /**
     * Given a fully qualified path to a file:
     * "C:\\dev\\apps\\source\\com\\mycompany\\myapp\\myComponent\\StructTable.java"
     * ...returns what the class name would be: "StructTable"
     *
     * @param fileName the name of a .Java File
     * @return the "simple" class name
     */
    public static String simpleClassNameFromPathName( String fileName )
    {
        if( !fileName.endsWith( ".java" ) )
        {
            throw new JavaException.NameException(
                "The fileName \"" + fileName + "\" does not end in .java" );
        }
        // (5) for ".java" + (1) since length > index
        int lastCharInClassName = fileName.length() - (".java".length() + 1);
        //rewind from BEFORE the .java until you reach an invalid character
        //String theClassName = fileName.substring( 0, lastCharInClassName );

        //point to start at the last char in the name keep rewinding 
        int charIndex = lastCharInClassName;

        while( charIndex >= 0 )
        {
            if( charIndex > 0
                && Character.isJavaIdentifierPart( fileName.charAt( charIndex ) )
                || Character.isJavaIdentifierStart( fileName.charAt( charIndex ) ) )
            {
                charIndex--;
            }
            else
            {
                return fileName.substring( charIndex + 1, lastCharInClassName + 1 );
            }
        }
        return fileName.substring( 0, lastCharInClassName + 1 );
    }

    public static String validateSimpleName( String simpleClassName )
        throws JavaException
    {
        if( simpleClassName == null || simpleClassName.trim().length() == 0 )
        {
            throw new JavaException( "Class name is null" );
        }
        //if( Arrays.binarySearch( RESERVED_WORDS, simpleClassName ) >= 0 )
        if( ReservedWords.contains( simpleClassName ) )
        {
            throw new JavaException(
                "Class name: \"" + simpleClassName + "\" reserved word" );
        }
        if( simpleClassName.endsWith( ">" ) )
        {
            //it's a Generic Class
            int openGenericIndex = simpleClassName.indexOf( "<" );
            if( openGenericIndex < 0 )
            {
                throw new JavaException( "className contains no matching '<' for '>'" );
            }
            //convert all < and > and , s to spaces so I can tokenize
            String removeAll = simpleClassName.replace( "<", " " );
            removeAll = removeAll.replace( ">", " " );
            removeAll = removeAll.replace( ",", " " );
            removeAll = removeAll.replace( "extends", " " );
            removeAll = removeAll.replace( "super", " " );
            removeAll = removeAll.replace( "?", " " );

            //bounded generics
            //"extends"
            // "? extends ..."
            // ? super Integer
            String[] components = removeAll.split( " " );
            for( int i = 0; i < components.length; i++ )
            {
                try
                {
                    validateSimpleName( components[ i ] );
                }
                catch( JavaException ve )
                {
                    throw new JavaException(
                        "Generic class Definition of \"" + simpleClassName
                        + "\" is not valid", ve );
                }
            }
            return simpleClassName;
        }
        char[] chars = simpleClassName.toCharArray();
        if( !Character.isJavaIdentifierStart( chars[ 0 ] ) )
        {
            throw new JavaException(
                "Class name: \"" + simpleClassName + "\" is invalid, char at ["
                + 0 + "] is '" + chars[ 0 ] + "' invalid for Java Class name" );
        }
        for( int i = 1; i < chars.length; i++ )
        {
            if( !Character.isJavaIdentifierPart( chars[ i ] ) )
            {
                throw new JavaException(
                    "Class name: \"" + simpleClassName + "\" is invalid, char at ["
                    + i + "] is '" + chars[ i ] + "' invalid for Java class name" );
            }
        }
        if( simpleClassName.length() > 512 )
        {
            throw new JavaException( "Class name \"" + simpleClassName
                + "\" is > 512 characters in length;"
                + "Java allows this, but we don't" );
        }
        return simpleClassName;
    }

    /**
     * Parses a Fully Qualified Class Name i.e. :<BR>
     * "com.mycompany.myproduct.mycomponent.Component" into (2) strings:
     * <OL>
     * <LI>String[0] ="com.mycompany.myproduct.mycomponent"; //pkg Name
     * <LI>String[1] ="Component"; //the "Simple" class Name
     * </OL>
     *
     * @param fullyQualifiedClassName
     * @return String[] where:
     * <OL>
     * <LI>String[0] is the package name (i.e. "java.lang")
     * <LI>String[1] is the "Simple" className (i.e. "String")
     * </OL>
     */
    public static String[] parsePackageAndClassName(
        String fullyQualifiedClassName )
        throws JavaException
    {
        String[] packageAndClassName = extractPackageAndClassName(
            fullyQualifiedClassName );
        if( packageAndClassName[ 0 ].equals( "" ) )
        {
            validateSimpleName( fullyQualifiedClassName );
        }
        else
        {
            PackageName.validate( packageAndClassName[ 0 ] );
            validateSimpleName( packageAndClassName[ 1 ] );
        }
        return packageAndClassName;
    }

    /**
     * validates that <CODE>fullyQualifiedClassName</CODE> is a valid i.e.
     * "java.util.Map"
     *
     * @param fullyQualifiedClassName
     * @return the valid name
     * @throws JavaException if the name is not valid
     */
    public static String validateFullClassName(
        String fullyQualifiedClassName )
        throws JavaException
    {
        parsePackageAndClassName( fullyQualifiedClassName );
        return fullyQualifiedClassName;
    }

    public static String toFullClassName(
        String packageName, String className )
    {
        if( packageName == null )
        {
            return validateSimpleName( className );
        }
        String trimPackage = packageName.trim();
        if( trimPackage.length() == 0 )
        {
            return className;
        }
        String fullName = trimPackage + "." + className;

        return validateFullClassName( fullName );
    }

    public static String[] extractPackageAndClassName(
        String fullyQualifiedClassName )
    {
        int lastDotIndex = fullyQualifiedClassName.lastIndexOf( '.' );
        if( lastDotIndex > 0 )
        {
            String packageName = fullyQualifiedClassName.substring(
                0, lastDotIndex );

            String simpleClassName = fullyQualifiedClassName.substring(
                lastDotIndex + 1,
                fullyQualifiedClassName.length() );
            return new String[]
            {
                packageName, simpleClassName
            };
        }
        return new String[]
        {
            "", fullyQualifiedClassName
        };
    }
}
