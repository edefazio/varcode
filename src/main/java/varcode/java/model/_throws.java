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
 * Declaration on a method the throwing of exception
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _throws
    implements _Java, Countable, Authored
{
    public static final _throws NONE = new _throws();

    private List<String> throwsException = new ArrayList<String>();

    public static _throws cloneOf( _throws prototype )
    {
        return new _throws( prototype );
    }
    
    public _throws( _throws prototype )
    {
        if( prototype != null )
        {
            this.throwsException.addAll( prototype.throwsException );
        }
    }

    public List<String> getList()
    {
        return throwsException;
    }
    
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public static Template THROWS = BindML.compile(
        "{+?throwsException:" + N + 
        "    throws +}{+throwsException+}" ); 
    

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( throwsException );
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
        final _throws other = (_throws)obj;
        if( !Objects.equals( this.throwsException, other.throwsException ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public Context getContext()
    {
        return VarContext.of( "throwsException", throwsException );
    }
    
    @Override
    public Template getTemplate()
    {
        return THROWS;
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString( THROWS, getContext(), directives );
    }

    public _throws setAt( int index, String value )
    {
        if( index < count() )
        {
            throwsException.set( index, value );
            return this;
        }
        throw new ModelException( " invalid index [" + index + "]" );
    }

    public String getAt( int index )
    {
        if( index < count() )
        {
            return throwsException.get( index );
        }
        throw new ModelException( " invalid index [" + index + "]" );
    }

    public _throws()
    {
        throwsException = new ArrayList<String>();
    }

    public _throws( String throwsClass )
    {
        throwsException = new ArrayList<String>();
        throwsException.add( throwsClass );
    }

    public _throws addThrows( Object throwsClass )
    {
        if( throwsClass instanceof String )
        {
            addThrows( (String)throwsClass );
        }
        else if( throwsClass instanceof Class )
        {
            addThrows( ((Class<?>)throwsClass).getCanonicalName() );
        }
        return this;
    }

    @Override
    public _throws replace( String target, String replacement )
    {
        List<String> replacedNames = new ArrayList<String>();
        for( int i = 0; i < this.throwsException.size(); i++ )
        {
            replacedNames.add(
                RefRenamer.apply( this.throwsException.get( i ), target, replacement ) );

            //replacedNames.add(
            //    this.throwsException.get( i ).replace( target, replacement ) );
        }
        this.throwsException = replacedNames;
        return this;
    }

    public _throws addThrows( String throwsClass )
    {
        throwsException.add( throwsClass );
        return this;
    }

    public static _throws of( Object... tokens )
    {
        _throws throwsExceptions = new _throws();
        for( int i = 0; i < tokens.length; i++ )
        {
            throwsExceptions.addThrows( tokens[ i ] );
        }
        return throwsExceptions;
    }

    public List<String> getThrows()
    {
        return this.throwsException;
    }

    public int count()
    {
        return this.throwsException.size();
    }

    public boolean isEmpty()
    {
        return count() == 0;
    }

    @Override
    public String toString()
    {
        return author();
    }
}
