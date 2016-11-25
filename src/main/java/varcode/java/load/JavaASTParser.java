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
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeWithModifiers;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import varcode.Model.ModelLoadException;

/**
 * Reads in Java Source as text and converts the text into an AST 
 * (Abstract Syntax Tree) {@code CompilationUnit}... And converts the AST
 * {@code CompilationUnit} into a java "Lang Model" (_class, _interface, _enum)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaASTParser
{
    ;
    
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
    
        
    public static CompilationUnit astFrom( String string )
        throws ParseException
    {
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( string.getBytes() );
        return JavaASTParser.astFrom( bais );
    }
    
    
    public static EnumDeclaration findEnumDeclaration(
        CompilationUnit astRoot, String enumName )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, enumName );
        if( td instanceof EnumDeclaration )
        {
            return (EnumDeclaration)td;            
        }
        throw new ModelLoadException( 
            "Could not find interface declaration for \""
                + enumName + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the EnumTypeDeclaration AST node
     */
    public static EnumDeclaration findEnumDeclaration( 
        CompilationUnit astRoot, Class clazz )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, clazz );
        if( td instanceof EnumDeclaration )
        {
            return (EnumDeclaration) td;
        }
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param interfaceName the name of the interface
     * @return the ClassOrInterfaceDeclaration AST node
     */
    public static ClassOrInterfaceDeclaration findInterfaceDeclaration( 
        CompilationUnit astRoot, String interfaceName )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, interfaceName );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)td;
            if( cd.isInterface() )
            {
                return cd;
            }
        }
        throw new ModelLoadException( 
            "Could not find interface declaration for \""
            + interfaceName + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the EnumTypeDeclaration AST node
     */
    public static ClassOrInterfaceDeclaration findInterfaceDeclaration( 
        CompilationUnit astRoot, Class clazz )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, clazz );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)td;
            if( cd.isInterface() )
            {
                return cd;
            }
        }
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param className the (simple) class Name ClassOrInterfaceDeclaration 
     * AST Node from
     * @return the EnumTypeDeclaration AST node
     */
    public static ClassOrInterfaceDeclaration findClassDeclaration( 
        CompilationUnit astRoot, String className )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, className );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)td;
            if( ! cd.isInterface() )
            {
                return cd;
            }
        }
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + className + "\"" );
    }
    
    /**
     * finds and returns the EnumTypeDeclaration (AST node) for the clazz 
     * @param astRoot the CompilationRoot AST Node
     * @param clazz the clazz to resolve the EnumDeclaration from
     * @return the EnumTypeDeclaration AST node
     */
    public static ClassOrInterfaceDeclaration findClassDeclaration( 
        CompilationUnit astRoot, Class clazz )
    {
        TypeDeclaration td = findTypeDeclaration( astRoot, clazz );
        if( td instanceof ClassOrInterfaceDeclaration )
        {
            ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration)td;
            if( ! cd.isInterface() )
            {
                return cd;
            }
        }
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }
    
    /**
     * Recursively search through the nodes of the AST
     * to find a {@code TypeDeclaration} node named <CODE>name</CODE>
     * and return that node.
     * @param cu the CompilationUnit (top Level AST node)
     * @param name the name of the TypeDeclaration to find
     * @return the TypeDeclaration node (and its children)
     * @throws ModelLoadException if unable to find a TypeDeclaration with that name
     */
    public static TypeDeclaration findTypeDeclaration( 
        CompilationUnit cu, String name )
    {
        List<TypeDeclaration> types =  cu.getTypes();
        //System.out.println( "LOOKING FOR \"" + name + "\"" );
        for( int i = 0; i < types.size(); i++ )
        {
            //System.out.println( "FOund "+ types.get( i ).getName() );
            TypeDeclaration td = types.get( i );
            
            if( td.getName().equals( name ) )
            {
                return (TypeDeclaration)td;
            }
            else
            {
                List<BodyDeclaration> bds = td.getMembers();
                for( int j = 0; j < bds.size(); j++ )
                {
                    if( bds.get( j ) instanceof TypeDeclaration )
                    {
                        TypeDeclaration ntd = (TypeDeclaration)bds.get( j );
                        if( ntd.getName().equals( name ) ) 
                        {
                            //System.out.println("FOUND NODE"+ ntd );
                            return (TypeDeclaration)ntd;
                        }
                        //recurse to find 
                        ntd = recurseTypeChildren( ntd, name );
                        if( ntd != null )
                        {
                            return ntd;
                        }
                    }
                }
            }
        }        
        //List<Node> nodes = cu.getChildrenNodes();
        throw new ModelLoadException( 
            "Could not find type declaration for \""+ name + "\"" );
    }
    
    private static TypeDeclaration recurseTypeChildren( 
        TypeDeclaration td, String name )
    {
        List<BodyDeclaration> bds = td.getMembers();
        for( int i = 0; i < bds.size(); i++ )
        {
            BodyDeclaration bd = bds.get( i );
            if( bd instanceof TypeDeclaration )
            {
                TypeDeclaration t = (TypeDeclaration)bd;
                if( t.getName().equals( name ) )
                {
                    return t;
                }
                return recurseTypeChildren( t, name );
            }
        }
        return null;
    }
    
    public static TypeDeclaration findTypeDeclaration( 
        CompilationUnit cu, Class clazz )
    {           
        return findTypeDeclaration( cu, clazz.getSimpleName() );
    }

    
    public static int getModifiers( Node node )
    {
        if( node instanceof NodeWithModifiers )
        {
            return ((NodeWithModifiers)node).getModifiers();
        }
        else
        {
            return 0; 
        }
    }
}
