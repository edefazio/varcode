/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.author.lib;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 *
 * @author Eric
 */
public abstract class ApplyToOneOrMore
{
    public abstract Object applyToOne( Object object );
    
    public Object applyToCollection( Object var )
    {
        return applyToArray( ((Collection<?>)var).toArray() );
    }
    
    public Object applyToArray( Object var )
    {
        //need to "firstCap" each element within the array
        int len = Array.getLength( var );
        Object[] arr = new Object[ len ];
        for( int i = 0; i < len; i++ )
        {
            Object idx = Array.get( var, i );
            if( idx != null )
            {
                arr[ i ] = applyToOne( idx );
                    //idx.toString().toUpperCase();
            }
            else
            {   //watch out for NPEs!
                arr[ i ] = null;
            }
        }
        return arr;            
    }
    
    public Object apply( Object var )
    {
        if( var == null )
        {
            return null;
        }
        if( var.getClass().isArray() )
        {   
            return applyToArray( var );            
        }
        if( var instanceof Collection )
        {
            return applyToCollection( var );            
        }
        return applyToOne( var );
    }
}
