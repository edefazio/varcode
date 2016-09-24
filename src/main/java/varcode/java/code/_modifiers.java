package varcode.java.code;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import varcode.Template;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.markup.bindml.BindML;

public class _modifiers
	extends Template.Base	
{
	public static _modifiers from( _modifiers mods )
	{
		return of( mods.getBits() );
	}
	
    
	public static _modifiers of( int modifiers )
	{
		return new _modifiers( modifiers );
	}
	
	public static _modifiers of( String...keywords )
	{
		_modifiers m = new _modifiers();
		m.set( keywords );
		return m;
	}
	
	private int mods = 0;

	public _modifiers()
	{
		
	}
    
    public _modifiers bindIn ( VarContext context )
    {
        return this;
    }
    
    public _modifiers replace( String target, String replacement )
    {
        List<String> keywords = this.bitsToKeywords( );
        for(int i=0; i< keywords.size(); i++ )
        {
            keywords.set( i, keywords.get( i ).replace(target, replacement) );
        }
        _modifiers rep = _modifiers.of( keywords.toArray( new String[ 0 ]) );
        this.mods = rep.mods;
        return this;
    }
    
    public int count()
    {
        return Integer.bitCount( this.mods );
    }
    
    public boolean isEmpty()
    {
        return count() == 0;
    }
    
	public int getBits()
	{
		return mods;
	}
	
	public _modifiers( int mods )
	{
		validate( mods );
		this.mods = mods;
	}
	
	public _modifiers set( String...keywords )
	{
		for( int i = 0; i < keywords.length; i++ )
		{
			set( keywords[ i ] );
		}
		this.validate( mods );
		return this;
	}
	
	public _modifiers set( String keyWord )
	{
		if( keyWord == null )
		{
			return this;
		}
		Integer bit = KEYWORD_TO_BIT_MAP.get( keyWord );
		if( bit == null )
		{
			throw new VarException( "Unknown keyword \""+ keyWord+"\"" );
		}
		this.mods |= bit;
		return this;		
	}
	
	public _modifiers setPublic()
	{
		this.mods &= ~(Modifier.PRIVATE | Modifier.PROTECTED);
		this.mods |= Modifier.PUBLIC;
		return this;
	}
	
	public _modifiers setProtected()
	{
		this.mods &= ~(Modifier.PRIVATE | Modifier.PUBLIC);
		this.mods |= Modifier.PROTECTED;
		return this;
	}
	public _modifiers setPrivate()
	{
		this.mods &= ~(Modifier.PUBLIC | Modifier.PROTECTED);
		this.mods |= Modifier.PRIVATE;
		return this;
	}

	public _modifiers setStatic()
	{
		this.mods |= Modifier.STATIC;
		return this;
	}
	
	public _modifiers setFinal()
	{
		this.mods |= Modifier.FINAL;
		return this;
	}
	
	public _modifiers setSynchronized()
	{
		this.mods |= Modifier.SYNCHRONIZED;
		return this;
	}
	
	public _modifiers setAbstract()
	{
		this.mods |= Modifier.ABSTRACT;
		return this;		
	}

	public _modifiers setNative()
	{
		this.mods |= Modifier.NATIVE;
		return this;		
	}
	
	public _modifiers setTransient()
	{
		this.mods |= Modifier.TRANSIENT;
		return this;		
	}
	public _modifiers setVolatile()
	{
		this.mods |= Modifier.VOLATILE;
		return this;		
	}
	
	public _modifiers setStrictFP()
	{
		this.mods |= Modifier.STRICT;
		return this;		
	}
	
	public boolean containsAny( String... keywords )
	{
		for( int i = 0; i < keywords.length; i++ )
		{
			Integer bit = KEYWORD_TO_BIT_MAP.get( keywords[i] );
			if( bit != null )
			{
				if( ( mods & bit ) != 0 )
				{
					return true;
				}
			}
		}
		return false;
	}
	

	public boolean contains( int modifier )
	{
		return ( ( mods & modifier) != 0 );		
	}
    
    public boolean contains( String modifier )
    {
        Integer bit = KEYWORD_TO_BIT_MAP.get( modifier );
        if( bit == null )
        {
            return false;
        }
        return ( bit & mods ) != 0;
    }
	
	public boolean containsAny( int... modifiers )
	{
		for( int i = 0; i < modifiers.length; i++ )
		{
			if( ( mods & modifiers[ i ] ) != 0 )
			{
				return true;
			}
		}
		return false;
	}

	public boolean containsAll( int... modifiers )
	{
		for( int i = 0; i < modifiers.length; i++ )
		{
			if( ( mods & modifiers[ i ] ) == 0 )
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean containsAll( String... keywords )
	{
		for( int i = 0; i < keywords.length; i++ )
		{
            //System.out.println ("Testing "+keywords[ i ] );
			Integer bit = KEYWORD_TO_BIT_MAP.get( keywords[ i ] );
			if( bit != null )
			{
				if( ( this.mods & bit ) == 0 )
				{
					return false;
				}
			}
		}
		return true;
	}
	public enum _mod
	{
		PUBLIC( "public", Modifier.PUBLIC ), 
		//DEFAULT( "", 0 ), 
		PROTECTED( "protected", Modifier.PROTECTED ), 
		PRIVATE( "private", Modifier.PRIVATE ),		
		STATIC( "static", Modifier.STATIC ),
		SYNCHRONIZED( "synchronized", Modifier.SYNCHRONIZED ),
		ABSTRACT( "abstract", Modifier.ABSTRACT ), 
		FINAL( "final", Modifier.FINAL ),
		NATIVE( "native", Modifier.NATIVE ), 
		TRANSIENT( "transient", Modifier.TRANSIENT ), 
		VOLATILE( "volatile", Modifier.VOLATILE ),
		STRICTFP( "strictfp", Modifier.STRICT ),
		INTERFACE_DEFAULT( "default", 1 << 12 ); //this is an "Eric Special"
		
		private final String keyword;
		private final int bitValue;
		
		private _mod( String keyword, int bitValue )
		{
			this.keyword = keyword;
			this.bitValue = bitValue;
		}
		
		public String getKeyword()
		{
			return this.keyword;
		}
		
		public int getBitValue()
		{
			return this.bitValue;
		}
	}
	
	public static Map<String,Integer>KEYWORD_TO_BIT_MAP = new HashMap<String, Integer>();
	public static Map<Integer,String>BIT_TO_KEYWORD_MAP = new HashMap<Integer, String>();
	
	static
	{
		KEYWORD_TO_BIT_MAP.put( "public", Modifier.PUBLIC ); 	
		BIT_TO_KEYWORD_MAP.put( Modifier.PUBLIC, "public" );
        
		KEYWORD_TO_BIT_MAP.put( "protected", Modifier.PROTECTED );
		BIT_TO_KEYWORD_MAP.put( Modifier.PROTECTED, "protected");
        
		KEYWORD_TO_BIT_MAP.put( "private", Modifier.PRIVATE );	
		BIT_TO_KEYWORD_MAP.put( Modifier.PRIVATE, "private");
        
		KEYWORD_TO_BIT_MAP.put( "static", Modifier.STATIC );
		BIT_TO_KEYWORD_MAP.put( Modifier.STATIC, "static" );
        
		KEYWORD_TO_BIT_MAP.put( "synchronized", Modifier.SYNCHRONIZED );
		BIT_TO_KEYWORD_MAP.put( Modifier.SYNCHRONIZED, "synchronized" );
        
		KEYWORD_TO_BIT_MAP.put( "abstract", Modifier.ABSTRACT );
		BIT_TO_KEYWORD_MAP.put( Modifier.ABSTRACT, "abstract" );
        
		KEYWORD_TO_BIT_MAP.put( "final", Modifier.FINAL );
		BIT_TO_KEYWORD_MAP.put( Modifier.FINAL, "final" );
        
		KEYWORD_TO_BIT_MAP.put( "native", Modifier.NATIVE );
		BIT_TO_KEYWORD_MAP.put( Modifier.NATIVE, "native" );
        
		KEYWORD_TO_BIT_MAP.put( "transient", Modifier.TRANSIENT );
		BIT_TO_KEYWORD_MAP.put( Modifier.TRANSIENT, "transient" );
        
		KEYWORD_TO_BIT_MAP.put( "volatile", Modifier.VOLATILE );
		BIT_TO_KEYWORD_MAP.put( Modifier.VOLATILE, "volatile" );
        
		KEYWORD_TO_BIT_MAP.put( "strictfp", Modifier.STRICT );
		BIT_TO_KEYWORD_MAP.put( Modifier.STRICT, "strictfp" );
		
		//FOR DEFAULT INTERFACES
		KEYWORD_TO_BIT_MAP.put( "default", 1 << 12 );
		BIT_TO_KEYWORD_MAP.put( 1 << 12, "default" );
	}

	/** is this combination of modifiers represented by a bitmask valid?*/
	public static boolean isValid( int modifiers )
	{
		if( ( ( modifiers & ( ( 1 << 13 ) -1 ) ) != modifiers ) )
		{
			return false;
		}
		if( Modifier.isAbstract(modifiers ) )
		{   //if you are abstract 
			return Integer.bitCount( 
				modifiers & 
				( Modifier.FINAL | 
				  Modifier.STATIC | 
				  Modifier.SYNCHRONIZED | 
				  Modifier.NATIVE |
				  Modifier.TRANSIENT |
				  Modifier.VOLATILE |
				  Modifier.STRICT |
                  _mod.INTERFACE_DEFAULT.bitValue ) ) == 0 
			&&
			Integer.bitCount( modifiers & ( Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED ) ) < 2;
		}
		return Integer.bitCount( modifiers & ( Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED ) ) < 2;		
	}
	
    /**
     * given a modifiers will validate and return it
     * @param modifiersBits bit
     */
	public static void validate( int modifiersBits )
	{
		if( (modifiersBits & ( ( 1 << 13 ) -1 ) ) != modifiersBits )
		{
			throw new VarException( "modifiers int contains set bits outside of range" );
		}
		
		if(  Integer.bitCount(modifiersBits & ( Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED ) ) > 1 )
		{
			throw new VarException( "cannot only be one of [public, private, protected]" );
		}
		if( ( modifiersBits & Modifier.ABSTRACT ) > 0  &&
			( modifiersBits & Modifier.FINAL ) > 0  )
		{
			throw new VarException( "cannot be both abstract and final" );
		}
		
		if( ( modifiersBits & Modifier.ABSTRACT ) > 0  &&
			( modifiersBits & Modifier.STATIC ) > 0  )
		{
			throw new VarException( "cannot be both abstract and static" );
		}
		
		if( ( modifiersBits & Modifier.ABSTRACT ) > 0  &&
			( modifiersBits & Modifier.SYNCHRONIZED ) > 0  )
		{
			throw new VarException( "cannot be both abstract and synchronized" );
		}		
		if( ( modifiersBits & Modifier.ABSTRACT ) > 0  &&
			( modifiersBits & Modifier.NATIVE ) > 0  )
		{
			throw new VarException( "cannot be both abstract and native" );
		}	
		if( Integer.bitCount( 
            modifiersBits & ( Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED ) ) > 1 )
		{
			throw new VarException( "can only be one of public, protected or private" );
		}		
	}
	
	public String author( Directive... directives )  
	{
		validate( this.mods );		
		return Author.code( BindML.compile( bitsToKeywordsString() ), VarContext.of( ), directives );
	}
	
	public String toString()
	{
		return bitsToKeywordsString();
	}
	
	private String bitsToKeywordsString()
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		int theMods = mods;
		while( theMods != 0 )
		{			
			//bithacks: isolate the rightmost bit
			int nextBit = theMods & -theMods;
		
			if( ! first )
			{
				sb.append( " " );
			}
			sb.append( BIT_TO_KEYWORD_MAP.get( nextBit ) ); 
			
			//bithacks: turn off the rightmost bit 
			theMods = theMods & ( theMods - 1 );
			first = false;
		}
		if( sb.length() > 0 )
		{
			sb.append( " " );
		}
		return sb.toString();		
	}
    
    private List<String> bitsToKeywords()
    {
        //if no modifiers
        if( Integer.bitCount( this.mods ) == 0 )
        {
            return Collections.EMPTY_LIST; //new ArrayList<String>();
        }
        
        List<String> keywords = new ArrayList<String>();
		//boolean first = true;
		int theMods = this.mods;
		while( theMods != 0 )
		{			
			//bithacks: isolate the rightmost bit
			int nextBit = theMods & -theMods;		
			keywords.add( BIT_TO_KEYWORD_MAP.get( nextBit ) ); 
			
			//bithacks: turn off the rightmost bit 
			theMods = theMods & ( theMods - 1 );
		}
		return keywords;		
    }
	
}
