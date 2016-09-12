package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.CodeAuthor;
import varcode.Template;
import varcode.VarException;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.Template.Base;
import varcode.markup.bindml.BindML;

/**
 * Models 
 * <UL>
 * <LI>NONE: ( )
 * <LI>ONE: ( "AString" ) 
 * <LI>or MORE THAN ONE:( "Hey", 5, new HashMap<Integer,String>(), true, 'c' )
 * </UL>
 * arguments
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
 * 
 */
public class _arguments 
	extends Template.Base
{
    /** 
     * Strings passed in with this prefix signify they are 
     * Literals and not a String representation of an entity
     */
    public static final String STRING_LITERAL_PREFIX = "$$";
    
	public static _arguments from( _arguments prototype ) 
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
	
    
    /** returns the argument at the index */
    public String get( int index )
    {
        if( index < count() && index >= 0 )
        {
            return arguments.get( index );
        }
        throw new VarException( "Invalid argument index ["+ index + "]" );
    }
    
    public _arguments addArgument( Object argument )
    {
        this.arguments.add( argumentFor( argument ) );
        return this;
    }
    
    public _arguments addArguments( Object... arguments )
    {
        for( int i = 0; i< arguments.length; i++ )
        {
            this.arguments.add( argumentFor( arguments[ i ] ) );
        }
        return this;
    }
    
	public String author( Directive... directives ) 
	{
		return Author.code( 
			ARGUMENTS_LIST, 
			VarContext.of( "args", arguments ), 
			directives );
	}
	
	public int count()
	{
		return arguments.size();
	}
	
    private String argumentFor( Object obj )
    {
        if( obj != null )
	    {
            if( obj.getClass().getPackage().getName()
                   .startsWith( "java.lang" ) )
            {       
                //if you want to pass a String, then yuo need to identify it 
                //as a literal
                if( obj instanceof String )
                {
                    String str = (String) obj;
                    //the String can begin with "$$"
                    if( str.startsWith( "$$" ) )
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
	 * KeyValue pairs of type - names
	 * "int", "x"
	 * "String", "name"
	 * "double", 
	 * @param arguments the arguments
	 * @return
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
			if( arguments[ i ] != null )
			{
                if( arguments[ i ].getClass().getPackage().getName()
                   .startsWith( "java.lang" ) )
                {          
                    //if you want to pass a String, then yuo need to identify it 
                    //as a literal
                    if( arguments[ i ] instanceof String )
                    {
                        String str = (String) arguments[ i ];
                        //the String can begin with "$$"
                        if( str.startsWith( "$$" ) )
                        {                            
                            args.add( _literal.of( str.substring( 2 ) ).toString() );
                        }
                        else
                        {
                            args.add( arguments[ i ].toString() );	
                        }
                    }
                    else
                    {
                        try
                        {
                            _literal l = _literal.of( arguments[ i ] );
                            args.add( l.toString() );
                        }
                        catch( Exception e )
                        {
                            args.add( arguments[i ].toString() );
                        }
                    }
                }
				else
				{
					 args.add( arguments[i ].toString() );
				}				
			}
			else
			{
				args.add( "null" );
			}
		}
		return new _arguments( args.toArray( new String[ 0 ] ) );
	}
	
	private List<String> arguments;
	
	public _arguments( String...args )
	{
        arguments = new ArrayList<String>();
        
        for( int i = 0; i < args.length; i++ )
        {
            arguments.add( args[ i ] );
        }
	}
		
	public String toString()
	{
		return author();
    }

    public void replace( String target, String replacement )
    {
        for( int i = 0; i < this.arguments.size(); i++ )
        {
            this.arguments.set( 
                i, this.arguments.get(i).replace( target, replacement ) ); 
        }
    }    
}
