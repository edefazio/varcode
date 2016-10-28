package varcode.dom;

import varcode.doc.Dom;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.doc.translate.TranslateBuffer;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.markup.bindml.BindML;


public class DomTest
	extends TestCase
{	
	public void testNoMarks()
	{
		Dom d = BindML.compile("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", d.getAllMarksTemplate().fill( ) );
		
		TranslateBuffer f = new TranslateBuffer	();
		d.getAllMarksTemplate().fill( f );
		
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", f.toString() );
		
		assertEquals( 0, d.getAllMarksTemplate().getBlanksCount() );
		assertEquals( -1, d.getAllMarksTemplate().getCharIndexOfBlank( 0 ) );
		
		assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", d.getAllMarksTemplate().getStaticText() );
		
		assertEquals( "", d.getAllMarksTemplate().getTextAfterBlank( 0 ) );
		
		//ALL the text if before the first blank
		assertEquals( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", d.getAllMarksTemplate().getTextBeforeBlank( 0 ) );	
		
		assertEquals( 0, d.getMarkIndicies().cardinality() );
		
		assertEquals( 0, d.getMarks().length );
		//HashSet<String> varNames = new HashSet<String>();
		//d.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 0, d.getAllVarNames( VarContext.of( ) ).size() ); 
		assertEquals( 0, d.getBlankFillers().length );
		assertEquals( 0, d.getFillTemplate().getBlanksCount() );
		assertEquals( 0, d.getForms().length );
		
		assertEquals( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", d.getMarkupText());
		assertEquals( 0, d.getDomContext().getOrCreateBindings( VarScope.STATIC ).size() );		
	}
	
	
	public void testOnlyOneMark()
	{
		Dom d = BindML.compile( "{+a+}" );
		assertEquals( "", d.getAllMarksTemplate().getStaticText() );
		assertEquals( 1, d.getMarkIndicies().cardinality() );
		assertEquals( 1, d.getMarks().length );
		
		//HashSet<String> varNames = new HashSet<String>();
		//d.collectVarNames( varNames, VarContext.of( ) );
		//assertEquals( 1, varNames.size() ); 
		assertEquals( 1, d.getAllVarNames( VarContext.of( ) ).size() );
		//assertEquals( 1, d.getAllVarNames(VarContext.of( ) ).size() ); 
		assertEquals( 1, d.getBlankFillers().length );
		assertEquals( 0, d.getForms().length );
		
		assertEquals( "{+a+}", d.getMarkupText());
		assertEquals( 0, d.getDomContext().getOrCreateBindings( VarScope.STATIC ).size() );
		
		assertEquals( 1, d.getAllMarksTemplate().getBlanksCount() );
		assertEquals( 0, d.getAllMarksTemplate().getCharIndexOfBlank( 0 ) );
		assertEquals( "", d.getAllMarksTemplate().getTextAfterBlank( 0 ) );
		assertEquals( "", d.getAllMarksTemplate().getTextBeforeBlank( 0 ) );
		
		
		assertEquals( "a", d.getAllMarksTemplate().fill( "a" ) );
		
		
		//assertEquals( "", d.allMarksTemplate.fill( null ) );
		
		TranslateBuffer f = new TranslateBuffer();
		d.getAllMarksTemplate().fill( f, "a" );
		assertEquals( "a",  f.toString() );
		
		try
		{
			d.getAllMarksTemplate().fill( f );
		}
		catch( VarException ve )
		{
			//expected exception (expects 1 parameter got 0)
		}
		
		try
		{
			d.getAllMarksTemplate().fill( );
		}
		catch( VarException ve )
		{
			//expected exception (expects 1 parameter got 0)
		}
	}
	
	public void testMarkAndText()
	{
		Dom d = BindML.compile( "start{+a+}middle{+b+}end" );
		assertEquals( "startmiddleend", d.getAllMarksTemplate().getStaticText() );
		assertEquals( 2, d.getMarkIndicies().cardinality() );
		assertEquals( 2, d.getMarks().length );
		
		assertEquals( 2, d.getAllVarNames( VarContext.of( ) ).size() );
		
		//HashSet<String> varNames = new HashSet<String>();
		//d.collectVarNames( varNames, VarContext.of( ) );
		//assertEquals( 2, varNames.size() ); 
		
		//assertEquals( 2, d.getAllVarNames(VarContext.of( ) ).size() ); 
		assertEquals( 2, d.getBlankFillers().length );
		assertEquals( 0, d.getForms().length );
		
		assertEquals( "start{+a+}middle{+b+}end", d.getMarkupText());
		assertEquals( 0, d.getDomContext().getOrCreateBindings( VarScope.STATIC ).size() );
		
		assertEquals( 2, d.getAllMarksTemplate().getBlanksCount() );
		assertEquals( 5, d.getAllMarksTemplate().getCharIndexOfBlank( 0 ) );
		assertEquals( 11, d.getAllMarksTemplate().getCharIndexOfBlank( 1 ) );
		
		assertEquals( "start", d.getAllMarksTemplate().getTextBeforeBlank( 0 ) );
		assertEquals( "middle", d.getAllMarksTemplate().getTextAfterBlank( 0 ) );
		
		assertEquals( "middle", d.getAllMarksTemplate().getTextBeforeBlank( 1 ) );
		assertEquals( "end", d.getAllMarksTemplate().getTextAfterBlank( 1 ) );
		
		
		assertEquals( "start(1)middle(2)end", d.getAllMarksTemplate().fill( "(1)", "(2)" ) );
		
	}
}
