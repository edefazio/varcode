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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarContext;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * Components (classes, interfaces, enums) that are be nested within one another
 * (the root node is the "declaring" class):
 * <PRE>
 * public class A {
 *    public static class B{
 *    	  private interface I {
 *        }
 *    }
 *    public enum E {
 *    	;
 *    }
 * }
 * </PRE>
 * <A HREF="https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html">Nested
 * Classes</A>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _nests
    implements _Java, Countable, Authored
{
    //all nested components of a declaring class (_class, _enum, _interface)
    public List<_model> models = new ArrayList<_model>();

    public static _nests of( _model... _ms )
    {
        _nests _ns = new _nests();
        for( int i = 0; i < _ms.length; i++ )
        {
            _ns.add( _ms[ i ] );
        }
        return _ns;
    }

    public _nests()
    {
    }

    public List<_model> getList()
    {
        return this.models;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash( models );
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
        final _nests other = (_nests)obj;
        if( !Objects.equals( this.models, other.models ) )
        {
            return false;
        }
        return true;
    }
        
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    public _model getByName( String name )
    {
        for( int i = 0; i < models.size(); i++ )
        {
            if( models.get( i ).getName().equals( name ) )
            {
                return models.get( i );
            }
        }
        return null;
    }

    public _model getAt( int index )
    {
        if( index >= 0 && index < models.size() )
        {
            return models.get( index );
        }
        throw new ModelException(
            " index [" + index + "] not in range [0..." + (models.size() - 1) + "]" );
    }

    public _nests add( _model...models )
    {
        for( int i = 0; i < models.length; i++ )
        {
            add( models[ i ] );
        }
        return this;
    }
    
    public _nests add( _model model )
    {
        //first verify that no other model has the same name
        for( int i = 0; i < this.models.size(); i++ )
        {
            if( this.models.get( i ).getName().equals(model.getName() ) )
            {
                throw new ModelException(
                    "cannot add nested with name \"" + model.getName()
                    + "\" a component with that name already exists" );
            }
        }
        this.models.add( model );
        return this;
    }

    public _nests remove( List<_model> modelsToRemove )
    {
        for( int i = 0; i < modelsToRemove.size(); i++ )
        {
            models.remove( modelsToRemove.get( i ) );
        }
        return this;
    }
    
    public _nests removeByName( String name )
    {
        for( int i = 0; i < this.models.size(); i++ )
        {            
            if( models.get( i ).getName().equals( name ) )
            {
                remove( models.get( i ) );
                return this;
            }            
        }
        throw new ModelException(
            "Unable to remove nested by name \"" + name + "\"" );
    }
    public _nests remove( _model model )
    {
        models.remove( model );
        return this;
    }
    
    @Override
    public _nests replace( String target, String replacement )
    {
        for( int i = 0; i < models.size(); i++ )
        {
            this.models.get( i ).replace( target, replacement );
        }
        return this;
    }

    @Override
    public int count()
    {
        return models.size();
    }

    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    public static final Template EMPTY = BindML.compile( 
        "" );
    
    public static final Template NESTS = BindML.compile( 
        "{{+:" + N + "{+models+}" + N + "+}}" );
    
    @Override
    public Context getContext()
    {
        return VarContext.of( "models", this.models );
    }
    
    @Override
    public Template getTemplate()
    {
        if( this.models.isEmpty() )
        {
            return EMPTY;
        }
        return NESTS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        String s = Author.toString( getTemplate(), getContext(), directives );
        return s;     
    }

    @Override
    public String toString()
    {
        return author();
    }

    public _nests( _nests prototype )
    {
        if( prototype != null )
        {
            for( int i = 0; i < prototype.count(); i++ )
            {
                _model thisComp = prototype.models.get( i );
                if( thisComp instanceof _class )
                {
                    models.add( _class.cloneOf( (_class)thisComp ) );
                }
                else if( thisComp instanceof _enum )
                {
                    models.add( _enum.cloneOf( (_enum)thisComp ) );
                }
                else if( thisComp instanceof _interface )
                {
                    models.add( _interface.cloneOf( (_interface)thisComp ) );
                }
                else if( thisComp instanceof _annotationType )
                {
                    models.add( _annotationType.cloneOf( (_annotationType) thisComp ) );
                }
                else
                {
                    throw new ModelException(
                        "unknown nested model "
                        + thisComp
                        + "; expected _class, _enum, _interface " );
                }            
            }
        }
    }
    public static _nests cloneOf( _nests nests )
    {
        return new _nests( nests );        
    }
}
