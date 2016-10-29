package varcode.java.model;

import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.Model;
import varcode.java.model._fields;

/**
 * Components (classes, interfaces, enums) 
 * that can be nested within one another: <PRE> 
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
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface _nest 
{	
    /**
     * A Component that can be nested (_class, _enum, _interface)
     * within a top level model (_class, _enum, _interface)
     */
	public interface component 
	    extends Model
	{
        /** @reurn the name of this component (simple name)*/
        String getName();
        
        /** @return the Dom of the component */
		Dom getDom();
		
		/** @return the context for this nest component */
		VarContext getContext();
		
		/** @return all imports for this component */
		_imports getImports();
        
        /** @return  the fields for this component */
        _fields getFields();
        
        /** @return the _methods for this component */
        _methods getMethods();
	}
	
    /**
     * a grouping of all nested (classes, enums, interfaces)
     * 
     */
	public static class _nestGroup
        implements Model
    {        
		public List<component>components = new ArrayList<component>();

		public _nestGroup()
		{			
		}
		
        public component getByName( String name )
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
        
		public _nestGroup add( component component )
		{
			this.components.add( component );
			return this;
		}
		
        @Override
        public _nestGroup bind( VarContext context )
        {
            for( int i = 0; i < components.size(); i++ )
            {
                this.components.get( i ).bind( context );                
            }
            return this;
        }
        
        @Override
        public _nestGroup replace( String target, String replacement )
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

        
		public static _nestGroup cloneOf( _nestGroup nests ) 
		{
			if( nests == null || nests.count() == 0 )
			{
				return new _nestGroup();
			}
			List<component> components = nests.components;
			_nestGroup clone = new _nestGroup();
			
			for( int i = 0; i < nests.count(); i++ )
			{
				component thisComp = components.get( i ); 
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
					throw new VarException(
						"unknown nested component type " + 
						thisComp + 
						"; expected _class, _enum, _interface ");
				}				
			}
			return clone;
		}
	}
}
