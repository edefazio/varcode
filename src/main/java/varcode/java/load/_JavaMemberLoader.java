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
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
import varcode.java.load._JavaLoader.ModelLoadException;
import varcode.load.BaseSourceLoader;
import varcode.load.SourceLoader;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author eric
 */
public class _JavaMemberLoader
{
    private final SourceLoader sourceLoader;
    
    /** Loads Java Members */
    public static final _JavaMemberLoader INSTANCE = 
        new _JavaMemberLoader( BaseSourceLoader.INSTANCE );
    
    
    public _JavaMemberLoader( SourceLoader sourceLoader )
    {
        this.sourceLoader = sourceLoader;
    }

    public _interface _interfaceOf( Class<?> interfaceClass )
    {
        TypeDeclaration td = memberAST( interfaceClass );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)td;
            if( !coid.isInterface() )
            {
                
                _interface _i = _interface.of( coid.getName() );
                return _JavaParser._Interface.fromInterfaceNode( _i, coid );
            }
            else
            {
                throw new ModelLoadException(
                    interfaceClass + " AST modeled as an interface" );
            }
        }
        throw new ModelLoadException(
            "Type declaration is not a ClassOrInterface " );
    }
    public _class _classOf( Class<?> memberClass )
    {
        TypeDeclaration td = memberAST( memberClass );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration coid = (ClassOrInterfaceDeclaration)td;
            if( !coid.isInterface() )
            {
                
                _class _c = _class.of( coid.getName() );
                return _JavaParser._Class.fromClassNode( _c, coid );
            }
            else
            {
                throw new ModelLoadException(
                    memberClass + " AST modeled as an interface" );
            }
        }
        throw new ModelLoadException(
            "Type declaration is not a ClassOrInterface " );
    }
    
    public <E extends Enum<E>> _enum _enumOf( Class<E> memberEnum )
    {
        if( !memberEnum.isEnum() )
        {
            throw new ModelLoadException( "Class "+ memberEnum+" is not an enum" );
        }
        TypeDeclaration td = memberAST( memberEnum );
        if( td instanceof EnumDeclaration )
        {
            EnumDeclaration coid = (EnumDeclaration)td;
            _enum _e = _enum.of( "enum " + coid.getName() );
            return _JavaParser._Enum.fromEnumNode( _e, coid );            
        }
        throw new ModelLoadException(
            "Type declaration is not an EnumDeclaration" );
    }
    
    /**
     * Returns the TypeDeclaration (AST) 
     * @param memberClass
     * @return 
     */
    public TypeDeclaration memberAST( Class<?> memberClass )
    {
        SourceStream declaringClassStream = sourceLoader.sourceStream( 
            memberClass.getDeclaringClass().getCanonicalName() + ".java" ); 
        
        CompilationUnit cu = null;
        try
        {
            cu = JavaParser.parse( declaringClassStream.getInputStream() );
            TypeDeclaration td = _JavaParser.findMemberNode( cu, memberClass );
            return td;            
        }
        catch( ParseException pe )
        {
            throw new _JavaLoader.ModelLoadException(
                "could not load model for "+ memberClass, pe );
        }
    }    
}
