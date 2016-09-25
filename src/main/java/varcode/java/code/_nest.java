package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.CodeAuthor;
import varcode.Template;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.dom.Dom;

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
	public interface component 
	    extends CodeAuthor
	{
		public Dom getDom();
		
		/**
		 * get the Context used to author the Dom into a Class
		 * @return the context for this nest component
		 */
		public VarContext getContext();
		
		/** 
		 * If the component is to be nested within another
		 * we need to move it's imports to the top level
		 * @return imports for this Component
		 */
		public _imports getImports();
        
        
        public component bindIn( VarContext context );
                
        public Template.Base replace( String target, String replacement );
	}
	
	public static class _nestGroup
		extends Template.Base
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
		
        public _nestGroup bindIn( VarContext context )
        {
            for( int i = 0; i < components.size(); i++ )
            {
                this.components.get( i ).bindIn( context );                
            }
            return this;
        }
        
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
        
		public String author( Directive... directives ) 
		{
			if( components == null || components.size() == 0 )
			{
				return "";
			}
			StringBuilder sb = new StringBuilder();
			for( int i = 0; i < components.size(); i++ )
			{
				sb.append( "\r\n" );
				sb.append(components.get(i).author( directives ) );				
			}
			return sb.toString();
		}		
		
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
