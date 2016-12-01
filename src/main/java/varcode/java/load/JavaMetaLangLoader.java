/*
 * Copyright 2016 M. Eric DeFazio
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
package varcode.java.load;

import varcode.load.LoadException;
import varcode.java.metalang.JavaMetaLangCompiler;
import varcode.java.ast.JavaASTParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 * Similar to a Class loader, but instead loads "Models"
 * Models are more refined AST.s Parse Trees that represent the model of 
 * things like (_class,_interface,_enum, _method,_annotation, _import) etc.
 * 
 * the reason for building models of source, is to make generative or 
 * meta-programming easier.
 * for instance the code:
 * <PRE>public class MyClass extends MyBaseClass implements Serializable
 *  {
 *  }
 * </PRE>
 * would be contained in a mutable _class model:
 * <PRE>
 * _class _myClass = _class.of(
 *   "public class MyClass extends MyBaseClass implements Serializable");
 * </PRE>
 *
 * we can easily read facets of the _class model:
 * 
 * System.out.println( _myClass.getName() );
 * _extends ext = myClass.getExtends();
 * _implements impls = myClass.getImplements();
 * _modifiers mods = myClass.getModifiers();
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class JavaMetaLangLoader
{    
    private static final Logger LOG = 
        LoggerFactory.getLogger( JavaMetaLangLoader.class );
 
    public static class _Interface
    {        
        public static _interface from( Class clazz )
        {
            return from(JavaSourceLoader.INSTANCE, clazz );
        }
        
        public static _interface from( SourceLoader sourceLoader, Class clazz )
            throws LoadException
        {
            if( !clazz.isInterface() )
            {
                throw new LoadException( 
                    clazz.getCanonicalName() +" is NOT an Interface " );
            }
            if( clazz.isMemberClass() )
            {
                //need to extract the Nodes from within the declared class
                SourceStream sourceStream = null;
                try
                {
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = clazz.getDeclaringClass();
                    sourceStream = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );    
                    
                    if( sourceStream == null )
                    {
                        throw new LoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    //Parse the Declaring Class into an AST
                    CompilationUnit astRoot = 
                        JavaASTParser.astFrom( sourceStream.getInputStream() );
                    
                    TypeDeclaration astTypeDecl = 
                        JavaASTParser.findTypeDeclaration( astRoot, clazz );
                    
                    if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
                    {
                        ClassOrInterfaceDeclaration astInterfaceDecl = 
                            (ClassOrInterfaceDeclaration)astTypeDecl;
                        if( astInterfaceDecl.isInterface() )
                        {
                            //return JavaASTParser._Interface.from( cu, id );
                            return JavaMetaLangCompiler._interfaceFrom(
                                astRoot, astInterfaceDecl );
                        }                        
                    }
                    throw new LoadException( 
                        clazz + " source not an interface " );                    
                }
                catch( ParseException pe )
                {
                    throw new LoadException(
                        "Error Parsing Source "+ sourceStream.describe(), pe );
                }                
            }
            //top Level Interface
            SourceStream ss = 
                JavaSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
                CompilationUnit astRoot = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration astClassDecl = 
                    JavaASTParser.findInterfaceDeclaration( astRoot, clazz );
                
                return JavaMetaLangCompiler._interfaceFrom( 
                    astRoot, astClassDecl );
                
            }    
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Unable to parse Interface Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }                
    }
    
    /**
     * Gets the top level class i.e. a Class named "TopLevelClass" 
     * (defined in a file like TopLevelClass.java") for a given Class
     *
     * NOTE: since we can get many nest Levels Deep:
     * <PRE>
     * public class TopLevel
     * {
     *    public static class Nested1Level 
     *    {
     *        public static class Nested2Level
     *        {
     *        }
     *    }
     * }
     * </PRE> ... we cant just get a Classes declaringClass, we need to continue
     * recursing until we find the class that has null for DeclaringClass.
     *
     * @param clazz the class to obtain top level class from
     * @return the top level Class that contains the Class (could be itself)
     */
    public static Class getTopLevelClass( Class clazz )
    {
        if( clazz.getDeclaringClass() == null )
        {   //it must be declared in its own file 
            return clazz;
        }
        return getTopLevelClass( clazz.getDeclaringClass() );
    }
    
    public static class _Class
    {   
        /**
         * Loads the _class (langmodel)
         * @param clazz
         * @return 
         */
        public static _class from( Class clazz )
        {
            return _Class.from( JavaSourceLoader.INSTANCE, clazz );
        }
        
        public static _class from( CompilationUnit astRoot, Class clazz )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                JavaASTParser.findClassDeclaration( astRoot, clazz );
            return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );
        }
        
        public static _class from( CompilationUnit astRoot, String className )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                JavaASTParser.findClassDeclaration( astRoot, className );
            return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );
        }
        
        
        public static _class from( CharSequence javaSourceCode )
            throws LoadException
        {
            try
            {
                CompilationUnit astRoot = JavaASTParser.astFrom( javaSourceCode );
                TypeDeclaration astTypeDecl = 
                    JavaASTParser.findRootTypeDeclaration( astRoot );
                String className = astTypeDecl.getName();
                if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration classDecl = 
                        (ClassOrInterfaceDeclaration)astTypeDecl;
                    if( ! classDecl.isInterface() )
                    {
                        return from( astRoot, classDecl.getName() );
                    }
                    throw new LoadException( 
                        className + " is an interface; expected a class" );
                }
                throw new LoadException(
                    "could not find class " + className + " in source " );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaSourceCode, pe );
            }
        }
        
        public static _class from( CharSequence javaSourceCode, String className )
        {
            try
            {
                CompilationUnit astRoot = JavaASTParser.astFrom( javaSourceCode );
                ClassOrInterfaceDeclaration astClassDecl = 
                    JavaASTParser.findClassDeclaration( astRoot, className );
                return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );                
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaSourceCode, pe );
            }
        }
        
        /**
         * 
         * @param sourceLoader
         * @param clazz
         * @return
         * @throws LoadException 
         */
        public static _class from( SourceLoader sourceLoader, Class clazz )
            throws LoadException
        {
            if( clazz.isMemberClass() )
            {
                SourceStream ss = null;
                try
                {
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();

                    ss = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );                          
                    if( ss == null )
                    {
                        throw new LoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    
                    //Parse the Declaring Class into an AST
                    CompilationUnit astRoot = 
                        JavaASTParser.astFrom( ss.getInputStream() );
                    
                    return from( astRoot, clazz );
                }
                catch( ParseException pe )
                {
                    throw new LoadException(
                        "Error Parsing Source "+ ss.describe(), pe );
                }
            }
            SourceStream ss = 
                JavaSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
                CompilationUnit astRoot = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                return from( astRoot, clazz );
            }    
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }                
    }
    
    
    public static class _Enum
    {   
        public static _enum from( Class clazz )
        {
            return _Enum.from(JavaSourceLoader.INSTANCE, clazz );
        }
        
        public static _enum from( SourceLoader sourceLoader, Class clazz )
        {
            if( clazz.isMemberClass() )
            {
                SourceStream ss = null;
                try
                {
                    //System.out.println( "IS MEMBER ENUM ");
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = clazz.getDeclaringClass();
                    ss = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );                          
                    if( ss == null )
                    {
                        throw new LoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    
                    //Parse the Declaring Class into an AST
                    CompilationUnit cu = 
                        JavaASTParser.astFrom( ss.getInputStream() );
                    
                    EnumDeclaration classDecl = 
                        JavaASTParser.findEnumDeclaration( cu, clazz );
                    
                    //return JavaASTParser._Enum.fromCompilationUnit( cu, classDecl );
                    return JavaMetaLangCompiler._enumFrom( cu, classDecl );
                }
                catch( ParseException pe )
                {
                    throw new LoadException(
                        "Error Parsing Source "+ ss.describe(), pe );
                }
            }
            SourceStream ss = 
                JavaSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                EnumDeclaration enumDecl = 
                    JavaASTParser.findEnumDeclaration( cu, clazz );
                return JavaMetaLangCompiler._enumFrom( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }                
    }
    
    public static final String N = System.lineSeparator();
}
