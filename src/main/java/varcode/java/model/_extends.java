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
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * extends keyword used on Classes and interfaces
 * Allows multiple extension (for interfaces)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _extends
    implements _Java, Countable, Authored
{
    public static final _extends NONE = new _extends();

    private List<String> extendsFrom = new ArrayList<String>();

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    public _extends( _extends prototype )
    {
        this.extendsFrom.addAll( prototype.extendsFrom );
    }
    
    public static _extends cloneOf( _extends prototype )
    {
        return new _extends( prototype );        
    }

    @Override
    public _extends replace( String target, String replacement )
    {
        List<String> replaced = new ArrayList<String>();
        for( int i = 0; i < extendsFrom.size(); i++ )
        {
            replaced.add(
                RefRenamer.apply( extendsFrom.get( i ), target, replacement ) );            
        }
        this.extendsFrom = replaced;
        return this;
    }

    public List<String> getList()
    {
        return this.extendsFrom;
    }
    
    public static Template EXTENDS = BindML.compile(
        "{+?extends:" + N
      + "    extends +}{{+:{+extends+}, +}}" );

    public _extends()
    {
    }

    public int count()
    {
        return extendsFrom.size();
    }

    public boolean isEmpty()
    {
        return count() == 0;
    }

    public String getAt( int index )
    {
        if( index > count() - 1 )
        {
            throw new ModelException(
                "extends[" + index + "] is out of range[0..." + (count() - 1) + "]" );
        }
        return extendsFrom.get( index );
    }

    public _extends( String extendsFromClass )
    {
        extendsFrom.add( extendsFromClass );
    }

    public _extends addExtends( Class classToExtendFrom )
    {
        extendsFrom.add( classToExtendFrom.getCanonicalName() );
        return this;
    }

    public _extends addExtends( String extendClass )
    {
        extendsFrom.add( extendClass );
        return this;
    }

    public static _extends of( String... tokens )
    {
        _extends xtends = new _extends();
        for( int i = 0; i < tokens.length; i++ )
        {
            xtends.addExtends( tokens[ i ] );
        }
        return xtends;
    }

    public _extends clear()
    {
        this.extendsFrom.clear();
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
        return VarContext.of( "extends", extendsFrom );
    }
    
    @Override
    public Template getTemplate()
    {
        return EXTENDS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        
        return Author.toString( EXTENDS, getContext(), directives );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( extendsFrom );
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
        final _extends other = (_extends)obj;
        if( !Objects.equals( this.extendsFrom, other.extendsFrom ) )
        {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        return author();
    }
}
