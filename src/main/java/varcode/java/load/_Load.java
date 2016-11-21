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
package varcode.java.load;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import varcode.Model.ModelLoadException;
import varcode.java.lang._class;
import varcode.java.lang._component;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
import varcode.load.BaseSourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _Load
{
    public static final _Load INSTANCE = new _Load( BaseSourceLoader.INSTANCE );
    
    private final SourceLoader sourceLoader;
    
    public _Load( SourceLoader sourceLoader )
    {
        this.sourceLoader = sourceLoader;
    }
    
    public static _class _classOf( Class clazz )
    {
        //return INSTANCE.load_class( clazz );
        return _Model._classOf( INSTANCE.sourceLoader, clazz );
    }
    
    public static _interface _interfaceOf( Class clazz )
    {
        return _Model._interfaceOf( INSTANCE.sourceLoader, clazz );
    }
    
    public static _enum _enumOf( Class clazz )
    {
        return _Model._enumOf( INSTANCE.sourceLoader, clazz );
        //return INSTANCE.load_enum( clazz );
    }
    
    public SourceStream sourceOf( Class clazz )
    {
        return JavaSource.fromClass(sourceLoader, clazz );
    }
    
    public TypeDeclaration astNodeOf( Class clazz )
    {
        return AST.ofClass( sourceLoader, clazz );          
    }
    
    public _component modelOf( Class clazz )
    {
        if( clazz.isInterface() )
        {
            return _interfaceOf( clazz );
        }
        if( clazz.isEnum() )
        {
            return _enumOf( clazz );
        }
        return _classOf( clazz );
    }
    
    /*
    public _interface _interfaceOf( Class clazz )
    {
        return _Model._interfaceOf( sourceLoader, clazz );
    }
    
    public _class load_class( Class clazz )
    {
        return _Model._classOf( sourceLoader, clazz );
    }
    
    public _enum load_enum( Class clazz )
    {
        return _Model._enumOf( sourceLoader, clazz );
    }
    */
    
    
    public static class JavaSource
    {    
        private final SourceLoader sourceLoader;
    
        /** Loads Java code member classes, enums and interfaces */
        //public static final JavaSource INSTANCE = 
       //     new JavaSource( BaseSourceLoader.INSTANCE );
    
        /**
        * Loads Member classes, 
        * @param sourceLoader 
        */
        public JavaSource( SourceLoader sourceLoader )
        {
            this.sourceLoader = sourceLoader;
        }
    
        public static SourceStream fromClass( 
            SourceLoader sourceLoader, Class<?> memberClass )
        {
            SourceStream declaringClassStream  = sourceStream( sourceLoader, 
                memberClass.getDeclaringClass().getCanonicalName() + ".java" ); 
        
            CompilationUnit cu = null;
            try
            {
                cu = JavaParser.parse( declaringClassStream.getInputStream() );
                TypeDeclaration td = JavaASTParser.findTypeDeclaration( cu, memberClass );
                JavaMemberSourceStream sss = new JavaMemberSourceStream( 
                    declaringClassStream, memberClass.getCanonicalName(), td.toString() );
                return sss;
            }
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "could not load model for "+ memberClass, pe );
            }
        }
    
        public static SourceStream sourceStream( 
            SourceLoader sourceLoader, String sourceId )
        {
            // pack.age.with.DeclaringClass$memberClass
            int indexOf$ = sourceId.indexOf( "$" );
            if( indexOf$ < 0 )
            {
                return sourceLoader.sourceStream( sourceId );
            }
            else
            {
                String declaringClass = sourceId.substring( 0, indexOf$ );
                
                SourceStream declareSource = 
                    sourceLoader.sourceStream( declaringClass + ".java" );
                
                CompilationUnit cu = null;
                try
                {
                    //create the AST for the source
                    cu = JavaParser.parse( declareSource.getInputStream() );
                    
                    String name = sourceId.substring( sourceId.lastIndexOf( "$" ) + 1 );
                    TypeDeclaration td = JavaASTParser.findTypeDeclaration( cu, name );
                    JavaMemberSourceStream sss = new JavaMemberSourceStream( 
                        declareSource, sourceId, td.toString() );
                    return sss;
                }
                catch( ParseException pe )
                {
                    throw new ModelLoadException(
                        "could not load model for "+ sourceId, pe );
                }
            }
        }
    }
    
    /**
     * A Source Stream that aliases a Member Class withinin a Declared Class
     * Source code
     */
    public static class JavaMemberSourceStream
        implements SourceLoader.SourceStream
    {
        public final SourceLoader.SourceStream parentSourceStream;
        public final String sourceId;
        public final String memberSource;
    
        public JavaMemberSourceStream( 
            SourceLoader.SourceStream parentSourceStream, String sourceId, String memberSource )
        {
            this.parentSourceStream = parentSourceStream;
            this.sourceId = sourceId;
            this.memberSource = memberSource;
        }
            
        @Override
        public InputStream getInputStream()
        {
            return new ByteArrayInputStream( memberSource.getBytes() );
        }

        @Override
        public String getSourceId()
        {
            return sourceId;
        }

        @Override
        public String describe()
        {
            return "[MemberSourceStream of " + sourceId + " in " + parentSourceStream.describe() +"]";
        }

        @Override
        public String asString()
        {
            return memberSource;
        }    
        
        @Override
        public String toString()
        {
            return asString();
        }
        
        public SourceLoader.SourceStream getParentSourceStream()
        {
            return this.parentSourceStream;
        }
    }
 
    /**
     * Calls the JavaParser AST to convert the source of a Class 
     * into an AST
     */
    public static class AST
    {
        /**
         * Returns the TypeDeclaration (AST) parsed by JavaParser for the class
         * @param sourceLoader sourceLoader
         * @param clazz the class to load AST for
         * @return the TypeDeclaration (AST NODE) for the class
         * 
         */
        public static TypeDeclaration ofClass( 
            SourceLoader sourceLoader, Class<?> clazz )
        {            
            SourceStream declaringClassStream = null;
            if( clazz.getDeclaringClass() == null )
            {
                declaringClassStream = 
                    sourceLoader.sourceStream( clazz.getCanonicalName()+ ".java" );
            }
            else
            {
                declaringClassStream = sourceLoader.sourceStream( 
                    clazz.getDeclaringClass().getCanonicalName() + ".java" ); 
            }
            CompilationUnit cu = null;
            try
            {
                cu = JavaParser.parse( declaringClassStream.getInputStream() );
                TypeDeclaration td = JavaASTParser.findTypeDeclaration( cu, clazz );
                return td;            
            }
            catch( ParseException pe )
            {
                throw new ModelLoadException(
                    "could not load model for " + clazz, pe );
            }
        }    
    }
    
    public static class _Model
    {
        public static _interface _interfaceOf( 
            SourceLoader sourceLoader, Class<?> interfaceClass )
        {
            TypeDeclaration td = AST.ofClass( sourceLoader, interfaceClass );
            if( td instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)td;
                if( coid.isInterface() )
                {                
                    _interface _i = _interface.of( "interface " + coid.getName() );
                    //return JavaASTParser._Interface.fromInterfaceNode( _i, coid );
                    return JavaASTToLangModel.fromInterfaceNode( _i, coid );
                }
                else
                {
                    throw new ModelLoadException(
                        interfaceClass + " AST modeled as an class" );
                }
            }
            throw new ModelLoadException(
                "Type declaration is not a ClassOrInterface " );
        }
        
        public static _class _classOf( Class<?> clazz )
        {
            return INSTANCE._classOf( clazz );
        }
        
        public static _interface _interfaceOf( Class<?> clazz )
        {
            return INSTANCE._interfaceOf( clazz );
        }
        
        public static _enum _enumOf( Class<?>clazz )
        {
            return INSTANCE._enumOf( clazz );
        }
        
        public static _class _classOf( SourceLoader sourceLoader, Class<?> clazz )
        {
            TypeDeclaration td = AST.ofClass( sourceLoader, clazz );
            if( td instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)td;
                if( !coid.isInterface() )
                {                
                    _class _c = _class.of( coid.getName() );
                    //return JavaASTParser._Class.fromClassNode( _c, coid );
                    return JavaASTToLangModel.fromClassNode( _c, coid );
                }
                else
                {
                    throw new ModelLoadException(
                        clazz + " AST modeled as an interface" );
                }
            }
            throw new ModelLoadException(
               "Type declaration is not a ClassOrInterface " );
        }   
    
        public static <E extends Enum<E>> _enum _enumOf( 
            SourceLoader sourceLoader, Class<E> enumClazz )
        {
            if( !enumClazz.isEnum() )
            {
                throw new ModelLoadException( 
                    "Class " + enumClazz + " is not an enum" );
            }
            TypeDeclaration td = AST.ofClass(sourceLoader, enumClazz );
            if( td instanceof EnumDeclaration )
            {
                EnumDeclaration coid = (EnumDeclaration)td;
                _enum _e = _enum.of( "enum " + coid.getName() );
                //return JavaASTParser._Enum.fromEnumNode( _e, coid ); 
                return JavaASTToLangModel.fromEnumNode( _e, coid );
            }
            throw new ModelLoadException(
                "Type declaration is not an EnumDeclaration" );
        }
    }
}
