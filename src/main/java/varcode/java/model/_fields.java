package varcode.java.model;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * 
 * members of( "int x;" );
 * members of( "String[] names" );
 * members.of( "public static Map<Integer,String>map" ); 
 * members.of( "public static final String name = new Member(\" \");" );
 *  
 */
public class _fields
	implements SelfAuthored
{
	//private static final Logger LOG = LoggerFactory.getLogger( _members.class );
	
	public static _fields from( _fields prototype ) 
	{
		_fields fs = new _fields();
		
		String[] fieldNames = prototype.fields.keySet().toArray(new String[0] );
		
		for( int i = 0; i < fieldNames.length; i++)
		{			
			fs.addFields( field.of( prototype.fields.get( fieldNames[ i ] ) ) );
		}
		return fs;
		
	}
	
    private final Map<String,field>fields;
    
    public _fields()
    {
    	fields = new TreeMap<String, field>();
    }
    
    public int count()
    {
    	return fields.size();
    }
    
    public Map<String,field>fieldMap()
    {
    	return fields;
    }
    
    public field byName( String name )
    {
    	return fields.get( name );
    }
    
    public _fields addFields( field... fields )
    {
    	for(int i = 0; i < fields.length; i++ )
    	{
    		field m = this.fields.put( fields[ i ].varName.toString(), fields[ i ] );
    		if( m != null )
    		{
    			//Log replaced
    		}
    	}
    	return this;
    }
    
	public static _fields of( String...fields )
	{
		_fields memberFields = new _fields();
		for( int i = 0; i < fields.length; i++ )
		{
			memberFields.addFields( field.of( fields[ i ] ) );			
		}
		return memberFields;
	}
	
	public static class field
		implements SelfAuthored
	{
		public static final Dom FIELD = BindML.compile(
			"{+javadocComment+}{+modifiers+}{+type+} {+varName+}{+init+};" ); 
		
		public static field of( field prototype )
		{
			field f = new field( 
				_modifiers.of( prototype.mods.getBits() ),
				_type.from( prototype.type ),
				_identifier.from( prototype.varName ) 
				);
			if( prototype.init != null && !prototype.init.isEmpty() )
			{
				f.setInit( prototype.init.initCode );
			}
			if( prototype.javadoc != null 
				&& ! ( prototype.javadoc.getComment() == null ) 
				&& ( prototype.javadoc.getComment().trim().length() == 0 ) )
			{
				f.javadoc( prototype.javadoc.getComment() );
			}
			return f;
		}
		
		
		public field init( _init initialization )
		{
			this.init = initialization;
			return this;
		}
		
		public _type getType()
		{
			return this.type;
		}
		
		public _modifiers getModifiers()
		{
			return this.mods;
		}
		
		public _identifier getName()
		{
			return this.varName;
		}
		
		public _javadoc getJavadoc()
		{
			return this.javadoc;
		}
		
		public _init getInit()
		{
			return this.init;
		}
		
		public static field of( String fieldDef )
		{
			return of( null, fieldDef );
		}
		
		public static field of( String javadoc, String fieldDef )
		{
			if( fieldDef.endsWith( ";" ) )
			{
				fieldDef = fieldDef.substring( 0, fieldDef.length() -1 );			
			}
			int indexOfEquals = fieldDef.indexOf('=');
			
			if( indexOfEquals > 0 )
			{   //there is an init AND a member 
				String member = fieldDef.substring( 0, indexOfEquals );
				String init = fieldDef.substring( indexOfEquals + 1 );
				field f = parseField( member );
				if( javadoc != null )
				{
					f.javadoc( javadoc );
				}
				return f.setInit( init );
			}
			field f = parseField( fieldDef );
			if( javadoc != null )
			{
				f.javadoc( javadoc );
			}
			return f;
		}
		
		private static field parseField( String fieldDef )
		{
			String[] tokens = _var.normalizeTokens( fieldDef );
			if( tokens.length < 2 )
			{
				throw new VarException( "Expected at least (2) tokens for field <type> <name>" );
			}
			_identifier name = _identifier.of( tokens[ tokens.length - 1 ] );
			_type t = varcode.java.model._type.of( tokens[ tokens.length - 2 ] );
			
			if( tokens.length > 2 )
			{
				String[] arr = new String[ tokens.length - 2 ];
				System.arraycopy( tokens, 0, arr, 0, arr.length );
				_modifiers mods = _modifiers.of( arr ); 
				if( mods.containsAny( 
					Modifier.ABSTRACT, Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.STRICT ) )
				{
					throw new VarException( "field contains invalid modifier " + N + mods.toString() );
				}
				if( mods.containsAll( Modifier.FINAL, Modifier.VOLATILE ) ) 
				{
					throw new VarException( "field cannot be BOTH final AND volatile" );
				}
				return new field( mods, t, name );
			}
			return new field( new _modifiers(), t, name );
		}
		
		public String author( Directive... directives ) 
		{
			return Author.code( FIELD, 
				VarContext.of( 
					"javadocComment", javadoc,
					"modifiers", mods,
					"type", type,
					"varName", varName,
					"init", init ),
				directives );
		}
		
		public String toString()
		{
			return author();
		}
		
		private _javadoc javadoc;
		private _modifiers mods;
		private _type type; 
		private _identifier varName;
		private _init init;
		
		public field( _modifiers modifiers, _type type, _identifier varName )
		{
			this.mods = modifiers;
			this.type = type;
			this.varName = varName;
		}
		
		public field( _modifiers modifiers, _type type, _identifier varName, _init init )
		{
			this.mods = modifiers;
			this.type = type;
			this.varName = varName;
			this.init = init;			
		}
		
		public field setInit( String init )
		{
			this.init = new _init( init );
			return this;
		}
		
		public field javadoc( String javaDocComment )
		{
			this.javadoc = new _javadoc( javaDocComment );
			return this;
		}

		/** does this field have an initial value */
		public boolean hasInit() 
		{
			return init != null && !init.isEmpty();
		}


		public field setType( String newType )  
		{
			this.type = _type.of( newType );
			return this;
		}
	}
	
	public String author( Directive... directives ) 
	{		
		StringBuilder staticFields = new StringBuilder();
		StringBuilder nonStaticFields = new StringBuilder();
		String[] allKeys = fields.keySet().toArray( new String[ 0 ] );
		for( int i = 0; i < allKeys.length; i++ )
		{
			field mem = fields.get( allKeys[ i ] );
			if( mem.mods.containsAny( Modifier.STATIC ) )
			{
				staticFields.append(mem.author( directives ) );
				staticFields.append( N );				
			}
			else
			{
				nonStaticFields.append(mem.author( directives ) );
				nonStaticFields.append( N );
			}
		}
		return Author.code(
			BindML.compile( staticFields.toString() + nonStaticFields.toString() + N ), 
			VarContext.of(), 
			directives ) ;
	}
	
	public String toString()
	{
		return author();
	}
	
	/** Field Initialization
	 * <PRE>
	 * i.e. public int x = 100;
	 *                   ^^^^^
	 *      public static final String NAME = "Eric";
	 *                                      ^^^^^^^^
	 * </PRE>        
	 */
	public static class _init 
		implements SelfAuthored
	{
		private final String initCode;
	
		public _init()
		{
			this.initCode = null;
		}
	
		public boolean isEmpty()
		{
			return initCode == null || initCode.trim().length() == 0;
		}
		
		public _init( String code )
		{
			if( code.trim().startsWith( "=" ) )
			{
				this.initCode = code.substring( code.indexOf( "=" ) + 1 ).trim();
			}
			else
			{
				this.initCode = code;
			}
		}

		public static final Dom INIT = BindML.compile( " ={+initCode*+}" );
	
		public String author( Directive... directives ) 
		{
			if( initCode != null )
			{
				return Author.code( INIT, VarContext.of( "initCode", initCode ), directives );
			}
			return "";
		}
	
		public String toString()
		{
			return author();
		}
		
		public String getCode()
		{
			return initCode;
		}

		public _init replace( String target, String replacement ) 
		{
			return new _init ( this.initCode.replace( target, replacement ) );			
		}
	}

	
}
