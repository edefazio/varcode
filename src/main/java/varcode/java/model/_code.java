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

import java.io.Serializable;
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
import varcode.markup.bindml.BindML;

/**
 * TODO I'm considering whether I should be able to "register Dependency" within
 * a codeBlock, which will allow me to more easily "port" a block of code...
 * (add it to the end of a method, static block, etc.)

 PERHAPS I extend _code to provide this functionality?

 So when I add a catchExceptionClass for instance, I would register it as a
 dependency (so any code that might need to use this code could query and
 import the classes appropriately)

 compile one class/enum/interface to another (i.e. to do multiple inheritance)
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _code
    implements _Java, Serializable, Authored
{
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public _code()
    {
        
    }
    
    public _code( _code prototype )
    {
        if( prototype != null )
        {            
            this.codeSequence.addAll( prototype.codeSequence );
        }
    }
    
    /**
     * Creates a code compile the objects (Strings, _code) for instance:
     * <PRE>
     * _code commentLog = _code.of(
     *     "//this is a comment",
     *     "LOG.debug(\"Line After Comment\");");</PRE>
     *
     * represents (2) lines of code...<BR><BR>
     *
     * ...we can also take an existing {@code _code} and add it to another
     * {@code _code}:
     * <PRE>
     * _code combined = _code.of( commentLog, "//This is a comment After the Log" );</PRE>
     *
     * Where "combined" is:
     * <PRE>
     * //this is a comment
     * LOG.debug("Line After Comment");
     * //This is a comment After the Log
     * </PRE>
     *
     * @param codeSequence a sequence of Strings and _code
     * @return the _code representing the code in sequence
     */
    public static _code of( Object... codeSequence )
    {
        _code code = new _code();
        if( codeSequence != null )
        {
            for( int i = 0; i < codeSequence.length; i++ )
            {
                code.codeSequence.add( codeSequence[ i ].toString() );
            }
        }
        return code;
    }

    /**
     * Create and return a clone of the prototype code
     *
     * @param prototype the prototype code
     * @return some code
     */
    public static _code cloneOf( _code prototype )
    {
        return new _code( prototype );

    }

    public boolean isEmpty()
    {
        return codeSequence.isEmpty();
    }

    /**
     * A List of "generic" objects that are convert-able to a sequence of code.
     * contains:
     * <UL>
     * <LI>Strings
     * <LI>_code
     * <LI>Template entities (like: _try, _for, _while, _if, _do)
     * </UL>
     */
    private List<String> codeSequence = new ArrayList<String>();

    public static final Template CODEBLOCK = BindML.compile( "{+codeBlock+}" );

    /**
     * The context
     *
     * @return context prior to Authoring
     */
    @Override
    public Context getContext()
    {
        return VarContext.of(
            "codeBlock", stringify( this.codeSequence )
        );
    }

    private String stringify( List<String> codeComponents )
    {
        if( codeComponents == null || codeComponents.isEmpty() )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < codeComponents.size(); i++ )
        {
            if( i > 0 )
            {
                sb.append( "\r\n" );
            }
            Object o = codeComponents.get( i );
            if( o != null )
            {
                sb.append( o.toString() );
            }
        }
        return sb.toString();
    }

    @Override
    public Template getTemplate()
    {
        return CODEBLOCK;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString(getTemplate(),
            getContext(),
            directives );
    }

    /**
     * Add a block of code at the head of the method (BEFORE all of the existing
     * code in teh codeBlock)
     *
     * @param codeBlock the codeBlokc to add before
     * @return the mutated _codeBlock
     */
    public _code addHeadCode( _code codeBlock )
    {
        List<String> headCode = new ArrayList<String>();
        headCode.addAll( codeBlock.codeSequence );
        headCode.addAll( this.codeSequence );
        this.codeSequence = headCode;
        return this;
    }

    /**
     * Adds code at the "top" (Head) of the code Block
     *
     * @param codeLines lines of code
     * @return
     */
    public _code addHeadCode( Object... codeLines )
    {
        List<String> headCode = new ArrayList<String>();
        for(int i=0; i< codeLines.length; i++ )
        {
            headCode.add( codeLines[i].toString() );
        }
        
        //add the existing code sequence AFTER
        headCode.addAll( codeSequence );
        
        this.codeSequence = headCode;
        return this;
    }

    /**
     * Adds lines of code to the tail (bottom) of the code block
     *
     * @param codeLines lines of code to add to the tail
     * @return this (modified)
     */
    public _code addTailCode( Object... codeLines )
    {
        for( int i = 0; i < codeLines.length; i++ )
        {
            this.codeSequence.add( codeLines[ i ].toString() );
        }            
        return this;        
    }

    /**
     * Add code to the tail
     *
     * @param codeBlock a code block to add
     * @return this (modified)
     */
    public _code addTailCode( _code codeBlock )
    {
        this.codeSequence.addAll( codeBlock.codeSequence );
        return this;
    }

    private static List<String> doReplace(
        List<String> list, String target, String replacement )
    {
        List<String> replace = new ArrayList<String>();
        for( int i = 0; i < list.size(); i++ )
        {
            replace.add( RefRenamer.apply( list.get( i ), target, replacement ) );            
        }
        return replace;
    }

    @Override
    public _code replace( String target, String replacement )
    {
        this.codeSequence = doReplace( this.codeSequence, target, replacement );
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( codeSequence );
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
        final _code other = (_code)obj;
        String thisAuthored = this.author();
        String otherAuthored = other.author();
        return thisAuthored.trim().equals( otherAuthored.trim() );
    }

    @Override
    public String toString()
    {
        return author();
    }
}
