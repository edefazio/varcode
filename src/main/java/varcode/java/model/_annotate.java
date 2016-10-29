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
package varcode.java.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

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
public class _annotate
    implements Model
{    
    public static _annotate cloneOf( _annotate annotations )
    {
        List<Object> clone = new ArrayList<Object>();
        for( int i = 0; i < annotations.count(); i++ )
        {
            Object o = annotations.getAt( i );
            if( o instanceof _annotation )
            {
                clone.add(_annotation.cloneOf( (_annotation)o ) );
            }
            else if( o instanceof String )
            {
                clone.add( ((String)o) + "" );
            }
            else
            {
                clone.add( o.toString() );
            }
        }        
        return new _annotate( clone );
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
    private List<Object> listOfAnnotations;
    
    public static final Dom ANNOTATION_LIST = 
        BindML.compile( "{{+:{+annotation+}" + N + "+}}" );
    
    public _annotate( )
    {
        this( new ArrayList<Object>() );
    }
    
    public _annotate( List<Object> annotations )
    {
        this.listOfAnnotations = new ArrayList<Object>();
    }
    
    public VarContext getContext()
    {
        return VarContext.of( "annotation", listOfAnnotations );
    }    
        
    @Override
    public _annotate bind( VarContext context )
    {
        for( int i = 0; i < this.listOfAnnotations.size(); i++ )
        {
            Object thisAnn = this.listOfAnnotations.get( i );
            if( thisAnn instanceof Model )
            {
                this.listOfAnnotations.set(i, 
                    ((Model)thisAnn).bind( context ) );
            }
            else if( thisAnn instanceof String )
            {
                thisAnn = Compose.asString( BindML.compile( (String)thisAnn), context );
                this.listOfAnnotations.set( i, thisAnn );
            }
            //otherwise... dont bother            
        }
        return this;
    }
    
    public _annotate add( Object...annotations )
    {
        this.listOfAnnotations.addAll( Arrays.asList( annotations ) );
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
        throw new VarException( "Could not get annotation at [" + index + "]" );                
    }
    
    @Override
    public _annotate replace( String target, String replacement )
    {
        List<Object> repList = new ArrayList<Object>();
        for( int i = 0; i < listOfAnnotations.size(); i++ )
        {
            Object o = listOfAnnotations.get( i );
            if( o instanceof String )
            {
                repList.add( ((String)o).replace( target, replacement ) );
            }
            else if( o instanceof Model )
            {
                repList.add(((Model )o).replace( target, replacement ) );
            }
            else
            {
                repList.add( o.toString().replace( target, replacement ) );
            }
        }
        this.listOfAnnotations = repList;
        return this;
    }
    
    @Override
    public String author( Directive... directives )
    {
        if( ! isEmpty() )
        {
            return Compose.asString( ANNOTATION_LIST, getContext(), directives );
        }
        return "";
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
        
    public List<Object> getAnnotations()
    {
        return this.listOfAnnotations;
    }
    
    public int count()
    {
        return this.listOfAnnotations.size();
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
        implements Model
    {        
        public static _attributes of( Object... nameValues )
        {
            if( nameValues.length == 1 )
            {
                return new _attributes( null, nameValues[ 0 ] );
            }             
            return new _attributes( nameValues );
        }
        
        private final List<Object>names;
        private final List<Object>values;

        public _attributes()
        {            
            this.names = new ArrayList<Object>();
            this.values = new ArrayList<Object>();            
        }
        
        @Override
        public _attributes bind( VarContext context )
        {
            for( int i = 0; i < names.size(); i++ )
            {
                Object thisName = names.get( i );
                if( thisName instanceof Model  )
                {
                    names.set(i , ((Model) thisName).bind( context ) );
                }
                else if( thisName instanceof String )
                {
                    names.set(i, 
                        Compose.asString( BindML.compile( (String)thisName), context ) );
                }   
            }
            for( int i = 0; i < values.size(); i++ )
            {
                Object thisValue = values.get( i );
                if( thisValue instanceof Model )
                {
                    values.set(i, ((Model) thisValue).bind( context ) );
                }
                else if( thisValue instanceof String )
                {
                    values.set(i, 
                        Compose.asString( BindML.compile((String)thisValue), context ) );
                }   
            }
            return this;
        }
        
        public void bindTo( VarContext context )
        {
            for( int i = 0; i < names.size(); i++ )
            {            
                Object thisName = names.get( i );
                Object thisValue = values.get( i );
                if( thisName instanceof Model )
                {
                    names.set(i , ((Model)thisName ).bind( context ) );                                        
                }
                else if( thisName instanceof String )
                {
                    names.set(i, 
                        Compose.asString( BindML.compile((String)thisName), context ) );
                }
                if( thisValue instanceof Model )
                {
                    values.set(i , ((Model) thisValue).bind( context ) );                                        
                }
                else if( thisValue instanceof String )
                {
                    values.set(i, 
                        Compose.asString( BindML.compile((String)thisValue ), context ) );
                }
            }
        }
         
        public int count()
        {
            return names.size();
        }
        
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        public _attributes( Object... nameValues )
        {
            this.names = new ArrayList<Object>();
            this.values = new ArrayList<Object>();
            
            add( nameValues );            
        }
        
        public final _attributes add( Object ... nameValues )
        {
            if( nameValues.length % 2 != 0 )
            {
                throw new VarException(
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
            }
            if( source instanceof String )
            {
                return ((String)source).replace(target, replacement);
            }
            return source.toString().replace( target, replacement );
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
        public static final Dom SINGLE_VALUE_ATTRIBUTE = 
            BindML.compile( "\"{+value*+}\"" );
            
        public static final Dom ATTRIBUTES = 
            BindML.compile( "{{+:{+name*+} = {+value*+}, +}}" );
        
        @Override
        public String author( Directive... directives )
        {
            if( names.size() > 0 )
            {
                if( names.size() == 1 && names.get( 0 ) == null )
                {
                    return Compose.asString(
                        SINGLE_VALUE_ATTRIBUTE, 
                        VarContext.of( "value", values ), 
                        directives );
                }
                return Compose.asString(
                    ATTRIBUTES, 
                    VarContext.of( "name", names, "value", values ), 
                    directives );
            }
            return "";
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
        implements Model
    {        
        /** Create and return a clone of this annotation
         * @param prototype the prototype annotation
         * @return a new clone of the base annotation
         */
        public static _annotation cloneOf( _annotation prototype )
        {
            _annotation ann = new _annotation( prototype.annotation + "" );
            ann.attributes(prototype.attributes );            
            return ann;
        }
        
        @Override
        public _annotation bind( VarContext context )
        {
            if( this.annotation != null )
            {
                this.annotation = Compose.asString( BindML.compile( this.annotation ), context );            
            }
            if (this.attributes != null && !attributes.isEmpty() )
            {
                this.attributes.bindTo( context ); 
            }
            return this;
        }
        
        public static _annotation of( Object...tokens )
        {
            if( tokens.length == 0 )
            {
                return new _annotation( null );
            }
            _annotation ann = new _annotation( tokens[ 0 ] );
            if( tokens.length > 1 )
            {
                Object[] attributes = new Object[ tokens.length -1 ];
                System.arraycopy( tokens, 1, attributes, 0, tokens.length - 1 );
                ann.attributes( attributes );
            }
            return ann;
        }
        
        /** A String starting with @ */
        private String annotation;
        
        private _attributes attributes;
        
        public static final Dom ANNOTATION = 
            BindML.compile( "{{+?annotation:{+annotation+} +}}" );
        
        public static final Dom ANNOTATION_ATTRIBUTES = 
            BindML.compile( "{+annotation+}{{+?attributes:({+attributes+}) +}}" );
        
        public _annotation( Object annotation )
        {
            if( annotation != null )
            {
                if( annotation.toString().startsWith( "@" ) )
                {
                    this.annotation = annotation.toString();
                }
                else
                {
                    this.annotation = "@" + annotation.toString();
                }
            }            
        }

        public boolean isEmpty()
        {
            return annotation == null;
        }
        
        public String getAnnotation()
        {
            return this.annotation;
        }
        
        @Override
        public _annotation replace( String target, String replacement )
        {
            if( this.annotation != null )
            {
                this.annotation = annotation.replace( target, replacement );
            }
            if( this.attributes != null )
            {
                this.attributes.replace( target, replacement );
            }
            return this;            
        }

        public _annotation attributes( Object...attributes )
        {
            this.attributes = _attributes.of( attributes );
            return this;
        }
        
        @Override
        public String author( Directive... directives )
        {
            if( this.annotation == null )
            {
                return "";
            }
            if( this.attributes == null || this.attributes.isEmpty() )
            {
                return Compose.asString( ANNOTATION, 
                    VarContext.of( "annotation", annotation ), directives );
            }
            return Compose.asString( ANNOTATION_ATTRIBUTES, 
                VarContext.of( 
                    "annotation", annotation, "attributes", attributes ), 
                directives ); 
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
