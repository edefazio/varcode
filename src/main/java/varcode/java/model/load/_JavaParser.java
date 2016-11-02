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
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
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
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._fields._field;
import varcode.java.model._fields._init;
import varcode.java.model._interface;
import varcode.java.model._javadoc;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._modifiers;
import varcode.java.model._parameters;
import varcode.java.model._throws;
import varcode.java.model.load._JavaLoader.ModelLoadException;

/**
 * Uses the JavaParser to Parse the input text file (.java file) into an AST
 * {@code CompilationUnit}, then converts the AST into a JavaModel 
 * (_class, _enum, _interface)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum _JavaParser
{
    ;
            
    public static CompilationUnit from( InputStream is )            
        throws ParseException
    {
        return JavaParser.parse( is );           
    }
        
    public static CompilationUnit from( String string )
        throws ParseException
    {
        ByteArrayInputStream bais = 
            new ByteArrayInputStream( string.getBytes() );
        return _JavaParser.from( bais );
    }
    
    public static EnumDeclaration findEnumNode( 
        CompilationUnit cu, Class clazz )
    {
        List<TypeDeclaration> types =  cu.getTypes();
         for( int i = 0; i < types.size(); i++ )
        {
            TypeDeclaration td = types.get( i );
            //System.out.println( "FOUND " + td.getName() );
            
            if( td.getName().equals( clazz.getSimpleName() ) )
            {
                return (EnumDeclaration)td;
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
                            return (EnumDeclaration)ntd;
                        }
                    }
                }
            }
        }        
        //List<Node> nodes = cu.getChildrenNodes();
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
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
        //List<Node> nodes = cu.getChildrenNodes();
        throw new ModelLoadException( 
            "Could not find class declaration for \""
            + clazz.getCanonicalName() + "\"" );
    }

    public static ClassOrInterfaceDeclaration getInterfaceNode( CompilationUnit cu )
    {
        ClassOrInterfaceDeclaration cd = getClassNode( cu ); 
        if( !cd.isInterface() )
        {
            throw new ModelLoadException( "Not an interface" );
        }        
        return cd;
    }
    
    public static EnumDeclaration getEnumNode( CompilationUnit cu )
    {
        List<Node> nodes = cu.getChildrenNodes();
        for( int i = 0; i < nodes.size(); i++ )
        {
            if( nodes.get( i ) instanceof EnumDeclaration )
            {
                EnumDeclaration ci = 
                    (EnumDeclaration)nodes.get( i );
                return ci;                
            }
        }
        throw new ModelLoadException( "Could not find enum declaration" );
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
                return ci;                
            }
        }
        throw new ModelLoadException( "Could not find class declaration" );
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
    
    public enum _Interface
    {
        ;
            
        public static _interface from( 
            CompilationUnit cu, ClassOrInterfaceDeclaration _interfaceDecl )
        {   
            if( !_interfaceDecl.isInterface() )
            {
                throw new ModelLoadException( 
                    _interfaceDecl.getName() + " NOT an interface" );
            }
            _interface _int = null;
            if( cu.getPackage() != null )
            {
                _int = _interface.of( cu.getPackage().getPackageName(), "interface " +_interfaceDecl.getName() );
            }
            else
            {
                _int = _interface.of( "interface "+ _interfaceDecl.getName() );
            }
            _int.getSignature().setModifiers( _modifiers.of( _interfaceDecl.getModifiers() ) );
            
            if( _interfaceDecl.getJavaDoc() != null )
            {
                _int.javadoc( _interfaceDecl.getJavaDoc().getContent() );
            }
            List<AnnotationExpr>annots = _interfaceDecl.getAnnotations();
            for( int i = 0; i < annots.size(); i++ )
            {
                _int.annotate( annots.get( i ).toString() );
            }
            List<ClassOrInterfaceType>ext = _interfaceDecl.getExtends();
            if( ext != null && ext.size() == 1 )
            {
                _int.getSignature().getExtends().addExtends( ext.get( 0 ).getName() );
            }
        
            List<ImportDeclaration>imports = cu.getImports();
        
            for( int i = 0; i < imports.size(); i++ )
            {
                if( imports.get(i).isStatic() )
                {
                    _int.importsStatic( 
                        imports.get( i ).getName().toStringWithoutComments()+ ".*" );
                }
                else
                {
                    _int.imports(  imports.get( i ).getName().toStringWithoutComments() );
                }
            }
            List<BodyDeclaration> members = _interfaceDecl.getMembers();
        
            for( int i = 0; i < members.size(); i++ )
            {
                BodyDeclaration member = members.get( i );
                if( member instanceof MethodDeclaration )
                {
                    MethodDeclaration md = (MethodDeclaration) member;
                    
                    String methd  = md.getDeclarationAsString( true, true, true );
                    //System.out.println( "MethodDeclaration" + methd);
                    
                    List<AnnotationExpr> ann = md.getAnnotations();
                    _method meth = null;
                    if( md.getBody() != null )
                    {
                        String body = md.getBody().toString();
                        body = body.substring( body.indexOf('{')+1, body.lastIndexOf( "}") ).trim();
                        meth = _method.of( methd, _code.of( body ) );  
                    }
                    else
                    {
                        meth = _method.of( methd );  
                    }
                    if( md.getJavaDoc() != null )
                    {                       
                        meth.javadoc( md.getJavaDoc().getContent() );
                    }
                    
                    for( int k = 0; k < ann.size(); k++ )
                    {
                        meth.annotate( ann.get( k ).toString() );
                    }
                    _int.method( meth );
                }
                else if( member instanceof FieldDeclaration ) 
                {
                    FieldDeclaration fd = (FieldDeclaration) member;

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
                        if( jd != null)
                        {
                            f.javadoc( jd.getContent() );
                        }
                        _int.field( f );
                    }                                   
                }
            }            
            return _int;
        }    
    }
    
    public enum _Class
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
            JavadocComment jd = classDecl.getJavaDoc();
            if( jd != null )
            {
                c.javadoc( jd.getContent() );
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
                            md.getJavaDoc().getContent(), methd, body );      
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
                    JavadocComment fjd = fd.getJavaDoc();
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
                        if( fjd != null)
                        {
                            f.javadoc( fjd.getContent() );
                        }
                        c.field( f );
                    }                                 
                }
            }
            return c;
        }
    }
    
    public enum _Enum
    {
        ;
            
        public static _enum fromCompilationUnit( 
            CompilationUnit cu, EnumDeclaration enumDecl )
        {            
            List<AnnotationExpr>annots =  enumDecl.getAnnotations();
        
            _enum _e = null;
            if( cu.getPackage() != null )
            {
                _e = _enum.of( cu.getPackage().getPackageName(), enumDecl.getName() );
            }
            else
            {
                _e = _enum.of( enumDecl.getName() );
            }
            JavadocComment jd = enumDecl.getJavaDoc();
            if( jd != null )
            {
                _e.javadoc( jd.getContent() );
            }
            
            _e.setModifiers( _modifiers.of( enumDecl.getModifiers() ) );
            for( int i = 0; i < annots.size(); i++ )
            {
                _e.annotate( annots.get( i ).toString() );
            }
        
            List<ClassOrInterfaceType>impls = enumDecl.getImplements();
            if( impls != null && impls.size() > 0 )
            {
                _e.implement( impls.get( 0 ).getName() );
            }
        
            List<ImportDeclaration>imports = cu.getImports();
        
            for( int i = 0; i < imports.size(); i++ )
            {
                if( imports.get(i).isStatic() )
                {
                    _e.importsStatic( 
                        imports.get( i ).getName().toStringWithoutComments()+ ".*" );
                }
                else
                {
                    _e.imports(  imports.get( i ).getName().toStringWithoutComments() );
                }
            }
            
            List<EnumConstantDeclaration>constDecls = enumDecl.getEntries();
            for( int i = 0; i < constDecls.size(); i++ )
            {
                EnumConstantDeclaration cdecl = constDecls.get( i );
                _e.value( cdecl.getName(), cdecl.getArgs().toArray() );
            }
            
            
            List<BodyDeclaration> members = enumDecl.getMembers();
        
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
                    _e.constructor( ctor );
                    
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
                        _e.method( meth );
                    }
                    else
                    {
                        _methods._method meth = _methods._method.of( 
                            md.getJavaDoc().getContent(), methd, body );      
                        for( int k = 0; k < ann.size(); k++ )
                        {
                            meth.annotate( ann.get( k ).toString() );
                        }
                        _e.method( meth );
                    }
                    
                }
                else if( member instanceof FieldDeclaration ) 
                {
                    FieldDeclaration fd = (FieldDeclaration) member;
                    JavadocComment fjd = fd.getJavaDoc();
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
                        if( fjd != null)
                        {
                            f.javadoc( fjd.getContent() );
                        }
                        _e.field( f );
                    }                                
                }
                else
                {
                    System.out.println( "NOT HANDLED "+ member );
                }
            }
            return _e;
        }
    }
}
