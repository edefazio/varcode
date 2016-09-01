package varcode.markup;

import java.util.HashMap;
import java.util.Map;

import varcode.doc.Directive;
import varcode.doc.lib.SHA1Checksum;
import varcode.doc.lib.java.MarkifyClassName;
import varcode.doc.lib.java.MarkifyPackageName;
import varcode.doc.lib.text.CondenseMultipleBlankLines;
import varcode.doc.lib.text.MarkTarget;
import varcode.doc.lib.text.PostReplace;
import varcode.doc.lib.text.Prefix;
import varcode.doc.lib.text.RemoveAllLinesWith;
import varcode.doc.lib.text.StripMarks;

public enum Markup 
{
	;
	
	public static Directive markify( String target, String varName )
    {
		return new MarkTarget( target, varName );
    }
	
	public static Directive className()
	{
		return className( "className" );
	}
		
	public static Directive className( String varName )
	{
		return className( varName, true );
	}
		
	public static Directive className( String varName, boolean isRequired )
	{
		return new MarkifyClassName( varName, isRequired );
	}
		
	public static Directive packageName()
	{
		return packageName( "packageName" );
	}
		
	public static Directive packageName( String varName )
	{
		return new MarkifyPackageName( varName );
	}
		
	public static Directive required( String... targetVarNamePairs )
	{
		return MarkTarget.ofRequired( targetVarNamePairs );
	}
		
	public static Directive of( String... targetVarNamePairs )
    {    		
    	return MarkTarget.of( targetVarNamePairs );
    }
		
	public static Directive markify( String target, String varName, boolean useDefault )
    {
    	return new MarkTarget( target, varName, useDefault );    		
    }
    	
    public static Directive removeAllMarks()
    {
    	return StripMarks.INSTANCE;
    }

	 //Directives For Java
    // Indent("    ");
    // RemoveLogging()
    // RemoveSystemOut()
    // Plug In Groovy as an expression Engine
    // JShell... whatevs

    //public static Directive markifyRequired( String... targetVarNamePairs )
    //{    		
    //	return MarkTarget.ofRequired( targetVarNamePairs );
    //	}
    public static Directive checksum()
    {
    	return SHA1Checksum.INSTANCE;
    }
    	
    public static Directive indent()
    {
    	return Prefix.INDENT_4_SPACES;
    }
    	
    public static Directive removeAllLinesWith( String... targetStrings )
    {
    	return new RemoveAllLinesWith( targetStrings );
    }
    	
    /**
     * a prefix for each line
     * @param eachLinePrefix
     * @return
     */
    public static Directive prefixEachLineWith( String eachLinePrefix )
    {
    	return new Prefix( eachLinePrefix );
    }
    
    /** replaces "/+*" and "*+/" with "/*" "*/    	
    public static Directive replaceComments()
    {
    	Map<String,String>commentReplaceMap = new HashMap<String,String>();
    	commentReplaceMap.put( "/+*", "/*" );
    	commentReplaceMap.put( "*+/", "*/" );
    	return replace( commentReplaceMap );
    }
    
    public static Directive replace( Map<String,String> targetToReplacement )
    {
    	return new PostReplace( targetToReplacement );
    }
    
    public static Directive replace( String findThis, String replaceWithThis )
    {
    	//create a preprocessor Directive that will replace all instances of
    	return new PostReplace( findThis, replaceWithThis );
    }

    /** replace multiple blank lines with a single blank line */
	public static Directive condenseMultipleBlankLines() 
	{
		return CondenseMultipleBlankLines.INSTANCE;
	}
	
}
