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
package varcode.java.load;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;

import varcode.LoadException;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.ast.JavaAst;
import varcode.java.ast.JavaCodeFormatVisitor;
import varcode.java.model._annotationType;
import varcode.java.model._annotationType._annotationProperty;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum;
import varcode.java.model._enum._constants._constant;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._generic._typeParams;
import varcode.java.model._imports;
import varcode.java.model._interface;
import varcode.java.model._javadoc;
import varcode.java.model._methods._method;
import varcode.java.model._modifiers;
import varcode.java.model._package;
import varcode.java.model._parameters;
import varcode.java.model._throws;
import varcode.ModelException;
import varcode.java.model._Java.Annotated;
import varcode.java.model._anns;
import varcode.java.model._ann;

/**
 * "Ports" Java code from the Java AST (Node-based) representation 
 * of Code into {@code _class, _interface, _enum, _annotationType } models.
 *
 * We do NOT load source code (as a String) directly into _class, _enum, 
 * _interface, _annotationType models, instead we convert the String to an AST 
 * and then into the (_class, _enum...) model.
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class _JavaAstPort
{   
    public static _imports _importsFrom( List<ImportDeclaration> astImports )
    {        
        _imports _imps = new _imports();
        
        for( int i = 0; i < astImports.size(); i++ )
        {
            if( astImports.get( i ).isStatic() )
            {
                _imps.addStaticImport(
                     astImports.get( i ).getName().toStringWithoutComments()+ ".*" );                    
            }
            else
            {
                _imps.addImport(
                    astImports.get( i ).getName().toStringWithoutComments() );                
            }
        }
        return _imps;
    }
    
    /**
     * Creates and atReturn an _interface MetaLang model from the AST Root node 
     * containing a astInterfaceDecl
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astInterface the interface Declaration AST Node 
     * (is a CHILD of the rootASTNode)
     * @param codeFormatter formatter for code internals
     * @return the _interface MetaLang model representing the Java code.
     */
    public static _interface _interfaceFrom( 
        CompilationUnit astRoot, 
        ClassOrInterfaceDeclaration astInterface,
        JavaCodeFormatVisitor codeFormatter )
    {
        return _interfaceFrom( astRoot, astInterface, codeFormatter, true, true );
    }
    
    public static _interface _interfaceFrom( 
        CompilationUnit astRoot, 
        ClassOrInterfaceDeclaration astInterface,
        JavaCodeFormatVisitor codeFormatter,
        boolean retainPackage, boolean retainImports )
    {
        if( !astInterface.isInterface() )
        {
            throw new LoadException( 
                astInterface.getName() + " NOT an interface" );
        }
        //I need to create an _interface MLM  to start populating 
        _interface _i = null;
        if( astRoot.getPackage() != null && retainPackage )
        {
            _i = _interface.of( _package.of( astRoot.getPackage().getPackageName() ), 
                "interface " + astInterface.getName() );
        }
        else
        {
            _i = _interface.of( "interface " 
                + astInterface.getName() );
        }
        if( retainImports )
        {
            _i.setImports( _importsFrom( astRoot.getImports() ) );       
        }
        return _interfaceFromAST( _i, astInterface, codeFormatter );
    }
        
    public static void annotate( 
        Annotated at, List<AnnotationExpr>astAnnotations )
    {        
        _anns _anns = new _anns();
        for( int i = 0; i < astAnnotations.size(); i++ )
        {
            AnnotationExpr astAnn = astAnnotations.get( i );
            _ann _ann = new _ann( astAnn.getName().toString() );
            System.out.println( astAnn.getChildrenNodes() );
            _anns.add( _ann );
            
        }
        at.annotate( _anns );
    }
    /**
     * build/update an _interface MetaLang model based on the contents within a 
     * interface AST node  
     * @param _i the _interface model to update
     * @param astInterface the interface declaration AST Node
     * @param codeFormatter formatter for code internals
     * @return the modified _interface MetaLang model 
     */
    public static _interface _interfaceFromAST( 
        _interface _i, ClassOrInterfaceDeclaration astInterface,
        JavaCodeFormatVisitor codeFormatter )   
    {
        _i.getSignature().setModifiers( 
            _modifiers.of( astInterface.getModifiers() ) );
        
        //Class Type Parameters.., like K, V in "Map<K, V>"
        List<TypeParameter>astTypeParams = astInterface.getTypeParameters();
        for( int i=0; i < astTypeParams.size(); i++ )
        {
            _i.getSignature().getGenericTypeParams().addParam( 
                astTypeParams.get( i ).toString() );
        }
        
        if( astInterface.getJavaDoc() != null )
        {
            _i.javadoc( _javadocFromAST( astInterface.getJavaDoc() ) );
        }
        
        List<AnnotationExpr>astAnnotations = 
            astInterface.getAnnotations();
        for( int i = 0; i < astAnnotations.size(); i++ )
        {
            _i.annotate( astAnnotations.get( i ).toString() );
        }
        List<ClassOrInterfaceType>astExtends = 
            astInterface.getExtends();
        if( astExtends != null && astExtends.size() == 1 )
        {
            for( int i = 0; i < astExtends.size(); i++ )
            {
                _i.extend( astExtends.get( i ).getName() );
            }
        }           
        List<BodyDeclaration> astMembers = astInterface.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            if( astMember instanceof MethodDeclaration )
            {
                //System.out.println( "HERE METHOD DECL");
                _i.method( 
                    _methodFromAST( (MethodDeclaration)astMember, codeFormatter ) );                
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                _i.fields(
                    _fieldsFromAST( 
                        (FieldDeclaration)astMember, codeFormatter ) );           
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
                        _nestedInterface, astNestedClassInterfaceDecl, codeFormatter );
                    _i.nest( _nestedInterface );
                }
                else
                {
                    _class _nestedClass = _class.of( 
                        astNestedClassInterfaceDecl.getName() );
                    _nestedClass = _classFromAST(
                        _nestedClass, astNestedClassInterfaceDecl, codeFormatter );
                    _i.nest( _nestedClass );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {
                EnumDeclaration astNestedEnum = (EnumDeclaration)astMember;
                _enum _nestedEnum = _enum.of( 
                    "enum " + astNestedEnum.getName() );
                _nestedEnum = _enumFromAST( _nestedEnum, astNestedEnum, 
                    codeFormatter );
                _i.nest( _nestedEnum );
            }            
            else if( astMember instanceof AnnotationDeclaration )
            {
                AnnotationDeclaration nestAstAnnotationDecl = 
                    (AnnotationDeclaration)astMember;
                _annotationType _nestAt = new _annotationType(
                    _annotationType._signature.of( 
                        "@interface "+ nestAstAnnotationDecl.getName()) ); 
                _nestAt = _annotationTypeFromAST( 
                    _nestAt, nestAstAnnotationDecl, codeFormatter );
                _i.nest( _nestAt );
            }
            else
            {
                System.err.println( 
                    " UNKNOWN AST MEMBER " + astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );       
            }
        }            
        return _i;            
    }
    
     /**
     * Creates and atReturn a _class MetaLang model from the AST Root node 
     * containing a AST Class Declaration.
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astAnnotationType the Annotation Type Declaration AST Node 
     * (is a CHILD of the astRoot)
     * @return the _annotationType Model representing the Java code.
     */
    public static _annotationType _annotationTypeFrom( 
        CompilationUnit astRoot, AnnotationDeclaration astAnnotationType )
    {
        return _annotationTypeFrom( 
            astRoot, 
            astAnnotationType, 
            new FormatJavaCode_AllmanScanStyle() );
    }
    
    /**
     * Creates and atReturn a _class MetaLang model from the AST Root node 
     * containing a AST Class Declaration.
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astAnnotationType the Annotation Type Declaration AST Node 
     * (is a CHILD of the astRoot)
     * @param codeFormatter
     * @return the _annotationType Model representing the Java code.
     */
    public static _annotationType _annotationTypeFrom( 
        CompilationUnit astRoot, AnnotationDeclaration astAnnotationType,
        JavaCodeFormatVisitor codeFormatter )
    {
        return _annotationTypeFrom( 
            astRoot, astAnnotationType, codeFormatter, true, true );
    }
    
    public static _annotationType _annotationTypeFrom( 
        CompilationUnit astRoot, AnnotationDeclaration astAnnotationType,
        JavaCodeFormatVisitor codeFormatter, 
        boolean retainPackage, boolean retainImports )
    {
        _annotationType _at = null;
        if( astRoot == null )
        {
            _at = new _annotationType ( _annotationType._signature.of( 
                "@interface " + astAnnotationType.getName() ) );
            return _annotationTypeFromAST(_at, astAnnotationType, codeFormatter );
        }
        if( astRoot.getPackage() != null & retainPackage )
        {
            _at = new _annotationType ( _annotationType._signature.of( 
                "@interface " + astAnnotationType.getName() ) );
            _at.setPackage( _package.of( astRoot.getPackage().getPackageName() ));
        }
        else
        {
            _at = new _annotationType( 
                _annotationType._signature.of( 
                    "@interface " + astAnnotationType.getName() ) );
        }
        if( retainImports )
        {
            _at.imports( _importsFrom( astRoot.getImports() ) );
        }

        return _annotationTypeFromAST( _at, astAnnotationType, codeFormatter );
    }
    
    /**
     * Build/Update an <CODE>_annotationType</CODE> Model based on the 
     * <CODE>astAnnotationDecl</CODE>
     * @param _at the _class MetaLang Model to be updated / built
     * @param astAnnotationDecl the AST classDeclaration root node
     * @param codeFormatter
     * @return the updated _annotationType Model
     */
    public static _annotationType _annotationTypeFromAST( 
        _annotationType _at, AnnotationDeclaration astAnnotationDecl,
        JavaCodeFormatVisitor codeFormatter )
    {        
        JavadocComment astClassJavaDoc = astAnnotationDecl.getJavaDoc();
        if( astClassJavaDoc != null )
        {
            _at.javadoc( _javadocFromAST( astClassJavaDoc ) );
        }
        
        _at.setModifiers( astAnnotationDecl.getModifiers() );

        
        List<AnnotationExpr>astClassAnnots = astAnnotationDecl.getAnnotations();
        for( int i = 0; i < astClassAnnots.size(); i++ )
        {
            _at.annotate( astClassAnnots.get( i ).toString() );
        }
        
        List<BodyDeclaration> astMembers = astAnnotationDecl.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            
            if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedDecl = 
                    (ClassOrInterfaceDeclaration)astMember;
                    
                if( astNestedDecl.isInterface() )
                {   //nested interface
                    _interface _nestI = _interface.of( 
                        "interface " + astNestedDecl.getName() );
                    _nestI = _interfaceFromAST( 
                        _nestI, astNestedDecl, codeFormatter );
                    _at.nest( _nestI );
                }
                else
                {   //nested class                        
                    _class _nestC = 
                        _classFromAST(
                            _class.of( astNestedDecl.getName() ), 
                            astNestedDecl, codeFormatter );
                    _at.nest( _nestC );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {   //nested enum
                EnumDeclaration astNestedEnumDecl = (EnumDeclaration)astMember;
                _enum _nestE = _enum.of( "enum " + astNestedEnumDecl.getName() );
                _nestE = _enumFromAST( _nestE, astNestedEnumDecl, codeFormatter );
                _at.nest( _nestE );
            }   
            else if( astMember instanceof AnnotationDeclaration )
            {
                AnnotationDeclaration nestAstAnnotationDecl = 
                    (AnnotationDeclaration)astMember;
                _annotationType _nestAt = new _annotationType(
                    _annotationType._signature.of( 
                        "@interface "+ nestAstAnnotationDecl.getName()) ); 
                _nestAt = _annotationTypeFromAST( 
                    _nestAt, 
                    nestAstAnnotationDecl, 
                    codeFormatter );
                _at.nest( _nestAt );
            }
            else if( astMember instanceof AnnotationMemberDeclaration )
            {
                AnnotationMemberDeclaration amd = 
                    (AnnotationMemberDeclaration)astMember;
                
                _annotationProperty _ap = _annotationProperty.of(
                    amd.getType().toString(), amd.getName() );
                if( amd.getDefaultValue() != null )
                {   //amd.getDefaultValue();
                    //hmm, I could                     
                    _ap.setDefault( 
                        JavaAst.formattedCodeFrom( 
                            amd.getDefaultValue(), codeFormatter ) );
                }                
                if( amd.getJavaDoc() != null )
                {
                    _ap.javadoc( _javadocFromAST( amd.getJavaDoc() ) );
                }                
                List<AnnotationExpr>astAnnots = amd.getAnnotations();
                if( astAnnots != null )
                {
                    for( int j = 0; j < astAnnots.size(); j++ )
                    {
                        _ap.annotate( astAnnots.get( j ).toString() );
                    }
                }
                _at.property( _ap );                
            }
            else if ( astMember instanceof EmptyMemberDeclaration )
            {
                EmptyMemberDeclaration emd = (EmptyMemberDeclaration)astMember; 
                
                System.err.println( "Empty Member declaration "+ astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() ); 
            }
            else
            {
                System.err.println( "Unable to handle AST MEMBER "+ astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );   
                
                //LOG.error( "Unable to handle AST MEMBER "+ astMember + System.lineSeparator() 
                //    + " of " + astMember.getClass() );                        
            }
        }
        return _at;       
    }
    
    /**
     * Creates and atReturn a _class MetaLang model from the AST Root node 
     * containing a AST Class Declaration.
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astClass the class Declaration AST Node 
     * (is a CHILD of the astRoot)
     * @param codeFormatter
     * @return the _class model representing the Java code.
     */
    public static _class _classFrom( 
        CompilationUnit astRoot, 
        ClassOrInterfaceDeclaration astClass, 
        JavaCodeFormatVisitor codeFormatter )
    {
        boolean retainPackage = true;
        boolean retainImports = true;
        
        return _classFrom( 
            astRoot, astClass, codeFormatter, retainPackage, retainImports );
    }
    
    /**
     * Creates and atReturn a _class MetaLang model from the AST Root node 
     * containing a AST Class Declaration.
     *
     * @param astRoot the top level AST CompilationUnit node of an interface
     * (contains package imports, etc. of the File)
     * @param astClass the class Declaration AST Node 
     * (is a CHILD of the astRoot)
     * @param codeFormatter the formatter for 
     * @param retainPackage IF we are loading a nested class, 
     * does the nested class retain the package of the declaring class?
     * @param retainImports IF we are loading a nested class, 
     * does the nested class retain the imports of the declaring class?
     * @return the _class MetaLang model representing the Java code.
     */    
    public static _class _classFrom( 
        CompilationUnit astRoot, 
        ClassOrInterfaceDeclaration astClass, 
        JavaCodeFormatVisitor codeFormatter,
        boolean retainPackage,
        boolean retainImports )
    {    
        _class _c = null;
        if( astRoot == null )
        {   //we are loading a Top Level class (i.e. NOT a nested class)
            _c = _class.of( astClass.getName() );
            return _classFromAST(_c, astClass, codeFormatter );
        }
        //if it's a nested class and I want to retain the package
        if( astRoot.getPackage() != null && retainPackage )
        {
            _c = _class.of(
                _package.of( astRoot.getPackage().getPackageName()), 
                astClass.getName() );
        }
        else
        {
            _c = _class.of( astClass.getName() );
        }
        if( retainImports )
        {
            _c.setImports( _importsFrom( astRoot.getImports() ) );
        }

        return _classFromAST(_c, astClass, codeFormatter );
    }
        
    /**
     * Given a block statement return the _code block representation
     * @param astBlockStatement the block statement
     * @return the _code representation
     */
    public static _code _codeFrom( BlockStmt astBlockStatement )
    {                
        return _codeFrom( astBlockStatement.getStmts() );        
    }
    
    /**
     * 
     * @param astStmts
     * @return the String representation of the code
     */
    public static _code _codeFrom( List<Statement> astStmts )
    {        
        String code = JavaAst.formattedCodeFrom( astStmts );        
        code = code.trim();
        return _code.of( code );
    }
    
    /**
     * Given the AST for a Javadoc, return the _javadoc metaLangModel
     * @param astJavadoc the AST representation of the Javadoc
     * @return the _javadoc meta model
     */
    public static _javadoc _javadocFromAST( JavadocComment astJavadoc )
    {
        /** having the first empty line in a javadoc is extraneous */
        boolean swallowedPrefixBlankLine = false;
        String javadocContent = astJavadoc.getContent();
        LineNumberReader lineNumberReader = 
            new LineNumberReader( new StringReader( javadocContent ) );
        _javadoc _jd = new _javadoc();        
        try
        {            
            String line = lineNumberReader.readLine();
            while( line != null )
            {
                String trimmed = line.trim();
                if( trimmed.length() == 0 )
                {   //if the javadoc empty & line is blank, lets swallow it (first line)
                    if( _jd.isEmpty() && !swallowedPrefixBlankLine )
                    {
                        swallowedPrefixBlankLine = true;
                        line = lineNumberReader.readLine();
                    }
                    else 
                    {   //read the next line, if null, (end) then continue (dont print it)
                        String nextLine = lineNumberReader.readLine();
                        if( nextLine == null )
                        {
                            line = nextLine;
                        }
                        else
                        {   //some blank line that is neither the first blank line
                            //nor is it a blank line BEFORE the end, so
                            _jd.add( System.lineSeparator() );
                            line = nextLine;
                        }
                    }
                }
                else 
                {
                    if( trimmed.startsWith( "* ") )
                    {
                        line = trimmed.substring( 2 );
                    }   
                    if( trimmed.startsWith( "*") )
                    {
                        line = trimmed.substring( 1 );
                    }
                    _jd.add( line );
                    line = lineNumberReader.readLine();
                }
            }
        }
        catch( IOException e )
        {
            throw new ModelException( 
                "Unable to read in Javadoc from " + javadocContent );
        }
        return _jd;
        //return new _javadoc( javadocContent );
    }
    
    
    /**
     * Creates/ atReturn the _method (MetaLangModel) from the 
     * AST representation
     * @param astMethod Root ASTNode for the method
     * @param codeFormatter
     * @return 
     */
    public static _method _methodFromAST( 
        MethodDeclaration astMethod, JavaCodeFormatVisitor codeFormatter )
    {        
        String methd = //astMethodDecl.toString();
            astMethod.getDeclarationAsString( true, true, true );
          
        List<AnnotationExpr> astMethodAnnots = 
            astMethod.getAnnotations();
        
        _method _m = _method.of( methd );
        List<TypeParameter> astTypeParams = astMethod.getTypeParameters();
        if( astTypeParams != null )
        {
            _typeParams _tps = new _typeParams();
            for( int j = 0; j < astTypeParams.size(); j++ )
            {
                _tps.addParam( astTypeParams.get( j ).toString() );
            }
            _m.getSignature().setGenericTypeParams( _tps );
        }
        if( astMethod.isDefault() )
        {
            //System.out.println( "default" );
            _m.getModifiers().set( "default" );
        }
        
        if( astMethod.getBody() != null )
        {
            _m.setBody( _codeFrom( astMethod.getBody() ) );
        }              
        
        if( astMethod.getJavaDoc() != null )
        {
            _m.javadoc( _javadocFromAST( astMethod.getJavaDoc() ) ); 
        }
        for( int k = 0; k < astMethodAnnots.size(); k++ )
        {
            _m.annotate( _ann.of(  astMethodAnnots.get( k ).toString() ) );
        }
        return _m;                                
    }
    
    public static _constructor _constructorFromAST( 
        ConstructorDeclaration astCtor )
    {
        return _constructorFromAST( 
            astCtor, new FormatJavaCode_AllmanScanStyle() );
    }
    /**
     * Given the ast declaration of the constructor, return the _constructor 
     * MetaLang Model representation
     * 
     * @param astCtor the ast representation of a constructor
     * @param codeFormatter
     * @return the _constructor representing the AST
     */
    public static _constructor _constructorFromAST( 
        ConstructorDeclaration astCtor,
        JavaCodeFormatVisitor codeFormatter )
    {
        String name = astCtor.getName();
        _modifiers mods = _modifiers.of(astCtor.getModifiers() );
        
        List<TypeParameter> astTypeParams = astCtor.getTypeParameters();
        
        _typeParams _tp = new _typeParams();
        for( int i = 0; i < astTypeParams.size(); i++ )
        {
            _tp.addParam( astTypeParams.get( i ).toString() );
        }
        _throws _ts = new _throws();
        for( int j = 0; j < astCtor.getThrows().size(); j++ )
        {
            _ts.addThrows( astCtor.getThrows().get( j ).toString() );
        }
        
        List<Parameter>astParameters = 
            astCtor.getParameters();
                
        _parameters _ps = new _parameters();
        for( int j = 0; j < astParameters.size(); j++ )
        {
            _ps.add(
                _parameters.of( astParameters.get( j ).toString() ) );                        
        }
        _constructor _ctor = new _constructor( 
            mods, _tp, name, _ps, _ts );
        
        _ctor.body( _codeFrom( astCtor.getBlock() ) );
 
        //set annotations on the constructor 
        List<AnnotationExpr> astCtorAnnots = 
            astCtor.getAnnotations();
            
        for( int j = 0; j < astCtorAnnots.size(); j++ )
        {
            _ctor.annotate( _ann.of(  astCtorAnnots.get( j ).toString() ) );
        }
                
        if( astCtor.getJavaDoc() != null )
        {
            _ctor.javadoc( _javadocFromAST( astCtor.getJavaDoc() ) );
        }                
        return _ctor;            
    }
    
    /**
     * Given an AST FieldDeclaration return the appropriate _fields
     * that model the declaration
     * 
     * NOTE: a single AST FieldDeclaration can signify multiple fields
     * since we can declare multiple fields of a single type with one "statement"
     * i.e. The following is a single (AST) FieldDeclaration <PRE>
     * int x,y,z;</PRE>
     * 
     * and it equates to multiple _field s
     * _fields.of( "int x", "int y", "int z" );
     * 
     * @param astField the AST Field Declaration
     * @param codeFormatter code formatter, used when fields have initialization
     * @return the _fields modeling the fields declared
     */
    public static _fields _fieldsFromAST( 
        FieldDeclaration astField, JavaCodeFormatVisitor codeFormatter )
    {
        //they could be doing this:
        //int a,b,c;
        JavadocComment astFieldJavaDoc = astField.getJavaDoc();
        List<VariableDeclarator>astVars = astField.getVariables();
        List<AnnotationExpr>astFieldAnnots = astField.getAnnotations();
        
        _fields _fs = new _fields();
        
        for( int j = 0; j < astVars.size(); j++ )
        {
            String name = astVars.get( j ).getId().getName();
            String init = null;
            
            if( astVars.get( j ).getInit() != null )
            {
                init = astVars.get( j ).getInit().toString();
            }
            String type = astField.getType().toString();
            _modifiers _mods = _modifiers.of( astField.getModifiers() );
                        
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
            
            for( int k = 0; k < astFieldAnnots.size(); k++ )
            {
                _f.annotate( _ann.of( astFieldAnnots.get( k ).toString()) );
            }
            
            if( astFieldJavaDoc != null)
            {
                _f.javadoc(  _javadocFromAST( astFieldJavaDoc ) );
            }
            _fs.add( _f );
        }
        return _fs;        
    }
    
    /**
     * Build/Update a <CODE>_class</CODE> MetaLang model based on the <CODE>astClassDecl</CODE>
     * @param _c the _class MetaLang Model to be updated / built
     * @param astClass the AST classDeclaration root node
     * @param codeFormatter
     * @return the updated _class MetaLang model
     */
    public static _class _classFromAST( 
        _class _c, ClassOrInterfaceDeclaration astClass,
        JavaCodeFormatVisitor codeFormatter )
    {        
        JavadocComment astClassJavaDoc = astClass.getJavaDoc();
        if( astClassJavaDoc != null )
        {
            _c.javadoc( _javadocFromAST( astClassJavaDoc ) );
        }
        
        _c.setModifiers( astClass.getModifiers() );

        List<AnnotationExpr>astClassAnnots = astClass.getAnnotations();
        for( int i = 0; i < astClassAnnots.size(); i++ )
        {
            _c.annotate( _ann.of( astClassAnnots.get( i ).toString()) );
        }
        
        List<ClassOrInterfaceType>astExtends = astClass.getExtends();
        if( astExtends != null && astExtends.size() == 1 )
        {
            _c.extend( astExtends.get( 0 ).getName() );            
        }        
        List<ClassOrInterfaceType>astImplements = astClass.getImplements();
        if( astImplements != null && astImplements.size() > 0 )
        {
            for( int i = 0; i < astImplements.size(); i++ )
            {
                _c.implement( astImplements.get( i ).getName() );
            }            
        }       
        List<BodyDeclaration> astMembers = astClass.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            
            if( astMember instanceof ConstructorDeclaration )
            {   
                _c.constructor( 
                    _constructorFromAST( 
                        (ConstructorDeclaration)astMember,
                        codeFormatter) );                
            }
            else if( astMember instanceof MethodDeclaration )
            {
                _c.method( 
                    _methodFromAST( 
                        (MethodDeclaration)astMember,
                        codeFormatter ) );                                 
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                _c.fields(
                    _fieldsFromAST( 
                        (FieldDeclaration)astMember,
                        codeFormatter ) );
            }              
            else if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedDecl = 
                    (ClassOrInterfaceDeclaration)astMember;
                    
                if( astNestedDecl.isInterface() )
                {   //nested interface
                    _interface _nestI = _interface.of( 
                        "interface " + astNestedDecl.getName() );
                    _nestI = _interfaceFromAST( 
                        _nestI, astNestedDecl, codeFormatter );
                    _c.nest( _nestI );
                }
                else
                {   //nested class                        
                    _class _nestC = 
                        _classFromAST(
                            _class.of( 
                                astNestedDecl.getName() ), 
                            astNestedDecl,
                            codeFormatter );
                    _c.nest( _nestC );
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {   //nested enum
                EnumDeclaration astNestedEnumDecl = (EnumDeclaration)astMember;
                _enum _nestE = _enum.of( "enum " + astNestedEnumDecl.getName() );
                _nestE = _enumFromAST( 
                    _nestE, astNestedEnumDecl, codeFormatter );
                _c.nest( _nestE );
            }
            else if( astMember instanceof AnnotationDeclaration )
            {
                AnnotationDeclaration astAnnotationDecl = (AnnotationDeclaration)astMember;
                _annotationType _nestAt = new _annotationType(
                    _annotationType._signature.of( 
                        "@interface "+ astAnnotationDecl.getName()) ); 
                _nestAt = _annotationTypeFromAST( 
                    _nestAt, astAnnotationDecl, codeFormatter );
                _c.nest( _nestAt );
            }
            else if( astMember instanceof InitializerDeclaration )
            {   //static block
                InitializerDeclaration astStaticBlock = 
                    (InitializerDeclaration)astMember;
                
                if( astStaticBlock.isStatic() )
                {
                    _c.staticBlock( 
                        _codeFrom( astStaticBlock.getBlock() ) );    
                        //astStaticBlock.getBlock().getStmts().toArray() );
                }
                else
                {
                    //code block??
                    System.err.println(
                        " UNKNOWN CLODE BLOCK MEMBER " + astMember + System.lineSeparator() 
                        + " of " + astMember.getClass() );   
                }
            }
            else if ( astMember instanceof EmptyMemberDeclaration )
            {
                EmptyMemberDeclaration emd = (EmptyMemberDeclaration)astMember; 
                
                System.err.println( "Empty Member declaration "+ astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() ); 
            }
            else
            {
                System.err.println( "Unable to handle AST MEMBER "+ astMember + System.lineSeparator() 
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
     * @param astEnum the AST node containing the Enum Declaration
     * @param codeFormatter
     * @return an updated _enum MetaLang model  
     */
    public static _enum _enumFrom( 
        CompilationUnit astRoot, EnumDeclaration astEnum, 
        JavaCodeFormatVisitor codeFormatter  )
    {
        return _enumFrom( astRoot, astEnum, codeFormatter, true, true );
    }
    
    public static _enum _enumFrom( 
        CompilationUnit astRoot, EnumDeclaration astEnum, 
        JavaCodeFormatVisitor codeFormatter, 
        boolean retainPackage, boolean retainImports )
    {    
        //System.out.println("**************************THE ENUM**********" );
        _enum _e = null;
        if( astRoot.getPackage() != null && retainPackage )
        {
            _e = _enum.of(_package.of( astRoot.getPackage().getPackageName() ), 
                astEnum.getName() );
        }
        else
        {
            _e = _enum.of( "enum " + astEnum.getName() );
        }
        if( retainImports )
        {
            _e.setImports( _importsFrom( astRoot.getImports() ) );
        }

        return _enumFromAST( _e, astEnum, codeFormatter );
    }
        
    /**
     * Updates an _enum meta model based on the content stored within an 
     * <CODE>EnumDeclaration</CODE> AST enum Declaration.
     *
     * @param _e the _enum MetaLang model to be updated
     * @param astEnum the Ast Enum Declaration containing the content
     * @param codeFormatter
     * @return the updated _enum
     */
    public static _enum _enumFromAST( _enum _e, EnumDeclaration astEnum,
        JavaCodeFormatVisitor codeFormatter )
    {                
        //System.out.println("**************************THE ENUM AST**********" );
        JavadocComment astEnumJavaDoc = astEnum.getJavaDoc();
        if( astEnumJavaDoc != null )
        {
            _e.javadoc( _javadocFromAST( astEnumJavaDoc ) );
        }
            
        _e.setModifiers(_modifiers.of(astEnum.getModifiers() ) );
        
        List<AnnotationExpr>astEnumAnnots = astEnum.getAnnotations();
        for( int i = 0; i < astEnumAnnots.size(); i++ )
        {   
            _e.annotate( _ann.of( astEnumAnnots.get( i ).toString()) );
        }        
        List<ClassOrInterfaceType>astImplements = astEnum.getImplements();
        if( astImplements != null && astImplements.size() > 0 )
        {
            for( int i = 0; i< astImplements.size(); i++ )
            {
                _e.implement( astImplements.get( i ).getName() );
            }            
        }
        
        List<EnumConstantDeclaration>astEnumConstants = astEnum.getEntries();
        for( int i = 0; i < astEnumConstants.size(); i++ )
        {
            EnumConstantDeclaration astConstDecl = astEnumConstants.get( i );
            
            _constant _const = _constant.of
                ( astConstDecl.getName(), astConstDecl.getArgs().toArray() );
            
            List<BodyDeclaration>bodyDecls = astConstDecl.getClassBody();
            for( int j = 0; j< bodyDecls.size(); j++ )
            {
                BodyDeclaration bd = bodyDecls.get( j );
                if( bd instanceof MethodDeclaration )
                {
                    _const.method( 
                        _methodFromAST( (MethodDeclaration)bd, codeFormatter ) );
                }
                else if( bd instanceof FieldDeclaration )
                {
                    _const.fields( 
                        _fieldsFromAST( (FieldDeclaration)bd, codeFormatter ) );
                }
                else
                {
                    System.err.println( "Unknown constant Body type " + bd );
                }
            }
            
            annotate( _const, astConstDecl.getAnnotations() );
            _e.constant( _const );
        }
            
        List<BodyDeclaration>astMembers = astEnum.getMembers();
        
        for( int i = 0; i < astMembers.size(); i++ )
        {
            BodyDeclaration astMember = astMembers.get( i );
            if( astMember instanceof ConstructorDeclaration )
            {
                _e.constructor( 
                    _constructorFromAST( 
                        (ConstructorDeclaration)astMember, 
                        codeFormatter ) );                                   
            }
            else if( astMember instanceof MethodDeclaration )
            {
                _e.method(
                    _methodFromAST( 
                        (MethodDeclaration)astMember,
                        codeFormatter ) );                           
            }
            else if( astMember instanceof FieldDeclaration ) 
            {
                _e.fields(
                    _fieldsFromAST( 
                        (FieldDeclaration)astMember,
                        codeFormatter ) );               
            }
            else if( astMember instanceof ClassOrInterfaceDeclaration )
            {
                ClassOrInterfaceDeclaration astNestedTypeDef = 
                    (ClassOrInterfaceDeclaration)astMember;
                    
                if( astNestedTypeDef.isInterface() )
                {
                    _interface _nestI = _interface.of(
                        "interface " + astNestedTypeDef.getName() );
                    _nestI = _interfaceFromAST(
                        _nestI, astNestedTypeDef, codeFormatter );
                    _e.nest(_nestI );
                }
                else
                {                    
                    _class _nestC = _class.of( astNestedTypeDef.getName() );
                    _nestC = _classFromAST( 
                        _nestC, astNestedTypeDef, codeFormatter );
                    
                    _e.nest( _nestC );
                }
            }
            else if( astMember instanceof InitializerDeclaration )
            {
                InitializerDeclaration astStaticBlock = 
                    (InitializerDeclaration)astMember;
                
                if( astStaticBlock.isStatic() )
                {
                    _e.staticBlock( 
                        _codeFrom( astStaticBlock.getBlock() ) );    
                }
                else
                {
                    if( astMember instanceof AnnotationDeclaration )
                    {
                        System.err.println( 
                            "Internal AnnotationDeclarations not supported "+ astMember );
                    }
                    else
                    {
                        //code block??
                        System.err.println( 
                            " UNKNOWN CLODE BLOCK MEMBER " + astMember + System.lineSeparator() 
                            + " of " + astMember.getClass() );   
                    }
                }
            }
            else if( astMember instanceof EnumDeclaration )
            {
                EnumDeclaration astNestdEnum = (EnumDeclaration)astMember;
                _enum _nestE = _enum.of( "enum " + astNestdEnum.getName() );
                _nestE = _enumFromAST(_nestE, astNestdEnum, codeFormatter );
                _e.nest( _nestE );
            }
            else if( astMember instanceof AnnotationDeclaration )
            {
                //System.out.println( "Annotation Declar" + astMember );
                AnnotationDeclaration nestAstAnnotationDecl = 
                    (AnnotationDeclaration)astMember;
                _annotationType _nestAt = new _annotationType(
                    _annotationType._signature.of( 
                        "@interface "+ nestAstAnnotationDecl.getName()) ); 
                _nestAt = _annotationTypeFromAST( 
                    _nestAt, nestAstAnnotationDecl, codeFormatter );
                //System.out.println( "NEST " + _nestAt );
                _e.nest( _nestAt );
            }
            else
            {
                System.err.println( "**** Unhandled AST Member " + astMember + System.lineSeparator() 
                    + " of " + astMember.getClass() );                              
            }
        }
        return _e;
    }    
}
