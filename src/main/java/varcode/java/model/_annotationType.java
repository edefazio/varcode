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
import varcode.author.Author;
import varcode.context.Directive;
import varcode.context.Context;
import varcode.context.VarContext;
import varcode.java.lang.RefRenamer;
import varcode.java.model._Java.FileModel;
import varcode.java.model._annotations._annotation;
import varcode.java.model._Java.Authored;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.ModelException;
import varcode.translate.PrimitiveTranslate;

/**
 * The declaration of an Annotation
 * i.e.
<PRE>
package io.varcode.mystuff;    // <-- package
import java.lang.annotation.*; // <-- imports
 
@Target( ElementType.METHOD ) //<-- annotations
@Retention( RetentionPolicy.RUNTIME ) //<-- annotations
public @interface ClassPreamble //<-- _signature
{
    int count() default 0; //<-- annotationProperty
    String[] values(); // <-- annotationProperty
    GType gtype();     // <-- annotationProperty
     
    public enum GType  //
    {                  // nested 
       A,              //
       B;              //
    }                  //
}
 </PRE> 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _annotationType
    extends FileModel
    implements _Java._model, Authored
{
    public static final String N = System.lineSeparator();
    
    private _javadoc javadoc = new _javadoc();
    private _signature signature;
    private _annotations annotations = new _annotations();
    private _annotationProperties annotationProperties = new _annotationProperties();
    private _nests nests = new _nests();
    
    public _annotationType add( Object obj )
    {
        return addPart( this, obj );
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash( this.annotationProperties, this.annotations, this.imports, 
            this.javadoc, this.nests, this.pckage, this.signature );
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
        {
            System.out.println( "SAME OBJ");
            return true;
        }
        if( obj == null )
        {
            System.out.println( "NULL OBJ");
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            System.out.println( "DIFF CLASS");
            return false;
        }
        final _annotationType other = (_annotationType)obj;
        if( !Objects.equals( this.javadoc, other.javadoc ) )
        {
            System.out.println( "DIFF JAVADOC");
            return false;
        }
        if( !Objects.equals( this.signature, other.signature ) )
        {
            System.out.println( "DIFF SIGN");
            return false;
        }
        if( !Objects.equals( this.annotations, other.annotations ) )
        {
            System.out.println( "DIFF ANN");
            return false;
        }
        if( !Objects.equals( this.annotationProperties, other.annotationProperties ) )
        {
            System.out.println( "DIFF ANN PROP");
            return false;
        }
        if( !Objects.equals( this.nests, other.nests ) )
        {
            System.out.println( "DIFF NESTS ");
            return false;
        }
        return true;
    }
    public static _annotationType addPart( _annotationType _at, Object part )
    {
        if( part instanceof String )
        {
            String str = (String)part;
            if( str.startsWith( "/**" ) )
            {
                return _at.javadoc( 
                    str.substring( 3, str.length() - 2 ).trim() );
            }
            if( str.startsWith( "/*" ) )
            {
                return _at.javadoc( 
                    str.substring( 2, str.length() - 2 ).trim() );
            }
            if( str.startsWith( "@" ) )
            {
                return _at.annotate( str );
            }
            if( str.startsWith( "import ") )
            {
                return _at.imports( str.substring( "import ".length() ) );
            }
            if( str.startsWith( "package" ) )
            {
                return _at.setPackage( 
                    _package.of( str.substring( "package".length() ).trim() ) );
            }
            throw new ModelException( "unable to add part \""
                + str + "\" to AnnotationType" );
        }
        if( part instanceof Class )
        {
            return _at.imports( part );
        }
        if( part instanceof _javadoc )
        {
            return _at.javadoc( (_javadoc)part );
        }
        if( part instanceof _imports )
        {
            return _at.imports( (_imports)part );
        }
        if( part instanceof _package )
        {
            return _at.setPackage( (_package)part );
        }
        if( part instanceof _annotations )
        {
            return _at.annotate( (_annotations)part );            
        }
        if( part instanceof _annotation )
        {
            return _at.annotate( (_annotation)part );            
        }
        if( part instanceof _annotationProperties )
        {
            return _at.properties( (_annotationProperties)part );
        }
        if( part instanceof _annotationProperty )
        {
            return _at.property( (_annotationProperty)part );
        }
        if( part instanceof _model )
        {
            return _at.nest( (_model) part );
        }
        throw new ModelException( "unable to add part \""
                + part + "\" to AnnotationType" );
    }
    
    public static _annotationType of( Object...parts )
    {
        if( parts.length == 0 )
        {
            throw new ModelException( 
                "annotation Type must have at least a signature");            
        }
        //the signature is ALWAYS the last part
        _signature _sig = _signature.of( parts[ parts.length - 1 ] );
        _annotationType _at = new _annotationType( _sig );
        Object[] nonSig = new Object[ parts.length - 1 ];
        System.arraycopy( parts, 0, nonSig, 0, parts.length -1 );
        for( int i = 0; i < nonSig.length; i++ )
        {
            addPart( _at, nonSig[ i ] );
        }
        return _at;
    }
    
   
    private _annotationType()
    {
        //private used internally for cloning
    }
    
    /**
     * _annotationType _at = _annotationType("@interface MyAnn"); 
     * @param sig the signature of the AnnotationType
     */
    public _annotationType( _signature sig )
    {
        this.signature = sig;                
        this.javadoc = new _javadoc();
        this.annotations = new _annotations();
        this.nests = new _nests();
        this.annotationProperties = new _annotationProperties();
    }
    
    /** copy constructor */
    public _annotationType( _annotationType prototype )
    {
        annotationProperties = 
            _annotationProperties.cloneOf(prototype.annotationProperties );
        annotations = 
            _annotations.cloneOf(prototype.annotations );
        imports = 
            _imports.cloneOf(prototype.imports );
        javadoc = 
            _javadoc.cloneOf(prototype.javadoc );
        nests = 
            _nests.cloneOf(prototype.nests );
        pckage = 
            _package.cloneOf(prototype.pckage );
        signature = 
            _signature.cloneOf(prototype.signature );        
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of(
            "package", this.pckage,
            "imports", this.getImports(),//roll up imports compile all nested models 
            "javadoc", this.javadoc,
            "annotations", this.annotations,
            "signature", this.signature,
            "annotationProperties", this.annotationProperties,
            "nests", authorNests( this.nests ) );
    }
    
    public static final Template ANNOTATION_TYPE = BindML.compile(        
        "{+package+}" +  //NOTE: IMPORTANT TO KEEP name "package" (for nesting)
        "{+imports+}" + //NOTE: IMPORTANT TO KEEP name "imports" (for nesting)
        "{+javadoc+}" + 
        "{+annotations+}" +      
        "{+signature+}" + N +     
        "{" + N + 
        "{{+:{+$>(annotationProperties)+}" + N +     
        "+}}" +  
        "{{+?nests:" + N + 
        "{+$>(nests)+}" + N + 
        "+}}" +    
        "}" + N );

    public static _annotationType cloneOf( _annotationType _proto )
    {
        return new _annotationType( _proto );
    }    
    
    public _annotationProperty getProperty( String name )
    {
        return this.annotationProperties.getByName( name );
    }
    
    public _annotationProperty getPropertyAt( int index )
    {
        return this.annotationProperties.getAt( index );
    }
    
    @Override
    public String getName()
    {
        return this.signature.getName();
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( ANNOTATION_TYPE, getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }
    
    public _annotationType setPackage( _package pack )
    {
        this.pckage = pack;
        return this;
    }
    
    public String getPackageName()
    {
        return this.pckage.getName();
    }
    
    @Override
    public _annotationType replace( String target, String replacement )
    {
        this.annotationProperties.replace( target, replacement );
        this.annotations.replace( target, replacement );
        this.imports.replace( target, replacement );
        this.javadoc.replace( target, replacement );
        this.nests.replace( target, replacement );
        this.pckage.replace( target, replacement );
        this.signature.replace( target, replacement );
        return this;
    }
    
    public _annotationType javadoc( _javadoc _jd )
    {
        this.javadoc = _jd;
        return this;
    }
    
    public _annotationType javadoc( String...javadoc )
    {
        this.javadoc = _javadoc.of( javadoc );
        return this;
    }
    
    public _annotationType nest( _model _m )
    {
        this.nests.add( _m );
        return this;
    }
    
    public _annotationType packageName( String packageName )
    {
        this.pckage = _package.of( packageName );
        return this;
    }
    
    public _annotationType setName( String name )
    {
        this.signature.setName( name );
        return this;
    }
    
    public _annotationType imports( _imports imports )
    {
        this.imports = imports;
        return this;
    }
    
    public _annotationType imports( Object...imports )
    {
        this.imports.add( imports );
        return this;
    }
    
    public _annotationType annotate( Object... _ann )
    {
        this.annotations.add( (Object[]) _ann );
        return this;
    }
    
    public _annotationType properties( _annotationProperties _ap )
    {
        this.annotationProperties.add( _ap );
        return this;
    }
    
    public _annotationType property( Class type, String name )
    {
        return property( type.getCanonicalName(), name );
    }

    /**
     * Builds a property of the annotation
     * ie.
     * <PRE>
     * _annotationType.of("@interface MyAnnotation")
     *     .property( "String", "name");
     * 
     *  represents :
     * 
     * @interface MyAnnotation
     * {
     *    String name();
     * }
     * </PRE>
     * @param type
     * @param name
     * @param defaultValue
     * @return the annotation type
     */
    public _annotationType property( 
        String type, String name, Object defaultValue )
    {
        return property( 
            type, 
            name, 
            PrimitiveTranslate.translateFrom( defaultValue ).toString() );
    }
    
    /**
     * 
     * @param type
     * @param name
     * @param defaultValue
     * @return 
     */
    public _annotationType property( 
        Class type, String name, Object defaultValue )
    {
        return property( 
            type.getCanonicalName(), 
            name, 
            PrimitiveTranslate.translateFrom( defaultValue ).toString() );
    }
    
    public _annotationType property( String type, String name )
    {
        return property( 
            _annotationProperty.of( type, name, null ) );
    }
    
    public _annotationType property( _annotationProperty _ap )
    {
        this.annotationProperties.add( _ap );
        return this;
    }
    public _annotationType property( String type, String name, String defaultValue )
    {
        this.annotationProperties.add( 
            _annotationProperty.of( type, name, defaultValue ) );
        return this;
    }
    
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public Template getTemplate()
    {
        return ANNOTATION_TYPE;
    }


    @Override
    public _annotations getAnnotations()
    {
        return this.annotations;
    }

    @Override
    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }

    @Override
    public int getNestCount()
    {
        return this.nests.count();
    }

    @Override
    public _model getNestAt( int index )
    {
        return this.nests.getAt( index );
    }

    @Override
    public _nests getNests()
    {
        return this.nests;
    }

    private static final _fields NO_FIELDS = new _fields();
    
    @Override
    public _fields getFields()
    {
        return NO_FIELDS;
    }

    private static final _methods NO_METHODS = new _methods();
    
    @Override
    public _methods getMethods()
    {
        return NO_METHODS;
    }
    
    public _modifiers getModifiers()
    {
        return this.signature.getModifiers();
    }
    
    public _annotationType setModifiers( int modifiers )
    {
        this.signature.setModifiers( _modifiers.of( modifiers ) );
        return this;
    }
    
    /**
     * The signature of the Annotation Type
     */
    public static class _signature
        implements _Java, Authored
    {
        public static final Template SIGNATURE = BindML.compile(
            "{+modifiers+}@interface {+annotationTypeName*+}" );  
        
        private _modifiers modifiers = new _modifiers();
        private String typeName;
        
        public static _signature of( Object signature )
        {
            if( signature instanceof _signature )
            {
                return (_signature)signature;
            }
            if( signature instanceof String )
            {
                String[] modsAndTypeName = parseModifiersTypeName( (String)signature );
                _signature _s = new _signature( modsAndTypeName[ 1 ].trim() );
                if( modsAndTypeName[ 0 ].trim().length() > 0 )
                {
                    _modifiers _mods = _modifiers.of( modsAndTypeName[ 0 ].trim() );
                    _s.setModifiers( _mods );
                }
                return _s;
            }
            throw new ModelException(
                "unable to create AnnotationType signature with "+ signature );
        }
        
        @Override
        public Template getTemplate()
        {
            return SIGNATURE;
        }
        
        private static String[] parseModifiersTypeName( String str )
        {
            int index = str.indexOf( "@interface" );
            if( index < 0 )
            {
                return new String[]{ "public", str };
                //throw new ModelException(
                //    "Annotation Type signature must contain @interface" );
            }
            return new String[]{ 
                str.substring( 0, index ), 
                str.substring( index + "@interface".length() ) };
        }
        
        public _signature( String typeName )
        {
            this( null, typeName );
        }
        
        public static _signature cloneOf( _signature sig )
        {
            return new _signature( sig );            
        }
        
        public _signature( _signature prototype )
        {
            this( _modifiers.cloneOf( prototype.modifiers ), prototype.typeName );
        }
        
        public _signature( _modifiers modifiers, String typeName )
        {            
            if( modifiers == null )
            {
                this.modifiers = new _modifiers();
            }
            else
            {
                validateModifiers( modifiers );
                this.modifiers = modifiers;
            }
            this.typeName = typeName;
            if( typeName == null )
            {
                throw new ModelException( "Annotation Type name cannot be null" );
            }
                        
        }
        
        private void validateModifiers( _modifiers modifiers )
        {
            if( modifiers.containsAny( 
                Modifier.ABSTRACT, Modifier.FINAL, Modifier.NATIVE, 
                Modifier.STATIC, Modifier.STRICT, Modifier.SYNCHRONIZED, 
                Modifier.TRANSIENT, Modifier.VOLATILE ) )
            {
                throw new ModelException( "Invalid modifiers for annotation type" );
            }
        }
        
        public _signature setModifiers( _modifiers _mods )
        {
            validateModifiers( _mods );
            this.modifiers = _mods;
            return this;
        }
        
        public _signature setName( String name )
        {
            this.typeName = name;
            return this;
        }
        
        public _modifiers getModifiers()
        {
            return this.modifiers;
        }
        
        public String getName()
        {
            return this.typeName;
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( this.modifiers, this.typeName );    
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
            if( !Objects.equals( this.typeName, other.typeName ) )
            {
                System.out.println( "DIFF TYPE NAME");
                return false;
            }
            if( !Objects.equals( this.modifiers, other.modifiers ) )
            {
                System.out.println( "DIFF MODIFIERS");
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
        public Context getContext()
        {
            return VarContext.of( 
                "modifiers", this.modifiers,
                "annotationTypeName", this.typeName );
        }
        
        @Override
        public String toString()
        {
            return author();
        }
        
        @Override
        public String author( Directive... directives )
        {   
            return Author.toString( SIGNATURE, 
                getContext(),    
                directives );
        }

        @Override
        public _signature replace( String target, String replacement )
        {
            this.modifiers.replace( target, replacement );
            this.typeName = RefRenamer.apply( this.typeName, target, replacement );
            return this;
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(  this );
        }
    }
    
    /**
     * All properties (like fields) that belong to an annotation
     */
    public static class _annotationProperties
        implements _Java, Countable, Authored
    {
        private List<_annotationProperty> properties = 
            new ArrayList<_annotationProperty>();
        
        public static final Template ANNOTATION_PROPERTIES = BindML.compile(
            "{{+:{+properties+}" + N + 
            "+}}" );
        
        public _annotationProperties()
        {
        }
        
        public _annotationProperties( _annotationProperties prototype )
        {
            for(int i=0; i< prototype.count(); i++ )
            {
                add( _annotationProperty.cloneOf( 
                    prototype.properties.get( i ) ) );
            }
        }
        
        public static _annotationProperties cloneOf( _annotationProperties prototype )
        {
            return new _annotationProperties( prototype );            
        }
         
        public int count()
        {
            return this.properties.size();
        }
        
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        public _annotationProperty getByName( String name )
        {
            for( int i = 0; i < this.properties.size(); i++ )
            {
                if( properties.get( i ).name.equals( name ) )
                {
                    return properties.get( i );
                }
            }
            return null;
        }   
        
        public  _annotationProperty getAt( int index )
        {
            if( index < 0 || index >= this.properties.size() )
            {
                throw new ModelException( "Invalid index [" + 
                    index + "] for annotation property" );
            }
            return this.properties.get( index );
        }
            
        private boolean isUniquePropName( String name )
        {
            for( int i = 0; i < this.properties.size(); i++ )
            {
                if( this.properties.get( i ).name.equals( name ) )
                {
                    return false;
                }
            }
            return true;
        }
        
        public _annotationProperties add( _annotationProperties _aps )
        {
            for(int i=0; i< _aps.properties.size(); i++ )
            {
                add( _aps.getAt( i ) );
            }
            return this;
        }
        public final _annotationProperties add( _annotationProperty _ap )
        {
            if( !isUniquePropName( _ap.name ) )
            {
                throw new ModelException( "Property name \"" + _ap.name
                    + "\" already exists on annotation type" );
            }
            properties.add( _ap );
            return this;            
        }
        
        public _annotationProperties add( 
            String type, String name, String defaultValue )
        {
            if( !isUniquePropName( name ) )
            {
                throw new ModelException( "Property name \"" + name
                    +"\" already exists on annotation type" );
            }
            properties.add( new _annotationProperty(type, name, defaultValue ) );
            return this;
        }
        
        public _annotationProperties add( 
            String type, String name )
        {
            return add( type, name, null );
        }

        public _annotationProperties remove( _annotationProperty _ap )
        {
            this.properties.remove( _ap );
            return this;
        }
        
        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public Template getTemplate()
        {
            return ANNOTATION_PROPERTIES;
        }
        
        @Override
        public Context getContext()
        {
            return VarContext.of( "properties", this.properties );
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( this.properties );    
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
            final _annotationProperties other = (_annotationProperties)obj;
            if( !Objects.equals( this.properties, other.properties ) )
            {
                return false;
            }
            return true;
        }
        
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( ANNOTATION_PROPERTIES, 
                getContext(),
                directives );
        }

        @Override
        public String toString()
        {
            return author();
        }
    
        @Override
        public _annotationProperties replace( String target, String replacement )
        {
            for( int i = 0; i < properties.size(); i++ )
            {
                properties.get( i ).replace( target, replacement );
            }
            return this;
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }        
    }
    
    /**
     * Single property of an annotationType
     */
    public static class _annotationProperty
        implements _Java, Authored
    {
        private _annotations annotations = new _annotations(true);
        private String type;
        private String name;
        private String defaultValue;
        private _javadoc javadoc =  new _javadoc();
        
        public static final Template PROPERTY = BindML.compile(
            "{+javadoc+}{+annotations+}{+type*+} {+name*+}(){{+?defaultValue: default {+defaultValue+}+}};" );   
        
        private _annotationProperty()
        {
        }
        
        public _annotationProperty( _annotationProperty prototype )
        {
            javadoc = _javadoc.cloneOf( prototype.javadoc );
            annotations = _annotations.cloneOf( prototype.annotations ); 
            type = prototype.type;
            name = prototype.name;
            defaultValue = prototype.defaultValue;
        }
        
        public static _annotationProperty cloneOf( _annotationProperty _ap )
        {
            return new _annotationProperty( _ap );            
        }
        
        public _annotationProperty( String type, String name )
        {
            this.name = name;
            this.type = type;
            this.defaultValue = null;
        }
        
        public _annotationProperty( 
            String type, String name, String defaultValue )
        {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }
        
        public _annotationProperty annotate( Object...annots )
        {
            this.annotations.add( annots );
            return this;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        public String getType()
        {
            return this.type;
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( this.annotations, this.defaultValue, 
                this.javadoc, this.name, this.type );    
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
            final _annotationProperty other = (_annotationProperty)obj;
            if( !Objects.equals( this.type, other.type ) )
            {
                return false;
            }
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.defaultValue, other.defaultValue ) )
            {
                return false;
            }
            if( !Objects.equals( this.annotations, other.annotations ) )
            {
                return false;
            }
            if( !Objects.equals( this.javadoc, other.javadoc ) )
            {
                return false;
            }
            return true;
        }
        
        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }
        
        public String getDefaultValue()
        {
            return this.defaultValue;
        }
        
        public _annotations getAnnotations()
        {
            return this.annotations;
        }
        
        public _annotationProperty javadoc( String...commentLines )
        {
            this.javadoc = _javadoc.of( commentLines );
            return this;
        }
        
        public _annotationProperty javadoc( _javadoc _jd )
        {
            this.javadoc = _jd;
            return this;
        }
        
        public _annotationProperty setName( String name )
        {
            this.name = name;
            return this;
        }
        
        public _annotationProperty setType( String type )
        {
            this.type = type;
            return this;
        }
        
        public _annotationProperty setDefault( String defaultValue )
        {
            this.defaultValue = defaultValue;
            return this;
        }
        
        public static _annotationProperty of( Class type, String name )
        {
            return of( type.getCanonicalName(), name, null );
        }
        
        public static _annotationProperty of( String type, String name )
        {
            return of( type, name, null );
        }
        
        public static _annotationProperty of( 
            String type, String name, String defaultValue )
        {
            _annotationProperty _ap = new _annotationProperty();
            return _ap.setName( name ).setType( type ).setDefault( defaultValue );
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public Template getTemplate()
        {
            return PROPERTY;
        }
        
        @Override
        public Context getContext()
        {
            return VarContext.of(
                "javadoc", this.javadoc,
                "annotations", this.annotations,
                "name", this.name, 
                "type", this.type, 
                "defaultValue", this.defaultValue );
        }
        
        @Override
        public String author( Directive... directives )
        {   
            return Author.toString( PROPERTY, 
                getContext(),
                directives );
        }

        @Override
        public String toString()
        {
            return author();
        }
    
        @Override
        public _annotationProperty replace( String target, String replacement )
        {
            this.javadoc = this.javadoc.replace( target, replacement );
            this.annotations = this.annotations.replace( target, replacement );
            this.defaultValue = 
                RefRenamer.apply( this.defaultValue, target, replacement );
            this.name = 
                RefRenamer.apply( this.name, target, replacement );
            this.type =
                RefRenamer.apply( this.type, target, replacement );
            return this;
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }
    }    
}
