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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.Collection;
import java.util.List;
import varcode.java.ast.JavaAst;
import varcode.java.model._JavaFileModel;

/**
 * Unified API for dealing with AdHoc compilation of code. <BR><BR>
 * 
 * Mechanism for Compiling one or more in memory Java source files 
 * {@link JavaSourceFile} into in memory Java class files {@code AdHocClassFile}s 
 * and loading the Classes into a new {@code AdHocClassLoader} (at runtime).
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class AdHoc
{
    /**
     * Compiles the Java source code, and returns an AdHocClassLoader
     * containing all of the Classes represented by this javaCode
     * 
     * NOTE:
     * the FIRST thing we have to do, is to pass the Java source code to the
     * JavaAST parser to determine the fully qualified class name of the top 
     * level class of this javacode... 
     * 
     * THEN I can call {@code compile( qualifiedClassName, javaCode );}
     * 
     * @param javaCode the java code 
     * @return an AdHocClassLoader (classLoader implementation) containing
     * the compiled classes that are represented in the Java code
     */
    public static AdHocClassLoader compile( String javaCode )
    {
        try
        {   //since they didnt tell us the name of the class, we gotta parse the
            // AST of teh source to find out
            CompilationUnit astRoot = JavaAst.astFrom( javaCode );
            
            String packageName = null;
            
            //we have to determine the name of the package (IF there is a package)
            if( astRoot.getPackage() != null )
            {
                packageName = astRoot.getPackage().getPackageName();
            }
            //we want the name of the Root Type Declaration 
            //( AND not all the Generic Stuff: 
            //...                     "Map<K,V>" (We dont want this)
            //...just the simple name "Map"      (We want this)
            TypeDeclaration typeDecl = JavaAst.findRootTypeDeclaration( astRoot );
            String topClassName = typeDecl.getName();
            
            if( packageName != null && packageName.trim().length() > 0 )
            {
                //System.out.println( "PACKAGE NAME "+ packageName.trim()  );
                return compile( packageName.trim() + "." + topClassName, javaCode );
            }
            return compile( topClassName, javaCode );            
        }
        catch( ParseException pe )
        {
            throw new JavacException( "Unable to parse AST from java source code " );
        }
    }
    
    /**
     * Compiles and returns an AdHocClassLoader that represents ALL of the 
     * Classes represented in the Java source String
     * 
     * @param qualifiedClassName the (fully qualified) name of the class 
     * (i.e. "java.lang.String", not "String")
     * @param javaCode the .java source code for the class, enum, interface, annotationType
     * @return a new AdHocClassLoader containing ALL of the compiled classes
     * that are represented in the source code(There can be more than one if 
     * the source contains Nested classes, enums, interfaces, annotationTypes)
     */
    public static AdHocClassLoader compile( 
        String qualifiedClassName, String javaCode )
        throws JavacException
    {
        return compile( new JavaSourceFile( qualifiedClassName, javaCode ) );
    }
    
    /**
     * Compile a fileModel( _class, _enum, _interface, _annotationType )
     * and return the {@code AdHocClassFile} containing the class bytecodes.
     * 
     * NOTE: since a single _JavaFileModel can contain nested member classes
 this will <B>ONLY return the single Class file from the Top Level Class</B>
     * (so in effect dont use this if you are compiling a Class that has nested
     *  members)
     * 
     * @param fileModel a model (_class, _enum, _interface, _annotationType) that is represented in
     * it's own file
     * @return the AdHocClassFile (containing the compiled bytecodes fro this file)
     */
    public static JavaClassFile compileToFile( _JavaFileModel fileModel )
    {
        JavaSourceFolder sourceFolder = new JavaSourceFolder();
        sourceFolder.add( fileModel );
        AdHocClassLoader adHocClassLoader = compile( sourceFolder );
        return adHocClassLoader.findClassFile( fileModel.getQualifiedName() );
    }
    
    /**
     * Compiles one or more (_class, _interface, _enum, ...) JavaFileModels
     * @param fileModels _class, _interface, _enum models
     * @return the AdHocClassLoader containing the Loaded classes
     */
    public static AdHocClassLoader compile( _JavaFileModel...fileModels )
    {
        JavaSourceFolder sourceFolder = new JavaSourceFolder();
        sourceFolder.add( fileModels );
        return compile( sourceFolder );
    }
    
    /**
     * Creates a new AdHocClassLoader, and compiles the javaFiles into the
     * AdHocClassLoader and returns it.
     *
     * @param javaFiles Java code to be compiled
     * @return an AdHocClassLoader containing the compiled .classes
     */
    public static AdHocClassLoader compile( JavaSourceFile... javaFiles )
        throws JavacException
    {
        return compile( AdHocClassPath.current(), javaFiles );
    }

    /*
    public static AdHocClassLoader compile(
        ClassLoader parentClassLoader, _JavaFileModel...fileModels )
    {
        JavaSourceFolder project = new JavaSourceFolder();
        project.add( fileModels );
        return compile( project, parentClassLoader );
    }
    */
    
    /**
     * Creates a JavaSourceFolder, adds the Java Files, compiles to Java Files and
     * loads the classes into the {@code adHocClassLoader} and returns the
     * updated {@code AdHocClassLoader}
     *
     * @param parentClassLoader the classLoader where files are to be loaded
     * @param javaSourceFiles the files to compile and load
     * @return the updated AdHocClassLoader containing the compiled .classes
     * @throws JavacException if an error occurs when compiling
     
    public static AdHocClassLoader compile(
        ClassLoader parentClassLoader,
        JavaSourceFile... javaSourceFiles )
        throws JavacException
    {
        JavaSourceFolder javaSourceFolder = JavaSourceFolder.of( javaSourceFiles );
        return compile( javaSourceFolder, parentClassLoader );
    }
    */ 
    
    /**
     * 
     * @param classPath
     * @param javaSourceFiles
     * @return
     * @throws JavacException 
     */
    public static AdHocClassLoader compile( 
        AdHocClassPath classPath, JavaSourceFile...javaSourceFiles )
        throws JavacException
    {
        JavaSourceFolder javaSourceFolder = JavaSourceFolder.of( javaSourceFiles );
        return compile( javaSourceFolder, classPath );
    }

    /**
     * 
     * @param javaSourceFiles
     * @param compilerOptions
     * @return
     * @throws JavacException 
     */
    public static AdHocClassLoader compile(
        List<JavaSourceFile> javaSourceFiles,
        Javac.JavacOptions.CompilerOption... compilerOptions )
        throws JavacException
    {
        return compile(              
            JavaSourceFolder.of( javaSourceFiles.toArray( new JavaSourceFile[ 0 ] ) ), 
            compilerOptions );
    }

    /**
     * 
     * @param parentClassLoader the parent Class Loader 
     * (if the java source files need to resolve "other" classes, they should
     * be available in the parentClassLoader)
     * 
     * @param javaFiles the files to be compiled and loaded
     * @param compilerOptions options passed to the runtime JAVAC compiler 
     * @return an AdHocClassLoader containing the classes that were created
     * @throws JavacException if an error occurs while compiling
     
    public static AdHocClassLoader compile(
        ClassLoader parentClassLoader,
        List<JavaSourceFile> javaFiles,
        Javac.JavacOptions.CompilerOption... compilerOptions )
        throws JavacException
    {
        JavaSourceFolder ws = JavaSourceFolder.of( javaFiles.toArray(new JavaSourceFile[ 0 ] ) );
        return compile( ws, parentClassLoader, compilerOptions );
    }
    */ 
    
    /**
     * Pass in a JavaSourceFolder, compile it and return the AdHocClassLoader containing the Classes 
     * 
     * @param sourceFolder sourceFolder containing Java source files to be compiled
     * @param compilerOptions options passed to the runtime JAVAC compiler
     * @return the AdHocClassLoader containing the compiled .class files
     * @throws JavacException if an error occurs when compiling
     */
    public static AdHocClassLoader compile( JavaSourceFolder sourceFolder,
        Javac.JavacOptions.CompilerOption...compilerOptions )        
        throws JavacException
    {
        //System.out.println( compilerOptions.length );
        //return compile( sourceFolder, 
        //    ClassLoader.getSystemClassLoader(), 
        //    compilerOptions );
        return compile( sourceFolder.getFiles(), compilerOptions );
    }

    public static AdHocClassLoader compile( Collection<JavaSourceFile> files,  
        Javac.JavacOptions.CompilerOption...compilerOptions ) 
    {
        AdHocClassLoader adHocClassLoader = new AdHocClassLoader( );
        
        AdHocFileManager adHocFileManager = 
            new AdHocFileManager( adHocClassLoader );

        Javac.doCompile( adHocFileManager, files, //sourceFolder.getFiles(),
            compilerOptions );
        return adHocClassLoader;
    }
    
    /**
     * Compile the contents of the JavaSourceFolder into a new AdHocClassLoader
     *
     * @param sourceFolder sourceFolder containing files
     * @param parentClassLoader the Parent ClassLoader
     * @param compilerOptions compiler options for the runtime Javac
     * @return the populated AdHocClassLoader containing the compiled classes
     * @throws JavacException if the sourceFolder did not compile
     
    public static AdHocClassLoader compile(
        JavaSourceFolder sourceFolder,
        ClassLoader parentClassLoader,
        Javac.JavacOptions.CompilerOption...compilerOptions )        
        throws JavacException
    {
        AdHocClassLoader adHocClassLoader = null;
        
        if( parentClassLoader instanceof AdHocClassLoader )
        {   //the "parent" is already an AdHocClassLoader
            adHocClassLoader = (AdHocClassLoader)parentClassLoader;
        }
        else
        {
            adHocClassLoader = new AdHocClassLoader( parentClassLoader );
        }

        AdHocFileManager adHocFileManager = 
            new AdHocFileManager( adHocClassLoader );

        Javac.doCompile( adHocFileManager, sourceFolder.getFiles(),
            compilerOptions );
        return adHocClassLoader;
    }  
    */ 
}
