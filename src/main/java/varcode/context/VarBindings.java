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
package varcode.context;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.script.Bindings;


/**
 * Simple implementation of JSR-223 {@code Bindings}
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarBindings
    implements Bindings
{
    private final TreeMap<String, Object> kvMap;

    public static VarBindings of( Object... nameValuePairs )
    {
        if( nameValuePairs.length % 2 != 0 )
        {
            throw new VarBindException(
                "Pairs values must be passed in as pairs, length ("
                + nameValuePairs.length + ") not valid" );
        }

        if( nameValuePairs.length == 0 )
        {
            return new VarBindings();
        }
        VarBindings bindings = new VarBindings();

        for( int i = 0; i < nameValuePairs.length; i += 2 )
        {
            bindings.put(
                nameValuePairs[ i ].toString(),
                nameValuePairs[ i + 1 ] );
        }
        return bindings;
    }

    public VarBindings()
    {
        this( new TreeMap<String, Object>() );
    }

    private VarBindings( Map<String, Object> keyValuePairMap )
    {
        this.kvMap = new TreeMap<String, Object>();
        this.kvMap.putAll( keyValuePairMap );
    }

    @Override
    public boolean containsKey( Object key )
    {
        return kvMap.containsKey( key );
    }

    @Override
    public Object get( Object key )
    {
        return kvMap.get( key );
    }

    @Override
    public int size()
    {
        return kvMap.size();
    }

    @Override
    public boolean isEmpty()
    {
        return kvMap.isEmpty();
    }

    @Override
    public boolean containsValue( Object value )
    {
        return kvMap.containsValue( value );
    }

    @Override
    public void clear()
    {
        kvMap.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return kvMap.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        return kvMap.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet()
    {
        return kvMap.entrySet();
    }

    @Override
    public Object put( String name, Object value )
    {
        if( value instanceof VarScript || value instanceof Directive )
        {
            if( name.startsWith( "$" ) )
            {
                Object rep
                    = kvMap.put( name, value );

                return rep;
            }
            Object rep = kvMap.put( "$" + name, value );
            return rep;
        }
        Object rep = kvMap.put( name, value );
        return rep;
    }

    @Override
    public void putAll( Map<? extends String, ? extends Object> toMerge )
    {
        Iterator<?> it = toMerge.keySet().iterator();
        while( it.hasNext() )
        {
            String name = (String)it.next();
            Object value = toMerge.get( name );
            put( name, value );
        }
    }

    @Override
    public Object remove( Object key )
    {
        return kvMap.remove( key );
    }

    @Override
    public String toString()
    {
        return kvMap.toString();
    }
}
