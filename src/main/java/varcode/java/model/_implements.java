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
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.lang.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.java.model._Java._facet;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * Model for list of implementers of Classes and Enums
 * 
 * @author M. Eric DeFazio
 */
public class _implements
    implements _Java, _facet, Countable, Authored
{
    public _implements( _implements prototype )
    {
        if( prototype != null )
        {
            for( int i = 0; i < prototype.count(); i++ )
            {
                impls.add( prototype.getAt( i ) );
            }
        }
    }
    
    public static _implements cloneOf( _implements prototype )
    {
        return new _implements( prototype );        
    }

    private List<String> impls = new ArrayList<String>();

    public static Template IMPLEMENTS = BindML.compile(        
        "{+?implements:" + N
      + "    implements +}{{+:{+implements+}, +}}" );

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _implements()
    {
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( impls );
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
        final _implements other = (_implements)obj;
        if( !Objects.equals( this.impls, other.impls ) )
        {
            return false;
        }
        return true;
    }

    public _implements implement( Class... interfaceClass )
    {
        for( int i = 0; i < interfaceClass.length; i++ )
        {
            impls.add( interfaceClass[ i ].getCanonicalName() );
        }
        return this;
    }

    public List<String> getList()
    {
        return impls;
    }
    
    public _implements implement( String... interfaceClass )
    {
        for( int i = 0; i < interfaceClass.length; i++ )
        {
            impls.add( interfaceClass[ i ] );
        }
        return this;
    }

    public int count()
    {
        return impls.size();
    }

    public boolean isEmpty()
    {
        return count() == 0;
    }

    public boolean contains( String interfaceClassName )
    {
        return this.impls.contains( interfaceClassName );
    }

    public boolean contains( Class interfaceClass )
    {
        return contains( interfaceClass.getCanonicalName() );
    }

    @Override
    public _implements replace( String target, String replacement )
    {
        List<String> replaced = new ArrayList<String>();

        for( int i = 0; i < impls.size(); i++ )
        {
            replaced.add(
                RefRenamer.apply( this.impls.get( i ), target, replacement ) );
        }
        this.impls = replaced;
        return this;
    }

    public static _implements of( Class... classes )
    {
        _implements impl = new _implements();
        for( int i = 0; i < classes.length; i++ )
        {
            impl.implement( classes[ i ] );
        }
        return impl;
    }

    public static _implements of( String... tokens )
    {
        _implements impl = new _implements();
        for( int i = 0; i < tokens.length; i++ )
        {
            impl.implement( tokens[ i ] );
        }
        return impl;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Context getContext()
    {
        return VarContext.of( "implements", impls );
    }
    
    @Override
    public Template getTemplate()
    {
        return IMPLEMENTS;
    }
    
    @Override
    public String author( Directive... directives )
    {        
        return Author.toString( IMPLEMENTS, getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }

    public String getAt( int index )
    {
        if( index > impls.size() - 1 )
        {
            throw new ModelException(
                "index [" + index + "] is outside of implements range [0..."
                + (impls.size() - 1) + "]" );
        }
        return impls.get( index );
    }
}
