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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeWithModifiers;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import varcode.VarException;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._methods;
import varcode.java.model._modifiers;

/**
 * Uses the JavaParser to Parse the input (.java file) into a 
 * {@code CompilationUnit}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum ModelParser
{
    ;
            
    public static CompilationUnit fromInputStream( InputStream is )            
        throws ParseException
    {
        return JavaParser.parse( is );           
    }
        
    public static CompilationUnit fromString( String string )
        throws ParseException
    {
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( string.getBytes() );
        return fromInputStream( bais );
    }
    
    public static ClassOrInterfaceDeclaration findMemberNode( 
        CompilationUnit cu, Class clazz )
    {        
        List<Node> nodes = cu.getChildrenNodes();
        //List<Node>nextLevel = new ArrayList<Node>();
        for( int i = 0; i < nodes.size(); i++ )
        {
            if( nodes.get( i ) instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration ci = 
                    (ClassOrInterfaceDeclaration)nodes.get( i );
                if( clazz.getName().endsWith( ci.getName() ) )
                {
                    return ci;
                }
            }
        }
        throw new VarException( "Could not find class declaration" );
    }
    
    public static ClassOrInterfaceDeclaration getClassNode( CompilationUnit cu )
    {
        List<Node> nodes = cu.getChildrenNodes();
        for( int i = 0; i < nodes.size(); i++ )
        {
            if( nodes.get( i ) instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration ci = 
                    (ClassOrInterfaceDeclaration)nodes.get( i );
                if( !ci.isInterface() )
                {
                    return ci;
                }
            }
        }
        throw new VarException( "Could not find class declaration" );
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
    
    public enum ClassModel
    {
        ;
            
        public static _class fromCompilationUnit( 
            CompilationUnit cu, ClassOrInterfaceDeclaration classDecl )
        {            
            List<AnnotationExpr>annots =  classDecl.getAnnotations();
        
            _class c = null;
            if( cu.getPackage() != null )
            {
                c = _class.of( cu.getPackage().getPackageName(), classDecl.getName() );
            }
            else
            {
                c = _class.of( classDecl.getName() );
            }
            c.getSignature().setModifiers( _modifiers.of( classDecl.getModifiers()) );
            for( int i = 0; i < annots.size(); i++ )
            {
                c.annotate( annots.get( i ).toString() );
            }
            List<ClassOrInterfaceType>ext = classDecl.getExtends();
            if( ext != null && ext.size() == 1 )
            {
                c.getSignature().getExtends().addExtends( ext.get( 0 ).getName() );
            }
        
        
            List<ClassOrInterfaceType>impls = classDecl.getImplements();
            if( impls != null && impls.size() > 0 )
            {
                c.getSignature().implement( impls.get( 0 ).getName() );
            }
        
            List<ImportDeclaration>imports = cu.getImports();
        
            for( int i = 0; i < imports.size(); i++ )
            {
                if( imports.get(i).isStatic() )
                {
                    c.importsStatic( 
                        imports.get( i ).getName().toStringWithoutComments()+ ".*" );
                }
                else
                {
                    c.imports(  imports.get( i ).getName().toStringWithoutComments() );
                }
            }
            List<BodyDeclaration> members = classDecl.getMembers();
        
            for( int i = 0; i < members.size(); i++ )
            {
                System.out.println( "MEMBER " + members.get( i ) );
                if( members.get( i ) instanceof MethodDeclaration )
                {
                    MethodDeclaration md = (MethodDeclaration) members.get( i );
                    String methd  = md.getDeclarationAsString( true, true, true );
                    System.out.println( "MethodDeclaration" + methd);
                    
                
                    String body = md.getBody().toString();
                    body = body.substring( body.indexOf('{')+1, body.lastIndexOf( "}") ).trim();
                    if( md.getJavaDoc() == null )
                    {
                        _methods._method meth = _methods._method.of( methd, _code.of( body ) );      
                        c.method( meth );
                    }
                    else
                    {
                        _methods._method meth = _methods._method.of( 
                            md.getJavaDoc().getContent().toString(), methd, body );      
                        c.method( meth );
                    }
                }
                else if ( members.get( i ) instanceof FieldDeclaration ) 
                {
                    FieldDeclaration fd = (FieldDeclaration) members.get( i );
                    c.field(  fd.toStringWithoutComments() );
                    //System.out.println( members.get( i ).getClass() );                
                }
            }
            //List<TypeDeclaration> typeDecl =  cu.getTypes();        
            JavadocComment jd = classDecl.getJavaDoc();
        
            return c;
        }
    }
}
