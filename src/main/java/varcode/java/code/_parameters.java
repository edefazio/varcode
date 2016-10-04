package varcode.java.code;

import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.code._annotate._annotation;
import varcode.markup.bindml.BindML;
import varcode.Model;

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
    implements Model
{            
	public static final Dom PARAMS_LIST = 
        BindML.compile( "( {{+:{+params+}, +}} )" );
	
	public static _parameters cloneOf( _parameters prototype )
	{
		List<_parameter> clone = new ArrayList<_parameter>();
		for( int i = 0; i < prototype.params.size(); i++ )
		{
			clone.add( _parameter.cloneOf( prototype.params.get( i ) ) );
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

        List<String> currentTokens = new ArrayList<String>();
        int prefix = 0;
        for( int i = 0; i < tokens.length; i++ )
        {
            if( tokens[ i ].startsWith( "@" ) || tokens[ i ].equals( "final" ) )
            {
                prefix++;
                currentTokens.add( tokens[ i ] );
            }
            else
            {
                currentTokens.add( tokens[ i ] );
                if( currentTokens.size() - prefix == 2 )
                {
                    params.add( 
                        new _parameter( 
                            currentTokens.toArray( new String[ 0 ] ) ) 
                        ); 
                    currentTokens.clear();
                    prefix = 0;
                }                
            }
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
	
    @Override
	public String author( Directive... directives ) 
	{
        if( params != null && params.size() > 0 )            
        {
            return Compose.asString( 
                PARAMS_LIST, 
                VarContext.of( 
                    "params", params ), directives );
        }
        return "(  )";
	}	
    
    @Override
	public String toString()
	{
		return author();
	}

    @Override
    public _parameters bindIn( VarContext context )
    {
        List<_parameter> modifiedParams = new ArrayList<_parameter>();
        for( int i = 0; i < this.params.size(); i++ )
        {
            modifiedParams.add(
                params.get( i ).bindIn( context ) );            
        }        
        this.params = modifiedParams;
        return this;
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
	
    /** a single name-value parameter to a method, constructor, etc. */
	public static class _parameter
        implements Model
    {                
		public static _parameter cloneOf( _parameter prototype ) 
		{
			_parameter p = new _parameter( 
				prototype.type + "", 
				prototype.name + "" );
            
            if( prototype.isFinal )
            {
                p.setFinal();
            }
            if( prototype.parameterAnnotation != null 
              && ! prototype.parameterAnnotation.isEmpty() )
            {
                p.annotate( prototype.parameterAnnotation );
            }
            return p;
		}
	
        public _parameter annotate( _annotation annotation )
        {
            this.parameterAnnotation = new _annotation( annotation.getAnnotation() );
            return this;
        }
        
        public _parameter annotate( String annotation )
        {
            this.parameterAnnotation = new _annotation( annotation );
            return this;
        }
        
		public static _parameter of( Object type, Object name )
		{
			return new _parameter( type, name );
		}
		
        /** The type4 of the parameter ( int, String, {+typeName+}, ... )*/
		private String type; 
        
        /** the name of the parameter ("count", "name"...) */
		private String name; 
	
        private Boolean isFinal = Boolean.FALSE;
        
        private _annotation parameterAnnotation;
        
		public static final Dom PARAMS = 
            BindML.compile( 
                "{+parameterAnnotation+}" +
                "{{+?isFinal:final +}}"+        
                "{+type*+} {+name*+}" );
		
		public _parameter( _parameter iv )
		{
            this.parameterAnnotation = iv.parameterAnnotation= 
                new _annotation( iv.parameterAnnotation.getAnnotation() );
            this.isFinal = iv.isFinal;
			this.type = iv.type+ "";
			this.name = iv.name + "";
		}
	
        public _parameter setFinal()
        {
            this.isFinal = true;
            return this;
        }
        
        public _parameter( String...tokens )
        {
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( "final" ) )
                {
                    this.isFinal = true;
                }
                else if( tokens[ i ].startsWith("@" ) )
                {
                    this.parameterAnnotation = new _annotation( tokens[ i ] );
                }
                else if( type == null )
                {
                    this.type = tokens[ i ];
                }
                else if( name == null )
                {
                    this.name = tokens[ i ];
                }
                else
                {
                    throw new VarException(
                        "unable to parse tokens, at "+ tokens[ i ] );
                }
            }
            if( this.parameterAnnotation == null )
            {
                this.parameterAnnotation = _annotation.of();
            }
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
        public _parameter bindIn( VarContext context )
        {
            this.type = Compose.asString( BindML.compile( this.type ), context);
            this.name = Compose.asString( BindML.compile( this.name ), context);
            if( this.parameterAnnotation != null && !this.parameterAnnotation.isEmpty() )
            {
                this.parameterAnnotation = 
                    this.parameterAnnotation.bindIn( context );
            }
            return this;
        }
        
        @Override
        public _parameter replace( String target, String replacement )
        {
            this.name = this.name.replace( target, replacement );
            this.type = this.type.replace( target, replacement );
            this.parameterAnnotation = 
                this.parameterAnnotation.replace( target, replacement );
            return this;
        }

        @Override
        public String author( Directive... directives )
        {
            return Compose.asString( 
                PARAMS, getContext(), directives );
        }
        
        public VarContext getContext()
        {
            VarContext vc = VarContext.of( 
                "type", type, 
                "name", name, 
                "parameterAnnotation", this.parameterAnnotation);        
            if( isFinal )
            {
                vc.set( "isFinal", true );
            }
            return vc;    
        }
	}

	public _parameter getAt( int index ) 
	{
		if( index < count() )
		{
			return params.get( index );
		}
		throw new VarException(
			"unable to get parameter ["+ 
			index +"] out of range [0..."+ ( count() -1 ) +"]" );
	}
   
    /**
     * If we have this:
     * 
     * _parameters.of( "String ... g" );
     * 
     * we tokenize the parameters as:
     * {"String", "...", "g"}
     * 
     * we want to treat them as:
     * {"String...", "g"}
     * 
     * ...also, if I have this:
     * _parameters.of( "String...g");
     *
     * we tokenize the parameters as:
     * {"String...g"}
     * 
     * we want to treat it as:
     * {"String...", "g"}
     * 
     * 
     * If we encounter a single token containing "..." (but NOT ENDING in "...")
     * we split it into two tokens
     * <PRE>
     * so if we see 
     * String[] tokens = {"String...names"};
     * we split it into :
     * String[] tokens = {"String...", "names"};
     * </PRE>
     * @return 
     */
    private static String[] splitVarargsTokens( String[] tokens )
    {
        List<String> toks = new ArrayList<String>();
        for( int i = 0; i < tokens.length; i++ )
        {
            
            if( i > 0 && tokens[ i ].equals( "..." ) )
            {   //add the "..." to the end of the previous token
                toks.set( i - 1, toks.get( i - 1 ).trim() + "..." );
            }
            /**
             * If a Token contains (but does not end with varargs)
             * i.e. "String...names"
             * then separate it into (2) tokens
             * {"String...", "names"}
             */
            else if( tokens[ i ].contains( "..." ) 
                && !tokens[ i ].endsWith( "..." ) )
            {
                toks.add( tokens[ i ]
                    .substring( 0, 
                       tokens[ i ].indexOf( "..." ) + 3 ) );
                
                toks.add( tokens[ i ]
                    .substring( tokens[ i ].indexOf( "..." ) + 3 ) );
            }
            else
            {
                toks.add( tokens[ i ] );
            }
        }
        return toks.toArray( new String[ 0 ] );
    }
    
    
	public static _parameters of( String parameterString ) 
	{
		String[] tokens = normalizeTokens( parameterString );
        tokens = splitVarargsTokens( tokens );
		return _parameters.of( tokens );
	}
}
