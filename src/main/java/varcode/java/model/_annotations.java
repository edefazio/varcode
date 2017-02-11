/*
 * Copyright 2016 M. Eric DeFazio
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.java.model._Java._facet;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * This represents the act of annotating within a Java Class File
 * with something like ({@code @Override}, {@code @SuppressWarnings}) 
 * 
 * This is NOT the "definition" of a new Annotation 
 * (which is _annotationDefinition)
 * 
 * NOTE: when there are more than one annotation 
 * they are displayed one per line:
 * <PRE>
 * @Path("/{id}")
 * @GET
 * Response findById( int id );
 * </PRE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _annotations
    implements _Java, _facet, Countable, Authored
{   
    
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }
    
    public _annotations( _annotations prototype )
    {
        this.inlineStyle = prototype.inlineStyle;
        for( int i = 0; i < prototype.count(); i++ )
        {
            Object o = prototype.getAt( i );
            if( o instanceof _annotation )
            {
                add(_annotation.cloneOf( (_annotation)o ) );
            }
            else if( o instanceof String )
            {
                add( ((String)o) + "" );
            }
            else
            {
                add( o.toString() );
            }
        }               
    }
    
    public static _annotations cloneOf( _annotations annotations )
    {
        return new _annotations( annotations );
    }
    
    
    public List<Object> getList()
    {
        return this.listOfAnnotations;
    }
     
    /** 
     * a List of Annotations, to be represented 
     * 
     * Note: this is an Object array, to support
     * accept a String representation of an annotation  
     * "@Path(\"/{id}\")"
     * -or-
     * an _annotation
     * _annotation.of("@Path", "
     * 
     */
    private List<Object> listOfAnnotations  = new ArrayList<Object>();
    
    public static final Template ANNOTATION_LIST = 
        BindML.compile( "{{+:{+annotation+}" + N + "+}}" );
    
    /** INLINE annotation Style (i.e." 
     *  public int doIt( @Deprecated @SafeVarArgs String... names )
     */
    public static final Template ANNOTATION_INLINE = 
        BindML.compile( "{{+:{+annotation+} +}}" );
    
    private final boolean inlineStyle;
    
    public _annotations( )
    {
        this( new ArrayList<Object>(), false );
    }
    
    /** 
     * Remove the exact annotation that evaluates to this toString
     * @param ann 
     * @return the modified _annotations (without "ann")
     * @throws ModelException if there is no annotation that represents "ann"
     */
    public _annotations remove( String ann )
        throws ModelException 
    {
        for( int i = 0; i < this.listOfAnnotations.size(); i++ )
        {
            if( this.listOfAnnotations.get( i ).toString().equals( ann ) )
            {
                this.listOfAnnotations.remove( i );
                return this;
            }
        }
        throw new ModelException( 
            "Unable to find annotation that evaluates to \""+ ann + "\"" );
    }
    
    public boolean contains( String ann )
    {
        for( int i = 0; i < this.listOfAnnotations.size(); i++ )
        {            
            if( this.listOfAnnotations.get( i ).toString().trim().equals( ann ) )
            {
                return true;
            }
        }
        return false;
    }
    
    public _annotations( boolean inline )
    {
        this( new ArrayList<Object>(), inline );
    }
    
    public _annotations( List<Object> annotations, boolean inlineStyle )
    {
        this.listOfAnnotations = annotations;
        this.inlineStyle = inlineStyle;
    }
    
    public _annotations removeAt( int annotationIndex )
    {
        if( this.listOfAnnotations.size() > annotationIndex )
        {
            this.listOfAnnotations.remove( annotationIndex );
            System.out.println( this.listOfAnnotations );
            return this;
        }
        throw new ModelException(
            "No annotation at index [" + annotationIndex + "]");
    }
    
    public _annotations clear()
    {
        this.listOfAnnotations.clear();   
        return this;
    }
    
    public Context getContext()
    {
        return VarContext.of( "annotation", listOfAnnotations );
    }    
            
    public final _annotations add( Object...annotations )
    {
        for( int i = 0; i < annotations.length; i++ )
        {
            Object ann = annotations[ i ];
            if( ann instanceof Class )
            {
                this.listOfAnnotations.add( "@" + ((Class) ann).getCanonicalName() );
            }
            else
            {
                this.listOfAnnotations.add( ann.toString().trim() );
            }
            //this.listOfAnnotations.addAll( Arrays.asList( annotations ) );
        }
        return this;
    }
    
    /** 
     * gets the annotation at a index
     * @param index the index of the annotation
     * @return the annotation
     * @throw VarException if invalid index
     */
    public Object getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return this.listOfAnnotations.get( index ); 
        }
        throw new ModelException( "Could not get annotation at [" + index + "]" );                
    }
    
    @Override
    public _annotations replace( String target, String replacement )
    {
        List<Object> repList = new ArrayList<Object>();
        for( int i = 0; i < listOfAnnotations.size(); i++ )
        {
            Object o = listOfAnnotations.get( i );
            if( o instanceof String )
            {
                //repList.add( ((String)o).replace( target, replacement ) );
                repList.add( 
                    RefRenamer.apply( (String)o, target, replacement ) );
            }
            else if( o instanceof _Java )
            {
                repList.add(( (_Java)o ).replace( target, replacement ) );
            }
            else
            {
                repList.add( 
                    RefRenamer.apply( o.toString(), target, replacement ) );
                //repList.add( o.toString().replace( target, replacement ) );
            }
        }
        this.listOfAnnotations = repList;
        return this;
    }
    
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );        
    }
    
    @Override
    public Template getTemplate()
    {
        if( isEmpty() )
        {
            return Template.EMPTY;
        }
        if( inlineStyle )
        {
            return ANNOTATION_INLINE;    
        }
        return ANNOTATION_LIST;
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
    
    public boolean isEmpty()
    {
        return this.listOfAnnotations.isEmpty();
    }
        
    @Override
    public int hashCode()
    {
        return Objects.hash( this.author().trim() );
        //return Objects.hash( listOfAnnotations, inlineStyle );
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
        final _annotations other = (_annotations)obj;
        if( this.inlineStyle != other.inlineStyle )
        {
            return false;
        }
        String thisAnn = this.author().trim();
        String otherAnn = other.author().trim();
        
        
        return thisAnn.equals( otherAnn );
    }
        
    public List<Object> getAnnotations()
    {
        return this.listOfAnnotations;
    }
    
    public int count()
    {
        return this.listOfAnnotations.size();
    }

    public void setAt( int index, String apply )
    {
        this.listOfAnnotations.set( index, apply );
    }
    
    /**
     * <PRE>
     * @Entity(tableName = "vehicles")
     *         ^^^^^^^^^^^^^^^^^^^^^^
     * (1) annotation attribute
     * 
     * @Entity(tableName = "vehicles", primaryKey = "id")
     *         ^^^^^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^
     * (2) annotation attributes
     * </PRE>
     * 
     */
    public static class _attributes
        implements _Java, Countable, Authored
    {        
        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }
    
        public static _attributes of( Object... nameValues )
        {
            if( nameValues.length == 1 )
            {
                return new _attributes( null, nameValues[ 0 ] );
            }             
            return new _attributes( nameValues );
        }
        
        
        public static _attributes cloneOf( _attributes prototype )
        {
            return new _attributes( prototype );
        }
        
        private final List<Object>names;
        private final List<Object>values;

        public _attributes()
        {            
            this.names = new ArrayList<Object>();
            this.values = new ArrayList<Object>();            
        }
                
        
        @Override
        public int hashCode()
        {
            return Objects.hash( names, values );
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
            final _attributes other = (_attributes)obj;
            if( !Objects.equals( this.names, other.names ) )
            {
                return false;
            }
            if( !Objects.equals( this.values, other.values ) )
            {
                return false;
            }
            return true;
        }
        
        @Override
        public int count()
        {
            return names.size();
        }
        
        @Override
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        public Object[] getAt( int index )
        {
            if( index < count() && index >= 0 )
            {
                return new Object[]{ 
                    this.names.get( index ),
                    this.values.get( index )};
            }
            throw new ModelException( "invalid index [" + index + "]" );
        }
        
        public _attributes( _attributes prototype )
        {
            this.names = new ArrayList<Object>();
            this.values = new ArrayList<Object>();
            for( int i = 0; i < prototype.names.size(); i++ )
            {
                this.names.add( prototype.names.get( i ) );
            }
            for( int i = 0; i < prototype.values.size(); i++ )
            {
                this.values.add( prototype.values.get( i ) );
            }
        }
        
        public _attributes( Object... nameValues )
        {
            this.names = new ArrayList<Object>();
            this.values = new ArrayList<Object>();
            
            add( nameValues );            
        }
        
        public final _attributes add( Object ... nameValues )
        {
            if( nameValues.length == 1 )
            {
                this.names.add( null );
                this.values.add( nameValues[ 0 ] );
                return this;
            }
            if( nameValues.length % 2 != 0 )
            {
                throw new ModelException(
                    "nameValues must be passed in as pairs" );
            }
            
            for( int i = 0; i < (nameValues.length / 2); i++ )
            {
                this.names.add( nameValues[ i * 2 ] );
                if( ! (nameValues[ (i * 2) + 1 ] instanceof String ) )
                {
                    this.values.add( _literal.of( nameValues[ (i * 2) + 1 ] ) );
                }
                else
                {
                    this.values.add( nameValues[ (i * 2) + 1 ]  );
                }
            }
            return this;
        }
        
        private static Object rep( 
            Object source, String target, String replacement )
        {
            if( source == null )
            {
                return null;
            }
            if( source instanceof _annotation )
            {
                return ((_annotation)source).replace( target, replacement );
                //return ((_annotation)source).replace( target, replacement );
            }
            if( source instanceof String )
            {
                return RefRenamer.apply((String)source, target, replacement );
                //return ((String)source).replace(target, replacement);
            }
            return RefRenamer.apply( source.toString(), target, replacement );
            //return source.toString().replace( target, replacement );
        }
        
        @Override
        public _attributes replace( String target, String replacement )
        {
            for( int i = 0; i < names.size(); i++ )
            {
                //names.set( i, names.get( i ).replace( target, replacement ) );
                //values.set( i, values.get( i ).replace( target, replacement ) );
                names.set( i, rep( names.get( i ), target, replacement ) );
                values.set( i, rep( values.get( i ), target, replacement ) );
            }
            return this;
        }

        /** Single Annotation attribute value without name: 
         * @Table("value") as apposed to @Table(name = "value")
         */
        public static final Template SINGLE_VALUE_ATTRIBUTE = 
            //BindML.compile( "\"{+value*+}\"" );
            BindML.compile( "{+value*+}" );
        
            
        public static final Template ATTRIBUTES = 
            BindML.compile( "{{+:{+name*+} = {+value*+}, +}}" );
        
        public Template getTemplate()
        {
            if( values.size() == 1 && names.get( 0 ) == null )
            {
                return SINGLE_VALUE_ATTRIBUTE;
            }
            return ATTRIBUTES;
        }
        
        public Context getContext()
        {
             return VarContext.of( "name", names, "value", values );
        }
        
        @Override
        public String author( Directive... directives )
        {
            if( names.size() > 0 )
            {
                return Author.toString( getTemplate(), getContext(), directives);
                /*
                if( names.size() == 1 && names.get( 0 ) == null )
                {
                    return Author.toString(
                        SINGLE_VALUE_ATTRIBUTE, 
                        VarContext.of( "value", values ), 
                        directives );
                }
                return Author.toString(
                    ATTRIBUTES, 
                    VarContext.of( "name", names, "value", values ), 
                    directives );
                */
            }
            return "";
        }
        
        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
        public String toString()
        {
            return author();
        }        
    }
    
    /**
     * A single annotation
     */
    public static class _annotation
        implements _Java, _facet, Authored
    {   
        public static final _annotation SAFE_VARARGS = 
             _annotation.of( SafeVarargs.class );
        
        public static final _annotation FUNCTIONAL_INTERFACE = 
            _annotation.of( FunctionalInterface.class );
        
        public static _annotation DEPRECATED = 
            _annotation.of( Deprecated.class );
        
        public static _annotation OVERRIDE = 
            _annotation.of( Override.class );
        
        public static _annotation SUPPRESS_WARNINGS = 
            _annotation.of( SuppressWarnings.class );
                
        public static final Template PROPERTY_ARRAY_DOM = 
            BindML.compile( "{ {{+:\"{+properties+}\", +}} }" );
        
        //@SuppressWarnings( { "unchecked", "deprecation" } )
        public static _annotation suppressWarnings( String...values )
        {
            if( values != null == values.length > 0 )
            {
                return _annotation.of( 
                    SuppressWarnings.class,
                    Author.toString( PROPERTY_ARRAY_DOM, "properties", values ) );
            }
            else
            {
                return SUPPRESS_WARNINGS;
            }
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
            final _annotation other = (_annotation)obj;
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.attributes, other.attributes ) )
            {
                return false;
            }
            return true;
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( name, attributes );
        }
        
        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }
    
        /**
         * Builds and returns a @Generated(value= value) annotation
         * @param value the value of the value annotation
         * @return the annotation
         */
        public static _annotation generated( String value )
        {
            return _annotation.of( Generated.class, "value", "\"" + value + "\"" );
        }
        
        /** Create and return a clone of this annotation
         * @param prototype the prototype annotation
         * @return a new clone of the base annotation
         */
        public static _annotation cloneOf( _annotation prototype )
        {
            return new _annotation( prototype );
        }
        
        
        public static _annotation of( Object...tokens )
        {
            if( tokens.length == 0 )
            {
                throw new ModelException( "cannot create an empty annotation (no tokens)" );
            }
            _annotation ann = null;
            if( tokens[ 0 ] instanceof Class )
            {
                ann = new _annotation( "@"+ ((Class)tokens[ 0 ]).getCanonicalName() );
            }
            else
            {
                ann = new _annotation( tokens[ 0 ].toString().trim() );
            }
            //_annotation ann = new _annotation( tokens[ 0 ] );
            if( tokens.length > 1 )
            {
                Object[] attributes = new Object[ tokens.length -1 ];
                System.arraycopy( tokens, 1, attributes, 0, tokens.length - 1 );
                ann.attributes( attributes );
            }
            return ann;
        }
        
        /** A String starting with @ */
        private String name;
        
        private _attributes attributes = new _attributes();
        
        public static final Template ANNOTATION = 
            BindML.compile( "{{+?annotation:{+annotation+} +}}" );
        
        public static final Template ANNOTATION_ATTRIBUTES = 
            BindML.compile( "{+annotation+}{{+?attributes:({+attributes+}) +}}" );
        
        private _annotation()
        {            
        }
        
        public _annotation( String annotation )
        {
            if( annotation != null )
            {
                if( annotation.toString().startsWith( "@" ) )
                {
                    this.name = annotation.toString();
                }
                else
                {
                    this.name = "@" + annotation.toString();
                }
            }            
        }
        
        public _annotation( _annotation prototype )
        {
            this.name = prototype.name;
            this.attributes = _attributes.cloneOf( prototype.attributes );
        }

        public boolean isEmpty()
        {
            return name == null;
        }
        
        public String getAnnotation()
        {
            return this.name;
        }
        
        @Override
        public _annotation replace( String target, String replacement )
        {
            if( this.name != null )
            {
                this.name = 
                    RefRenamer.apply( name, target, replacement );
                    //name.replace( target, replacement );
            }
            if( this.attributes != null )
            {
                this.attributes.replace( target, replacement );
            }
            return this;            
        }

        public _annotation attribute( Object name, Object value )
        {
            this.attributes.add( name, value );
            return this;
        }
        
        public _annotation attributes( Object...attributes )
        {            
            this.attributes.add( attributes );
            return this;
        }
        
        public Context getContext()
        {
            if( this.name == null )
            {
                return VarContext.of();
            }
            if( this.attributes == null || this.attributes.isEmpty() )
            {
                return VarContext.of("annotation", name );
            }
            return 
                VarContext.of("annotation", name, "attributes", attributes ); 
        }
        
        public static final Template EMPTY = BindML.compile("");
        
        public Template getTemplate()
        {
            if( this.name == null )
            {
                return EMPTY;
            }
            if( this.attributes == null || this.attributes.isEmpty() )
            {
                return ANNOTATION;
            }
            return ANNOTATION_ATTRIBUTES;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( getTemplate(), getContext(), directives );
        }
        
        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
        public String toString()
        {
            if( this.isEmpty() )
            {
                return "";
            }
            return author( );
        }
    }
}        
