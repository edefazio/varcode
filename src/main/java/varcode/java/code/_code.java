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
 * code... PERHAPS I extend _code to provide this functionality
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
		return codeSequence.size() == 0;
	}
	
	/** 
	 * A List of "generic" objects that are convert-able to a sequence
	 * of code.  contains:
	 * <UL>
	 *   <LI>Strings
	 *   <LI>_code
	 * </UL>  
	 */
	private List<Object>codeSequence = new ArrayList<Object>();
	
	/**
	 * provides a try(...resourceInit...) { } structure for code
	 */
	private List<Object> tryWithResources = new ArrayList<Object>();
	
	/**
	 * names of exceptions that are caught 
	 * (MATCHES the caughtException with an entry in handleException)
	 */
	private List<String> catchException = 
		new ArrayList<String>();
				
	/**
	 * 
	 */
	private List<_code> handleException = 
		new ArrayList<_code>();
	
	private List<Object> finallyBlock = new ArrayList<Object>();
	
	/**
	 * If the codeblock does not have any try(resources) catch/finally
	 */
	public static final Dom BARE_CODEBLOCK = BindML.compile(
		"{+codeBlock+}" );
	
	/**
	 * Any try(withResources)...catch()...finally() block of code
	 */
	public static final Dom TRY_CATCH_FINALLY_BLOCK = BindML.compile(
		"try{{+:( {+withResources+} )+}}" + N +
		"{" + N +
		"{+$indent4Spaces(codeBlock)+}"+ N +
		"}" + N +
		"{{+:catch( {+catchException+} e )"+ N +
		"{" + N +
		"{+$indent4Spaces(handleException)+}" + N +
		"}"+ N +
		"+}}{{+:finally" + N +
		"{" + N +
		"{+$indent4Spaces(finallyBlock)+}" + N +
		"}"+ N + 
		"+}}");
	
	public VarContext getContext()
	{
		return VarContext.of(
			"codeBlock", stringify( this.codeSequence ),
			"withResources", stringify( this.tryWithResources ),
			"catchException", catchException,
			"handleException", handleException,
			"finallyBlock", stringify( this.finallyBlock ) );
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
		if( isEmpty( tryWithResources ) &&
			isEmpty( catchException ) &&
			isEmpty( finallyBlock ) )
		{
			return BARE_CODEBLOCK;
		}
		return TRY_CATCH_FINALLY_BLOCK;
	}
	
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
	
	public List<_code>doReplaceCode( List<_code>list, String target, String replacement )
	{
		for(int i=0; i<list.size(); i++)
		{
			list.set(i, list.get( i ).replace( target, replacement ) );
		}
		return list;
	}
	
	public List<Object>doReplace( List<Object>list, String target, String replacement )
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
		this.tryWithResources = doReplace( this.tryWithResources, target, replacement );
		this.handleException = doReplaceCode( this.handleException, target, replacement );
		this.finallyBlock = doReplace( this.finallyBlock, target, replacement );
		for( int i = 0; i < this.catchException.size(); i++ )
		{
			this.catchException.set( i, 
				this.catchException.get( i ).replace( target, replacement ) );
		}
		return this;
	}
	
    @Override
	public String toString()
	{
		return author();
	}
	
	public _code catchHandleException( Class<?> catchException, Object... handleCode )
	{
		this.catchException.add( catchException.getCanonicalName() );
		for( int i = 0; i < handleCode.length; i++ )
		{
			this.handleException.add( _code.of( handleCode[ i ] ) );
		}
		return this;
	}
	
	/**
	 * Catches and exception by name (it may be an exception that is being authored)
	 * @param simpleExceptionName the name of the exception (i.e. "IOException", "FileNotFoundException")
	 * @param handleCode the 
	 * @return
	 */
	public _code catchHandleException( String simpleExceptionName, String... handleCode ) 
	{
		this.catchException.add( simpleExceptionName );
		for( int i = 0; i < handleCode.length; i++ )
		{
			this.handleException.add( _code.of( handleCode[ i ] ) );
		}
		return this;		
	}
	
	public _code tryWith( String... resourceInit )
	{
		for( int i = 0; i < resourceInit.length; i++ )
		{
			this.tryWithResources.add( resourceInit[ i ] );
		}
		return this;
	}
	
	public _code finallyBlock( String...finallyCode )
	{
		for( int i = 0; i < finallyCode.length; i++ )
		{
			this.finallyBlock.add( finallyCode[ i ] );
		}
		return this;
	}
}
