package varcode.java.model;

import varcode.java.JavaCase.JavaCaseAuthor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.java.JavaCase;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.model._constructors._constructor;
import varcode.java.model._enum._valueConstructs._valueConstruct;
import varcode.java.model._methods._method;
import varcode.java.model._nest._nestGroup;
import varcode.java.model._nest.component;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _enum
	implements JavaCaseAuthor, _nest.component
{
	private _package enumPackage = new _package( "" ); 
	private _imports imports = new _imports();
	private _javadoc javadoc = new _javadoc();
    private _annotations annotations;
	private _signature enumSignature;
	private _constructors constructors = new _constructors();
	private _staticBlock staticBlock = new _staticBlock( (Object[])null );
	private _fields fields = new _fields();
	private _valueConstructs values = new _valueConstructs();
	private _methods methods = new _methods();
	private _nest._nestGroup nests = new _nestGroup();
	
	public static final Dom ENUM = 
		BindML.compile( 
			"{+pckage+}" +
			"{{+?imports:{+imports+}" + N +"+}}" +
			"{+javadocComment+}" +
            "{+enumAnnotations+}" +        
			"{+enumSignature*+}" + N +
			"{" + N +
			"{+values+}" + N +
			"{{+?members:{+$>(members)+}" + N +
			"+}}" +
			"{{+?constructors:{+$>(constructors)+}" + N +
			"+}}" +
			"{{+?methods:{+$>(methods)+}" + N + 
			"+}}" +
			"{{+?nested:{+$>(nested)+}" + N + 
			"+}}" +
			"{{+?staticBlock:{+$>(staticBlock)+}" + N +
			"+}}" +
			"}" );
	
	public static _enum cloneOf(_enum prototype )
	{
		return new _enum( prototype );
	}
	
	public _enum( _enum prototype )
	{
		this.enumPackage = _package.cloneOf( prototype.enumPackage );
		this.imports = _imports.cloneOf( prototype.imports );
		this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.annotations = _annotations.cloneOf( prototype.annotations );
		this.enumSignature = _signature.cloneOf( prototype.enumSignature  );
		this.constructors = _constructors.cloneOf( prototype.constructors );
		if( prototype.staticBlock!= null && !prototype.staticBlock.isEmpty() )
		{
			this.staticBlock = _staticBlock.of( prototype.staticBlock.getBody() );
		}
		else
		{
			this.staticBlock = new _staticBlock();
		}
		this.fields = _fields.cloneOf( prototype.fields );
		this.values = _valueConstructs.cloneOf( prototype.values );
		this.methods = _methods.cloneOf( prototype.methods );		
		this.nests = _nestGroup.cloneOf( prototype.nests );
	}
    
    @Override
    public _enum bind( VarContext context )
    {
        this.enumPackage.bind( context);
        this.imports.bind(context);
        this.javadoc.bind(context);
        this.annotations.bind(context);
        this.enumSignature.bind(context);
        this.constructors.bind(context);
        this.staticBlock.bind(context);
        this.fields.bind(context);
        this.values.bind(context);
        this.methods.bind(context);
        this.nests.bind(context);
        return this;
    }
    
    @Override
    public String getName()
    {
        return this.enumSignature.getName();
    }
    
    public String getPackageName()
    {
        if( this.enumPackage.isEmpty() ) 
        {
            return "";
        }    
        return this.enumPackage.getName();
    }
    
    public _annotations getAnnotations()
    {
        return this.annotations;
    }
    
    @Override
	public VarContext getContext()
	{
		String[] n = null;
		if( nests.count() > 0 )
		{
			//I need to go to each of the nested classes/ interfaces/ etc.
			// and read what thier imports are, then add these imports to my imports
			String[] nested = new String[nests.count()];
			for( int i = 0; i < nests.count(); i++ )
			{
				component comp = nests.components.get( i );
				VarContext vc = comp.getContext();
				vc.getScopeBindings().remove("pckage");
				vc.getScopeBindings().remove("imports");
				nested[ i ] = Compose.asString( comp.getDom(), vc );				
			}
			n = nested;			
		}
		_imports imp = null;
		if( this.getImports().count() > 0 )
		{
			imp = this.getImports();
		}
		_constructors cons = null;
		if( constructors.count() > 0 )
		{
			cons = constructors; 
		}
		_fields mems = null;
		if( this.fields != null && this.fields.count() > 0 )
		{
			mems = this.fields;
		}
		_methods meth = null;
		if( this.methods != null && this.methods.count() > 0 )
		{
			meth = this.methods;
		}			
		return VarContext.of(
			"pckage", this.enumPackage, 
			"imports", imp, 
			"javadocComment", javadoc,
			"enumSignature", enumSignature,
			"constructors", cons,
			"staticBlock", staticBlock,
			"members", mems,
			"values", values,
			"nested", n,
			"methods", meth );
	}
	
    public Class loadClass( )
    {
        return toJavaCase().loadClass();
    }
    
    public _enum implement( Class...implementClass )
    {
        this.enumSignature.implementsFrom.implement( implementClass );
        return this;
    }
    
    public _enum implement( String...implementClass )
    {
        this.enumSignature.implementsFrom.implement( implementClass );
        return this;
    }
    
    public Class loadClass( AdHocClassLoader adHocClassLoader )
    {
        return toJavaCase().loadClass( adHocClassLoader );
    }
    
    @Override
	public JavaCase toJavaCase( Directive... directives) 
	{	
		return JavaCase.of(
			getFullyQualifiedClassName(),
			ENUM, 
			getContext(),
			directives );
	}
    
    @Override
	public JavaCase toJavaCase( VarContext context, Directive...directives ) 
	{
        String FullClassName = this.getFullyQualifiedClassName();
        Dom classNameDom = BindML.compile( FullClassName );
        
        String theClassName = Compose.asString( classNameDom, context ); 
        
        //first lets print out the structure and optional Marks
        String authored = this.author( directives );
        
        //now compile the marks and fill them in with the context
		return JavaCase.of(
			theClassName,
			BindML.compile( authored ), 
			context,
			directives );			
	}
	
    public _package getPackage()
    {
        return this.enumPackage;
    }
    
    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }
    
    public _signature getSignature()
    {
        return this.enumSignature;
    }
    
    public _constructors getConstructors()
    {
        return this.constructors;
    }
    
    public _staticBlock getStaticBlock()
    {
        return this.staticBlock;
    }
    
    public _fields getFields()
    {
        return this.fields;
    }
    
    public _valueConstructs getValueConstructs()
    {
        return this.values;
    }
    
    public _methods getMethods()
    {
        return this.methods;
    }
    
    public _nest._nestGroup getNests()
    {
        return this.nests;
    }
    
	public String getFullyQualifiedClassName()
	{
		
		if( this.enumPackage != null && ! this.enumPackage.isEmpty() )
		{
			return this.enumPackage.getName() + "." + this.enumSignature.getName();
		}
		else
		{
			return this.enumSignature.getName();
		}
	}
	
    @Override
	public String author( Directive... directives ) 
	{
		return toJavaCase( directives ).toString();
	}

    @Override
	public String toString()
	{
		return author();
	}
	
	public static _enum of( String enumSignature )
	{
		_signature sig = _signature.of( enumSignature );
		_enum e = new _enum( sig );
		return e;
	}
	
	public static _enum of( String packageName, String enumSignature )
	{
		_signature sig = _signature.of( enumSignature );
		_enum e = new _enum( sig );
		e.packageName( packageName );
		return e;
	}
    
    public static _enum of( String javadoc, String packageName, String enumSignature )
    {
        _signature sig = _signature.of( enumSignature );
		_enum e = new _enum( sig );
		e.packageName( packageName );
        e.javadoc( javadoc );
		return e;
    }
	
	public _enum( _signature signature )
	{
		this.enumSignature = signature;
	}
	
	public _enum(
		_package enumPackage, 
		_imports imports, 
		_javadoc javadocComment, 
		_signature signature,		
		_fields members,
		_staticBlock staticBlock,
		_methods methods,
		_nestGroup nested )
	{
		this.enumPackage = enumPackage;
		this.imports = imports;
		this.javadoc = javadocComment;
		this.enumSignature = signature;
		this.staticBlock = staticBlock;
		this.fields = members;
		this.methods = methods;
		this.nests = nested;
	}
	
	public _enum packageName( String packageName )
	{
		return packageName( _package.of( packageName ) );
	}
	
	public _enum packageName( _package packageImpl )
	{
		this.enumPackage = packageImpl;
		return this;
	}
	

	/**
	 * <PRE>{@code 
	 * _class MyAClass = new _class("public A")
	 *     .addConstructor("public A( String name )", "this.name = name;");}</PRE>
	 *     
	 * @param constructorSignature
	 * @param body
	 * @return
	 */
	public _enum constructor( String constructorSignature, Object... body )
	{
		_constructors._constructor c = 
            new _constructors._constructor( constructorSignature )
                .body( body );
        if( c.getSignature().getModifiers().containsAny( 
                Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new VarException( 
                "Enum constructors cannot be public or protected" );
        }
		return constructor( c );
	}
	
	public _enum constructor( _constructor c )
	{
		constructors.addConstructor( c );
        if( c.getSignature().getModifiers().containsAny( 
                Modifier.PUBLIC, Modifier.PROTECTED ) )
        {
            throw new VarException( "Enum constructors cannot be public or protected" );
        }
		return this;
	}
	
	public _enum nest( _nest.component component )
	{
		this.nests.add( component );
		return this;
	}
	
	public _enum method( String methodSignature, String... bodyLines )
	{
		return method( _method.of( null, methodSignature, (Object[]) bodyLines ) );
	}

	public _enum method( _method m )
	{
		if( m.isAbstract() )
		{
			throw new VarException(
				"Cannot add an abstract method " + N + m + N + " to an enum " );
		}
		this.methods.addMethod( m );
		return this;
	}
	
	public _enum imports( Object...imports )
	{
		this.imports.addImports( imports );
		return this;
	}
	
    @Override
	public _imports getImports()
	{
		for( int i = 0; i < nests.count(); i++ )
		{
			this.imports.merge( nests.components.get( i ).getImports() );
		}
		return this.imports;
	}
	
    public _enum annotate( Object... annotations )
    {
        this.annotations.add( annotations );
        return this;
    }
    
	public _enum javadoc( String... comment )
	{
		this.javadoc = new _javadoc( comment );
		return this;
	}
	
	public _enum value( String name, Object...arguments )
	{
		return value( _valueConstruct.of( name, arguments ) );
	}
	
	public _enum value( _valueConstruct valueConstruct )
	{
		this.values.addEnumValue( valueConstruct );
		return this;
	}
    
    public _enum values( _valueConstructs values )
    {
        for( int i = 0; i < values.count(); i++ )
        {
            this.value( values.getAt( i ) );
        }
        return this;
    }
    /** 
     * adds a field to the enum<PRE> 
     * myEnum.field( "public final int value;" );
     * myEnum.field( "private String value = \"Butler\";" );
     * </PRE>
     * @param field field to add
     * @return this (modified)
     */
	public _enum field( String field )
	{
		fields.addFields(_fields._field.of( field ) );		
		return this;
	}
	
	public _enum staticBlock( Object... staticBlockCode )
	{
		if( this.staticBlock == null )
		{
			this.staticBlock = new _staticBlock();
		}
		this.staticBlock.addTailCode( staticBlockCode );
		return this;
	}
	
	public _enum fields( String...fields )
	{
		for( int i = 0; i < fields.length; i++ )
		{
			this.fields.addFields( _fields._field.of( fields[ i ] ) );
		}
		return this;
	}
	
	public _enum fields( _fields._field...fields )
	{
		this.fields.addFields( fields );
		return this;
	}
	
    
	/**
	 * When we construct enums each enum value
	 * 
	 * must call a constructor
	 * so for example:
	 * <PRE>
	 * enum Octal
	 * {
	 *    _0(), _1(), _2(), _3(), _4(), _5(), _6(), _7();
	 * }
	 * </PRE>
	 * 
	 * you might also call the constructors with parameters
	 * 
	 * <PRE>
	 * enum Octal
	 * {
	 *    _0(0, "zero"), _1(1, "one"), _2(2, "two"), _3(3, "three"), 
	 *    _4(4, "four"), _5(5, "five"), _6(6, "six"), _7(7, "seven");
	 * }
	 * </PRE>
	 * this abstractions contains the List and verifies that
	 *    
	 */
	public static class _valueConstructs
		implements varcode.Model
	{
        /** TODO, cant I just iterate through each time w/o having to keep this around??*/
		//private Set<String> valueNames = new HashSet<String>();
		
		private List<_valueConstruct> valueConstructs = 
			new ArrayList<_valueConstruct>();
			
		public _valueConstructs addEnumValue( _valueConstruct value )
		{
            for( int i = 0; i < this.valueConstructs.size(); i++ )
            {
                if( this.valueConstructs.get( i ).name.equals( value.name ) )
                {
                    throw new VarException( 
                        "Enum already contains a value for \"" + value.name +"\"" );
                }
            }
			this.valueConstructs.add( value );
			return this;
		}
		
		//the form is always
		// Identifier1( parameters, separated, by commas ), 
		// Identifier2( parameters, separated, by commas ),  
		//   -or-
		// Ideitifier1,
		// Ideitifier2,
		
		//NOTE : must be unique names
		
		public static _valueConstructs cloneOf( _valueConstructs prototype ) 
		{
			_valueConstructs clone = new _valueConstructs();
			for( int i = 0; i < prototype.count(); i++ )
			{
				clone.addEnumValue( _valueConstruct.cloneOf( 
			        prototype.valueConstructs.get( i ) ) );
			}
			return clone;
		}

        public _valueConstruct getAt( int index )
        {
            if( index < count() )
            {
                return this.valueConstructs.get( index );
            }
            throw new VarException(
                "Invalid value construct index [" + index + "]");
        }
            
        @Override
        public _valueConstructs bind( VarContext context )
        {
            for( int i = 0; i < this.valueConstructs.size(); i++ )
            {
                this.valueConstructs.get( i ).bind( context );
            }
            return this;
        }
                
        @Override
        public _valueConstructs replace( String target, String replacement )
        {
            for( int i = 0; i < this.valueConstructs.size(); i++ )
            {
                this.valueConstructs.get( i ).replace( target, replacement );
            }
            return this;
        }
        
        /**
         * Individual construction of an Enum Value
         */
		public static class _valueConstruct
			implements Model
		{
			private String name;
			private _arguments args;
			
			/** So:
			 * <PRE>
			 * ERIC( 42, "Michael" ),
			 * ^^^^  ^^^^^^^^^^^^^
			 * name    arguments
			 * 
			 * ERIC,
			 * ^^^^
			 * name
			 * </PRE>
			 * 
			 * @param name the name iof the enumValue
			 * @param arguments the arguments passed into the enum constructor
			 * @return the valueConstruct with name and arguments
			 */
			public static _valueConstruct of( String name, Object... arguments )
			{
				return new _valueConstruct( name, _arguments.of( arguments ) );								
			}
			
			public static _valueConstruct cloneOf( _valueConstruct construct )
			{
				return new _valueConstruct( 
					construct.name,
					_arguments.cloneOf( construct.args ) );
					
			}
            
			public _valueConstruct( String name, _arguments args )
			{
				this.name = name;
				this.args = args;
			}
			
			public Dom VALUE_CONSTRUCT = BindML.compile(
				"{+name*+}{+args+}" );			
    
            @Override
            public _valueConstruct bind( VarContext context )
            {
                this.name = Compose.asString( BindML.compile(this.name), context );
                this.args.bind( context );
                return this;
            }
            
            @Override
			public String author( Directive... directives ) 
			{
				VarContext vc = VarContext.of( "name", name );
				if( args != null && args.count() > 0 )
				{
					vc.set( "args", args );
				}
				return Compose.asString( VALUE_CONSTRUCT, vc, directives );
			}		
			
            @Override
			public String toString()
			{
				return author(); 
			}
            
            @Override
            public _valueConstruct replace( String target, String replacement )
            {
                this.args = this.args.replace( target, replacement );
                this.name = this.name.replace( target, replacement );
                return this;
            }
		}

		public Dom VALUE_CONSTRUCTORS = BindML.compile(
			"{{+:    {+valueConstructs+}," + N 
		 + "+}};" + N + N );
		
        @Override
		public String author( Directive... directives ) 
		{
			return Compose.asString( 
				VALUE_CONSTRUCTORS, 
				VarContext.of( "valueConstructs", valueConstructs ),
				directives );
		}
		
		public int count()
		{
			return this.valueConstructs.size();
		}
		
        @Override
		public String toString()
		{
			return author();
		}
	}
	
    /**
     * Enum Constructor signature
     */
	public static class _signature
		implements Model
	{
		private String enumName = "";		
		private _implements implementsFrom = new _implements();
		private _modifiers modifiers = new _modifiers();
		
		public static final Dom CLASS_SIGNATURE = 
			BindML.compile( "{+modifiers+}enum {+enumName*+}{+implementsFrom+}" );

        @Override
		public String author( Directive... directives ) 
		{
			return Compose.asString( CLASS_SIGNATURE, 
				VarContext.of(
					"enumName", enumName,
					"modifiers", modifiers,
					"implementsFrom", implementsFrom ), 
				directives );
		} 
        
        @Override
        public _signature bind( VarContext context )
        {
            this.enumName = Compose.asString(BindML.compile(this.enumName), context );
            this.implementsFrom.bind( context );
            this.modifiers.bind( context );
            return this;
        }
        
		public static _signature cloneOf( _signature prototype ) 
		{
			_signature s = new _signature();
			s.enumName = prototype.enumName + "";
			s.implementsFrom = _implements.cloneOf( prototype.implementsFrom );
			s.modifiers = _modifiers.cloneOf( prototype.modifiers );
			return s; 			
		}

        @Override
		public String toString()
		{
			return author();
		}

        @Override
        public _signature replace( String target, String replacement )
        {
            this.enumName = this.enumName.replace(target, replacement);
            this.implementsFrom = this.implementsFrom.replace( target, replacement );
            this.modifiers = this.modifiers.replace( target, replacement );
            return this;
        }
        
		public _modifiers getModifiers()
		{
			return this.modifiers;
		}
		public _implements getImplements()
		{
			return this.implementsFrom;
		}
		
		public String getName()
		{
			return this.enumName;
		}
        
        /**
         * finds the index of the target token 
         * @param tokens
         * @param target
         * @return 
         */
        private static int indexOf( String[] tokens, String target )
        {
            for( int i = 0; i < tokens.length; i++ )
            {
                if( tokens[ i ].equals( target ) )
                {
                    return i;
                }
            }
            return -1;
        }
        
		public static _signature of( String enumSignature )
		{
			_signature sig = new _signature();
		
			String[] tokens = enumSignature.split(" ");
		
			int enumTokenIndex = -1;
			int implementsTokenIndex = -1;
		
            if( indexOf( tokens, "enum" ) < 0 )
            {
                //infer they want a public class
                String[] preamble = new String[ tokens.length + 2 ];  
                preamble[0] = "public";
                preamble[1] = "enum";
                System.arraycopy( tokens, 0, preamble, 2, tokens.length );
                tokens = preamble;
            }
			if( tokens.length < 2 )
			{
				throw new VarException( 
                    "enum signature must have at least (2) tokens \"enum enumName\" " );	
			} 		
			for( int i = 0; i < tokens.length; i++ )
			{
				if( tokens[ i ].equals( "enum" ) )
				{
					enumTokenIndex = i;
				}			
				else if( tokens[ i ].equals( "implements" ) )
				{
					implementsTokenIndex = i;
				}
			}
		
			if( ( enumTokenIndex < 0 ) || ( enumTokenIndex >= tokens.length -1 ) )
			{   //cant be 
				throw new VarException( 
					"enum token cant be not found or the last token for \"" 
                        + enumSignature + "\"" ); 
			}
			sig.enumName = tokens[ enumTokenIndex + 1 ];
		
			if( enumTokenIndex > 0 )
			{   //modifier provided
				String[] mods = new String[ enumTokenIndex ];
				System.arraycopy( tokens, 0, mods, 0, enumTokenIndex );
				sig.modifiers = _modifiers.of( mods );
				if( sig.modifiers.containsAny( 
					Modifier.ABSTRACT, 
					Modifier.FINAL, 
					Modifier.NATIVE,
					Modifier.STRICT,
					Modifier.STATIC,
					Modifier.PROTECTED,
					Modifier.PRIVATE,
					Modifier.SYNCHRONIZED, 
					Modifier.TRANSIENT, 
					Modifier.VOLATILE,
                    _modifiers._mod.INTERFACE_DEFAULT.getBitValue() ) )
				{
					throw new VarException(
						"Invalid Modifier(s) for enum of \"" + enumSignature + "\" only public allowed" );
				}
			}		
			if( implementsTokenIndex > enumTokenIndex + 1 )
			{
				if( implementsTokenIndex == tokens.length -1 )
				{
					throw new VarException( "implements token cannot be the last token" );
				}
				int tokensLeft = tokens.length - ( implementsTokenIndex + 1 );
				String[] implementsTokens = new String[ tokensLeft ];
				System.arraycopy(tokens, implementsTokenIndex + 1, implementsTokens, 0, tokensLeft );
				List<String>normalImplementsTokens = new ArrayList<String>();
				for( int i = 0; i < implementsTokens.length; i++)
				{
					if( implementsTokens[ i ].contains( "," ) )
					{
						String[] splitTokens = implementsTokens[ i ].split( "," );
						for( int j = 0; j < splitTokens.length; j++ )
						{
							String tok = splitTokens[ j ].trim();
							if( tok.length() > 0 )
							{ 
								normalImplementsTokens.add( tok );
							}
						}
					}
				}
				sig.implementsFrom = 
                    varcode.java.model._implements.of( 
                        normalImplementsTokens.toArray( new String[ 0 ] ) ); //className.of( implementsTokens );
			}
			return sig;		
		}

		public String getImplements( int index ) 
		{			
			return implementsFrom.getAt( index );
		}	
	}

    @Override
	public Dom getDom() 
	{
		return ENUM;
	}

    @Override
	public _enum replace( String target, String replacement  ) 
	{
        this.constructors = this.constructors.replace( target, replacement );
        this.enumPackage = this.enumPackage.replace( target, replacement);
        this.enumSignature = this.enumSignature.replace( target, replacement );
        return this;
	}
}
