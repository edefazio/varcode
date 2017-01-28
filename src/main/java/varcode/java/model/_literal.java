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
import varcode.ModelException;
import varcode.author.lib.EscapeString;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _literal
{
    private final String rep;

    public static final _literal NULL_LITERAL = new _literal();

    public static _literal of( Object o )
    {
        if( o == null )
        {
            return NULL_LITERAL;
        }
        if( Long.class.isInstance( o ) )
        {
            return new _literal( ((Long)o).longValue() );
        }
        if( Double.class.isInstance( o ) )
        {
            return new _literal( ((Double)o).doubleValue() );
        }
        if( Float.class.isInstance( o ) )
        {
            return new _literal( ((Float)o).floatValue() );
        }
        if( Character.class.isInstance( o ) )
        {
            return new _literal( ((Character)o).charValue() );
        }
        if( Boolean.class.isInstance( o ) )
        {
            return new _literal( ((Boolean)o).booleanValue() );
        }
        if( Short.class.isInstance( o ) )
        {
            return new _literal( ((Short)o).shortValue() );
        }
        if( Byte.class.isInstance( o ) )
        {
            return new _literal( ((Byte)o).byteValue() );
        }
        if( Integer.class.isInstance( o ) )
        {
            return new _literal( ((Integer)o).intValue() );
        }
        if( String.class.isInstance( o ) )
        {
            return new _literal( (String)o );
        }
        throw new ModelException( "cannot convert Object " + o + " to literal" );
    }

    public _literal( int i )
    {
        this.rep = "" + i;
    }

    private _literal()
    {
        this.rep = "null";
    }

    public _literal( boolean b )
    {
        this.rep = "" + b;
    }

    public _literal( char c )
    {
        this.rep = "'" + EscapeString.escapeJavaString( c + "" ) + "'";
    }

    public _literal( short s )
    {
        this.rep = "(short)" + s;
    }

    public _literal( byte b )
    {
        this.rep = "(byte)" + b;
    }

    public _literal( double d )
    {
        this.rep = d + "d";
    }

    public _literal( float f )
    {
        this.rep = f + "F";
    }

    public _literal( long l )
    {
        this.rep = l + "L";
    }

    public _literal( String s )
    {
        this.rep = "\"" + EscapeString.escapeJavaString( s ) + "\"";
    }

    public String toString()
    {
        return rep;
    }

    /**
     * Handles an array of values to be printed literally
     *
     * i.e. Object[] o = new Object[]{ "3", 3, true, Math.PI, 'c' }; _array a =
     * _array.of( o ); will be represented as : "3", 3, true, 3.14159D, 'c'
     *
     * NOTE: if you want custom aggregations outside of this like { }: "{ "3",
     * 3, true, 3.14159D, 'c' } you need to add this manually
     */
    public static class _array
    {
        public List<_literal> elementRep;

        public static _array of( Object... elements )
        {
            if( elements == null || elements.length == 0 )
            {
                return new _array( new ArrayList<_literal>() );
            }
            List<_literal> reps = new ArrayList<_literal>();
            for( int i = 0; i < elements.length; i++ )
            {
                reps.add( _literal.of( elements[ i ] ) );
            }
            return new _array( reps );
        }

        private _array( List<_literal> elementReps )
        {
            this.elementRep = elementReps;
        }

        public String toString()
        {
            if( elementRep == null || elementRep.size() == 0 )
            {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < elementRep.size(); i++ )
            {
                if( i > 0 )
                {
                    sb.append( ", " );
                }
                sb.append( elementRep.get( i ) );
            }
            return sb.toString();
        }
    }
}
