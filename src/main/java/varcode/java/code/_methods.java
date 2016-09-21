package varcode.java.code;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import varcode.CodeAuthor;
import varcode.Template;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

public class _methods
	extends Template.Base
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
	
    @Override
	public String author( Directive... directives ) 
	{
		List<_method>nonStaticMethods = new ArrayList<_method>();
		List<_method>staticMethods = new ArrayList<_method>();
		List<_method._signature>abstractMethods = new ArrayList<_method._signature>();
		
		String[] methodNames = methodsByName.keySet().toArray( new String[ 0 ] );
		
		for( int i = 0; i < methodNames.length; i++ )
		{
			List<_method> oMethods = methodsByName.get( methodNames[ i ] );
			for( int j = 0; j < oMethods.size(); j++ )
			{
				if( oMethods.get( j ).signature.modifiers.contains( Modifier.ABSTRACT ) )
				{
					abstractMethods.add(oMethods.get( j ).signature );
				}					
				else if( oMethods.get( j ).signature.modifiers.contains( Modifier.STATIC ) )
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
	
    @Override
	public String toString()
	{
		return author();
	}
	
    public boolean isEmpty()
    {
        return count() == 0;
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
		return addMethod( signature, (Object[])null );
	}
	
	public _methods addMethod( String signature, Object... body )
	{
		_method m = _method.of( null, signature, (Object[]) body );
		verifyAndAddMethod( m );
		return this;
	}
	
    /** returns all of the methods by the name
     * @param name the name of the method
     * @return all methods with this name 
     */
    public List<_method> getByName( String name )
    {
        return this.methodsByName.get( name );
    }
    
	private void verifyAndAddMethod( _method m )
	{
		List<_method> methodsWithTheSameName = 
				methodsByName.get( m.signature.methodName );
		if( methodsWithTheSameName == null )
		{
			List<_method> methods = new ArrayList<_method>();
			methods.add( m );
			methodsByName.put( m.signature.methodName, methods );
		}
		else
		{//verify there is no conflict
			for( int i = 0; i < methodsWithTheSameName.size(); i++ )
			{
				if( m.signature.matchesParamType(methodsWithTheSameName.get( i ).signature ) )
				{
					throw new VarException( 
                        "Could not add method; another method "+ N +
						methodsWithTheSameName.get( i ) + N +	
						"with same parameter signature as "+ N + 
						m );
				}			
			}
			methodsWithTheSameName.add( m );
		}
	}

    @Override
    public _methods replace( String target, String replacement )
    {
        Map<String, List<_method>> replacedMethods = 
            new HashMap<String, List<_method>>();
        
        String[] names = this.methodsByName.keySet().toArray( new String[ 0 ] );        
        for( int i = 0; i < names.length; i++ )
        {
            List<_method> methods = this.methodsByName.get( names[ i ] );
            
            for( int j = 0; j < methods.size(); j++  )
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
        return this;
    }
    
	
	public static class _method		
		extends Template.Base
	{
		public static final Dom METHOD = 
			BindML.compile(
				"{+javadocComment+}" +	
                "{+methodAnnotations+}" +        
				"{+methodSignature*+}" + N +
				"{" + N +
				"{+$indent4Spaces(methodBody)+}" + N +
				"}" );

		public static final Dom ABSTRACT_METHOD = 
			BindML.compile(
                "{+javadocComment+}" +    
				"{+methodSignature*+};" + N );
	
		public static _method of( String methodSignature )
		{
			_method m = new _method( methodSignature );
			return m;
		}
        
        public static _method of( String methodSingature, _code body )
        {
            _method m = new _method( methodSingature );
            return m.body( body );            
        }
		
        public String getName()
        {
            return this.signature.getName();
        }
        
        public _annotate getAnnotations()
        {
            return this.annotations;
        }
        
		public static _method from( _method prototype ) 
		{
			_method m = 
				new _method( _signature.from(prototype.signature ) );
			m.javadoc = _javadoc.from(prototype.javadoc );
			m.methodBody = prototype.getBody();			
            m.annotations = new _annotate( prototype.annotations.getAnnotations() );
			return m;
		}

		public _signature getSignature()
		{
			return signature;
		}
		
		public static _method of( String comment, String signature, Object... body )
		{
			_method m = new _method( signature );
			if( body != null && body.length > 0 )
			{
                m.body( body );
			}			
			if( comment != null && comment.trim().length() > 0 )
			{
				m.javadoc( comment );
			}			
			return m;
		}

		private _javadoc javadoc;
        private _annotate annotations;
		private _signature signature;
		private _code methodBody;
	
	
		public boolean isAbstract()
		{
			return this.signature.modifiers.containsAny( Modifier.ABSTRACT );
		}
		
		public _method( 
			_modifiers modifiers, 
			String returnType, 
			String methodName, 
			_parameters params,
			_throws throwsExceptions )
		{
			this( new _signature( modifiers, returnType, methodName, params, throwsExceptions ) );		            
		}
	
		public _method( _signature sig )
		{
			this.signature = sig;
            this.annotations = new _annotate();
            this.methodBody = new _code();
            this.javadoc = new _javadoc();
		}
		
		public _method( String methodSignature, Object...bodyLines )
		{
            this( _signature.of( methodSignature ) );
            this.methodBody = _code.of( bodyLines );
		}
	
		public _code getBody()
		{
			return this.methodBody;
		}
		
        public _method annotate( Object...annotations )
        {
            this.annotations.add( annotations );
            return this;
        }
        
        private _method body( _code body )
        {
            this.methodBody = body;
            return this;
        }
        
		public _method body( Object... linesOfCode )
		{
			if( this.isAbstract() && methodBody != null && linesOfCode != null && linesOfCode.length > 0)
			{
				throw new VarException(
					"Abstract methods : "+ N + signature + N + "cannot have a method body" );
			}
			this.methodBody = _code.of( linesOfCode );
			return this;
		}
	
		public _method javadoc( String javadocComment )
		{            
			this.javadoc = new _javadoc( javadocComment );
			return this;
		}
	
        @Override
		public String toString()
		{
			return author();
		}

        public VarContext getContext()
        {
            //if( this.isAbstract() )
			//{
		    //		return VarContext.of(
            //        "javadocComment", javadocComment,    
			//		"methodSignature", methodSignature );
			// }
			return VarContext.of("javadocComment", javadoc,
                "methodAnnotations", annotations,    
                "methodSignature", signature,                        
				"methodBody", methodBody );
        }
        
        @Override
        public String bind( VarContext context, Directive...directives )
        {
            VarContext vc = VarContext.of(
                "javadocComment", javadoc.bind( context, directives ),
                "methodAnnotations", annotations.bind( context, directives ),    
                "methodSignature", signature.bind( context, directives ),                        
				"methodBody", methodBody.bind( context, directives )   );
        
            if( this.isAbstract() )
            {
                return Author.code( ABSTRACT_METHOD, vc, directives );    
            }
            else
            {
                return Author.code( METHOD, vc, directives );    
            }
            
        }
        
        @Override
		public String author( Directive... directives ) 
		{            
			if( this.isAbstract() )
			{
				return Author.code( ABSTRACT_METHOD, 
					getContext(),
					directives );
			}
			return Author.code( METHOD, 
				getContext(),
				directives );
		}
		
		public static class _signature
			extends Template.Base
		{
			public static _signature from( _signature prototype )
			{
				return new _signature(
				    _modifiers.from( prototype.modifiers),
					prototype.returnType + "",
					prototype.methodName + "",
					_parameters.from( prototype.params ),
					_throws.from( prototype.throwsExceptions )
					);
			}
			private _modifiers modifiers;
			private String returnType;
			private String methodName;
			private _parameters params;
			private _throws throwsExceptions;
	
			public _signature(
				_modifiers modifiers, 
                String returnType, 
                String methodName, 
                _parameters params,
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
			
			public String getReturnType()
			{
				return returnType;
			}
            
            @Override
            public _signature replace( String target, String replacement )
            {
                this.returnType = this.returnType.replace( target, replacement );
                this.params.replace( target, replacement ); 
                this.modifiers.replace( target, replacement );
                this.methodName = this.methodName.replace( target, replacement );
                throwsExceptions.replace(target, replacement);
                return this;
            }
			
			public String getName()
			{
				return methodName;
			}
			
			public _parameters getParameters()
			{
				return params;
			}
			
			public _throws getThrownExceptions()
			{
				return this.throwsExceptions;
			}
			
			public static _signature of( String methodSpec )
			{
				methodSpec = methodSpec.trim();
		
				// Get the parameters
				int openParenIndex = methodSpec.indexOf( "(" );
			//	if( openParenIndex < 0 )
			//	{
			//		throw new VarException( "method signature must contain \"(\" ");
				//}
				int closeParenIndex = methodSpec.lastIndexOf( ")" );
				//if( closeParenIndex < 0 )
				//{
				//	throw new VarException( "method signature contain \")\" " );
				//}
                _parameters params = new _parameters( );
                if( openParenIndex < 0 && closeParenIndex < 0 )
                {
                    //no parameters
                    openParenIndex = methodSpec.length();
                    closeParenIndex = methodSpec.length();
                }                
                else
                {
                    String paramInside = methodSpec.substring( openParenIndex + 1 , closeParenIndex );
                    
                    if( paramInside.trim().length() > 0 )
                    {
                        params = _parameters.of( paramInside  ); 
                    }
                }
		
				String sig = methodSpec.substring( 0, openParenIndex );
			
				String[] tokens = sig.split( " " );
		
				//if( tokens.length < 2 )
				//{
                    //assume it's void '
                    
                    /*
					throw new VarException( 
						"method signature must have at least (2) tokens <returnType> <methodName>" );
                    */
				//}
				// get the throws		
				_throws throwsExceptions = _throws.NONE;
				
                if( openParenIndex != closeParenIndex)
                {
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
                }
                String methodName = tokens[ tokens.length - 1 ];
                String returnType = "void";
                        
                if( tokens.length >= 2 )
                {
                    returnType = tokens[ tokens.length - 2 ];
                }
                
				//_identifier methodName = _identifier.of( tokens[ tokens.length - 1 ] );
                //String methodName = tokens[ tokens.length - 1 ];
                
				//String 
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
				return new _signature( 
                    mods, returnType, methodName, params, throwsExceptions );			
			}
	
			public boolean matchesParamType( _signature sig )
			{
				if( sig.methodName.equals( this.methodName ) )
				{
					if( sig.params.count() == this.params.count() )
					{
						for( int i = 0; i < sig.params.count(); i++ )
						{
							if( !sig.params.get( i ).getType().equals( 
								this.params.get( i ).getType() ) )
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
	
			public String author( Directive... directives ) 
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
				return author();
			}		
		}


        
		/** searches through the contents to find target and replaces with replacement */
		public _method replace( String target, String replacement ) 
		{
			this.javadoc.replace( target, replacement );
            
            this.annotations.replace( target, replacement );
            this.methodBody.replace( target, replacement );            
            this.signature.replace( target, replacement );
            
            return this;
		}	
	}
}
