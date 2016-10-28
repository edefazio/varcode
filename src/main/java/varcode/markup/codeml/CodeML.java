package varcode.markup.codeml;

import java.io.InputStream;

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.Compose;
import varcode.doc.DocState;
import varcode.doc.Dom;
import varcode.markup.MarkupException;
import varcode.markup.mark.Mark;
import varcode.markup.repo.MarkupRepo.MarkupStream;

/**
 * CodeML is a Markup Language that "hides" Marks within comments of
 * source code of languages (Java, Javascript, C, C++, C#, D, F, ...)
 * ...(using /&#42; &#42;/) For example: 
 * <PRE>"public class /&#42;{+className&#42;/_Clazz/&#42;+}&#42;/ {}";</PRE>
 * NOTE:
 *    <UL>
 *       <LI>the Javac Compiler disregards the Mark-comments, and can compile and 
 *       create a class (that contains mark-comments)
 *       <LI>that the CodeMLCompiler will parse/understand the mark comments,
 *       and collect (but not parse) any code not within in a mark. 
 *       (This allows the CodeMLCompiler/CodeMLParser to be used to parse 
 *       code in many languages while also does not interfere with the "target"
 *       language compiler ( Javac, GCC, etc.)
 *    </UL>    
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CodeML
{
	;
	
	/**
	 * Compiles a {@code Dom}  from the String Markup using CodeMLCompiler/Parser
	 * @param codeMLMarkup textual representation of the document
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( String codeMLMarkup )
		throws MarkupException	
	{
		return CodeMLCompiler.fromString( codeMLMarkup );
	}

	/**
	 * Compiles a {@code Dom} from the MarkupStream using CodeMLCompiler/Parser
	 * @param codeMLMarkupStream an input stream containing a document with CodeML Marks
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( MarkupStream codeMLMarkupStream )
		throws MarkupException	
	{
		return CodeMLCompiler.fromMarkupStream( codeMLMarkupStream );
	}

	/**
	 * 
	 * @param codeMLMarkupStream an input Stream containing a document with CodeML Markup
	 * @return a {@code Dom} containing the {@code Mark}s and text.
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Dom compile( InputStream codeMLMarkupStream )
		throws MarkupException	
	{
		return CodeMLCompiler.fromInputStream( codeMLMarkupStream );
	}
	
	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param codeMLMark textual representation of the {@code Mark}
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark( String codeMLMark )
		throws MarkupException
	{
		return parseMark( VarContext.of( ), codeMLMark );
	}
	
	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param context context with which to parse the Mark
	 * (NOTE: Some Marks are Statically Derived at Parse-Time and need access to
	 * {@code VarScripts}, or Variables, for instance I might have a statically
	 * defined variable CIRCUMFERENCE which is equal to "PI * R * 2",  To parse
	 * and create this Mark, it expects the VarContext to be able to resolve 
	 * the variables {"PI" ,"R"} (at Parse-Time).   
	 * @param codeMLMark textual representation of the {@code Mark}
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark( VarContext context, String codeMLMark )
		throws MarkupException
	{
		return parseMark( context, codeMLMark, -1 );
	}

	/**
	 * Parses a single Mark from the {@code MarkText} 
	 * @param context context with which to parse the Mark
	 * (NOTE: Some Marks are Statically Derived at Parse-Time and need access to
	 * {@code VarScripts}, or Variables, for instance I might have a statically
	 * defined variable CIRCUMFERENCE which is equal to "PI * R * 2",  To parse
	 * and create this Mark, it expects the VarContext to be able to resolve 
	 * the variables {"PI" ,"R"} (at Parse-Time). 
	 * @param codeMLMark textual representation of the {@code Mark}
	 * @param lineNumber the lineNumber where the mark occurs within the Markup
	 * @return {@code Mark} Dom-based Object representation of the Mark
	 * @throws MarkupException if there is a problem converting the Textual Markup
	 * to a {@code Dom}
	 */
	public static Mark parseMark(  
		VarContext context,
		String codeMLMark, 
		int lineNumber )
		throws MarkupException
	{
		return CodeMLParser.INSTANCE.parseMark( context, codeMLMark, lineNumber );
	}
	
	/**
	 * 
	 * @param codeMLMarkup text that contains CodeML Marks
	 * @param keyValuePairs pairs of Key-values
	 * @return the result of compiling the {@code Dom} from the CodeML
	 * and tailoring the result with the keyValuePairs
	 */
	public static String tailorCode( String codeMLMarkup, Object...keyValuePairs )
	{
		Dom dom = compile( codeMLMarkup );
		return Compose.asString( dom, keyValuePairs );
	}
	
	/**
	 * 
	 * @param codeMLMarkup text that contains CodeML Marks
	 * @param context the varContext for tailoring the code
	 * @param directives OPTIONAL directives (pre-processors, post-processors)
	 * @return the result of compiling the {@code Dom} from the CodeML
	 * and tailoring the result with the keyValuePairs
	 */
	public static String tailorCode( String codeMLMarkup, VarContext context, Directive...directives )
	{
		Dom dom = compile( codeMLMarkup );
		return Compose.asString( dom, context, directives );
	}

	public static DocState tailor( String codeMLMarkup, Object...keyValuePairs )
	{
		Dom dom = compile( codeMLMarkup );
		return Compose.toState( dom, VarContext.of( keyValuePairs ) );
	}

	public static DocState tailor( String codeMLMarkup, VarContext context, Directive...directives )
	{
		Dom dom = compile( codeMLMarkup );
		return Compose.toState( dom, context, directives );
	}
}
