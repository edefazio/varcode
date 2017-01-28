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
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * NOTE: this COULD get out of control with all the Nonsense inside of Generics
 * (i.e. ? super etc.)

 FOR EXAMPLE (compile Hacker News https://news.ycombinator.com/item?id=11777133 )

 interface Z {} interface N<x> {} interface L<x> {} interface Qlr<x> {}
 * interface Qrl<x> {} interface E<x> extends Qlr<N<?super Qr<?super E<?super
 * E<?super x>>>>>, Qrl<N<?super Ql<?super E<?super E<?super x>>>>> {} interface
 * Ql<x> extends L<N<?super Ql<?super L<?super N<?super x>>>>>, E<Qlr<?super
 * N<?super x>>> {} interface Qr<x> extends L<N<?super Qr<?super L<?super
 * N<?super x>>>>>, E<Qrl<?super N<?super x>>> {} class Main { L<?super N<?super
 * L<?super N<?super L<?super N<?super E<?super E<?super Z>>>>>>>> doit( Qr<?
 * super E<? super E<? super Z>>> v ){ return v; } }
 *
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _generic
{

    /**
     *
     * Represents Type Parameters inside the <,>s
     *
     * The stuff inside the < > 's (i.e. the "K", "V" inside Map<K, V>
     */
    public static class _typeParams
        implements _Java._facet,  Countable, Authored
    {
        private List<String> params = new ArrayList<String>();

        public static final Template TYPE_PARAMS = BindML.compile(
            "<{{+:{+params+}, +}}> " );

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit(this);
        }

        @Override
        public int count()
        {
            return params.size();
        }

        @Override
        public boolean isEmpty()
        {
            return count() == 0;
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash( params );
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
            final _typeParams other = (_typeParams)obj;
            if( !Objects.equals( this.params, other.params ) )
            {
                return false;
            }
            return true;
        }
        
        public _typeParams setAt( int index, String value )
        {
            if( index < 0 || index > params.size() - 1 )
            {
                throw new ModelException(
                    "Unable to get params at [" + index + "]" );
            }
            params.set( index, value );
            return this;
        }
        
        public String getAt( int index )
        {
            if( index < 0 || index > params.size() - 1 )
            {
                throw new ModelException(
                    "Unable to get params at [" + index + "]" );
            }
            return params.get( index );
        }

        public boolean contains( String name )
        {
            for( int i = 0; i < this.params.size(); i++ )
            {
                if( params.get( i ).equals( name ) )
                {
                    return true;
                }
            }
            return false;
        }

        public static _typeParams of( String... params )
        {
            _typeParams _tp = new _typeParams();
            _tp.addParams( params );
            return _tp;
        }

        public _typeParams( _typeParams prototype )
        {
            if( prototype != null )
            {
                this.params.addAll( prototype.params );
            }
        }
        
        public static _typeParams cloneOf( _typeParams prototype )
        {
            return new _typeParams( prototype );            
        }

        public _typeParams()
        {
            params = new ArrayList<String>();
        }

        public _typeParams addParam( String param )
        {
            params.add( param );
            return this;
        }

        public _typeParams addParams( String... params )
        {
            for( int i = 0; i < params.length; i++ )
            {
                addParam( params[ i ] );
            }
            return this;
        }

        public _typeParams replace( String target, String replacement )
        {
            for( int i = 0; i < this.params.size(); i++ )
            {
                this.params.set( i,
                    RefRenamer.apply( this.params.get( i ), target, replacement ) );
                    //this.params.get( i ).replace( target, replacement ) );
            }
            return this;
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public Context getContext()
        {
            return VarContext.of( "params", this.params );
        }

        @Override
        public String toString()
        {
            return author();
        }

        @Override
        public Template getTemplate()
        {
            if( this.params.isEmpty() )
            {
                return Template.EMPTY;
            }
            return TYPE_PARAMS;
        }
        @Override
        public String author( Directive... directives )
        {
            return Author.toString( getTemplate(), getContext(), directives );
        }
    }

}
