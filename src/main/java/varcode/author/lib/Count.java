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
package varcode.author.lib;

import java.lang.reflect.Array;
import java.util.Collection;
import varcode.context.Context;

import varcode.context.VarScript;
//import varcode.translate.JSArrayToArrayTranslate;

/**
 * Count of the number of Elements of a bound variable
 */
public enum Count
    implements VarScript
{
    INSTANCE;

    @Override
    public Object eval( Context context, String input )
    {
        return getCount( context, input );
    }

    /*
    public Integer getJSArrayCount( Object obj )
    {
        Object[] arr = JSArrayToArrayTranslate.getJSArrayAsObjectArray( obj );
        if( arr != null )
        {
            return arr.length;
        }
        return null;
    }
    */

    public Integer getCount( Context context, String varName )
    {
        //the user passes in the NAME of the one I want index for
        Object var = context.resolveVar( varName );
        return getCount( var );
    }

    public Integer getCount( Object var )
    {
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                return Array.getLength( var );
            }
            if( var instanceof Collection )
            {
                return ((Collection<?>)var).size();
            }
            /*
            Integer jsCount = getJSArrayCount( var );
            if( jsCount != null )
            {
                return jsCount;
            }
            */
            return 1;
        }
        return null;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

}
