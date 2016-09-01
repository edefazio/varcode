package varcode.java.model;

import varcode.VarException;
import varcode.doc.lib.text.EscapeString;

public class _literal	
{
	private final String rep;
	
	public static final _literal NULL_LITERAL = new _literal();
	
	public static _literal of( Object o )
	{
		//System.out.println (o.getClass() );
		if( o == null )
		{
			return NULL_LITERAL;
		}
		//if( o.getClass().getPackage().getName().equals( "java.lang" ) )
		//{
		if( Long.class.isInstance( o ) )
		{
			return new _literal( ((Long)o).longValue() );
		}
		if( Double.class.isInstance( o ) )
		{
			return new _literal( ((Double)o).doubleValue() );
		}
		if( Float.class.isInstance( o ) )
		{
			return new _literal( ((Float)o).floatValue() );
		}
		if( Character.class.isInstance( o ) )
		{
			return new _literal( ((Character)o).charValue() );
		}
		if( Boolean.class.isInstance( o ) )
		{
			return new _literal( ((Boolean)o).booleanValue() );
		}
		if( Short.class.isInstance( o ) )
		{
			return new _literal( ((Short)o).shortValue() );
		}
		if( Byte.class.isInstance( o ) )
		{
			return new _literal ( ((Byte)o).byteValue() );
		}
		if( Integer.class.isInstance( o ) )
		{
			return new _literal( ((Integer)o).intValue() );
		}
		if( String.class.isInstance( o ) )
		{
			return new _literal( (String)o );
		}
		//}
		throw new VarException( "cannot convert Object " + o + " to literal" );
	}
	
	public _literal( int i )
	{
		this.rep = ""+i;
	}
	
	private _literal()
	{
		this.rep = "null";
	}
	public _literal( boolean b )
	{
		this.rep = "" + b;
	}
	
	public _literal( char c )
	{
		this.rep = "'"+ EscapeString.escapeJavaString( c + "" ) +"'";
	}
	
	public _literal( short s )
	{
		this.rep = "(short)"+s;
	}
	
	public _literal( byte b )
	{
		this.rep = "(byte)"+b;
	}
	
	public _literal( double d )
	{
		this.rep = d + "d";
	}
	
	public _literal( float f )
	{
		this.rep = f + "F";
	}
	
	public _literal( long l )
	{
		this.rep = l + "L";
	}
	
	public _literal( String s )
	{
		this.rep = "\"" + EscapeString.escapeJavaString( s ) + "\"";
	}
	
	public String toString()
	{
		return rep;
	}
	
}
