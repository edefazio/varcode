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
package varcode.java.naming;

/**
 * Encapsulates the conventions for Primitive notation in .java source code
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Primitive
{
    BOOLEAN( boolean.class, Boolean.class ),
    BYTE( byte.class, Byte.class ),
    CHAR( char.class, Character.class ),
    DOUBLE( double.class, Double.class ),
    FLOAT( float.class, Float.class ),
    INT( int.class, Integer.class ),
    LONG( long.class, Long.class ),
    SHORT( short.class, Short.class );

    private final String name;
    private final Class primitiveClass;
    private final Class wrapperClass;

    private Primitive( Class primitiveClass, Class wrapperClass )
    {
        this.primitiveClass = primitiveClass;
        this.name = primitiveClass.getSimpleName();
        this.wrapperClass = wrapperClass;
    }

    public static boolean contains( Class clazz )
    {
        return ( byClass( clazz )!= null );
    }
    
    public static boolean contains( String name )
    {
        return ( byName( name )!= null );
    }
    
    public static Primitive byName( String name )
    {
        for( int i = 0; i < Primitive.values().length; i++ )
        {
            if( Primitive.values()[ i ].name.equals( name ) )
            {
                return Primitive.values()[ i ];
            }
        }
        return null;
    }

    public static Primitive byClass( Class clazz )
    {
        for( int i = 0; i < Primitive.values().length; i++ )
        {
            if( Primitive.values()[ i ].primitiveClass.equals( clazz ) )
            {
                return Primitive.values()[ i ];
            }
        }
        return null;
    }

    public static Primitive byWrapperClass( Class wrapperClass )
    {
        for( int i = 0; i < Primitive.values().length; i++ )
        {
            if( Primitive.values()[ i ].wrapperClass.equals( wrapperClass ) )
            {
                return Primitive.values()[ i ];
            }
        }
        return null;
    } 
}
