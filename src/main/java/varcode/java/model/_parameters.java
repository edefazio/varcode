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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.java.lang.RefRenamer;
import varcode.java.model._annotations._annotation;
import varcode.java.model._Java.Authored;
import varcode.java.model._Java.Countable;
import varcode.markup.bindml.BindML;
import varcode.ModelException;

/**
 * Models
 * <UL>
 * <LI>NONE ()
 * <LI>ONE (int x)
 * <LI>or MORE THAN ONE(String name, long id, Date dob)
 * </UL>
 * parameters
 *
 */
public class _parameters
    implements _Java, Countable, Authored
{
    public static final Template PARAMS_LIST
        = BindML.compile( "( {{+:{+params+}, +}} )" );

    public _parameters( _parameters prototype )
    {
        if( prototype != null )
        {
            for( int i = 0; i < prototype.count(); i++ )
            {
                params.add( 
                    _parameter.cloneOf( prototype.params.get( i ) ) );
            }
        }
    }
    
    public static _parameters cloneOf( _parameters prototype )
    {
        return new _parameters( prototype );        
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit( this );
    }

    public static _parameters of()
    {
        return new _parameters();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.params );
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
        final _parameters other = (_parameters)obj;
        if( !Objects.equals( this.params, other.params ) )
        {
            return false;
        }
        return true;
    }

    public static _parameters of( _parameter... ps )
    {
        List<_parameter> params = new ArrayList<_parameter>();
        for( int i = 0; i < ps.length; i++ )
        {
            params.add( ps[ i ] );
        }
        return new _parameters( params );
    }

    public static _parameters of( _var... vars )
    {
        List<_parameter> params = new ArrayList<_parameter>();
        for( int i = 0; i < vars.length; i++ )
        {
            params.add( new _parameter( vars[ i ].type, vars[ i ].varName.toString() ) );
        }
        return new _parameters( params );
    }

    /**
     * KeyValue pairs of type - names "int", "x" "String", "name" "double",
     *
     * @param tokens tokens comprised of parameters
     * @return _parameters representing parsed tokens
     */
    public static _parameters of( String[] tokens )
    {
        List<_parameter> params = new ArrayList<_parameter>();

        List<String> currentTokens = new ArrayList<String>();
        int prefix = 0;
        for( int i = 0; i < tokens.length; i++ )
        {
            if( tokens[ i ].startsWith( "@" ) || tokens[ i ].equals( "final" ) )
            {
                prefix++;
                currentTokens.add( tokens[ i ] );
            }
            else
            {
                currentTokens.add( tokens[ i ] );
                if( currentTokens.size() - prefix == 2 )
                {
                    params.add(
                        new _parameter(
                            currentTokens.toArray( new String[ 0 ] ) )
                    );
                    currentTokens.clear();
                    prefix = 0;
                }
            }
        }
        return new _parameters( params );
    }

    private List<_parameter> params = new ArrayList<_parameter>();

    public _parameters()
    {
    }

    public _parameters add( String type, String name )
    {
        this.params.add( _parameter.of( type, name ) );
        return this;
    }

    public _parameters add( _parameter param )
    {
        this.params.add( param );
        return this;
    }

    public _parameters add( _parameters params )
    {
        for( int i = 0; i < params.count(); i++ )
        {
            this.params.add( params.getAt( i ) );
        }
        return this;
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

    public _parameters( List<_parameter> params )
    {
        this.params = params;
    }

    /**
     *
     * @param commaAndSpaceSeparatedTokens
     * @return
     */
    protected static String[] normalizeTokens(
        String commaAndSpaceSeparatedTokens )
    {
        String[] toks = commaAndSpaceSeparatedTokens.split( " " );
        List<String> toksList = new ArrayList<String>();
        String temp = "";
        for( int i = 0; i < toks.length; i++ )
        {
            if( toks[ i ].endsWith( "," ) )
            {
                toks[ i ] = toks[ i ].substring( 0, toks[ i ].length() - 1 );
            }
            if( toks[ i ].startsWith( "," ) )
            {
                toks[ i ] = toks[ i ].substring( 1 );
            }
            String[] ts = toks[ i ].split( " " );

            for( int j = 0; j < ts.length; j++ )
            {
                String t = ts[ j ].trim();
                if( temp.length() > 0 )
                {
                    temp = temp + "," + t;
                    if( symmeticGeneric( temp ) )
                    {
                        toksList.add( temp );
                        temp = "";
                    }
                }
                else if( t.length() > 0 )
                {
                    if( t.contains( "<" ) )
                    {
                        if( symmeticGeneric( t ) )
                        {
                            toksList.add( t );
                        }
                        else
                        {
                            temp += t;
                        }
                    }
                    else
                    {
                        toksList.add( t );
                    }
                }
            }
        }
        if( temp.length() > 0 )
        {
            throw new ModelException(
                "unable to parse tokens, remaining temp = " + temp );
        }
        return toksList.toArray( new String[ 0 ] );
    }

    /**
     * Tests if a "type" is defined as a Generic (i.e. it contains the
     * appropriate amount of< >'s to represent a Generic Type
     *
     * @param s
     * @return
     */
    private static boolean symmeticGeneric( String s )
    {
        int openCount = 0;

        for( int i = 0; i < s.length(); i++ )
        {
            if( s.charAt( i ) == '<' )
            {
                openCount++;
            }
            else if( s.charAt( i ) == '>' )
            {
                openCount--;
            }
            //I could check if openCount < 0 but thats for the compiler
        }
        return openCount == 0;
    }

    /**
     * Gets JUST the parameter names (not types)
     * 
     * @return  the paramter names
     */
    public List<String> getNames()
    {
        List<String> pNames = new ArrayList<String>();
        for(int i=0; i < this.params.size(); i++ )
        {
            pNames.add( this.params.get( i ).getName() );
        }
        return pNames;
    }
    
    public List<_parameter> getParameters()
    {
        return this.params;
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public Template getTemplate()
    {
        if( params == null && params.isEmpty() )
        {
            return NO_PARAMS;
        }
        return PARAMS_LIST;
    }
    
    @Override
    public Context getContext()
    {
        return VarContext.of( "params", params );
    }
    
    public static final Template NO_PARAMS = BindML.compile( "(  )" );
    
    @Override
    public String author( Directive... directives )
    {
        return Author.toString( getTemplate(), getContext(), directives );
    }

    @Override
    public String toString()
    {
        return author();
    }

    @Override
    public _parameters replace( String target, String replacement )
    {
        List<_parameter> replaced = new ArrayList<_parameter>();
        for( int i = 0; i < this.params.size(); i++ )
        {
            replaced.add(
                params.get( i ).replace( target, replacement ) );
        }
        this.params = replaced;
        return this;
    }

    /**
     * a single name-value parameter to a method, constructor, etc.
     */
    public static class _parameter
        implements _Java, Annotated, Authored
    {
        public static _parameter cloneOf( _parameter prototype )
        {
            _parameter p = new _parameter(
                prototype.type + "",
                prototype.name + "" );

            if( prototype.isFinal )
            {
                p.setFinal();
            }
            p.annotations = _annotations.cloneOf( prototype.annotations );
            return p;
        }

        @Override
        public void visit( ModelVisitor visitor )
        {
            visitor.visit( this );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( name, type, isFinal, this.annotations );
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
            final _parameter other = (_parameter)obj;
            if( !Objects.equals( this.type, other.type ) )
            {
                return false;
            }
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.isFinal, other.isFinal ) )
            {
                return false;
            }
            if( !Objects.equals( this.annotations, other.annotations ) )
            {
                return false;
            }
            return true;
        }
        
        @Override
        public _annotations getAnnotations()
        {
            return this.annotations;
        }

        public _parameter annotate( _annotations annotations )
        {
            this.annotations.add( annotations );
            return this;
        }

        public _parameter annotate( _annotation annotation )
        {
            //this.parameterAnnotation = new _annotation( annotation.getAnnotation() );
            this.annotations.add( annotation );
            return this;
        }

        public _parameter annotate( String annotation )
        {
            //this.parameterAnnotation = new _annotation( annotation );
            this.annotations.add( annotation );
            return this;
        }

        
        public static _parameter of( Object...parts )
        {
            if( parts == null || parts.length < 2 )
            {
                throw new ModelException(
                    "parameters requires at least (2) args, type, name" );
            }
            if( parts.length == 2 )
            {   //only (2) means type then name
                return new _parameter( parts[ 0 ], (String)parts[ 1 ] );
            }
            ParamData pd = new ParamData();
            //the last (2) args are ALWAYS the Type and name
            pd.type = parts[parts.length - 2 ];
            pd.name = parts[parts.length -1 ].toString();
            for( int i = 0; i < parts.length -2; i++ )
            {
                if( parts[ i ] instanceof _annotation )
                {
                    pd.annotations.add( parts[ i ] );
                }
                if( parts[ i ] instanceof _annotations )
                {
                    pd.annotations.add( parts[ i ] );
                }
                if( parts[ i ] instanceof String )
                {
                    String str = (String)parts[i];
                    if( str.startsWith( "@" ) )
                    {
                        pd.annotations.add( parts[ i ] );
                    }
                    else if( str.equals( "final" ) )
                    {
                        pd.isFinal = true;
                    }
                }
            }
            _parameter _p = new _parameter( pd.type, pd.name );
            _p.annotations = pd.annotations;
            _p.isFinal = pd.isFinal;
            return _p;            
        }
        
        private static class ParamData
        {
            //pass in true to use "inline" annotation style
            public _annotations annotations = new _annotations( true );
            public boolean isFinal = false;
            public String name;
            public Object type;            
        }
        
        /*
        public static _parameter of( Object type, String name )
        {
            return new _parameter( type, name );
        }
        */

        /** type of the parameter ( int, String, ... ) */
        private String type;

        /** the name of the parameter ("count", "name"...)*/
        private String name;

        private Boolean isFinal = Boolean.FALSE;

        public boolean isVararg()
        {
            return type.endsWith( "..." );
        }

        public boolean isFinal()
        {
            return Boolean.TRUE.equals( this.isFinal );
        }

        //private _annotation parameterAnnotation;
        //pass in TRUE to have the annotations be the inline style
        private _annotations annotations = new _annotations( true );

        public static final Template PARAMS
            = BindML.compile(
                "{+annotations+}"
                + "{{+?isFinal:final +}}"
                + "{+type*+} {+name*+}" );

        public _parameter( _parameter iv )
        {
            /*
            this.parameterAnnotation = iv.parameterAnnotation= 
                new _annotation( iv.parameterAnnotation.getAnnotation() );
             */
            this.annotations = _annotations.cloneOf( iv.annotations );
            this.isFinal = iv.isFinal;
            this.type = iv.type + "";
            this.name = iv.name + "";
        }

        public _parameter setFinal()
        {
            this.isFinal = true;
            return this;
        }

        public _parameter setType( TypeMirror type )
        {
            this.type = type.toString();
            return this;
        }
        
        public _parameter setType( Type type )
        {
            this.type = type.toString();
            return this;
        }
        
        public _parameter setType( Class type )
        {
            this.type = type.toGenericString();
            return this;
        }
        
        public _parameter setType( String type )
        {
            this.type = type;
            return this;
        }

        public _parameter setName( String name )
        {
            this.name = name;
            return this;
        }

        public _parameter( String... tokens )
        {
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( "final" ) )
                {
                    this.isFinal = true;
                }
                else if( tokens[ i ].startsWith( "@" ) )
                {
                    this.annotations.add( new _annotation( tokens[ i ] ) );
                }
                else if( type == null )
                {
                    this.type = tokens[ i ];
                }
                else if( name == null )
                {
                    this.name = tokens[ i ];
                }
                else
                {
                    throw new ModelException(
                        "unable to parse tokens, at " + tokens[ i ] );
                }
            }
        }

        public _parameter( Object type, String name )
        {
            if( type instanceof Class )
            {
                this.type = ((Class)type).getCanonicalName();
            }
            else if( type instanceof Type )
            {
                this.type = ((Type)type).getTypeName();
            }
            else
            {
                this.type = type.toString();
            }
            //this.type = type.toString();
            this.name = name.toString();
        }

        @Override
        public String toString()
        {
            return author();
        }

        public String getType()
        {
            return this.type;
        }

        public String getName()
        {
            return this.name;
        }

        @Override
        public _parameter replace( String target, String replacement )
        {
            this.name
                = RefRenamer.apply( this.name, target, replacement );
            //this.name.replace( target, replacement );
            this.type
                = //this.type.replace( target, replacement );
                RefRenamer.apply( this.type, target, replacement );
            this.annotations
                = this.annotations.replace( target, replacement );
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
            return PARAMS;
        }
        
        @Override
        public String author( Directive... directives )
        {
            return Author.toString(
                PARAMS, getContext(), directives );
        }

        @Override
        public Context getContext()
        {
            VarContext vc = VarContext.of(
                "type", type,
                "name", name,
                "annotations", this.annotations );
            //"parameterAnnotation", this.parameterAnnotation);        
            if( isFinal )
            {
                vc.set( "isFinal", true );
            }
            return vc;
        }
    }

    public _parameter getAt( int index )
    {
        if( index < count() )
        {
            return params.get( index );
        }
        throw new ModelException(
            "unable to get parameter ["
            + index + "] out of range [0..." + (count() - 1) + "]" );
    }

    /**
     * If we have this:
     *
     * _parameters.of( "String ... g" );
     *
     * we tokenize the parameters as: {"String", "...", "g"}
     *
     * we want to treat them as: {"String...", "g"}
     *
     * ...also, if I have this: _parameters.of( "String...g");
     *
     * we tokenize the parameters as: {"String...g"}
     *
     * we want to treat it as: {"String...", "g"}
     *
     *
     * If we encounter a single token containing "..." (but NOT ENDING in "...")
     * we split it into two tokens
     * <PRE>
     * so if we see
     * String[] tokens = {"String...names"};
     * we split it into :
     * String[] tokens = {"String...", "names"};
     * </PRE>
     *
     * @return
     */
    private static String[] splitVarargsTokens( String[] tokens )
    {
        List<String> toks = new ArrayList<String>();
        for( int i = 0; i < tokens.length; i++ )
        {

            if( i > 0 && tokens[ i ].equals( "..." ) )
            {   //add the "..." to the end of the previous token
                toks.set( i - 1, toks.get( i - 1 ).trim() + "..." );
            }
            /**
             * If a Token contains (but does not end with varargs) i.e.
             * "String...names" then separate it into (2) tokens {"String...",
             * "names"}
             */
            else if( tokens[ i ].contains( "..." )
                && !tokens[ i ].endsWith( "..." ) )
            {
                toks.add( tokens[ i ]
                    .substring( 0,
                        tokens[ i ].indexOf( "..." ) + 3 ) );

                toks.add( tokens[ i ]
                    .substring( tokens[ i ].indexOf( "..." ) + 3 ) );
            }
            else
            {
                toks.add( tokens[ i ] );
            }
        }
        return toks.toArray( new String[ 0 ] );
    }

    public static _parameters of( String parameterString )
    {
        String[] tokens = normalizeTokens( parameterString );
        tokens = splitVarargsTokens( tokens );
        return _parameters.of( tokens );
    }
}
