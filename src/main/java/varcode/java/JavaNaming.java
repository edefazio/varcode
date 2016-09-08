/*
 * Copyright 2015 M. Eric DeFazio.
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
package varcode.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import varcode.VarException;

/**
 * Conventions About Java Source Code 
 * constraints / invariants (of Naming, Packaging, Reading  etc.)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaNaming
{
    ; //singleton enum idiom    

	/**
     * Java Reserved Words
     */
    public static final String[] PRIMITIVE_TYPES =
        { "boolean", "byte", "char", "double", "float", "int", "long", "short"};
    
    /**
     * Java Reserved Words
     */
    public static final String[] RESERVED_WORDS =
        { "abstract", "assert", "boolean", "break", "byte", "case", "catch", 
          "char", "class", "const", "continue", "default", "do", "double", 
          "else", "enum", "extends", "final", "finally", "float", "for", "goto", 
          "if", "implements", "import", "instanceof", "int", "interface", 
          "long", "native", "new", "package", "private", "protected", "public",
          "return", "short", "static", "strictfp", "super", "switch", 
          "synchronized", "this", "throw", "throws", "transient", "try", 
          "void", "volatile", "while" };

    /**
     * Understands Code constraints on Java Class Names
     * methods and replace and clean up the code here
     */
    public static class ClassName
    {
        /**
         * validates that <CODE>fullyQualifiedClassName</CODE> is a valid
         * i.e. "java.util.Map"
         * 
         * @param fullyQualifiedClassName
         * @return the valid name
         * @throws VarException if the name is not valid
         */
        public static String validateFullClassName( String fullyQualifiedClassName )
            throws VarException
        {
            parsePackageAndClassName( fullyQualifiedClassName );
            return fullyQualifiedClassName;
        }

        public static final String toFullClassName( 
            String packageName, String className )
        {
            if( packageName == null )
            {
                return JavaNaming.ClassName.validateSimpleName( className );
            }
            String trimPackage = packageName.trim(); 
            if( trimPackage.length() == 0  )
            {
                return className;
            }
            String fullName = trimPackage + "." + className;
            
            return JavaNaming.ClassName.validateFullClassName( fullName );
        }
        
        /**
         *  Parses a Fully Qualified Class Name i.e. :<BR> 
         * "com.mycompany.myproduct.mycomponent.Component"
         * into (2) strings:
         * <OL>
         * <LI>String[0] ="com.mycompany.myproduct.mycomponent"; //pkg Name
         * <LI>String[1] ="Component"; //the "Simple" class Name
         * </OL>
         * 
         *  @param fullyQualifiedClassName
         *  @return String[] where:
         * <OL> 
         * <LI>String[0] is the package name (i.e. "java.lang")
         * <LI>String[1] is the "Simple" className (i.e. "String")
         * </OL>
         */
        public static String[] parsePackageAndClassName( 
            String fullyQualifiedClassName )
            throws VarException
        {
        	String[] packageAndClassName = extractPackageAndClassName( 
                fullyQualifiedClassName );
        	if( packageAndClassName[ 0 ].equals( "" ) )
        	{
        		 validateSimpleName( fullyQualifiedClassName );
        	}
        	else
        	{
        		JavaNaming.PackageName.validate( packageAndClassName[ 0 ] );
                validateSimpleName( packageAndClassName[ 1 ] );
        	}
        	return packageAndClassName;
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
                 return new String[]{ packageName, simpleClassName };
             }
             return new String[]{ "", fullyQualifiedClassName };
        }
        
        public static String validateSimpleName( String simpleClassName )
            throws VarException
        {
            if( simpleClassName == null || simpleClassName.trim().length() ==0 )
            {
                throw new VarException( "Class name is null" );
            }
            if( Arrays.binarySearch( RESERVED_WORDS, simpleClassName ) >= 0 )
            {
                throw new VarException( 
                    "Class name: \"" + simpleClassName + "\" reserved word" );
            }
            if( simpleClassName.endsWith( ">" ) )
            {
                //it's a Generic Class
                int openGenericIndex =  simpleClassName.indexOf( "<" );
                if( openGenericIndex < 0 )
                {
                    throw new VarException("className contains no matching '<' for '>'");
                }
                //convert all < and > and , s to spaces so I can tokenize
                String removeAll = simpleClassName.replace("<", " ");
                removeAll = removeAll.replace( ">", " " );
                removeAll = removeAll.replace( ",", " " );
                removeAll = removeAll.replace( "extends", " " );
                removeAll = removeAll.replace( "super", " " );
                removeAll = removeAll.replace( "?", " " );
                
                //bounded generics
                //"extends"
                // "? extends ..."
                // ? super Integer
                
                String[] components = removeAll.split(" ");
                for(int i=0; i<components.length; i++ )
                {
                    try
                    {
                        validateSimpleName( components[ i ] ); 
                    }
                    catch( VarException ve )
                    {
                        throw new VarException( 
                           "Generic class Definition of \""+ simpleClassName
                            +"\" is not valid", ve );
                    }
                }
                return simpleClassName;
            }
            char[] chars = simpleClassName.toCharArray();
            if( !Character.isJavaIdentifierStart( chars[ 0 ] ) )
            {
                throw new VarException(
                    "Class name: \"" + simpleClassName + "\" is invalid, char at [" 
                  + 0 + "] is '" + chars[ 0 ] + "' invalid for Java Class name" );
            }
            for( int i = 1; i < chars.length; i++ )
            {
                if( !Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    throw new VarException(
                        "Class name: \"" + simpleClassName + "\" is invalid, char at [" 
                      + i + "] is '" + chars[ i ] + "' invalid for Java class name" );
                }
            }
            if( simpleClassName.length() > 512 )
            {
                throw new VarException( "Class name \"" + simpleClassName
                    + "\" is > 512 characters in length;" 
                    + "Java allows this, but we don't" );
            }
            return simpleClassName;
        }

        /**
         * Converts a "raw" class name to an "actual" valid Java class name
         * @param name what I would like to name the class
         * @return a class name that is valid for Java source code 
         */
        public static String convertName( String name )
        {
            char[] chars = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            if( Character.isJavaIdentifierStart( chars[ 0 ] ) )
            {
                sb.append( chars[ 0 ] );
            }
            else
            {
                //just use an underscore INSTEAD
                sb.append( '_' );
            }
            for( int i = 1; i < chars.length; i++ )
            { //look through the remaining characters after first
                if( Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    sb.append( chars[ i ] );
                }
                else
                {
                    sb.append( '_' );
                }
            }
            String validName = sb.toString();

            if( Arrays.binarySearch( RESERVED_WORDS, validName ) >= 0 )
            { //verify that we didnt try to name it as a reserved word                    
                return "_" + validName;
            }
            return validName;
        }

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
         * @throws VarException
         */
        public static String toSourcePath( String canonicalClassName )
            throws VarException
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

        /** 
         * Given a fully qualified path to a file:
         * "C:\\dev\\apps\\source\\com\\mycompany\\myapp\\myComponent\\StructTable.java"
         * ...returns what the class name would be:
         * "StructTable"
         * 
         * @param fileName the name of a .Java File
         */
        public static String simpleClassNameFromPathName( String fileName )
        {
            if( !fileName.endsWith( ".java" ) )
            {
                throw new VarException(
                    "The fileName \"" + fileName + "\" does not end in .java" );
            }
            // (5) for ".java" + (1) since length > index
            int lastCharInClassName = fileName.length() - ( ".java".length() + 1 );
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
    }    

    /** 
     * Constraints on Java Identifiers     
     */
    public static class IdentifierName
    {
        public static String validate( String identifierName )
            throws VarException
        {
            if( ( identifierName == null ) || identifierName.length() < 1 )
            {
                throw new VarException( 
                	"Invalid identifier name : \"" + identifierName 
                	+ "\" Standard Identifiers must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( identifierName.charAt( 0 ) ) )
            {
                throw new VarException(
                	"Invalid Java identifier name: \"" + identifierName 
                	+"\" Standard Identifiers must start with a letter{a-z, A-Z} " 
                	+ "or '_', '$'" );
            }
            for( int i = 1; i < identifierName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( identifierName.charAt( i ) ) )
                {
                    throw new VarException(
                    	"Invalid Java identifier name: \"" + identifierName 	
                    	+ "\" Standard Identifiers cannot contain the character '"
                        + identifierName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( Arrays.binarySearch( RESERVED_WORDS, identifierName ) > 0 )
            {
                throw new VarException( 
                	"Invalid Standard Identifier name \"" + identifierName
                    + "\" is a reserved word" );
            }
            
            return identifierName;
        }
    }
    
    private static Set<String>arrayDimensions = new HashSet<String>();	
    static
    {
    	arrayDimensions.add("[]");
    	arrayDimensions.add("[][]");
    	arrayDimensions.add("[][][]");
    }
    
    public static class TypeName
    {
    	public static String validate( String typeName )
    	{
    		return validate( typeName, true );
    	}
    	
    	public static String validate( String typeName, boolean voidAllowed )
            throws VarException
        {
    		if( ( typeName == null ) || typeName.length() < 1 )
            {
    			throw new VarException( 
                    "Invalid type name : \"" + typeName 
                  + "\" Standard Identifiers must be at least 1 character" );
            }
            if( !Character.isJavaIdentifierStart( typeName.charAt( 0 ) ) )
            {
                throw new VarException(
                  	"Invalid Java type name: \"" + typeName 
                   	+"\" types must start with a letter{a-z, A-Z} " 
                   	+ "or '_', '$'" );
            }
            //I need to separate any < > generic notations
            
            if( typeName.endsWith( ">" ) )
            {
            	int genericStart = typeName.indexOf( '<' );
            	if( genericStart < 0 )
            	{
            		throw new VarException(
            			"typeName ends with '>' but does not have a corresponding '<'" );
            	}
            	//the generic part of the type
            	//String genericPart = 
            	//	typeName.substring( genericStart + 1, typeName.length() -1 );
            	String initialPart = 
            		typeName.substring( 1, genericStart );
            	
            	for( int i = 0; i < initialPart.length(); i++ )
            	{
            		if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
            		{
            			throw new VarException(
            				"Invalid Java type name: \"" + typeName 	
                        	+ "\" types cannot contain the character '"
                        	+ typeName.charAt( i ) + "' at [" + ( i + 1 ) + "]" );
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
            		throw new VarException( 
                        "Invalid Type, expected \"[\" to match \"]\"" );
            	}
            	String braces = typeName.substring( arrayStart );
            	
            	braces = braces.replace( " ", "" );
            	if( !arrayDimensions.contains( braces ) )
            	{
            		throw new VarException( 
                        "Invalid Type, ArrayDimensions \"" 
                         + braces + "\" invalid" );
            	}            	
            	for( int i = 1; i < arrayStart; i++ )
            	{
            		if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
            		{
            			throw new VarException(
            				"Invalid Java type name: \"" + typeName 	
                        	+ "\" types cannot contain the character '"
                        	+ typeName.charAt( i ) + "' at [" + ( i + 1 ) + "]" );
            		}
                }
            	return typeName;
            }
            
            for( int i = 1; i < typeName.length(); i++ )
            {
                if( !Character.isJavaIdentifierPart( typeName.charAt( i ) ) )
                {
                    throw new VarException(
                     	"Invalid Java type name: \"" + typeName 	
                       	+ "\" types cannot contain the character '"
                        + typeName.charAt( i ) + "' at [" + i + "]" );
                }
            }
            if( Arrays.binarySearch( RESERVED_WORDS, typeName ) > 0 )
            {   //its a reserved word 
             	if( Arrays.binarySearch( PRIMITIVE_TYPES, typeName ) < 0 )
               	{	//...but its not a primitive type
             		if( voidAllowed && typeName.equals( "void" ) )
             		{
             			
             		}
             		else
             		{
             			throw new VarException( 
             				"Invalid Java type name \"" + typeName
             				+ "\" is a reserved word" );
             		}
               	}
            }
            return typeName;
        }	
    }

    /**
     * Conventions for Java Class Names 
     */
    public static class PackageName
    {
        public static String validate( String packageName )
            throws VarException
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
            if( Arrays.binarySearch( RESERVED_WORDS, part ) >= 0 )
            {
                throw new VarException( 
                	"package name part \"" + part
                    + "\" of package name : \""+ fullName + 
                    "\" is a reserved word, invalid for package name" );
            }
            if( !Character.isJavaIdentifierStart( part.charAt( 0 ) ) )
            {
                throw new VarException( "first character \"" + part.charAt( 0 )
                    + "\" of package part \"" + part + "\" of package name \"" 
                	+ fullName + "\" is invalid");
            }
            char[] chars = part.toCharArray();
            for( int i = 1; i < chars.length; i++ )
            {
                if( !Character.isJavaIdentifierPart( chars[ i ] ) )
                {
                    throw new VarException(
                        "character in part \"" + part + "\" at char [" + i + 
                        "] of package name : \"" + fullName + "\" is invalid " );
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
}
