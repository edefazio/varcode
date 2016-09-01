package varcode.java.model;

import varcode.java.JavaNaming;

/**
 * TODO I need a void-included type (i.e. for return types) and a
 * void-free type (i.e. for paramter lists)
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _type
{
	public static _type of( String name )
	{
		return new _type( name );
	}
	public static _type from( _type type )
	{
		return new _type( type.typeName );
	}
	
	public static _type of( Object name )
	{
		return new _type( name );
	}
		
	public static _type[] of( Object...names )
	{
		_type[] types = new _type[ names.length ];
		for( int i = 0; i < names.length; i++ )
		{
			types[ i ] = new _type( names[ i ] );
		}
		return types;
	}
	
	private final String typeName;
	
	public String getName()
	{
		return typeName;			
	}
	
	public _type( Object typeName )
	{
		if( typeName instanceof _type )
		{
			this.typeName = ((_type)typeName).typeName;
		}
		else if( typeName instanceof Class )
		{
			Class<?> type = ((Class<?>)typeName);
			if( type.isPrimitive() )
			{
				this.typeName = type.getSimpleName();
			}
			else if( type.getPackage().getName().equals( "java.lang" ) )
			{
				this.typeName = type.getSimpleName();
			}
			else
			{
				this.typeName = type.getCanonicalName();
			}
		} 
		else
		{
			JavaNaming.TypeName.validate( typeName.toString() );
			this.typeName = typeName.toString();
		}
	}
	
	public String toString()
	{
		return typeName;
	}		
}