package varcode.markup.mark;

import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.lib.text.RemoveEmptyLines;
import varcode.dom.Dom;
import varcode.markup.codeml.CodeMLCompiler;
import junit.framework.TestCase;

public class DocDirectiveTest
	extends TestCase
{

	private final String N = System.lineSeparator();
	
	public void testTailorPostProcessor()
	{
		Dom markup = CodeMLCompiler.fromString( 
			"/*{$$removeEmptyLines()$$}*/" 
		    + N + "A" + N + N + N + N + N + N + "Z" );
		
		VarContext vc = VarContext.of( 
			"removeEmptyLines", RemoveEmptyLines.INSTANCE );
		
		
		System.out.println( Author.code(  markup, vc ) );
		
	}
}
