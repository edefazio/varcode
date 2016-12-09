package varcode.java.lang;

import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Directive;

/**
 * Components (classes, interfaces, enums) 
 * that are be nested within one another 
 * (the root node is the "declaring" class): <PRE> 
 * public class A {
 *    public static class B{    
 *    	  private interface I {
 *        }
 *    }
 *    public enum E {
 *    	;
 *    }
 * }
 * </PRE>
 * <A HREF="https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html">Nested Classes</A>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _nests 
    implements JavaMetaLang
{	
    //all nested components of a declaring class (_class, _enum, _interface)
    public List<_model>components = new ArrayList<_model>();

    public _nests()
    {			
    }
		
    public _model getByName( String name )
    {
        for( int i = 0; i < components.size(); i++ )
        {
            if( components.get( i ).getName().equals( name ) )
            {
                return components.get( i );
            }
        }
        return null;
    }
    
    public _model getAt( int index )
    {
        if( index >= 0 && index < components.size() )
        {
            return components.get( index );
        }
        throw new ModelException(
            " index [" + index + "] not in range [0..." + ( components.size() -1 ) + "]" );
    }
    
	public _nests add( _model component )
	{
        //first verify that no other component has the same name
        for( int i = 0; i < this.components.size(); i++ )
        {
            if( this.components.get( i ).getName().equals( component.getName() ) )
            {
                throw new ModelException(
                    "cannot add nested with name \"" + component.getName() + 
                    "\" a component with that name already exists" );
            }
        }
		this.components.add( component );
		return this;
    }
		
    @Override
    public _nests bind( VarContext context )
    {
        for( int i = 0; i < components.size(); i++ )
        {
            this.components.get( i ).bind( context );                
        }
        return this;
    }
        
    @Override
    public _nests replace( String target, String replacement )
    {
        for( int i = 0; i < components.size(); i++ )
        {
            this.components.get( i ).replace( target, replacement );
        }
        return this;
    }
        
	public int count()
	{
		return components.size();
	}
		
    public boolean isEmpty()
    {
        return count() == 0;
    }
        
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
    
    @Override
	public String author( Directive... directives ) 
	{
		if( components == null || components.isEmpty() )
		{
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < components.size(); i++ )
		{
			sb.append( N );  
			sb.append( components.get( i ).author( directives ) );				
		}
		return sb.toString();
	}		
		
    @Override
	public String toString()
	{
		return author();
	}
    
	public static _nests cloneOf( _nests nests ) 
	{
		if( nests == null || nests.count() == 0 )
		{
			return new _nests();
		}
		List<_model> components = nests.components;
		_nests clone = new _nests();
			
		for( int i = 0; i < nests.count(); i++ )
		{
			_model thisComp = components.get( i ); 
			if( thisComp instanceof _class )
			{
				clone.add( _class.cloneOf( (_class)thisComp ) );
			}
			else if( thisComp instanceof _enum )
			{
				clone.add( _enum.cloneOf( (_enum)thisComp ) );
			}
			else if( thisComp instanceof _interface )
			{
				clone.add( _interface.cloneOf( (_interface)thisComp ) );
			}
			else
			{
				throw new ModelException(
					"unknown nested component type " + 
					thisComp + 
					"; expected _class, _enum, _interface ");
			}				
		}
		return clone;
	}
}

