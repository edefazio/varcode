package varcode.markup.codeml;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.markup.MarkupException;
import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddFormIfExpression;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddVarIsExpression;
import varcode.markup.mark.Cut;
import varcode.markup.mark.CutComment;
import varcode.markup.mark.CutIfExpression;
import varcode.markup.mark.CutIfVarIs;
import varcode.markup.mark.CutJavaDoc;
import varcode.markup.mark.DefineVar;
import varcode.markup.mark.DefineVarAsExpressionResult;
import varcode.markup.mark.EvalExpression;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.Mark;
import varcode.markup.mark.ReplaceWithExpressionResult;
import varcode.markup.mark.ReplaceWithForm;
import varcode.markup.mark.ReplaceWithScriptResult;
import varcode.markup.mark.ReplaceWithVar;
import junit.framework.TestCase;

/**   
 * Tests that given Strings I can parse and return the appropriate 
 * Mark implementations
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class CodeMLParserMarkTest
	extends TestCase
{
    /** A TAB character (as a String) */
    public static final String T = (char)9 + "";
    
    /** A lineFeed String */
    public static final String N = System.lineSeparator();
    
	public void testAddFormIfExpression()
	{
		String mark = "/*{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}*/";
		Mark theMark = CodeML.parseMark( mark );
		AddFormIfExpression afie = (AddFormIfExpression)theMark;
		assertEquals( " logLevel > debug ", afie.getExpression() );
		assertEquals( "LOG.debug({+a+} + {+b+});", afie.getForm().getText() );
	}
	
	public void testAddFormIfExpressionAlt()
	{
		String mark = "/*{_+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+_}*/";
		Mark theMark = CodeML.parseMark( mark );
		AddFormIfExpression afie = (AddFormIfExpression)theMark;
		assertEquals( " logLevel > debug ", afie.getExpression() );
		assertEquals( "LOG.debug({+a+} + {+b+});", afie.getForm().getText() );
	}
    
    public void testTagToAdd() 
    {
    	//We dont have a way of doing this yet
    	// IfExpressionAddScriptResult
    	//Dom d = CodeML.compile( "/*{+?((env < test)):$captureInput(input=rateInputVo)+}*/" );    	
    	//assertEquals( "", Tailor.code( d ,new VarContext() ) );
    	
    	//If Expression AddScriptResult
    	Dom d = CodeML.compile(
    		"/*{#captureInput:(( "
    		+ "typeof env != 'undefined' "
    		+ "&& typeof test != 'undefined' "
    		+ "&& env < test ))#}*/" +
    		"/*{+?((captureInput==true)):$capture()+}*/" );
    	
    	assertEquals("", Compose.asString( d , VarContext.of() ) );
    	
    	assertEquals("", Compose.asString( d , VarContext.of( "env", 100, "test", 150 ) ) );

    }
    
    public void testEvalExpressionMark()
    {
    	// /*{(( (a + b / 2) |0 ))}*/ EvalExpression
    	String expression = "3 + 8";
    	String mark = "/*{(("+ expression + "))}*/";
    	
    	EvalExpression ee = (EvalExpression)CodeML.parseMark( mark );
    	
    	assertEquals( -1, ee.getLineNumber() );
    	assertEquals( mark, ee.getText() );
    	assertEquals( expression, ee.getExpression() );
    	assertEquals( null, ee.derive( VarContext.of( ) ) );
    }
    public void testReplaceWithExpressionResultMark()
    {
    	///*{+(( (a + b / 2) |0 ))*/ 37 /*+}*/ ReplaceWithExpressionResult
    	String expression = "3 + 8"; 
    	String toReplace = "REPLACEME";
    	String mark = "/*{+((" + expression + "))*/" + toReplace + "/*+}*/";
    	ReplaceWithExpressionResult rwe = 
    		(ReplaceWithExpressionResult)CodeML.parseMark( mark );
    	
    	assertEquals( expression, rwe.getExpression() );
    	assertEquals( toReplace, rwe.getWrappedText() );
    	VarContext vc = VarContext.of( );
    	assertEquals( 11, rwe.derive( vc ) );    	
    }

    public void testReplaceWithExpressionResultMarkVars()
    {
    	///*{+(( (a + b / 2) |0 ))*/ 37 /*+}*/ ReplaceWithExpressionResult
    	String expression = "a + b"; 
    	String toReplace = "REPLACEME";
    	String mark = "/*{+((" + expression + "))*/" + toReplace + "/*+}*/";
    	ReplaceWithExpressionResult rwe = 
    		(ReplaceWithExpressionResult)CodeML.parseMark( mark );
    	
    	assertEquals( expression, rwe.getExpression() );
    	assertEquals( toReplace, rwe.getWrappedText() );
    	VarContext vc = VarContext.of("a", 1, "b", 2);
    	assertEquals( 3.0, rwe.derive( vc ) );    	
    }
    
    public void testAddExpressionResultMark()
    {
    	Mark ma = CodeML.parseMark( "/*{+((3 + 5))+}*/" );
    	assertTrue( ma instanceof AddExpressionResult );
    	AddExpressionResult ar = (AddExpressionResult) ma;
    	assertEquals( ar.getExpression(), "3 + 5" );
    	assertEquals( 8 , ar.derive( VarContext.of() ) );
    }

    public void testDefineInstanceVarAsExpressionResultMark()
    {
    	Mark ma = CodeML.parseMark( "/*{#a:((5+4))#}*/");
    	assertTrue( ma instanceof DefineVarAsExpressionResult.InstanceVar );
    	DefineVarAsExpressionResult.InstanceVar da = 
    		(DefineVarAsExpressionResult.InstanceVar)ma;
    	assertEquals( "5+4", da.getExpression());
    	assertEquals( 9, da.derive( VarContext.of() ) );
    	VarContext vc = VarContext.of( ); 
    	da.bind( vc );
    	assertEquals( 9, vc.resolveVar( "a" ) );    	
    }
    
    public void testDefineStaticVarAsExpressionResultMark()
    {
    	Mark ma = CodeML.parseMark( "/*{##a:((5+4))##}*/");
    	assertTrue( ma instanceof DefineVarAsExpressionResult.StaticVar );
    	DefineVarAsExpressionResult.StaticVar da = 
    		(DefineVarAsExpressionResult.StaticVar)ma;
    	assertEquals( "5+4", da.getExpression());
    	assertEquals( 9, da.derive( VarContext.of() ) ); 
    	    	
    }
    
    public void testCutIfVarIs()
    {
    	//cut if var equals (exactly)
    	Mark ma = CodeML.parseMark( "/*{-?removeLog==true:*/someText/*-}*/" );
    	assertTrue( ma instanceof CutIfVarIs );
    	CutIfVarIs ar = (CutIfVarIs) ma;
    	assertEquals( ar.getVarName(), "removeLog" );
    	assertEquals( ar.getTargetValue(), "true" );
    	assertEquals( ar.getWrappedText(), "someText" );
    	
    	assertEquals( "someText" , ar.derive( VarContext.of() ) );
    	assertEquals( "" , ar.derive( VarContext.of("removeLog", "true") ) );
    	assertEquals( "someText" , ar.derive( VarContext.of("removeLog", "false") ) );
    	
    	//if removeLog is bound AT ALL
    	ma = CodeML.parseMark( "/*{-?removeLog:*/someText/*-}*/" );
    	assertTrue( ma instanceof CutIfVarIs );
    	ar = (CutIfVarIs) ma;
    	assertEquals( ar.getVarName(), "removeLog" );
    	assertEquals( ar.getTargetValue(), null );
    	assertEquals( ar.getWrappedText(), "someText" );
    	
    	assertEquals( "someText" , ar.derive( VarContext.of() ) );
    	assertEquals( "" , ar.derive( VarContext.of("removeLog", "anything") ) );
    	assertEquals( "" , ar.derive( VarContext.of("removeLog", false) ) );
    	
    }
    
    public void testCutIfExpression()
    {
    	Mark ma = CodeML.parseMark( "/*{-?(( 3 + 5 == 8 )):*/someText/*-}*/" );
    	assertTrue( ma instanceof CutIfExpression );
    	CutIfExpression ar = (CutIfExpression) ma;
    	assertEquals( ar.getExpression(), " 3 + 5 == 8 " );
    	assertEquals( null , ar.derive( VarContext.of() ) );
    	
    	ar = (CutIfExpression)CodeML.parseMark( "/*{-?(( 3 + 5 == 9 )):*/someText/*-}*/" );
    	assertEquals( "someText" , ar.derive( VarContext.of() ) );    	
    }
    
    public void testRequiredReplaceMark()
    {
    	Mark ma = CodeML.parseMark( "/*{+$script(*/replace/*)*+}*/" );
    	assertTrue( ma instanceof ReplaceWithScriptResult );
    	ReplaceWithScriptResult r = (ReplaceWithScriptResult) ma;
    	assertTrue( r.isRequired() );
    }
    public void testScriptReplaceMark()
    {
    	Mark ma = CodeML.parseMark( "/*{+$script(*/replace/*)+}*/" );
    	assertTrue( ma instanceof ReplaceWithScriptResult );
    	ReplaceWithScriptResult r = (ReplaceWithScriptResult) ma;
    	assertFalse( r.isRequired() );
    }
    
    public void testNamedListReplaceWithForm()
    {
        String mark = 
        "/*{{+eachField*:" + N
      + "public static int {+name+};" + N
      + N  
      +"*/"+ N
      +"public static int a;" + N
      + "/*+}}*/";
        
        ReplaceWithForm ma = (ReplaceWithForm)CodeML.parseMark( mark );
        assertEquals( ma.getVarName(), "eachField" );        
    }
    
    public void testCutIf()
    {
        String EXPRESSION = 
           "typeof debug==='undefined' || debug == false || debug.equals( 'false' )";
        
        String CONTENT = 
            "System.out.println(\"Created new source code export directory\");";
        
        String MARK = "/*{-?((" + EXPRESSION + ")):*/" 
                     + CONTENT
                     + "/*-}*/";
        CutIfExpression ci = (CutIfExpression)CodeML.parseMark( MARK );
        
        assertEquals( ci.getText() , MARK );
        assertEquals( EXPRESSION, ci.getExpression() );
        assertEquals( CONTENT, ci.getConditionalText() );        
    }
    
    public void testDefine()
    {
        String mark = "/*{#name:eric#}*/";
        
        //todo allow = INSTEAD OF :
        DefineVar d = (DefineVar)CodeML.parseMark( mark );
        assertEquals( d.getVarName(), "name" );
        assertEquals( d.getValue(), "eric" );
        
        
        //I'd rather not, but I see this happening alot, so lets just make it valid
        mark = "/*{#name=eric#}*/";
        d = (DefineVar)CodeML.parseMark( mark );
        assertEquals( d.getVarName(), "name" );
        assertEquals( d.getValue(), "eric" );
        
    }
    
    public void testMatchCloseTag()
    {
        assertEquals( "+}", CodeMLParser.INSTANCE.closeTagFor( "{+" ) );
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{+" ) );
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{+$" ) );
        assertEquals( "+}}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{{+" ) );
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{+?" ) );
        assertEquals( "+}}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{{+?" ) );
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{+?" ) );
        
        assertEquals( "/*-}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{-*/" ) );
        assertEquals( "/*-}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{-?" ) );
        assertEquals( "-}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{-" ) );
        assertEquals( "-}*/", CodeMLParser.INSTANCE.closeTagFor( "/**{-" ) );
        
        assertEquals( "#}}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{{#" ) );
        assertEquals( "#}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{#" ) );
        
        assertEquals( "##}}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{{##" ) );
        assertEquals( "##}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{##" ) );  
    }
    
    public void testAllMarksValid()
    {
        //Add
    	System.out.println( CodeML.parseMark( "{+name+}" ).getClass() );
        assertTrue( CodeML.parseMark( "{+name+}" ) instanceof AddVarIsExpression );
        assertTrue( CodeML.parseMark( "/*{+name+}*/" ) instanceof AddVarIsExpression );
        assertTrue( CodeML.parseMark( "/*{+name:(( name.length() > 1 ))+}*/" ) instanceof AddVarIsExpression );
        
        assertTrue( CodeML.parseMark( "/*{+$date(format=yyyy-MM-dd)+}*/" ) instanceof AddScriptResult );
        
        //Replace
        CodeML.parseMark( "/*{+replace*/code to be replaced/*+}*/" );
        
        //AddScriptResult
        CodeML.parseMark( "/*{+$date(*/2013-03-14/*)+}*/" );
        
        CodeML.parseMark( "/*{{+:{+type+} {+name+}, */int a, String b/*+}}*/" );
        
        //IfAddCode
        CodeML.parseMark( "/*{+?log=trace:LOG.trace(\"Rate loop [\"\"+i+\"\"])+}*/" );
        //IfAddCodeForm
        CodeML.parseMark( 
            "/*{{+?log:" + N
          + "import {+loggerClass+};" + N
          + "import {+loggerFactory+};" + N
          + "INTERIOR TEXT" + N
          + "+}}*/");
        
        //CutCode
        CodeML.parseMark( "/*{-*/ System.out.println(\"Got here\" ); /*-}*/" );
        //CutComment
        CodeML.parseMark( "/*{- cut comment -}*/" );
        
        CodeML.parseMark( "/**{- cut Javadoc comment -}*/" );
        
        //SetMetadata
        CodeML.parseMark( "/**{@metadata:works @}*/" );
        //DefineCodeForm
        CodeML.parseMark( "/*{{#className:IntFrameOf{+fieldCount+}#}}*/" );
        
        //DefineVarAsScriptResult          
        CodeML.parseMark( "/*{#today:$date(format=yyyy-MM-dd)#}*/" );

        //IfAddScriptResult     
        CodeML.parseMark( "/*{+?env=dev:$captureInput(input=rateInputVo)+}*/" );        
    }
    

    private static VarContext vc()
    {
        return VarContext.of();
    }
    
    public void testAddScriptResult()
    {
        assertEquals( "+}*/", CodeMLParser.INSTANCE.closeTagFor( "/*{+$" ) );
        
        AddScriptResult fun = (AddScriptResult)CodeML.parseMark(
            vc(),
            "/*{+$tab(name=this   is  tab separated   )}*/", 
            0 );
        assertEquals( "tab", fun.getScriptName() );
        //System.out.println(  fun.getInput() );
        assertTrue( fun.getScriptInput() != null );
        
    }
    
    public void testScriptMark()
    {
        RunScript sm = 
          (RunScript) CodeML.parseMark( "/*{$scriptName()$}*/" );
        assertEquals( "", sm.getScriptInput() );
        
        assertEquals( "scriptName",  sm.getScriptName() );
        
        sm = 
          (RunScript) CodeML.parseMark( "/*{$scriptName(input)$}*/" );
        
        assertEquals("/*{$scriptName(input)$}*/", sm.getText() );                    
    }
    
    public void testCutComment()
    {
        CutComment c = (CutComment)CodeML.parseMark( "/*{- comment-}*/"  );
        //assertTrue( c.toString().equals("/*{- comment-}*/" ) );
        assertTrue( c.getCutText().equals(" comment" ) );
        assertTrue( c.getText().equals( "/*{- comment-}*/" ) );
        //assertTrue( c.getLineNumber() == 0 );
        //assertTrue( c.getCloseTag().equals( "}*/" ) );
        //assertTrue( c.getOpenTag().equals( "/*{-" ) );        
    }

    public void testCutJDocComment()
    {
        CutJavaDoc c = (CutJavaDoc)CodeML.parseMark( "/**{- comment-}*/"  );
        assertTrue( c.getCutText().equals(" comment") );
        assertTrue( c.getText().equals( "/**{- comment-}*/" ) );
        //assertTrue( c.getLineNumber() == 0 );        
    }
    
	
	public void testAddCode()
	{
		AddVarIsExpression i = (AddVarIsExpression)CodeML.parseMark( "{+name+}" );
	    //assertTrue( i.directFill( "blah" ).equals( "blah" ) );
	    //assertTrue( i.directFill( null ).equals( "" ) ); //we dont want to print "null"
	    assertEquals( null, i.derive( VarContext.of() ) ); //if cant resolve, it's blank
	    assertTrue( i.derive( VarContext.of( "name", "blah2" ) ).equals( "blah2" ) );
	}
	
	public void testAddCodeWithDefault()
	{
	    AddVarIsExpression i = (AddVarIsExpression)CodeML.parseMark( "{+name|default+}");
        //assertTrue( i.directFill( "blah" ).equals( "blah" ) );
        //assertTrue( i.directFill( null ).equals( "" ) ); //use default when null
        assertTrue( i.derive( VarContext.of() ).equals( "default" ) ); //if cant resolve, it's blank
        assertTrue( i.derive( VarContext.of( "name", "blah2" ) ).equals( "blah2" ) );
	}
	
	
	public void testAddReplaceCodeForm()
	{
	    String formPattern = "this is {+name+}"; 
	    Mark m = CodeML.parseMark( 
            "/*{{+a*:" + System.lineSeparator() 
          + formPattern + "*/" + System.lineSeparator() 
          + "WRAPPED CONTENT "
          + "/*+}}*/" );
	    assertTrue( m instanceof ReplaceWithForm );
	    
	    ReplaceWithForm addReplaceCodeForm = (ReplaceWithForm) m;
	    /*
	    addReplaceCodeForm.getForm().().equals( formPattern );
	    */
	    addReplaceCodeForm.getForm().derive( 
	        VarContext.of( "name", "eric" ) )
	        .equals( "this is eric" );
	    
	    //basically if we SET "a", it is used for the value
	    String replaced = (String)addReplaceCodeForm.derive( 
	        VarContext.of( 
	            //"a", "theResult"
	            "name", "result" 
	        ) );
	    //System.out.println( "\""+ replaced +"\"" );
	    assertTrue( replaced.equals( "this is result" ) );
	    
	    //if "a" doesnt exist, I should TRY to derive a from the values
	    
	    replaced = (String)addReplaceCodeForm.derive( 
	        VarContext.of( 
                "name", "derived"
            ) );
	    //System.out.println( "FORMSERIES "+ replaceForm.getFormPattern().getFormSeries() );
	    //System.out.println( "REPLACED \"" + replaced + "\"" );
	    assertTrue( replaced.equals( "this is derived" ) );
	}
	
	/** verify that I can go from a String (that represents a Mark) to the correct Mark implementation by parsing*/
	public void testParseMarks()
	{	
	    
		Mark m = CodeML.parseMark( "/*{-*/deleteMark text between/*-}*/");
		assertTrue( m  instanceof Cut );
		Cut dm = (Cut)m;
		//System.out.println( "\"" + dm.getWrappedContent() + "\"" );
		assertTrue( dm.getCutText().equals( "deleteMark text between" ) ); //there IS NO between text in an insert Mark
		assertTrue( dm.getText().equals( "/*{-*/deleteMark text between/*-}*/" ) );
		
		assertTrue( dm.getLineNumber() == -1 );
		
		m = CodeML.parseMark( 
		    "/*{{+a:" + N 
		  + "this is {+name+} " + N
		  + "*/" + N
		  + "/*+}}*/");
		
		assertTrue( m instanceof ReplaceWithForm );
		
		
		m = CodeML.parseMark( "/*{-comment-}*/" );
		assertTrue( m  instanceof CutComment );
		//assertTrue( m.getContent().equals( "comment" ) );
		m = CodeML.parseMark( "/*{- comment-}*/");
        assertTrue( m  instanceof CutComment );
        //assertTrue( m.getContent().equals( " comment" ) );
        
		m = CodeML.parseMark( "/*{+insertMark+}*/");
		assertTrue( m instanceof AddVarIsExpression );
		AddVarIsExpression im = (AddVarIsExpression)m;		
		assertEquals( "/*{+insertMark+}*/", im.getText()  );
		
		assertEquals( -1, im.getLineNumber() );
		
		m = CodeML.parseMark( "/*{+replaceMark*/replaceMark between text/*+}*/");
		assertTrue( m  instanceof ReplaceWithVar );
		ReplaceWithVar rm = (ReplaceWithVar)m;
		assertEquals( "replaceMark between text", rm.getWrappedText() ); 
		assertEquals( "/*{+replaceMark*/replaceMark between text/*+}*/", rm.getText() );
		
		assertTrue( rm.getLineNumber() == -1 );
		
		m = CodeML.parseMark( "/*{+replaceMark*/\"replaceMark between text\"/*+}*/");
		assertTrue( m  instanceof ReplaceWithVar );
		ReplaceWithVar rmq = (ReplaceWithVar)m;
		assertTrue( rmq.getWrappedText().equals( "\"replaceMark between text\"" ) ); 
		assertTrue( rmq.getText().equals( "/*{+replaceMark*/\"replaceMark between text\"/*+}*/" ) );
		
		assertTrue( rmq.getLineNumber() == -1 );
	}
	
	
	public void testParseMarksCarriageReturns()
	{
		String between = "deleteMark " + System.lineSeparator()
				+ " text between";
		
		String mark = "/*{-*/" + between + "/*-}*/";
		
		Mark m = CodeML.parseMark( mark);
		assertTrue( m  instanceof Cut );
		Cut dm = (Cut)m;
		assertTrue( dm.getCutText().equals( between ) ); //there IS NO between text in an insert Mark
		assertTrue( dm.getText().equals( mark ) );
		
		assertTrue( dm.getLineNumber() == -1 );
		
		between = "replaceMark " + System.lineSeparator() + " between " + System.lineSeparator() + " text";
		mark = "/*{+replaceMark*/" + between + "/*+}*/";
		m = CodeML.parseMark( mark);
		assertTrue( m instanceof ReplaceWithVar );
		ReplaceWithVar rm = (ReplaceWithVar)m;
		assertTrue( rm.getWrappedText().equals( between ) ); 
		assertTrue( rm.getText().equals( mark ) );
		
		assertTrue( rm.getLineNumber() == -1 );
		
		//Make sure that if I have Quoted Replace Marks, I parse appropriately
		between = "\" some text " + System.lineSeparator() +
				  " more text "+ System.lineSeparator() +
				  "last bit of Text\"";
		mark = "/*{+replaceMark*/" + between + "/*+}*/";
		
		m = CodeML.parseMark( mark);
		
		assertTrue( m instanceof ReplaceWithVar );
		ReplaceWithVar rmq = (ReplaceWithVar)m;
		assertTrue( rmq.getWrappedText().equals( between ) ); 
		assertTrue( rmq.getText().equals( mark ) );		
	}
	
	public void testBadReplaceMarksParse()
	{
		try
		{
			Mark m = 
				CodeML.parseMark( 
			          "/*{^name*/replaceText/*}*/" );
			fail( "expected CodeMarkException (&& Invalid Java identifier); instead got "+ m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
		
		try
		{
			Mark m = CodeML.parseMark( "/*{static*/replaceText/*}*/" );
			fail( "expected ParseException (static is a reserved word in Java, not Invalid Java identifier); instead got " + m );
		}
		catch ( MarkupException cme )
		{
			//expected
		}		
		try
		{
			Mark m = CodeML.parseMark(
			    "/*{replaceMark*/text inside /*{and another close /}*/" );
			fail( "expected ParseException; instead got " + m );
		}
 		catch( MarkupException cme )
		{
			//expected
		}
		
		try
		{
			Mark m = CodeML.parseMark( 
			    "/*{replaceMark*/text inside and }*/ another close /}*/" );
			fail( "expected ParseException; instead got " + m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
	}
	
	public void testBadInsertMarksParse()
	{
		try
		{
			Mark m = CodeML.parseMark( "/*{&&}*/");
			fail( "expected ParseException (&& Invalid Java identifier); instead got " + m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
		
		try
		{
			Mark m = CodeML.parseMark( "/*{int}*/");
			fail( "expected ParseException (int RESERVED WORD Invalid Java identifier); instead got " + m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
		try
		{
			Mark m = CodeML.parseMark( "/*{insertMark*/text inside/}*/");
			fail( "expected ParseException; instead got "+ m );
		}
		catch( MarkupException cme )
		{
			//expected
		}	
	}
	
	public void testBadDeleteMarksParse()
	{
		try
		{
			Mark m = CodeML.parseMark( "/*{*/deleteMark text inside/}*/");
			fail( "expected ParseException; instead got " + m );
		}
		catch( MarkupException cme)
		{
			//expected
		}
		
		try
		{
			Mark m = CodeML.parseMark( "/*{*/deleteMark }*/text inside/*}*/");
			fail( "expected ParseException; (there is a bad terminator }*/ inside the mark instead got "+ m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
		
		try
		{
			Mark m = CodeML.parseMark( "/*{*/deleteMark /*{text between/*}*/");
			fail( "expected ParseException; (there is an instead got " + m );
		}
		catch( MarkupException cme )
		{
			//expected
		}
	}
	
	/*
	 * IM SO GLAD I DIDNT DO THIS STRUCT NONSENSE... WHAT A DISASTER
	 * 
    public void testStruct()
    {
        String STRUCT = 
        "{{struct field:" + N
        +"    {+fieldName} the name of the field (i.e. \"A\", \"B\", \"C\") (starts with uppercase)" + N
        +"    {+fieldIndex} the index of the field in the frame (from 0 to 31) (i.e. {0, 3})" + N
        +"    {+formType} the Java type of the form of the field (i.e. {int, char, String, Flavor})" + N
        +"    {+formWrapperType} the Object wrapper type of the form (i.e. {Integer, Character, String, Flavor})" + N
        +"    {+paramName} the name used when we pass this fields form as a parameter {i.e. \"a\", \"b\", ...} (starts with lowercase)" + N
        +"    {+binParamName} the name used for this field as a bin parameter{i.e. \"aBin\", \"bBin\"}" + N
        +"}}";
        
        Mark m = MarkParser.parse( STRUCT, 0 );
        
        assertTrue( m instanceof Struct );
        
        Struct struct = (Struct)m;
        assertTrue( struct.getText().equals( STRUCT ) );
        
        String[] fieldNames = struct.getFieldNames();
        assertTrue( fieldNames.length == 6 );
        
        assertTrue( fieldNames[ 0 ].equals( "fieldName" ) );
        assertTrue( fieldNames[ 1 ].equals( "fieldIndex" ) );
        assertTrue( fieldNames[ 2 ].equals( "formType" ) );
        assertTrue( fieldNames[ 3 ].equals( "formWrapperType" ) );
        assertTrue( fieldNames[ 4 ].equals( "paramName" ) );
        assertTrue( fieldNames[ 5 ].equals( "binParamName" ) );
    }
    */
}
