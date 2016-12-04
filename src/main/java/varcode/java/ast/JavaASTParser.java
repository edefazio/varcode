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
package varcode.java.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.load.LoadException;

/**
 * Reads in Java Source as text and converts the text into an AST 
 * (Abstract Syntax Tree) {@code CompilationUnit}... And converts the AST
 * {@code CompilationUnit} into a java "Lang Model" (_class, _interface, _enum)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaASTParser
{
    ; //singleton enum idiom
    
    /** 
     * read the .java source from the input stream and construct a 
     * {@code CompilationUnit}
     * @param javaSourceInputStream .java source as an inputStream
     * @return the CompilationUnit root node of the parsed AST
     * @throws com.github.javaparser.ParseException if parsing failed
     */
    public static CompilationUnit astFrom( 
        InputStream javaSourceInputStream )            
        throws ParseException
    {
        return JavaParser.parse( javaSourceInputStream );           
    }
     
    /** 
     * read the .java source from the input stream and construct a 
     * {@code CompilationUnit}
     * @param javaSourceCode .java source as an inputStream
     * @return the CompilationUnit root node of the parsed AST
     * @throws com.github.javaparser.ParseException if parsing failed
     */
    public static CompilationUnit astFrom( CharSequence javaSourceCode )
        throws ParseException 
    {
        return astFrom( javaSourceCode.toString() );
    }
    
    /**
     * Loads the AST from the java source code
     * @param javaSourceCode the java source code
     * @return the CompilationUnit root node of the parsed AST
     * @throws com.github.javaparser.ParseException if parsing failed
     */
    public static CompilationUnit astFrom( String javaSourceCode )
        throws ParseException
    {
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( javaSourceCode.getBytes() );
        return JavaASTParser.astFrom( bais );
    }
    
    /**
     * Looks into the AST nodes (Types) of a {@code CompilationUnit} to return 
     * the "Top Level" type
     * @param astRoot the AST root Node
     * @return the top Level {@code TypeDeclaration}
     * @throws LoadException if unable to find the root type declaration
     */
    public static TypeDeclaration findRootTypeDeclaration( 
        CompilationUnit astRoot )
        throws LoadException
    {
        List<TypeDeclaration> astTypes =  astRoot.getTypes();
        
        if( astTypes.size() == 1 )
        {   //if there is ONLY 1 declaration, that is it
            return astTypes.get( 0 );
        }
        //lets try to be smart to find the "root" node
        List<TypeDeclaration> astTypeCandidates = 
            new ArrayList<TypeDeclaration>();
        
        for( int i = 0; i < astTypes.size(); i++ )
        {   //I guess get the only non-static ones
            if( (astTypes.get( i ).getModifiers() 
               & Modifier.STATIC ) != 0 )
            {
                astTypeCandidates.add(astTypes.get( i ) ); 
            }
        }
        if( astTypeCandidates.size() == 1 )
        {   //only 1 non-static candidate, return that
            return astTypeCandidates.get( 0 );
        }
        
        if( astTypes.isEmpty() )
        {
            throw new LoadException( 
                "No Type Declarations in AST : "+ System.lineSeparator() + 
                astRoot );
        }
        //or I give up, just return the first one
        return astTypes.get( 0 );
    }
    
    /**
     * 
     * @param astRoot the root AST node
     * @param enumName the name of the Enum
     * @return the EnumDeclaration astNode
     * @throws LoadException if unable to find the enum declaration
     */
    public static EnumDeclaration findEnumDeclaration(
        CompilationUnit astRoot, String enumName )
        throws LoadException    
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration( astRoot, enumName );
        if( astTypeDecl instanceof EnumDeclaration )
        {
            return (EnumDeclaration)astTypeDecl;            
        }
        throw new LoadException( 
            "Could not find interface declaration for \""
                + enumName + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the EnumTypeDeclaration AST node
     * @throws LoadException if unable to find the enum declaration
     */
    public static EnumDeclaration findEnumDeclaration( 
        CompilationUnit astRoot, Class clazz )
        throws LoadException
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration( astRoot, clazz );
        if( astTypeDecl instanceof EnumDeclaration )
        {
            return (EnumDeclaration) astTypeDecl;
        }
        throw new LoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param interfaceName the name of the interface
     * @return the ClassOrInterfaceDeclaration AST node
     * @throws LoadException if unable to find the interface declaration
     */
    public static ClassOrInterfaceDeclaration findInterfaceDeclaration( 
        CompilationUnit astRoot, String interfaceName )
        throws LoadException
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration( astRoot, interfaceName );
        if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                (ClassOrInterfaceDeclaration)astTypeDecl;
            
            if( astClassDecl.isInterface() )
            {
                return astClassDecl;
            }
        }
        throw new LoadException( 
            "Could not find interface declaration for \""
            + interfaceName + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the EnumTypeDeclaration AST node
     * @throws LoadException if unable to find the interface declaration
     */
    public static ClassOrInterfaceDeclaration findInterfaceDeclaration( 
        CompilationUnit astRoot, Class clazz )
        throws LoadException
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration( astRoot, clazz );
        if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                (ClassOrInterfaceDeclaration)astTypeDecl;
            
            if( astClassDecl.isInterface() )
            {
                return astClassDecl;
            }
        }
        throw new LoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param simpleClassName the (simple) class Name ClassOrInterfaceDeclaration 
     * AST Node from
     * @return the EnumTypeDeclaration AST node
     * @throws LoadException if unable to resolve the class declaration
     */
    public static ClassOrInterfaceDeclaration findClassDeclaration( 
        CompilationUnit astRoot, String simpleClassName )
        throws LoadException
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration(astRoot, simpleClassName );
        if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                (ClassOrInterfaceDeclaration)astTypeDecl;
            
            if( ! astClassDecl.isInterface() )
            {
                return astClassDecl;
            }
        }
        throw new LoadException( 
            "Could not find class declaration for \""
            + simpleClassName + "\"" );
    }
    
    /**
     * finds and returns the {@code ClassOrInterfaceTypeDeclaration} (AST node) 
     * for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the ClassOrInterfaceTypeDeclaration AST node     
     * @throws LoadException if unable to find the class declaration
     */
    public static ClassOrInterfaceDeclaration findClassDeclaration( 
        CompilationUnit astRoot, Class clazz )
        throws LoadException
    {
        TypeDeclaration astTypeDecl = findTypeDeclaration( astRoot, clazz );
        if( astTypeDecl instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration astClassDecl = 
                (ClassOrInterfaceDeclaration)astTypeDecl;
            if( ! astClassDecl.isInterface() )
            {
                return astClassDecl;
            }
        }
        throw new LoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    /**
     * Recursively search through the nodes of the AST
     * to find a {@code TypeDeclaration} node named <CODE>name</CODE>
     * and return that node.
     * @param astRoot the CompilationUnit (top Level AST node)
     * @param typeName the name of the TypeDeclaration to find
     * @return the TypeDeclaration node (and its children)
     * @throws LoadException if unable to find a TypeDeclaration
     */
    public static TypeDeclaration findTypeDeclaration( 
        CompilationUnit astRoot, String typeName )
        throws LoadException
    {
        List<TypeDeclaration> astTypes =  astRoot.getTypes();
        //System.out.println( "LOOKING FOR \"" + name + "\"" );
        for( int i = 0; i < astTypes.size(); i++ )
        {
            //System.out.println( "FOund "+ types.get( i ).getName() );
            TypeDeclaration astTypeDecl = astTypes.get( i );
            
            if( astTypeDecl.getName().equals( typeName ) )
            {
                return (TypeDeclaration)astTypeDecl;
            }
            else
            {
                List<BodyDeclaration> astMembers = astTypeDecl.getMembers();
                for( int j = 0; j < astMembers.size(); j++ )
                {
                    if( astMembers.get( j ) instanceof TypeDeclaration )
                    {
                        TypeDeclaration astNestedTypeDecl = 
                            (TypeDeclaration)astMembers.get( j );
                        if( astNestedTypeDecl.getName().equals( typeName ) ) 
                        {
                            //System.out.println("FOUND NODE"+ ntd );
                            return (TypeDeclaration)astNestedTypeDecl;
                        }
                        //recurse to find 
                        astNestedTypeDecl = recurseTypeChildren( 
                            astNestedTypeDecl, typeName );
                        if( astNestedTypeDecl != null )
                        {
                            return astNestedTypeDecl;
                        }
                    }
                }
            }
        }        
        //List<Node> nodes = cu.getChildrenNodes();
        throw new LoadException( 
            "Could not find type declaration for \"" + typeName + "\"" );
    }
    
    private static TypeDeclaration recurseTypeChildren( 
        TypeDeclaration astTypeDecl, String typeName )
    {
        List<BodyDeclaration> astMembers = astTypeDecl.getMembers();
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration bd = astMembers.get( i );
            if( bd instanceof TypeDeclaration )
            {
                TypeDeclaration astNestedTypeDecl = (TypeDeclaration)bd;
                if( astNestedTypeDecl.getName().equals( typeName ) )
                {
                    return astNestedTypeDecl;
                }
                return recurseTypeChildren( astNestedTypeDecl, typeName );
            }
        }
        return null;
    }
    
    public static TypeDeclaration findTypeDeclaration( 
        CompilationUnit astRoot, Class clazz )
    {           
        return findTypeDeclaration( astRoot, clazz.getSimpleName() );
    }
}
