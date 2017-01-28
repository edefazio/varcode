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
 * Creates an index count (an array of sequential indexes) for all elemnts in
 * the array
 *
 * For example:
 * <PRE>
 * if I have the input String[]{ "A", "B", "C", "D", "E" };
 * it will return int[]{ 0, 1, 2, 3, 4 };
 *
 * if I have the input String[]{"Yes" "No", "Maybe"};
 * it will return int[]{ 0, 1, 2 };
 * </PRE>
 */
public enum CountIndex
    implements VarScript
{
    INSTANCE;

    @Override
    public Object eval(
        Context context, String input )
    {
        return getCountIndex( context, input );
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
    public Object getCountIndex( Context context, String varName )
    {
        //the user passes in the NAME of the one I want index for
        //Object var = context.get( sourceCode );
        Object var = context.resolveVar( varName );
        if( var != null )
        {
            if( var.getClass().isArray() )
            {
                int len = Array.getLength( var );
                int[] countIndex = new int[ len ];
                for( int i = 0; i < len; i++ )
                {
                    countIndex[ i ] = i;
                }
                return countIndex;
            }
            if( var instanceof Collection )
            {
                Collection<?> coll = (Collection<?>)var;
                int len = coll.size();
                int[] countIndex = new int[ len ];
                for( int i = 0; i < len; i++ )
                {
                    countIndex[ i ] = i;
                }
                return countIndex;
            }
            /*
            Integer jsCount = getJSArrayCount( var );
            if( jsCount != null )
            {
                int[] countIndex = new int[ jsCount ];
                for( int i = 0; i < jsCount; i++ )
                {
                    countIndex[ i ] = i;
                }
                return countIndex;
            }
            */
            return new int[] { 0 };
        }
        return null;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

}
