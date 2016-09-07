package varcode.markup.forml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.form.Form;
import varcode.form.SeriesFormatter;
import varcode.form.VarForm;
import varcode.form.SeriesFormatter.BetweenTwo;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddVar;
import varcode.markup.mark.Mark;
import junit.framework.TestCase;

public class FormTest
    extends TestCase
{
    
 public static final String N = "\r\n";
    
    public static final String FIELD_FORM = 
       "public String {+field+};" + N;
    
    public static final String themark = 
        "/*{{+fields:" + N
      + FIELD_FORM
      + "*/" + N    
      + "/** description of aField */" +N 
      + "public String aField;" + N    
      + "/*+}}*/";
    
    
    public void testFormWithScript()
    {
    	VarForm theForm = 
            (VarForm)ForMLCompiler.INSTANCE.compile( "{+$^(name)+}" );
    	
    	
    	//System.out.println( theForm.formDom );    	
    	//System.out.println( theForm.formDom.getAllMarks()[0].getClass() );
    	//System.out.println( "NAME" + theForm.getName() );
    	
    	AddScriptResult asr = (AddScriptResult) theForm.formTemplate.getMarks()[0];
    	assertEquals( "Eric", asr.derive( VarContext.of( "name", "eric" ) ) );
    	
    	asr.getScriptName();
    	
    	//Set<String> names = theForm.getAllVarNames( VarContext.of("name", "eric") );
    	//assertTrue(names.contains("name"));
    	VarContext vc = VarContext.of( "name", "eric" );
    	assertEquals( "eric", vc.resolveVar( "name") );
    	assertEquals( "Eric", asr.derive( vc ) );
    }
    
    public void testMark()
    {
        Form field = 
            ForMLCompiler.INSTANCE.compile( FIELD_FORM );
                        
        /*
        assertTrue( ( "public String A;" + N )
            .equals( field.bindEval( "field", "A" ) ) );
        */
        assertTrue( ( "public String A;" + N )
            .equals( field.derive( VarContext.of( "field", "A" ) ) ) );
        
        //assertEquals( "public String A;" + N,
        //    field.evalCount( VarContext.of( "field", "A" ), 1 ) );
        
        assertEquals( 
            ( "public String A;" + N
             + "public String B;" + N ), 
                 field.derive(  
                     VarContext.of( 
                         "field", 
                         new String[] {"A", "B"} ) ) );
        
        
    }    
    
    public void testTwoArraysOneElement()
    {
    	String form = "{+field*+} {+value*+};";
    	VarForm f = (VarForm) ForMLCompiler.INSTANCE.compile( form );
    	
    	String d = f.derive( VarContext.of( 
    			"field", new String[]{"FIELD"},
    			//"value", "VALUE" ) );
    			"value", new String[]{"VALUE"}  ) );
    	
    	assertEquals( "FIELD VALUE;", d );

    	ArrayList<String>field = new ArrayList<String>();
    	ArrayList<String>value = new ArrayList<String>();
    	field.add("FIELD");
    	value.add("VALUE");
    	d = f.derive( VarContext.of( 
    			"field", field,
    			//"value", "VALUE" ) );
    			"value", value  ) );
    	
    	assertEquals( "FIELD VALUE;", d );
    	
    }
    
    public void testMismatchedCardinality()
    {
    	 String form = "{+field*+} {+value*+};";
    	 VarForm f = (VarForm)ForMLCompiler.INSTANCE.compile( form );
    	    	
    	 try
    	 {
    		 f.derive( VarContext.of( 
    		    "field", new String[]{"FIELD"},
    			"value", new String[]{"1", "2"}  ) );
    		 fail( "Expected Exception for mismatched cardinality" );
    	 }
    	 catch( VarException e )
    	 {
    		 //expected
    	 }    	 
    }
    
    public void testManyAndNoneCardinality()
    {
    	
    	String form = "{+field*+} {+value*+};";
   	 	VarForm f = (VarForm)ForMLCompiler.INSTANCE.compile( form );
   	    	
   	 	try
   	 	{
   	 		f.derive( VarContext.of(    	 			
   	 			"value", new String[]{"1", "2"}  ) );
   	 		fail( "Expected Exception for mismatched cardinality" );
   	 	}
   	 	catch( VarException e )
   	 	{
   	 		//expected
   	 	}  
    }
    // patterns CANNOT be static (must have a dynanmic element) 
    /*
    public void testStaticPattern()
    {
        String s = "statictext";
        String text = "some static text";
        CodeForm p = BaseFormParser.INSTANCE.fromString( s );
        //Form p = Form.of( s,  text);
        
        
        assertEquals( p.getName(), s );        
        assertEquals( p.getText(), text );        
        
        
        assertTrue( p.getText().equals( text ) );

    }
    */
    
    
    public void testLineFeeds()
    {
        String form = 
            "import {+logger+};" + "\r\n"
          + "import {+loggerFactory+};" + "\r\n";
        
        Form f = ForMLCompiler.INSTANCE.compile( form );
        
        assertTrue( f instanceof VarForm );
        VarForm fp = (VarForm)f;
        
        assertTrue( fp.getText().equals( form ) );
        //FormSeries fs = fp.getFormSeries();
        //System.out.println( "THE FORM SERIES "+ fs );
        
        String res = fp.derive( 
            VarContext.of( 
                "logger", "org.slf4j.Logger",
                "loggerFactory", "org.slf4j.LoggerFactory"
            ) );
        //System.out.println("   RES \""+ res  +"\"");
        assertTrue( 
            res.equals( 
            "import org.slf4j.Logger;" + "\r\n"
         +  "import org.slf4j.LoggerFactory;" + "\r\n" ) );
    }
    
    public void testFormMissingRequired()
    {
        Form f = ForMLCompiler.INSTANCE.compile( 
            "formatWithList", "{+name*+}, " );
        
        try
        {
            f.derive( VarContext.of() );
            fail("expected Exception for missing required Var ");
        }
        catch( VarException e )
        {
            //exopected
        }
        
    }
    public void testList()
    {
        Form f = ForMLCompiler.INSTANCE.compile( "formatWithList", "{+name+}, " );
        
        
        assertTrue( f.getText().equals( "{+name+}, " ) );
        
        Mark[] params = f.getAllMarks();
        assertTrue( params.length == 1 );
        assertTrue( params[ 0 ] instanceof AddVar );
        AddVar av = (AddVar)params[ 0 ];
        assertTrue( av.getVarName().equals( "name" ) );
        //assertTrue( av.isList() );
        assertTrue( !av.isRequired() );
        

		HashSet<String> varNames = new HashSet<String>();
		av.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		
        //String[] names = av.getAllVarNames( VarContext.of() ).toArray( new String[ 0 ] );        
        //assertTrue( names.length == 1 );
        //assertTrue( names[ 0 ].equals( "name" ) );
        
        String res = f.derive( VarContext.of( "name", "Eric" ) );
        //System.out.println(  res  );
        assertTrue( res.equals( "Eric" ) );
        
       
    }    
        
    public void testEvalCount()
    {
        List<String> l = new ArrayList<String>();
        l.add( "a" );
        l.add( "b" );
        l.add( "c" );
        
        VarContext p = VarContext.of( "name",  l );

        VarForm f = (VarForm)ForMLCompiler.INSTANCE.compile( 
               "formatWithList", 
               "{+name+}, " );
        
        assertTrue( f.getText().equals( "{+name+}, " ) );
        
        SeriesFormatter fs = f.seriesFormatter;
        assertTrue( fs instanceof BetweenTwo );
        BetweenTwo b = (BetweenTwo) fs;
        assertTrue( b.getText().equals( ", " ) );
        //
        
        //System.out.println("FILL AB C" + f.fill( "a", "b", "c" ) ); 
        
        
        String res = f.derive( p );
        //System.out.println( res );
        assertTrue( res.equals( "a, b, c" ) );
        
        p = VarContext.of( "name", new String[] {"a", "b", "c"} );
        
        res = f.derive( p );
        assertTrue( res.equals( "a, b, c" ) );        
    }
    
    public void testRequiredList() 
    {    
        //Required List
        Form f = ForMLCompiler.INSTANCE.compile( 
            "formatWithRequiredList", "{+name*+}" );
        
        assertTrue( f.getText().equals( "{+name*+}" ) );
        
        Mark[] params = f.getAllMarks();
        assertTrue( params.length == 1 );
        AddVar av = (AddVar)params[ 0 ];
        assertTrue( av.getVarName().equals( "name" ) );
        //assertTrue( av.isList() );
        assertTrue( av.isRequired() );
        
        //String[] names = f.getAllVarNames().toArray( new String[ 0 ] );
        //Set<String>names = f.getAllVarNames( VarContext.of() );
        

		HashSet<String> varNames = new HashSet<String>();
		av.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		
        //assertTrue( names.size() == 2 );
        
        //assertTrue( names.contains( "formatWithRequiredList" ) );
        //assertTrue( names.contains( "name" ) );
        
        //assertTrue( )
    }
    
    /*
    public void testA()
    {
        Form p = BaseFormCompiler.INSTANCE.fromString( "name", "{+a}" );
        String res = p.derive( VarContext.of( "a", "1"  ) );
        //System.out.println("RES :" + res );
        assertTrue( res.equals( "1" ) );
        assertEquals( p.fill( "2" ), "2" );
    }
    */
    public void testDynamicPattern()
    {
        VarForm p = (VarForm)ForMLCompiler.INSTANCE.compile( "name", "{+dynamicText+}" );
        //assertEquals( p.getName(), "name" );        
        assertEquals( p.getText(), "{+dynamicText+}" );
        

		HashSet<String> varNames = new HashSet<String>();
		p.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 1, varNames.size() );
		assertTrue( varNames.contains( "dynamicText" ) );
		
        //assertTrue( p.getAllVarNames( VarContext.of() ).size() == 2 );
        //assertTrue( varNames.contains( "name" ) );
        assertTrue( varNames.contains( "dynamicText" ) );
        
        assertTrue( p.derive( VarContext.of(  ) ).equals( "" ) );
        String res = p.derive( VarContext.of( "dynamicText", "a"  ) );
        //System.out.println( res );
        assertEquals( "a", res );
        //assertEquals( p.derive( "A" ), "A" );
        
        /*
        //try firstcap 
        
        
        //test ALLCAPS
       
        */
        res = p.derive( VarContext.of(  "dynamicText", "ab"  ) );
        //System.out.println( "RES IS" + res );
        assertTrue("bindAll should be the same as bind for one element", 
                   res.equals( p.derive( VarContext.of( "dynamicText", "ab" ) ) ) );        
    }
    
    /*
    public void testFirstCaps()
    {
        Form p = Form.of( "name", "{+^dynamicText}" );
        assertTrue( p.eval( VarContext.of( "dynamicText", "ab"  ) ).equals( "Ab" ) );
    }
    */
    
    
    public void testStaticMixedWithDynamicReduceToBlanks()
    {
        Form p = ForMLCompiler.INSTANCE.compile( "name", "{+a+} a;" );
        //
        assertTrue( "if ALL fills are a null, pattern resolves empty",
            "".equals(  p.derive( VarContext.of( ) ) ) );
    }
    
    public void testNewLinesNoBetween()
    {
        Form p = ForMLCompiler.INSTANCE.compile( "name", 
             "    {+a+}" + "\r\n" + ";" );
        assertTrue(
            "null bindings evaluate to ",
            "".equals(  p.derive( VarContext.of( ) ) ) );
        
       // System.out.println( p.bind( "a", "Eric" ) );
        /*
        assertTrue( 
            ("    Eric" + System.lineSeparator() + ";" )
            .equals(  p.bindEval( "a", "Eric" ) ) );
            */
    }

    
    /**
     * When binding a single pattern
     */
    public void testBindOnlyWhatYouGot()
    {
        //If the variables for a pattern are 
        Form p = ForMLCompiler.INSTANCE.compile( "name", 
            "{+a+} {+b+};" );
        assertEquals( "", p.derive( VarContext.of(  ) ) );
        
        String b = p.derive( VarContext.of( "a", "1" ) );
        //System.out.println( b );
        
        assertEquals( "if ANY of the fills is non-null, present it", 
            "1 ;", b );
        
        b = p.derive( VarContext.of( "b", "2" ) );
        //System.out.println( b );
        
        assertEquals( "if ANY of the fills is non-null, present it", 
            " 2;", b );
        
    }
    
    /**
     * Test Series with Between token
     */
    public void testBetween()
    {    
        Form p = ForMLCompiler.INSTANCE.compile( "name", "{+dynamicText+}," );
        assertTrue( "".equals(  p.derive( VarContext.of( ) ) ) );
        
        assertTrue(
            "verify that when binding ONE, the bewteen separator is not present", 
            "plugin".equals( p.derive( VarContext.of( "dynamicText", "plugin" ) ) ) );
        
        /*
        String two = p.evalAll( 
            Pairs.eachOf( 1, 
                "dynamicText", "A", 
                "dynamicText", "B" ) );
        
        assertEquals( "A,B", two );
        
        String three = p.evalAll( 
            Pairs.eachOf( 1, 
                "dynamicText", "A", 
                "dynamicText", "B",
                "dynamicText", "C" ) );
        
        assertEquals( "A,B,C", three );
        */        
    }
    
    /*
    public void testBetweenSpacesTabs()
    {
        
        Form p = Form.of( "name", "    {+a}, " );
        String prefixSpaces = p.bindEval( "a", "Eric" );
        assertTrue( "prefix spaces before the first element",  
            prefixSpaces.equals( "    Eric" ) );
        
        prefixSpaces = p.evalAll( 
            Pairs.of( "a", "1" ), 
            Pairs.of( "a", "2" ) );
        
        //System.out.println( "\"" + prefixSpaces + "\"" );
        assertTrue( "(2) prefix spaces before the first element",  
            prefixSpaces.equals( "    1,  2" ) );
        
        prefixSpaces = p.evalAll( 
            Pairs.of( "a", "1" ), 
            Pairs.of( "a", "2" ), 
            Pairs.of( "a", "3" ) );

        assertTrue( "prefix spaces before the first element ONLY (not 2)",  
            prefixSpaces.equals( "    1,  2,  3" ) );
        
        //NOT a between
        p = Form.of( "name", "    {+a};" );
        prefixSpaces = p.evalAll( 
            Pairs.of( "a", "1" ), 
            Pairs.of( "a", "2" ), 
            Pairs.of( "a", "3" ) );
        //System.out.println( "\"" + prefixSpaces + "\"" );
        assertTrue( 
            prefixSpaces.equals("    1;    2;    3;" ) );
                    //"    1;" + System.lineSeparator() + 
                    //"    2;" + System.lineSeparator() +
                    //"    3;" + System.lineSeparator() ) );
        //System.out.println( "\""+prefixSpaces + "\"" );
        
        p = Form.of( "name", "    {+a};" + System.lineSeparator() );
        prefixSpaces = p.evalAll( 
            Pairs.of( "a", "1" ), 
            Pairs.of( "a", "2" ), 
            Pairs.of( "a", "3" ) );
        
        //System.out.println( "\""+prefixSpaces + "\"" );
        
        assertTrue( prefixSpaces.equals(
            "    1;" + System.lineSeparator() 
          + "    2;" + System.lineSeparator()
          + "    3;" + System.lineSeparator() ) );
        //System.out.println( "\""+prefixSpaces + "\"" );
        
    }
    */
    
}
