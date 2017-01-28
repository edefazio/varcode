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
import varcode.ModelException;

import varcode.java.lang.IdentifierName;
import varcode.java.lang.TypeName;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * The idea of a variable with type and identifier
 *
 * @author M. Eric DeFazio
 */
public class _var
{
    public static final Template VAR = BindML.compile( "{+type*+} {+varName*+}" );
    public final _type type;
    public final _identifier varName;

    public static _var of( Object type, Object identifier )
    {
        return new _var( type, identifier );
    }

    public _var( _var iv )
    {
        this.type = iv.type;
        this.varName = iv.varName;
    }

    public _var( Object type, Object varName )
    {
        this.type = _type.of( type );
        this.varName = _identifier.of( varName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, varName );
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
        final _var other = (_var)obj;
        if( !Objects.equals( this.type, other.type ) )
        {
            return false;
        }
        if( !Objects.equals( this.varName, other.varName ) )
        {
            return false;
        }
        return true;
    }

    public static String[] normalizeTokens( String commaAndSpaceSeparatedTokens )
    {
        String[] toks = commaAndSpaceSeparatedTokens.split( " " );
        List<String> toksList = new ArrayList<String>();
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
                if( t.length() > 0 )
                {
                    toksList.add( t );
                }
            }
        }
        //System.out.println( toksList );
        return toksList.toArray( new String[ 0 ] );
    }

    /**
     * TODO I need a void-included type (i.e. for return types) and a void-free
     * type (i.e. for parameter lists)
     *
     * @author M. Eric DeFazio eric@varcode.io
     */
    public static class _type
    {
        public static _type of( String name )
        {
            return new _type( name );
        }

        public static _type from( _type type )
        {
            return new _type( type.typeName );
        }

        public static _type of( Object name )
        {
            return new _type( name );
        }

        public static _type[] of( Object... names )
        {
            _type[] types = new _type[ names.length ];
            for( int i = 0; i < names.length; i++ )
            {
                types[ i ] = new _type( names[ i ] );
            }
            return types;
        }

        private final String typeName;

        public String getName()
        {
            return typeName;
        }

        public _type( Object typeName )
        {
            if( typeName instanceof _type )
            {
                this.typeName = ((_type)typeName).typeName;
            }
            else if( typeName instanceof Class )
            {
                Class<?> type = ((Class<?>)typeName);
                if( type.isPrimitive() )
                {
                    this.typeName = type.getSimpleName();
                }
                else if( type.getPackage().getName().equals( "java.lang" ) )
                {
                    this.typeName = type.getSimpleName();
                }
                else
                {
                    this.typeName = type.getCanonicalName();
                }
            }
            else
            {
                TypeName.validate( typeName.toString() );
                this.typeName = typeName.toString();
            }
        }

        @Override
        public String toString()
        {
            return typeName;
        }
    }/* type */

    public static class _identifier
    {
        public static _identifier cloneOf( _identifier id )
        {
            return new _identifier( id.identifierName );
        }

        public static _identifier of( Object name )
        {
            return new _identifier( name );
        }

        public static _identifier[] of( Object... names )
        {
            _identifier[] identifiers = new _identifier[ names.length ];
            for( int i = 0; i < names.length; i++ )
            {
                identifiers[ i ] = new _identifier( names[ i ] );
            }
            return identifiers;
        }

        private final String identifierName;

        public _identifier( Object identifierName )
        {
            if( identifierName != null )
            {
                IdentifierName.validate( identifierName.toString() );
            }
            else
            {
                throw new ModelException( "identifier name cannot be null" );
            }
            this.identifierName = identifierName.toString();
        }

        @Override
        public String toString()
        {
            return identifierName;
        }
    }
}
