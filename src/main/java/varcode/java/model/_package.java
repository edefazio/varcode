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

import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.naming.RefRenamer;
import varcode.java.model._Java.Authored;
import varcode.markup.bindml.BindML;

/**
 * package representation
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _package
    implements _Java, Authored
{
    public static final Template PACKAGE
        = BindML.compile(
            "{{+?name:package {+name+};" + N + N
            + "+}}" );

    public _package( _package prototype )
    {
        if( prototype != null )
        {
            this.name = prototype.name;
        }        
    }
    
    public static _package cloneOf( _package prototype )
    {
        return of( prototype.name );
    }

    public static _package of( Object packageName )
    {
        return new _package( packageName );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name );
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
        final _package other = (_package)obj;
        if( !Objects.equals( this.name, other.name ) )
        {
            return false;
        }
        return true;
    }
        
    public static _package of( String packageName )
    {
        if( packageName == null )
        {
            return new _package( null );
        }
        if( packageName.startsWith( "package " ) )
        {
            packageName = packageName.substring( "package ".length() );
        }
        if( packageName.endsWith( ";" ) )
        {
            packageName = packageName.substring( 0, packageName.length() - 1 );
        }
        return new _package( packageName );
    }

    private String name;

    public boolean isEmpty()
    {
        return name == null || name.trim().length() == 0;
    }

    /**
     * Gets the package name (in canonical '.' form)
     *
     * @return the package name in canonical form
     */
    public String getName()
    {
        return name;
    }

    public _package( Object name )
    {
        if( name == null )
        {
            this.name = null;
        }
        else if( name instanceof _package )
        {
            this.name = ((_package)name).name;
        }
        else
        {
            this.name = name.toString();
        }
    }

    @Override
    public String toString()
    {
        return author();
    }

    @Override
    public _package replace( String target, String replacement )
    {
        if( this.name != null )
        {
            this.name = RefRenamer.apply( this.name, target, replacement );
            //this.name = this.name.replace( target, replacement );
        }
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
        return PACKAGE;
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of( "name", this.name );
    }
    @Override
    public String author( Directive... directives )
    {
        return Author.toString(
            PACKAGE,
            getContext(), 
            directives );
    }

}
