package varcode.java.model;

import varcode.CodeAuthor;
import varcode.java.JavaCase.JavaCaseAuthor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.JavaCase;
import varcode.java.JavaNaming;
import varcode.java.model._methods._method;
import varcode.java.model._nest._nestGroup;
import varcode.java.model._nest.component;
import varcode.markup.bindml.BindML;

/**
 * "parameteric code" using a fluent builder pattern for 
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
	private final signature classSignature;
	
	private _constructors constructors;
	private _fields fields;
	private _methods methods;
	private _staticBlock staticBlock;
	
	/** Nested inner classes, static inner classes, interfaces, enums */
	private _nestGroup nests;
	
	public static _class from( _class prototype )
	{
		return new _class( prototype );
	}
	
	public static _class of( String classSignature )
	{
		return new _class( null, classSignature ); 
	}
	
	public static _class of( String packageName, String classSignature )
	{
		return new _class( packageName, classSignature ); 
	}
	
	public static _class of( String javadoc, String packageName, String classSignature )
	{
		_class c = new _class( packageName, classSignature );
		c.javaDoc( javadoc );
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
		this.classPackage = _package.of( packageName );
		this.javadoc = new _javadoc();
		this.classSignature = signature.of( classSignature );
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
		this.classPackage = _package.from( prototype.classPackage );
		this.imports = _imports.from( prototype.imports );
		this.classSignature = signature.from( prototype.classSignature  );
		
		this.javadoc = _javadoc.from( prototype.javadoc );
		this.methods = _methods.from( prototype.methods );
		this.fields = _fields.from( prototype.fields );
		if( prototype.staticBlock!= null && prototype.staticBlock.count() > 0 )
		{
			this.staticBlock = _staticBlock.of( prototype.staticBlock.getBody() );
		}
		else
		{
			this.staticBlock = new _staticBlock();
		}
		this.constructors = _constructors.from( prototype.constructors );
		
		//NESTEDS
		this.nests = _nestGroup.from( prototype.nests );
	}
	
	public static final Dom CLASS = 
		BindML.compile( 
			"{+pckage+}" +
			"{{+?imports:{+imports+}" + N +"+}}" +
			"{+classJavaDoc+}" +
			"{+classSignature*+}" + N +
			"{" + N +			
			"{{+?members:{+$indent4Spaces(members)+}" + N +
			"+}}" +
			"{{+?constructors:{+$indent4Spaces(constructors)+}" + N +
			"+}}" +
			"{{+?methods:{+$indent4Spaces(methods)+}" + N + 
			"+}}" +
			"{{+?staticBlock:{+$indent4Spaces(staticBlock)+}" + N +
			"+}}" +
			"{{+?nests:{+$indent4Spaces(nests)+}" + N +
			"+}}" +
			"}" );
			
	public String author( Directive... directives ) 
	{
		return toJavaCase( directives ).toString();				
	}
	
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
				
				//inner classes inherit package
				vc.getScopeBindings().remove("pckage");
				
				//inner classes have imports at top level
				vc.getScopeBindings().remove("imports");
				nested[ i ] = Author.code( comp.getDom(), vc );				
			}
			n = nested;			
		}
		_imports imp = null;
		if( this.getImports().count() > 0 )
		{
			imp = this.getImports();
		}
		_staticBlock sb = null;
		if( this.staticBlock.count() > 0 )
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
			VarContext.of( 
				"pckage", classPackage,
				"imports", imp,
				"classJavaDoc", javadoc,
				"classSignature", classSignature,
				"members", mem,
				"methods", meth,
				"staticBlock", sb, 
				"constructors", cs,
				"nests", n );
	}
	
	public Dom getDom()
	{
		return CLASS;
	}
	
	public String getFullyQualifiedClassName()
	{
		
		if( this.classPackage != null && ! this.classPackage.isEmpty() )
		{
			return this.classPackage.getName() + "." + this.classSignature.getName();
		}
		else
		{
			return this.classSignature.className.name;
		}
	}
	
	public JavaCase toJavaCase( Directive...directives ) 
	{
		return JavaCase.of(
			getFullyQualifiedClassName(),
			CLASS, 
			getContext(),
			directives );			
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
	public _class constructor( String constructorSignature, Object... body )
	{
		_constructors.constructor c = new _constructors.constructor( constructorSignature ).body( body );
		constructors.addConstructor( c );
		return this;
	}
	
	public _class javaDoc( String classJavaDocComment )
	{
		this.javadoc = new _javadoc( classJavaDocComment );
		return this;
	}

	public _class method( String methodSignature )
	{
		return method(  _method.of( null, methodSignature, new String[ 0 ] ) );
	}
	
	public _class method( String methodSignature, String... bodyLines )
	{
		return method( _method.of( null, methodSignature, bodyLines ) );
	}

	public boolean isAbstract()
	{
		return this.classSignature.modifiers.contains( Modifier.ABSTRACT );
	}
	
	public _class staticBlock( String code )
	{
		this.staticBlock.addTailCode( code );
		return this;
	}
	
	public _class method( _method m )
	{
		if( m.isAbstract() && !this.isAbstract() )
		{
			throw new VarException(
				"Cannot add an abstract method " + N + m + N + " to a non-abstract class " );
		}
		if( m.isAbstract() && m.getBody() != null )
		{
			throw new VarException( 
				"abstract method :" + N + m + N + "cannot have a method body" + N + m.getBody() );
		}
		this.methods.addMethod( m );
		return this;
	}
	
	public _class field( String field )
	{
		fields.addFields( _fields.field.of( field ) );		
		return this;
	}
	
	public _class fields( String...fields )
	{
		for( int i = 0; i < fields.length; i++ )
		{
			this.fields.addFields( _fields.field.of( fields[ i ] ) );
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
	
	public _imports getImports()
	{
		for( int i = 0; i < nests.count(); i++ )
		{
			this.imports.merge( nests.components.get( i ).getImports() );
		}
		return this.imports;
	}
	
	public String toString( )
	{
		return author();
	}
	
	public _class packageName( String packageName )
	{
		this.classPackage = _package.of( packageName );
		return this;
	}

    @Override
    public JavaCase toJavaCase(VarContext context, Directive... directives)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
	public static class signature
		implements CodeAuthor
	{
		private _modifiers modifiers = new _modifiers();
		private _class.simpleName className;
		private _extends extendsFrom;
		private _implements implementsFrom;
		
		public static final Dom CLASS_SIGNATURE = 
			BindML.compile("{+modifiers+}class {+className*+}{+extendsFrom+}{+implementsFrom+}" );

		public String author( Directive... directives ) 
		{
			return Author.code( CLASS_SIGNATURE, 
				VarContext.of(
					"className", className,
					"modifiers", modifiers,
					"extendsFrom", extendsFrom,
					"implementsFrom", implementsFrom ), 
				directives );
		} 
		
		private signature()
		{			
		}
		
		public String toString()
		{
			return author();
		}

		public String getName()
		{
			return className.getName();
		}
		
		public static signature from ( signature prototype )
		{
			signature clone = new signature();
			clone.className = (_class.simpleName)
				simpleName.from( prototype.className );
			clone.extendsFrom = _extends.from( prototype.extendsFrom );
			clone.implementsFrom = _implements.from( prototype.implementsFrom );
			clone.modifiers = _modifiers.from( prototype.modifiers );
			
			return clone;
		}
		
		public static signature of( String classSignature )
		{
			signature sig = new signature();
		
			//MUST have sequence
			//   ...class 
			String[] tokens = classSignature.split(" ");
		
			int classTokenIndex = -1;
			int extendsTokenIndex = -1;
			int implementsTokenIndex = -1;
		
			if( tokens.length < 2 )
			{
				throw new RuntimeException( "class must have at least (2) tokens \"class <name>\" " );	
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
					//System.out.println( "implementsTokenIndex " + i );
					implementsTokenIndex = i;
				}
			}
		
			if( ( classTokenIndex < 0 ) || ( classTokenIndex > tokens.length -1 ) )
			{   //cant be 
				throw new VarException(
					"class token cant be not found or the last token"); 
			}
			sig.className = new _class.simpleName( tokens[ classTokenIndex + 1 ] );
		
			if( classTokenIndex > 0 )
			{   //modifier provided
				String[] mods = new String[ classTokenIndex ];
				System.arraycopy( tokens, 0, mods, 0, classTokenIndex );
				sig.modifiers = _modifiers.of( mods );
				if( sig.modifiers.containsAny(
					Modifier.NATIVE, Modifier.SYNCHRONIZED, Modifier.NATIVE,
					Modifier.TRANSIENT, Modifier.VOLATILE, Modifier.STRICT ) )
				{
					throw new VarException( "classSignature contains invalid modifiers" );
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
						"Extends token cannot be the last token" );
				}
				sig.extendsFrom = new _extends( tokens[ extendsTokenIndex + 1 ] );						
			}
			if( implementsTokenIndex > 0 )
			{
				if( implementsTokenIndex == tokens.length -1 )
				{
					throw new RuntimeException( 
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
	}

	public static abstract class name 
	{
		/** 
		 * with an existing simple or full class name, return 
		 * a "clone" 
		 * @param aName the name
		 * @return a "clone"
		 */
		public static name from( name aName ) 
		{
			if( aName instanceof simpleName )
			{
				return new simpleName( ((simpleName) aName).name );
			}
			return new fullName( ((fullName) aName).name );
		}
		
		public static name[] of( String[] tokens )
		{
			name[] classNames = new name[ tokens.length ];
			for(int i=0; i<tokens.length; i++)
			{
				classNames[ i ] = of( tokens[ i ] );
			}
			return classNames;
		}
		
		public static name of( Class<?> clazz )
		{
			return new fullName( clazz );
		}
		
		public static name of( String string )
		{
			if( string.contains( "." ) )
			{
				return new fullName( string );
			}
			return new simpleName( string );
		}		
	}
	
	public static class simpleName
		extends name
	{
		private String name;
		
		public simpleName( Object className )
		{
			if( className == null )
			{
				throw new VarException( "name cannot be null" );
			}
			
			if( className instanceof simpleName )
			{
				this.name = ((simpleName)className).name;			
			}
			else if( className instanceof fullName )
			{
				this.name = ((fullName)className).getSimple();
			}
			else if( className instanceof Class )
			{
				this.name = ((Class<?>)className).getSimpleName();
			}
			else
			{
				this.name = className.toString();			
				JavaNaming.ClassName.validateSimpleName( this.name );
			}				
		}
		
		public String getName()
		{
			return name;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	/**
	 * A Fully Qualified Class Name,
	 * 
	 * so "java.lang.String"
	 * verses "String"
	 * 
	 * @author M. Eric DeFazio eric 
	 *
	 */
	public static class fullName 
		extends name	
	{
		private String name;
		
		public fullName( Object className )
		{
			if( className == null )
			{
				throw new VarException( "className_full cannot be null" );
			}		
			if( className instanceof fullName )
			{
				this.name = ((fullName)className).name;			
			}
			else if( className instanceof Class )
			{
				this.name = ((Class<?>)className).getCanonicalName();
			}
			else
			{
				this.name = className.toString();			
				JavaNaming.ClassName.validateFullClassName( this.name );
			}				
		}
	
		public String getSimple()
		{
			int lastDotIndex = name.lastIndexOf( '.' );
			if( lastDotIndex > 0 )
			{
				return name.substring( lastDotIndex + 1 );
			}
			return name;
		}
	
		public String toString()
		{
			return name;
		}
		
		public String getName()
		{
			return this.name;
		}
	}

	//move this to enum and interface
	public void replace( String target, String replacement  ) 
	{
		
		this.classSignature.className = (_class.simpleName)
			_class.simpleName.of( this.classSignature.getName().replace(target, replacement ) );
		
		this.constructors.replace( target, replacement );
		if( this.javadoc != null)
		{
			this.javadoc.replace( target, replacement );
		}
		//TODO do I need to check fields, methods??
		String[] fieldNames = this.fields.fieldMap().keySet().toArray( new String[ 0 ] );
		for( int i = 0; i < fieldNames.length; i++ )
		{			
			_fields.field f = fields.byName( fieldNames[ i ] );
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
		}
        this.methods.replace( target, replacement );
	}
}
