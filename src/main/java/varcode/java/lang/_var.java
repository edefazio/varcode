package varcode.java.lang;


import java.util.ArrayList;
import java.util.List;
import varcode.VarException;

import varcode.context.VarBindings;
import varcode.context.VarBindings.SelfBinding;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.java.JavaNaming;
import varcode.markup.bindml.BindML;

/**
 * The idea of a variable
 * 
 * @author M. Eric DeFazio
 */
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
	this.type = _type.of( type );
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
		toks[ i ] = toks[ i ].substring( 0, toks[ i ].length() -1 ); 
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
    
    /**
     * TODO I need a void-included type (i.e. for return types) and a
     * void-free type (i.e. for parameter lists)
     *  
     * @author M. Eric DeFazio eric@varcode.io
     */
    public static class _type
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
	
        @Override
	public String toString()
	{
            return typeName;
	}		
    }/* type */

    public static class _identifier
    {
	public static _identifier cloneOf( _identifier id )
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
		
        @Override
	public String toString()
	{
		return identifierName;
	}				
    }
}
