package varcode.java.code;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import varcode.CodeAuthor;
import varcode.Template;

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
    extends Template.Base
{
	//private static final Logger LOG = LoggerFactory.getLogger( _members.class );
	
    public static final Dom FIELDS = 
        BindML.compile( 
            "{{+?staticFields:{+staticFields+}" + N +
            "+}}"+
            "{{+?instanceFields:{+instanceFields+}" + N +
            "+}}" );
    
	public static _fields from( _fields prototype ) 
	{
		_fields fs = new _fields();
		
		String[] fieldNames = prototype.fields.keySet().toArray(new String[0] );
		
		for( int i = 0; i < fieldNames.length; i++)
		{			
			fs.addFields( _field.of( prototype.fields.get( fieldNames[ i ] ) ) );
		}
		return fs;		
	}
	
    private final Map<String,_field>fields;
    
    public _fields()
    {
    	fields = new TreeMap<String, _field>();
    }
    
    public int count()
    {
    	return fields.size();
    }
    
    public Map<String,_field>fieldMap()
    {
    	return fields;
    }
    
    //returns the names of all the fields
    public String[] getFieldNames()
    {
        return this.fields.keySet().toArray( new String[ 0 ] );
    }
    
    public _field getByName( String name )
    {
    	return fields.get( name );
    }
    public void replace( String target, String replacement )
    {
        //TODO do I need to check fields, methods??
		String[] fieldNames = this.fieldMap().keySet().toArray( new String[ 0 ] );
		for( int i = 0; i < fieldNames.length; i++ )
		{			
			_fields._field f = this.fields.get( fieldNames[ i ] );
            f.javadoc.replace( target, replacement );
            f.init.replace( target, replacement );
            f.varName = f.varName.replace( target, replacement );
            f.type = f.type.replace( target, replacement );
			/*
            if( f.getJavadoc() != null )
			{
				f.getJavadoc().replace( target, replacement );
			}
			if( f.hasInit() )
			{   //
				f.init( f.getInit().replace( target, replacement ) );
			}
			if( f.getType().getName().contains( target ) )
			{
				f.setType( f.getType().getName().replace(target, replacement ) );
			}
            */
		}
    }
    
    public _fields addFields( _field... fields )
    {
    	for(int i = 0; i < fields.length; i++ )
    	{
    		_field m = this.fields.put( fields[ i ].varName.toString(), fields[ i ] );
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
			memberFields.addFields( _field.of( fields[ i ] ) );			
		}
		return memberFields;
	}
	
	public static class _field
		implements CodeAuthor
	{
		public static final Dom FIELD = BindML.compile(
			"{+javadocComment+}{+modifiers+}{+type+} {+varName+}{+init+};" ); 
		
		public static _field of( _field prototype )
		{
			_field f = new _field( 
				_modifiers.of( prototype.mods.getBits() ),
				prototype.type+ "",
				prototype.varName + "" );
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
		
		public _field init( _init initialization )
		{
			this.init = initialization;
			return this;
		}
		
		public String getType()
		{
			return this.type;
		}
		
		public _modifiers getModifiers()
		{
			return this.mods;
		}
		
		public String getName()
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
		
		public static _field of( String fieldDef )
		{
			return of( null, fieldDef );
		}
		
		public static _field of( String javadoc, String fieldDef )
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
				_field f = parseField( member );
				if( javadoc != null )
				{
					f.javadoc( javadoc );
				}
				return f.setInit( init );
			}
			_field f = parseField( fieldDef );
			if( javadoc != null )
			{
				f.javadoc( javadoc );
			}
			return f;
		}
		
		private static _field parseField( String fieldDef )
		{
			String[] tokens = _var.normalizeTokens( fieldDef );
			if( tokens.length < 2 )
			{
				throw new VarException( "Expected at least (2) tokens for field <type> <name>" );
			}
			String name = tokens[ tokens.length - 1 ];
            
			//_type t = varcode.java.code._type.of( tokens[ tokens.length - 2 ] );
			
            String t = tokens[ tokens.length - 2 ];
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
				return new _field( mods, t, name );
			}
			return new _field( new _modifiers(), t, name );
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
		private String type; 
		private String varName;
		private _init init;
		
		public _field( _modifiers modifiers, String type, String varName )
		{
			this.mods = modifiers;
			this.type = type;
			this.varName = varName;
		}
		
		public _field( _modifiers modifiers, String type, String varName, _init init )
		{
			this.mods = modifiers;
			this.type = type;
			this.varName = varName;
			this.init = init;			
		}
		
		public _field setInit( String init )
		{
			this.init = new _init( init );
			return this;
		}
		
		public _field javadoc( String javaDocComment )
		{
			this.javadoc = new _javadoc( javaDocComment );
			return this;
		}

		/** does this field have an initial value */
		public boolean hasInit() 
		{
			return init != null && !init.isEmpty();
		}


		public _field setType( String newType )  
		{
			this.type = newType;
			return this;
		}
	}
	
    public VarContext getContext()
    {
        List<_field> staticFields = new ArrayList<_field>();
		List<_field> instanceFields = new ArrayList<_field>();
		String[] allKeys = fields.keySet().toArray( new String[ 0 ] );
		for( int i = 0; i < allKeys.length; i++ )
		{
			_field mem = fields.get( allKeys[ i ] );
			if( mem.mods.containsAny( Modifier.STATIC ) )
			{
				staticFields.add( mem );
			}
			else
			{
				instanceFields.add( mem );
			}
		}
        return VarContext.of("staticFields", staticFields,
			"instanceFields", instanceFields ); 
    }
    
    public String author(  Directive...directives )
    {             
        return Author.code( 
			FIELDS, 
            getContext(),			
			directives );	
    /*
	public String author( Directive... directives ) 
	{		

		return Author.code(
			BindML.compile( staticFields.toString() + nonStaticFields.toString() + N ), 
			VarContext.of(), 
			directives ) ;
	}
    */
    }
    
	public String toString()
	{
		return author( );
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
		implements CodeAuthor
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
