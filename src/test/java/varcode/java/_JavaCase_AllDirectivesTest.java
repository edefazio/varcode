package varcode.java;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

/*{-*/
import junit.framework.TestCase;
import varcode.VarException;
import varcode.doc.Compose;
import varcode.doc.lib.java.ValidateIdentifierName;
import varcode.doc.lib.java.ValidateTypeName;
import varcode.dom.Dom;
/*-}*/
import varcode.markup.bindml.BindML;

public class _JavaCase_AllDirectivesTest
/*{-*/	extends TestCase/*-}*/
{
	public void testBindML()
	{
		Dom d = BindML.compile(
			"{$varcode.doc.lib.java.ValidateTypeName(type)$}" +  
			"{$varcode.doc.lib.java.ValidateIdentifierName(name)$}" +			   
			"{$sameCount(type,name)$}" + //verifies the number of types and names is the same
			"{{+:{+type+} {+name+}, +}}" );
		
		System.out.println(Compose.asString( d, "type", int.class, "name", "Eric" ) );
		
		System.out.println(Compose.asString( d, 
			"type", new Class[]{int.class, String.class}, 
			"name", new String[]{"Eric", "Daffy"} ) );
		
		Compose.asString( d , "type", "Map<String>", "name", "blah" );
		
		try
		{
			Compose.asString( d , "type", "4", "name", "blah" );	
		}
		catch( VarException ve )
		{
			//expected
			//System.out.println( ve );
		}
	}
	
	public void testJavaBindML()
	{
		Dom d = BindML.compile(
			"{$$java$$}" +
			"{$!typeName(type)$}" +  
			"{$!identifierName(name)$}" +			   
			"{$sameCount(type,name)$}" + //verifies the number of types and names is the same
			"{{+:{+type+} {+name+}, +}}" );
		
		Compose.asString( d , "type", "Map<String>", "name", "blah" );
	}
	
	
	// Handle IOException
	// type could be 1 (not array)
	// name could be 1 (not array)
	// constructs new BufferedWriter( StringWriter )
	
	public static String printArgList( Object[] type, Object[] name )
		throws IOException
	{
		BufferedWriter out = new BufferedWriter( new StringWriter() );
	    if( type == null )
	    {
	        throw new RuntimeException( "type cannot be null" );
	    }
	    if( name == null )
	    {
	        throw new RuntimeException( "name cannot be null" );
	    }
	    if( type.length != name.length )
	    {
	        throw new RuntimeException( "the length of name["+name.length+"] " +
	        	"must be the same type[" +type.length+ "]");
	    }
	    for( int i = 0; i < type.length; i++ )
	    {        
	        ValidateTypeName.INSTANCE.validateType( "type", type[ i ] );
	        ValidateIdentifierName.INSTANCE.validate( "name", name );
	        //validateJavaIdentifier( name[ i ] );
	        if( i > 0 ) 
	        {
	            out.write( ", ");
	        }
	        out.write( type[ i ].toString() );
	        out.write( " " );
	        out.write( name[ i ].toString() );
	    }
	    return out.toString();
	}
	
	public static String printArgList2( Object[] type, Object[] name )
	{
		if( type == null )
		{
			throw new RuntimeException( "type cannot be null" );
		}
		if( name == null )
		{
			throw new RuntimeException( "name cannot be null" );
		}
		if( type.length != name.length )
		{
			throw new RuntimeException( "the length of name["+name.length+"] " +
		        	"must be the same type[" +type.length+ "]");
		}
		String res = "";
		for( int i = 0; i < type.length; i++ )
		{       
			ValidateTypeName.INSTANCE.validateType( "type", type[ i ] );
		    ValidateIdentifierName.INSTANCE.validate( "name", name );
		    
		    if( i > 0 ) 
		    {
		    	res+= ", ";
		    }
		    res+= type[ i ].toString();
		    res+= " ";
		    res+= name[ i ].toString();
		}
		return res;
    }
}
