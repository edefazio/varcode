package varcode.java.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.java.CloneInstance;
import varcode.java.lang.JavaMetaLang._body;
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
    implements JavaMetaLang, _body
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
            code.codeSequence.addAll( Arrays.asList( codeSequence ) );
	}
	return code;
    }
	 
    /**
     * Create and return a clone of the prototype code
     * @param prototype the prototype code
     * @return some code
     */
    public static _code cloneOf( _code prototype )
    {
        Object[] cloneArr = new Object[ prototype.codeSequence.size() ];
        for( int i = 0; i < prototype.codeSequence.size(); i++ )
        {
            Object o = prototype.codeSequence.get( i );
            if( o == null )
            {
                cloneArr[ i ] = null;
            }
            else if( o instanceof String )
            {
                cloneArr[ i ] = (String) o;
            }
            else 
            {
                cloneArr[ i ] = CloneInstance.clone( o ); 
            }
        }
        _code theClone = _code.of( cloneArr );
        return theClone;
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
     *   <LI>Template entities (like: _try, _for, _while, _if, _do)
     * </UL>  
     */
    private List<Object>codeSequence = new ArrayList<Object>();
	
    public static final Dom CODEBLOCK = BindML.compile( "{+codeBlock+}" );
	
    /**
     * The context 
     * @return context prior to Authoring
     */
    public VarContext getContext()
    {
	return VarContext.of(
            "codeBlock", stringify( this.codeSequence ) 
        );
    }
    
    @Override
    public _code bind( VarContext context )
    {
        for( int i = 0; i < this.codeSequence.size(); i++ )
        {
            Object o = codeSequence.get( i );
            
            if( o instanceof JavaMetaLang )
            {   //try, do, while, if, for, _thread, 
                codeSequence.set(i, 
                   ((JavaMetaLang)o).bind( context ) );
            }
            else if( o instanceof String )
            {
                codeSequence.set(i , 
                    Compose.asString( BindML.compile( (String)o ), context ) );
            }
            else
            {
                codeSequence.set(i , 
                    Compose.asString( BindML.compile( o.toString() ), context ) );
            }
        }
        return this;
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
            Object o = codeComponents.get( i );
            if( o != null )
            {
                sb.append( o.toString() );			
            }
	}
	return sb.toString();
    }
	
    public Dom getDom()
    {
        return CODEBLOCK;
    }
	
    @Override
    public String author( )
    {
        return author( new Directive[ 0 ] );
    }
        
    @Override
    public String author( Directive... directives ) 
    {			
	return Compose.asString(
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
        headCode.addAll( Arrays.asList( codeLines ) );
	headCode.addAll( codeSequence );
	this.codeSequence = headCode;
	return this;
    }
	
    /** 
     * Adds lines of code to the tail (bottom) of the code block
     * @param codeLines lines of code to add to the tail
     * @return this (modified) 
     */
    public _code addTailCode( Object...codeLines )
    {
        this.codeSequence.addAll(Arrays.asList(codeLines));
	return this;
    }
	
    /**
     * Add code to the tail
     * @param codeBlock a code block to add
     * @return this (modified)
     */
    public _code addTailCode( _code codeBlock )
    {
	this.codeSequence.add( codeBlock );		
	return this;
    }
	
    private static List<Object>doReplace( 
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
            else if( obj instanceof JavaMetaLang )
            {
		replace.add(((JavaMetaLang)obj ).replace( target, replacement ) );
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