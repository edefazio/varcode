package varcode.java.code;

import varcode.VarException;
import varcode.java.JavaNaming;

public class _identifier
{
	public static _identifier from( _identifier id )
	{
		return new _identifier( id.identifierName );
	}
	
	public static _identifier of( Object name )
	{
		return new _identifier( name );
	}
		
	public static _identifier[] of( Object...names )
	{
		_identifier[] identifiers = new _identifier[ names.length ];
		for( int i = 0; i < names.length; i++ )
		{
			identifiers[ i ] = new _identifier( names[ i ] );
		}
		return identifiers;
	}
		
	private final String identifierName;
		
	public _identifier( Object identifierName )
	{
		if( identifierName != null )
		{
			JavaNaming.IdentifierName.validate( identifierName.toString() );
		}
		else
		{
			throw new VarException( "identifier name cannot be null" );
		}
		this.identifierName = identifierName.toString();
	}
		
	public String toString()
	{
		return identifierName;
	}				
}