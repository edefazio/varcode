package varcode.java.model;

import java.util.ArrayList;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.dom.Dom;

/**
 * Components (classes, interfaces, enums) that can be nested within one another: <PRE> 
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
	public interface component 
	    extends SelfAuthored
	{
		public Dom getDom();
		
		/**
		 * get the Context used to author the Dom into a Class
		 * @return 
		 */
		public VarContext getContext();
		
		/** 
		 * If the component is to be nested within another
		 * we need to move it's imports to the top level
		 * @return imports for this Component
		 */
		public _imports getImports();
	}
	
	public static class _nestGroup
		implements SelfAuthored
	{
		public List<component>components = new ArrayList<component>();

		public _nestGroup()
		{			
		}
		
		public _nestGroup add( component component )
		{
			this.components.add( component );
			return this;
		}
		
		public int count()
		{
			return components.size();
		}
		
		public String toCode( Directive... directives ) 
		{
			if( components == null || components.size() == 0 )
			{
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for( int i = 0; i < components.size(); i++ )
			{
				sb.append( System.lineSeparator() );
				sb.append( components.get(i).toCode( directives ) );				
			}
			return sb.toString();
		}		
		
		public String toString()
		{
			return toCode();
		}

		public static _nestGroup from( _nestGroup nests ) 
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
					clone.add( _class.from( (_class)thisComp ) );
				}
				else if( thisComp instanceof _enum )
				{
					clone.add( _enum.from( (_enum)thisComp ) );
				}
				else if( thisComp instanceof _interface )
				{
					clone.add( _interface.from( (_interface)thisComp ) );
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
