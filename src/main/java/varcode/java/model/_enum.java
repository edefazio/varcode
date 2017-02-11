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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum._constants._constant;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.markup.bindml.BindML;
import varcode.java.model._Java._model;
import varcode.java.naming.RefRenamer;
import varcode.java.model._JavaFileModel;
import varcode.ModelException;

/**
 * Java Model for an enum 
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _enum
    extends _JavaFileModel
    implements _model
{
    private _javadoc javadoc = new _javadoc();
    private _annotations annotations = new _annotations();
    private _signature signature;
    private _constructors constructors = new _constructors();
    private _staticBlock staticBlock = new _staticBlock( (Object[])null );
    private _fields fields = new _fields();
    private _constants constants = new _constants();
    private _methods methods = new _methods();
    private _nests nests = new _nests();

    public static final Template ENUM
        = BindML.compile(
            "{+package+}"
            + "{{+?imports:{+imports+}" + N + "+}}"
            + "{+javadoc+}"
            + "{+annotations+}"
            + "{+signature*+}" + N
            + "{" + N
            + "{+constants+}" + N
            + "{{+?fields:{+$>(fields)+}" + N
            + "+}}"
            + "{{+?constructors:{+$>(constructors)+}" + N
            + "+}}"
            + "{{+?methods:{+$>(methods)+}" + N
            + "+}}"
            + "{{+?nests:{+$>(nests)+}" + N 
            + "+}}" 
            + "{{+?staticBlock:{+$>(staticBlock)+}" + N
            + "+}}"
            + "}" );

    /** Calls the copy-constructor to create a DEEP clone 
     * of this enum and return it*/
    public static _enum cloneOf( _enum prototype )
    {
        return new _enum( prototype );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _enum add( _facet... facets )
    {
        for( int i = 0; i < facets.length; i++ )
        {
            add( facets[ i ] );
        }
        return this;
    }

    public _enum add( _facet facet )
    {
        if( facet instanceof _annotations._annotation )
        {
            this.annotations.add( facet );
            return this;
        }
        if( facet instanceof _constructor )
        {
            this.constructors.addConstructor( (_constructor)facet );
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
            "Unsupported facet " + facet + " for _enum" );
    }

    public _modifiers getModifiers()
    {
        return this.getSignature().getModifiers();
    }

    /** 
     * Copy constructor 
     * @param prototype the prototype this enum will be created from
     */
    public _enum( _enum prototype )
    {
        this.pckage = _package.cloneOf( prototype.pckage );
        this.imports = _imports.cloneOf( prototype.imports );
        this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.annotations = _annotations.cloneOf( prototype.annotations );
        this.signature = _signature.cloneOf( prototype.signature );
        this.constructors = _constructors.cloneOf( prototype.constructors );
        this.staticBlock = _staticBlock.cloneOf( prototype.staticBlock );
        this.fields = _fields.cloneOf( prototype.fields );
        this.constants = _constants.cloneOf( prototype.constants );
        this.methods = _methods.cloneOf( prototype.methods );
        this.nests = _nests.cloneOf( prototype.nests );
    }

    /**
     * sets the name of the class, (and the constructors) and return
     * @param name the new name
     * @return the modified _enum
     */
    public _enum setName( String name )
    {
        String oldName = this.getName();
        this.replace( oldName, name );
        return this;
    }

    @Override
    public String getName()
    {
        return this.signature.getName();
    }

    public String getPackageName()
    {
        if( this.pckage.isEmpty() )
        {
            return "";
        }
        return this.pckage.getName();
    }

    @Override
    public _annotations getAnnotations()
    {
        return this.annotations;
    }

    @Override
    public Context getContext()
    {   
        return VarContext.of(
            "package", this.pckage,            
            "imports",  this.getImports(),
            "javadoc", javadoc,
            "annotations", this.annotations,
            "signature", signature,
            "constructors", this.constructors,
            "staticBlock", staticBlock,
            "fields", this.fields,
            "constants", constants,
            "nests", authorNests( this.nests ),
            "methods", this.methods );
    }

    public _enum implement( Class... implementClass )
    {
        this.signature.implementsFrom.implement( implementClass );
        return this;
    }

    public _enum implement( String... implementClass )
    {
        this.signature.implementsFrom.implement( implementClass );
        return this;
    }
    
    public _package getPackage()
    {
        return this.pckage;
    }

    @Override
    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }

    public _signature getSignature()
    {
        return this.signature;
    }

    public _constructors getConstructors()
    {
        return this.constructors;
    }

    public _staticBlock getStaticBlock()
    {
        return this.staticBlock;
    }

    @Override
    public _fields getFields()
    {
        return this.fields;
    }

    public _constants getConstants()
    {
        return this.constants;
    }

    @Override
    public _methods getMethods()
    {
        return this.methods;
    }

    @Override
    public _nests getNests()
    {
        return this.nests;
    }

    @Override
    public int getNestCount()
    {
        return this.nests.count();
    }

    public _model getNestedByName( String name )
    {
        return this.nests.getByName( name );
    }

    @Override
    public _model getNestAt( int index )
    {
        return this.nests.getAt( index );
    }



    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( ENUM, getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }

    private static class EnumParams
    {
        _package pack;
        /* package io.varcode....*/
        _imports imports = new _imports(); // "import Java.lang", Class
        _javadoc javadoc; // starts with /* ends with */
        _annotations annots = new _annotations(); //starts with @
        _enum._signature signature; //starts with 
        List<_facet> facets = new ArrayList<_facet>();
        _nests nesteds = new _nests();
    }

    public static _enum of( Object... components )
    {
        EnumParams eParams = new EnumParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString( eParams, (String)components[ i ] );
            }
            else if( components[ i ] instanceof Class )
            {
                if( ((Class)components[ i ]).isAnnotation() )
                {
                    eParams.annots.add( components[ i ] );
                    eParams.imports.addImport( components[ i ] );
                }
                else
                {
                    eParams.imports.addImport( components[ i ] );
                }
            }
            else if( components[ i ] instanceof _package )
            {
                eParams.pack = (_package)components[ i ];
            }
            else if( components[ i ] instanceof _imports )
            {
                eParams.imports = (_imports)components[ i ];
            }
            else if( components[ i ] instanceof _javadoc )
            {
                eParams.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _annotations )
            {
                eParams.annots = (_annotations)components[ i ];
            }
            else if( components[ i ] instanceof _class._signature )
            {
                eParams.signature = (_enum._signature)components[ i ];
            }
            else if( components[ i ] instanceof _facet )
            {
                eParams.facets.add( (_facet)components[ i ] );
            }
            else if( components[ i ] instanceof _model )
            {
                eParams.nesteds.add( (_model)components[ i ] );
            }

        }
        _enum _e = new _enum( eParams.signature );
        for( int i = 0; i < eParams.annots.count(); i++ )
        {
            _e.annotate( eParams.annots.getAt( i ) );
        }
        for( int i = 0; i < eParams.imports.count(); i++ )
        {
            _e.imports( eParams.imports.getImports().toArray( new Object[ 0 ] ) );
        }
        for( int i = 0; i < eParams.facets.size(); i++ )
        {
            _e.add( eParams.facets.get( i ) );
        }
        if( eParams.javadoc != null && !eParams.javadoc.isEmpty() )
        {
            _e.javadoc( eParams.javadoc.getComment() );
        }
        if( eParams.pack != null && !eParams.pack.isEmpty() )
        {
            _e.packageName( eParams.pack.getName() );
        }
        if( !eParams.nesteds.isEmpty() )
        {
            for( int i = 0; i < eParams.nesteds.count(); i++ )
            {
                _e.nest( eParams.nesteds.getAt( i ) );
            }
        }
        return _e;
    }

    private static void fromString( EnumParams cd, String component )
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
        else if( component.startsWith("import " ) )
        {
            cd.imports.add( component.substring( "import ".length() ) );
        }
        else if( component.startsWith( "@" ) )
        {
            cd.annots.add( _annotations._annotation.of( component ) );
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
                    cd.signature = _enum._signature.of( component );
                }
            }
            else
            {
                cd.signature = _enum._signature.of( component );
            }
        }
    }

    public _enum( _signature signature )
    {
        this.signature = signature;
    }

    public _enum(
        _package enumPackage,
        _imports imports,
        _javadoc javadocComment,
        _signature signature,
        _fields fields,
        _staticBlock staticBlock,
        _methods methods,
        _nests nested )
    {
        this.pckage = enumPackage;
        this.imports = imports;
        this.javadoc = javadocComment;
        this.signature = signature;
        this.staticBlock = staticBlock;
        this.fields = fields;
        this.methods = methods;
        this.nests = nested;
    }

    public _enum packageName( String packageName )
    {
        return packageName( _package.of( packageName ) );
    }

    public _enum packageName( _package packageImpl )
    {
        this.pckage = packageImpl;
        return this;
    }

    /**
     * <PRE>{@code
     * _class MyAClass = new _class("public A")
     *     .addConstructor("public A( String name )", "this.name = name;");}</PRE>
     *
     * @param constructorSignature
     * @param body
     * @return
     
    public _enum constructor( String constructorSignature, Object... body )
    {
        _constructors._constructor c
            = new _constructors._constructor( constructorSignature )
                .body( body );
        if( c.getSignature().getModifiers().containsAny(
            Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new ModelException(
                "Enum constructors cannot be public or protected" );
        }
        return constructor( c );
    }
    */
    
    public _enum constructor( Object... parts )
    {
        _constructor _ctor = _constructor.of( parts );
        return constructor( _ctor );
    }
    
    public _enum constructor( _constructor _ctor )
    {
        constructors.addConstructor( _ctor );
        if( _ctor.getSignature().getModifiers().containsAny(
            Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new ModelException(
                "Enum constructors cannot be public or protected" );
        }
        return this;
    }

    public _enum nest( _model component )
    {
        this.nests.add( component );
        return this;
    }
    
    public _enum nest( _model... models )
    {
        this.nests.add( models );
        return this;
    }

    /*
    public _enum method( String methodSignature, String... bodyLines )
    {
        return method( _method.of( (_javadoc)null, methodSignature, (Object[])bodyLines ) );
    }

    */
    
    public _enum method( Object...parts )
    {
        return method( _method.of( parts ) );
    }
    
    public _enum method( _method m )
    {
        if( m.isAbstract() )
        {
            throw new ModelException(
                "Cannot add an abstract method " + N + m + N + " to an enum " );
        }
        this.methods.add( m );
        return this;
    }

    /**
     * Builds and adds a main method to this _enum (with the bodyLines) and
     * atReturn the modified _enum
     *
     * @param bodyLines
     * @return the modified _class
     */
    public _enum mainMethod( Object... bodyLines )
    {
        //Object[] method = new Object[]
        if( bodyLines.length > 0 )
        {
            if( bodyLines[ 0 ] instanceof _javadoc )
            {
                Object[] lines = new Object[ bodyLines.length - 1 ];
                System.arraycopy( bodyLines, 1, lines, 0, bodyLines.length - 1 );

                return method( _method.of(
                    (_javadoc)bodyLines[ 0 ],
                    "public static void main( String[] args )",
                    lines ) );
            }
        }
        return method( _method.of(
            (_javadoc)null,
            "public static void main( String[] args )",
            bodyLines ) );
    }
    
    public _enum importsStatic( Object... imports )
    {
        this.imports.addStaticImports( imports );
        return this;
    }

    public _enum setImports( _imports imports )
    {
        this.imports = imports;
        return this;
    }

    public _enum imports( Object... imports )
    {
        this.imports.add( imports );
        return this;
    }

    /*
    @Override
    public _imports getImports()
    {
        for( int i = 0; i < nesteds.count(); i++ )
        {
            this.imports.mergeBindings(
                nesteds.models.get( i ).getImports() );
        }
        return this.imports;
    }
    */

    public _enum annotate( Object... annotations )
    {
        if( this.annotations == null )
        {
            this.annotations = new _annotations();
        }
        this.annotations.add( annotations );
        return this;
    }

    /**
     * Authors the enum, compiles and returns all of the Enum constants 
     * defined by the _enum
     * 
     * NOTE: this is "expensive" (It will author and COMPILE EACH TIME IT IS 
     * CALLED) for Normal use, load the class once and manually get the
     * constants
     * 
    
     * @return  the enum constants defined by the enum
     
    public Object[] loadConstants()
    {
        Class clazz = loadClass();
        return clazz.getEnumConstants();
    }
    */
    
    /**
     * 
     * @param javadoc
     * @return 
     */
    public _enum javadoc( _javadoc javadoc )
    {
        this.javadoc = javadoc;
        return this;
    }

    public _enum javadoc( String... comment )
    {
        this.javadoc = new _javadoc( comment );
        return this;
    }

    public _enum constant( String name, Object... arguments )
    {
        return constant( _constant.of( name, arguments ) );
    }

    public _enum constant( _constant constants )
    {
        this.constants.addConstant( constants );
        return this;
    }

    public _enum constants( _constants constants )
    {
        for( int i = 0; i < constants.count(); i++ )
        {
            this.constant( constants.getAt( i ) );
        }
        return this;
    }

    public _enum field( _field field )
    {
        fields.add( field );
        return this;
    }

    public _enum field( _modifiers mods, String type, String name )
    {
        _field f = new _field( mods, type, name );
        return field( f );
    }

    /*
    public _enum field( int mods, String type, String name )
    {
        _field f = new _field( mods, type, name );
        return field( f );
    }
    */

    public _enum field( int modifiers, String type, String name, String init )
    {
        return field( new _field( modifiers, type, name, init ) );
    }

    public _field getFieldByName( String fieldName )
    {
        return this.fields.getByName( fieldName );
    }
    
    public _enum property( Object...parts )
    {
        return property( _field.of( parts ) );
    }
    
    public _enum property( _field _f )
    {
        field( _f );
        String name = _f.getName();
        String nameCaps
            = Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
        //add a get method
        return method( 
            "public " + _f.getType() + " get" + nameCaps + "()",
            "return this." + name + ";" );        
    }
    
    /**
     * adds a field to the enum<PRE>
     * myEnum.field( "public final int value;" );
     * myEnum.field( "private String value = \"Butler\";" );
     * </PRE>
     *
     * @param parts the parts (javadoc, Annotations, declaration) of the field
     * @return this (modified)
     */
    public _enum field( Object... parts )
    {
        fields.add( _fields._field.of( parts ) );
        return this;
    }

    public _enum staticBlock( Object... staticBlockCode )
    {
        if( this.staticBlock == null )
        {
            this.staticBlock = new _staticBlock();
        }
        this.staticBlock.addTailCode( staticBlockCode );
        return this;
    }

    public _enum fields( _fields fields )
    {
        for( int i = 0; i < fields.count(); i++ )
        {
            field( fields.getAt( i ) );
        }
        return this;
    }

    public _enum fields( String... fields )
    {
        for( int i = 0; i < fields.length; i++ )
        {
            this.fields.add( _fields._field.of( fields[ i ] ) );
        }
        return this;
    }

    public _enum fields( _fields._field... fields )
    {
        this.fields.add( fields );
        return this;
    }

    public _enum setModifiers( _modifiers mods )
    {
        if( this.signature == null )
        {
            System.out.println( "WE got real problems" );
        }
        if( this.signature.modifiers == null )
        {
            this.signature.modifiers = new _modifiers();
        }
        this.signature.modifiers = mods;
        return this;
    }

    /**
     * When we construct enums each enum value
     *
     * must call a constructor so for example:
     * <PRE>
     * enum Octal
     * {
     *    _0(), _1(), _2(), _3(), _4(), _5(), _6(), _7();
     * }
     * </PRE>
     *
     * you might also call the constructors with parameters
     *
     * <PRE>
     * enum Octal
     * {
     *    _0(0, "zero"), _1(1, "one"), _2(2, "two"), _3(3, "three"),
     *    _4(4, "four"), _5(5, "five"), _6(6, "six"), _7(7, "seven");
     * }
     * </PRE> this abstractions contains the List and verifies that
     *
     */
    public static class _constants
        implements _Java, 
        _Java.Countable,
        _Java.Authored
    {
        /**
         * TODO, cant I just iterate through each time w/o having to keep this
         * around??
         */
        private List<_constant> constants
            = new ArrayList<_constant>();

        public _constants()
        {            
        }
        
        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }

        public final _constants addConstant( _constant constant )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                if( this.constants.get( i ).name.equals( constant.name ) )
                {
                    throw new ModelException(
                        "Enum already contains a constant with name \"" + constant.name + "\"" );
                }
            }
            this.constants.add( constant );
            return this;
        }

        public _constants( _constants prototype )
        {
            for(int i=0; i< prototype.count(); i++ )
            {
                addConstant(_constant.cloneOf( prototype.constants.get( i ) ) );
            }
        }
        
        //the form is always
        // Identifier1( parameters, separated, by commas ), 
        // Identifier2( parameters, separated, by commas ),  
        //   -or-
        // Ideitifier1,
        // Ideitifier2,
        //NOTE : must be unique names
        public static _constants cloneOf( _constants prototype )
        {
            return new _constants( prototype );
            
        }

        public _constant getByName( String name )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                if( this.constants.get( i ).name.equals( name ) )
                {
                    return this.constants.get( i );
                }
            }
            return null;
        }

        public _constant getAt( int index )
        {
            if( index < count() )
            {
                return this.constants.get( index );
            }
            throw new ModelException(
                "Invalid value construct index [" + index + "]" );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( constants );
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
            final _constants other = (_constants)obj;
            if( !Objects.equals( this.constants, other.constants ) )
            {
                return false;
            }
            return true;
        }
    
        @Override
        public _constants replace( String target, String replacement )
        {
            for( int i = 0; i < this.constants.size(); i++ )
            {
                this.constants.get( i ).replace( target, replacement );
            }
            return this;
        }

        /**
         * Individual constants within enums can define their own constantBody:
 
 enum Rochambo
 {
     ROCK("theRock") //this constant has it's own "constantBody"
     {
          public int count = 100;
         *          
         *          @Override
         *          public String toString()
         *          {
         *              return name + count;
         *          }
         *     },
         *     PAPER("thePaper"),
         *     SCISSORS("theScissors");
         *     
         *     private String name;
         * 
         *     private Rochambo( String name )
         *     {
         *         this.name = name;
         *     }
         * }
         */
        public static class _constantBody
            implements _Java, Authored
        {
            private _methods methods = new _methods();
            private _fields fields = new _fields();
            
            /** If there are no fields or methods, print blank  (no {} braces)*/
            public static final Template EMPTY = BindML.compile( "" );
            
            public static final Template CONSTANT_BODY = BindML.compile( N +
                "    {" + N + 
                "{{+?fields:{+$>>(fields)+}" + N + "+}}" +    
                "{{+?methods:{+$>>(methods)+}" + N + "+}}" +
                "    }");
            
            public Template getTemplate()
            {
                if( methods.count() + fields.count() == 0 )
                {
                    return EMPTY;
                }
                return CONSTANT_BODY;
            }
            
            private _constantBody()
            {
                //used interally for building
            }
            
            public _constantBody( _constantBody prototype )
            {
                methods = _methods.cloneOf( prototype.methods );
                fields = _fields.cloneOf( prototype.fields );
            }
            public static _constantBody cloneOf( _constantBody prototype )
            {
                return new _constantBody( prototype );
            }
            
            @Override
            public int hashCode()
            {
                return Objects.hash( methods, fields );
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
                final _constantBody other = (_constantBody)obj;
                if( !Objects.equals( this.methods, other.methods ) )
                {
                    return false;
                }
                if( !Objects.equals( this.fields, other.fields ) )
                {
                    return false;
                }
                return true;
            }
            
            public Context getContext()
            {
                return VarContext.of( "methods", methods, "fields", fields );
            }

            @Override
            public String author()
            {
                return author( new Directive[ 0 ] );
            }

            @Override
            public String author( Directive... directives )
            {
                return Author.toString( getTemplate(), getContext(), directives );
            }

            @Override
            public String toString()
            {
                return author();
            }
            
            @Override
            public _constantBody replace( String target, String replacement )
            {
                this.methods = this.methods.replace( target, replacement );
                this.fields = this.fields.replace( target, replacement );
                return this;
            }

            @Override
            public void visit( ModelVisitor visitor )
            {
                visitor.visit( this );
            }
        }
        
        /**
         * Individual Enum value const constructor
         */
        public static class _constant
            implements _Java, Authored
        {
            private String name;
            private _args args;
            
            private _constantBody constantBody = new _constantBody();
            
            /**
             * So:
             * <PRE>
             * ERIC( 42, "Michael" ),
             * ^^^^  ^^^^^^^^^^^^^
             * name    arguments
             *
             * ERIC,
             * ^^^^
             * name
             * </PRE>
             *
             * @param name the name iof the enumValue
             * @param arguments the arguments passed into the enum constructor
             * @return the valueConstruct with name and arguments
             */
            public static _constant of( String name, Object... arguments )
            {
                return new _constant( name, _args.of( arguments ) );
            }

            public _constant method( _method method )
            {
                this.constantBody.methods.add( method );
                return this;
            }
            
            public _constant method( Object...methodParts )
            {
                this.constantBody.methods.add( _method.of( methodParts ) );
                return this;
            }
            
            public _constant methods( _method... _ms )
            {
                this.constantBody.methods.add( _ms );
                return this;
            }
            
            public _constant fields( _fields _fs )
            {
                this.constantBody.fields.add( _fs );
                return this;
            }
            
            public _constant fields( String...fields )
            {
                this.constantBody.fields.add( fields );
                return this;
            }
            
            public _constant fields( _field... _fs )
            {
                this.constantBody.fields.add( _fs );
                return this;
            }
            
            @Override
            public void visit( ModelVisitor visitor )
            {
                visitor.visit(this);
            }

            public String getName()
            {
                return this.name;
            }

            public _args getArguments()
            {
                return this.args;
            }

            public _methods getMethods()
            {
                return this.constantBody.methods;
            }
            
            public _fields getFields()
            {
                return this.constantBody.fields;
            }
            
            public _constant constantBody( _constantBody constantBody )
            {
                this.constantBody = constantBody;
                return this;
            }
            
            public _constant(_constant prototype )
            {
                this.args = _args.cloneOf( prototype.args );
                this.constantBody = _constantBody.cloneOf( prototype.constantBody );
                this.name = prototype.name;
            }
            
            public static _constant cloneOf( _constant prototype )
            {
                return new _constant( prototype );                              
            }

            public _constant( String name, _args args )
            {
                this.name = name;
                this.args = args;
            }

            public Template CONST_CONSTRUCT = BindML.compile(
                "{+name*+}{+args+}{+constantBody+}" );

            @Override
            public int hashCode()
            {
                return Objects.hash( name, args, constantBody );
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
                final _constant other = (_constant)obj;
                if( !Objects.equals( this.name, other.name ) )
                {
                    return false;
                }
                if( !Objects.equals( this.args, other.args ) )
                {
                    return false;
                }
                if( ! Objects.equals( this.constantBody, other.constantBody ) )
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
                return Author.toString( CONST_CONSTRUCT, getContext(), directives );
            }

            @Override
            public Template getTemplate()
            {
                return CONST_CONSTRUCT;
            }
            
            @Override
            public Context getContext()
            {
                VarContext vc = VarContext.of( "name", name );
                if( args != null && args.count() > 0 )
                {
                    vc.set( "args", args );
                }
                vc.set( "constantBody", this.constantBody );
                return vc;
            }
            
            @Override
            public String toString()
            {
                return author();
            }

            @Override
            public _constant replace( String target, String replacement )
            {
                this.args = this.args.replace( target, replacement );
                this.name = 
                    RefRenamer.apply( this.name, target, replacement );
                this.constantBody = 
                    this.constantBody.replace( target, replacement );
                return this;
            }
        }

        public Template CONST_CONSTRUCTORS = BindML.compile(
            "{{+:    {+constConstructs+}," + N + "+}};" + N + N );

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public Context getContext()
        {
            return  VarContext.of( "constConstructs", constants );
        }
        
        //@Override
        public Template getTemplate()
        {
            return CONST_CONSTRUCTORS;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( CONST_CONSTRUCTORS,
                getContext(),
                directives );
        }

        //@Override
        public int count()
        {
            return this.constants.size();
        }
        
        //@Override
        public boolean isEmpty()
        {
            return count() == 0;
        }
        

        @Override
        public String toString()
        {
            return author();
        }
    }

    
    /**
     * Enum Constructor signature
     */
    public static class _signature
        implements _Java, Authored
    {
        private String enumName = "";
        private _implements implementsFrom = new _implements();
        private _modifiers modifiers = new _modifiers();

        public static final Template ENUM_SIGNATURE
            = BindML.compile( "{+modifiers+}enum {+enumName*+}{+implements+}" );

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( enumName, implementsFrom, modifiers );
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
            if( !Objects.equals( this.enumName, other.enumName ) )
            {
                return false;
            }
            if( !Objects.equals( this.implementsFrom, other.implementsFrom ) )
            {
                return false;
            }
            if( !Objects.equals( this.modifiers, other.modifiers ) )
            {
                return false;
            }
            return true;
        }
    
        @Override
        public Context getContext()
        {
            return VarContext.of(
                "enumName", enumName,
                "modifiers", modifiers,
                "implements", implementsFrom );
        }
        
        @Override
        public Template getTemplate()
        {
            return ENUM_SIGNATURE;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( ENUM_SIGNATURE,
                getContext(),
                directives );
        }

        private _signature()
        {
            //used for building internally 
        }
        public _signature( _signature prototype )
        {
            enumName = prototype.enumName + "";
            implementsFrom = _implements.cloneOf( prototype.implementsFrom );
            modifiers = _modifiers.cloneOf( prototype.modifiers );
            
        }
        public static _signature cloneOf( _signature prototype )
        {
            return new _signature( prototype );            
        }

        @Override
        public String toString()
        {
            return author();
        }

        @Override
        public _signature replace( String target, String replacement )
        {
            this.enumName = 
                RefRenamer.apply( this.enumName, target, replacement);
                //this.enumName.replace( target, replacement );
            this.implementsFrom = this.implementsFrom.replace( target, replacement );
            this.modifiers = this.modifiers.replace( target, replacement );
            return this;
        }

        public _modifiers getModifiers()
        {
            return this.modifiers;
        }

        public _implements getImplements()
        {
            return this.implementsFrom;
        }

        public String getName()
        {
            return this.enumName;
        }

        /**
         * finds the index of the target token
         *
         * @param tokens
         * @param target
         * @return
         */
        private static int indexOf( String[] tokens, String target )
        {
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( target ) )
                {
                    return i;
                }
            }
            return -1;
        }

        public static _signature of( String enumSignature )
        {
            _signature sig = new _signature();

            String[] tokens = enumSignature.split( " " );

            int enumTokenIndex = -1;
            int implementsTokenIndex = -1;

            if( indexOf( tokens, "enum" ) < 0 )
            {
                //infer they want a public class
                String[] preamble = new String[ tokens.length + 2 ];
                preamble[ 0 ] = "public";
                preamble[ 1 ] = "enum";
                System.arraycopy( tokens, 0, preamble, 2, tokens.length );
                tokens = preamble;
            }
            if( tokens.length < 2 )
            {
                throw new ModelException(
                    "enum signature must have at least (2) tokens \"enum enumName\" " );
            }
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( "enum" ) )
                {
                    enumTokenIndex = i;
                }
                else if( tokens[ i ].equals( "implements" ) )
                {
                    implementsTokenIndex = i;
                }
            }

            if( (enumTokenIndex < 0) || (enumTokenIndex >= tokens.length - 1) )
            {   //cant be 
                throw new ModelException(
                    "enum token cant be not found or the last token for \""
                    + enumSignature + "\"" );
            }
            sig.enumName = tokens[ enumTokenIndex + 1 ];

            if( enumTokenIndex > 0 )
            {   //modifier provided
                String[] mods = new String[ enumTokenIndex ];
                System.arraycopy( tokens, 0, mods, 0, enumTokenIndex );
                sig.modifiers = _modifiers.of( mods );
                if( sig.modifiers.containsAny(
                    Modifier.ABSTRACT,
                    Modifier.FINAL,
                    Modifier.NATIVE,
                    Modifier.STRICT,
                    Modifier.STATIC,
                    Modifier.PROTECTED,
                    Modifier.PRIVATE,
                    Modifier.SYNCHRONIZED,
                    Modifier.TRANSIENT,
                    Modifier.VOLATILE,
                    _modifiers._modifier.INTERFACE_DEFAULT.getBitValue() ) )
                {
                    throw new ModelException(
                        "Invalid Modifier(s) for enum of \"" + enumSignature
                        + "\" only public allowed" );
                }
            }
            if( implementsTokenIndex > enumTokenIndex + 1 )
            {
                if( implementsTokenIndex == tokens.length - 1 )
                {
                    throw new ModelException(
                        "implements token cannot be the last token" );
                }
                int tokensLeft = tokens.length - (implementsTokenIndex + 1);
                String[] implementsTokens = new String[ tokensLeft ];
                System.arraycopy(
                    tokens, implementsTokenIndex + 1, implementsTokens, 0, tokensLeft );
                List<String> normalImplementsTokens = new ArrayList<String>();
                for( int i = 0; i < implementsTokens.length; i++ )
                {
                    if( implementsTokens[ i ].contains( "," ) )
                    {
                        String[] splitTokens = implementsTokens[ i ].split( "," );
                        for( int j = 0; j < splitTokens.length; j++ )
                        {
                            String tok = splitTokens[ j ].trim();
                            if( tok.length() > 0 )
                            {
                                normalImplementsTokens.add( tok );
                            }
                        }
                    }
                }
                sig.implementsFrom
                    = varcode.java.model._implements.of(
                        normalImplementsTokens.toArray( new String[ 0 ] ) ); //className.of( implementsTokens );
            }
            return sig;
        }

        public String getImplements( int index )
        {
            return implementsFrom.getAt( index );
        }
    }

    @Override
    public Template getTemplate()
    {
        return ENUM;
    }

    @Override
    public _enum replace( String target, String replacement )
    {
        this.constructors = this.constructors.replace( target, replacement );
        this.pckage = this.pckage.replace( target, replacement );
        this.signature = this.signature.replace( target, replacement );
        this.annotations = this.annotations.replace( target, replacement );
        this.constants = this.constants.replace( target, replacement );
        this.fields = this.fields.replace( target, replacement );
        this.imports = this.imports.replace( target, replacement );
        this.javadoc = this.javadoc.replace( target, replacement );
        this.methods = this.methods.replace( target, replacement );
        this.nests = this.nests.replace( target, replacement );
        this.staticBlock = this.staticBlock.replace( target, replacement );
        return this;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash( constructors, pckage, signature, annotations, constants,
            fields, imports, javadoc, methods, nests, staticBlock );
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
        final _enum other = (_enum)obj;
        if( !Objects.equals( this.pckage, other.pckage ) )
        {
            return false;
        }
        if( !Objects.equals( this.imports, other.imports ) )
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
        if( !Objects.equals( this.signature, other.signature ) )
        {
            return false;
        }
        if( !Objects.equals( this.constructors, other.constructors ) )
        {
            return false;
        }
        if( !Objects.equals( this.staticBlock, other.staticBlock ) )
        {
            return false;
        }
        if( !Objects.equals( this.fields, other.fields ) )
        {
            return false;
        }
        if( !Objects.equals( this.constants, other.constants ) )
        {
            return false;
        }
        if( !Objects.equals( this.methods, other.methods ) )
        {
            return false;
        }
        if( !Objects.equals( this.nests, other.nests ) )
        {
            return false;
        }
        return true;
    }
    
}
