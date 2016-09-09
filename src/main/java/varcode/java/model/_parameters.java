package varcode.java.model;

import varcode.CodeAuthor;
import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * Models 
 * <UL>
 * <LI>NONE ()
 * <LI>ONE (int x) 
 * <LI>or MORE THAN ONE(String name, long id, Date dob)
 * </UL>
 * parameters
 *
 */
public class _parameters
	implements CodeAuthor
{
	public static final Dom PARAMS_LIST = BindML.compile( "( {{+:{+params+}, +}} )" );
	
	public static _parameters from( _parameters prototype )
	{
		List<parameter> clone = new ArrayList<parameter>();
		for( int i = 0; i < prototype.params.size(); i++ )
		{
			clone.add( parameter.from( prototype.params.get( i ) ) );
		}
		return new _parameters( clone );
	}
	
	public static _parameters of( _var...vars )
	{
		List<parameter> params = new ArrayList<parameter>();
		for( int i = 0; i < vars.length ; i++ )
		{
			params.add( new parameter( vars[ i ].type, vars[ i ].varName ) );
		}
		return new _parameters( params );
	}
	
	/**
	 * KeyValue pairs of type - names
	 * "int", "x"
	 * "String", "name"
	 * "double", 
	 * @param tokens
	 * @return
	 */
	public static _parameters of( String[] tokens )
	{
		List<parameter> params = new ArrayList<parameter>();
		if( tokens.length % 2 != 0 )
		{
			throw new VarException( 
				"There must be an even number of <type>, <name> token parameters");
		}
		for( int i = 0; i < tokens.length / 2 ; i++ )
		{
			params.add( new parameter( tokens[ i * 2 ], tokens[ ( i * 2 ) + 1 ]) );
		}
		return new _parameters( params );
	}
	
	private List<parameter> params;
	
	public _parameters()
	{
		this.params = new ArrayList<parameter>();
	}
	
	public _parameters add( parameter param )
	{
		this.params.add( param );
		return this;
	}
	
	public int count()
	{
		return params.size();
	}
	
	public _parameters( List<parameter>params )
	{
		this.params = params;
	}
	
	/**
	 * 
	 * @param commaAndSpaceSeparatedTokens
	 * @return
	 */
	protected static String[] normalizeTokens( String commaAndSpaceSeparatedTokens )
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
	
	public List<parameter> getParameters()
	{
		return this.params;
	}
	
	public String author( Directive... directives ) 
	{
		return Author.code( PARAMS_LIST, VarContext.of( "params", params ), directives );
	}	
	public String toString()
	{
		return author();
	}

    public void replace( String target, String replacement )
    {
        List<parameter> modifiedParams = new ArrayList<parameter>();
        for( int i = 0; i < this.params.size(); i++ )
        {
            modifiedParams.add(
                parameter.of( 
                    params.get( i ).type.toString().replace( target, replacement ),
                    params.get( i ).varName.toString().replace( target, replacement ) ) 
            );
        }        
        this.params = modifiedParams;
    }
	
	public static class parameter		
	{			
		public static parameter from( parameter prototype ) 
		{
			return new parameter( 
				_type.from( prototype.type ), 
				_identifier.from( prototype.varName ) );
		}
	
		public static parameter of( Object type, Object varName )
		{
			return new parameter( type, varName );
		}
		
		public final _type type; 
		public final _identifier varName; 
	
		public static final Dom PARAMS = BindML.compile( "{{+:{+type*+} {+varName*+}+}}" );
		
		public parameter( parameter iv )
		{
			this.type = iv.type;
			this.varName = iv.varName;
		}
	
		public parameter( Object type, Object varName )
		{
			this.type = varcode.java.model._type.of( type );
			this.varName = _identifier.of( varName );
		}
	
		public String toString()
		{
			return type.toString() + " "+ varName.toString();
		}
	}

	public parameter get( int index ) 
	{
		if( index < count() )
		{
			return params.get(  index );
		}
		throw new VarException(
			"unable to get parameter ["+ 
			index +"] out of range [0..."+ ( count() -1 ) +"]" );
	}

	public static _parameters of( String parameterString ) 
	{
		String[] tokens = normalizeTokens( parameterString );
		return _parameters.of( tokens );
	}

}
