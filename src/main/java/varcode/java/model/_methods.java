package varcode.java.model;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _methods
	implements SelfAuthored
{
	private Map<String, List<_method>>methodsByName = 
		new HashMap<String, List<_method>>();
	
	public static _methods from( _methods prototype )
	{
		_methods m = new _methods();
		String[] methodNames = 
			prototype.methodsByName.keySet().toArray( new String[ 0 ] );
		for( int i=0; i< methodNames.length; i++)
		{
			List<_method> methodsWithName = 
				prototype.methodsByName.get( methodNames[ i ] );
			
			for( int j = 0; j < methodsWithName.size(); j++ )
			{
				m.addMethod( _method.from( methodsWithName.get( j ) ) );				
			}			
		}
		return m;
	}
	
	public _methods()
	{				
	}

	public static final Dom METHODS = BindML.compile( 
		"{{+?staticMethods:" + N + "{+staticMethods+}+}}" + 
		"{{+?nonStaticMethods:" + N + "{+nonStaticMethods+}+}}" +
		"{{+?abstractMethods:" + N + "{+abstractMethods+};+}}" );
	
	public String toCode( Directive... directives ) 
	{
		List<_method>nonStaticMethods = new ArrayList<_method>();
		List<_method>staticMethods = new ArrayList<_method>();
		List<_method.signature>abstractMethods = new ArrayList<_method.signature>();
		
		String[] methodNames = methodsByName.keySet().toArray( new String[ 0 ] );
		
		for( int i = 0; i < methodNames.length; i++ )
		{
			List<_method> oMethods = methodsByName.get( methodNames[ i ] );
			for( int j = 0; j < oMethods.size(); j++ )
			{
				if( oMethods.get( j ).methodSignature.modifiers.contains( Modifier.ABSTRACT ) )
				{
					abstractMethods.add( oMethods.get( j ).methodSignature );
				}					
				else if( oMethods.get( j ).methodSignature.modifiers.contains( Modifier.STATIC ) )
				{
					staticMethods.add( oMethods.get( j ) ); 
				}
				else
				{
					nonStaticMethods.add( oMethods.get( j ) );
				}
			}			
		}
		return Author.code( 
			METHODS, 
			VarContext.of( 
				"staticMethods", staticMethods,
				"nonStaticMethods", nonStaticMethods,
				"abstractMethods", abstractMethods ), 
			directives );		
	}
	
	public String toString()
	{
		return toCode();
	}
	
	public int count()
	{
		return methodsByName.size();
	}
	
	public _methods addMethod( _method method )
	{
		verifyAndAddMethod( method );
		return this;
	}
	
	public _methods addMethod( String signature )
	{
		return addMethod( signature, (String[])null );
	}
	
	public _methods addMethod( String signature, String... body )
	{
		//return addMethod( null, signature, body );
		_method m = _method.of( null, signature, body );
		verifyAndAddMethod( m );
		return this;
	}
	
	private void verifyAndAddMethod( _method m )
	{
		List<_method> methodsWithTheSameName = 
				methodsByName.get( m.methodSignature.methodName.toString() );
		if( methodsWithTheSameName == null )
		{
			List<_method> methods = new ArrayList<_method>();
			methods.add( m );
			methodsByName.put( m.methodSignature.methodName.toString(), methods );
		}
		else
		{//verify there is no conflict
			for( int i = 0; i < methodsWithTheSameName.size(); i++ )
			{
				if( m.methodSignature.matchesParamType( methodsWithTheSameName.get( i ).methodSignature ) )
				{
					throw new VarException( "Could not add method; Found another method "+ N +
						methodsWithTheSameName.get( i ) + N +	
						"with same parameter signature as "+ N + 
						m );
				}			
			}
			methodsWithTheSameName.add( m );
		}
	}

    public void replace( String target, String replacement )
    {
        Map<String, List<_method>> replacedMethods = new HashMap<String, List<_method>>();
        
        String[] names = this.methodsByName.keySet().toArray( new String[ 0 ] );        
        for( int i = 0; i < names.length; i++ )
        {
            List<_method> methods = this.methodsByName.get(names[ i ] );
            
            for( int j=0; j<methods.size(); j++  )
            {
                _method thisOne = methods.get( j );
                thisOne.replace( target, replacement );
                List<_method> ex = 
                    replacedMethods.get( thisOne.getName() );
                if( ex == null )
                {
                    ex = new ArrayList<_method>();        
                    replacedMethods.put( thisOne.getName(), ex );
                }
                ex.add( thisOne );
                //replacedMethods.add( thisOne.methodSignature.methodName )
            }
        }
        this.methodsByName = replacedMethods;
    }
    
	
	public static class _method		
		implements SelfAuthored
	{
		public static final Dom METHOD = 
			BindML.compile(
				"{+javadocComment+}" +	
				"{+methodSignature*+}" + N +
				"{" + N +
				"{+$indent4Spaces(methodBody)+}" + N +
				"}" );

		public static final Dom ABSTRACT_METHOD = 
			BindML.compile(
				"{+methodSignature*+};" + N );
	
		public static _method of( String methodSignature )
		{
			_method m = new _method( methodSignature );
			return m;
		}
		
        public String getName()
        {
            return this.methodSignature.getName();
        }
        
		public static _method from(_method prototype ) 
		{
			_method m = 
				new _method( signature.from( prototype.methodSignature ) );
			m.javadocComment = _javadoc.from(prototype.javadocComment );
			m.methodBody = prototype.getBody();			
			return m;
		}

		public signature getSignature()
		{
			return methodSignature;
		}
		
		public static _method of( String comment, String signature, String... body )
		{
			_method m = new _method( signature );
			if( body != null && body.length > 0 )
			{
				if( body.length == 1 )
				{
					if( body[ 0 ].endsWith( ";" ) )
					{
						m.body( body[ 0 ] );
					}
					else
					{
						m.body( body[ 0 ] + ";" );
					}
				}
				else
				{
					StringBuilder sb = new StringBuilder();
					for( int i = 0; i < body.length; i++ )
					{
						if( i > 0 )
						{
							sb.append( "\r\n" );
						}
						sb.append( body[ i ] );
					}
					m.body( sb.toString() );
				}				
			}			
			if( comment != null && comment.trim().length() > 0 )
			{
				m.comment( comment );
			}			
			return m;
		}
	
		private _javadoc javadocComment;
		private signature methodSignature;
		private _code methodBody;
	
	
		public boolean isAbstract()
		{
			return this.methodSignature.modifiers.containsAny( Modifier.ABSTRACT );
		}
		
		public _method( 
			_modifiers modifiers, 
			_type returnType, 
			_identifier methodName, 
			_parameters params,
			_throws throwsExceptions )
		{
			this.methodSignature =
				new signature( modifiers, returnType, methodName, params, throwsExceptions );		
		}
	
		public _method( signature sig )
		{
			this.methodSignature = sig;
		}
		
		public _method( String methodSignature )
		{
			this.methodSignature = signature.of( methodSignature );
		}
	
		public _code getBody()
		{
			return this.methodBody;
		}
		
		public _method body( Object... linesOfCode )
		{
			if( this.isAbstract() && methodBody != null && linesOfCode != null && linesOfCode.length > 0)
			{
				throw new VarException(
					"Abstract methods : "+ N + methodSignature + N + "cannot have a method body" );
			}
			this.methodBody = _code.of( linesOfCode );
			return this;
		}
	
		public _method comment( String javadocComment )
		{            
			this.javadocComment = new _javadoc( javadocComment );
			return this;
		}
	
		public String toString()
		{
			return toCode();
		}
	
		/*
		public boolean conflictsWith( _method method )
		{
			_method.signature sig =  method.methodSignature;
			if ( sig.methodName.equals( this.methodSignature.methodName ) )
			{
				if( sig.params.params.size() == this.methodSignature.params.params.size() )
				{				
					for( int i = 0; i < sig.params.params.size(); i++ )
					{
						if(! sig.params.params.get( i ).type.typeName.equals
							( this.methodSignature.params.params.get( i ).type.typeName ) )
						{
							return false;
						}
						//	I could also try to check generic and extensions, but lets just do this
					}
				}
			}
			return false;
		}
		*/
	
		public String toCode( Directive... directives ) 
		{
			if( this.isAbstract() )
			{
				return Author.code( ABSTRACT_METHOD, 
					VarContext.of(
						"methodSignature", methodSignature ),
					directives );
			}
			return Author.code( METHOD, 
				VarContext.of(
					"methodSignature", methodSignature,
					"methodBody", methodBody, 
					"javadocComment", javadocComment ),
				directives );
		}
		
		public static class signature
			implements SelfAuthored
		{
			public static signature from( signature prototype )
			{
				return new signature(
						_modifiers.from( prototype.modifiers),
						_type.from( prototype.returnType),
						_identifier.from( prototype.methodName ),
						_parameters.from( prototype.params ),
						_throws.from( prototype.throwsExceptions )
					);
			}
			private _modifiers modifiers;
			private _type returnType;
			private _identifier methodName;
			private _parameters params;
			private _throws throwsExceptions;
	
			public signature(
				_modifiers modifiers, _type returnType, _identifier methodName, _parameters params,
				_throws throwsExceptions )
			{
				this.modifiers = modifiers;
				this.returnType = returnType;
				this.methodName = methodName;
				this.params = params;
				this.throwsExceptions = throwsExceptions;
			}
	
			public _modifiers getModifiers()
			{
				return this.modifiers;
			}
			
			public _type getReturnType()
			{
				return returnType;
			}
            
            public void replace( String target, String replacement )
            {
                this.returnType = _type.of( this.returnType.getName().replace( target, replacement) );
                this.params.replace( target, replacement ); 
                this.methodName = _identifier.of( this.methodName.toString().replace( target, replacement ) );
                throwsExceptions.replace(target, replacement);
            }
			
			public String getName()
			{
				return methodName.toString();
			}
			
			public _parameters getParameters()
			{
				return params;
			}
			
			public _throws getThrownExceptions()
			{
				return this.throwsExceptions;
			}
			
			public static signature of( String methodSpec )
			{
				methodSpec = methodSpec.trim();
		
				// Get the parameters
				int openParenIndex = methodSpec.indexOf( "(" );
				if( openParenIndex < 0 )
				{
					throw new VarException( "method signature must contain \"(\" ");
				}
				int closeParenIndex = methodSpec.lastIndexOf( ")" );
				if( closeParenIndex < 0 )
				{
					throw new VarException( "method signature contain \")\" " );
				}
		
				String paramInside = methodSpec.substring( openParenIndex + 1 , closeParenIndex );
				_parameters params = new _parameters( );
				if( paramInside.trim().length() > 0 )
				{
					params = _parameters.of( paramInside  ); 
				}
		
				String sig = methodSpec.substring( 0, openParenIndex );
			
				String[] tokens = sig.split( " " );
		
				if( tokens.length < 2 )
				{
					throw new VarException( 
						"method signature must have at least (2) tokens <returnType> <methodName>" );
				}
				// get the throws		
				_throws throwsExceptions = _throws.NONE;
				
				String throwsTokens = methodSpec.substring( closeParenIndex + 1 ).trim();
				if( throwsTokens.length() > 1 )
				{
					if( ! throwsTokens.startsWith( "throws" ) )
					{
						throw new VarException( 
							"Tokens found after (parameters); expected throws ..., got \"" + throwsTokens + "\"");
					}
					throwsTokens = throwsTokens.substring( "throws".length() );
					
					String[] throwsTokensStrings = _var.normalizeTokens( throwsTokens );
			
					if( throwsTokensStrings.length > 0 )
					{
						throwsExceptions = _throws.of( (Object[])throwsTokensStrings );
					}			
				}
		
				_identifier methodName = _identifier.of( tokens[ tokens.length - 1 ] );
				_type returnType = _type.of( tokens[ tokens.length - 2 ] );
				_modifiers mods = new _modifiers();
				if( tokens.length > 2 )
				{
					String[] modi = new String[ tokens.length - 2 ];
					System.arraycopy( tokens, 0, modi, 0, modi.length );
					mods = _modifiers.of( modi ); 
				}
			
				if( mods.containsAny( Modifier.TRANSIENT, Modifier.VOLATILE ) )
				{
					throw new VarException(
						"Invalid Modifiers for method; (cannot be transient or volatile)" );
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.FINAL ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and final )" );
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.NATIVE ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and native )" );				
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.PRIVATE ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and private )" );				
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.STATIC ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and static )" );				
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.STRICT ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and strictfp )" );				
				}
				if( mods.containsAll( Modifier.ABSTRACT, Modifier.SYNCHRONIZED ) )
				{
					throw new VarException( 
						"Invalid Modifiers for method; (cannot be BOTH abstract and synchronized )" );				
				}
				return new signature( mods, returnType, methodName, params, throwsExceptions );			
			}
	
			public boolean matchesParamType( signature sig )
			{
				if( sig.methodName.toString().equals( this.methodName.toString() ) )
				{
					if( sig.params.count() == this.params.count() )
					{
						for( int i = 0; i < sig.params.count(); i++ )
						{
							if( !sig.params.get( i ).type.getName().equals( 
								this.params.get( i ).type.getName() ) )
							{
								return false;
							}
						}
						return true;
					}
					return false;
				}
				return false;
			}
		
			public static final Dom METHOD_SIGNATURE = 
				BindML.compile("{+modifiers+}{+returnType+} {+methodName+}{+params+}{+throwsExceptions+}");
	
			public String toCode( Directive... directives ) 
			{
				return Author.code( METHOD_SIGNATURE, 
					VarContext.of(
						"modifiers", modifiers,
						"returnType", returnType,
						"methodName", methodName,
						"params", params,
						"throwsExceptions", throwsExceptions ),
					directives );
			}
	
			public String toString()
			{
				return toCode();
			}		
		}

		/** searches through the contents to find target and replaces with replacement */
		public void replace( String target, String replacement ) 
		{
			if( this.javadocComment != null )
            {
                this.javadocComment.replace( target, replacement );
            }
            if( this.methodBody != null )
            {
                this.methodBody.replace( target, replacement );
            }
            this.methodSignature.replace( target, replacement );
		}	
	}
}
