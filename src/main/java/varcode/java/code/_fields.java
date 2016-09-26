package varcode.java.code;

import java.lang.reflect.Modifier;
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
    
	public static _fields cloneOf( _fields prototype ) 
	{
		_fields fs = new _fields();
		
        for( int i = 0; i < prototype.fields.size(); i++)
		{			
			fs.addFields( _field.cloneOf( prototype.fields.get( i ) ) );
		}
		return fs;		
	}
	
    /** the fields in order of addition */
    private final List<_field> fields;
    
    public _fields()
    {
        fields = new ArrayList<_field>();
    }
    
    public _fields bindIn( VarContext context )
    {
        for( int i = 0; i < fields.size(); i++ )
        {
            this.fields.get( i ).bindIn( context );
        }
        return this;
    }
    
    public int count()
    {
    	return fields.size();
    }

    /** returns the field at this index */
    public  _field getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return fields.get( index );
        }
        throw new VarException( "invalid field index ["+ index + "]" );
    }
            
    //returns the names of all the fields
    public String[] getFieldNames()
    {
        String[] fieldNames = new String[ this.fields.size() ];
        for( int i = 0; i < fieldNames.length; i++ )
        {
            fieldNames[ i ] = fields.get( i ).name;
        }
        return fieldNames;
    }
    
    /** Verify there is no other field with this name */
    public boolean canAddFieldName( String fieldName )
    {
        for( int i = 0; i < fields.size(); i++ )
        {
            if( this.fields.get( i ).name.equals( fieldName ) )
            {
                return false;
            }
        }
        return true;
    }
    
    public _field getByName( String name )
    {
    	for( int i = 0; i < this.fields.size(); i++ )
        {
            if( this.fields.get( i ).name.equals( name ) )
            {
                return this.fields.get( i ); 
            }
        }
        return null;        
    }
    
    /**
     * replaces the target string with the replacement String
     * @param target
     * @param replacement 
     * @return this field after modification 
     */
    public _fields replace( String target, String replacement )
    {
		for( int i = 0; i < fields.size(); i++ )
		{		            
			_fields._field f = this.fields.get( i );
            f.replace( target, replacement );
		}
        return this;
    }
    
    /** Adds one or more _field to the _fields */
    public _fields addFields( _field... fields )
    {
    	for( int i = 0; i < fields.length; i++ )
    	{
            //check 
            if( canAddFieldName( fields[ i ].name ) )
            {
                this.fields.add( fields[ i ] );
            }
            else
    		{
    			throw new VarException(
                    "cannot add field with name \""
                   + fields[ i ].name 
                  + "\" a field with the same name already exists" );
    		}
    	}
    	return this;
    }
    
    /**
     * Create a new _fields of the member fields
     * @param fields each _field to be included in the _fields
     * @return a new _fields including fields
     */
	public static _fields of( String...fields )
	{
		_fields memberFields = new _fields();
		for( int i = 0; i < fields.length; i++ )
		{
			memberFields.addFields( _field.of( fields[ i ] ) );			
		}
		return memberFields;
	}
	
    /**
     * field 
     */
	public static class _field
		extends Template.Base
	{
		public static final Dom FIELD = BindML.compile(
			"{+javadoc+}{+fieldAnnotations+}{+modifiers+}{+type+} {+varName+}{+init+};" ); 
		
		public static _field cloneOf( _field prototype )
		{
			_field f = new _field( 
				_modifiers.of( prototype.mods.getBits() ),
				prototype.type + "",
				prototype.name + "" );
            
			if( prototype.init != null && !prototype.init.isEmpty() )
			{
				f.setInit( prototype.init.initCode );
			}
            //System.out.println("CLONE for "+ prototype );
			if( prototype.javadoc != null 
				&& !( prototype.javadoc.getComment() == null ) 
				&& ( prototype.javadoc.getComment().trim().length() > 0 ) )
			{
                //System.out.println ( "cloning JDoc " + prototype.javadoc.getComment() );
				f.javadoc( prototype.javadoc.getComment() );
			}
            if( prototype.fieldAnnotations != null 
                && !prototype.fieldAnnotations.isEmpty() )
            {
                f.annotate( prototype.fieldAnnotations );
            }
			return f;
		}
		
		public _field init( _init initialization )
		{
			this.init = initialization;
			return this;
		}
        
		public _field annotate( Object...annotations )
        {
            this.fieldAnnotations.add( annotations );
            return this;
        }
        
        public _annotate getAnnotations()
        {
            return this.fieldAnnotations;
        }
        
		public String getType()
		{
			return this.type;
		}
		
		public _modifiers getModifiers()
		{
			return this.mods;
		}
		
        @Override
        public _field replace( String target, String replacement )
        {
            javadoc.replace( target, replacement );
            init.replace( target, replacement );
            fieldAnnotations.replace( target, replacement );
            name = name.replace( target, replacement );
            type = type.replace( target, replacement );
            return this;
        }
        
        public _field bindIn( VarContext context )
        {
            this.javadoc.bindIn( context );
            this.init.bindIn( context );
            this.fieldAnnotations.bindIn(context);
            this.mods.bindIn( context );
            this.name = Author.code( BindML.compile(this.name), context );
            this.type = Author.code( BindML.compile(this.type), context );
            return this;
        }
        
		public String getName()
		{
			return this.name;
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
		
        /**
         * DOES NOT Handle parsing Field Annotations (only field signatures)
         * 
         * @param fieldDef the definition of the field (sans annotations)
         * @return a _field based on the field
         */
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
		
        public VarContext getContext()
        {
            return VarContext.of(
                "javadoc", this.javadoc,
                "fieldAnnotations", this.fieldAnnotations,    
				"modifiers", this.mods,
				"type", this.type,
				"varName", this.name,
				"init", this.init );
        }
        @Override
		public String author( Directive... directives ) 
		{
			return Author.code( FIELD, 
				getContext(),
				directives );
		}
		
        @Override
		public String toString()
		{
			return author();
		}
		
        private _annotate fieldAnnotations;
		private _javadoc javadoc;
		private _modifiers mods;
		private String type; 
		private String name;
		private _init init;
		
		public _field( _modifiers modifiers, String type, String name )
		{
			this.mods = modifiers;
			this.type = type;
			this.name = name;
            this.javadoc = new _javadoc();
            this.init = new _init();
            this.fieldAnnotations = new _annotate();
		}
		
		public _field( _modifiers modifiers, String type, String varName, _init init )
		{
			this.mods = modifiers;
			this.type = type;
			this.name = varName;
			this.init = init;			
            this.javadoc = new _javadoc();
            this.fieldAnnotations = new _annotate();
		}
		
		public _field setInit( String init )
		{
			this.init = new _init( init );
			return this;
		}
		
		public _field javadoc( String javadoc )
		{
			this.javadoc = new _javadoc( javadoc );
			return this;
		}

		/** does this field have an initial value
         * @return  true if the field has initialization, false otherwise
         */
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
		for( int i = 0; i < fields.size(); i++ )
		{
			_field mem = fields.get( i );
			if( mem.mods.containsAny( Modifier.STATIC ) )
			{
				staticFields.add( mem );
			}
			else
			{
				instanceFields.add( mem );
			}
		}
        
        return VarContext.of( 
            "staticFields", staticFields,
			"instanceFields", instanceFields ); 
    }
    
    public String author(  Directive...directives )
    {             
        return Author.code( 
			FIELDS, 
            getContext(),			
			directives );	
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
		extends Template.Base
	{
		private String initCode;
	
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
	
        public _init bindIn( VarContext context )
        {
            if( this.initCode != null )
            {
                this.initCode = Author.code( BindML.compile( this.initCode ), context );
            }
            return this;
        }
        
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
            if( this.initCode != null )
            {
                return new _init ( this.initCode.replace( target, replacement ) );			
            }
            return this;
		}
	}	
}
