/*
 * Copyright 2017 Eric.
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
import varcode.ModelException;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarContext;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

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
public class _anns
    implements _Java, _Java._facet, _Java.Countable, _Java.Authored
{   
    
    @Override
    public void visit( _Java.ModelVisitor visitor )
    {
        visitor.visit(this);
    }
    
    public static _anns of( _ann...anns )
    {
        _anns _a = new _anns();
        _a.add( anns );
        return _a;
    }
    
    public static _anns of( String...anns )
    {
        _anns _a = new _anns();
        _a.add( anns );
        return _a;
    }   
    
    public _anns( _anns prototype )
    {
        this.inlineStyle = prototype.inlineStyle;
        for( int i = 0; i < prototype.count(); i++ )
        {
            add( new _ann( prototype.getAt( i ) ) );
        }               
    }
    
    /**
     * Gets ONLY ONE of the annotations that are of the specific class
     * or NULL if there is no annotation of this specific class
     * @param clazz the class of the annotation
     * @return a single annotation or NULL if there are more than one or 
     */
    public _ann getOne( Class clazz )
    {
        List<_ann> list = get( clazz );
        if( list.size() == 1 )
        {
            return list.get(0);
        }
        return null;
    }
    
    /**
     * Get all Annotations that are of the clazz
     * @param clazz
     * @return 
     */
    public List<_ann> get( Class clazz )
    {
        String simpleName = clazz.getSimpleName();
        List<_ann> byClass = new ArrayList<_ann>();
        for( int i = 0; i< this.listOfAnnotations.size(); i++ )
        {
            _ann _a = this.listOfAnnotations.get(i);
            if( _a.getName().equals( simpleName ) 
                || _a.getName().equals( clazz.getCanonicalName() ) )
            {
                byClass.add( _a );
            }
        }
        return byClass;
    }
    
    /**
     * Since I can support annotations as String or as _annotation,
     * just return the Stringified version (ALWAYS)
     * @param annotationName
     * @return 
     */
    public List<_ann> get( String annotationName )
    {
        List<_ann> named = new ArrayList<_ann>();
        for( int i = 0; i< this.listOfAnnotations.size(); i++ )
        {
            _ann _a = this.listOfAnnotations.get(i);
            if( _a.getName().equals( annotationName ) )
            {
                named.add( _a );
            }
        }
        return named;
    }
    
    public static _anns cloneOf( _anns annotations )
    {
        return new _anns( annotations );
    }
    
    
    public List<_ann> getList()
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
    private List<_ann> listOfAnnotations  = new ArrayList<_ann>();
    
    public static final Template ANNOTATION_LIST = 
        BindML.compile( "{{+:{+annotation+}" + N + "+}}" );
    
    /** INLINE annotation Style (i.e." 
     *  public int doIt( @Deprecated @SafeVarArgs String... names )
     */
    public static final Template ANNOTATION_INLINE = 
        BindML.compile( "{{+:{+annotation+} +}}" );
    
    private final boolean inlineStyle;
    
    public _anns( )
    {
        this( new ArrayList<_ann>(), false );
    }
    
    
    public _anns remove( List<_ann> toRemove )
    {
        for(int i=0; i< toRemove.size(); i++ )
        {
            this.listOfAnnotations.remove( toRemove.get( i ) );
        }
        return this;
    }
    public _anns remove( Class clazz )
    {
        remove( clazz.getSimpleName() );
        return remove( clazz.getCanonicalName() );
    }
    
    /** 
     * Remove the annotation with a given name
     * @param ann 
     * @return the modified _annotations (without "ann")
     * @throws ModelException if there is no annotation that represents "ann"
     */
    public _anns remove( String ann )
        throws ModelException 
    {
        for( int i = 0; i < this.listOfAnnotations.size(); i++ )
        {
            if( this.listOfAnnotations.get( i ).getName().equals( ann ) )
            {
                this.listOfAnnotations.remove( i );                
            } 
        }
        return this;
    }
    
    public boolean contains( Class ann )
    {
        String canon = ann.getCanonicalName();
        
        return contains( canon ) || contains( ann.getSimpleName() );
    }
    
    public boolean contains( String ann )
    {
        if( ann.startsWith( "@" ) )
        {
            ann = ann.substring( 1 );
        }
        for( int i = 0; i < this.listOfAnnotations.size(); i++ )
        {  
            _ann a = this.listOfAnnotations.get( i );
            if( a.getName().equals( ann ) )
            {
                return true;
            }
        }
        return false;
    }
    
    public _anns( boolean inline )
    {
        this( new ArrayList<_ann>(), inline );
    }
    
    public _anns( List<_ann> annotations, boolean inlineStyle )
    {
        this.listOfAnnotations = annotations;
        this.inlineStyle = inlineStyle;
    }
    
    public _anns removeAt( int annotationIndex )
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
    
    public _anns clear()
    {
        this.listOfAnnotations.clear();   
        return this;
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of( "annotation", listOfAnnotations );
    }    
    
    
    public final _anns add( String...annotations )
    {
        for( int i = 0; i < annotations.length; i++ )
        {
            this.listOfAnnotations.add( _ann.of(  annotations[ i ] ) );
        }        
        return this;
    }
    
    public final _anns add( _ann...annotations )
    {
        for( int i = 0; i < annotations.length; i++ )
        {
            this.listOfAnnotations.add( annotations[ i ] );
        }        
        return this;
    }
    
    /** 
     * gets the annotation at a index
     * @param index the index of the annotation
     * @return the annotation
     * @throw VarException if invalid index
     */
    @Override
    public _ann getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return this.listOfAnnotations.get( index ); 
        }
        throw new ModelException( "Could not get annotation at [" + index + "]" );                
    }
    
    @Override
    public _anns replace( String target, String replacement )
    {
        for( int i = 0; i < listOfAnnotations.size(); i++ )
        {
            _ann _a = listOfAnnotations.get( i );            
            _a.replace( target, replacement );
        }
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
    
    @Override
    public boolean isEmpty()
    {
        return this.listOfAnnotations.isEmpty();
    }
        
    @Override
    public int hashCode()
    {
        return Objects.hash( this.listOfAnnotations, this.inlineStyle );
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
        final _anns other = (_anns)obj;
        if( this.inlineStyle != other.inlineStyle )
        {
            return false;
        }
        String thisAnn = this.author().trim();
        String otherAnn = other.author().trim();
        
        return thisAnn.equals( otherAnn );
    }
        
    public List<_ann> getAnnotations()
    {
        return this.listOfAnnotations;
    }
    
    @Override
    public int count()
    {
        return this.listOfAnnotations.size();
    }

    public void setAt( int index, _ann ann )
    {
        this.listOfAnnotations.set( index, ann );
    }
}
