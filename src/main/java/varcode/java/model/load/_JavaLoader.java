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
package varcode.java.model.load;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model.ModelException;

import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;
import varcode.source.BaseSourceLoader;
import varcode.source.SourceLoader;
import varcode.source.SourceLoader.SourceStream;

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
 
    public static class ModelLoadException 
        extends ModelException
    {        
        public ModelLoadException( String message, Throwable throwable ) 
        {
            super( message, throwable );
        }
        
        public ModelLoadException( String message ) 
        {
            super( message );
        }
        
        public ModelLoadException( Throwable throwable ) 
        {
            super( throwable );
        }
    }
    
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
                        _JavaParser.from( ss.getInputStream() );
                    
                    ClassOrInterfaceDeclaration interfaceDecl = 
                        _JavaParser.findMemberNode( cu, clazz );
                    
                    return _JavaParser._Interface.from( cu, interfaceDecl );
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
                    _JavaParser.from( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration classDecl = _JavaParser.getClassNode( cu );
                return _JavaParser._Interface.from( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Interface Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        
        public static _interface from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    _JavaParser.from( sourceInputStream );
                
                ClassOrInterfaceDeclaration interfaceDecl = 
                    _JavaParser.getInterfaceNode( cu );
                return _JavaParser._Interface.from( cu, interfaceDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse _interface Source from InputStream", pe );
            }                     
        }
        
        public static _interface from( String interfaceSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    _JavaParser.from( interfaceSource );
                
                ClassOrInterfaceDeclaration classDecl = _JavaParser.getInterfaceNode( cu );
                return _JavaParser._Interface.from( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }        
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
                    System.out.println( "IS MEMBER CLASS ");
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
                        _JavaParser.from( ss.getInputStream() );
                    
                    ClassOrInterfaceDeclaration classDecl = 
                        _JavaParser.findMemberNode( cu, clazz );
                    
                    return _JavaParser._Class.fromCompilationUnit( cu, classDecl );
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
                    _JavaParser.from( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration classDecl = _JavaParser.getClassNode( cu );
                return _JavaParser._Class.fromCompilationUnit( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        
        public static _class from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    _JavaParser.from( sourceInputStream );
                
                ClassOrInterfaceDeclaration classDecl = _JavaParser.getClassNode( cu );
                return _JavaParser._Class.fromCompilationUnit( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from InputStream", pe );
            }                     
        }
        
        public static _class from( String classSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    _JavaParser.from( classSource );
                
                ClassOrInterfaceDeclaration classDecl = _JavaParser.getClassNode( cu );
                return _JavaParser._Class.fromCompilationUnit( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }        
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
                        _JavaParser.from( ss.getInputStream() );
                    
                    EnumDeclaration classDecl = 
                        _JavaParser.findEnumNode( cu, clazz );
                    
                    return _JavaParser._Enum.fromCompilationUnit( cu, classDecl );
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
                    _JavaParser.from( ss.getInputStream() );
                
                EnumDeclaration enumDecl = _JavaParser.getEnumNode( cu );
                return _JavaParser._Enum.fromCompilationUnit( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        
        public static _enum from( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    _JavaParser.from( sourceInputStream );
                
                EnumDeclaration enumDecl = _JavaParser.getEnumNode( cu );
                return _JavaParser._Enum.fromCompilationUnit( cu, enumDecl );
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
                    _JavaParser.from( classSource );
                
                EnumDeclaration enumDecl = _JavaParser.getEnumNode( cu );
                return _JavaParser._Enum.fromCompilationUnit( cu, enumDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from String", pe );
            }
        }        
    }
    public static final String N = System.lineSeparator();
}
