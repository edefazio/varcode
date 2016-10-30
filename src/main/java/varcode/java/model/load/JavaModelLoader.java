/*
 * Copyright 2016 eric.
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

import java.io.InputStream;
import varcode.Model.ModelException;

import varcode.java.model._class;
import varcode.source.BaseSourceLoader;
import varcode.source.SourceLoader;
import varcode.source.SourceLoader.SourceStream;


/**
 * Similar to a Class loader, but instead loads "Models"
 * Models are more refined AST.s Parse Trees that represent the model of 
 * things like _class,_interface,_method,_annotation, _import, etc.
 * 
 * the reason for building models of source, is to make generative or 
 * metaprogramming easier.
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
 * _implements impls = myclass.getImplements();
 * _modifiers mods = myClass.getModifiers();
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class JavaModelLoader
{    
    /**
     * 
     */
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
    
    public static class ClassModel
    {   
        public static _class fromClass( Class clazz )
        {
            return fromClass( BaseSourceLoader.INSTANCE, clazz );
        }
        
        public static _class fromClass( SourceLoader sourceLoader, Class clazz )
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
                            "\" with "+sourceLoader.describe() );
                    }
                    
                    //Parse the Declaring Class into an AST
                    CompilationUnit cu = 
                        JavaModelParser.fromInputStream( ss.getInputStream() );
                    
                    ClassOrInterfaceDeclaration classDecl = 
                        JavaModelParser.findMemberNode( cu, clazz );
                    
                    return JavaModelParser.ClassModel.fromCompilationUnit( cu, classDecl );
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
                    JavaModelParser.fromInputStream( ss.getInputStream() );
                
                ClassOrInterfaceDeclaration classDecl = JavaModelParser.getClassNode( cu );
                return JavaModelParser.ClassModel.fromCompilationUnit( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source for \"" + clazz + "\" from " 
                    + sourceLoader.describe(), pe );
            }        
        }
        
        
        public static _class fromInputStream( InputStream sourceInputStream )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaModelParser.fromInputStream( sourceInputStream );
                
                ClassOrInterfaceDeclaration classDecl = JavaModelParser.getClassNode( cu );
                return JavaModelParser.ClassModel.fromCompilationUnit( cu, classDecl );
            }    
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "Unable to parse Source from InputStream", pe );
            }                     
        }
        
        public static _class fromString( String classSource )
        {
            try 
            {
                // parse the file
                CompilationUnit cu = 
                    JavaModelParser.fromString( classSource );
                
                ClassOrInterfaceDeclaration classDecl = JavaModelParser.getClassNode( cu );
                return JavaModelParser.ClassModel.fromCompilationUnit( cu, classDecl );
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
