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
package varcode.java.adhoc;

import java.io.IOException;
import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import varcode.java.ClassNameQualified;

/**
 * In memory representation of a .java source file. (as apposed to a .java 
 * source file that ie being read from a File on the File System)
 * 
 * For feeding to the Javac compiler at runtime .  Integrating with the 
 * {@code SimpleJavaFileObject} to be "fed" into the Javac compiler at Runtime 
 * (to convert from .java source to a .class bytecode)
 * 
 * NOTE: a single {@code AdHocJavaFile} can contain MANY Class Declarations 
 * (Inner Classes, Anonymous Classes) as Member/Nested classes.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHocJavaFile
    extends SimpleJavaFileObject
    implements ClassNameQualified
{    
    /** the fully qualified name of the Source created (i.e. "ex.varcode.MyClass") */
    private final String className;
    
    /** the .java source code content of the (class, enum, interface, ...) */
    private final String code;

    /**
     * Returns the .java code content of the (class, enum, interface, ...)
     * @param ignoreEncodingErrors
     * @return the Java source code
     */
    @Override
    public CharSequence getCharContent( boolean ignoreEncodingErrors ) 
    {
        return code;
    }
    
    /**
     * Creates and returns an AdHocJavaFile with the packageName, className, of code
     * @param packageName the package where class is
     * @param className the simple name of the class
     * @param code the source code for the class
     * @return the AdHocJavaFile
     */
    public static AdHocJavaFile of( String packageName, String className, String code )
    {
        return new AdHocJavaFile( packageName, className, code );
    }
    
    /**
     * Creates and returns an AdHocJavaFile with the packageName, className, of code
     * @param className the simple name of the class
     * @param code the source code for the class
     * @return the AdHocJavaFile
     */
    public static AdHocJavaFile of( String className, String code )
    {
        return new AdHocJavaFile( className, code );
    }
    
    /**
     * creates and returns an AdHocJavaCode representing the javaFileObject
     * @param javaFileObject object representing a Java File (class)
     * @return the generated AdHocJavaCode
     */
    public static AdHocJavaFile of( JavaFileObject javaFileObject )
    {
    	String code = null;
    	try
    	{
            code = javaFileObject.getCharContent( true ).toString();
    	}
    	catch( IOException ioe )
    	{
            throw new AdHocException(
                "Unable to read code from JavaFileObject \"" + javaFileObject 
              + "\"", ioe );
    	}
        //the className does not have the 
        String fullName = null;
        if( javaFileObject.getName().endsWith( ".java" ) )
        {
            fullName = 
                javaFileObject.getName().substring( 
                    0, javaFileObject.getName().lastIndexOf( ".java" ) );
        }
        else
        {
            fullName = javaFileObject.getName();
        }
    	fullName = fullName.replace( "\\", "." );
    	fullName = fullName.replace( "/", "." );
        
    	if( fullName.contains( "." ) )
    	{
            String className = 
                fullName.substring( fullName.lastIndexOf( "." ) + 1 );
            String packageName = 
                fullName.substring( 0, fullName.lastIndexOf( "." ) );
            return new AdHocJavaFile( packageName, className, code );
    	}
    	else
    	{
            return new AdHocJavaFile( fullName, code );
    	}
    }
    
    public AdHocJavaFile( String className, String code )
    {
        super( 
            URI.create( 
                "string:///" + className.replace('.', '/') + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        
        this.className = className;
        this.code = code;
    }
    
    public AdHocJavaFile( 
        String packageName, String className, String code )
        throws AdHocException
    {
        super( 
            URI.create( 
                "string:///" + ( packageName + "." + className ).replace( '.', '/' ) 
                + Kind.SOURCE.extension ), 
            Kind.SOURCE );
        if( packageName == null || packageName.trim().length() == 0 )
        {
            this.className = className;
        }
        else
        {
            this.className = packageName + "." + className;
        }
        this.code = code;
    }
    
    @Override
    public String getQualifiedName()
    {
        return className;
    }

    public String asString()
    {
        return code;
    }

    public String describe()
    {
        return getQualifiedName() + ".java : AdHocJavaFile@" + Integer.toHexString( this.hashCode() );
    }
    
    @Override
    public String toString()
    {
        return code;
    }
}
