package varcode.java.model;

import java.util.Set;
import java.util.TreeSet;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.java.JavaNaming;
import varcode.markup.bindml.BindML;

/**
 * 
 * 
 * "*" imports?
 * static imports?
 * inner static class imports?
 * array SomeClass[]?
 * generic input
 * 
 * @author M. Eric DeFazio
 */
public class _imports
	implements SelfAuthored
{
	/** Create and return a mutable clone given the imports */
	public static _imports from( _imports imports )
	{
		_imports created = new _imports();
		created.merge( imports );
		return created;
	}
	
	public static Dom IMPORTS = BindML.compile( 
		"{{+:import {+imports*+};" + N +
		"+}}" +
		"{{+:import static {+staticImports+};" + N +
		"+}}" + N );
	
	private Set<String> importClasses = new TreeSet<String>();
	private Set<String> staticImports = new TreeSet<String>();
	
	public int count()
	{
		return importClasses.size() + staticImports.size();
	}
	
	public boolean contains( String s )
	{
		return importClasses.contains( s ) || staticImports.contains( s );
	}
	
	public String toCode( Directive... directives ) 
	{
		return Author.code( 
			IMPORTS, 
			VarContext.of( 
				"imports", this.importClasses, 
				"staticImports", this.staticImports ), directives );
	}
	
	public _imports addImports( Object...imports )
	{
		for( int i = 0; i < imports.length; i++ )
		{
			addImport( imports[ i ] );
		}
		return this;
	}
	
	public _imports addStaticImports( Object...staticImports )
	{
		for( int i = 0; i < staticImports.length; i++ )
		{
			addStaticImport( staticImports[ i ] );
		}
		return this;
	}
	
	public _imports addStaticImport( Object importStatic ) 
	{
		return addImport( staticImports, importStatic, true );
	}
	
	public _imports addImport( Object importClass )
	{
		return addImport( importClasses, importClass , false );
	}
	
	
	private _imports addImport( Set<String>imports, Object importClass, boolean isStatic )
	{
		if( importClass instanceof Class )
		{
			Class<?> clazz = (Class<?>)importClass;
			if( clazz.isArray() )
			{
				return addImport( imports, clazz.getComponentType(), isStatic );
			}
			if(! clazz.isPrimitive() 
				&& !clazz.getPackage().getName().equals( "java.lang" ) )
			{   //dont need to add primitives or java.lang classes
				if( isStatic )
				{  // they want us to model a class (statically) 
					imports.add( clazz.getCanonicalName() + ".*" );
				}
				else
				{
					imports.add( clazz.getCanonicalName() );
				}
			}			
		}
		else if( importClass instanceof String )
		{
			String s = (String)importClass;
			String[] packageAndClassName = JavaNaming.ClassName.extractPackageAndClassName( s );
			String packageName = packageAndClassName[ 0 ];
			String className = packageAndClassName[ 1 ];
	
			
			if( className.equals( "*" ) )
			{   //wildcard import				 
				JavaNaming.ClassName.validateFullClassName( packageName );
				imports.add( s );				
			}
			else
			{
				JavaNaming.PackageName.validate( packageName );
				JavaNaming.ClassName.validateSimpleName( className );
				imports.add( s );
			}
		}
		else if( importClass.getClass().isArray() )
		{
			Object[] arr = (Object[])importClass;
			for( int i = 0; i < arr.length; i++ )
			{
				addImport( imports, arr[ i ], isStatic );
			}
		}	
		else
		{
			throw new VarException( "Dont know what to do with "+ importClass );
		}
		return this;
	}

	public String toString()
	{
		return toCode();
	}

	public void merge( _imports toMerge ) 
	{
		this.importClasses.addAll( toMerge.importClasses );
		this.staticImports.addAll( toMerge.staticImports );
	}	
}
