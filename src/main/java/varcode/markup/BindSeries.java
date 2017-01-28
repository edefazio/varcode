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
package varcode.markup;

import java.util.HashMap;
import java.util.Map;
import varcode.context.VarBindException;

/**
 * Allows "named variables" to be populated by a series of variables in 
 * "encounter order" (lazily bound the input parameters to named variables)
 * 
 * Example:<BR>
 * If we have a series of variables {$a$, $b$, $c$, $b$, $a$} 
 * 
 * there are (3) unique variables {$a$, $b$, $c$} in this <B>encounter order</B>
 * {$a$, $b$, $c$}
 *
 * we create a BindSeries to map the variables {$a$, $b$, $c$} lazily...
 * 
 * So, by creating this:
 BindSeries nums = BindSeries.of( 1, 2, 3 );
 
 then, we can iterate over the series of variables:
 <PRE>
 * {$a$, $b$, $c$, $b$, $a$}, to create another set:
 * { 1,   2,   3,   2,   1 }
 * </PRE>
 * 
 * we use this method as an alternative to having the client manually 
 * create a mapping to pass in
 * 
 * Similar to binding "Actual" parameters in some Java code...
 * for instance, with a method with multiple arguments
 * <PRE>
 * public static double distance( <B>double x1, double y1, double x2, double y2 </B>)
 * {
 *     //....
 * }
 * </PRE>
 * ...and you call the method:<PRE>
 * double theDistance = distance(<B> 3.0d, 4.5d, 11.0d, 3.5d</B> );</PRE>
 * ...where we "match" the actual arguments to the names of the parameters within
 * the body of the method.
 * <UL>
 * <LI>x1 = 3.0d
 * <LI>y1 = 4.5d
 * <LI>x2 = 11.0d
 * <LI>y2 = 3.5d
 * </UL>
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindSeries
{
    /** 
     * Create a BindSeries with a Series of values to be bound 
     * in a first come-first serve basis.
     * @param values values to be bound into a document
     * @return the BindSeries
     */
    public static BindSeries of( Object... values )
    {
        return new BindSeries( values );
    }
    
    /** 
     * the index BEFORE the next value 
     * i.e. we start at -1, and when we need a value, we:<PRE>
     * 1) increment (from -1 to 0)
     * 2) return the value at that index ( i.e. valuesInEncounterOrder[ 0 ] )</PRE>
     */
    public int valueIndex = -1;
    
    /** the valuesInEncounterOrder to be (on a first come basis) */
    public Object[] valuesInEncounterOrder;
    
    /**
     * AFTER we bind a value to a name, we store the binding
     * in this boundNameToValue map.
     */
    public Map<String, Object> boundNameToValue = 
        new HashMap<String, Object>();

    /**
     * 
     * @param values the valuesInEncounterOrder to be bound into the named binding
     */
    public BindSeries( Object...values )
    {
        this.valuesInEncounterOrder = values;
    }
    
    /**
     * Checks whether a value for the "name" has been bound:
     * if so, returns it.
     * if not, gets the next value and binds it to this name, and returns it
     *
     * NOTE: by convention, vars are signified with prefix/postfix $'s
     * but you may decide to omit the $'s for simplicity (we will add them
     * in manually if they are omitted).
     * 
     * SO, for example, this:
     * <PRE>
     * Object res = BindSeries.resolve( "$name$");
     * //... produces the same result as this:
     * Object res = BindSeries.resolve( "name" );
     * </PRE>
     * 
     * @param name the name of the variable to get
     * @return the value
     * @throes VarBindException if unable to resolve the value 
     * (ran out of free values)
     */
    public Object resolve( String name )
        throws VarBindException
    {
        if( !name.startsWith( "$" ) )
        {
            name = "$" + name;
        }
        if( !name.endsWith( "$" ) )
        {
            name = name + "$";
        }
        //System.out.println( "looking for cached " + name );
        Object val = boundNameToValue.get( name );
        if( val != null )
        {
            //System.out.println("FOUND EXACT cached " + name );
            return val;
        }
        //check First Caps
        String firstCaps = "$" + Character.toLowerCase( name.charAt( 1 ) )
            + name.substring( 2 );
        
        //System.out.println( "checking FIRST CAPS cached \"" + firstCaps +"\"");
        
        //
        val = boundNameToValue.get( firstCaps );
        if( val != null )
        {
            //System.out.println (" FOUND FIRSTCAPS cached "+ firstCaps );
            String vs = val.toString();
            return Character.toUpperCase( vs.charAt( 0 ) ) + vs.substring( 1 );
        }
        
        //Check all caps
        //System.out.println( "checking ALLCAPS cached " + name.toLowerCase() );
        val = boundNameToValue.get( name.toLowerCase() );
        
        if( val != null )
        { 
            //System.out.println (" FOUND ALLCAPS cached "+ val.toString().toUpperCase() );
            return val.toString().toUpperCase();
        }        
        
        valueIndex++; //advance the index
        if( valueIndex > valuesInEncounterOrder.length - 1 )
        {
            throw new VarBindException( "ran out of bind values " 
                + ( valueIndex + 1 ) + " for " + name );
        }
        String varName = name.toLowerCase(); //normalize the name
        
        //System.out.println( "binding name is "+ varName+" to "+ valuesInEncounterOrder[ valueIndex ] );
            
        boundNameToValue.put( varName, valuesInEncounterOrder[ valueIndex ] ); //cache it
        if( name.equals( varName ) )
        {   
            //System.out.println( "name is simple " +  varName );
            return valuesInEncounterOrder[ valueIndex ];
        }
        else
        {   //if everything BUT the
            
            if( varName.substring( 2 ).equals( name.substring( 2 ) ) )
            {   //need to firstCaps
                String str = valuesInEncounterOrder[ valueIndex ].toString();
                String normalized = Character.toUpperCase( str.charAt( 0 ) ) + str.substring( 1 );
                //System.out.println( "returning firstCaps "+ normalized );
                return normalized;
            }
            else
            {
                String normalized = valuesInEncounterOrder[ valueIndex ].toString().toUpperCase();
                //System.out.println( "returning ALLCAPS "+ normalized );
                return valuesInEncounterOrder[ valueIndex ].toString().toUpperCase();
            }
        }
    }
}
