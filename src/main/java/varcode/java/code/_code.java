package varcode.java.code;

import varcode.Template;
import java.util.ArrayList;
import java.util.List;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * TODO I'm considering whether I should be able to "register Dependency"
 * within a codeBlock, which will allow me to  more easily "port" a block of 
 * code... (add it to the end of a method, static block, etc.)
 * 
 * PERHAPS I extend _code to provide this functionality?
 * 
 * So when I add a catchExceptionClass for instance, I would register it as
 * a dependency (so any code that might need to use this code could query
 * and import the classes appropriately)
 * 
 * from one class/enum/interface to another (i.e. to do multiple inheritance)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _code
    extends Template.Base    
{    
	/**
	 * Creates a code from the objects (Strings, _code) for instance:<PRE>
	 * _code commentLog = _code.of(
	 *     "//this is a comment", 
	 *     "LOG.debug(\"Line After Comment\");");</PRE>
	 *     
	 * represents (2) lines of code...<BR><BR>
	 * 
	 * ...we can also take an existing {@code _code} and add it
	 * to another {@code _code}:<PRE>
	 * _code combined = _code.of( commentLog, "//This is a comment After the Log" );</PRE>
	 * 
	 * Where "combined" is:<PRE>
	 * //this is a comment
	 * LOG.debug("Line After Comment");
	 * //This is a comment After the Log
	 * </PRE> 
	 * 
	 * @param codeSequence a sequence of Strings and _code 
	 * @return the _code representing the code in sequence
	 */
	public static _code of( Object...codeSequence )
	{
		_code code = new _code();
		if( codeSequence != null )
		{
			for( int i = 0; i < codeSequence.length; i++ )
			{
                code.codeSequence.add( codeSequence[ i ] );                
			}
		}
		return code;
	}
	    
	public boolean isEmpty()
	{
		return codeSequence.isEmpty();
	}
	
	/** 
	 * A List of "generic" objects that are convert-able to a sequence
	 * of code.  contains:
	 * <UL>
	 *   <LI>Strings
	 *   <LI>_code
     *   <LI>Template.Base entities (like: _try, _for, _while, _if, _do)
	 * </UL>  
	 */
	private List<Object>codeSequence = new ArrayList<Object>();
	
	public static final Dom CODEBLOCK = BindML.compile(      
		"{+codeBlock+}" );
	
	
	public VarContext getContext()
	{
		return VarContext.of(
			"codeBlock", stringify( this.codeSequence ) 
        );
	}
    
    @Override
    public _code bindIn( VarContext context )
    {
        for( int i = 0; i < this.codeSequence.size(); i++ )
        {
            Object o = codeSequence.get( i );
            
            if( o instanceof Template.Base )
            {   //try, do, while, if, for, _thread, 
                ((Template.Base)o).bindIn( context );
            }
            else if( o instanceof String )
            {
                codeSequence.set( i , 
                    Author.code( BindML.compile( (String)o ), context ) );
            }
            //else dunno
        }
        return this;
    }
    
	@Override
    public String bind( VarContext context, Directive...directives )
    {
        String codeB = null;
        
        if( this.codeSequence != null && !this.codeSequence.isEmpty() )
        {
            codeB = bindify( this.codeSequence, context, directives );
        }
        
        VarContext vc = VarContext.of( 
           "codeBlock", codeB ); 
  
        return Author.code( getDom(), vc, directives );
    }
    
    private static String bindify( List list, VarContext context, Directive... directives  )
    {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < list.size(); i++ )
        {
            if( i > 0 )
            {
                sb.append( "\r\n" );
            }
            Object o = list.get( i );
            if( o instanceof Template.Base )
            {
                sb.append( ((Base) o).bind( context, directives ) );                
            }
            else
            {
                sb.append( 
                    Author.code( 
                        BindML.compile( o.toString() ), context, directives ) );                
            }
        }
        return sb.toString();
    }
        
	private String stringify( List<Object>codeComponents )
	{
		if( codeComponents == null || codeComponents.isEmpty() )
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < codeComponents.size(); i++ )
		{
			if( i > 0 )
			{
				sb.append( "\r\n" );
			}
			sb.append( codeComponents.get( i ).toString() );			
		}
		return sb.toString();
	}
	
	private boolean isEmpty( List list )
	{
		return list == null || list.isEmpty();
	}
	
	public Dom getDom()
	{
        return CODEBLOCK;
	}
	
    @Override
	public String author( Directive... directives ) 
	{			
		return Author.code(
			getDom(), 
			getContext(), 
			directives );
	}
	
	/**
	 * Add a block of code at the head of the method 
	 * (BEFORE all of the existing code in teh codeBlock)
	 * 
	 * @param codeBlock the codeBlokc to add before
	 * @return the mutated _codeBlock
	 */
	public _code addHeadCode( _code codeBlock )
	{
		List<Object> headCode = new ArrayList<Object>();
		headCode.add( codeBlock );		
		headCode.addAll( this.codeSequence );
		this.codeSequence = headCode;
		return this;
	}
	
	/**
	 * Adds code at the "top" (Head) of the code Block
	 * @param codeLines lines of code
	 * @return
	 */
	public _code addHeadCode( Object... codeLines )
	{
		List<Object> headCode = new ArrayList<Object>();
		for( int i = 0; i < codeLines.length; i++ )
		{
			headCode.add( codeLines[ i ] );
		}
		headCode.addAll( codeSequence );
		this.codeSequence = headCode;
		return this;
	}
	
	/** Adds lines of code to the tail (bottom) of the code block */
	public _code addTailCode( Object...codeLines )
	{
		for( int i = 0; i < codeLines.length; i++ )
		{
			this.codeSequence.add( codeLines[ i ] );
		}
		return this;
	}
	
	public _code addTailCode( _code codeBlock )
	{
		this.codeSequence.add( codeBlock );		
		return this;
	}
	
	public List<_code>doReplaceCode( 
        List<_code>list, String target, String replacement )
	{
		for(int i=0; i<list.size(); i++)
		{
			list.set(i, list.get( i ).replace( target, replacement ) );
		}
		return list;
	}
	
	public List<Object>doReplace( 
        List<Object>list, String target, String replacement )
	{
		List<Object> replace = new ArrayList<Object>();
		for( int i = 0; i < list.size(); i++ )
		{
			Object obj = list.get( i );
			if( obj instanceof String )
			{
				replace.add( ((String)obj).replace( target, replacement) ); 
			}
			else if( obj instanceof Template.Base )
			{
				replace.add(  ((Template.Base)obj ).replace( target, replacement ) );
			}
			else
			{
				replace.add( obj.toString().replace( target, replacement ) );
			}
		}
		return replace;
	}
	
    @Override
	public _code replace( String target, String replacement )
	{
		this.codeSequence = doReplace( this.codeSequence, target, replacement );
        return this;
	}
	
    @Override
	public String toString()
	{
		return author();
	}
}
