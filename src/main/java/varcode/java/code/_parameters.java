package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.Template;

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
	extends Template.Base
{
	public static final Dom PARAMS_LIST = BindML.compile( "( {{+:{+params+}, +}} )" );
	
	public static _parameters from( _parameters prototype )
	{
		List<_parameter> clone = new ArrayList<_parameter>();
		for( int i = 0; i < prototype.params.size(); i++ )
		{
			clone.add( _parameter.from( prototype.params.get( i ) ) );
		}
		return new _parameters( clone );
	}
	
	public static _parameters of( _var...vars )
	{
		List<_parameter> params = new ArrayList<_parameter>();
		for( int i = 0; i < vars.length ; i++ )
		{
			params.add( new _parameter( vars[ i ].type, vars[ i ].varName ) );
		}
		return new _parameters( params );
	}
	
	/**
	 * KeyValue pairs of type - names
	 * "int", "x"
	 * "String", "name"
	 * "double", 
	 * @param tokens tokens comprised of parameters
	 * @return _parameters representing parsed tokens
	 */
	public static _parameters of( String[] tokens )
	{
		List<_parameter> params = new ArrayList<_parameter>();
		
        if( tokens.length % 2 != 0 )
		{
            System.out.println( params );
			throw new VarException( 
				"There must be an even number of <type>, <name> token parameters" 
              + System.lineSeparator() + params);
            
		}
        
		for( int i = 0; i < tokens.length / 2 ; i++ )
		{
			params.add( new _parameter( tokens[ i * 2 ], tokens[ ( i * 2 ) + 1 ]) );
		}
		return new _parameters( params );
	}
	
	private List<_parameter> params;
	
	public _parameters()
	{
		this.params = new ArrayList<_parameter>();
	}
	
	public _parameters add( _parameter param )
	{
		this.params.add( param );
		return this;
	}
	
	public int count()
	{
		return params.size();
	}
	
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
	public _parameters( List<_parameter>params )
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
        String temp = "";
		for( int i = 0; i < toks.length; i++ )
		{
			if( toks[ i ].endsWith( "," ) )
			{
				toks[ i ] = toks[i].substring( 0, toks[ i ].length() -1 ); 
			}
			if( toks[ i ].startsWith( "," ) )
			{
				toks[ i ] = toks[ i ].substring( 1 ); 
			}
			String[] ts = toks[ i ].split( " " );
			
            
			for( int j = 0; j < ts.length; j++ )
			{
				String t = ts[ j ].trim();
                if( temp.length() > 0 )
                {
                    temp = temp +"," + t;
                    if( symmeticGeneric( temp ) )
                    {
                        toksList.add( temp );
                        temp = "";
                    }                    
                }
                else if( t.length() > 0 )
				{
                    if( t.contains( "<" ) )
                    {
                        System.out.println ("contains <");
                        if( symmeticGeneric( t ) )
                        {
                            toksList.add( t );
                        }
                        else
                        {
                            temp += t;
                        }
                    }
                    else
                    {
                        toksList.add( t );
                    }
				}
			}            
		}
        if( temp.length() > 0 )
        {
            throw new VarException( 
                "unable to parse tokens, remaining temp = "+temp);
        }
		//System.out.println( toksList );
		return toksList.toArray( new String[ 0 ] );
	}
	
    private static boolean symmeticGeneric( String s )
    {
        int openCount = 0;
        
        for( int i = 0; i < s.length(); i++ )
        {
            if( s.charAt( i )== '<' )
            {
                openCount++;
            }
            else if( s.charAt( i ) == '>' )
            {
                openCount--;
            }
            //I could check if openCount < 0 but thats for the compiler
        }
        return openCount == 0;
    }
    
	public List<_parameter> getParameters()
	{
		return this.params;
	}
	
	public String author( Directive... directives ) 
	{
		return Author.code( PARAMS_LIST, VarContext.of( "params", params ), directives );
	}	
    
    @Override
	public String toString()
	{
		return author();
	}

    @Override
    public _parameters replace( String target, String replacement )
    {
        List<_parameter> modifiedParams = new ArrayList<_parameter>();
        for( int i = 0; i < this.params.size(); i++ )
        {
            modifiedParams.add(
                params.get( i ).replace( target, replacement ) );            
        }        
        this.params = modifiedParams;
        return this;
    }
	
	public static class _parameter
        extends Template.Base
	{			
        
		public static _parameter from( _parameter prototype ) 
		{
			return new _parameter( 
				prototype.type + "", 
				prototype.name + "" );
		}
	
		public static _parameter of( Object type, Object name )
		{
			return new _parameter( type, name );
		}
		
        /** The type4 of the parameter ( int, String, {+typeName+}, ... )*/
		private String type; 
        
        /** the name of the parameter ("count", "name"...) */
		private String name; 
	
        
		public static final Dom PARAMS = 
            BindML.compile( "{{+:{+type*+} {+name*+}+}}" );
		
		public _parameter( _parameter iv )
		{
			this.type = iv.type+ "";
			this.name = iv.name + "";
		}
	
		public _parameter( Object type, Object name )
		{
			this.type =  type.toString();
			this.name = name.toString();
		}
	
        @Override
		public String toString()
		{
			return author();
		}
        
        public String getType()
        {
            return this.type;
        }
        
        public String getName()
        {
            return this.name;
        }

        @Override
        public _parameter replace( String target, String replacement )
        {
            this.name = this.name.replace( target, replacement );
            this.type = this.type.replace( target, replacement );
            return this;
        }

        @Override
        public String author( Directive... directives )
        {
            return Author.code( 
                PARAMS, getContext(), directives );
        }
        
        public VarContext getContext()
        {
            return  VarContext.of( "type", type, "name", name );
        }
	}

	public _parameter get( int index ) 
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
