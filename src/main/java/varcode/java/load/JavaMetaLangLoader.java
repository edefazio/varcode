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
import varcode.java.lang.JavaMetaLangCompiler;
import varcode.java.ast.JavaAst;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
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
            return from( JavaSourceLoader.INSTANCE, clazz );
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
                        JavaAst.astFrom( sourceStream.getInputStream() );
                    
                    TypeDeclaration astTypeDecl = 
                        JavaAst.findTypeDeclaration( astRoot, clazz );
                    
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
                    JavaAst.astFrom( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration astClassDecl = 
                    JavaAst.findInterfaceDeclaration( astRoot, clazz );
                
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
        
        public static _interface from( CompilationUnit astRoot, Class clazz )
        {
            return from( astRoot, clazz.getSimpleName() );
        }
        
        public static _interface from( CompilationUnit astRoot, String typeName )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                JavaAst.findInterfaceDeclaration( astRoot, typeName );
            return JavaMetaLangCompiler._interfaceFrom( astRoot, astClassDecl );
        }
        
        public static _interface from( CharSequence javaSourceCode )
            throws LoadException
        {
            try
            {
                CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
                TypeDeclaration astTypeDecl = 
                    JavaAst.findRootTypeDeclaration( astRoot );
                String typeName = astTypeDecl.getName();
                return from( astRoot, typeName );               
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaSourceCode, pe );
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
                JavaAst.findClassDeclaration( astRoot, clazz );
            return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );
        }
        
        public static _class from( 
            CompilationUnit astRoot, String simpleClassName )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                JavaAst.findClassDeclaration( astRoot, simpleClassName );
            return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );
        }
        
        
        public static _class from( CharSequence javaSourceCode )
            throws LoadException
        {
            try
            {
                CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
                TypeDeclaration astTypeDecl = 
                    JavaAst.findRootTypeDeclaration( astRoot );
                String className = astTypeDecl.getName();
                if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration astClassDecl = 
                        (ClassOrInterfaceDeclaration)astTypeDecl;
                    if( ! astClassDecl.isInterface() )
                    {
                        return from( astRoot, astClassDecl.getName() );
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
        
        public static _class from( 
            CharSequence javaCode, String simpleClassName )
        {
            try
            {
                CompilationUnit astRoot = JavaAst.astFrom( javaCode );
                ClassOrInterfaceDeclaration astClassDecl = 
                    JavaAst.findClassDeclaration(astRoot, simpleClassName );
                return JavaMetaLangCompiler._classFrom( astRoot, astClassDecl );                
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaCode, pe );
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
                        JavaAst.astFrom( ss.getInputStream() );
                    
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
                    JavaAst.astFrom( ss.getInputStream() );
                
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
            return _Enum.from( JavaSourceLoader.INSTANCE, clazz );
        }
        
        public static _enum from( CompilationUnit astRoot, Class clazz )
        {
            EnumDeclaration astEnumDecl = 
                JavaAst.findEnumDeclaration( astRoot, clazz );
            return JavaMetaLangCompiler._enumFrom( astRoot, astEnumDecl );
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
                    CompilationUnit astRoot = 
                        JavaAst.astFrom( ss.getInputStream() );
                    
                    EnumDeclaration enumDecl = 
                        JavaAst.findEnumDeclaration( astRoot, clazz );
                    
                    //return JavaASTParser._Enum.fromCompilationUnit( cu, classDecl );
                    return JavaMetaLangCompiler._enumFrom( astRoot, enumDecl );
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
                CompilationUnit astRoot = 
                    JavaAst.astFrom( ss.getInputStream() );
                
                EnumDeclaration enumDecl = 
                    JavaAst.findEnumDeclaration( astRoot, clazz );
                
                return JavaMetaLangCompiler._enumFrom( astRoot, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }              
        
        public static _enum from( CharSequence javaCode )
            throws LoadException
        {
            try
            {
                CompilationUnit astRoot = JavaAst.astFrom(javaCode );
                TypeDeclaration astTypeDecl = 
                    JavaAst.findRootTypeDeclaration( astRoot );
                String enumName = astTypeDecl.getName();
                return from( astRoot, enumName );
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaCode, pe );
            }
        }
        
        public static _enum from( 
            CompilationUnit astRoot, String simpleClassName )
        {
            EnumDeclaration astEnumDecl = 
                JavaAst.findEnumDeclaration( astRoot, simpleClassName );
            return JavaMetaLangCompiler._enumFrom( astRoot, astEnumDecl );
        }
        
        
        public static _enum from( CharSequence javaSourceCode, String className )
        {
            try
            {
                CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
                EnumDeclaration astEnumDecl = 
                    JavaAst.findEnumDeclaration( astRoot, className );
                return JavaMetaLangCompiler._enumFrom(astRoot, astEnumDecl );                
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "Error Parsing AST from Java Source :" + System.lineSeparator() +
                    javaSourceCode, pe );
            }
        }        
    }
    
    public static final String N = System.lineSeparator();
}
