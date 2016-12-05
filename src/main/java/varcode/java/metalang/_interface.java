package varcode.java.metalang;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.java.JavaCase;
import varcode.java.JavaCase.JavaCaseAuthor;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.metalang._fields._field;
import varcode.java.metalang._methods._method;
import varcode.markup.bindml.BindML;

//allow default methods
public class _interface 
    implements JavaCaseAuthor, _javaComponent
{    
    public static final Dom INTERFACE = 
	BindML.compile( 
	    "{+pckage+}" +
	    "{{+?imports:{+imports+}" + N +"+}}" +
	    "{+javaDoc+}" +
        "{+annotations+}" +        
	    "{+signature*+}" + N +
	    "{" + N +			
	    "{{+?members:{+$>(members)+}" + N +
	    "+}}" +
	    "{{+?methods:{+$>(methods)+}" + N + 
	    "+}}" +
	    "{{+?nests:{+$>(nests)+}" + N +
	    "+}}" +
	    "}" );
	
    @Override
    public VarContext getContext() 
    {
        String[] n = null;
        if( nesteds.count() > 0 )
        {
            //I need to go to each of the nested classes/ interfaces/ etc.
            // and read what thier imports are, then add these imports to my imports
            String[] nested = new String[ nesteds.count() ];
            for( int i = 0; i < nesteds.count(); i++ )
            {
                _javaComponent comp = nesteds.components.get( i );
                VarContext vc = comp.getContext();
                vc.getScopeBindings().remove( "pckage" );
                vc.getScopeBindings().remove( "imports" );
                nested[ i ] = Compose.asString( comp.getDom(), vc );				
            }
            n = nested;			
        }
		
        _imports imp = null;
        if( this.getImports().count() > 0 )
        {
            imp = this.getImports();
        }
        _fields mem = null;
        if( this.fields.count() > 0 )
        {
            mem = this.fields;
        }
        _methods meth = null;
        if( this.methods.count() > 0 )
        {
            meth = this.methods;
        }
		
        return VarContext.of( 
            "pckage", interfacePackage,
            "imports", imp,
            "javaDoc", javadoc,
            "annotations", this.annotations,
            "signature", interfaceSignature,
            "members", mem,
            "methods", meth, 
            "nests", n );		
    }
	
	/**
	 * i.e.<PRE>
	 * _interface inter = 
	 *    _interface.of("public interface MyInterface extends Serializable");
	 * </PRE>    
	 * @param interfaceSignature the signature of the interface
	 * @return the interface
	 */
	public static _interface of( String interfaceSignature )
	{
	    return of( "", interfaceSignature );
	}
	
	public static _interface cloneOf( _interface prototype )
	{
	    return new _interface( prototype );
	}
	
    /** Creates and returns a clone of this component 
     * @return a deep clone of this component
     */
    public _interface clone()
    {
        return cloneOf( this );
    }
    
    public _interface add( _facet... facets )
    {
        for( int i = 0; i < facets.length; i++ )
        {
            add( facets[ i ] );
        }
        return this;
    }
    
    public _interface add( _facet facet )
    {
        if( facet instanceof _annotations._annotation )
        {
            this.annotations.add( facet );
            return this;
        }
        if( facet instanceof _field )
        {
            this.fields.addFields( (_field)facet );
            return this;
        }
        if( facet instanceof _method )
        {
            this.methods.addMethod( (_method)facet );
            return this;
        }
        if( facet instanceof _modifiers._modifier )
        {
            this.getModifiers().set( (_modifiers._modifier)facet );            
            return this;
        }        
        throw new ModelException(
            "Unsupported facet " + facet + " for _class" );
    }
    
    public _modifiers getModifiers()
    {
        return this.interfaceSignature.modifiers;
    }
    
	/**
	 * i.e.<PRE>
	 * _interface inter = 
	 *    _interface.of(
	 *        "io.varcode.mypackage", 
	 *        "public interface MyInterface extends Serializable");
	 * </PRE>  
	 * @param packageName
	 * @param interfaceSignature
	 * @return
	 */
	public static _interface of( String packageName, String interfaceSignature )
	{
	    return new _interface( packageName, interfaceSignature );
	}

    private _package interfacePackage;
    private _javadoc javadoc;
    private _annotations annotations;
    private _signature interfaceSignature;
    private _fields fields;
    private _methods methods;
    private _imports imports;
    private _nests nesteds;
	
    @Override
    public _interface bind( VarContext context )
    {
        this.interfacePackage.bind(context);
        this.annotations.bind( context );
        this.fields.bind(context);
        this.imports.bind(context);
        this.interfaceSignature.bind( context );
        this.javadoc.bind(context);
        this.methods.bind( context );
        this.nesteds.bind( context );
        return this;
    }
    
    @Override
    public _interface replace( String target, String replacement )
    {
        this.interfacePackage.replace( target, replacement );
        this.annotations.replace( target, replacement );
        this.javadoc.replace( target , replacement );
        this.interfaceSignature.replace( target, replacement );
        this.fields.replace( target, replacement );
        this.methods.replace( target, replacement );
        this.imports.replace( target, replacement );
        this.nesteds.replace( target, replacement );
        
        return this;
    }
    
	/**
	 * Create and return a mutable clone given the prototype
	 * @param prototype the prototype
	 */
    public _interface( _interface prototype )
    {
        this.interfacePackage = _package.cloneOf( prototype.interfacePackage );
        this.annotations = _annotations.cloneOf( prototype.annotations );
        this.javadoc = _javadoc.cloneOf( prototype.javadoc );
        this.interfaceSignature = _signature.cloneOf( prototype.interfaceSignature  );
        this.fields = _fields.cloneOf( prototype.fields );
        this.methods = _methods.cloneOf( prototype.methods );
        this.imports = _imports.cloneOf( prototype.imports );
		
		//NESTEDS
        this.nesteds = _nests.cloneOf(prototype.nesteds );
    }
	
    public _interface( String packageName, String interfaceSignature )
    {
        this.interfacePackage = _package.of( packageName );
        this.annotations = new _annotations();
        this.interfaceSignature = _signature.of( interfaceSignature );
        this.javadoc = new _javadoc();
        this.methods = new _methods();
        this.fields = new _fields();
        this.imports = new _imports();
        this.nesteds = new _nests();
    }

    public _interface packageName( String packageName )
    {
        this.interfacePackage = _package.of( packageName );
        return this;
    }
	
    public String getPackageName()
    {
        return this.interfacePackage.getName();
    }
	
    @Override
    public String getName()
    {
        return this.interfaceSignature.getName();
    }
    
    public _annotations getAnnotations()
    {
        return this.annotations;        
    }
    
    public _signature getSignature()
    {
        return this.interfaceSignature;
    }
	
    public String getJavadoc()
    {
        return this.javadoc.getComment();
    }
	
    @Override
    public _fields getFields()
    {
        return this.fields;
    }
	
    @Override
    public _methods getMethods()
    {
        return this.methods;
    }
	
    @Override
    public _nests getNesteds()
    {
        return this.nesteds;
    }
    
    /** 
     * Returns a String[] representing all class Names (for the top level
     * and all nested classes, enums, interfaces)
     * 
     * for example: 
     * <PRE>
     * package ex.varcode;
     * 
     * public class A
     * {
     *     public static class N
     *     {
     *           public class NN
     *           {
     *              
     *           }   
     *     }
     * }
     * </PRE>
     * 
     * <PRE>
     * _class c = _JavaLoader.from( A.class );
     * 
     * system.out.println( c.getAllNestedClassNames() );
     * </PRE>
     * //prints
     * <PRE>
     * ["ex.varcode.A$N", "ex.varcode.A$N$NN"]
     * </PRE>
     * @return all Names for the 
     */
    @Override
    public List<String> getAllNestedClassNames( 
        List<String>nestedClassNames, String containerClassName )
    {         
        for( int i = 0; i < this.nesteds.count(); i++ )
        {
            _javaComponent nest = this.nesteds.getAt( i );
            String nestedClassName = nest.getName();
            String thisNestClassName = containerClassName + "$" + nestedClassName;
            nestedClassNames.add(  thisNestClassName );
            for( int j = 0; j< nest.getNestedCount(); j++ )
            {
                nestedClassNames = 
                     nest.getAllNestedClassNames( 
                        nestedClassNames, thisNestClassName );
            }    
        }
        return nestedClassNames;
    }
    
    @Override
    public int getNestedCount()
    {
        return this.nesteds.count();
    }
        
    public _javaComponent getNestedByName( String name )
    {
        return this.nesteds.getByName( name ); 
    }
    
    public _javaComponent getNestedAt( int index )
    {
        return this.nesteds.getAt(  index );
    }
    
    public _interface importsStatic( Object...staticImports )
    {
        this.imports.addStaticImports( staticImports );
        return this;
    }
    
    public _interface imports( Object... imports )
    {
        this.imports.addImports( imports );
        return this;
    }
	
    public _interface javadoc( String...javadoc )
    {
        this.javadoc = _javadoc.of( javadoc );
        return this;
    }
    
    @Override
    public _imports getImports()
    {
        for( int i = 0; i < nesteds.count(); i++ )
        {
            this.imports.merge(nesteds.components.get( i ).getImports() );
        }
        return this.imports;
    }
	
    public _interface method( String javadoc, String signature )
    {
        _method method = _method.of( signature );
		
        if( javadoc != null  )
        {
            method.javadoc( javadoc );
        }
        _methods._method._signature sig = method.getSignature();
	//Default Method
        if( sig.getModifiers().contains( Modifier.PRIVATE ) )
        {
            throw new ModelException( 
                "Cannot add a private method " + N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {
            throw new ModelException( 
                "Cannot add a final method " + N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().contains( Modifier.PROTECTED ) )
        {
            throw new ModelException( 
                "Cannot add a protected method " + N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().containsAny(
            Modifier.NATIVE, Modifier.STRICT, Modifier.SYNCHRONIZED, 
            Modifier.TRANSIENT, Modifier.VOLATILE ) )
        {
            throw new ModelException( "Invalid Modifiers for interface method "+ N + sig );
        }
	method.getSignature().getModifiers().set( "abstract" );
	this.methods.addMethod( method );
	return this;		
    }
    
    public _interface method( _method method )
    {
        this.methods.addMethod( method );
        return this;
    }
    
    public _interface method( String signature )
    {
        return method( null, signature );
    }
    
    /**
     * add one or more extensions for this interface to the signature and return 
     * the modified _interface
     * @param extendFrom one or more extends to add to the interface
     * @return the modified _interface
     */
    public _interface extend( String...extendFrom )
    {
        for( int i = 0; i < extendFrom.length; i++ )
        {
            this.interfaceSignature.extendsFrom.addExtends( extendFrom[ i ] );
        }
        return this;
    }
    
    /**
     * Add one or more annotations to the _interface and return the modified 
     * _interface
     * @param annotations String annotations to parse and add
     * @return the modified _interface
     */
    public _interface annotate( String... annotations )
    {
        this.annotations.add(  (Object[])annotations );
        return this;
    }
	
    public _interface staticMethod( String signature, Object...linesOfCode )
    {
        _method method = _method.of( (_javadoc)null, signature, linesOfCode);
	_methods._method._signature sig = method.getSignature();
        if( !sig.getModifiers().contains( Modifier.STATIC ) )
        {
            sig.getModifiers().set( "static" );
	}
	if( sig.getModifiers().contains( Modifier.PRIVATE ) )
	{
            throw new ModelException( 
                "Cannot add a private method " +N + sig + N +" to an interface ");
	}
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {
            throw new ModelException( 
                "Cannot add a final method " +N + sig + N +" to an interface ");
	}
	if( sig.getModifiers().contains( Modifier.PROTECTED ) )
	{
	    throw new ModelException( 
            "Cannot add a protected method " +N + sig + N +" to an interface ");
	}
	if( sig.getModifiers().containsAny( 
	    Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE))
	{
            throw new ModelException( 
                "Invalid Modifiers for interface method "+ N + sig );
	}
	this.methods.addMethod( method );
	return this;
    }
    
    public _interface defaultMethod( String signature, Object...linesOfCode )
    {
        _method method = _method.of( (_javadoc)null, signature, linesOfCode);
		
        _methods._method._signature sig = method.getSignature();
        if( !sig.getModifiers().contains( 
            _modifiers._modifier.INTERFACE_DEFAULT.getBitValue() ) )
        {			
            sig.getModifiers().set( "default" );
        }
        if( sig.getModifiers().contains( Modifier.PRIVATE ) )
        {
            throw new ModelException( 
                "Cannot add a private method " +N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().contains( Modifier.FINAL ) )
        {   
            throw new ModelException( 
                "Cannot add a final method " +N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().contains( Modifier.PROTECTED ) )
        {
            throw new ModelException( 
                "Cannot add a protected method " +N + sig + N +" to an interface ");
        }
        if( sig.getModifiers().containsAny( 
            Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.TRANSIENT, Modifier.VOLATILE))
        {
            throw new ModelException( 
                "Invalid Modifiers for interface method "+ N + sig );
        }
        this.methods.addMethod( method );
        return this;
    }
	
    public _interface field( String comment, String fieldSignature )
    {
        _fields._field m = _fields._field.of( fieldSignature );
        
        if( !m.hasInit() )
        {
            throw new ModelException("Field : " + N + m +  N 
                + " has not been initialized for interface ");
        }
        m.javadoc( comment );
        fields.addFields( m );		
        return this;    
    }
    
    public _interface field( _field field )
    {
        this.fields.addFields( field );
        return this;
    }
    
    public _interface field( String fieldSignature )
    {
        _fields._field m = _fields._field.of( fieldSignature );
        if( m.getInit() == null || m.getInit().getCode().trim().length() == 0 )
        {
            throw new ModelException( "Field : " + N + m +  N 
                + " has not been initialized for interface ");
        }
        fields.addFields( m );		
        return this;
    }

    public _interface nest( _javaComponent component )
    {
        this.nesteds.add( component );
        return this;
    }
	
    /** interface signature */
    public static class _signature
        implements JavaMetaLang
    {                
        private _modifiers modifiers = new _modifiers();
        private String interfaceName;		
        private _extends extendsFrom = new _extends();
		
        public static final Dom INTERFACE_SIGNATURE = 
            BindML.compile(
                "{+modifiers+}interface {+interfaceName*+}{+extendsFrom+}" );

        @Override
        public String author( )
        {
            return author( new Directive[ 0 ] );
        }
        
        @Override
		public String author( Directive... directives ) 
		{
			return Compose.asString( INTERFACE_SIGNATURE, 
				VarContext.of(
					"modifiers", modifiers,
					"interfaceName", interfaceName,				
					"extendsFrom", extendsFrom ), 
				directives );
		} 
		
		public static _signature cloneOf( _signature prototype ) 
		{
			_signature clone = new _signature( );
			clone.modifiers = _modifiers.cloneOf( prototype.modifiers );
			clone.interfaceName = prototype.interfaceName + "";
			clone.extendsFrom = _extends.cloneOf( prototype.extendsFrom );
			return clone;
		}
		
        @Override
        public _signature bind( VarContext context )
        {
            this.interfaceName = Compose.asString(BindML.compile(this.interfaceName), context );
            this.extendsFrom.bind( context );
            this.modifiers.bind( context );
            return this;
        }
        
        @Override
        public _signature replace( String target, String replacement )
        {
            this.interfaceName = this.interfaceName.replace( target, replacement );
            this.extendsFrom.replace( target, replacement );            
            this.modifiers.replace( target, replacement );
            return this;
        }

		public String getName()
		{
			return this.interfaceName;
		}
		
        @Override
		public String toString()
		{
			return author();
		}
		
		public _extends getExtends()
		{
			return extendsFrom;
		}

        public _signature setModifiers( _modifiers modifiers )
        {
            this.modifiers = modifiers;
            return this;
        }
        
        public _signature setModifiers( int...modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }
        
        public _signature setExtends( String...extendsFrom )
        {
            this.extendsFrom = _extends.of( extendsFrom );
            return this;
        }
        
        public _signature setName( String name )
        {
            this.interfaceName = name;
            return this;
        }
        
		public static _signature of( String interfaceSignature )
		{
			_signature sig = new _signature();
		
			//MUST have sequence
			//   ...interface 
			String[] tokens = interfaceSignature.split(" ");
			int interfaceTokenIndex = -1;
			int extendsTokenIndex = -1;
		
			if( tokens.length < 2 )
			{
				throw new ModelException( 
                    "interface signature must have at least (2) tokens interface <name>" );	
			}
		
			for( int i = 0; i < tokens.length; i++ )
			{
				if( tokens[ i ].equals( "interface" ) )
				{
					interfaceTokenIndex = i;
				}
				else if( tokens[ i ].equals( "extends" ) )
				{
					extendsTokenIndex = i;
				}			
			}  
		
			if(( interfaceTokenIndex < 0 ) || ( interfaceTokenIndex > tokens.length -1 ) )
			{   //cant be 
				throw new ModelException(
                    "interface token cant be not found or the last token"); 
			}
			sig.interfaceName = 
                tokens[ interfaceTokenIndex + 1 ];
		
			if( interfaceTokenIndex > 0 )
			{   //modifiers provided
				String[] mods = new String[ interfaceTokenIndex ];
				System.arraycopy( tokens, 0, mods, 0, interfaceTokenIndex );
				sig.modifiers = varcode.java.metalang._modifiers.of( mods );
			}
			
			if( extendsTokenIndex > interfaceTokenIndex + 1 )
			{
				if( extendsTokenIndex == tokens.length -1 )
				{
					throw new ModelException( 
                        "extends token cannot be the last token" );
				}
				
				int tokensLeft = tokens.length - ( extendsTokenIndex + 1 );
				String[] extendsTokens = new String[ tokensLeft ];
				
				System.arraycopy( 
                    tokens, extendsTokenIndex + 1, extendsTokens, 0, tokensLeft );
				List<String>normalExtendsTokens = new ArrayList<String>();
				for( int i = 0; i < extendsTokens.length; i++)
				{
					if( extendsTokens[ i ].contains( "," ) )
					{
						String[] splitTokens = extendsTokens[ i ].split( "," );
						for( int j = 0; j < splitTokens.length; j++ )
						{
							String tok = splitTokens[ j ].trim();
							if( tok.length() > 0 )
							{
								normalExtendsTokens.add( tok );
							}
						}
					}
					else
					{
						String[] splitTokens = extendsTokens[ i ].split( " " );
						for( int j = 0; j < splitTokens.length; j++ )
						{
							String tok = splitTokens[ j ].trim();
							if( tok.length() > 0 )
							{
								normalExtendsTokens.add( tok );
							}
						}
					}
				}
				sig.extendsFrom = varcode.java.metalang._extends.of( 
					normalExtendsTokens.toArray( new String[ 0 ] ) ); 
			}
			return sig;		
		}	
	}

    @Override
	public JavaCase toJavaCase( Directive... directives ) 
	{
		return JavaCase.of( getFullyQualifiedClassName(), INTERFACE, getContext(), directives );
	}

    /** 
     * authors, compiles and loads this interface in an AdHocClassLoader 
     * and returns the Class.
     * 
     * @return Class the class representing this interface
     */
    public Class loadClass( )
    {
        return toJavaCase().loadClass();
    }
    
    /** 
     * authors, compiles and loads this interface in an AdHocClassLoader 
     * and returns the Class.
     * 
     * @param adHocClassLoader the class loader to load the class
     * @return Class the class representing this interface
     */
    public Class loadClass( AdHocClassLoader adHocClassLoader )
    {
        return toJavaCase().loadClass( adHocClassLoader );
    }
    
	public String getFullyQualifiedClassName()
	{
		if( this.interfacePackage != null && ! this.interfacePackage.isEmpty() )
		{
			return this.interfacePackage.getName() + "." + this.interfaceSignature.getName();
		}
		else
		{
			return this.interfaceSignature.getName();
		}
	}
	
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
        
    @Override
	public String author( Directive... directives ) 
	{
        return Compose.asString( INTERFACE, getContext(), directives);
	}
    
    @Override
	public JavaCase toJavaCase( VarContext context, Directive...directives ) 
	{
        String FullClassName = this.getFullyQualifiedClassName();
        Dom classNameDom = BindML.compile( FullClassName );
        
        String theClassName = Compose.asString( classNameDom, context ); 
        
        System.out.println( theClassName );
        //first lets print out the structure and optional Marks
        String authored = JavaCase.of( 
            theClassName,
            INTERFACE, 
            getContext() ).toString();
        
        //now compile the marks and fill them in with the context
		return JavaCase.of(
			theClassName,
			BindML.compile( authored ), 
			context,
			directives );			
	}
    
    @Override
	public String toString()
	{
		return author();
	}
	
    @Override
	public Dom getDom() 
	{
		return INTERFACE;
	}
}
