/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this toJavaFile except in compliance with the License.
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

import varcode.java.load.BaseJavaSourceLoader;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.JavaSourceFile;
import varcode.java.ast.JavaAst;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;
import varcode.java.load._JavaLoad;
import varcode.LoadException;
import varcode.java.adhoc.AdHoc;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.ast.JavaCodeFormatVisitor;
import varcode.java.model._annotationType;
import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;

/**
 * Unified API for varcode Java functionality:
 * 
 * Unifies loading related to loading porting, compiling, invoking 
 * _Java models from Java source, ASTs, from the classPath
 * <UL>
 *  <LI>finding and <B>loading .java source</B> code for runtime classes
 *  {@link #sourceFrom(Class) sourceFrom}, 
 *  {@link #sourceFrom(SourceLoader, Class)}
 *
 *  <LI><B>loading / building ASTs</B> (Abstract syntax trees) from .java 
 *  source code at runtime {@link #astFrom(CharSequence)} {@link #astFrom(Class)}
 * {@link #astFrom(SourceLoader, Class)} 
 * {@link #astTypeDeclarationFrom(CompilationUnit, clazz)}
 * {@link #astTypeDeclarationFrom(clazz)} 
 * 
 * <LI><B>loading / building models</B> (_class, _interface, _enum, _annotationType)
 * from Classes, Strings, ASTs (for modeling source code) 
 * 
 * {@link #_classFrom(java.lang.CharSequence) }
 * {@link #_classFrom(java.lang.Class) }
 * {@link #_classFrom(varcode.source.Source.SourceLoader, java.lang.Class)}
 * {@link #_classFrom(varcode.source.Source.SourceLoader, java.lang.Class, varcode.java.ast.JavaCodeFormatVisitor)}
 * 
 * {@link #_enumFrom(java.lang.CharSequence) }
 * {@link #_enumFrom(java.lang.Class) }
 * {@link #_enumFrom(varcode.source.Source.SourceLoader, java.lang.Class) }
 * 
 * {@link #_interfaceFrom(java.lang.CharSequence) }
 * {@link #_interfaceFrom(java.lang.Class) }
 * {@link #_interfaceFrom(java.lang.Class, varcode.java.ast.JavaCodeFormatVisitor) 
 * {@link #_interfaceFrom(varcode.source.Source.SourceLoader, java.lang.Class) 
 * 
 * {@link #_annotationTypeFrom(java.lang.CharSequence)}
 * {@link #_annotationTypeFrom(java.lang.Class) }
 * {@link #_annotationTypeFrom(com.github.javaparser.ast.CompilationUnit, java.lang.Class) }
 * {@link #_annotationTypeFrom(varcode.source.Source.SourceLoader, java.lang.Class) }
 * 
 *  <LI><B>authoring</B> {@code AdHocJavaFile}s (.java source code) 
 * from Templates. 
 * {@link #authorJavaFile(String, Template, Object...)}
 * {@link #authorJavaFile(String, Template, Context, Directive...)}
 * 
 *  <LI><B>compiling/loading</B> {@code AdHocJavaFile}s (.java code) into Classes
 *  {@link #compileAndLoadClass(AdHocJavaFile,CompilerOption...)
 *  {@link #compileAndLoadClass(AdHocClassLoader, AdHocJavaFile, CompilerOption...)
 * 
 * <LI><B>Reflection</B> 
 * <UL>
 *  <LI>reflectively instantiating new Objects: 
 *   {@link #instance(java.lang.Class, java.lang.Object...) }
 *  <LI>reflectively invoking methods :
 *   {@link #call(java.lang.Class, java.lang.String, java.lang.Object...) }
 *   {@link #call(java.lang.Object, java.lang.String, java.lang.Object...) }
 *   {@link #callMain(java.lang.Class, java.lang.String...) }
 *   {@link #callMain(java.lang.Object, java.lang.String...) }
 *  <LI>reflectively setting/getting fields 
 *   {@link #setFieldValue(java.lang.Object, java.lang.String, java.lang.Object) }
 *   {@link #set(java.lang.Object, java.lang.String, java.lang.Object) }
 *   {@link #getFieldValue(java.lang.reflect.Field) }
 *   {@link #getFieldValue(java.lang.Class, java.lang.String) }
 *   {@link #getFieldValue(java.lang.Object, java.lang.String) }
 * </UL>
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Java 
{    
    /**
     * Authors an AdHocJavaFile given the classname, {@code Template} 
     * and keyValuePairs used in the context.
     * 
     * <PRE>
     * AdHocJavaFile adhoc = Java.authorJavaFile(
     *     "example.MyClass", 
     *     BindML.compile( 
     *         "package {+packageName+};" + N + 
     *         "public class {+className+}" + N + 
     *         "{" + N + 
     *         "    public static void main(String[] args) " + N + 
     *         "    { " + N
     *         "        System.out.println( \"{+message+}\" );" + N +
     *         "    }" + N + 
     *         "}"),
     *     "message", "Hello World!" );
     * </PRE>
     * NOTE: {+packageName+} and {+className+} are used as default parameters
     * (given by the className passed in)
     * 
     * @param className the name of the class to be 
     * @param template the template to be specialized
     * @param keyValuePairs parameters comprising the context
     * @return the Authored AdHocJavaFile containing the (.java) source
     */
    public static JavaSourceFile authorJavaFile( 
        String className, Template template, Object...keyValuePairs )
    {
        return JavaAuthor.toJavaFile( className, template, keyValuePairs );
    }
    
    /**
     * Authors and returns an {@code AdHocJavaFile} with name {@code className} 
     * using a {@code Dom} and based on the specialization provided in the
     * {@code context} and {@code directives}
     * 
     * @param className the class name to be authored (i.e. "io.varcode.ex.MyClass") 
     * @param template the document object template to be specialized
     * @param context the specialization/functionality to be applied to the Dom
     * @param directives optional pre and post processing commands 
     * @return an AdHocJavaFile
     */
    public static JavaSourceFile authorJavaFile(
    	String className, Template template, Context context, Directive...directives )    
    {       
        return JavaAuthor.toJavaFile( className, template, context, directives );    	
    }
    
    /**
     * Compiles and Loads an AdHocJavaFile using the runtime JAVAC 
     * using the compilerOptions and return a new Class (loaded into a
     * new AdHocClassLoader.
     * 
     * @param javaFile
     * @param compilerOptions Optional Compiler Arguments (@see JavacOptions)
     * @return a new Class compiled and loaded into a new AdHocClassLoader
     * @throws JavacException if unable to compile the toJavaFile
     
    public static Class<?> compileAndLoadClass( 
    	JavaSourceFile javaFile,
    	JavacOptions.CompilerOption...compilerOptions )
        throws JavacException
    {
        AdHocClassLoader adHocClassLoader = 
            AdHoc.compile( JavaSourceFolder.of( javaFile ),              
                compilerOptions );
        return adHocClassLoader.findClass( javaFile );
              
    }
    */ 
    
    /**
     * Load the (.java) source for a given Class
     * @param clazz the class to load the source for
     * @return the SourceStream (carrying an inputStream)
     */
    public static SourceStream sourceFrom ( Class clazz )
    {
        return BaseJavaSourceLoader.INSTANCE.sourceStream( clazz );
    }
    
    /**
     * Load the (.java) source for a given Class using the 
     * <CODE>sourceLoader</CODE> provided.
     * @param sourceLoader the loader for finding and returning the source
     * @param clazz the class to load the source for
     * @return  the SourceStream ( wrapping an inputStream )
     */
    public static SourceStream sourceFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        return sourceLoader.sourceStream( clazz.getCanonicalName() );
    }
    
    /**
     * Returns the ASTRoot {@code CompilationUnit}
     * @param javaSourceCode
     * @return 
     * @throws LoadException if unable to  load the AST _annotationTypeFrom 
     * the javaSourceCode
     */
    public static CompilationUnit astFrom( CharSequence javaSourceCode )
        throws LoadException 
    {
        try
        {
            return JavaAst.astFrom( javaSourceCode );
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
     * @throws LoadException if unable to parse the {@code CompilationUnit} 
     */
    public static CompilationUnit astFrom( Class topLevelClass )
        throws LoadException
    {
        try
        {
            return JavaAst.astFrom(
                sourceFrom( topLevelClass ).getInputStream() );
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "Unable to parse Model from " + topLevelClass, pe );
        }
    }
    
    /**
     * Gets the "top level" class (the one having a toJavaFile name) that contains
 the source/ declaration of the <CODE>clazz</CODE>
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
     * Loads and return the Ast Type Declaration for the class within
     * 
     * @param astRoot the root AST node
     * @param clazz the class
     * @return the TypeDeclaration (astNode) for the Clazz
     * @throws LoadException if unable to load the Class AST within the AST root
     */
    public static TypeDeclaration astTypeDeclarationFrom( 
        CompilationUnit astRoot,
        Class clazz )
        throws LoadException
    {
        try
        {
            return JavaAst.findTypeDeclaration( astRoot, clazz );
        }
        catch( ParseException pe )
        {
            throw new LoadException("Unable to load " + clazz + " from AST", pe );
        }
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
        SourceStream ss = 
            BaseJavaSourceLoader.INSTANCE.sourceStream( topLevelClass );
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( ss.getInputStream() );
            return astTypeDeclarationFrom( astRoot, clazz );
        }
        catch( ParseException ex )
        {
            throw new LoadException(
                "Unable to Parse " + topLevelClass + " to extract source for " 
                + clazz, ex );
        }
    }
    
    /**
     * 
     * @param sourceLoader (strategy) where to load the source code
     * @param clazz the class to load source for
     * @return the AST compilation Unit
     */
    public static CompilationUnit astFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        Class topLevelClass = getTopLevelClass( clazz ); 
        try
        {
            return JavaAst.astFrom( 
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
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param javaSourceCode the Java source code to parse and compile into a _class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( CharSequence javaSourceCode )
        throws LoadException
    {
        return _JavaLoad._classFrom( javaSourceCode, 
            new FormatJavaCode_AllmanScanStyle() );
    }

    /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param javaSourceCode the Java source code to parse and compile into a _class
     * @param codeFormatter
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( 
        CharSequence javaSourceCode, JavaCodeFormatVisitor codeFormatter )
        throws LoadException
    {
        return _JavaLoad._classFrom( javaSourceCode, codeFormatter );
    }    
    
    /**
     * Loads an Annotation Type Definition _annotationTypeFrom a String
     * i.e.<PRE>
     * _annotationType _at = Java._annotationTypeFrom( 
     *     "@interface MyAnn { int count() default 1; }" );
     * </PRE>
     * @param javaSourceCode the java source code for the Annotation Declaration
     * @return an _annotationType model for the AnnotationType
     */
    public static _annotationType _annotationTypeFrom( 
        CharSequence javaSourceCode )
    {
        return _JavaLoad._annotationTypeFrom( javaSourceCode );
    }
    
     /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param sourceLoader the loader for resolving the source for the class
     * @param clazz a Java class
     * @return an _annotationType (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _annotationType _annotationTypeFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        return _JavaLoad._annotationTypeFrom( sourceLoader, clazz );
    }
    
    /**
     * Parse and return the _annotationType (model) by reading .Java source 
     * code for the class
     * @param clazz a Java class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _annotationType _annotationTypeFrom( Class clazz )
        throws LoadException
    {
        return _JavaLoad._annotationTypeFrom( clazz );
    }
    
    /**
     * 
     * @param astRoot the root of the AST
     * @param clazz the class
     * @return the _annotationType based on the source of the Class
     */
    public static _annotationType _annotationTypeFrom( 
        CompilationUnit astRoot, Class clazz )
    {
        return _JavaLoad._annotationTypeFrom( astRoot, clazz );
    }
    
    /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param clazz a Java class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( Class clazz )
        throws LoadException
    {
        return _JavaLoad._classFrom( clazz );
    }
    
    /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param sourceLoader the loader for resolving the source for the class
     * @param clazz a Java class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( 
        SourceLoader sourceLoader, Class clazz )
    {
        return _classFrom( 
            sourceLoader, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param sourceLoader the loader for resolving the source for the class
     * @param clazz a Java class
     * @param codeFormatter
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _class _classFrom( 
        SourceLoader sourceLoader, Class clazz, 
        JavaCodeFormatVisitor codeFormatter )
    {
        return _JavaLoad._classFrom( sourceLoader, clazz, codeFormatter );
    }
    
    /**
     * 
     * @param astRoot
     * @param clazz
     * @return 
     */
    public static _class _classFrom( 
        CompilationUnit astRoot, Class clazz )
    {
        return _classFrom( astRoot, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _class _classFrom( 
        CompilationUnit astRoot, Class clazz, JavaCodeFormatVisitor codeFormatter )        
    {
        return _JavaLoad._classFrom( astRoot, clazz, codeFormatter );
    }
    
    public static _interface _interfaceFrom( Class clazz )
    {
        return _interfaceFrom( clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _interface _interfaceFrom( 
        Class clazz, JavaCodeFormatVisitor codeFormatter )
    {
        return _JavaLoad._interfaceFrom( clazz, codeFormatter );
    }

    public static _interface _interfaceFrom( 
         SourceLoader sourceLoader, Class clazz )
    {
        return _JavaLoad._interfaceFrom( 
            sourceLoader, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    
     /**
     * Parse and return the _class (lang_model) _annotationTypeFrom the source code
     * @param javaSourceCode the Java source code to parse and compile into a _class
     * @return a _class (lang_model) representing the java source
     * @throws LoadException if unable to load the _class 
     */
    public static _interface _interfaceFrom( CharSequence javaSourceCode )
        throws LoadException
    {
        return _JavaLoad._interfaceFrom( 
            javaSourceCode, new FormatJavaCode_AllmanScanStyle() );
    }
    
    
    public static _enum _enumFrom( Class clazz )
    {
        return _JavaLoad._enumFrom( clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _enum _enumFrom( SourceLoader sourceLoader, Class clazz )
    {
        return _JavaLoad._enumFrom( 
            sourceLoader, clazz, new FormatJavaCode_AllmanScanStyle() );
    }
    
    public static _enum _enumFrom( CharSequence enumSourceCode )
    {
        return _JavaLoad._enumFrom( 
            enumSourceCode, new FormatJavaCode_AllmanScanStyle() );
    }
    
    // * * * REFLECTION RELATED * * * //
    
    
    public static Object instance( _class _c, Object...constructorArgs )
    {
        AdHocClassLoader adHocClassLoader = 
            AdHoc.compile( _c.toJavaFile( ) );
        
        Class c = adHocClassLoader.findClass( _c );
        //Class c = adHocClassLoader.classByName( _c.getQualifiedName() );
        return instance( c, constructorArgs );
    }
    
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
     * calls the main method on the target class
     * @param targetClass
     * @param arguments 
     */
    public static void callMain( Class targetClass, String... arguments )
    {
        JavaReflection.invokeMain( targetClass, arguments );
    }
    
    /**
     * 
     * @param target
     * @param arguments 
     */
    public static void callMain( Object target, String...arguments )
    {        
        Java.callMain( target.getClass(), arguments );        
    }
    
    /**
     * Invokes the instance method and returns the result
     * @param target the target instance to call the method on
     * @param methodName the name of the method
     * @param arguments the parameters to pass to the method
     * @return the result of the call
     */
    public static Object call( 
    	Object target, String methodName, Object... arguments )
    {      
        return JavaReflection.invoke( target, methodName, arguments );
    }

    /**
     * Tries to get from a field on the instance or class, THEN tries
     * calling a getter method for a property.
     * 
     * NOTE: this is for CONVENIENCE, NOT for performance critical code
     * (you want to use the more specific variants: 
     * {@link #getFieldValue(java.lang.Class, java.lang.String) }
     * {@link #call(java.lang.Class, java.lang.String, java.lang.Object...) }
     * 
     * to directly access the field or method appropriately
     * 
     * @param instanceOrClass the class or instance
     * @param propertyName the name i.e. "count"
     * @return the value
     * @throws JavaException if it fails
     */
    public static Object get( Object instanceOrClass, String propertyName )
        throws JavaException 
    {
        return JavaReflection.get( instanceOrClass, propertyName );
    }
    
    public static Object getFieldValue( Class<?> clazz, String fieldName )
    {
    	return JavaReflection.getStaticFieldValue( clazz, fieldName );
    }
    
    public static Object getFieldValue( Field field )
    {
        return JavaReflection.getStaticFieldValue( field );
    }
    
    public static void set( Object instance, String fieldName, Object value )
    {
        JavaReflection.set( instance, fieldName, value );
    }
    
    public static void setFieldValue( Object instance, String fieldName, Object value )
    {
        JavaReflection.setFieldValue( instance, fieldName, value );
    }
    
    
    public static Object getFieldValue( Object instanceOrClass, String fieldName )
    {
        return JavaReflection.getFieldValue( instanceOrClass, fieldName );
    }
    
    public static Object call( 
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
