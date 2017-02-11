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
import java.util.Arrays;
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
 * Models
 * <UL>
 * <LI>NONE: ( )
 * <LI>ONE: ( "AString" )
 * <LI>or MORE THAN ONE:( "Hey", 5, new HashMap<Integer,String>(), true, 'c' )
 * </UL>
 * arguments passed to methods
 *
 * NOTE: to differentiate between a String and Some code, String literals can be
 * prefixed with "$$"
 *
 * so this:
 * <PRE>
 * _arguments args = _arguments.of( "new HashMap()", "$$StringLiteral");
 * ...is represented as:
 * System.out.println( args.toString() );
 *
 *  //prints:
 * "( new HashMap(), \"StringLiteral\" )"
 *
 * </PRE>
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _args
    implements _Java, Countable, Authored
{
    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( arguments );
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
        final _args other = (_args)obj;
        if( !Objects.equals( this.arguments, other.arguments ) )
        {
            return false;
        }
        return true;
    }
     
    public _args( _args prototype )
    {
        for( int i = 0; i < prototype.count(); i++ )
        {
            arguments.add( prototype.arguments.get( i ) );
        }
    }
    
    /**
     * creates a new _arguments as a clone of prototype
     *
     * @param prototype the prototype to base the clone
     * @return new clone instance
     */
    public static _args cloneOf( _args prototype )
    {
        return new _args( prototype );                
    }

    public static final Template ARGUMENTS_LIST
        = BindML.compile( "( {{+:{+args+}, +}} )" );

    /**
     * returns the argument at the index
     *
     * @param index the index of the argument to get
     * @return String the argument at this index
     */
    public String getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return arguments.get( index );
        }
        throw new ModelException( "Invalid argument index [" + index + "]" );
    }

    /**
     * Adds an argument at the end of the current arguments list
     *
     * @param argument an argument to add to the end of existing arguments
     * @return this (updated with new argument)
     */
    public _args addArgument( Object argument )
    {
        this.arguments.add( stringFormOf( argument ) );
        return this;
    }

    public _args addArguments( Object... arguments )
    {
        for( int i = 0; i < arguments.length; i++ )
        {
            this.arguments.add( stringFormOf( arguments[ i ] ) );
        }
        return this;
    }

    @Override
    public Template getTemplate()
    {
        return ARGUMENTS_LIST;
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of("args", arguments );
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString(
            getTemplate(),
            getContext(),
            directives );
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public int count()
    {
        return arguments.size();
    }
    
    @Override
    public boolean isEmpty()
    {
        return count() == 0;
    }

    /**
     * Decides how arguments are displayed (as Strings) since we store arguments
     * as Strings
     * <UL>
     * <LI>sometimes we want a String i.e. "myBean" to be a reference to an
     * entity/instance),
     * <LI>sometimes we want the String "Dear Sir or Madam" to be treated as a
     * String literal): we do this by prefixing String literals with "$$", so:
     * <PRE>
     * _arguments args = _arguments.of( "$$Dear Sir or Madam" );
     * will print "( \"Dear Sir or Madam\" );
     * <LI>Sometimes we encounter a null, we want to print "null"
     * <LI>Sometimes we have an entity that describes itself as a String
     * (i.e. an anonymous class) and we call toString() on it
     * </PRE>
     *
     * @param obj an object argument
     * @return the String representation of the argument (as it would appear in
     * code)
     */
    private static String stringFormOf( Object obj )
    {
        if( obj != null )
        {
            if( obj.getClass().getPackage().getName()
                .startsWith( "java.lang" ) )
            {
                //to pass a String, then you need to identify it 
                //as a literal
                if( obj instanceof String )
                {
                    String str = (String)obj;
                    //the String can begin with "$$"
                    if( str.startsWith( STRING_LITERAL_PREFIX ) )
                    {
                        return _literal.of( str.substring( 2 ) ).toString();
                    }
                    else
                    {
                        return obj.toString();
                    }
                }
                else
                {
                    try
                    {
                        _literal l = _literal.of( obj );
                        return l.toString();
                    }
                    catch( Exception e )
                    {
                        return obj.toString();
                    }
                }
            }
            return obj.toString();
        }
        return "null";
    }

    /**
     * @param arguments the arguments
     * @return a new _arguments container
     */
    public static _args of( Object... arguments )
    {
        if( arguments == null )
        {
            return new _args( "null" );
        }

        List<String> args = new ArrayList<String>();

        for( int i = 0; i < arguments.length; i++ )
        {
            args.add( stringFormOf( arguments[ i ] ) );
        }
        return new _args( args.toArray( new String[ 0 ] ) );
    }

    /**
     * the arguments list
     */
    private final List<String> arguments = new ArrayList<String>();

    public _args( String... args )
    {        
        arguments.addAll( Arrays.asList( args ) );
    }

    @Override
    public String toString()
    {
        return author();
    }

    @Override
    public _args replace( String target, String replacement )
    {
        for( int i = 0; i < this.arguments.size(); i++ )
        {
            this.arguments.set(
                i, 
                RefRenamer.apply( this.arguments.get( i ), target, replacement ) );
        }
        return this;
    }
}
