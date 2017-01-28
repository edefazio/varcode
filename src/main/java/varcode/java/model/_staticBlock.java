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
import varcode.java.model._Java.Authored;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * Model of a Static block
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _staticBlock
    implements _Java, Authored
{
    public static final Template STATIC_BLOCK
        = BindML.compile(
            "static" + N
            + "{" + N
            + "{+$>(body)+}" + N
            + "}" );

    public static _staticBlock of( Object... linesOfCode )
    {
        return new _staticBlock( linesOfCode );
    }

    public static _staticBlock of( _code code )
    {
        return new _staticBlock( code );
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _staticBlock( _staticBlock prototype )
    {
        if( prototype != null )
        {
            this.body = _code.cloneOf(  prototype.body );
        }
    }
    
    public static _staticBlock cloneOf( _staticBlock prototype )
    {
        return new _staticBlock( prototype );        
    }

    private _code body;

    public _staticBlock()
    {
        this.body = new _code();
    }

    public _staticBlock( Object... linesOfCode )
    {
        this.body = _code.of( linesOfCode );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.body );
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
        final _staticBlock other = (_staticBlock)obj;
        if( !Objects.equals( this.body, other.body ) )
        {
            return false;
        }
        return true;
    }

    public boolean isEmpty()
    {
        return body == null || body.isEmpty();
    }

    public _code getBody()
    {
        return this.body;
    }

    public _staticBlock addTailCode( Object... linesOfCode )
    {
        body.addTailCode( linesOfCode );
        return this;
    }

    public _staticBlock addHeadCode( Object... codeSequence )
    {
        body.addHeadCode( codeSequence );
        return this;
    }

    @Override
    public _staticBlock replace( String target, String replacement )
    {
        this.body = this.body.replace( target, replacement );
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
        return STATIC_BLOCK;
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of( "body", body );
    }
    
    @Override
    public String author( Directive... directives )
    {
        if( !body.isEmpty() )
        {
            return Author.toString(
                STATIC_BLOCK, getContext(), directives );
        }
        return "";
    }

    @Override
    public String toString()
    {
        return author();
    }
}
