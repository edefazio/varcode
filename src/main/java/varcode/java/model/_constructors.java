package varcode.java.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Model for building one or more constructors 
 * (belonging to a {@code _class} or {@code_enum}) 
 * 
 * Note: there are dependencies among all constructors
 * (i.e. each must have a unique signature).
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _constructors
    implements Model
{
	private List<_constructor>constructors = new ArrayList<_constructor>();

    /**
     * Builds and returns a clone of the prototype constructors
     * @param prototype baseline to build clone from
     * @return a new _constructors based from the prototype
     */
	public static _constructors cloneOf( _constructors prototype ) 
	{
		_constructors ctors = new _constructors();
		for( int i = 0; i < prototype.count(); i++ )
		{
			ctors.addConstructor( 
				_constructor.cloneOf( prototype.constructors.get( i ) ) );
		}
		return ctors;
	}
	
	/**
	 * ex:<PRE> 
	 * _constructors cs = _constructors.of(
	 *    "public MyClass(String message)",
	 *    "this.message = message;" ); 
     * </PRE>
	 * @param signature
	 * @param linesOfCode
	 * @return
	 */
	public static _constructors of( String signature, Object...linesOfCode )
	{
		_constructors cs = new _constructors();
		cs.addConstructor( signature, linesOfCode );
		return cs;
	}
	
	public _constructors()
	{
	}
    
    @Override
    public _constructors bind( VarContext context )
    {
        for( int i = 0; i < this.constructors.size(); i++ )
        {
            this.constructors.get( i ).bind( context ); 
        }
        return this;
    }
    
	public List<_constructor> getConstructors()
	{
		return this.constructors;
	}
    
    public _constructor getAt( int index )
    {
        if( index >= 0  && index < count() )
        {
            return this.constructors.get( index );
        }
        throw new VarException( "invalid index [" + index + "]" );
    }
    
	public static final Dom CONSTRUCTORS = BindML.compile(
		"{{+?constructors:{+constructors+}" + N + "+}}" );

    @Override
	public String author( Directive... directives ) 
	{
		if( constructors.size() > 0 )
		{
			return Compose.asString(
				CONSTRUCTORS,
				VarContext.of( "constructors", constructors ), 
				directives );
		}	
		return "";
	}

    @Override
	public String toString()
	{
		return author();
	}

	private void verifyNoConflicts( _constructor constructor )	
	{
		for( int i = 0; i < this.constructors.size(); i++ )
		{
			if( constructors.get( i ).matchesExisting( constructor ) )
			{
				throw new VarException( 
					"Error, adding constructor " + N + constructor.toString() + N + 
					"a constructor with the same signature found " + N +
					constructors.get( i ).toString() );
			}
		}
	}

	public _constructors addConstructor( String constructorSignature, Object... bodyLines )
	{
		_constructor c = new _constructor( constructorSignature ).body( bodyLines );
		return addConstructor( c );
	}

	public int count()
	{
		return constructors.size();
	}
	
	public _constructors addConstructor( _constructor constructor )
	{
        verifyNoConflicts( constructor );
		constructors.add( constructor );
		return this; 
	}
	
	/** Individual constructor model */
	public static class _constructor
		implements Model
	{
        public static _constructor of( String constructorSig, Object... body )
        {
            _constructor ctor = new _constructor( constructorSig );
            ctor.body( body );
            return ctor;
        }
        
		public static _constructor cloneOf( _constructor prototype )
		{
			_constructor ctor =  new _constructor(
				_signature.cloneOf( prototype.constructorSig ) );
			ctor.annotations = prototype.getAnnotations();
            if( prototype.javadoc != null )
            {
                ctor.javadoc = _javadoc.of( prototype.getJavadoc().getComment() );
            }
            
			ctor.body( prototype.body );
			return ctor;			
		}
		
        private _annotations annotations;
        
        private _javadoc javadoc;
        
		private _signature constructorSig;
		
		private _code body;
	
        public _constructor( _modifiers modifiers,
            String name,
			_parameters params,
			_throws throwsExceptions )
        {
            this( new _signature( 
                modifiers, name, params, throwsExceptions ) );
            
        }
        
		public _constructor( _signature sig )
		{
			this.constructorSig = sig;
            this.annotations = new _annotations();
		}
    
        @Override
        public _constructor bind( VarContext context )
        {
            this.constructorSig = this.constructorSig.bind( context );
            this.body = this.body.bind( context );
            if( this.javadoc != null )
            {
                this.javadoc = this.javadoc.bind( context );
            }
            if( this.annotations != null )
            {
                this.annotations.bind( context );
            }
            return this;
        }
		
		public _constructor( String constructorSignature )
		{
			this.constructorSig = _signature.of( constructorSignature );
            this.annotations = new _annotations();
		}
	
        public _constructor annotate( Object...annotations )
        {
            this.annotations.add( annotations );
            return this;
        }
        
        public _constructor setSignature( _signature sig )
        {
            this.constructorSig = sig;
            return this;
        }
        
        public _constructor setName( String name )
        {
            this.constructorSig.className = name;
            return this;
        }
        
        public _signature getSignature()
        {
            return constructorSig;
        }
        
        public _constructor setJavadoc( _javadoc javadoc )
        {
            this.javadoc = javadoc;
            return this;
        }
        
        public _javadoc getJavadoc()
        {
            return this.javadoc;
        }
        
        public _constructor setAnnotations( _annotations annotations )
        {
            this.annotations = annotations;
            return this;
        }
        
        public _annotations getAnnotations()
        {
            return this.annotations;
        }
        
        public _constructor setBody( _code body )
        {
            this.body = body;
            return this;
        }
        
        public _code getBody()
        {
            return this.body;
        }
        
        @Override
        public _constructor replace( String target, String replacement )
        {
            if( this.javadoc != null  && ! this.javadoc.isEmpty() )
            {
                this.javadoc.replace( target, replacement );
            }
            
            if( this.annotations != null  && ! this.annotations.isEmpty() )
            {
                this.annotations.replace( target, replacement );
            }
            
            this.body.replace( target, replacement );
			
            this.constructorSig.replace( target, replacement );
			
            return this;
        }
        
        public _constructor javadoc( String comment )
        {
            this.javadoc = new _javadoc( comment );
            return this;
        }
        
		public _constructor body( Object... bodyLines )
		{
			if( bodyLines == null || bodyLines.length == 0 )
			{
				this.body = null;
			}
			this.body = _code.of( bodyLines );
		
			return this;
		}
	
		/**
		 * Ask if the constructor matches an existing constructor
		 * (contains the same number of parameters of the same type)
		 * 
		 * NOTE: this is not comprehensive on the account of Polymorphism
		 * but intends to catch the most flagrant constructors violations.
		 * 
		 * @param constructor
		 * @return
		 */
		public boolean matchesExisting( _constructor constructor )
		{
			_constructor._signature sig =  constructor.constructorSig;
			
			if ( sig.className.equals( this.constructorSig.className ) )
			{
				if( sig.params.count() == this.constructorSig.params.count() )
				{				
					for( int i = 0; i < sig.params.count(); i++ )
					{						
						if(! sig.params.getAt( i ).getType().equals
							( this.constructorSig.params.getAt( i ).getType() ) )
						{							
							return false;
						}
						//I could also try to check generic and extensions, but lets just do this
					}
					return true;
				}
			}
			return false;
		}
	
		public static final Dom CONSTRUCTOR = 
			BindML.compile(
                "{+javadoc+}" + 
                "{+annotations+}" +         
				"{+constructorSig*+}" + N +
				"{" + N +
				"{+$>(body)+}" + N +
				"}" );
	        
        @Override
		public String author( Directive... directives ) 
		{
			return Compose.asString(
				CONSTRUCTOR, 
				VarContext.of(
                    "javadoc", javadoc,    
                    "annotations", this.annotations,   
					"constructorSig", constructorSig,
					"body", body ),
				directives );
		}
	
        @Override
		public String toString()
		{
			return author();
		}
	
        /** constructor signature */
		public static class _signature
			implements Model
		{
			private String className;
			private _modifiers modifiers; //public protected private
			private _parameters params;
			private _throws throwsExceptions;

			public _signature(
				_modifiers modifiers, 
				String className, 
				_parameters params,
				_throws throwsExceptions )
			{
				this.modifiers = modifiers;
				this.className = className;
				this.params = params;
				this.throwsExceptions = throwsExceptions;
			}
            
            @Override
            public _signature bind( VarContext context )
            {
                this.className = Compose.asString( 
                    BindML.compile( this.className ), 
                    context );
                this.modifiers.bind( context );
                this.params.bind( context ); 
                this.throwsExceptions.bind( context );
                return this;                
            }
            
            public String getClassName()
            {
                return this.className;
            }
            
            @Override
            public _signature replace( String target, String replacement )
            {
                this.className = 
                    this.className.replace(target, replacement ); 
                this.modifiers.replace( target, replacement );
                this.params.replace( target, replacement );
                this.throwsExceptions.replace( target, replacement );
                return this;                
            }
            
            public _parameters getParameters()
            {
                return this.params;
            }
            
            public _throws getThrows()
            {
                return this.throwsExceptions;
            }
            
            public _modifiers getModifiers()
            {
                return modifiers;
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
				return toksList.toArray( new String[ 0 ] );
			}
            
			public static _signature of( String constructorSpec )
			{
				constructorSpec = constructorSpec.trim();
	
				// Get the parameters
				_parameters params = new _parameters( );
				int openParenIndex = constructorSpec.indexOf( "(" );
				int closeParenIndex = constructorSpec.lastIndexOf( ")" );
			
				String sig = null;
			
				if( openParenIndex > -1 )
				{							
					if( closeParenIndex < 0 )
					{
						throw new VarException( 
							"cannot find matching ')' for constructor signature contain \")\" " );
					}
	
					String paramInside = constructorSpec.substring( 
                        openParenIndex + 1 , closeParenIndex );
				
					if( paramInside.trim().length() > 0 )
					{
						params = _parameters.of( paramInside ); 
					}
					sig = constructorSpec.substring( 0, openParenIndex );
				}
				else
				{
					closeParenIndex = constructorSpec.length() -1;
					sig = constructorSpec;
				}
		
				String[] tokens = sig.split( " " );
	
				// get the throws		
				_throws throwsExceptions = _throws.NONE;
	
				String throwsTokens = constructorSpec.substring( closeParenIndex + 1 ).trim();
				if( throwsTokens.length() > 1 )
				{
					if( ! throwsTokens.startsWith( "throws" ) )
					{
						throw new VarException( 
							"Tokens found after (parameters); expected throws ...; "
							+ "got \"" + throwsTokens + "\"");
					}
					throwsTokens = throwsTokens.substring( "throws".length() );
				
					String[] throwsTokensStrings = normalizeTokens( throwsTokens );
		
					if( throwsTokensStrings.length > 0 )
					{
						throwsExceptions = _throws.of( (Object[])throwsTokensStrings );
					}			
				}
	
				String className = tokens[ tokens.length - 1 ];
			
				_modifiers mods = new _modifiers();
				if( tokens.length > 1 )
				{
					String[] modi = new String[ tokens.length - 1 ];
					System.arraycopy( tokens, 0, modi, 0, modi.length );
					mods = _modifiers.of( modi ); 
				}	
				
				//Verify MOds
				if( mods.containsAny( 
					Modifier.ABSTRACT, Modifier.FINAL, Modifier.NATIVE, Modifier.STATIC, 
					Modifier.STRICT, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE ) )
				{
					throw new VarException(
						"Invalid Modifier(s) Constructors may only be private" );
				}
				return new _signature( mods, className, params, throwsExceptions );			
			}

			public static _signature cloneOf( _signature prototype )
			{
				return new _signature(
					_modifiers.cloneOf( prototype.modifiers ),
					 prototype.className,
					_parameters.cloneOf( prototype.params ),
					_throws.cloneOf( prototype.throwsExceptions ));				
			}
			
			public static final Dom CONSTRUCTOR_SIGNATURE = 
				BindML.compile( "{+modifiers+}{+className+}{+params+}{+throwsExceptions+}" );

            @Override
			public String author( Directive... directives ) 
			{
				return Compose.asString( CONSTRUCTOR_SIGNATURE, 
					VarContext.of(
						"modifiers", modifiers,
						"className", className,
						"params", params,
						"throwsExceptions", throwsExceptions ),
					directives );
			}

            @Override
			public String toString()
			{
				return author();
			}		
		}			
	}

	//called when the class/ enum is renamed (renames all of the constructors
    @Override
	public _constructors replace( String target, String replacement ) 
	{
		for( int i = 0; i < constructors.size(); i++ )
		{
			_constructor cons = constructors.get( i );
            constructors.set( i, cons.replace( target, replacement ) );			
		}
        return this;
	}
}
