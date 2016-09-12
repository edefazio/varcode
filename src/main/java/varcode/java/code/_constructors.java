package varcode.java.code;

import varcode.Template;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

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
    extends Template.Base
{
	private List<_constructor>constructors = new ArrayList<_constructor>();

	public static _constructors from( _constructors prototype ) 
	{
		_constructors c = new _constructors();
		for( int i = 0; i < prototype.count(); i++ )
		{
			c.addConstructor( 
				_constructor.from( prototype.constructors.get( i ) ) );
		}
		return c;
	}
	
	/**
	 * ex:<PRE> 
	 * _constructors cs = _constructors.of(
	 *    "public MyClass(String message)",
	 *    "this.message = message;" ); 
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

	public List<_constructor> getConstructors()
	{
		return this.constructors;
	}
    
    public _constructor getAt( int index )
    {
        if( index < count() )
        {
            return this.constructors.get( index );
        }
        throw new VarException( "invalid index ["+index+"]" );
    }
    
	public static final Dom CONSTRUCTORS = BindML.compile(
		"{{+?constructors:{+constructors+}" + N + "+}}" );

	public String author( Directive... directives ) 
	{
		if( constructors.size() > 0 )
		{
			return Author.code(
				CONSTRUCTORS,
				VarContext.of( 
					"constructors", constructors ), 
				directives );
		}	
		return "";
	}

	public String toString()
	{
		return author();
	}

	public void verifyNoConflicts( _constructor constructor )	
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
		for( int i = 0; i < constructors.size(); i++ )
		{   			
			if( constructors.get( i ).matchesExisting( constructor ) )
			{
				throw new VarException(
					"adding constructor " + N + constructor + N + 
					" failed, constructor conflicts with existing constructor" + N + 
					constructors.get( i ).toString() );
			}
		}
		constructors.add( constructor );
		return this; 
	}
	
	/** Constructor model */
	public static class _constructor
		extends Template.Base
	{
		public static _constructor from( _constructor prototype )
		{
			_constructor c =  new _constructor(
				_signature.from( prototype.constructorSig ) );
			
			c.body( prototype.body );
			return c;			
		}
		
		private _signature constructorSig;
		
		private _code body;
	
		public _constructor( _signature sig )
		{
			this.constructorSig = sig;
		}
		
		public _constructor( String constructorSignature )
		{
			this.constructorSig = _signature.of( constructorSignature );
		}
	
        public _signature getSignature()
        {
            return constructorSig;
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
			
			if ( sig.className.toString().equals( 
                this.constructorSig.className.toString() ) )
			{
				if( sig.params.count() == this.constructorSig.params.count() )
				{				
					for( int i = 0; i < sig.params.count(); i++ )
					{
						
						if(! sig.params.get( i ).getType().equals
							( this.constructorSig.params.get( i ).getType() ) )
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
				"{+constructorSig*+}" + N +
				"{" + N +
				"{+$>(body)+}" + N +
				"}" );
	
		public String author( Directive... directives ) 
		{
			return Author.code(
				CONSTRUCTOR, 
				VarContext.of(
					"constructorSig", constructorSig,
					"body", body ),
				directives );
		}
	
		public String toString()
		{
			return author();
		}
	
		public static class _signature
			extends Template.Base
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

            public String getClassName()
            {
                return this.className;
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
				//System.out.println( toksList );
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
					//	System.out.println( "PARAMS "+ paramInside );
				
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

			public static _signature from( _signature prototype )
			{
				return new _signature(
					_modifiers.from( prototype.modifiers ),
					 prototype.className,
					_parameters.from( prototype.params ),
					_throws.from( prototype.throwsExceptions ));				
			}
			
			public static final Dom CONSTRUCTOR_SIGNATURE = 
				BindML.compile( "{+modifiers+}{+className+}{+params+}{+throwsExceptions+}" );

			public String author( Directive... directives ) 
			{
				return Author.code( CONSTRUCTOR_SIGNATURE, 
					VarContext.of(
						"modifiers", modifiers,
						"className", className,
						"params", params,
						"throwsExceptions", throwsExceptions ),
					directives );
			}

			public String toString()
			{
				return author();
			}		
		}			
	}

	//called when the class/ enum is renamed (renames all of the constructors
	public void replace( String target, String replacement ) 
	{
		for( int i = 0; i < constructors.size(); i++ )
		{
			_constructor cons = constructors.get( i );
			cons.body.replace( target, replacement );
			
			cons.constructorSig.className = 
				cons.constructorSig.className.replace(target, replacement ); 
			//_identifier.of( enumName );
		}
	}
}
