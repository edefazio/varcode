package varcode.doc.lib;

import java.lang.reflect.Array;
import java.util.Collection;

import varcode.context.VarContext;
import varcode.doc.lib.text.PrintAsLiteral;

/**
 * this converts the columns of data passed in, into row-wise:
 * 
 * <PRE>
 * VarContext vc = VarContext.of( 
 *    "columns", new String[]{"letters", "numbers"},
 *    "letters", new String[]{"A", "B", "C", "D"},
 *    "numbers", new int[]{ 1, 2, 3, 4} );
 * 
 * Rowify.doRowify( vc, "columns" );
 * 
 * ...returns a String[] of rows:
 * row[0] = "A, 1" 
 * row[1] = "B, 2"
 * row[2] = "C, 3" 
 * row[3] = "D, 4"
 * 
 * </PRE>       * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class Rowify 
{
	/** 
     * API interface that allows a single var passed in varNamesArray
     * <PRE>
     * VarContext vc = VarContext.of( 
     *    "columns", new String[]{"letters", "numbers"},
     *    "letters", new String[]{"A", "B", "C", "D"},
     *    "numbers", new int[]{ 1, 2, 3, 4} );
     * 
     * Rowify.doRowify( vc, "columns" );
     * 
     * ...returns a String[] of rows:
     * row[0] = "A, 1" 
     * row[1] = "B, 2"
     * row[2] = "C, 3" 
     * row[3] = "D, 4"
     * 
     * </PRE>      
     * 
     * @param context
     * @param columnNamesArray the name of the array containing the names of 
     * columns to be rowified
     * @return 
     */
    public static String[] doRowify( VarContext context, String columnNamesArray )
    {
    	//System.out.println( "FIELD NAME "+ varNamesArray );
    	Object varNames = context.resolveVar(columnNamesArray );
    	
    	if( varNames.getClass().isArray() )
    	{
    		return rowify( context, (Object[])varNames );
    	}
    	if ( varNames instanceof Collection ) 
    	{
    		Collection<?> coll = (Collection<?>) varNames;
    		return rowify( context, (Object[]) coll.toArray( new Object[ 0 ] ) );
    	}
    	return rowify( context, new Object[] { varNames } );
    }
    	
    /** 
     * Makes this VarScript callable
     * @param context 
     * @param columnVarNames
     * @return rowify  
     */
    public static String[] rowify( VarContext context, Object...columnVarNames )
    {
    	//First collect all of the values for these vars
    	/*
    	for( int i = 0; i < columnVarNames.length; i++)
    	{
    		System.out.println( columnVarNames[ i ].toString() ); 
    	}
    	*/
    	Object[] columns = new Object[ columnVarNames.length ];
    	int rowCount = Count.INSTANCE.getCount( context, columnVarNames[ 0 ].toString() );
    	String[] rows = new String[ rowCount ];
    
    	for( int i = 0; i < columnVarNames.length; i++ )
    	{
    		//each of the Columns in NOW an Object[] (standardize this once)
    		columns[ i ] = ToArray.from( context, columnVarNames[ i ].toString() );
    	}
    
    	for( int i = 0; i < rowCount; i++ )
    	{
    		rows[ i ] = "";
    		for( int c = 0; c < columns.length; c++ )
    		{
    			if( c > 0 )
    			{
    				rows[ i ] += ", ";
    			}
    			rows[ i ] += PrintAsLiteral.printAsLiteral( 
                    Array.get( columns[ c ], i ) );
    		}
    	}	    	
    	return rows;
    }

}
