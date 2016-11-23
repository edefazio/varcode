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
        //System.out.println( "LOOKING FOR "+ clazz.getSimpleName() );
        for( int i = 0; i < types.size(); i++ )
        {
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

    /*
    public static ClassOrInterfaceDeclaration getInterfaceNode( 
        CompilationUnit cu )
    {
        ClassOrInterfaceDeclaration cd = getClassNode( cu ); 
        if( !cd.isInterface() )
        {
            throw new ModelLoadException( "Not an interface" );
        }        
        return cd;
    }
    */
    /*
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
    */
    
    /*
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
    */
    
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
    /*
    public enum _Interface
    {
        ;
            
        public static _interface from( 
            CompilationUnit cu, ClassOrInterfaceDeclaration interfaceDecl )
        {   
            if( !interfaceDecl.isInterface() )
            {
                throw new ModelLoadException( 
                    interfaceDecl.getName() + " NOT an interface" );
            }
            _interface _int = null;
            if( cu.getPackage() != null )
            {
                _int = _interface.of(cu.getPackage().getPackageName(), 
                    "interface " +interfaceDecl.getName() );
            }
            else
            {
                _int = _interface.of("interface "+ interfaceDecl.getName() );
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
            return fromInterfaceNode(_int, interfaceDecl );
        }
        
        public static _interface fromInterfaceNode( 
            _interface _int, ClassOrInterfaceDeclaration interfaceDecl )   
        {
            _int.getSignature().setModifiers( _modifiers.of( interfaceDecl.getModifiers() ) );
            
            if( interfaceDecl.getJavaDoc() != null )
            {
                _int.javadoc(interfaceDecl.getJavaDoc().getContent() );
            }
            List<AnnotationExpr>annots = interfaceDecl.getAnnotations();
            for( int i = 0; i < annots.size(); i++ )
            {
                _int.annotate( annots.get( i ).toString() );
            }
            List<ClassOrInterfaceType>ext = interfaceDecl.getExtends();
            if( ext != null && ext.size() == 1 )
            {
                _int.getSignature().getExtends().addExtends( ext.get( 0 ).getName() );
            }           
            List<BodyDeclaration> members = interfaceDecl.getMembers();
        
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
                else if( member instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration cidef = 
                        (ClassOrInterfaceDeclaration)member;
                    
                    if( cidef.isInterface() )
                    {
                        _interface _i = _interface.of( "interface " + cidef.getName() );
                        _i = _Interface.fromInterfaceNode( _i, cidef );
                        _int.nest( _i );
                    }
                    else
                    {
                        _class _c = _class.of( cidef.getName() );
                        _c = _Class.fromClassNode( _c, cidef );
                        _int.nest( _c );
                    }
                }
                else if( member instanceof EnumDeclaration )
                {
                    EnumDeclaration nestedEnum = (EnumDeclaration)member;
                    _enum _en = _enum.of( "enum "+ nestedEnum.getName() );
                    _en = _Enum.fromEnumNode( _en, nestedEnum );
                    _int.nest( _en );
                }
                else
                {
                    System.out.println( "NOT HANDLED "+ member );
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
            _class _c = null;
            if( cu == null )
            {
                _c = _class.of( classDecl.getName() );
                return fromClassNode(_c, classDecl );
            }
            if( cu.getPackage() != null )
            {
                _c = _class.of( cu.getPackage().getPackageName(), classDecl.getName() );
            }
            else
            {
                _c = _class.of( classDecl.getName() );
            }
            
            List<ImportDeclaration>imports = cu.getImports();
        
            for( int i = 0; i < imports.size(); i++ )
            {
                if( imports.get(i).isStatic() )
                {
                    _c.importsStatic( 
                        imports.get( i ).getName().toStringWithoutComments()+ ".*" );
                }
                else
                {
                    _c.imports(  imports.get( i ).getName().toStringWithoutComments() );
                }
            }
            return fromClassNode(_c, classDecl );
        }
        
        public static _class fromClassNode( _class c, ClassOrInterfaceDeclaration classDecl )
        {
             List<AnnotationExpr>annots =  classDecl.getAnnotations();
             
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
                    FieldDeclaration fd = (FieldDeclaration)member;
                    
                    
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
                else if( member instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration cidef = 
                        (ClassOrInterfaceDeclaration)member;
                    
                    if( cidef.isInterface() )
                    {   //nested interface
                        _interface _i = _interface.of( "interface " + cidef.getName() );
                        _i = _Interface.fromInterfaceNode( _i, cidef );
                        c.nest( _i );
                    }
                    else
                    {   //nested class
                        _class _c = _class.of( cidef.getName() );
                        _c = _Class.fromClassNode( _c, cidef );
                        c.nest( _c );
                    }
                }
                else if( member instanceof EnumDeclaration )
                {   //nested enum
                    EnumDeclaration nestedEnum = (EnumDeclaration)member;
                    _enum _en = _enum.of( "enum " + nestedEnum.getName() );
                    _en = _Enum.fromEnumNode( _en, nestedEnum );
                    c.nest( _en );
                }
                else
                {
                    System.out.println( "NOT HANDLED "+ member );
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
            
            _enum _e = null;
            if( cu.getPackage() != null )
            {
                _e = _enum.of( cu.getPackage().getPackageName(), enumDecl.getName() );
            }
            else
            {
                _e = _enum.of( enumDecl.getName() );
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
            return fromEnumNode( _e, enumDecl );
        }
        
        public static _enum fromEnumNode( _enum _e, EnumDeclaration enumDecl )
        {
            List<AnnotationExpr>annots =  enumDecl.getAnnotations();        
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
                else if( member instanceof ClassOrInterfaceDeclaration )
                {
                    ClassOrInterfaceDeclaration cidef = 
                        (ClassOrInterfaceDeclaration)member;
                    
                    if( cidef.isInterface() )
                    {
                        _interface _i = _interface.of( "interface " + cidef.getName() );
                        _i = _Interface.fromInterfaceNode( _i, cidef );
                        _e.nest( _i );
                    }
                    else
                    {
                        _class _c = _class.of( cidef.getName() );
                        _c = _Class.fromClassNode( _c, cidef );
                        _e.nest( _c );
                    }
                }
                else if( member instanceof EnumDeclaration )
                {
                    EnumDeclaration nestedEnum = (EnumDeclaration)member;
                    _enum _en = _enum.of( "enum "+ nestedEnum.getName() );
                    _en = _Enum.fromEnumNode( _en, nestedEnum );
                    _e.nest( _en );
                }
                else
                {
                    System.out.println( "NOT HANDLED "+ member );
                }
            }
            return _e;
        }
    }
*/
}
