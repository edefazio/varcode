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
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import varcode.VarException;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields._field;
import varcode.java.model._fields._init;
import varcode.java.model._javadoc;
import varcode.java.model._methods;
import varcode.java.model._modifiers;
import varcode.java.model._parameters;
import varcode.java.model._throws;
import varcode.java.model.load.JavaModelLoader.ModelLoadException;

/**
 * Uses the JavaParser to Parse the input text file (.java file) into an AST
 * {@code CompilationUnit}, then converts the AST into a JavaModel 
 * (_class, _enum, _interface)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum JavaModelParser
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
        
        List<TypeDeclaration> types =  cu.getTypes();
        //System.out.println( "LOOKING FOR "+ clazz.getSimpleName() );
        for( int i = 0; i < types.size(); i++ )
        {
            TypeDeclaration td = types.get( i );
            //System.out.println( "FOUND " + td.getName() );
            
            if( td.getName().equals( clazz.getSimpleName() ) )
            {
                return (ClassOrInterfaceDeclaration)td;
            }
            else
            {
                List<BodyDeclaration> bds = td.getMembers();
                for( int j = 0; j < bds.size(); j++ )
                {
                    if( bds.get( j ) instanceof TypeDeclaration )
                    {
                        TypeDeclaration ntd = (TypeDeclaration)bds.get( j );
                        if( ntd.getName().equals( clazz.getSimpleName() ) ) 
                        {
                            //System.out.println("FOUND NODE"+ ntd );
                            return (ClassOrInterfaceDeclaration)ntd;
                        }
                    }
                }
            }
        }
        
        List<Node> nodes = cu.getChildrenNodes();
        //List<Node>nextLevel = new ArrayList<Node>();
        /*
        System.out.println( "LOOKING FOR "+ clazz.getSimpleName() +" IN CHILD NODES");
        for( int i = 0; i < nodes.size(); i++ )
        {
            if( nodes.get( i ) instanceof ClassOrInterfaceDeclaration )
            {
                //System.out.println( "FOUND " + td.getName() );
                
                ClassOrInterfaceDeclaration ci = 
                    (ClassOrInterfaceDeclaration)nodes.get( i );
                if( clazz.getName().endsWith( ci.getName() ) )
                {
                    return ci;
                }
            }
        }
        */
        throw new ModelLoadException( 
            "Could not find class declaration for \""+clazz.getCanonicalName()+"\"" );
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
    
    /**
     * 
     */
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
                BodyDeclaration member = members.get( i );
                //System.out.println( "MEMBER " +  member);
                if( member instanceof ConstructorDeclaration )
                {
                    ConstructorDeclaration cd = (ConstructorDeclaration)member;
                    
                    String name = cd.getName();
                    _modifiers mods = _modifiers.of( cd.getModifiers() );
                    
                    _throws throwsEx = new _throws();
                    for( int j = 0; j < cd.getThrows().size(); j++ )
                    {
                        throwsEx.addThrows( cd.getThrows().get( j ).toString() );
                    }
                    List<Parameter>parameters =  cd.getParameters();
                    _parameters params = new _parameters();
                    for( int j = 0; j < parameters.size(); j++ )
                    {
                        params.add( _parameters.of( parameters.get( j ).toString() ) );                        
                    }
                    _constructor ctor = new _constructor( mods, name, params, throwsEx );
                    
                    //set the body
                    List<Statement>statements = cd.getBlock().getStmts();
                    
                    for( int j = 0; j < statements.size(); j++ )
                    {
                        ctor.body( statements.get( j ).toString() );
                    }
                    //ctor.body( cd.getBlock().getStmts() );
                    
                    //set annotations on the constructor 
                    List<AnnotationExpr> annotates = cd.getAnnotations();
                    for( int j = 0; j < annotates.size(); j++ )
                    {
                        ctor.annotate( annotates.get( j ).toString() );
                    }
                    
                    _javadoc doc = new _javadoc();
                    ctor.javadoc( doc.getComment() );
                    
                    //cd.getJavaDoc();
                    //cd.getBlock();
                    //cd.getParameters();
                    c.constructor( ctor );
                    
                }
                else if( member instanceof MethodDeclaration )
                {
                    MethodDeclaration md = (MethodDeclaration) member;
                    
                    String methd  = md.getDeclarationAsString( true, true, true );
                    //System.out.println( "MethodDeclaration" + methd);
                    
                    List<AnnotationExpr> ann = md.getAnnotations();
                    String body = md.getBody().toString();
                    body = body.substring( body.indexOf('{')+1, body.lastIndexOf( "}") ).trim();
                    if( md.getJavaDoc() == null )
                    {
                        _methods._method meth = _methods._method.of( methd, _code.of( body ) );      
                        for( int k = 0; k < ann.size(); k++ )
                        {
                            meth.annotate( ann.get( k ).toString() );
                        }
                        c.method( meth );
                    }
                    else
                    {
                        _methods._method meth = _methods._method.of( 
                            md.getJavaDoc().getContent().toString(), methd, body );      
                        for( int k = 0; k < ann.size(); k++ )
                        {
                            meth.annotate( ann.get( k ).toString() );
                        }
                        c.method( meth );
                    }
                    
                }
                else if( member instanceof FieldDeclaration ) 
                {
                    FieldDeclaration fd = (FieldDeclaration) member;
                    //c.field( fd.toStringWithoutComments() );
                    //fd.getComment();
                    //fd.getType();
                    //fd.getModifiers();
                    //fd.getJavaDoc();
                    //fd.getAnnotations();
                    
                    
                    //they could be doing this:
                    //int a,b,c;
                    JavadocComment jd = fd.getJavaDoc();
                    List<VariableDeclarator>vars = fd.getVariables();
                    List<AnnotationExpr>ann = fd.getAnnotations();
                    for( int j = 0; j < vars.size(); j++ )
                    {
                        String name = vars.get( j ).getId().getName();
                        String init = null;
                        if( vars.get( j ).getInit() != null )
                        {
                            init = vars.get( j ).getInit().toString();
                        }
                        String type = fd.getType().toString();
                        _modifiers mods = _modifiers.of( fd.getModifiers() );
                        
                        _field f = null;
                        if( init == null || init.trim().length() == 0 )
                        {
                            f = new _field( mods, type, name );
                        }
                        else
                        {
                            f = new _field( mods, type, name, _init.of( init ) ); 
                        }
                        for( int k = 0; k < ann.size(); k++ )
                        {
                            f.annotate( ann.get( k ).toString() );
                        }
                        //todo I need to clean this up
                        f.javadoc( jd.getContent() );
                        c.field( f );
                    }
                    
                    //System.out.println( members.get( i ).getClass() );                
                }
            }
            //List<TypeDeclaration> typeDecl =  cu.getTypes();        
            
            
            return c;
        }
    }
}
