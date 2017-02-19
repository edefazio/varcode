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
import javax.lang.model.element.Name;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.java.model._modifiers._modifier;
import varcode.markup.bindml.BindML;
import varcode.java.model._Java._model;
import varcode.java.model._generic._typeParams;
import varcode.LoadException;
import varcode.context.Context;
import varcode.java.ast.JavaAst;
import varcode.java.naming.RefRenamer;
import varcode.java.JavaReflection;
import varcode.ModelException;

/**
 * "parametric code" using a fluent builder pattern for generating the source
 * code of a Java Class.<BR><BR>
 *
 * Allows fields, methods, constructors, etc. to be incrementally "plugged in"
 * and well formatted Java Source code to the generated.
 *
 * Other classes, interfaces, enums can be nested within a single class
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _class
    extends _JavaFileModel
    implements _Java._model
{
    private _javadoc javadoc;
    private _signature signature;
    private _anns annotations;
    private _constructors constructors;
    private _fields fields;
    private _methods methods;
    private _staticBlock staticBlock;

    /**
     * Nested inner classes, static nested classes, interfaces, enums
     */
    private _nests nesteds = new _nests();

    public static _class cloneOf( _class prototype )
    {
        return new _class( prototype );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    /**
     * Would be nice to handle nesteds as well... maybe make add
     * Object
     * @param facets
     * @return 
     */
    public _class add( _facet... facets )
    {
        for( int i = 0; i < facets.length; i++ )
        {
            add( facets[ i ] );
        }
        return this;
    }

    public _class add( _facet facet )
    {
        if( facet instanceof _imports )
        {
            this.imports.add( (_imports)facet );
            return this;
        }
        if( facet instanceof _fields )
        {
            this.fields.add( (_fields)facet );
            return this;
        }
        if( facet instanceof _ann )
        {
            this.annotations.add( (_ann)facet );
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
        if( facet instanceof _modifier )
        {
            this.getModifiers().set( (_modifier)facet );
            return this;
        }
        
        throw new ModelException(
            "Unsupported facet " + facet + " for _class" );
    }

    private static class ClassParams
    {
        //_license license;
        _package pack;
        /* package io.varcode....*/
        _imports imports = new _imports(); // "import Java.lang", Class
        _javadoc javadoc; // starts with /* ends with */
        _anns annots = new _anns(); //starts with @
        _class._signature signature; //starts with 
        List<_facet> facets = new ArrayList<_facet>();
        _nests nesteds = new _nests();
    }

    public static _class of( Object... components )
    {
        ClassParams cp = new ClassParams();
        for( int i = 0; i < components.length; i++ )
        {
            if( components[ i ] instanceof String )
            {
                fromString( cp, (String)components[ i ] );
            }
            else if( components[ i ] instanceof Class )
            {
                if( ((Class)components[ i ]).isAnnotation() )
                {
                    cp.annots.add( ((Class)components[ i ]).getCanonicalName() );
                    cp.imports.addImport( components[ i ] );
                }
                else
                {
                    cp.imports.addImport( components[ i ] );
                }
            }
            else if( components[ i ] instanceof _package )
            {
                cp.pack = (_package)components[ i ];
            }
            else if( components[ i ] instanceof _imports )
            {
                cp.imports = (_imports)components[ i ];
            }
            else if( components[ i ] instanceof _javadoc )
            {
                cp.javadoc = (_javadoc)components[ i ];
            }
            else if( components[ i ] instanceof _anns )
            {
                cp.annots = (_anns)components[ i ];
            }
            else if( components[ i ] instanceof _class._signature )
            {
                cp.signature = (_class._signature)components[ i ];
            }
            else if( components[ i ] instanceof _facet )
            {
                cp.facets.add( (_facet)components[ i ] );
            }
            else if( components[ i ] instanceof _model )
            {
                cp.nesteds.add( (_model)components[ i ] );
            }
        }
        _class _c = new _class( cp.signature );
        for( int i = 0; i < cp.annots.count(); i++ )
        {
            _c.annotate( cp.annots.getAt( i ) );
        }
        for( int i = 0; i < cp.imports.count(); i++ )
        {
            _c.imports( cp.imports.getImports().toArray( new Object[ 0 ] ) );
        }
        for( int i = 0; i < cp.facets.size(); i++ )
        {
            _c.add( cp.facets.get( i ) );
        }
        if( cp.javadoc != null && !cp.javadoc.isEmpty() )
        {
            _c.javadoc( cp.javadoc.getComment() );
        }
        if( cp.pack != null && !cp.pack.isEmpty() )
        {
            _c.packageName( cp.pack.getName() );
        }
        if( !cp.nesteds.isEmpty() )
        {
            for( int i = 0; i < cp.nesteds.count(); i++ )
            {
                _c.nest( cp.nesteds.getAt( i ) );
            }
        }
        return _c;
    }

    private static void fromString( ClassParams cd, String component )
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
                    cd.signature = _signature.of( component );
                }
            }
            else
            {
                cd.signature = _signature.of( component );
            }
        }
    }

    @Override
    public String getName()
    {
        return this.signature.getName();
    }

    /**
     * Parse the classSignature and return a new class ex:
     * <PRE>
     * _class c = _class("public static class MyClass extends YadaYada, implements Blah");
     * _class abs = _class("protected abstract class MyClass extends YadaYada, implements Blah");
     * </PRE>
     *
     * @param classSignature
     */
    public _class( String classSignature )
    {
        this( null, classSignature );
    }

    public _class( _signature sig )
    {
        this( _package.of( null ), sig );
    }

    public _class( String packageName, String classSignature )
    {
        this( _package.of( packageName ), _signature.of( classSignature ) );
    }

    public _class( _package pack, _signature sig )
    {
        this.annotations = new _anns();
        this.pckage = pack; //_package.of( packageName );
        this.javadoc = new _javadoc();
        this.signature = sig; //_signature.of( classSignature );
        this.imports = new _imports();
        this.fields = new _fields();
        this.methods = new _methods();
        this.staticBlock = new _staticBlock();
        this.constructors = new _constructors();
        this.nesteds = new _nests();
    }

    /**
     * Copy constructor
     *
     * @param prototype the prototype
     */
    public _class( _class prototype )
    {
        this.annotations = _anns.cloneOf( prototype.annotations );
        this.pckage = _package.cloneOf( prototype.pckage );
        this.imports = _imports.cloneOf( prototype.imports );
        this.signature = _signature.cloneOf( prototype.signature );

        this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.methods = _methods.cloneOf( prototype.methods );
        this.fields = _fields.cloneOf( prototype.fields );
        this.staticBlock = _staticBlock.cloneOf( prototype.staticBlock );        
        this.constructors = _constructors.cloneOf( prototype.constructors );

        //NESTEDS
        this.nesteds = _nests.cloneOf( prototype.nesteds );
    }

    public static final Template CLASS
        = BindML.compile(
            "{+package+}"
            + "{{+?imports:{+imports+}" + N + "+}}" + N 
            + "{+javadoc+}"
            + "{+annotations+}"
            + "{+signature*+}" + N
            + "{" + N
            + "{{+?fields:{+$>(fields)+}" + N
            + "+}}"
            + "{{+?constructors:{+$>(constructors)+}" + N
            + "+}}"
            + "{{+?methods:{+$>(methods)+}" + N
            + "+}}"
            + "{{+?staticBlock:{+$>(staticBlock)+}" + N
            + "+}}"
            + "{{+?nests:" + N +  "{+$>(nests)+}" + N
            + "+}}"
            + "}" );

    @Override
    public String author()
    {
        return Author.toString( CLASS, getContext() );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( CLASS, getContext(), directives );
    }

    /**
     * sets the modifiers using the int notation (i.e.
     * <CODE>setModifiers( Modifier.PUBLIC | Modifier.STATIC );</CODE> )
     *
     * @param modifiers the modifiers as an int
     * @return the modified class
     */
    public _class setModifiers( int modifiers )
    {
        this.signature.modifiers = _modifiers.of( modifiers );
        return this;
    }

    public _class setImports( _imports imports )
    {
        this.imports = imports;
        return this;
    }

    public _class setModifiers( String... keywords )
    {
        this.signature.modifiers = _modifiers.of( keywords );
        return this;
    }

    public _modifiers getModifiers()
    {
        return this.signature.getModifiers();
    }

    public _extends getExtends()
    {
        return this.signature.getExtends();
    }

    public _implements getImplements()
    {
        return this.signature.getImplements();
    }

    @Override
    public _anns getAnnotations()
    {
        return this.annotations;
    }
    
    /** Gets an annotation that is of the class */
    public _ann getAnnotation( Class annotationClass )
    {
        return this.annotations.getOne( annotationClass );
    }
    
    @Override
    public Context getContext()
    {        
        return VarContext.of(
            "package", pckage,
            "imports", getImports(), //this find all imports from nested classes
            "javadoc", this.javadoc,
            "annotations", this.annotations,
            "signature", this.signature,
            "fields", this.fields,
            "methods", this.methods,
            "staticBlock", this.staticBlock,
            "constructors", this.constructors,
            "nests", authorNests( this.nesteds ) 
        );
    }

    @Override
    public Template getTemplate()
    {
        return CLASS;
    }

    /**
     * <UL>
     * <LI>Build the (.java) source of the {@code _class} model
     * <LI>compile the source using Javac to create .class
     * <LI>load the .class in a new {@code AdHocClassLoader}
     * <LI>create a new instance of the class using {@code constructorArgs}
     * </UL>
     *
     * @param constructorArgs arguments passed into the constructor
     * @return a new instance of the Ad Hoc class
     */
    public Object instance( Object... constructorArgs )
    {
        Class clazz = loadClass( );
        return JavaReflection.instance( clazz, constructorArgs );
    }

    /**
     * Builds a constructor and adds it to this class from it's constituent 
     * parts:
     * NOTE: "parts" can be inferred by type (i.e. _javadoc, _annotation, etc.)
     * or if the part is a String by the String pattern:
     * <UL>
     *  <LI> String that start with "/** ..." are Javadoc s
     *  <LI> Strings that start with "@" are annotations
     * </UL>
     * 
     * ..NOTE: AFTER the constructor signature is read, all Strings AFTER
     * are considered BODY TEXT. <PRE>
     * 
     * public 
     * 
     * @param parts the parts of the constructor
     * @return the _class (after adding the _constructor)
     */
    public _class constructor( Object... parts )
    {
        _constructor _ctor = _constructor.of( parts );
        return constructor( _ctor );
    }
    
    /**
     * Adds a new constructor
     * <PRE>{@code
     * _c.constructor(
     *     _constructor.of( "public A( String name )", "this.name = name;") 
     * );}</PRE>
     * 
     * @return this
     */    
    public _class constructor( _constructor _ctor )
    {
        constructors.addConstructor( _ctor );
        return this;
    }

    /**
     * Sets the Javadoc comment for the class
     *
     * @param javadoc the Javadoc comment
     * @return this
     */
    public _class javadoc( String... javadoc )
    {
        return javadoc( new _javadoc( javadoc ) );
    }

    public _class javadoc( _javadoc javadoc )
    {
        this.javadoc = javadoc;
        return this;
    }

    public _class implement( String... interfaces )
    {
        if( signature.implementsFrom == null )
        {
            signature.implementsFrom = new _implements();
        }
        signature.implementsFrom.implement( interfaces );
        return this;
    }

    /**
     * Implement one or more interfaces
     *
     * @param classes to implement
     * @return this
     */
    public _class implement( Class... classes )
    {
        if( signature.implementsFrom == null )
        {
            signature.implementsFrom = new _implements();
        }
        this.imports.add( (Object[])classes );
        this.signature.implementsFrom.implement( classes );
        return this;
    }

    @Override
    public _class annotate( _anns annotations )
    {
        this.annotations = annotations;
        return this;
    }
    
    public _class annotate( _ann... annotations )
    {
        this.annotations.add( annotations );
        return this;
    }

    public _class methods( _methods methods )
    {
        String[] methodNames = methods.getNames();

        for( int i = 0; i < methods.count(); i++ )
        {
            List<_method> methodsByName = methods.getMethodsNamed( methodNames[ i ] );
            for( int j = 0; j < methodsByName.size(); j++ )
            {
                method( methodsByName.get( j ) );
            }
        }
        return this;
    }

    /**
     * Builds and adds a main method to this _class (with the bodyLines) and 
     * and return the modified _class
     *
     * @param bodyLines
     * @return the modified _class
     */
    public _class mainMethod( Object... bodyLines )
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

    public _class method( Object...parts )
    {
        return method( _method.of( parts ) );
    }
    
    public _class staticBlock( _staticBlock staticBlock )
    {
        this.staticBlock = staticBlock;
        return this;
    }
    
    public _class staticBlock( _code code )
    {
        this.staticBlock = _staticBlock.of( code );
        return this;
    }

    public _class staticBlock( Object... code )
    {
        this.staticBlock.addTailCode( (Object[])code );
        return this;
    }

    public _class method( _method _m )
    {
        if( _m.isAbstract() && !this.getSignature().getModifiers().contains( "abstract" ) )
        {
            throw new ModelException(
                "Cannot add an abstract method " + N + _m + N + " to a non-abstract class " );
        }
        if( _m.isAbstract() && !_m.getBody().isEmpty() )
        {
            throw new ModelException(
                "abstract method :" + N + _m + N + "cannot have a method body:" + N + _m.getBody() );
        }

        //let me automatically "look through" the method signature and ensure
        // I am importing the appropriate types        
        this.methods.add( _m );
        return this;
    }

    /**
     * Adds a field to the class and atReturn the _class
     *
     * @param field the field to add
     * @return the _class with field added
     */
    public _class field( _field field )
    {
        fields.add( field );
        return this;
    }

    /**
     * Adds a Field, and getter/setter methods on the class
     * <PRE>
     * _class _c = new _class("public class MyClass");
     * _c.property( _field.of( "private String name" ) );
     *
     * adds :
     * public class MyClass
     * {
     *    <B>private String name;</B>
     *
     *    <B>public String getName()
     *    {
     *        return this.name;
     *    }
     *    public MyClass setName( String name )
     *    {
     *        this.name = name;
     *    }</B>
     * }
     * </PRE>
     *
     * @param _f the field declaration
     * @return the modified _class
     */
    public _class property( _field _f )
    {
        fields.add(_f );
        String name = _f.getName();
        String nameCaps
            = Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
        //add a get method
        this.methods.add( "public " + _f.getType() + " get" + nameCaps + "()",
            "return this." + name + ";" );

        if( !_f.getModifiers().contains( "final" ) )
        {   //ONly add a set method IF the field is NOT final     
            //use a fleutn 
            this.methods.add("public " + getName() + " set" + nameCaps + "( " + _f.getType() + " " + name + " )",
                "this." + name + " = " + name + ";",
                "return this;" );
        }
        return this;
    }
    
    public static final Template FLUENT_SET_SIGNATURE = BindML.compile(
        "public {+name+} set{+Name+}( {+type+} {+name+} )"
    );
    
    public static final Template FLUENT_SET_BODY = BindML.compile(
        "this.name = name;" + "\n\r" +
        "return this;"    
    );
    
    public _class properties( String...fieldDeclarations )
    {
        for( int i = 0; i < fieldDeclarations.length; i++ )
        {
            property( fieldDeclarations[ i ] );
        }
        return this;
    }
    
    /**
     * Creates a Field, and getter/setter methods on the class and atReturn the 
     * class null     i.e.<PRE>
     * _class _c = new _class("public class MyClass");
     * _c.property( "private String name");
     *
     * adds :
     * public class MyClass
     * {
     *    <B>private String name;</B>
     *
     *    <B>public String getName()
     *    {
     *        return this.name;
     *    }
     *    public void setName( String name )
     *    {
     *        this.name = name;
     *    }</B>
     * }
     * </PRE>
     *
     * @param parts the declaration of the field
     * @return the modified _class
     */
    public _class property( Object... parts )
    {
        return property( _field.of( parts ) );        
    }

    /**
     * Returns the field with the given name, or null if a field by that name
     * doesn't exist.
     *
     * @param fieldName the name of the field
     * @return the _field (langmodel) for the field
     */
    public _field getField( String fieldName )
    {
        return this.fields.getByName( fieldName );
    }

    public _field getFieldAt( int fieldIndex )
    {
        return this.fields.getAt( fieldIndex );
    }
    
    public _class field( Object... parts )
    {
        _field _f = _field.of( parts );
        return field( _f );
    }
    
    /*
    public _class field( String field )
    {
        fields.add( _fields._field.of( field ) );
        return this;
    }
    */
    public _class fields( _fields _fs )
    {
        for( int i = 0; i < _fs.count(); i++ )
        {
            this.fields.add( _fs.getAt( i ) );
        }
        return this;
    }

    public _class fields( String... fields )
    {
        for( int i = 0; i < fields.length; i++ )
        {
            this.fields.add( _fields._field.of( fields[ i ] ) );
        }
        return this;
    }

    public _class importsStatic( Object... imports )
    {
        this.imports.addStaticImports( imports );
        return this;
    }

    public _class imports( Object... imports )
    {
        this.imports.add( imports );
        return this;
    }

    /**
     * nest an ( _enum, _interface, _class ) inside this _class
     *
     * @param component the component to nest inside this class
     * @return this _class containing the nested component
     */
    public _class nest( _model component )
    {
        this.nesteds.add( component );
        return this;
    }

    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }

    public String getPackageName()
    {
        if( this.pckage != null )
        {
            return this.pckage.getName();
        }
        return null;
    }

    public _package getClassPackage()
    {
        return this.pckage;
    }

    @Override
    public _fields getFields()
    {
        return this.fields;
    }

    public _signature getSignature()
    {
        return this.signature;
    }

    public _constructor getConstructor( int ctorIndex )
    {
        if( ctorIndex >= this.constructors.count() )
        {
            throw new ModelException( "No Constructor at ["+ ctorIndex + "]" );
        }
        return this.constructors.getAt( ctorIndex );
    }
    
    public _constructors getConstructors()
    {
        return this.constructors;
    }

    @Override
    public _methods getMethods()
    {
        return this.methods;
    }

    /**
     * For <B>NON-Overloaded methods</B>, at Return a _method if there is
     * <B>only ONE method</B> with this <B>exact name</B>
     *
     * @param name the name of the method to find
     * @return the ONLY method that has this name, - or - null if there are no
     * methods with this name - or - VarException if there are more than one
     * method with this name
     * @throws ModelException if more than one method has this name
     */
    public _method getMethod( String name )
        throws ModelException
    {
        return this.methods.getMethod( name );        
    }

    public List<_method> getMethodsNamed( String name )
    {
        return this.methods.getMethodsNamed( name );
    }

    @Override
    public int getNestCount()
    {
        return this.nesteds.count();
    }

    @Override
    public _nests getNests()
    {
        return this.nesteds;
    }

    /** 
     * removes a nest by name
     * @param nestName the (simple) name of the nested component
     * @return this class
     * @throws ModelException if there is no nest found by that name
     */
    public _class removeNestByName( String nestName )
        throws ModelException
    {
        this.nesteds.removeByName( nestName );
        return this;
    }
    
    @Override
    public _model getNestAt( int index )
    {
        return this.nesteds.getAt( index );
    }

    /**
     * Gets the nested entity by name
     * @param name the simple name of the entity
     * @return the Nested model 
     */
    public _model getNest( String name )
    {
        return this.nesteds.getByName( name );
    }

    public _class setName( String name )
    {
        String oldName = getName();

        //any old reference to OLDNAME.class, change it to NEWNAME.class
        //this.replace( oldName + ".class", name + ".class" );
        
        //TODO I could be more safe if I verified that the character 
        //prior was not a Char.identifierChar
        // so this: 
        // _class _c = _class.of("public class c").field( "class f = Javac.class");
        // _c.setName( "BLAH" ); 
        // would NOT produce this:
        // public class BLAH{
        //   class f = JavaBLAH.class;
        // }
        //"c.class" would not try to 

        //what about "fully qualified references?" DOESNT matter (above should cover it)
        this.getSignature().className = name;
        _constructors cs = this.getConstructors();
        for( int i = 0; i < cs.count(); i++ )
        {
            cs.getAt( i ).setName( name );
        }

        //update any atReturn that return an instance
        for( int i = 0; i < this.methods.count(); i++ )
        {
            if( methods.getAt( i ).getReturnType().equals( oldName ) )
            {
                methods.getAt( i ).setReturnType( name );
            }
        }

        //update any fields that have an instance
        for( int i = 0; i < this.fields.count(); i++ )
        {
            if( fields.getAt( i ).getType().equals( oldName ) )
            {
                fields.getAt( i ).setType( name );
            }
        }
        return this;
    }

    public _staticBlock getStaticBlock()
    {
        return this.staticBlock;
    }

    @Override
    public String toString()
    {
        return author();
    }

    public _class packageName( Name name )
    {
        this.pckage = _package.of( name );
        return this;
    }
    
    public _class packageName( String packageName )
    {
        this.pckage = _package.of( packageName );
        return this;
    }

    public _class extend( Class baseClass )
    {
        this.signature.extendsFrom.clear();
        if( baseClass != null )
        {
            this.signature.extendsFrom.addExtends( baseClass );
        }
        return this;
    }

    public _class extend( String baseClass )
    {
        this.signature.extendsFrom.clear();
        this.signature.extendsFrom.addExtends( baseClass );
        return this;
    }

    /**
     * signature of the _class
     */
    public static class _signature
        implements _Java, Authored
    {
        private _modifiers modifiers;
        private String className;

        /**
         * optional, (for generic classes) things inside the < >'s (like "K, V"
         * of "class Map<K, V>")
         */
        private _typeParams genericTypeParams;

        private _extends extendsFrom;

        private _implements implementsFrom;

        /**
         * Class Signature:<PRE>
         * public static class A<K,V> extends Base implements I1, I2 
         * \-----------/        \___/         \__/            \____/
         *    modifiers      generic type   extends           implements
         *                     params
         * </PRE>
         */                     
        public static final Template CLASS_SIGNATURE
            = BindML.compile(
                "{+modifiers+}class {+className*+}{+genericTypeParams+}"
                + "{+extends+}"
                + "{+implements+}" );

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( 
                className, extendsFrom, genericTypeParams, 
                implementsFrom, modifiers );
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
            if( !Objects.equals( this.className, other.className ) )
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
            if( !Objects.equals( this.implementsFrom, other.implementsFrom ) )
            {
                return false;
            }
            return true;
        }
        
        @Override
        public Context getContext()
        {
            return VarContext.of(
                "className", className,
                "genericTypeParams", this.genericTypeParams,
                "modifiers", modifiers,
                "extends", extendsFrom,
                "implements", implementsFrom );
        }
        
        @Override
        public Template getTemplate()
        {
            return CLASS_SIGNATURE;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( CLASS_SIGNATURE,
                getContext(),
                directives );
        }

        public _signature( String className )
        {
            modifiers = new _modifiers();
            this.className = className;
            this.genericTypeParams = new _typeParams();
            this.extendsFrom = new _extends();
            this.implementsFrom = new _implements();
        }

        private _signature()
        {
            this( " ");            
        }
        
        /** copy constructor */
        public _signature( _signature prototype )
        {
            className = prototype.className + "";
            genericTypeParams = _typeParams.cloneOf( prototype.genericTypeParams );
            extendsFrom = _extends.cloneOf( prototype.extendsFrom );
            implementsFrom = _implements.cloneOf( prototype.implementsFrom );
            modifiers = _modifiers.cloneOf( prototype.modifiers );
        }

        @Override
        public String toString()
        {
            return author();
        }

        public String getName()
        {
            return this.className;
        }

        public _typeParams getGenericTypeParams()
        {
            return this.genericTypeParams;
        }

        public _extends getExtends()
        {
            return this.extendsFrom;
        }

        public _signature setGenericTypeParams( String... typeParams )
        {
            this.genericTypeParams = _typeParams.of( typeParams );
            return this;
        }

        public _signature setGenericTypeParams( _typeParams _typePars )
        {
            this.genericTypeParams = _typePars;
            return this;
        }

        public _signature extend( String className )
        {
            this.extendsFrom.addExtends( className );
            return this;
        }

        public _signature extend( Class clazz )
        {
            this.extendsFrom.addExtends( clazz );
            return this;
        }

        public _implements getImplements()
        {
            return this.implementsFrom;
        }

        public _modifiers getModifiers()
        {
            return this.modifiers;
        }

        public _signature setModifiers( _modifiers modifiers )
        {
            this.modifiers = modifiers;
            return this;
        }

        public _signature setModifiers( String... modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }

        public _signature setModifiers( int... modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }

        public static _signature cloneOf( _signature prototype )
        {
            return new _signature( prototype );            
        }

        @Override
        public _signature replace( String target, String replacement )
        {
            this.className = RefRenamer.apply( className, target, replacement);
                //className.replace( target, replacement );
            this.genericTypeParams = this.genericTypeParams.replace( target, replacement );
            this.extendsFrom.replace( target, replacement );
            this.implementsFrom.replace( target, replacement );
            return this;
        }

        public _signature implement( Class... interfaceClasses )
        {
            this.implementsFrom.implement( interfaceClasses );
            return this;
        }

        public _signature implement( String... interfaceClasses )
        {
            this.implementsFrom.implement( interfaceClasses );
            return this;
        }

        public static _signature of( String classSignature )
        {
            if( !classSignature.contains( " class " ) )
            {
                classSignature = "public class " + classSignature;
            }
            try
            {   // Since we already HAVE an AST parser, lets use it to parse the
                // String signature (not roll our own) just add "{}" to
                // the signature (an empty class body)
                ClassOrInterfaceDeclaration coid = new ClassOrInterfaceDeclaration();
                CompilationUnit astRoot = 
                    JavaAst.astFrom( classSignature + "{}" );
                ClassOrInterfaceDeclaration astCoid
                    = (ClassOrInterfaceDeclaration)astRoot.getTypes().get( 0 );
                List<ClassOrInterfaceType> astExtends = astCoid.getExtends();
                List<ClassOrInterfaceType> astImpls = astCoid.getImplements();
                List<TypeParameter> astTypeParams = astCoid.getTypeParameters();

                String name = astCoid.getName();
                _modifiers _mods = _modifiers.of( astCoid.getModifiers() );

                if( _mods.containsAny(
                    Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.NATIVE,
                    Modifier.TRANSIENT, Modifier.VOLATILE, Modifier.STRICT ) )
                {
                    throw new ModelException(
                        "classSignature \"" + classSignature
                        + "\" contains invalid modifiers" );
                }

                _signature cs = new _signature( name );
                cs.setModifiers( _mods );
                if( astTypeParams.size() > 0 )
                {
                    _typeParams _tp = new _typeParams();
                    for( int i = 0; i < astTypeParams.size(); i++ )
                    {
                        _tp.addParam( astTypeParams.get( i ).toString() );
                    }
                    cs.setGenericTypeParams( _tp );
                }
                if( astExtends.size() > 0 )
                {
                    cs.extend( astExtends.get( 0 ).toString() );
                }
                if( astImpls.size() > 0 )
                {
                    for( int i = 0; i < astImpls.size(); i++ )
                    {
                        cs.implement( astImpls.get( i ).toString() );
                    }
                }
                return cs;
            }
            catch( ParseException pe )
            {
                throw new ModelException(
                    "Error parsing Class Signature \""+ classSignature+"\"", pe );
            }
            catch( LoadException le )
            {
                throw new ModelException(
                    "Error parsing Class Signature \""+ classSignature+"\"", le );
            }
        }
    }

    @Override
    public _class replace( String target, String replacement )
    {
        this.signature.replace( target, replacement );
        this.annotations.replace( target, replacement );
        this.pckage.replace( target, replacement );
        this.constructors.replace( target, replacement );
        this.javadoc.replace( target, replacement );
        
        this.imports.replace( target, replacement );
        this.methods.replace( target, replacement );
        this.staticBlock.replace( target, replacement );
        this.nesteds.replace( target, replacement );

        this.fields.replace( target, replacement );
        return this;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash( this.signature, this.annotations, this.pckage,
            this.constructors, this.javadoc, this.imports, this.methods, 
            this.staticBlock, this.nesteds, this.fields );
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
        final _class other = (_class)obj;
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
        if( !Objects.equals( this.signature, other.signature ) )
        {
            return false;
        }
        if( !Objects.equals( this.annotations, other.annotations ) )
        {
            return false;
        }
        if( !Objects.equals( this.constructors, other.constructors ) )
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
        if( !Objects.equals( this.staticBlock, other.staticBlock ) )
        {
            return false;
        }
        if( !Objects.equals( this.nesteds, other.nesteds ) )
        {
            return false;
        }
        return true;
    }
}
