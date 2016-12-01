/*
 * Copyright 2016 Eric.
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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.Dom;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.JavacOptions;
import varcode.java.adhoc.Workspace;
import varcode.java.ast.JavaASTParser;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;
import varcode.java.load.JavaSourceLoader;
import varcode.java.load.JavaMetaLangLoader;
import varcode.load.LoadException;
import varcode.load.SourceLoader;

/**
 * A Unified API for loading/reflecting/authoring/invoking on Java source code
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _Java 
{
    private static final Logger LOG = 
    	LoggerFactory.getLogger( _Java.class );
    
    public static final String JAVA_CLASS_NAME = "fullyQualifieldJavaClassName";
    
    public static final String JAVA_SIMPLE_CLASS_NAME = "className";
    
    public static final String JAVA_PACKAGE_NAME = "packageName";
    
    
    public static AdHocJavaFile author( String className, Dom dom, Object...keyValuePairs )
    {
        return author( className, dom, VarContext.of( keyValuePairs ) );
    }
    
    /**
     * Authors and returns an {@code AdHocJavaFile} with name {@code className} 
     * using a {@code Dom} and based on the specialization provided in the
     * {@code context} and {@code directives}
     * 
     * @param className the class name to be authored (i.e. "io.varcode.ex.MyClass") 
     * @param dom the document object template to be specialized
     * @param context the specialization/functionality to be applied to the Dom
     * @param directives optional pre and post processing commands 
     * @return an AdHocJavaFile
     */
    public static AdHocJavaFile author(
    	String className, Dom dom, VarContext context, Directive...directives )    
    {       	
    	DocState docState = 
        	new DocState( dom, context, directives ); 
        
    	String[] pckgClass = 
            JavaNaming.ClassName.extractPackageAndClassName( className );
    	
    	context.set( JAVA_CLASS_NAME, className, VarScope.INSTANCE );
    	context.set( JAVA_PACKAGE_NAME, pckgClass[ 0 ], VarScope.INSTANCE );
    	context.set( JAVA_SIMPLE_CLASS_NAME, pckgClass[ 1 ], VarScope.INSTANCE );
    	
        //author the Document by binding the {@code Dom} with the {@code context}
        docState = Compose.toState( docState );
        
        if( pckgClass[ 0 ] != null )
        {
            AdHocJavaFile adHocJavaFile =
                new AdHocJavaFile( 
                    pckgClass[ 0 ], 
                    pckgClass[ 1 ], 
                    docState.getTranslateBuffer().toString() );
                
            LOG.debug( "Authored : \"" + pckgClass[ 0 ] + "." + pckgClass[ 1 ] + ".java\"" );
            return adHocJavaFile;
        }
        LOG.debug( "Authored : \"" + pckgClass[ 1 ] + ".java\"" );
        return new AdHocJavaFile( 
            pckgClass[ 1 ], docState.getTranslateBuffer().toString() ); 
    }
    
    /**
     * 
     * @param javaFile
     * @param compilerOptions Optional Compiler Arguments (@see JavacOptions)
     * @return
     */
    public static Class<?> loadClass( 
    	AdHocJavaFile javaFile,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        AdHocClassLoader adHocClassLoader = new AdHocClassLoader();
        return loadClass( adHocClassLoader, javaFile, compilerOptions );        
    }
    
    /**
     * Compiles the javaFile and loads the Class into the 
     * {@code adHocClassLoader}
     * 
     * @param adHocClassLoader the classLoader to load the compiled classes
     * @param javaFile file containing at least one top level Java class
     * (and potentially many nested classes)
     * @param compilerOptions options passed to the Runtime Javac compiler
     * @return the Class (loaded in the ClassLoader)
     */
    public static Class<?> loadClass( 
    	AdHocClassLoader adHocClassLoader, 
    	AdHocJavaFile javaFile,
    	JavacOptions.CompilerOption...compilerOptions )
    {
        Workspace workspace = new Workspace( adHocClassLoader );
        workspace.addCode( javaFile );
        adHocClassLoader = workspace.compile( compilerOptions );
        return adHocClassLoader.find( javaFile.getClassName() );
    }
    
    /**
     * Load the (.java) source for a given Class
     * @param clazz the class to load the source for
     * @return the SourceStream (carrying an inputStream)
     */
    public static SourceLoader.SourceStream sourceFrom ( Class clazz )
    {
        return JavaSourceLoader.INSTANCE.sourceStream( clazz );
    }
    
    /**
     * Load the (.java) source for a given Class using the 
     * <CODE>sourceLoader</CODE> provided.
     * @param sourceLoader the loader for finding and returning the source
     * @param clazz the class to load the source for
     * @return  the SourceStream ( wrapping an inputStream )
     */
    public static SourceLoader.SourceStream sourceFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        return sourceLoader.sourceStream( clazz.getCanonicalName() );
    }
    
    /**
     * Returns the ASTRoot {@code CompilationUnit}
     * @param javaSourceCode
     * @return 
     * @throws Model.LoadException if unable to  load the AST from the javaSourceCode
     */
    public static CompilationUnit astFrom( CharSequence javaSourceCode )
        throws LoadException 
    {
        try
        {
            return JavaASTParser.astFrom( javaSourceCode );
        }
        catch( ParseException pe )
        {
             throw new LoadException(
                "Unable to parse AST from Java Source :" 
                + System.lineSeparator() + javaSourceCode, pe );
        }
    }
    
    /**
     * Loads the astRootNode {@code CompilationUnit} for a 
     * @param topLevelClass a Top Level Java class
     * @return the {@code CompilationUnit} (root AST node)
     * @throws Model.LoadException if unable to parse the {@code CompilationUnit} 
     */
    public static CompilationUnit astFrom( Class topLevelClass )
        throws LoadException
    {
        try
        {
            return JavaASTParser.astFrom(
                sourceFrom( topLevelClass ).getInputStream() );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Unable to parse Model from " + topLevelClass, pe );
        }
    }
    
    /**
     * Gets the "top level" class (the one having a file name) that contains
     * the source/ declaration of the <CODE>clazz</CODE>
     * @param clazz the class to retrieve
     * @return the top Level Class for this class
     */
    private static Class getTopLevelClass( Class clazz )
    {
        if( clazz.getDeclaringClass() == null )
        {
            return clazz;
        }
        return getTopLevelClass( clazz.getDeclaringClass() );
    }
    
    /**
     * returns the AST TypeDeclaration node that represents the clazz
     * the TypeDeclaration instance is a child node of a ast Root 
     * <CODE>CompilationUnit</CODE>
     * @param clazz a Class to read the 
     * @return the {@code TypeDeclaration} ast node
     * @throws LoadException if unable to parse the {@code TypeDeclaration} 
     */
    public static TypeDeclaration astTypeDeclarationFrom( Class clazz )
    {
        Class topLevelClass = getTopLevelClass( clazz ); 
        SourceLoader.SourceStream ss = 
            JavaSourceLoader.INSTANCE.sourceStream( topLevelClass );
        try
        {
            CompilationUnit cu = JavaASTParser.astFrom( ss.getInputStream() );
            return JavaASTParser.findTypeDeclaration( cu, clazz );
        }
        catch( ParseException ex )
        {
            throw new LoadException(
                "Unable to Parse " + topLevelClass + " to extract source for " 
                + clazz, ex );
        }
    }
    
    public static CompilationUnit astFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        Class topLevelClass = getTopLevelClass( clazz ); 
        try
        {
            return JavaASTParser.astFrom( 
                sourceLoader.sourceStream( topLevelClass.getName() + ".java" )
                .getInputStream() );
        }
        catch( ParseException pe )
        {
            throw new LoadException( 
                "Unable to parse contents of class "+ clazz+" ", pe );
        }        
    }
    
     /**
     * Parse and return the _class (lang_model) from the source code
     * @param javaSourceCode the Java source code to parse and compile into a _class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( CharSequence javaSourceCode )
        throws LoadException
    {
        return JavaMetaLangLoader._Class.from( javaSourceCode );
    }
    
    /**
     * Parse and return the _class (lang_model) from the source code
     * @param clazz a Java class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( Class clazz )
        throws LoadException
    {
        return JavaMetaLangLoader._Class.from( clazz );
    }
    
    /**
     * Parse and return the _class (lang_model) from the source code
     * @param sourceLoader the loader for resolving the source for the class
     * @param clazz a Java class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( SourceLoader sourceLoader, Class clazz )
    {
        return JavaMetaLangLoader._Class.from( sourceLoader, clazz );
    }
    
    public static _class _classFrom( CompilationUnit astRoot, Class clazz )
    {
        return JavaMetaLangLoader._Class.from( astRoot, clazz );
    }
    
    public static _interface _interfaceFrom( Class clazz )
    {
        return JavaMetaLangLoader._Interface.from( clazz );
    }
    
    public static _enum _enumFrom( Class clazz )
    {
        return JavaMetaLangLoader._Enum.from( clazz );
    }
    
    // * * * REFLECTION RELATED * * * 
    /** 
     * <UL>
     * <LI>creates an instance of the tailored class constructor 
     * (given the constructor params)
     * <LI>returns an instance of the Tailored Class.
     * </UL>
     * @param theClass the class to create an instance of
     * @param constructorArgs params passed into the constructor
     * @return an Object instance of the tailored class
     */
    public static Object instance( 
        Class<?> theClass, Object... constructorArgs )
    {
        return JavaReflection.instance( theClass, constructorArgs );
    }
    
    /**
     * Invokes the instance method and returns the result
     * @param target the target instance to invoke the method on
     * @param methodName the name of the method
     * @param arguments the parameters to pass to the method
     * @return the result of the call
     */
    public static Object invoke( 
    	Object target, String methodName, Object... arguments )
    {      
        return JavaReflection.invoke( target, methodName, arguments );
    }

    public static Object getStaticFieldValue( Class<?> clazz, String fieldName )
    {
    	return JavaReflection.getStaticFieldValue( clazz, fieldName );
    }
    
    public static Object getStaticFieldValue( Field field )
    {
        return JavaReflection.getStaticFieldValue( field );
    }
    
    public static void setFieldValue( Object instance, String fieldName, Object value )
    {
        JavaReflection.setFieldValue( instance, fieldName, value );
    }
    
    public static Object getFieldValue( Object instanceOrClass, String fieldName )
    {
        return JavaReflection.getFieldValue( instanceOrClass, fieldName );
    }
    
    public static Object invokeStatic( 
        Class<?> clazz, String methodName, Object... args )
    {
        return JavaReflection.invokeStatic( clazz, methodName, args );
    }
    
    public static Method getStaticMethod( 
        Method[] methods, String methodName, Object[] args )
    {
        return JavaReflection.getStaticMethod( methods, methodName, args );
    }
    
    public static List<Method> getMethodsByName( Class clazz, String methodName )
    {
        return JavaReflection.getMethods( clazz, methodName );
    }
    
    public static Method getMethod( 
        Method[] methods, String methodName, Object... args )
    {
        return JavaReflection.getMethod( methods, methodName, args );
    }
}
