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
package varcode.context.resolve;

import java.lang.reflect.Array;
import java.util.Collection;
import varcode.author.lib.AllCap;
import varcode.author.lib.FirstCap;
import varcode.context.Context;
import varcode.java.Java;
import varcode.java.JavaException;

/**
 * Resolves Variables
 * 
 * Knows how to resolve the data values by name
 */
public interface VarResolver
    extends Resolve
{
    /**
     * @param context the context to resolve the Var
     * @param varName the name of the var to resolve
     * @return the var or null
     */
    public Object resolveVar( Context context, String varName );

    /**
     * Resolves the Var "through"
     */
    public enum SmartVarResolver
        implements VarResolver
    {
        INSTANCE;

        /**
         * Tries to resolve the object by the var name, 
         * returning null if the Var is not bound
         * (as EITHER:
         * <UL>
         * <LI>a var in the ( Javascript ) Expression engine
         * <LI>a value within the {@code ScopeBindings}
         * </UL>
         * 
         * NOTE: we handle certain Capitalization Variants
         * <PRE>
         * if we have: 
         * VarContext.of( "name", "eric" )
         * 
         * ...and our Template wants 
         * Template t = BindML.compile( "Hello, {+Name+}" );
         * 
         * we assume the template wants the value of "name" using "FirstCaps"
         * 
         * so:
         * Author.string( BindML.compile( "Hello, {+Name+}"), "name", "eric" );
         * 
         * will return: 
         * "Hello Eric"  <-- resolve "name", convert it from "eric" to "Eric"
         * 
         * likewise if we have an "AllCaps" 
         * Author.string( BindML.compile( "Hello, {+NAME+}"), "name", "eric" );
         * 
         * will return:
         * "Hello ERIC" <-- resolve "name" and convert it from "eric" to "ERIC"
         * </PRE>
         */
        @Override
        public Object resolveVar( Context context, String varName )
        {
            if( varName == null || varName.trim().length() == 0 )
            {
                return null;
            }
            Object var = context.get( varName );
            if( var != null )
            {
                return var;
            }
            String systemProp = System.getProperty( varName );
            if( systemProp != null )
            {
                return systemProp;
            }
            //check if they want the FirstCaps variant
            if( Character.isUpperCase( varName.charAt( 0 ) ) )
            {   
                var = context.get( Character.toLowerCase( varName.charAt( 0 ) ) 
                    + varName.substring( 1 ) );
                if( var != null )
                {
                    return FirstCap.doFirstCaps( var );
                }
            }
            //IF the var we want to resolve is ALL CAPS: 
            // search the keys of the context for any keys that equalIgnoreCase
            // then capitalize the result
            if( varName.toUpperCase().equals( varName ) )
            {   
                String[] keys = context.keySet().toArray( new String[ 0 ]);
                for( int i = 0; i < keys.length; i++ )
                {
                    if( keys[ i ].equalsIgnoreCase( varName ) )
                    {
                        var = context.get( keys[ i ] );
                        return AllCap.doAllCaps( var );
                    }
                }
            }
            
            //Here we check if we are trying to access a var with a "."
            // so "field.name", we might want to try accessing "field" first
            //
            // then reflectively invoking Java.get( instance, "name" ) property
            
            if( varName.contains( "." ) )
            {
                //System.out.println( "Varname contains . "+ varName );
                String containerVar = varName.substring( 0, varName.indexOf( "." ) );
                String fieldVar = varName.substring(  varName.indexOf( "." ) +1 );
                //System.out.println( "Searching container \""+ containerVar+ "\"" );
                //System.out.println( "for field \"" + fieldVar +"\"" );
                Object container = resolveVar( context, containerVar );
                if( container != null )
                {
                    if( container instanceof Collection )
                    {
                        //convert it to an array and then process it like that
                        container = ((Collection)container).toArray();                        
                    }
                    // Converts From AOS (Arrays of Structs) like:
                    // Point[] ps =
                    //  new Point[]{ new Point(1,4), new Point(2,5), new Point(3,6)};
                    //
                    // to Struct of arrays
                    // int[] point.x = {1,2,3};
                    // int[] point.y = {4,5,6};
                    // for the purposes of serializing the contents to templates
                    if( container.getClass().isArray() )
                    {                        
                        int length = Array.getLength( container );
                        //System.out.println( "IS ARRAY ["+length+"]" );
                        Object[] fieldValueArray = new Object[length];
                        for( int i = 0; i < length; i++ )
                        {
                            Object cont = Array.get( container, i );
                            //what if field var is an index?? (it shouldnt be, but)
                            try
                            {
                                Object val = Java.get( cont, fieldVar );
                                fieldValueArray[ i ] = val;
                            }
                            catch( JavaException je )
                            {
                                return null;
                            }
                            //System.out.println( "Setting fieldValue["+i+"] = "+ val );
                            
                        }
                        return fieldValueArray;
                    }                    
                    try
                    {
                        return Java.get( container, fieldVar );
                    }
                    catch( JavaException je )
                    {   //swallow this and return null
                        return null;
                    }
                }
            }
            //TODO ? Check ThreadLocal
            return null;
        }
    }
}
