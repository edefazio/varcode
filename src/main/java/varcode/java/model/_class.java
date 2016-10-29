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
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.java.model._nest._nestGroup;
import varcode.java.model._nest.component;
import varcode.markup.bindml.BindML;
import varcode.Model;
import varcode.java.model._constructors._constructor;

/**
 * "parametric code" using a fluent builder pattern for 
 * generating the source code of a Java Class.<BR><BR>
 * 
 * Allows members, methods, constructors, etc. to be incrementally "plugged in" 
 * and well formatted Java Source code to the generated.
 * 
 * Other classes, interfaces, enums can be nested within a single class
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _class    
	implements JavaCaseAuthor, _nest.component
{	
	private _package classPackage;
	private _imports imports;
	private _javadoc javadoc;
	private _signature signature;
	private _annotate annotations;
    
	private _constructors constructors;
	private _fields fields;
	private _methods methods;
	private _staticBlock staticBlock;
	
	/** Nested inner classes, static inner classes, interfaces, enums */
	private _nestGroup nests;
	
	public static _class cloneOf( _class prototype )
	{
		return new _class( prototype );
	}
	
    /** Create and return a builder for a new class<PRE> 
     * i.e. _class.of( "public class MyClass" );</PRE>
     * 
     * creates and returns a public _class "MyClass" in package "ex.varcode"
     * @param classSignature the signature of the class to add
     * @return _class a class with the signature
     */
	public static _class of( String classSignature )
	{
		return new _class( null, classSignature ); 
	}
	
    /** Create and return a builder for a new class<PRE> 
     * _class.of( 
     *    "ex.varcode", "public class MyClass extends BaseClass implements Something" );</PRE>
     * 
     * creates and returns a public _class "MyClass" in package "ex.varcode"
     * that extends from BaseClass
     * @param packageName the name of the package the class belongs in (i.e. "ex.app")
     * @param classSignature the signature of the class ("public class MyClass")
     * or
     * ("private static class MyClass extends BaseClass implements Callable<Integer>")
     * @return a new _class
     */
	public static _class of( String packageName, String classSignature )
	{
		return new _class( packageName, classSignature ); 
	}
	
    
    public String getName()
    {
        return this.signature.getName();
    }
    
    /** Create and the model for a new class<PRE> 
     * i.e. _class.of( 
     * "A Sample Ad Hoc Class", 
     * "ex.varcode", 
     * "public class MyClass extends BaseClass" );</PRE>
     * 
     * creates and returns a public _class "MyClass" in package "ex.varcode"
     * that extends from BaseClass and that has a JavaDocComment
     * 
     * @param javadoc the javadoc comment at the top of the class
     * @param packageName the name of the package the class belongs in (i.e. "ex.app")
     * @param classSignature the signature of the class
     * @return a new _class
     */
	public static _class of( String javadoc, String packageName, String classSignature )
	{
		_class c = new _class( packageName, classSignature );
		c.javadoc( javadoc );
		return c;
	}
	
	/**
	 * Parse the classSignature and return a new class 
	 * ex:<PRE>
	 * _class c = _class("public static class MyClass extends YadaYada, implements Blah");
	 * _class abs = _class("protected abstract class MyClass extends YadaYada, implements Blah");
	 * </PRE>
	 *  
	 * @param classSignature
	 */
	public _class( String classSignature )
	{
		this( null, classSignature );
	}
	
	public _class( String packageName, String classSignature )
	{
        this.annotations = new _annotate();
		this.classPackage = _package.of( packageName );
		this.javadoc = new _javadoc();
		this.signature = _signature.of( classSignature );
		this.imports = new _imports();	
		this.fields = new _fields();
		this.methods = new _methods();
		this.staticBlock = new _staticBlock();
		this.constructors = new _constructors();
		this.nests = new _nestGroup();
	}
	
	/**
	 * Create and return a mutable clone given the prototype
	 * @param prototype the prototype
	 */
	public _class( _class prototype )
	{
        this.annotations = _annotate.cloneOf( prototype.annotations );
		this.classPackage = _package.cloneOf( prototype.classPackage );
		this.imports = _imports.cloneOf( prototype.imports );
		this.signature = _signature.cloneOf(prototype.signature  );
		
		this.javadoc = _javadoc.cloneOf( prototype.javadoc );
		this.methods = _methods.cloneOf( prototype.methods );
		this.fields = _fields.cloneOf( prototype.fields );
		if( prototype.staticBlock != null && ! prototype.staticBlock.isEmpty() )
		{
			this.staticBlock = _staticBlock.of( prototype.staticBlock.getBody() );
		}
		else
		{
			this.staticBlock = new _staticBlock();
		}
		this.constructors = _constructors.cloneOf( prototype.constructors );
		
		//NESTEDS
		this.nests = _nestGroup.cloneOf( prototype.nests );
	}
	
	public static final Dom CLASS = 
		BindML.compile( 
			"{+pckage+}" +
			"{{+?imports:{+imports+}" + N +"+}}" +
			"{+classJavaDoc+}" +
            "{+classAnnotation+}" +        
			"{+classSignature*+}" + N +
			"{" + N +			
			"{{+?members:{+$>(members)+}" + N +
			"+}}" +
			"{{+?constructors:{+$>(constructors)+}" + N +
			"+}}" +
			"{{+?methods:{+$>(methods)+}" + N + 
			"+}}" +
			"{{+?staticBlock:{+$>(staticBlock)+}" + N +
			"+}}" +
			"{{+?nests:{+$>(nests)+}" + N +
			"+}}" +
			"}" );
			
    @Override
	public String author( Directive... directives ) 
	{
        return Compose.asString( CLASS, getContext(), directives );			
	}
	
    public _annotate getAnnotations()
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
			String[] nested = new String[ nests.count() ];
			for( int i = 0; i < nests.count(); i++ )
			{
				component comp = nests.components.get( i );
				VarContext vc = comp.getContext();
				
				//inner classes inherit package, so remove the package
				vc.getScopeBindings().remove( "pckage" );
				
				//inner classes have imports at top level so remove imports
				vc.getScopeBindings().remove( "imports" );
                
                //author the member class/enum/interface w/o package or imports
				nested[ i ] = Compose.asString( comp.getDom(), vc );				
			}
			n = nested;			
		}
		_imports imp = null;
		if( this.getImports().count() > 0 )
		{
			imp = this.getImports();
		}
		_staticBlock sb = null;
		if( !this.staticBlock.isEmpty() )
		{
			sb = this.staticBlock; 
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
		_constructors cs = null;
		if( this.constructors != null && this.constructors.count() > 0 )
		{
			cs = this.constructors;
		}
		return 
			VarContext.of("pckage", classPackage,
				"imports", imp,
				"classJavaDoc", javadoc,
                "classAnnotation", this.annotations,
				"classSignature", signature,
				"members", mem,
				"methods", meth,
				"staticBlock", sb, 
				"constructors", cs,
				"nests", n );
	}
	
    @Override
    public _class bind( VarContext context )
    {
        this.classPackage = this.classPackage.bind( context );
        this.imports = this.imports.bind( context );
        this.javadoc = this.javadoc.bind( context );
        this.annotations = this.annotations.bind( context );
        this.signature = this.signature.bind( context );
        this.fields = this.fields.bind( context );
        this.staticBlock = this.staticBlock.bind( context );
        this.methods = this.methods.bind( context );
        this.constructors = this.constructors.bind( context );
        this.nests = this.nests.bind( context );      
        return this;
    }
        
    @Override
	public Dom getDom()
	{
		return CLASS;
	}
	
	public String getFullyQualifiedClassName()
	{
		
		if( this.classPackage != null && ! this.classPackage.isEmpty() )
		{
			return this.classPackage.getName() + "." + this.signature.getName();
		}
		else
		{
			return this.signature.className;
		}
	}
	
    /**
     * <UL> 
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>compile the source using Javac to create .class
     *  <LI>load the .class in a new {@code AdHocClassLoader}
     *  <LI>return a reference to the loaded class
     * </UL>
     * @return the {@code Class } loaded from a {@code AdHocClassLoader}
     */
    public Class loadClass( )
    {
        return toJavaCase().loadClass();
    }
    
    /**
     * <UL> 
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>compile the source using Javac to create .class
     *  <LI>load the .class in the {@code adHocClassLoader}
     *  <LI>return a reference to the loaded class
     * </UL>
     * @param adHocClassLoader the class Loader used for compiling and loading
     * the class.
     * @return the {@code Class } loaded from in the a {@code AdHocClassLoader}
     */
    public Class loadClass( AdHocClassLoader adHocClassLoader )
    {
        return toJavaCase().loadClass( adHocClassLoader );
    }
    
    /**
     * <UL> 
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>compile the source using Javac to create .class
     *  <LI>load the .class in a new {@code AdHocClassLoader}
     *  <LI>create a new instance of the class using {@code constructorArgs} 
     * </UL>
     * @param constructorArgs arguments passed into the constructor
     * @return  a new instance of the Ad Hoc class
     */
    public Object instance( Object...constructorArgs )
    {
        return toJavaCase().instance( constructorArgs );
    }
    
    /**
     * <UL> 
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>compile the source using Javac to create .class
     *  <LI>load the .class in the {@code adHocClassLoader}
     *  <LI>create a new instance of the class using {@code constructorArgs} 
     * </UL>
     * @param classLoader the class Loader used for compiling and loading
     * the Class.
     * @param constructorArgs arguments for the construction of the class
     * @return the {@code Class } loaded from in the a {@code AdHocClassLoader}
     */
    public Object instance( AdHocClassLoader classLoader, Object...constructorArgs )
    {
        return toJavaCase().instance( classLoader, constructorArgs );
    }
    
   

    /**
     * <UL>
     *  <LI>Binds in the context into the _class and (optional) directives
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>return the JavaCase
     * </UL>
     * @param context contains bound variables and scripts to bind data into
     * the template
     * @param directives pre-and post document directives 
     * @return the populated Template bound with Data from the context
     */
    public final JavaCase bindCase( VarContext context, Directive...directives )
    {
        String FullClassName = this.getFullyQualifiedClassName();
        Dom classNameDom = BindML.compile( FullClassName );
        String theClassName = Compose.asString( classNameDom, context ); 
        
        Dom dom = BindML.compile( author() ); 
        
        //String boundCode = Author.code( dom, context, directives );
        return JavaCase.of(
            theClassName, dom, context, directives);
    }
        
    /**
     * <UL> 
     *  <LI>Bind in the context into the _class and (optional) directives
     *  <LI>Build the (.java) source of the {@code _class} model
     *  <LI>compile the source using Javac to create .class
     *  <LI>load the .class in the {@code adHocClassLoader}
     *  <LI>return a reference to the loaded class
     * </UL>
     * @param context the context for filling in Marks within the Class
     * @param directives directives for 
     * @return 
     */
    public final Class bindClass( VarContext context, Directive...directives )
    {
        JavaCase jc = bindCase( context, directives );
        return jc.loadClass();
    }
    
    public final Object bindInstance( VarContext context, Object...parameters )
    {
        JavaCase jc = bindCase( context );
        return jc.instance( parameters );
    }
    
    @Override
    public JavaCase toJavaCase( Directive... directives) 
	{	
		return JavaCase.of(
			getFullyQualifiedClassName(),
			CLASS, 
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
        String authored = JavaCase.of( 
            theClassName,
            CLASS, 
            getContext() ).toString();
        
        //now compile the marks and fill them in with the context
		return JavaCase.of(
			theClassName,
			BindML.compile( authored ), 
			context,
			directives );			
	}
	
	/**
     * Adds a new constructor
	 * <PRE>{@code 
	 * _class MyAClass = new _class("public A")
	 *     .addConstructor("public A( String name )", "this.name = name;");}</PRE>
	 *     
	 * @param constructorSignature the signature of the constructor
	 * @param body the body of the constructor
	 * @return this
	 */
	public _class constructor( String constructorSignature, Object... body )
	{
		_constructors._constructor construct = 
            new _constructors._constructor( constructorSignature ).body( body );
		return constructor( construct );
	}
    
    public _class constructor( _constructor construct )
    {
        constructors.addConstructor( construct );
		return this;
    }
	/** 
     * Sets the Javadoc comment for the class
     * 
     * @param javadoc the Javadoc comment
     * @return this
     */
	public _class javadoc( String javadoc )
	{
		this.javadoc = new _javadoc( javadoc );
		return this;
	}

    /**
     * Implement one or more interfaces
     * @param classes to implement
     * @return this
     */
    public _class implement( Class...classes )
    {
        if( signature.implementsFrom == null )
        {
            signature.implementsFrom = new _implements();
        }
        this.signature.implementsFrom.implement(classes );
        return this;
    }
    
    public _class annotate( Object... annotations )
    {
        this.annotations.add( annotations );
        return this;
    }
    
    public _class methods ( _methods methods )
    {
        String[] methodNames = methods.getNames();
        
        for( int i = 0; i < methods.count(); i++ )
        {
            List<_method> methodsByName = methods.getByName( methodNames[ i ] );
            for( int j = 0; j < methodsByName.size(); j++ )
            {
                method( methodsByName.get( j ) ); 
            }
        }        
        return this;
    }
    
	public _class method( String methodSignature )
	{
		return method( _method.of( null, methodSignature, new Object[ 0 ] ) );
	}
	  
	public _class method( String methodSignature, Object... bodyLines )
	{
		return method( _method.of( null, methodSignature, bodyLines ) );
	}
    
    public _class method( String comment, String methodSignature, _code body )
    {
        return method( _method.of( comment, methodSignature, body ) );
    }

	public boolean isAbstract()
	{
		return this.signature.modifiers.contains( Modifier.ABSTRACT );
	}
	
	public _class staticBlock( Object... code )
	{
		this.staticBlock.addTailCode( (Object[])code );
		return this;
	}
	
    public _class method( String javadoc, _method method )
    {
        method.javadoc( javadoc );
        method( method );
        return this;
    }
    
	public _class method( _method m )
	{
		if( m.isAbstract() && !this.isAbstract() )
		{
			throw new VarException(
				"Cannot add an abstract method " + N + m + N + " to a non-abstract class " );
		}
		if( m.isAbstract() && !m.getBody().isEmpty() )
		{
			throw new VarException( 
				"abstract method :" + N + m + N + "cannot have a method body:" + N + m.getBody() );
		}
		this.methods.addMethod( m );
		return this;
	}
	
    public _class field( _field field )
    {
        fields.addFields( field );		
        return this;
    }
    
    /** 
     * adds a single field with a javadoc comment i.e.
     * field("comment", "public int a");
     * 
     * is:
     * /**
     *  * comment
     *  * /
     * public int a;
     * 
     * @param comment
     * @param field
     * @return 
     */
    public _class field( String comment, String field )
	{
		fields.addFields( _fields._field.of( field ).javadoc( comment ) );		
		return this;
	}
    
	public _class field( String field )
	{
		fields.addFields( _fields._field.of( field ) );		
		return this;
	}
	
	public _class fields( String...fields )
	{
		for( int i = 0; i < fields.length; i++ )
		{
			this.fields.addFields( _fields._field.of( fields[ i ] ) );
		}
		return this;
	}
	

    
	public _class importsStatic( Object... imports )
	{
		this.imports.addStaticImports( imports );
		return this;
	}
	
	public _class imports( Object... imports )
	{
		this.imports.addImports( imports );
		return this;
	}
	
	public _class nest( _nest.component component )
	{
		this.nests.add( component );
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
	
    public _javadoc getJavadoc()
    {
        return this.javadoc;
    }
    
    public _package getClassPackage()
    {
        return this.classPackage;
    }
    
    @Override
    public _fields getFields()
    {
        return this.fields;
    }

    public _signature getSignature()
    {
        return this.signature;
    }
    
    public _constructors getConstructors()
    {
        return this.constructors;
    }
    
    public _methods getMethods()
    {
        return this.methods;
    }
    
    public List<_method> getMethodsByName( String name )
    {
        return this.methods.getByName( name );
    }
    
    public _nestGroup getNests()
    {
        return this.nests;
    }
    
    public _staticBlock getStaticBlock()
    {
        return this.staticBlock;
    }
    
    @Override
	public String toString( )
	{
		return author();
	}
	
	public _class packageName( String packageName )
	{
		this.classPackage = _package.of( packageName );
		return this;
	}
	
    /**
     * signature of the _class
     */
	public static class _signature
        implements Model
    {        
        
   		private _modifiers modifiers;
		private String className;
		private _extends extendsFrom;
		private _implements implementsFrom;
		
		public static final Dom CLASS_SIGNATURE = 
			BindML.compile( 
                "{+modifiers+}class {+className*+}"+
                "{+extendsFrom+}" +
                "{+implementsFrom+}" );

        @Override
		public String author( Directive... directives ) 
		{
			return Compose.asString( CLASS_SIGNATURE, 
				VarContext.of(
					"className", className,
					"modifiers", modifiers,
					"extendsFrom", extendsFrom,
					"implementsFrom", implementsFrom ), 
				directives );
		} 
		
        @Override
        public _signature bind( VarContext context )
        {
            this.className = Compose.asString( 
                BindML.compile( this.className ), 
                context );
            this.modifiers.bind( context );
            this.extendsFrom.bind( context ); 
            this.implementsFrom.bind( context );
            return this;
        }
        
		private _signature()
		{			
            modifiers = new _modifiers();
            className = "";
            this.extendsFrom = new _extends();
            this.implementsFrom = new _implements();            
		}
		
        @Override
		public String toString()
		{
			return author();
		}

		public String getName()
		{
			return this.className;
		}
        
        public _extends getExtends()
        {
            return this.extendsFrom;
        }
        
        public _implements getImplements()
        {
            return this.implementsFrom;
        }
        
        public _modifiers getModifiers()
        {
            return this.modifiers;
        }
		
        public _signature setModifiers( String...modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }
        
        public _signature setModifiers( int...modifiers )
        {
            this.modifiers = _modifiers.of( modifiers );
            return this;
        }
        
		public static _signature cloneOf( _signature prototype )
		{
			_signature clone = new _signature();
			clone.className = prototype.className + "";
			clone.extendsFrom = _extends.cloneOf( prototype.extendsFrom );
			clone.implementsFrom = _implements.cloneOf( prototype.implementsFrom );
			clone.modifiers = _modifiers.cloneOf( prototype.modifiers );
			
			return clone;
		}
		
        @Override
        public _signature replace( String target, String replacement )
        {
            this.className = className.replace( target, replacement );
            this.extendsFrom.replace( target, replacement );
            this.implementsFrom.replace( target, replacement );
            return this;
        }
        
        
        public _signature implement( Class... interfaceClasses )
        {
            this.implementsFrom.implement( interfaceClasses );            
            return this;
        }
        
        public _signature implement( String... interfaceClasses )
        {
            this.implementsFrom.implement( interfaceClasses );  
            return this;
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
        
        /** Creates a Signature by parsing the classSDignature provided 
         * 
         * @param classSignature the signature 
         * (i.e. "public class MyClass extends A implements B")
         * @return the signature for the clas
         */
		public static _signature of( String classSignature )
		{
			_signature sig = new _signature();
		
			String[] tokens = classSignature.split(" ");
		
			int classTokenIndex = -1;
			int extendsTokenIndex = -1;
			int implementsTokenIndex = -1;
		
            //check if the array contains  the "class" token
            // if NOT
            if( indexOf( tokens, "class" ) < 0 )
            {
                //infer they want a public class
                String[] preamble = new String[ tokens.length + 2 ];  
                preamble[0] = "public";
                preamble[1] = "class";
                System.arraycopy( tokens, 0, preamble, 2, tokens.length );
                tokens = preamble;
            }
			 
			for( int i = 0; i < tokens.length; i++ )
			{
				if( tokens[ i ].equals( "class" ) )
				{
					classTokenIndex = i;
				}
				else if( tokens[ i ].equals( "extends" ) )
				{
					extendsTokenIndex = i;
				}
				else if( tokens[ i ].equals( "implements" ) )
				{
					implementsTokenIndex = i;
				}
			}
		
			if( ( classTokenIndex < 0 ) || ( classTokenIndex > tokens.length -1 ) )
			{   //cant be 
				throw new VarException(
					"class token cant be not found or the last token" ); 
			}
			sig.className = tokens[ classTokenIndex + 1 ];
		
			if( classTokenIndex > 0 )
			{   //modifier provided
				String[] mods = new String[ classTokenIndex ];
				System.arraycopy( tokens, 0, mods, 0, classTokenIndex );
				sig.modifiers = _modifiers.of( mods );
				if( sig.modifiers.containsAny(
					Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.NATIVE,
					Modifier.TRANSIENT, Modifier.VOLATILE, Modifier.STRICT ) )
				{
					throw new VarException( 
                        "classSignature \"" + classSignature + "\" contains invalid modifiers" );
				}
				
				if( sig.modifiers.containsAll(
					Modifier.ABSTRACT, Modifier.FINAL ) )
				{
					throw new VarException( "class cannot be both abstract and final" );
				}
			}
			if( extendsTokenIndex > classTokenIndex + 1 )
			{
				if( extendsTokenIndex == tokens.length -1 )
				{
					throw new VarException( 
						"\"extends\" token cannot be the last token" );
				}                
				sig.extendsFrom = new _extends( tokens[ extendsTokenIndex + 1 ] );						
                if(( implementsTokenIndex < 0 ) && 
                   ( tokens.length - 2 > extendsTokenIndex ) )
                {
                    throw new VarException(
                        "class can only use single extension inheritance ");
                }
			}
			if( implementsTokenIndex > 0 )
			{
				if( implementsTokenIndex == tokens.length -1 )
				{
					throw new VarException( 
						"implements token cannot be the last token" );
				}
				int tokensLeft = tokens.length - ( implementsTokenIndex + 1 );
				
				String[] implementsTokens = new String[ tokensLeft ];

				
				System.arraycopy( 
					tokens, implementsTokenIndex + 1, implementsTokens, 0, tokensLeft );
				List<String>normalImplementsTokens = new ArrayList<String>();
				for( int i = 0; i < implementsTokens.length; i++ )
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
					else
					{
						normalImplementsTokens.add( implementsTokens[ i ] );
					}
				}
				sig.implementsFrom = _implements.of( 
					normalImplementsTokens.toArray( new String[ 0 ] ) ); //className.of( implementsTokens );
			}
			return sig;		
		}	

        public void setModifiers(String apublic)
        {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
	}

	//move this to enum and interface
    @Override
	public _class replace( String target, String replacement  ) 
	{		
        this.signature.replace( target, replacement );
        this.annotations.replace( target, replacement );
        this.classPackage.replace( target, replacement );
        this.constructors.replace( target, replacement );
        this.javadoc.replace( target, replacement );
        this.constructors.replace( target, replacement );
        this.imports.replace( target, replacement );
		this.methods.replace( target, replacement );
        
        this.staticBlock.replace( target, replacement );
        this.nests.replace( target, replacement );
        
		this.fields.replace( target, replacement );
        return this;        
	}
}