package varcode.markup.codeml;

import junit.framework.TestCase;
import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Dom;

public class CodeMLStateTest
    extends TestCase
{
	/**
	 * Verify that if I assign a Static Var
	 */
	public void testAssignStatic()
	{
		CodeMLParseState cms = new CodeMLParseState();
		
		//cms.completeMark( "/*{#$removeEmptyLines()}*/" , 0 );
		cms.completeMark( "/*{##a=1##}*/" , 0 );
		
		assertEquals( "1", cms.parseContext.resolveVar( "a" ) );
		
	}
    public void testTextOnly()
    {
        CodeMLParseState cmb = new CodeMLParseState(
            //CodeMLParser.INSTANCE,            
        		VarContext.of() );
            //ForMLCompiler.INSTANCE,);
        
        cmb.addText( "A" );
        Dom cm = cmb.compile(   );
        //System.out.println( cm.toString() );
        
        assertEquals( "A", cmb.compile(  ).getMarkupText() ); //( cm, VarContext.of(  ) ) );
        
        //there are no marks
        assertTrue( cm.getMarkIndicies().cardinality() == 0 );
        
        assertEquals( 0, cm.getAllVarNames( VarContext.of( ) ).size() );
        //HashSet<String> varNames = new HashSet<String>();
		//cm.collectVarNames( varNames, VarContext.of( ) );
		//assertEquals( 0, varNames.size() ); 
        //assertTrue( cm.getAllVarNames( VarContext.of( ) ).size() == 0 );
        assertTrue( cm.getMarks().length == 0 );
        assertTrue( cm.getBlanksCount() == 0 );
        //assertTrue( cm.getForm( "any" ) == null );
        assertTrue( cm.getForms().length == 0 );
        //assertTrue( cm.getMarksByName( "any" ).length == 0 );
        //assertTrue( cm.getUniqueBindMarks().length == 0 );
    }

    public void testOneMarkOnly()
    {
        CodeMLParseState cmb = 
            new CodeMLParseState(  
            	VarContext.of() );        
        cmb.completeMark( "{+Name+}", 1 );
        Dom cm = cmb.compile( );
        //System.out.println( cm.toString() );
        
        assertEquals("", Compose.asString( cm, VarContext.of(  ) ) );
        assertEquals("one", Compose.asString( cm, VarContext.of( "Name", "one" ) ) );
        //assertTrue( BaseTailor.doAlter( cm, Pairs.of( "name", "one" ) ).equals( "One" ) );
        
        //there is one mark
        assertTrue( cm.getMarks().length == 1 );
        
        //it is a bound mark
        assertTrue( cm.getBlanksCount() == 1 );
        
        assertEquals( 1, cm.getAllVarNames( VarContext.of( ) ).size() );
        
        //HashSet<String> varNames = new HashSet<String>();
		//cm.collectVarNames( varNames, VarContext.of( ) );
		//assertEquals( 1, varNames.size() ); 
		assertTrue( cm.getAllVarNames( VarContext.of( ) ).contains( "Name" ) );
        //assertTrue( cm.getAllVarNames( VarContext.of() ).size() == 1 );
        //assertTrue( cm.getAllVarNames( VarContext.of() ).contains( "Name" ) );
        
        assertTrue( cm.getMarkIndicies().cardinality() == 1 );
        //it is at [0]
        assertTrue( cm.getMarkIndicies().get( 0 ) == true );
        
        
        //assertTrue( cm.getForm( "any" ) == null );
        assertTrue( cm.getForms().length == 0 );
        //assertEquals( cm.getAllVarNames( VarContext.of() ).size(), 1 );
        //assertTrue( cm.getAllVarNames( VarContext.of() ).contains( "Name") );
        
        //assertTrue( cm.getMarksByName( "any" ).length == 0 );
        //assertTrue( cm.getMarksByName( "Name" ).length == 1 );
        //assertTrue( cm.getUniqueBindMarks().length == 1 );
    }
    
}
