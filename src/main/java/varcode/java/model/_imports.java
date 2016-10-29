package varcode.java.model;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Handles imports
 * 
 * "*" imports?
 * static imports?
 * inner static class imports?
 * array SomeClass[]?
 * generic input
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _imports
    implements Model
{            
	/** Create and return a mutable clone given the imports
     * @param prototype the prototype imports
     * @return a mutable clone
     */
	public static _imports cloneOf( _imports prototype )
	{
		_imports created = new _imports();
		created.merge( prototype );
		return created;
	}
    
    @Override
    public _imports bind( VarContext context )
    {
        String[] ics = importClasses.toArray( new String[ 0 ] );
        Set<String> importClassesBound = new TreeSet<String>();
        for( int i = 0; i < ics.length; i++ )
        {
            importClassesBound.add(Compose.asString( BindML.compile( ics[ i ] ), context ) );
        }
        this.importClasses = importClassesBound;
        
        String[] sis = this.staticImports.toArray( new String[ 0 ] );
        Set<String> sisBound = new TreeSet<String>();
        for( int i = 0; i < sis.length; i++ )
        {
            sisBound.add(Compose.asString( BindML.compile( sis[ i ] ), context ) );
        }
        this.staticImports = sisBound;        
        return this;
    }
    
    @Override
    public _imports replace( String target, String replacement )
    {
        Set<String> replaced = new TreeSet<String>();
        String[] classes = this.importClasses.toArray( new String[0] );
        for( int i=0; i< classes.length; i++ )
        {
            replaced.add( classes[ i ].replace( target, replacement ) ); 
        }
        this.importClasses = replaced;
        
        Set<String> replacedStatic = new TreeSet<String>();
        classes = this.staticImports.toArray( new String[0] );
        for( int i=0; i< classes.length; i++ )
        {
            replacedStatic.add( classes[ i ].replace( target, replacement ) ); 
        }
        this.staticImports = replacedStatic;
        return this;
    }
    
	public static _imports of( Object...imports )
    {
        _imports im = new _imports();
        return im.addImports( imports );
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
	
    @Override
	public String author( Directive... directives ) 
	{
		return Compose.asString( 
			IMPORTS, 
			VarContext.of( 
				"imports", this.importClasses, 
				"staticImports", this.staticImports ), directives );
	}
	
    public _imports addImports( _imports imports )
    {
        for( int i = 0; i < imports.count(); i++ )
		{
			addImports( imports.getImports().toArray() );
		}
		return this;
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
	
    public Set<String> getStaticImports()
    {
        return this.staticImports;
    }
    
    public Set<String> getImports()
    {
        return this.importClasses;
    }	
    
    // we dont need to import these Strings */
    public static final String[] PRIMITIVES = new String[]
        {"boolean", "byte", "char", "double", "float", "int", "long", "short"};
    
	private _imports addImport( Set<String>imports, Object importClass, boolean isStatic )
	{
		if( importClass instanceof Class )
		{
			Class<?> clazz = (Class<?>)importClass;
			if( clazz.isArray() )
			{
				return addImport( imports, clazz.getComponentType(), isStatic );
			}
			if( !clazz.isPrimitive() 
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
            if( Arrays.binarySearch( PRIMITIVES, s ) < 0 
                && !s.startsWith( "java.lang" ) 
                && !s.equals( "class" ) )
            {
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

    @Override
	public String toString()
	{
		return author();
	}

	public void merge( _imports toMerge ) 
	{
		this.importClasses.addAll( toMerge.importClasses );
		this.staticImports.addAll( toMerge.staticImports );
	}	

}
