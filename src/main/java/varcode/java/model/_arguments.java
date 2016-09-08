package varcode.java.model;

import java.util.ArrayList;
import java.util.List;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * Models 
 * <UL>
 * <LI>NONE: ()
 * <LI>ONE: ( 4 ) 
 * <LI>or MORE THAN ONE:( "Hey", 5, new HashMap<Integer,String>(), true )
 * </UL>
 * arguments
 *
 */
public class _arguments
	implements SelfAuthored
{
	public static _arguments from( _arguments prototype ) 
	{
		_arguments clone = new _arguments();
		for( int i = 0; i < prototype.count(); i++ )
		{
			clone.arguments.add( argument.from( prototype.arguments.get( i ) ) ); 
		}
		return clone;
	}
	
	public static final Dom ARGUMENTS_LIST = 
		BindML.compile( "( {{+:{+args+}, +}} )" );
	
	public String toCode( Directive... directives ) 
	{
		return Author.code( 
			ARGUMENTS_LIST, 
			VarContext.of( "args", arguments ), 
			directives );
	}
	
	public int count()
	{
		return arguments.size();
	}
	
	/**
	 * KeyValue pairs of type - names
	 * "int", "x"
	 * "String", "name"
	 * "double", 
	 * @param arguments the arugments
	 * @return
	 */
	public static _arguments of( Object... arguments )
	{
		if( arguments == null )
		{
			return new _arguments();
		}
		List<argument> args = new ArrayList<argument>();
		
		for( int i = 0; i < arguments.length ; i++ )
		{
			if( arguments[ i ] != null )
			{
				if( arguments[ i ] instanceof String )
				{
					args.add( new argument( arguments[ i ].toString() ) );	
				}
				else
				{
					try
					{
						_literal l = _literal.of( arguments[ i ] );
						args.add( new argument( l ) );
					}
					catch( Exception e )
					{
						args.add( new argument( arguments[i ].toString() ) );
					}
				}
				
			}
			else
			{
				args.add( new argument( "null" ) );
			}
		}
		return new _arguments( args );
	}
	
	private List<argument> arguments;
	
	public _arguments()
	{
		this.arguments = new ArrayList<argument>();
	}
	
	public _arguments( List<argument>args )
	{
		this.arguments = args;
	}
	
	public String toString()
	{
		return toCode();
	}
	
	//an argument COULD be
	// " " String literal 
	// a Reference
	// a constructor new Date()
	public static class argument
	{			
		private final Object argument; 
	
		public static final Dom ARGUMENT = BindML.compile( "{+argument*+}" );
		
		public argument( argument iv )
		{
			this.argument = iv.argument;
		}
	
		public static argument from( argument prototype ) 
		{
			return new argument( prototype );
		}

		public argument( _literal literal )
		{
			this.argument = literal;
		}
		
		public argument( String arg )
		{
			if( arg == null )
			{
				this.argument = "null";
			}
			else
			{
				this.argument = arg;
			}
		}
	
		public String toString()
		{
			return argument.toString();
		}
	}

}
