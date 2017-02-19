/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java.model;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.markup.bindml.BindML;
import varcode.java.model._Java._model;
import varcode.java.model._generic._typeParams;
import varcode.LoadException;
import varcode.context.Context;
import varcode.java.ast.JavaAst;
import varcode.ModelException;

/**
 * Meta Lang Model of a Java interface
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _interface
    extends _JavaFileModel
    implements _model
{
    public static final Template INTERFACE
        = BindML.compile(
            "{+package+}"
            + "{{+?imports:{+imports+}" + N + "+}}"
            + "{+javadoc+}"
            + "{+annotations+}"
            + "{+signature*+}" + N
            + "{" + N
            + "{{+?fields:{+$>(fields)+}" + N
            + "+}}"
            + "{{+?methods:{+$>(methods)+}" + N
            + "+}}"
            + "{{+?nests:{+$>(nests)+}" + N
            + "+}}"
            + "}" );

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    @Override
    public Context getContext()
    {       
        return VarContext.of(
            "package", this.pckage,
            "imports", this.getImports(),
            "javadoc", this.javadoc,
            "annotations", this.annotations,
            "signature", interfaceSignature,
            "fields", this.fields,
            "methods", this.methods,
            "nests", authorNests( this.nesteds ) );
    }

    public static _interface cloneOf( _interface prototype )
    {
        return new _interface( prototype );
    }

    public _interface add( _facet... facets )
    {
        for( int i = 0; i < facets.length; i++ )
        {
            add( facets[ i ] );
        }
        return this;
    }

    public _interface add( _facet facet )
    {
        if( facet instanceof _ann )
        {
            this.annotations.add( (_ann)facet );
            return this;
        }
        if( facet instanceof _field )
        {
            this.fields.add( (_field)facet );
            return this;
        }
        if( facet instanceof _method )
        {
            this.methods.add( (_method)facet );
            return this;
        }
        if( facet instanceof _modifiers._modifier )
        {
            this.getModifiers().set( (_modifiers._modifier)facet );
            return this;
        }
        throw new ModelException(
            "Unsupported facet " + facet + " for _class" );
    }

    public _modifiers getModifiers()
    {
        return this.interfaceSignature.modifiers;
    }

    private static class InterfaceParams
    {
        _package pack;
        _imports imports = new _imports(); // "import Java.lang", Class
        _javadoc javadoc; // starts with /* ends with */
        _anns annots = new _anns(); //starts with @
        _interface._signature signature; //starts with 
        List<_facet> facets = new ArrayList<_facet>();
        _nests nesteds = new _nests();
    }

    public static _interface of( Object... components )
    {
        InterfaceParams ip = new InterfaceParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString( ip, (String)components[ i ] );
            }
            else if( components[ i ] instanceof Class )
            {
                if( ((Class)components[ i ]).isAnnotation() )
                {
                    ip.annots.add( ((Class)components[ i ]).getCanonicalName() );
                    ip.imports.addImport( components[ i ] );
                }
                else
                {
                    ip.imports.addImport( components[ i ] );
                }
            }
            else if( components[ i ] instanceof _package )
            {
                ip.pack = (_package)components[ i ];
            }
            else if( components[ i ] instanceof _imports )
            {
                ip.imports = (_imports)components[ i ];
            }
            else if( components[ i ] instanceof _javadoc )
            {
                ip.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotations )
            {
                ip.annots = (_anns)components[ i ];
            }
            else if( components[ i ] instanceof _interface._signature )
            {
                ip.signature = (_interface._signature)components[ i ];
            }
            else if( components[ i ] instanceof _facet )
            {
                ip.facets.add( (_facet)components[ i ] );
            }
            else if( components[ i ] instanceof _model )
            {
                ip.nesteds.add( (_model)components[ i ] );
            }
        }
        _interface _i = new _interface( null, ip.signature );
        for( int i = 0; i < ip.annots.count(); i++ )
        {
            _i.annotate( ip.annots.getAt( i ) );
        }
        for( int i = 0; i < ip.imports.count(); i++ )
        {
            _i.imports( ip.imports.getImports().toArray( new Object[ 0 ] ) );
        }
        for( int i = 0; i < ip.facets.size(); i++ )
        {
            _i.add( ip.facets.get( i ) );
        }
        if( ip.javadoc != null && !ip.javadoc.isEmpty() )
        {
            _i.javadoc( ip.javadoc.getComment() );
        }
        if( ip.pack != null && !ip.pack.isEmpty() )
        {
            _i.packageName( ip.pack.getName() );
        }
        if( !ip.nesteds.isEmpty() )
        {
            for( int i = 0; i < ip.nesteds.count(); i++ )
            {
                _i.nest( ip.nesteds.getAt( i ) );
            }
        }
        return _i;
    }

    private static void fromString( InterfaceParams cd, String component )
    {
        if( component.startsWith( "/**" ) )
        {
            cd.javadoc = _javadoc.of( component.substring( 3, component.length() - 2 ) );
        }
        else if( component.startsWith( "/*" ) )
        {
            cd.javadoc = _javadoc.of( component.substring( 2, component.length() - 2 ) );
        }
        else if( component.startsWith( "package " ) )
        {
            cd.pack = _package.of( component );
        }
        else if( component.startsWith( "import " ) )
        {
            cd.imports.add( component.substring( "import ".length() ) );
        }
        else if( component.startsWith( "@" ) )
        {
            cd.annots.add( _ann.of( component ) );
        }
        else
        {
            String[] tokens = component.split( " " );
            if( tokens.length == 1 )
            {
                if( tokens[ 0 ].indexOf( "." ) > 0 )
                {
                    cd.pack = _package.of( tokens[ 0 ] );
                }
                else
                {
                    cd.signature = _interface._signature.of( component );
                }
            }
            else
            {
                cd.signature = _interface._signature.of( component );
            }
        }
    }

    private _javadoc javadoc;
    private _anns annotations;
    private _signature interfaceSignature;
    private _fields fields;
    private _methods methods;
    private _nests nesteds;

    @Override
    public _interface replace( String target, String replacement )
    {
        this.pckage.replace( target, replacement );
        this.annotations.replace( target, replacement );
        this.javadoc.replace( target, replacement );
        this.interfaceSignature.replace( target, replacement );
        this.fields.replace( target, replacement );
        this.methods.replace( target, replacement );
        this.imports.replace( target, replacement );
        this.nesteds.replace( target, replacement );

        return this;
    }

    /**
     * Create and return a mutable clone given the prototype
     *
     * @param prototype the prototype
     */
    public _interface( _interface prototype )
    {
        this.pckage = _package.cloneOf( prototype.pckage );
        this.annotations = _anns.cloneOf( prototype.annotations );
        this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.interfaceSignature = _signature.cloneOf( prototype.interfaceSignature );
        this.fields = _fields.cloneOf( prototype.fields );
        this.methods = _methods.cloneOf( prototype.methods );
        this.imports = _imports.cloneOf( prototype.imports );

        //NESTEDS
        this.nesteds = _nests.cloneOf( prototype.nesteds );
    }

    public _interface( String packageName, String interfaceSignature )
    {
        this( packageName, _signature.of( interfaceSignature ) );
    }

    public _interface( String packageName, _signature signature )
    {
        this.pckage = _package.of( packageName );
        this.annotations = new _anns();
        this.interfaceSignature = signature; //_signature.of( interfaceSignature );
        this.javadoc = new _javadoc();
        this.methods = new _methods();
        this.fields = new _fields();
        this.imports = new _imports();
        this.nesteds = new _nests();
    }

    public _interface packageName( String packageName )
    {
        this.pckage = _package.of( packageName );
        return this;
    }

    public _package getPackage()
    {
        return this.pckage;
    }

    @Override
    public String getName()
    {
        return this.interfaceSignature.getName();
    }
    
    public _interface setName( String name )
    {
        this.interfaceSignature.interfaceName = name;
        return this;
    }

    @Override
    public _anns getAnnotations()
    {
        return this.annotations;
    }

    @Override
    public _interface annotate( _anns annotations )
    {
        this.annotations = annotations;
        return this;
    }
        
    public _signature getSignature()
    {
        return this.interfaceSignature;
    }

    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }

    @Override
    public _fields getFields()
    {
        return this.fields;
    }

    @Override
    public _methods getMethods()
    {
        return this.methods;
    }

    @Override
    public _nests getNests()
    {
        return this.nesteds;
    }

    @Override
    public int getNestCount()
    {
        return this.nesteds.count();
    }

    public _model getNestedByName( String name )
    {
        return this.nesteds.getByName( name );
    }

    @Override
    public _model getNestAt( int index )
    {
        return this.nesteds.getAt( index );
    }

    public _interface importsStatic( Object... staticImports )
    {
        this.imports.addStaticImports( staticImports );
        return this;
    }

    public _interface imports( Object... imports )
    {
        this.imports.add( imports );
        return this;
    }

    public _interface setImports( _imports imports )
    {
        this.imports = imports;
        return this;
    }

    public _interface javadoc( _javadoc javadoc )
    {
        this.javadoc = javadoc;
        return this;
    }

    public _interface javadoc( String... javadoc )
    {
        this.javadoc = _javadoc.of( javadoc );
        return this;
    }

    public _interface method( Object...parts )
    {
        _method _m = _method.of( parts );
        return method( _m );
    }
   

    public _interface method( _method method )
    {
        _method._signature sig = method.getSignature();
        if( sig.getModifiers().contains( Modifier.PRIVATE ) )
        {
            throw new ModelException(
                "Cannot add a private method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {
            throw new ModelException(
                "Cannot add a final method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.PROTECTED ) )
        {
            throw new ModelException(
                "Cannot add a protected method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().containsAny(
            Modifier.NATIVE, Modifier.STRICT, Modifier.SYNCHRONIZED,
            Modifier.TRANSIENT, Modifier.VOLATILE ) )
        {
            throw new ModelException( "Invalid Modifiers for interface method " + N + sig );
        }        
        this.methods.add( method );
        return this;
    }

    /**
     * add one or more extensions for this interface to the signature and return
     * the modified _interface
     *
     * @param extendFrom one or more extends to add to the interface
     * @return the modified _interface
     */
    public _interface extend( String... extendFrom )
    {
        for( int i = 0; i < extendFrom.length; i++ )
        {
            this.interfaceSignature.extendsFrom.addExtends( extendFrom[ i ] );
        }
        return this;
    }

    /**
     * Add one or more annotations to the _interface and return the modified
     * _interface
     *
     * @param annotations String annotations to parse and add
     * @return the modified _interface
     */
    public _interface annotate( String... annotations )
    {
        this.annotations.add( annotations );
        return this;
    }

    public _interface annotate( _ann annotation )
    {
        this.annotations.add( annotation );
        return this;
    }

    /**
     * JDK8+ static method directly on interface
     * 
     * @param signature
     * @param linesOfCode
     * @return 
     */
    public _interface staticMethod( String signature, Object... linesOfCode )
    {
        _method method = _method.of( (_javadoc)null, signature, linesOfCode );
        _methods._method._signature sig = method.getSignature();
        if( !sig.getModifiers().contains( Modifier.STATIC ) )
        {
            sig.getModifiers().set( "static" );
        }
        if( sig.getModifiers().contains( Modifier.PRIVATE ) )
        {
            throw new ModelException(
                "Cannot add a private method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {
            throw new ModelException(
                "Cannot add a final method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.PROTECTED ) )
        {
            throw new ModelException(
                "Cannot add a protected method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().containsAny(
            Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE ) )
        {
            throw new ModelException(
                "Invalid Modifiers for interface method " + N + sig );
        }
        this.methods.add( method );
        return this;
    }

    public _interface defaultMethod( String signature, Object... linesOfCode )
    {
        _method method = _method.of( (_javadoc)null, signature, linesOfCode );

        _methods._method._signature sig = method.getSignature();
        if( !sig.getModifiers().contains(
            _modifiers._modifier.INTERFACE_DEFAULT.getBitValue() ) )
        {
            sig.getModifiers().set( "default" );
        }
        if( sig.getModifiers().contains( Modifier.PRIVATE ) )
        {
            throw new ModelException(
                "Cannot add a private method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {
            throw new ModelException(
                "Cannot add a final method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().contains( Modifier.PROTECTED ) )
        {
            throw new ModelException(
                "Cannot add a protected method " + N + sig + N + " to an interface " );
        }
        if( sig.getModifiers().containsAny(
            Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE ) )
        {
            throw new ModelException(
                "Invalid Modifiers for interface method " + N + sig );
        }
        this.methods.add( method );
        return this;
    }

    public _field getFieldByName( String fieldName )
    {
        return this.fields.getByName( fieldName );
    }
    
    public _interface fields( String... fields )
    {
        for( int i = 0; i < fields.length; i++ )
        {
            this.fields.add( _fields._field.of( fields[ i ] ) );
        }
        return this;
    }
    
    public _interface fields( _fields _fs )
    {
        for( int i = 0; i < _fs.count(); i++ )
        {
            field( _fs.getAt( i ) );
        }
        return this;
    }

    public _interface field( String comment, String fieldSignature )
    {
        _fields._field m = _fields._field.of( fieldSignature );

        if( !m.hasInit() )
        {
            throw new ModelException( "Field : " + N + m + N
                + " has not been initialized for interface " );
        }
        m.javadoc( comment );
        fields.add( m );
        return this;
    }

    public _interface field( _field field )
    {
        //this.fields.add( field );
        if( field.getInit() == null || field.getInit().getCode().trim().length() == 0 )
        {
            throw new ModelException( "Field : " + N + field + N
                + " has not been initialized for interface " );
        }
        fields.add( field );
        return this;
    }

    public _interface field( Object...parts )
    {
        return field( _field.of( parts ) );
    }

    public _interface nest( _model...models )
    {
        this.nesteds.add( models );
        return this;
    }
    
    public _interface nest( _model component )
    {
        this.nesteds.add( component );
        return this;
    }

    /**
     * interface signature i.e.
     * 
     * "public interface MyInterface<K> extends Serializable, Marker"
     */
    public static class _signature
        implements _Java, Authored
    {
        private _modifiers modifiers = new _modifiers();
        private String interfaceName;
        private _typeParams genericTypeParams = new _typeParams();
        private _extends extendsFrom = new _extends();

        public static final Template INTERFACE_SIGNATURE
            = BindML.compile(
                "{+modifiers+}interface {+interfaceName*+}{+genericTypeParams+}{+extends+}" );

        public _signature( String name )
        {
            this.interfaceName = name;
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        public _typeParams getGenericTypeParams()
        {
            return this.genericTypeParams;
        }

        public _signature setGenericTypeParams( _typeParams genericTypeParams )
        {
            this.genericTypeParams = genericTypeParams;
            return this;
        }

        @Override
        public Context getContext()
        {
            return VarContext.of(
                "modifiers", modifiers,
                "interfaceName", interfaceName,
                "genericTypeParams", genericTypeParams,
                "extends", extendsFrom );
        }
        
        @Override
        public Template getTemplate()
        {
            return INTERFACE_SIGNATURE;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( INTERFACE_SIGNATURE,
                getContext(),
                directives );
        }

        public _signature( _signature prototype )
        {
            this.interfaceName = prototype.interfaceName;
            this.modifiers = _modifiers.cloneOf( prototype.modifiers );
            this.genericTypeParams = _typeParams.cloneOf( prototype.getGenericTypeParams() );
            this.extendsFrom = _extends.cloneOf( prototype.extendsFrom );
        }
        
        public static _signature cloneOf( _signature prototype )
        {
            return new _signature( prototype );            
        }

        @Override
        public _signature replace( String target, String replacement )
        {
            this.interfaceName = this.interfaceName.replace( target, replacement );
            this.extendsFrom.replace( target, replacement );
            this.genericTypeParams.replace( target, replacement );
            this.modifiers.replace( target, replacement );
            return this;
        }

        public String getName()
        {
            return this.interfaceName;
        }

        @Override
        public String toString()
        {
            return author();
        }

        public _extends getExtends()
        {
            return extendsFrom;
        }

        public _signature setModifiers( _modifiers modifiers )
        {
            this.modifiers = modifiers;
            return this;
        }

        public _signature setModifiers( int... modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }

        public _signature setExtends( String... extendsFrom )
        {
            this.extendsFrom = _extends.of( extendsFrom );
            return this;
        }

        public _signature extend( Class extendsFrom )
        {
            this.extendsFrom.addExtends( extendsFrom );
            return this;
        }

        public _signature extend( String extendsFrom )
        {
            this.extendsFrom.addExtends( extendsFrom );
            return this;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( 
                this.extendsFrom, this.genericTypeParams, 
                this.interfaceName, this.modifiers);
        }

        @Override
        public boolean equals( Object obj )
        {
            if( this == obj )
            {
                return true;
            }
            if( obj == null )
            {
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                return false;
            }
            final _signature other = (_signature)obj;
            if( !Objects.equals( this.interfaceName, other.interfaceName ) )
            {
                return false;
            }
            if( !Objects.equals( this.modifiers, other.modifiers ) )
            {
                return false;
            }
            if( !Objects.equals( this.genericTypeParams, other.genericTypeParams ) )
            {
                return false;
            }
            if( !Objects.equals( this.extendsFrom, other.extendsFrom ) )
            {
                return false;
            }
            return true;
        }
        
        public _signature setName( String name )
        {
            this.interfaceName = name;
            return this;
        }

        /**
         * Builds the appropriate _interface._signature given the Text of the
         * signature
         *
         * (Internally uses the AST Parser and "tacks-on" an empty body"{}" to
         * parse into components.)
         *
         * @param iSig the text for the interface signature
         * @return the _interface._signature meta model
         */
        public static _interface._signature of( String iSig )
            throws ModelException
        {
            if( !iSig.contains( "interface" ) )
            {
                iSig = "public interface " + iSig;
            }
            try
            {   // Since we already HAVE an AST parser, lets use it to parse the
                // String signature (not roll our own) just add "{}" to
                // the signature (an empty class body)
                ClassOrInterfaceDeclaration coid = new ClassOrInterfaceDeclaration();
                CompilationUnit astRoot = 
                    JavaAst.astFrom( iSig +"{}" );
                ClassOrInterfaceDeclaration astCoid
                    = (ClassOrInterfaceDeclaration)astRoot.getTypes().get( 0 );
                List<ClassOrInterfaceType> astExtends = astCoid.getExtends();
                //List<ClassOrInterfaceType> astImpls = astCoid.getImplements();
                List<TypeParameter> astTypeParams = astCoid.getTypeParameters();

                String name = astCoid.getName();
                _modifiers _mods = _modifiers.of( astCoid.getModifiers() );

                if( _mods.containsAny(
                    Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.NATIVE,
                    Modifier.TRANSIENT, Modifier.VOLATILE, Modifier.STRICT ) )
                {
                    throw new ModelException(
                        "interface Signature \"" + iSig
                        + "\" contains invalid modifiers" );
                }

                
                _interface._signature _is = new _interface._signature( name );
                _is.setModifiers( _mods );
                if( astTypeParams.size() > 0 )
                {
                    _typeParams _tp = new _typeParams();
                    for( int i = 0; i < astTypeParams.size(); i++ )
                    {
                        _tp.addParam( astTypeParams.get( i ).toString() );
                    }
                    _is.setGenericTypeParams( _tp );
                }
                if( astExtends.size() > 0 )
                {
                    for( int i = 0; i < astExtends.size(); i++ )
                    {
                        _is.extend( astExtends.get( i ).toString() );
                    }
                }
                //System.out.println( _is );
                return _is;
            }
            catch( ParseException pe )
            {
                throw new ModelException(
                    "Parsing error for  Class Signature \"" + iSig + "\"", pe );
            }
            catch( LoadException le )
            {
                throw new ModelException(
                    "Error parsing Class Signature \"" + iSig + "\"" , le );
            }
        }
    }
    @Override
    public int hashCode()
    {
        return Objects.hash( javadoc, this.annotations, this.fields, 
            this.imports, this.pckage, this.interfaceSignature, 
            this.methods, this.nesteds );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            return true;
        }
        if( obj == null )
        {
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            return false;
        }
        final _interface other = (_interface)obj;
        if( !Objects.equals( this.pckage, other.pckage ) )
        {
            return false;
        }
        if( !Objects.equals( this.javadoc, other.javadoc ) )
        {
            return false;
        }
        if( !Objects.equals( this.annotations, other.annotations ) )
        {
            return false;
        }
        if( !Objects.equals( this.interfaceSignature, other.interfaceSignature ) )
        {
            return false;
        }
        if( !Objects.equals( this.fields, other.fields ) )
        {
            return false;
        }
        if( !Objects.equals( this.methods, other.methods ) )
        {
            return false;
        }
        if( !Objects.equals( this.imports, other.imports ) )
        {
            return false;
        }
        if( !Objects.equals( this.nesteds, other.nesteds ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( INTERFACE, getContext(), directives );
    }
    
    @Override
    public String toString()
    {
        return author();
    }

    @Override
    public Template getTemplate()
    {
        return INTERFACE;
    }
}
