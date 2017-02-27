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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * Model of an if statement
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _if
    implements _Java, Serializable
{
    public static final String N = System.lineSeparator();
    
    public static _if is( Object condition, Object... bodyLines )
    {
        return new _if( condition, bodyLines );
    }

    public _code condition;
    public _code body;
    public List<_elseIf> elseIfs;
    public _code elseBody;

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _if( Object condition, Object... bodyLines )
    {
        this.condition = _code.of( condition );
        this.body = _code.of( bodyLines );
        this.elseIfs = new ArrayList<_elseIf>();
        this.elseBody = new _code();
    }

    @Override
    public _if replace( String target, String replacement )
    {
        this.condition = this.condition.replace( target, replacement );
        this.body = this.body.replace( target, replacement );
        this.elseBody = this.elseBody.replace( target, replacement );
        for( int i = 0; i < this.elseIfs.size(); i++ )
        {
            this.elseIfs.get( i ).replace( target, replacement );
        }
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( condition, body, elseBody, elseIfs );
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
        final _if other = (_if)obj;
        if( !Objects.equals( this.condition, other.condition ) )
        {
            return false;
        }
        if( !Objects.equals( this.body, other.body ) )
        {
            return false;
        }
        if( !Objects.equals( this.elseIfs, other.elseIfs ) )
        {
            return false;
        }
        if( !Objects.equals( this.elseBody, other.elseBody ) )
        {
            return false;
        }
        return true;
    }

    public Context getContext()
    {
        return VarContext.of(
            "condition", this.condition,
            "body", this.body,
            "elseIf", this.elseIfs,
            "elseBody", this.elseBody );
    }

    public static final Template IF_BLOCK = BindML.compile(
        "if( {+condition*+} )" + N
        + "{" + N
        + "{+$>(body)*+}" + N
        + "}" + N
        + "{{+?elseIf:{+elseIf+}" + N
        + "+}}"
        + "{{+?elseBody:else" + N
        + "{" + N
        + "{+$>(elseBody)+}" + N
        + "}" + N
        + "+}}" );

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( IF_BLOCK, getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }

    public _if _else( Object... elseBodyLines )
    {
        return _else( _code.of( elseBodyLines ) );
    }

    public _if _else( _code elseBody )
    {
        this.elseBody = elseBody;
        return this;
    }

    public _if elseIf( String condition, Object... codeBody )
    {
        _elseIf elf = new _elseIf( _code.of( condition ), _code.of( codeBody ) );
        this.elseIfs.add( elf );
        return this;
    }

    public _if elseIf( String elseCondition, _code elseIfBody )
    {
        _elseIf elf = new _elseIf( _code.of( elseCondition ), elseIfBody );
        this.elseIfs.add( elf );
        return this;
    }

    /**
     * Model of an else if statement
     */
    public static class _elseIf
        implements _Java, Authored
    {
        public static final Template ELSEIF = BindML.compile(
            "else if( {+condition*+} )" + N
            + "{" + N
            + "{+$>(elseIfBody)*+}" + N
            + "}" );

        private _code condition;
        private _code elseIfBody;

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }

        public _elseIf( _code condition, _code elseIfBody )
        {
            this.condition = condition;
            this.elseIfBody = elseIfBody;
        }

        //TODO
        @Override
        public _elseIf replace( String target, String replacement )
        {
            this.condition.replace( target, replacement );
            this.elseIfBody.replace( target, replacement );
            return this;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( condition, elseIfBody );
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
            final _elseIf other = (_elseIf)obj;
            if( !Objects.equals( this.condition, other.condition ) )
            {
                return false;
            }
            if( !Objects.equals( this.elseIfBody, other.elseIfBody ) )
            {
                return false;
            }
            return true;
        }

        @Override
        public Template getTemplate()
        {
            return ELSEIF;
        }
        
        @Override
        public Context getContext()
        {
            return VarContext.of(
                "condition", this.condition,
                "elseIfBody", this.elseIfBody );
        }

        @Override
        public String author()
        {
            return author( new Directive[ 0 ] );
        }

        @Override
        public String author( Directive... directives )
        {
            return Author.toString( ELSEIF, getContext(), directives );
        }

        @Override
        public String toString()
        {
            return author();
        }
    }
}
