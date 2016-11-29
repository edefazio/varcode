/*
 * Copyright 2016 Eric DeFazio.
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
package varcode.java.metalang;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.Model.ModelLoadException;
import varcode.java.metalang._constructors._constructor;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;

/**
 * "Compiles" the AST (Node-based) representation of Java Code into  
 * MetaLang {@code _class, _interface, _enum } models.
 *
 * @author Eric DeFazio eric@varcode.io
 */
public class JavaMetaLangCompiler
{
    public static final Logger LOG = 
        LoggerFactory.getLogger( JavaMetaLangCompiler.class );
    
    /**
     * Creates and returns an _interface MetaLang model from the AST Root node 
     * containing a astInterfaceDecl
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astInterfaceDecl the interface Declaration AST Node 
     * (is a CHILD of the rootASTNode)
     * @return the _interface MetaLang model representing the Java code.
     */
    public static _interface _interfaceFrom( 
        CompilationUnit astRoot, 
        ClassOrInterfaceDeclaration astInterfaceDecl )
    {   
        if( !astInterfaceDecl.isInterface() )
        {
            throw new ModelLoadException( 
                astInterfaceDecl.getName() + " NOT an interface" );
        }
        _interface _int = null;
        if( astRoot.getPackage() != null )
        {
            _int = _interface.of( astRoot.getPackage().getPackageName(), 
                "interface " + astInterfaceDecl.getName() );
        }
        else
        {
            _int = _interface.of( "interface " 
                + astInterfaceDecl.getName() );
        }
        List<ImportDeclaration>astImports = astRoot.getImports();
        
        for( int i = 0; i < astImports.size(); i++ )
        {
            if( astImports.get( i ).isStatic() )
            {
                _int.importsStatic(
                    astImports.get( i ).getName().toStringWithoutComments()+ ".*" );
            }
            else
            {
                _int.imports( 
                    astImports.get( i ).getName().toStringWithoutComments() );
            }
        }
        return _interfaceFromAST( _int, astInterfaceDecl );
    }
        
    /**
     * build/update an _interface MetaLang model based on the contents within a 
     * interface AST node  
     * @param _int the _interface model to update
     * @param astInterfaceDecl the interface declaration AST Node
     * @return the modified _interface MetaLang model 
     */
    public static _interface _interfaceFromAST( 
        _interface _int, ClassOrInterfaceDeclaration astInterfaceDecl )   
    {
        _int.getSignature().setModifiers( _modifiers.of(
            astInterfaceDecl.getModifiers() ) );
            
        if( astInterfaceDecl.getJavaDoc() != null )
        {
            _int.javadoc( astInterfaceDecl.getJavaDoc().getContent() );
        }
        List<AnnotationExpr>astAnnotations = 
            astInterfaceDecl.getAnnotations();
        for( int i = 0; i < astAnnotations.size(); i++ )
        {
            _int.annotate( astAnnotations.get( i ).toString() );
        }
        List<ClassOrInterfaceType>astExtends = 
            astInterfaceDecl.getExtends();
        if( astExtends != null && astExtends.size() == 1 )
        {
            for( int i = 0; i < astExtends.size(); i++ )
            {
                _int.extend( astExtends.get( i ).getName() );
            }
        }           
        List<BodyDeclaration> astMembers = astInterfaceDecl.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            if( astMember instanceof MethodDeclaration )
            {
                MethodDeclaration astMethodDeclaration = 
                    (MethodDeclaration)astMember;
                    
                String methodDecl = 
                    astMethodDeclaration.getDeclarationAsString( 
                        true, true, true );
                    
                List<AnnotationExpr> astMethodAnn = 
                    astMethodDeclaration.getAnnotations();
                    
                _methods._method _meth = null;
                if( astMethodDeclaration.getBody() != null )
                {
                    String body = astMethodDeclaration.getBody().toString();
                    
                    /*TODO I NEED TO NORMALIZE THE CONTENT FIRST */
                    body = body.substring( 
                        body.indexOf( "{" ) + 1, 
                        body.lastIndexOf( "}" ) )
                        .trim();
                        
                    _meth = _methods._method.of( 
                        methodDecl, _code.of( body ) );  
                }
                else
                {
                    _meth = _methods._method.of( methodDecl );  
                }
                if( astMethodDeclaration.getJavaDoc() != null )
                {                       
                    _meth.javadoc( 
                        astMethodDeclaration.getJavaDoc().getContent() );
                }
                    
                for( int k = 0; k < astMethodAnn.size(); k++ )
                {
                    _meth.annotate( astMethodAnn.get( k ).toString() );
                }
                _int.method(_meth );
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                FieldDeclaration astFieldDecl = 
                    (FieldDeclaration) astMember;

                JavadocComment astFieldJavaDoc = astFieldDecl.getJavaDoc();
                    
                //they could be doing this:
                //int a,b,c;
                List<VariableDeclarator>astFieldVars = 
                    astFieldDecl.getVariables();
                    
                List<AnnotationExpr>astFieldAnn = 
                    astFieldDecl.getAnnotations();
                    
                //define each var separately, so:
                // int i, j, k;
                // becomes:
                // int i;
                // int j;
                // int k;
                for( int j = 0; j < astFieldVars.size(); j++ )
                {
                    String name = astFieldVars.get( j ).getId().getName();
                    String init = null;
                    if( astFieldVars.get( j ).getInit() != null )
                    {
                        init = astFieldVars.get( j ).getInit().toString();
                    }
                    String type = astFieldDecl.getType().toString();
                    _modifiers _mods = _modifiers.of( 
                        astFieldDecl.getModifiers() );
                        
                    _field _f = null;
                    if( init == null || init.trim().length() == 0 )
                    {
                        _f = new _field( _mods, type, name );
                    }
                    else
                    {
                        _f = new _field( 
                            _mods, type, name, _fields._init.of( init ) ); 
                    }
                    for( int k = 0; k < astFieldAnn.size(); k++ )
                    {
                        _f.annotate( astFieldAnn.get( k ).toString() );
                    }
                    //todo I need to clean this up
                    if( astFieldJavaDoc != null)
                    {
                        _f.javadoc( astFieldJavaDoc.getContent() );
                    }
                    _int.field( _f  );
                }                                   
            }
            else if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedClassInterfaceDecl = 
                    (ClassOrInterfaceDeclaration)astMember;
                
                if( astNestedClassInterfaceDecl.isInterface() )
                {
                    _interface _nestedInterface = _interface.of( 
                        "interface " + astNestedClassInterfaceDecl.getName() );
                    _nestedInterface = _interfaceFromAST(
                        _nestedInterface, astNestedClassInterfaceDecl );
                    _int.nest( _nestedInterface );
                }
                else
                {
                    _class _nestedClass = _class.of( 
                        astNestedClassInterfaceDecl.getName() );
                    _nestedClass = _classFromAST(
                        _nestedClass, astNestedClassInterfaceDecl );
                    _int.nest( _nestedClass );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {
                EnumDeclaration astNestedEnum = (EnumDeclaration)astMember;
                _enum _nestedEnum = _enum.of( 
                    "enum " + astNestedEnum.getName() );
                _nestedEnum = _enumFromAST( _nestedEnum, astNestedEnum );
                _int.nest( _nestedEnum );
            }
            
            else
            {
                LOG.error(" UNKNOWN AST MEMBER " + astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );         
            }
        }            
        return _int;            
    }
    
    /**
     * Creates and returns a _class MetaLang model from the AST Root node 
     * containing a AST Class Declaration.
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astClassDecl the class Declaration AST Node 
     * (is a CHILD of the astRoot)
     * @return the _class MetaLang model representing the Java code.
     */
    public static _class _classFrom( 
        CompilationUnit astRoot, ClassOrInterfaceDeclaration astClassDecl )
    {            
        _class _c = null;
        if( astRoot == null )
        {
            _c = _class.of( astClassDecl.getName() );
            return _classFromAST( _c, astClassDecl );
        }
        if( astRoot.getPackage() != null )
        {
            _c = _class.of(
                astRoot.getPackage().getPackageName(), astClassDecl.getName() );
        }
        else
        {
            _c = _class.of( astClassDecl.getName() );
        }
            
        List<ImportDeclaration>astImports = astRoot.getImports();
        
        for( int i = 0; i < astImports.size(); i++ )
        {
            if( astImports.get( i ).isStatic() )
            {
                _c.importsStatic(
                    astImports.get( i ).getName().toStringWithoutComments()+ ".*" );
            }
            else
            {
                _c.imports(
                    astImports.get( i ).getName().toStringWithoutComments() );
            }
        }
        return _classFromAST( _c, astClassDecl );
    }
        
    /**
     * Build/Update a <CODE>_class</CODE> MetaLang model based on the <CODE>astClassDecl</CODE>
     * @param _c the _class MetaLang Model to be updated / built
     * @param astClassDecl the AST classDeclaration root node
     * @return the updated _class MetaLang model
     */
    public static _class _classFromAST( 
        _class _c, ClassOrInterfaceDeclaration astClassDecl )
    {        
        JavadocComment astClassJavaDoc = astClassDecl.getJavaDoc();
        if( astClassJavaDoc != null )
        {
            _c.javadoc( astClassJavaDoc.getContent() );
        }
        
        _c.setModifiers( astClassDecl.getModifiers() );

        List<AnnotationExpr>astClassAnnots = astClassDecl.getAnnotations();
        for( int i = 0; i < astClassAnnots.size(); i++ )
        {
            _c.annotate( astClassAnnots.get( i ).toString() );
        }
        
        List<ClassOrInterfaceType>astExtends = astClassDecl.getExtends();
        if( astExtends != null && astExtends.size() == 1 )
        {
            _c.extend( astExtends.get( 0 ).getName() );            
        }        
        List<ClassOrInterfaceType>astImplements = astClassDecl.getImplements();
        if( astImplements != null && astImplements.size() > 0 )
        {
            for( int i = 0; i < astImplements.size(); i++ )
            {
                _c.implement( astImplements.get( i ).getName() );
            }            
        }       
        List<BodyDeclaration> astMembers = astClassDecl.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            
            if( astMember instanceof ConstructorDeclaration )
            {
                ConstructorDeclaration astConstructorDecl = 
                    (ConstructorDeclaration)astMember;
                    
                String name = astConstructorDecl.getName();
                _modifiers mods = _modifiers.of(
                    astConstructorDecl.getModifiers() );
                    
                _throws throwsEx = new _throws();
                for( int j = 0; j < astConstructorDecl.getThrows().size(); j++ )
                {
                    throwsEx.addThrows( 
                        astConstructorDecl.getThrows().get( j ).toString() );
                }
                List<Parameter>astParameters = 
                    astConstructorDecl.getParameters();
                
                _parameters _params = new _parameters();
                for( int j = 0; j < astParameters.size(); j++ )
                {
                    _params.add(
                        _parameters.of( astParameters.get( j ).toString() ) );                        
                }
                _constructor _ctor = new _constructor( 
                    mods, name, _params, throwsEx );
                    
                //set the body
                List<Statement>statements = 
                    astConstructorDecl.getBlock().getStmts();
                    
                for( int j = 0; j < statements.size(); j++ )
                {
                    _ctor.body( statements.get( j ).toString() );
                }
                    
                //set annotations on the constructor 
                List<AnnotationExpr> astCtorAnnots = 
                    astConstructorDecl.getAnnotations();
                for( int j = 0; j < astCtorAnnots.size(); j++ )
                {
                    _ctor.annotate( astCtorAnnots.get( j ).toString() );
                }
                
                //TODO I NEED TO CLEAN UP/ NORMALIZE THE JAVADOCS
                if( astConstructorDecl.getJavaDoc() != null )
                {
                    _ctor.javadoc( astConstructorDecl.getJavaDoc().getContent() );
                }                
                _c.constructor( _ctor );                    
            }
            else if( astMember instanceof MethodDeclaration )
            {
                MethodDeclaration astMethodDecl = (MethodDeclaration)astMember;
                    
                String methd = 
                    astMethodDecl.getDeclarationAsString( true, true, true );
                
                List<AnnotationExpr> astMethodAnnots = 
                    astMethodDecl.getAnnotations();
                String body = null;
                if( astMethodDecl.getBody() != null )
                {
                    //I could "getStatements()"
                    //TODO I need to normalize the code inside methods
                    body = astMethodDecl.getBody().toString();
                    body = body.substring( 
                        body.indexOf( "{" ) + 1, body.lastIndexOf( "}" ) ).trim();
                }
                
                if( astMethodDecl.getJavaDoc() == null )
                {
                    _method _meth = _method.of( methd, _code.of( body ) );     
                        
                    for( int k = 0; k < astMethodAnnots.size(); k++ )
                    {
                        _meth.annotate( astMethodAnnots.get( k ).toString() );
                    }
                    _c.method(_meth );
                }
                else
                {
                    _method _meth = _method.of(
                        astMethodDecl.getJavaDoc().getContent(), methd );
                    if( body != null )
                    {
                        _meth.body( body );
                    }
                    for( int k = 0; k < astMethodAnnots.size(); k++ )
                    {
                        _meth.annotate( astMethodAnnots.get( k ).toString() );
                    }
                    _c.method(_meth );
                }                    
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                FieldDeclaration astFieldDecl = (FieldDeclaration)astMember;
                    
                //they could be doing this:
                //int a,b,c;
                JavadocComment astFieldJavaDoc = astFieldDecl.getJavaDoc();
                List<VariableDeclarator>astVars = astFieldDecl.getVariables();
                List<AnnotationExpr>astFieldAnnots = astFieldDecl.getAnnotations();
                for( int j = 0; j < astVars.size(); j++ )
                {
                    String name = astVars.get( j ).getId().getName();
                    String init = null;
                    if( astVars.get( j ).getInit() != null )
                    {
                        init = astVars.get( j ).getInit().toString();
                    }
                    String type = astFieldDecl.getType().toString();
                    _modifiers _mods = _modifiers.of( astFieldDecl.getModifiers() );
                        
                    _field _f = null;
                    if( init == null || init.trim().length() == 0 )
                    {
                        _f = new _fields._field( _mods, type, name );
                    }
                    else
                    {
                        _f = new _fields._field( 
                            _mods, type, name, _fields._init.of( init ) ); 
                    }
                    for( int k = 0; k < astFieldAnnots.size(); k++ )
                    {
                        _f.annotate( astFieldAnnots.get( k ).toString() );
                    }
                    //todo I need to clean this up
                    if( astFieldJavaDoc != null)
                    {
                        _f.javadoc( astFieldJavaDoc.getContent() );
                    }
                    _c.field(_f );
                }                                 
            }
            else if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedDecl = 
                    (ClassOrInterfaceDeclaration)astMember;
                    
                if( astNestedDecl.isInterface() )
                {   //nested interface
                    _interface _nestedInterface = _interface.of( 
                        "interface " + astNestedDecl.getName() );
                    _nestedInterface = _interfaceFromAST(
                        _nestedInterface, astNestedDecl );
                    _c.nest( _nestedInterface );
                }
                else
                {   //nested class                        
                    _class _nestedClass = 
                        _classFromAST(
                            _class.of( astNestedDecl.getName() ), astNestedDecl );
                    _c.nest( _nestedClass );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {   //nested enum
                EnumDeclaration astNestedEnumDecl = (EnumDeclaration)astMember;
                _enum _nestedEnum = _enum.of( "enum " + astNestedEnumDecl.getName() );
                _nestedEnum = _enumFromAST( _nestedEnum, astNestedEnumDecl );
                _c.nest( _nestedEnum );
            }
            else if( astMember instanceof InitializerDeclaration )
            {   //static block
                InitializerDeclaration astBlock = 
                    (InitializerDeclaration)astMember;
                
                if( astBlock.isStatic() )
                {
                    _c.staticBlock( astBlock.getBlock().getStmts().toArray() );
                }
                else
                {
                    //code block??
                    LOG.error(" UNKNOWN CLODE BLOCK MEMBER " + astMember + System.lineSeparator() 
                        + " of " + astMember.getClass() );   
                }
            }
            else
            {
                LOG.error( "Unable to handle AST MEMBER "+ astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );                        
            }
        }
        return _c;       
    }
    
    /**
     * Builds an _enum MetaLang model based on AST root node containing a 
     * enumDeclaration.
     *
     * @param astRoot the Root AST node for the declaring class
     * @param astEnumDecl the AST node containing the Enum Declaration
     * @return an updated _enum MetaLang model
     */
    public static _enum _enumFrom( 
        CompilationUnit astRoot, EnumDeclaration astEnumDecl )
    {            
        _enum _e = null;
        if( astRoot.getPackage() != null )
        {
            _e = _enum.of( 
                astRoot.getPackage().getPackageName(), 
                astEnumDecl.getName() );
        }
        else
        {
            _e = _enum.of( "enum " + astEnumDecl.getName() );
        }
        
        List<ImportDeclaration>astImports = astRoot.getImports();
        
        for( int i = 0; i < astImports.size(); i++ )
        {
            if( astImports.get(i).isStatic() )
            {
                _e.importsStatic( 
                    astImports.get( i ).getName().toStringWithoutComments()+ ".*" );
            }
            else
            {
                _e.imports(
                    astImports.get( i ).getName().toStringWithoutComments() );
            }
        }
        return _enumFromAST( _e, astEnumDecl );
    }
        
    /**
     * updates an _enum MetaLang model based on the content stored within an 
     * <CODE>EnumDeclaration</CODE> AST enum Declaration.
     *
     * @param _e the _enum MetaLang model to be updated
     * @param astEnumDecl the Ast Enum Declaration containing the content
     * @return the updated _enum
     */
    public static _enum _enumFromAST( _enum _e, EnumDeclaration astEnumDecl )
    {
        List<AnnotationExpr>astEnumAnnots = astEnumDecl.getAnnotations();        
        JavadocComment astEnumJavadoc = astEnumDecl.getJavaDoc();
        if( astEnumJavadoc != null )
        {
            _e.javadoc( astEnumJavadoc.getContent() );
        }
            
        _e.setModifiers( _modifiers.of( astEnumDecl.getModifiers() ) );
        for( int i = 0; i < astEnumAnnots.size(); i++ )
        {
            _e.annotate( astEnumAnnots.get( i ).toString() );
        }
        
        List<ClassOrInterfaceType>astImplements = astEnumDecl.getImplements();
        if( astImplements != null && astImplements.size() > 0 )
        {
            for( int i = 0; i< astImplements.size(); i++ )
            {
                _e.implement( astImplements.get( i ).getName() );
            }            
        }
        
        List<EnumConstantDeclaration>astEnumConstants = astEnumDecl.getEntries();
        for( int i = 0; i < astEnumConstants.size(); i++ )
        {
            EnumConstantDeclaration astConstDecl = astEnumConstants.get( i );
            _e.value( astConstDecl.getName(), astConstDecl.getArgs().toArray() );
        }
            
        List<BodyDeclaration>astMembers = astEnumDecl.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            if( astMember instanceof ConstructorDeclaration )
            {
                ConstructorDeclaration astConstDecl = 
                    (ConstructorDeclaration)astMember;
                    
                String name = astConstDecl.getName();
                _modifiers _ctorMods = _modifiers.of( astConstDecl.getModifiers() );
                    
                _throws throwsEx = new _throws();
                for( int j = 0; j < astConstDecl.getThrows().size(); j++ )
                {
                    throwsEx.addThrows( 
                        astConstDecl.getThrows().get( j ).toString() );
                }
                
                List<Parameter>astParameters =  astConstDecl.getParameters();
                _parameters _params = new _parameters();
                for( int j = 0; j < astParameters.size(); j++ )
                {
                    _params.add( _parameters.of( astParameters.get( j ).toString() ) );                        
                }
                _constructor _ctor = new _constructor( 
                    _ctorMods, name, _params, throwsEx );
                    
                //set the body
                List<Statement>astBodyStatements = astConstDecl.getBlock().getStmts();
                    
                for( int j = 0; j < astBodyStatements.size(); j++ )
                {
                    _ctor.body( astBodyStatements.get( j ).toString() );
                }
                
                //set annotations on the constructor 
                List<AnnotationExpr> astCtorAnnots = astConstDecl.getAnnotations();
                for( int j = 0; j < astCtorAnnots.size(); j++ )
                {
                    _ctor.annotate( astCtorAnnots.get( j ).toString() );
                }
                
                if( astConstDecl.getJavaDoc() != null )
                {
                    //_javadoc doc = new _javadoc();
                    _ctor.javadoc( astConstDecl.getJavaDoc().getContent() );    
                }
                
                _e.constructor( _ctor );
                    
            }
            else if( astMember instanceof MethodDeclaration )
            {
                MethodDeclaration astMethodDecl = (MethodDeclaration) astMember;
                    
                String methd  = astMethodDecl.getDeclarationAsString( true, true, true );
                    
                List<AnnotationExpr> astMethodAnnots = astMethodDecl.getAnnotations();
                String body = astMethodDecl.getBody().toString();
                body = body.substring( body.indexOf('{')+1, body.lastIndexOf( "}") ).trim();
                if( astMethodDecl.getJavaDoc() == null )
                {
                    _method _meth = _method.of( methd, _code.of( body ) );     
                        
                    for( int k = 0; k < astMethodAnnots.size(); k++ )
                    {
                        _meth.annotate( astMethodAnnots.get( k ).toString() );
                    }
                    _e.method(_meth );
                }
                else
                {
                    _methods._method _meth = _methods._method.of(
                        astMethodDecl.getJavaDoc().getContent(), methd, body );      
                    for( int k = 0; k < astMethodAnnots.size(); k++ )
                    {
                        _meth.annotate( astMethodAnnots.get( k ).toString() );
                    }
                    _e.method(_meth );
                }
                    
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                FieldDeclaration astFieldDecl = (FieldDeclaration) astMember;
                JavadocComment astFieldJavaDoc = astFieldDecl.getJavaDoc();
                List<VariableDeclarator>astFieldVars = astFieldDecl.getVariables();
                List<AnnotationExpr>astFieldAnnots = astFieldDecl.getAnnotations();
                
                for( int j = 0; j < astFieldVars.size(); j++ )
                {
                    String name = astFieldVars.get( j ).getId().getName();
                    String init = null;
                    if( astFieldVars.get( j ).getInit() != null )
                    {
                        init = astFieldVars.get( j ).getInit().toString();
                    }
                    String type = astFieldDecl.getType().toString();
                    _modifiers _mods = _modifiers.of( astFieldDecl.getModifiers() );
                        
                    _field _f = null;
                    if( init == null || init.trim().length() == 0 )
                    {
                        _f = new _fields._field( _mods, type, name );
                    }
                    else
                    {
                        _f = new _fields._field( 
                            _mods, type, name, _fields._init.of( init ) ); 
                    }
                    for( int k = 0; k < astFieldAnnots.size(); k++ )
                    {
                        _f.annotate( astFieldAnnots.get( k ).toString() );
                    }
                    //todo I need to clean this up
                    if( astFieldJavaDoc != null)
                    {
                        _f.javadoc( astFieldJavaDoc.getContent() );
                    }
                    _e.field( _f );
                }                                
            }
            else if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedTypeDef = 
                    (ClassOrInterfaceDeclaration)astMember;
                    
                if( astNestedTypeDef.isInterface() )
                {
                    _interface _nestedInterface = _interface.of(
                        "interface " + astNestedTypeDef.getName() );
                    _nestedInterface = _interfaceFromAST(
                        _nestedInterface, astNestedTypeDef );
                    _e.nest( _nestedInterface );
                }
                else
                {
                    _class _nestedClass = _class.of( astNestedTypeDef.getName() );
                    _nestedClass = _classFromAST( _nestedClass, astNestedTypeDef );
                    _e.nest(_nestedClass );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {
                EnumDeclaration astNestdEnum = (EnumDeclaration)astMember;
                _enum _nestedEnum = _enum.of( "enum "+ astNestdEnum.getName() );
                _nestedEnum = _enumFromAST( _nestedEnum, astNestdEnum );
                _e.nest( _nestedEnum );
            }
            else
            {
                LOG.error( "Unhandled AST Member " + astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );              
            }
        }
        return _e;
    }    
}
