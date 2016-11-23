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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model.ModelLoadException;
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
import varcode.load.BaseSourceLoader;
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
public class _JavaLoader
{    
    private static final Logger LOG = 
        LoggerFactory.getLogger( _JavaLoader.class );
 
    public static class _Interface
    {        
        public static _interface from( Class clazz )
        {
            return from( BaseSourceLoader.INSTANCE, clazz );
        }
        
        public static _interface from( SourceLoader sourceLoader, Class clazz )
        {
            if( !clazz.isInterface() )
            {
                throw new ModelLoadException( 
                    clazz.getCanonicalName() +" is NOT an Interface " );
            }
            if( clazz.isMemberClass() )
            {
                //need to extract the Nodes from within the declared class
                SourceStream ss = null;
                try
                {
                    System.out.println( "IS MEMBER INTERFACE ");
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = clazz.getDeclaringClass();
                    ss = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );    
                    
                    if( ss == null )
                    {
                        throw new ModelLoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    //Parse the Declaring Class into an AST
                    CompilationUnit cu = 
                        JavaASTParser.astFrom( ss.getInputStream() );
                    
                    TypeDeclaration interfaceDecl = 
                        JavaASTParser.findTypeDeclaration( cu, clazz );
                    
                    if( interfaceDecl instanceof ClassOrInterfaceDeclaration )
                    {
                        ClassOrInterfaceDeclaration id = 
                            (ClassOrInterfaceDeclaration)interfaceDecl;
                        if( id.isInterface() )
                        {
                            //return JavaASTParser._Interface.from( cu, id );
                            return Java_LangModelCompiler._interfaceFrom( cu, id );
                        }                        
                    }
                    throw new ModelLoadException( clazz +" source not an interface " );                    
                }
                catch( ParseException pe )
                {
                    throw new ModelLoadException(
                        "Error Parsing Source "+ ss.describe(), pe );
                }                
            }
            //top Level Interface
            SourceStream ss = 
                BaseSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
                CompilationUnit cu = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration classDecl = 
                    JavaASTParser.findClassDeclaration( cu, clazz );
                
                //ClassOrInterfaceDeclaration classDecl = 
                //    JavaASTParser.getClassNode( cu );
                //return JavaASTParser._Interface.from( cu, classDecl );
                return Java_LangModelCompiler._interfaceFrom( cu, classDecl );
                
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Interface Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        /**
        
        @param sourceInputStream
        @return 
        
        public static _interface from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( sourceInputStream );
                
                ClassOrInterfaceDeclaration interfaceDecl = 
                    JavaASTParser.getInterfaceNode( cu );
                //return JavaASTParser._Interface.from( cu, interfaceDecl );
                return Java_LangModelCompiler._interfaceFrom( cu, interfaceDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse _interface Source from InputStream", pe );
            }                     
        }
        */
        
        /*
        public static _interface from( String interfaceSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( interfaceSource );
                
                ClassOrInterfaceDeclaration classDecl = JavaASTParser.getInterfaceNode( cu );
                //return JavaASTParser._Interface.from( cu, classDecl );
                return Java_LangModelCompiler._interfaceFrom( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }     
        */
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
        public static _class from( Class clazz )
        {
            return _Class.from( BaseSourceLoader.INSTANCE, clazz );
        }
        
        public static _class from( SourceLoader sourceLoader, Class clazz )
        {
            if( clazz.isMemberClass() )
            {
                SourceStream ss = null;
                try
                {
                    //System.out.println( "IS MEMBER CLASS ");
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = getTopLevelClass( clazz ); //clazz.getDeclaringClass();
                    //if( declaringClass == null )
                    //{
                        //System.out.println( "Declaring Class is null " );
                    //    declaringClass = clazz;
                    //}
                    //else
                    //{
                    //    System.out.println( "Declaring class " 
                    //        + declaringClass.getCanonicalName() + ".java" );
                    //}
                    ss = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );                          
                    if( ss == null )
                    {
                        throw new ModelLoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    
                    //Parse the Declaring Class into an AST
                    CompilationUnit cu = 
                        JavaASTParser.astFrom( ss.getInputStream() );
                    
                    TypeDeclaration classDecl = 
                        JavaASTParser.findTypeDeclaration( cu, clazz );
                    
                    if( classDecl instanceof ClassOrInterfaceDeclaration )
                    {
                        ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) classDecl;
                        if( !cd.isInterface() )
                        {
                            //return JavaASTParser._Class.fromCompilationUnit( cu, cd );
                            return Java_LangModelCompiler._classFrom( cu, cd );
                        }
                        else
                        {
                            throw new ModelLoadException( clazz + " is an interface " ); 
                        }
                    }
                }
                catch( ParseException pe )
                {
                    throw new ModelLoadException(
                        "Error Parsing Source "+ ss.describe(), pe );
                }
            }
            SourceStream ss = 
                BaseSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
                CompilationUnit cu = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                //ClassOrInterfaceDeclaration classDecl = 
                //    JavaASTParser.getClassNode( cu );
                
                ClassOrInterfaceDeclaration classDecl = 
                    JavaASTParser.findClassDeclaration( cu, clazz );
                
                //return JavaASTParser._Class.fromCompilationUnit( cu, classDecl );
                return Java_LangModelCompiler._classFrom( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        /*
        public static _class from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( sourceInputStream );
                
                ClassOrInterfaceDeclaration classDecl = 
                    JavaASTParser.getClassNode( cu );
                //return JavaASTParser._Class.fromCompilationUnit( cu, classDecl );
                return Java_LangModelCompiler._classFrom( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from InputStream", pe );
            }                     
        }
        */
        
        /*
        public static _class from( String classSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( classSource );
                
                ClassOrInterfaceDeclaration classDecl = JavaASTParser.getClassNode( cu );
                //return JavaASTParser._Class.fromCompilationUnit( cu, classDecl );
                return Java_LangModelCompiler._classFrom( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }   
        */
    }
    
    
    public static class _Enum
    {   
        public static _enum from( Class clazz )
        {
            return _Enum.from( BaseSourceLoader.INSTANCE, clazz );
        }
        
        public static _enum from( SourceLoader sourceLoader, Class clazz )
        {
            if( clazz.isMemberClass() )
            {
                SourceStream ss = null;
                try
                {
                    System.out.println( "IS MEMBER ENUM ");
                    // we have to find the source of the Member class WITHIN the 
                    // source of the declaring class
                    Class declaringClass = clazz.getDeclaringClass();
                    ss = sourceLoader.sourceStream( 
                        declaringClass.getCanonicalName() + ".java" );                          
                    if( ss == null )
                    {
                        throw new ModelLoadException(
                            "Unable to find source for \"" + declaringClass + 
                            "\" with " + sourceLoader.describe() );
                    }
                    
                    //Parse the Declaring Class into an AST
                    CompilationUnit cu = 
                        JavaASTParser.astFrom( ss.getInputStream() );
                    
                    EnumDeclaration classDecl = 
                        JavaASTParser.findEnumDeclaration( cu, clazz );
                    
                    //return JavaASTParser._Enum.fromCompilationUnit( cu, classDecl );
                    return Java_LangModelCompiler._enumFrom( cu, classDecl );
                }
                catch( ParseException pe )
                {
                    throw new ModelLoadException(
                        "Error Parsing Source "+ ss.describe(), pe );
                }
            }
            SourceStream ss = 
                BaseSourceLoader.INSTANCE.sourceStream( clazz );        
            try 
            {
                // parse the file
                //CompilationUnit cu = JavaParser.parse( ss.getInputStream() );
                CompilationUnit cu = 
                    JavaASTParser.astFrom( ss.getInputStream() );
                
                EnumDeclaration enumDecl = 
                    JavaASTParser.findEnumDeclaration( cu, clazz );
                //return JavaASTParser._Enum.fromCompilationUnit( cu, enumDecl );
                return Java_LangModelCompiler._enumFrom( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        /*
        public static _enum from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( sourceInputStream );
                
                EnumDeclaration enumDecl = JavaASTParser.getEnumNode( cu );
                //return JavaASTParser._Enum.fromCompilationUnit( cu, enumDecl );
                return Java_LangModelCompiler._enumFrom( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from InputStream", pe );
            }                     
        }
        
        public static _enum from( String classSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaASTParser.astFrom( classSource );
                
                EnumDeclaration enumDecl = JavaASTParser.getEnumNode( cu );
                //return JavaASTParser._Enum.fromCompilationUnit( cu, enumDecl );
                return Java_LangModelCompiler._enumFrom( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }
        */
    }
    
    public static final String N = System.lineSeparator();
}
