package varcode.context;

import varcode.doc.Directive;
import varcode.doc.lib.Count;
import varcode.doc.lib.CountIndex;
import varcode.doc.lib.DateTime;
import varcode.doc.lib.SHA1Checksum;
import varcode.doc.lib.SameCount;
import varcode.doc.lib.java.JavaLib;
import varcode.doc.lib.text.AllCap;
import varcode.doc.lib.text.CondenseMultipleBlankLines;
import varcode.doc.lib.text.EscapeString;
import varcode.doc.lib.text.FirstCap;
import varcode.doc.lib.text.FirstLower;
import varcode.doc.lib.text.LowerCase;
import varcode.doc.lib.text.Prefix;
import varcode.doc.lib.text.PrintAsLiteral;
import varcode.doc.lib.text.Quote;
import varcode.doc.lib.text.RemoveEmptyLines;
import varcode.doc.lib.text.StripMarks;
import varcode.doc.lib.text.Trim;
import varcode.eval.Eval_JavaScript;
import varcode.markup.VarNameAudit;
import varcode.script.VarScript;

/**   
 * This class will initialize EVERY new VarContext
 * to provide default functionality for: 
 * <UL>
 *   <LI>parsing Markup into a {@code Dom}
 *   <LI>filling a {@code Dom} as a tailored document  
 * </UL>   
 * 
 * Separated out (from VarContext) for easy access /  changing
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Bootstrap 
{
	;	
	
	public static void init( VarContext context )
	{
	    VarBindings bindings = context.getOrCreateBindings( VarScope.CORE_LIBRARY );

	    bindScript( bindings, Count.INSTANCE, "#", "count" );
	    bindScript( bindings, SameCount.INSTANCE, "#=", "samecount", "sameCount" );
	    bindScript( bindings, CountIndex.INSTANCE, "[#]", "countIndex", "indexCount" );
	        
	    bindDirective( bindings, SHA1Checksum.INSTANCE , "checksum", "sha1" );		
	        
		bindScript( bindings, AllCap.INSTANCE, "^^", "cap", "caps", "allCap" );		
	    bindScript( bindings, FirstCap.INSTANCE, "^", "firstCap", "firstCaps" );	     
	    bindScript( bindings, LowerCase.INSTANCE, "lower", "allLower" );
	    bindScript( bindings, FirstLower.INSTANCE, "firstLower" );
	    bindScript( bindings, EscapeString.INSTANCE, "escapeString", "escape" );
	    bindScript( bindings, PrintAsLiteral.COMMA_SEPARATED_LIST, "lit", "literal", "printAsLiteral" );
	    bindScript( bindings, PrintAsLiteral.USE_ARRAY_NOTATION, "lit[]", "literal[]", "printAsLiteral[]" );
	    bindScript( bindings, Trim.INSTANCE, "trim" );
	    bindScript( bindings, Quote.INSTANCE, "\"", "quote" );
	    bindScript( bindings, RemoveEmptyLines.INSTANCE, "-{}", "removeEmptyLines" );  
	    
	    
	    bindDirective( bindings, CondenseMultipleBlankLines.INSTANCE, "condenseBlankLines" );	    
	    bindDirective( bindings, StripMarks.INSTANCE , "stripMarks" );
	    
	    bindScript( bindings, Prefix.INDENT_4_SPACES, "indent", "indent4", "indent4Spaces" );
	    bindScript( bindings, Prefix.INDENT_TAB, "tab" );
	    
	    bindDirective( bindings, JavaLib.INSTANCE, "java" );
	    
	    context.getOrCreateBindings( VarScope.CORE_LIBRARY ).put( "date", DateTime.DATE_FORMAT );  
	        
		//set the expression evaluator in the context
		context.set( 
			VarContext.EXPRESSION_EVALUATOR_NAME, 
			Eval_JavaScript.INSTANCE, 
			VarScope.CORE );
		
		context.set( 
			VarContext.VAR_NAME_AUDIT_NAME, 
			VarNameAudit.BASE, 
			VarScope.CORE );

		context.set( 
			VarContext.VAR_RESOLVER_NAME,
			Resolve.SmartVarResolver.INSTANCE,
			VarScope.CORE );
		
		context.set( 
			VarContext.SCRIPT_RESOLVER_NAME,
			Resolve.SmartScriptResolver.INSTANCE,
			VarScope.CORE );
	}
	
	public static void bindDirective( 
		VarBindings bindings, Directive boundTo, String... names )
	{
		for( int i = 0; i < names.length; i++ )
		{
			bindings.putDirective( names[ i ], boundTo ); //firstCap    		
	    }
	}
	
	public static void bindScript(
		VarBindings bindings, VarScript script, String... names )
	{
		for( int i = 0; i < names.length; i++ )
		{
	    	bindings.putScript( names[ i ], script );    		
		}
	}
}
