package varcode.java.lang;


import java.util.ArrayList;
import java.util.List;

import varcode.context.VarBindings;
import varcode.context.VarBindings.SelfBinding;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;

public class _var
	implements SelfBinding
{
	public static final Dom DOM = BindML.compile( "{+type*+} {+varName*+}" );
	
	public final _type type; 
	public final _identifier varName; 
	
	public _var( _var iv )
	{
		this.type = iv.type;
		this.varName = iv.varName;
	}
	
	public _var( Object type, Object varName )
	{
		this.type = varcode.java.lang._type.of( type );
		this.varName = _identifier.of( varName );
	}
	
    @Override
	public void bindTo( VarBindings bindings ) 
	{
		bindings.setAllPublicFieldsOf( this );		
	}
	
    @Override
	public String toString()
	{
		return Compose.asString( DOM, VarContext.of( ).set( this ) );
	}
	
	public static String[] normalizeTokens( String commaAndSpaceSeparatedTokens )
	{
		String[] toks = commaAndSpaceSeparatedTokens.split( " " );
		List<String>toksList = new ArrayList<String>(); 
		for( int i = 0; i < toks.length; i++ )
		{
			if( toks[ i ].endsWith( "," ) )
			{
				toks[ i ] = toks[i].substring( 0, toks[i].length() -1 ); 
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
}
