package varcode.java.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import varcode.VarException;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;

/**
 * Models 
 * <UL>
 * <LI>NONE: ( )
 * <LI>ONE: ( "AString" ) 
 * <LI>or MORE THAN ONE:( "Hey", 5, new HashMap<Integer,String>(), true, 'c' )
 * </UL>
 * arguments passed to methods
 * 
 * NOTE: to differentiate between a String and Some code, String literals
 * can be prefixed with "$$"
 * 
 * so this:
 * <PRE>
 * _arguments args = _arguments.of( "new HashMap()", "$$StringLiteral");
 * ...is represented as:
 * System.out.println( args.toString() );
 * 
 *  //prints:
 * "( new HashMap(), \"StringLiteral\" )"
 * 
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _arguments 
	implements Model
{

    /** creates a new _arguments as a clone of prototype
     * @param prototype the prototype to base the clone
     * @return new clone instance
     */
	public static _arguments cloneOf( _arguments prototype ) 
	{
		_arguments clone = new _arguments();
		for( int i = 0; i < prototype.count(); i++ )
		{
			clone.arguments.add( prototype.arguments.get( i ) ); 
		}
		return clone;
	}
	
	public static final Dom ARGUMENTS_LIST = 
		BindML.compile( "( {{+:{+args+}, +}} )" );
	
    /** 
     * returns the argument at the index
     * @param index the index of the argument to get
     * @return String the argument at this index
     */
    public String getAt( int index )
    {
        if( index < count() && index >= 0 )
        {
            return arguments.get( index );
        }
        throw new ModelException( "Invalid argument index ["+ index + "]" );
    }
    
    /** 
     * Adds an argument at the end of the current arguments list
     * @param argument an argument to add to the end of existing arguments
     * @return this (updated with new argument)
     */
    public _arguments addArgument( Object argument )
    {
        this.arguments.add( stringFormOf( argument ) );
        return this;
    }
    
    public _arguments addArguments( Object... arguments )
    {
        for( int i = 0; i< arguments.length; i++ )
        {
            this.arguments.add( stringFormOf( arguments[ i ] ) );
        }
        return this;
    }
    
    @Override
    public _arguments bind( VarContext context )
    {
        for( int i = 0; i < arguments.size(); i++ )
        {
            this.arguments.set(i, 
                Compose.asString( BindML.compile( this.arguments.get( i ) ), context ) );
        }
        return this;
    }
    
    @Override
	public String author( Directive... directives ) 
	{
		return Compose.asString( 
			ARGUMENTS_LIST, 
			VarContext.of( "args", arguments ), 
			directives );
	}
	
	public int count()
	{
		return arguments.size();
	}
	
    /**
     * Decides how arguments are displayed (as Strings) 
     * since we store arguments as Strings
     * <UL>
     * <LI>sometimes we want a String i.e. "myBean" to be a reference to an entity/instance),
     * <LI>sometimes we want the String "Dear Sir or Madam" to be treated as a String literal):
     * we do this by prefixing String literals with "$$", so:
     * <PRE>
     * _arguments args = _arguments.of( "$$Dear Sir or Madam" );
     * will print "( \"Dear Sir or Madam\" );
     * <LI>Sometimes we encounter a null, we want to print "null"
     * <LI>Sometimes we have an entity that describes itself as a String 
     * (i.e. an anonymous class) and we call toString() on it
     * </PRE>
     * @param obj an object argument
     * @return the String representation of the argument (as it would appear in code)
     */
    private static String stringFormOf( Object obj )
    {
        if( obj != null )
	    {
            if( obj.getClass().getPackage().getName()
                .startsWith( "java.lang" ) )
            {       
                //to pass a String, then you need to identify it 
                //as a literal
                if( obj instanceof String )
                {
                    String str = (String) obj;
                    //the String can begin with "$$"
                    if( str.startsWith( STRING_LITERAL_PREFIX ) )
                    {                            
                        return _literal.of( str.substring( 2 ) ).toString();
                    }
                    else
                    {
                        return obj.toString();	
                    }
                }
                else
                {
                    try
                    {
                        _literal l = _literal.of( obj );
                        return l.toString();
                    }
                    catch( Exception e )
                    {
                        return obj.toString();
                    }
                }
            }
            return obj.toString();
	    }	
        return "null";	
    }
    
	/**
	 * @param arguments the arguments
	 * @return a new _arguments container
	 */
	public static _arguments of( Object... arguments )
	{
		if( arguments == null )
		{
			return new _arguments( "null" );
		}
        
		List<String> args = new ArrayList<String>();
		
		for( int i = 0; i < arguments.length ; i++ )
		{
            args.add( stringFormOf( arguments[ i ] ) );            
		}
		return new _arguments( args.toArray( new String[ 0 ] ) );
	}
	
    /** the arguments list */
	private final List<String> arguments;
	
	public _arguments( String...args )
	{
        arguments = new ArrayList<String>();        
        arguments.addAll(Arrays.asList(args));
	}
		
    @Override
	public String toString()
	{
		return author();
    }

    @Override
    public _arguments replace( String target, String replacement )
    {
        for( int i = 0; i < this.arguments.size(); i++ )
        {
            this.arguments.set( 
                i, this.arguments.get( i ).replace( target, replacement ) ); 
        }
        return this;
    }    
}
