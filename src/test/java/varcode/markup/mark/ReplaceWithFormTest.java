package varcode.markup.mark;

import java.util.HashSet;

import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.doc.form.Form;
import varcode.doc.form.VarForm;
import varcode.markup.codeml.CodeMLParser;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.ReplaceWithForm;
import junit.framework.TestCase;

public class ReplaceWithFormTest
    extends TestCase
{
    public static final String N = System.lineSeparator();
    
    public void testAnon()
    { 
        String mark = 
            "/*{{+:IntFieldBox {+Fields+}, */IntFieldBox A, IntFieldBox B/*+}}*/";
        ReplaceWithForm anon = 
            new ReplaceWithForm( 
                mark, 
                1, //int lineNumber,
                "_", //String name,
                ForMLCompiler.INSTANCE.compile( 
                    "IntFieldBox {+Fields+}, " ), //CodeForm form,
                true, //required
                "/IntFieldBox A, IntFieldBox B" ); //String wrappedContent 
                    
        VarContext varContext = VarContext.of();
        //StringBuilder out = new StringBuilder();
            
        varContext.set( 
            "Fields", new String[]{ "A", "B", "C" }, VarScope.INSTANCE );
        //anon.fillTo( varContext, out ) ;    
        //System.out.println(  anon.derive( varContext ) );
        assertEquals( "IntFieldBox A, IntFieldBox B, IntFieldBox C", 
                       anon.derive( varContext ) );
        //System.out.println( out.toString() );
         
    }
    
   
    
    
//    public void testFieldIndent()
//    {
//        String theForm = 
//           "    //{+description}" + N
//         + "    private Object {+name};" + N + N;
//         
//        CodeForm form = BaseFormCompiler.INSTANCE.fromString( theForm );
//        
//        String formEval = form.derive(
//            Pairs.of( 
//                "description", "the Description", 
//                "name", "field" ) );
//        //System.out.println( formEval );
//        
//        String theMark = 
//            "/*{{+fields:" + N        
//          + "    //{+description}" + N
//          + "    private Object {+name};" + N 
//          + N
//          + "    */" + N                
//          + "    //the description of field" + N
//          + "    private Object field;" + N    
//          + "    /*}}*/";
//        
//        ReplaceWithForm mark = 
//              TagMark.ReplaceWithFormMark.of( 
//                  theMark, 
//                  -1,
//                  BaseFormCompiler.INSTANCE,
//                  NameAudit.IDENTIFIER );
//        String markEval = (String)mark.prepare( 
//            Pairs.of( 
//                "description", "the Description", 
//                "name", "field" ) );
//        
//        System.out.println("MARKEVAL "+ markEval + "\"" );
//        System.out.println("FORMEVAL "+ formEval + "\"" );
//        
//        assertTrue( markEval.equals( formEval ) );
//        
//        String markEvalAll = mark.getForm().derive(  
//            Pairs.of( 
//                "description", new String[]{ "a1 Description", "a2Description"}, 
//                "name", new String[]{ "a1", "a2" } )
//             );
//        System.out.println( markEvalAll );
//    }
    
    public void testRequired()
    {
        String THEMARK = 
        "/*{{+declareFields*:" + N
        + "        // {+description+}" + N  
        + "        public Object {+name+};" + N + N
        + "*/" + N
        + "..." 
        + "/*+}}*/";
        
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of(
            VarContext.of(  ),
            THEMARK, 
            0,
            ForMLCompiler.INSTANCE );
        
        assertTrue( rf.isRequired() );
        assertTrue( rf.getVarName().equals( "declareFields" ) );
        
    }
    public void testRepeating()
    {
        String DECLARE_FIELD_REPLACE_FORM = 
            "/*{{+declareFields:" + N
          + "        // {+description+}" + N  
          + "        public Object {+name+};" + N  + N
          + "*/" + N
          + "..." 
          + "/*+}}*/";
        
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of(
            VarContext.of(  ),
            DECLARE_FIELD_REPLACE_FORM, 
            0,
            ForMLCompiler.INSTANCE );
       
        //System.out.println( rf.getForm() );
       
        Form f = ForMLCompiler.INSTANCE.compile( 
            "        // {+description+}" + N  
          + "        public Object {+name+};" + N + N );
       
       String s = rf.getForm().derive( 
           VarContext.of( 
               "description", "theDescription", 
               "name", "a" ) );
       
       //System.out.println( "\""+ s +"\"" );
       
       assertEquals(  
           "        // theDescription" + N
          +"        public Object a;" + N +N, 
          s );
       
       //System.out.println( s );
       
       s = f.derive( 
           VarContext.of( 
                   "name", "a", 
                   "description", "describe a" ) 
               ); 
       
       
       f.derive( 
           VarContext.of( 
               "name", new String[]{"a","b","c"}, 
               "description", new String[]{"describe a","describe b", "describe c"} )           
            );
       
       //System.out.println( allDeclarations );
       
       f = ForMLCompiler.INSTANCE.compile( 
           "        /** {+description+} */" + N  
         + "        public Object {+name+};" + N );
      
       s = f.derive( 
           VarContext.of( 
              "description", "theDescription", 
              "name", "a" ) );
      
      // System.out.println( s );
       assertTrue( s.equals( 
           "        /** theDescription */" + N
          +"        public Object a;" + N ) );
       
       f.derive( 
           VarContext.of( 
               "name", new String[]{"a","b","c"}, 
               "description", new String[]{"describe a","describe b", "describe c"} )           
       );
       //System.out.println( allDeclarations );       
    }
    
    public void testComma()
    {        
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of(
            VarContext.of(  ),
            "/*{{+name:" + N 
            + "      {+type+} {+name+}, */" + N
            + "      int a, int b" + N
            + "/*+}}*/",
            0, 
            ForMLCompiler.INSTANCE );
        //System.out.println( rf );
        
        //System.out.println( "NAME:   \""+  rf.getName() + "\"" );
        //System.out.println( "CONTENT:   \""+  rf.getContent() + "\"" );
        //System.out.println( "FORM:   \""+  rf.getForm() + "\"" );
        
        //CodeMark cm = CodeMark.Load.fromString( rf.getForm() );
        Form form = rf.getForm();
        
        
        //String[] names = form.getAllVarNames( VarContext.of() ).toArray( new String[ 0 ] );
        
        HashSet<String> varNames = new HashSet<String>();
		form.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 2, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		assertTrue( varNames.contains( "type" ) );
		
        //assertTrue( names.length == 2 );
        
        String tailored = //we can fill the blanks by a "bind" by key value 
            form.derive( VarContext.of(  
                "type", int.class,
                "name", "a" ) );
        
        //System.out.println("TAILORED \""+ tailored +"\"");
        assertTrue( tailored.equals( "      int a" ) );
        
        /*
        tailored = //we can fill the blanks by a "bind" by key value 
            form.derive(
                Pairs.of( 
                    "type", int.class,
                    "name", "a" ) 
                );
        
        assertTrue( tailored.equals( "      int a" ) );
        
        tailored = form.derive( 
            Pairs.of( 
                "type", new Class[]{int.class, int.class},
                "name", new String[]{"a", "b"} ) );
        System.out.println( "\""+ tailored + "\"" );
        //assertTrue( tailored.equals( "      int a,  int b" ) );
        //System.out.println( tailored );
        
        tailored = form.derive(
            Pairs.of( 
                "type", new Class[]{int.class, int.class, int.class},
                "name", new String[]{"a", "b", "c"} ) );
            
        assertTrue( tailored.equals( "      int a,  int b,  int c" ) );
        //System.out.println( tailored );    
              
         */
    }
    
    public void testFormLineFeed()
    {
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of(
            VarContext.of(  ),
            "/*{{+name:{+type+} {+name+} = {+initialValue+};*/" + N
          + "        int a = 4;" + N
          + "        int b = 5;" + N
          + "/*+}}*/",
           0, 
           ForMLCompiler.INSTANCE );
        
        Form form = rf.getForm();
        //String[] names = form.getAllVarNames( VarContext.of() ).toArray( new String[ 0 ] );
        
        HashSet<String> varNames = new HashSet<String>();
		form.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 3, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		assertTrue( varNames.contains( "type" ) );
		assertTrue( varNames.contains( "initialValue" ) );
		
        //assertTrue( names.length == 3 );
        
        String tailored = //we can fill the blanks by a "bind" by key value 
            form.derive( VarContext.of( 
                "type", int.class,
                "name", "param",
                "initialValue", 12345 ) );
        
     
        //System.out.println("\""+ tailored +"\"");        
        assertTrue( tailored.equals( "int param = 12345;" ) );        
    }
    
    public void testFormArray()
    {
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of( 
            VarContext.of(  ),
            "/*{{+name:{+type+} {+name+}, */" 
          + "int a, int b" + N
          + "/*+}}*/",
           0,
           ForMLCompiler.INSTANCE );
        
        VarForm form = (VarForm) rf.getForm();
        //Set<String> names = form.getAllVarNames( VarContext.of() );
        
        HashSet<String> varNames = new HashSet<String>();
		form.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 2, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		assertTrue( varNames.contains( "type" ) );
		
        
        String tailored = //we can fill the blanks by a "bind" by key value 
            form.derive(  
                VarContext.of( 
                    "type", new Class[]{ int.class, int.class}, 
                    "name", new String[]{"a", "b"} ) );
                    
        //System.out.println("TAILORED \"" + tailored +"\"" );
                    
        assertEquals( "int a, int b", tailored );
    }
    
    public void testFormCommaSameLine()
    {
        ReplaceWithForm rf = CodeMLParser.ReplaceWithFormMark.of( 
            VarContext.of(  ),
            "/*{{+name:{+type+} {+name+}, */" 
          + "int a, int b" + N
          + "/*+}}*/",
           0,
           ForMLCompiler.INSTANCE );
        
        VarForm form = (VarForm) rf.getForm();
        //String[] names = form.getAllVarNames( VarContext.of() ).toArray( new String[0] );
        
        HashSet<String> varNames = new HashSet<String>();
		form.collectVarNames( varNames, VarContext.of( ) );
		assertEquals( 2, varNames.size() );
		assertTrue( varNames.contains( "name" ) );
		assertTrue( varNames.contains( "type" ) );
		//assertTrue( varNames.contains( "initialValue" ) );
		
        //assertTrue( names.length == 2 );
        
        String tailored = //we can fill the blanks by a "bind" by key value 
            form.derive( VarContext.of(  
                "type", int.class,
                "name", "a" ) );
        
     
        //System.out.println("\""+ tailored +"\"");
        assertTrue( tailored.equals( "int a" ) );     
        
        tailored = //we can fill the blanks by a "bind" by key value 
            form.derive(  
                VarContext.of( "type", int.class, "name", "a" )
            );
        assertTrue( tailored.equals( "int a" ) );
        
        tailored = //we can fill the blanks by a "bind" by key value 
            form.derive(  
                VarContext.of( 
                    "type", new Class[]{ int.class, int.class}, 
                    "name", new String[]{"a", "b"} ) );
        
        //System.out.println("TAILORED \"" + tailored +"\"" );
        
        assertTrue( tailored.equals( "int a, int b" ) );
        
    }
}
