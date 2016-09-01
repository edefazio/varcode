package varcode.markup.bindml;

import java.util.HashMap;
import java.util.Map;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.eval.Eval_JavaScript;
import varcode.form.Form;
import varcode.markup.MarkupException;
import varcode.markup.MarkupParser;
import varcode.markup.VarNameAudit;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfExpression;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddScriptResultIfExpression;
import varcode.markup.mark.AddTextIfVar;
import varcode.markup.mark.AddVar;
import varcode.markup.mark.AddVarIsExpression;
import varcode.markup.mark.AddVarOneOf;
import varcode.markup.mark.Cut;
import varcode.markup.mark.DefineVar;
import varcode.markup.mark.DefineVarAsExpressionResult;
import varcode.markup.mark.DefineVarAsForm;
import varcode.markup.mark.DefineVarAsScriptResult;
import varcode.markup.mark.EvalExpression;
import varcode.markup.mark.Mark;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.SetMetadata;
import varcode.markup.mark.DocDirective;

/**
 * Associates Tags Marks within varcode source and "realizes" (creates) 
 * the appropriate {@code MarkAction}s     
 *  
 * NOTE: This is the standard usage for 
 * Javadoc/C, C++ "style" Marks.
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class BindMLParser
    implements MarkupParser
{   
    public static final ForMLCompiler FORM_COMPILER = 
    	ForMLCompiler.INSTANCE;
    
    public static final BindMLParser INSTANCE = 
       new BindMLParser( FORM_COMPILER );
    
    public static final String N = System.lineSeparator();
       
    private final ForMLCompiler forMLCompiler;
    
    
    public BindMLParser( ForMLCompiler forMLCompiler )
    { 
    	this.forMLCompiler = forMLCompiler;
    }
    
    
    public String getFirstOpenTag( String line )
    {
        return firstOpenTag( line );
    }

    
    public String closeTagFor( String openTag )
        throws MarkupException
    {
        return matchCloseTag( openTag );
    }

    public Mark of( String markText )
        throws MarkupException
    {
        return parseMark( markText ); 
    }

    public Mark parseMark(
        VarContext context,
        String markText, 
        int lineNumber )
        throws MarkupException
    {
        return parseMark( 
            context, 
            markText, 
            lineNumber, 
            forMLCompiler ); 
    }
    
    //"{+",   "+}"  //Add               
    //"{+$",  "+}"  //AddScriptResult, ReplaceWithScript
    //"{+?",  "+}"  //Add --> AddIf
    //"{+?(( expression )):$script(params)+}
    
    
    // {# 
    //"{#",    "#}" //DefineVar, DefineVarAsScriptResult (Instance)
    //"{##",  "##}" //DefineVar, DefineVarAsScriptResult  (Static)
    //"{#$",  ")#}" //TailorDirective
    
    //"{-",    "-}"   //CutMark
    
    //"{@",    "@}"   //SetMetadata
    
    // {{    
    //"{{+",  "+}}"  //ReplaceWithForm, AddForm(TBD)
    //"{{+?", "+}}"  //AddFormIf
    //"{{#",   "#}}" //DefineForm (Instance)
    //"{{##", "##}}" //DefineForm (Static)
    
    
    // {_
    //"{_+",  "+_}" //ReplaceWithForm, AddForm(TBD)
    //"{_+?", "+_}" //AddFormIf    
    //"{_#",  "#_}"  //DefineForm (Instance)
    //"{_##","##_}"  //DefineForm (Static)

    //"{$",   ")$}"  //RunScriptMark
       
    public static String firstOpenTag( String line )
    {
        int openBraceIndex = line.indexOf( '{' );
        
                                      //the open brace cant be the last char on the line
        while( openBraceIndex >= 0 && openBraceIndex < line.length() -1 )
        {        
            //if the NEXT character AFTER the '{' is a '+', '#', '-', '@' or '{'        
            switch( line.charAt( openBraceIndex + 1 ) )
            {
                case '+' : // "{+", "{+?" "{+$" "{+("
                    return "{+";
                    
                case '#' : //{#
                	       //   {#
                           //   {##
                	       //   {#$                                              
                    if( charIs( line, openBraceIndex + 2, '#' ) )
                    {
                        return "{##";                  
                    }
                    //if( charIs( line, openBraceIndex + 2, '$' ) )
                    //{
                    //    return "{#$";                  
                    //}
                    return "{#";
                    
                case '-' :  //{- 
                    return "{-";
                    
                case '@' :
                    return "{@";
                    
                case '$' : //"/*{$"
                	if( charIs( line, openBraceIndex + 2, '$' ) )
                	{
                		return "{$$"; //tailorDirective
                	}
                    return "{$"; //RunScript                   
                case '{' : // "{{"
                           // "{{+"  
                	       // "{{+?"
                           // "{{#"
                           // "{{##"
                    if( charIs( line, openBraceIndex + 2, '#' ) )
                    {
                    	if( charIs( line, openBraceIndex + 3, '#' ) )
                    	{
                    		return "{{##";
                    	}
                    	return "{{#";
                    }
                    if( charIs( line, openBraceIndex + 2, '+' ) )
                    {
                    	return "{{+";
                    }                    
                    break;                    
                case '_' : // "{_"
                           // "{_+"  
         	               // "{_+?"
                           // "{_#"
                           // "{_##"
                	if( charIs( line, openBraceIndex + 2, '#' ) )
                    {
                    	if( charIs( line, openBraceIndex + 3, '#' ) )
                    	{
                    		return "{_##";
                    	}
                    	return "{_#";
                    }
                    if( charIs( line, openBraceIndex + 2, '+' ) )
                    {
                    	return "{_+";
                    }                    
                    break;
                case '(' : // "{(("
                	if( charIs( line, openBraceIndex + 2, '(' ) )
                	{
                		return "{((";
                	}
            	}
            openBraceIndex = line.indexOf( '{', openBraceIndex + 1 );
        }
        return null;
    }
     
    private static final Map<String, String> OPENTAG_TO_CLOSETAG = 
        new HashMap<String, String>();

    static
    {   
    	//AddFormIfExpression
        OPENTAG_TO_CLOSETAG.put(  "{+",   "+}"   ); //Add               
        OPENTAG_TO_CLOSETAG.put(  "{+$",  "+}"  ); //AddScriptResult, ReplaceWithScript
        OPENTAG_TO_CLOSETAG.put(  "{+?",  "+}"  ); //Add --> AddIf, AddScriptResultIfExpression
         // {+?(( env == test )):$capture(inputVO)+}
        OPENTAG_TO_CLOSETAG.put(  "{((",  "))}"  ); //EvaluateExpression
        
        OPENTAG_TO_CLOSETAG.put(  "{+((",  "+}"  ); //AddExpressionResult 
        OPENTAG_TO_CLOSETAG.put(  "{{+",  "+}}" ); //ReplaceWithForm, AddForm(TBD)
        OPENTAG_TO_CLOSETAG.put(  "{{+?", "+}}" ); //AddFormIf, AddFormIfExpression
        
        OPENTAG_TO_CLOSETAG.put(  "{_+",  "+_}" ); //ReplaceWithForm, AddForm(TBD)
        OPENTAG_TO_CLOSETAG.put(  "{_+?", "+_}" ); //AddFormIf
        

        OPENTAG_TO_CLOSETAG.put(  "{#",    "#}"  ); //DefineVar, DefineVarAsScriptResult (Instance)
        
        
        OPENTAG_TO_CLOSETAG.put(  "{{#",   "#}}" ); //DefineForm (Instance)
        OPENTAG_TO_CLOSETAG.put(  "{{##", "##}}" ); //DefineForm (Static)
        
        OPENTAG_TO_CLOSETAG.put(  "{_#",  "#_}" ); //DefineForm (Instance)
        OPENTAG_TO_CLOSETAG.put(  "{_##","##_}" ); //DefineForm (Static)
        
        
        OPENTAG_TO_CLOSETAG.put(  "{##",  "##}"  ); //DefineVar, DefineVarAsScriptResult  (Static)
        
        OPENTAG_TO_CLOSETAG.put(  "{$$",  "$$}"  ); //TailorDirective
        OPENTAG_TO_CLOSETAG.put(  "{$",   ")$}" ); //RunScriptMark
        
        OPENTAG_TO_CLOSETAG.put(  "{-",    "-}"  ); //CutComment
        
        OPENTAG_TO_CLOSETAG.put(  "{@",    "@}"  ); //SetMetadata
    }
    
    public static String matchCloseTag( String openTag )
    {
    	
        String matchingCloseTag = 
        	OPENTAG_TO_CLOSETAG.get( openTag );
        if( matchingCloseTag == null )
        {
        	throw new MarkupException ("No Matching close tag for Open tag \""+openTag+"\"" );
        }
        return matchingCloseTag;
    }

    
    public static final boolean charIs( String string, int index, char expect )
    {
        return  string != null  
            && string.length() > index 
            && index > -1
            && string.charAt( index ) == expect; 
    }
    
    
    //"{+",   "+}"  //Add               
    //"{+$",  "+}"  //AddScriptResult, ReplaceWithScript
    //"{+?",  "+}"  //Add --> AddIf

    //"{#",    "#}" //DefineVar, DefineVarAsScriptResult, DefineVarAsExpressionResult (Instance)
    //"{##",  "##}" //DefineVar, DefineVarAsScriptResult  DefineVarAsExpressionResult (Static)
    
    
    //"{-",    "-}"   //CutMark
    
    //"{@",    "@}"   //SetMetadata
    
    //"{{+:",  "+}}"  //AddForm(TBD)
    //"{{+?", "+}}"  //AddFormIf
    //"{{#",   "#}}" //DefineForm (Instance)
    //"{{##", "##}}" //DefineForm (Static)
    
    
    //"{$$",  ")$$}" //TailorDirective
    
    //"{_+:",  "+_}" //AddForm(TBD)
    //"{_+?", "+_}" //AddFormIf    
    //"{_#",  "#_}"  //DefineForm (Instance)
    //"{_##","##_}"  //DefineForm (Static)
    
    public static Mark parseMark( String text )
    {
        return parseMark( 
            VarContext.of( ),
            text, 
            -1, 
            ForMLCompiler.INSTANCE );
    }
    
    /** 
     * Given Text return the appropriate Mark
     * 
     * NOTE: I could make this more efficient, but it's fine 
     * 
     * @param markText the text of the entire mark
     * @param the line number where the mark appears
     * @param nameAudit audits the name of the Mark for validity
     * @throws CodemarkException if the Mark is invalid
     */
    public static Mark parseMark(
        VarContext parseContext,
        String text, 
        int lineNumber,
        ForMLCompiler forMLCompiler )
        throws MarkupException
    {
        if( text.startsWith( "{" ) )
        {
            // "{+",     "+}"  ); //AddVar         
            // "{+$",    "+}"  ); //AddScriptResult
            // "{+?",    "+}"  ); //AddIfVar, AddScriptResultIfExpression
            // "{+(("    "))+}"); //AddExpressionResult
        	// "{{+",    "+}}" ); //AddForm
            // "{{+?",   "+}}" ); //AddFormIf
        	
        	
        	// "{_+",    "+_}" ); //AddFormAlt
            // "{_+?",   "+_}" ); //AddFormIf
        	
            // "{-",      "}"  ); //Cut
            // "{-?(",   "-}"  ); //CutIf
            // "{#",     "#}*/"  ); //DefineInstanceVar, DefineVarAsScriptResult
        	// "{$$",   ")$$}*/" ); //TailorDirective
            // "{{#",    "#}}*/" ); //DefineForm (Instance)        	
        	// "{_#",    "#_}" ); //DefineForm (Instance)
            
            //---------------------------------------
            // "{+",     "+}"  ); //AddVar         
            // "{+$",    "+}"  ); //AddScriptResult
            // "{+?",    "+}"  ); //AddIfVar
            if( charIs( text, 1, '+' ) )
            {
                if( charIs( text, 2, '$' ) )
                {   // "{+$",    "+}"  ); //AddScriptResult
                    return AddScriptResultMark.of( text, lineNumber );
                }
                if( charIs( text, 2, '?' ) )
                {   //"{+?",    "+}"  //AddIf
                	
                	if( charIs( text, 3, '(' ) && charIs(text, 4, '(' ) )
                	{
                		//"{+?((   
                		return AddScriptResultIfExpressionMark.of( text, lineNumber );
                	}
                    return AddIfVarMark.of( text, lineNumber );
                }                
                if( charIs( text, 2, '(' ) )
                {   //"{+(("    "+))" //addExpression 
                	return AddExpressionResultInlineMark.of( text, lineNumber );
                }
                // "{+odd[1,3,5,7,9]+}"
                if( text.contains( ":[" ) )
                {
                	return AddVarOneOfMark.of( parseContext, text, lineNumber );
                }
                //return AddVarMark.of( text, lineNumber );
                return AddVarExpressionMark.of( text, lineNumber );
            }
            
            //----------------------------
            // "{{+",     "+}}" ); //AddForm
            // "{{+?",    "+}}" ); //AddFormIf
            if( text.substring( 1, 3 ).equals( "{+" ) )
            {
                if( charIs( text, 3, '?' ) )
                {
                	if( charIs(text, 4, '(' ) )
                	{
                		//"{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}";
                		return AddFormIfExpressionMark.of( 
                			parseContext , text, lineNumber );
                	}
                    return AddFormIfVarMark.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        forMLCompiler  );
                }
                return AddFormMark.of( 
                    parseContext, text, lineNumber, forMLCompiler );
            }
            
            //---------------------------- b 
            // "{_+",     "+_}" ); //AddForm
            // "{_+?",    "+_}" ); //AddFormIf
            if( text.substring( 1, 3 ).equals( "_+" ) )
            {
                if( charIs( text, 3, '?' ) )
                {
                	if( charIs( text, 4, '(' ) ) 
                	{
                		return AddFormIfExpressionMark_Alt.of(
                			parseContext, text, lineNumber );
                	}
                    return AddFormIfVarMark_Alt.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        forMLCompiler );
                }
                //Add
                return AddFormMark_Alt.of( 
                    parseContext, text, lineNumber, forMLCompiler );
            }
            
            //----------------------------
            // "{-", "-}"  ); //Cut
            if( charIs( text, 1, '-' ) )
            {
                return CutMark.of( text, lineNumber );                
            }
            
            //--------------------------------
            // "{#",     "#}"  // DefineVar (Instance)
            // "{#",    ")#}"  // DefineVarAsScriptResult
            
            // "{# ((   ))#}"  // DefineVarAsExpressionResult
            
            // "{##      ##}" //DefineVar (Static)
            // "{##      )##}" //DefineVarAsScriptResult (Static)
            if( charIs( text, 1, '#' ) )
            {
            	//"{##a:$count(blah)##}"
            	if( charIs( text, 2, '#') ) // "{##"
            	{
            		if( text.endsWith( "))##}" ) )
            		{
            			return DefineStaticVarAsExpressionResult.of( text, lineNumber );
            			//return DefineStaticVarAsExpressionResultMark.of( text, lineNumber );
            		}
            		if( text.endsWith( ")##}" ) )
            		{
            			return DefineStaticVarAsScriptResultMark.of( 
            				text, lineNumber, parseContext.getVarNameAudit() ); 
            		}
            		// "{##"..."##}";
            		return DefineStaticVarMark.of( 
            			text, lineNumber, parseContext.getVarNameAudit() );
            	}
            	if( text.endsWith( "))#}" ) ) /* {#c:((a + b))#} */
            	{
            		return DefineInstanceVarAsExpressionResult.of( 
            			text, lineNumber, parseContext.getVarNameAudit() );
            	}
                if( text.endsWith( ")#}" ) )
                {
                	/*
                	
                	*/                	
                    return DefineInstanceVarAsScriptResultMark.of( 
                        text, lineNumber, parseContext.getVarNameAudit() );
                }
                return DefineInstanceVarMark.of( 
                    text, lineNumber, parseContext.getVarNameAudit() );
            }
            
            //--------------------------------
            // "{$",     ")}"  // ScriptMark 
            if( charIs( text, 1, '$' ) )
            {
            	if( charIs( text, 2, '$' ) ) //  {$$directive()$$}  
            	{
            		return TailorDirectiveMark.of( text, lineNumber );
            	}
                return EvalScriptMark.of( text, lineNumber );                
            }
            
            //--------------------------------
            // "{{#",     "#}}" ); //DefineVarAsForm (Instance)
            // "{{##",   "##}}" ); //DefineVarAsForm (Instance)
            if( text.substring( 1, 3 ).equals( "{#" ) )
            {
            	if( charIs( text, 3, '#' ) )
            	{
            		return DefineStaticVarAsFormMark.of( 
                            parseContext,
                            text, 
                            lineNumber,  
                            forMLCompiler );
            	}
                return DefineInstanceVarAsFormMark.of( 
                    parseContext,
                    text, 
                    lineNumber,  
                    forMLCompiler );
            }
            
            //--------------------------------
            // "{_#",     "#_}" ); //DefineVarAsForm (Instance)
            // "{_##",   "##_}" ); //DefineVarAsForm (Static)
            if( text.substring( 1, 3 ).equals( "_#" ) )            	
            {
            	if( charIs( text, 3, '#' ) )
            	{
            		return DefineStaticVarAsFormMark_Alt.of( 
                            parseContext,
                            text, 
                            lineNumber,  
                            forMLCompiler );
            	}
                return DefineInstanceVarAsFormMark_Alt.of( 
                    parseContext,
                    text, 
                    lineNumber,
                    forMLCompiler );
            } 
        }
           
        // "{@",     "@}"  ); //SetMetadata
        if( charIs( text, 1, '@' ) )
        {
            return SetMetadataMark.of( text, lineNumber );
        }         
        
        // "{(("      "))}"   //EvalExpression     
        if( text.substring( 1, 3 ).equals( "((" ) )
        {
        	return EvalExpressionMark.of( text, lineNumber );
        }
       
        throw new MarkupException(
            "Could not find Open Tag matching Mark :" + N + text + N 
            +"on line [" + lineNumber + "]", text, lineNumber );
    }
    // {+name}
    // {+name|eric}
    
    // {+name+}    
    // {+name|eric+}
    
   
    
    public static class AddVarMark
    {
        public static final String OPEN_TAG = "{+";
        public static final String CLOSE_TAG = "+}";

        public static AddVar of( String text )
        {
            return of( text, -1, VarNameAudit.BASE );
        }
        
        public static AddVar of( String text, int lineNumber )
        {
            return of( text, lineNumber, VarNameAudit.BASE );
        }
        
        public static AddVar of( String text, int lineNumber, VarNameAudit nameAudit )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( "Invalid AddVar : " + N 
                    + text + N + "  ... on line ["
                    + lineNumber + "] must start with " 
                    + " \"" + OPEN_TAG + "\" ", text, lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( "Invalid AddVar : " + N 
                    + text + N + "  ... on line ["
                    + lineNumber + "] must end with CLOSE MARK" 
                    + " \"" + CLOSE_TAG + "\" ", text, lineNumber );
            }
            String tag = text.substring( 
                OPEN_TAG.length(), 
                text.indexOf( CLOSE_TAG ) );

            //System.out.println( "TAG \"" + tag + "\"" );
            String[] nameDefault = Tokenize.byChar( tag, '|' );

            String name = null;
            boolean isRequired = false;
            String defaultValue = null;

            if( nameDefault[ 0 ].endsWith( "*" ) )
            {
                name = nameDefault[ 0 ].substring( 0, nameDefault[ 0 ].length() - 1 );
                isRequired = true;
            }
            else
            {
                name = nameDefault[ 0 ];
                isRequired = false;
            }
            if( nameDefault.length == 2 )
            {   //they specified a default
                defaultValue = nameDefault[ 1 ];
            }
            else
            {
                defaultValue = null;
            }
            try
            {
                nameAudit.audit( name );                
            }            
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddVar Name \"" + name + "\", on line [" 
                  + lineNumber + "] ", text, lineNumber, e );
            }
            return new AddVar( text, lineNumber, name, isRequired, defaultValue );
        }
    }
    
    // "{+vowel['a','e','i','o','u']+}"
    // "{+vowel['a','e','i','o','u']|'e'+}"
    // "{+vowel['a','e','i','o','u']*+}"
    public static class AddVarOneOfMark
    {	
    	public static final String OPEN_TAG = "{+";
    	
    	public static final String CLOSE_TAG = "+}";
    	
    	public static AddVarOneOf of( VarContext parseContext, String text, int lineNumber )
    	{
    		 if( !text.startsWith( OPEN_TAG ) )
             {
                 throw new MarkupException( "Invalid AddVarOneOf : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must start with " 
                     + " \"" + OPEN_TAG + "\" ", text, lineNumber );
             }
             if( !text.endsWith( CLOSE_TAG ) )
             {
                 throw new MarkupException( "Invalid AddVarOneOf : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must end with CLOSE MARK" 
                     + " \"" + CLOSE_TAG + "\" ", text, lineNumber );
             }
             int openIndexOfArr = text.indexOf( ":[" );
             if( openIndexOfArr < 3 )
             {
            	 throw new MarkupException( "Invalid AddVarOneOf : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must contain ':[' to signify start of array ", 
                     text, lineNumber );
             }
             
             int closeIndexOfArr = text.indexOf( ']', openIndexOfArr );
             if( closeIndexOfArr < 0 )
             {
            	 throw new MarkupException( "Invalid AddVarOneOf : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must contain ']' to signify end of array ", 
                     text, lineNumber );
             }
             // "{+vowel['a','e','i','o','u']+}"
             //  --                              OPEN_TAG
             //    -----                         varName 
             //         -                        open arr 
             //          -------------------     arr 
             //                             -    close arr
             //                              --  CLOSE_TAG
             
             String varName = text.substring( OPEN_TAG.length(), openIndexOfArr );
             String arr = text.substring( openIndexOfArr + 1, closeIndexOfArr + 1 );
             Object[] array = null;
             try
             {
            	 array = 
            	     Eval_JavaScript.getJSArrayAsObjectArray( 
            	         parseContext.evaluate( arr ) );
             }
             catch( Exception e )
             {
            	 throw new MarkupException( "Invalid AddVarOneOf : " + N 
                         + text + N + "  ... on line ["
                         + lineNumber + "] could not evaluate array " + arr , 
                         text, lineNumber, e ); 
             }
             String defaultValue = null;
             boolean isRequired = 
            	text.substring( 
            		text.length() - CLOSE_TAG.length() -1, 
            		text.length() - CLOSE_TAG.length() )
            	.equals( "*" );
             if( charIs(text, closeIndexOfArr + 1, '|' ) )
             {
            	 defaultValue = text.substring( 
            		closeIndexOfArr + 2,
            		text.length() - CLOSE_TAG.length() );
            	 /*
            	 if( defaultValue.length() > 0 && defaultValue.trim().length() > 0 )
            	 {   //verify this value is within the array
            		 
            	 }
            	 else
            	 {
            		 
            	 }
            	 */
             }
             // "{+vowel:['a','e','i','o','u']|'e'+}"
             // "{+vowel:['a','e','i','o','u']*+}"
             return new AddVarOneOf( text, lineNumber, varName, array, arr, isRequired, defaultValue );
    	}
    }
    // adding these
    // {+name:(( expression  ))+}
    // {+name:(( expression  ))*+}
    // {+name:(( expression  ))|default+}
    
    // 1) look for "((:"
    //NOTE: I might add some indirection to allow this:
    
    // {+name:['a','b','c']+}
    
    // and "manufacture" the expression:
    // if( ['a', 'b', 'c'].indexOf(name) >= 0 )
    
    public static class AddVarExpressionMark
    {
    	 public static final String OPEN_TAG = "{+";
         public static final String CLOSE_TAG = "+}";
         
         public static final String EXPRESSION_OPEN_TAG = ":((";
         public static final String EXPRESSION_CLOSE_TAG = "))";

         public static AddVarIsExpression of( String text )
         {
             return of( text, -1, VarNameAudit.BASE );
         }
         
         public static AddVarIsExpression of( String text, int lineNumber )
         {
             return of( text, lineNumber, VarNameAudit.BASE );
         }
         
         // {+name:(( expression  ))+}
         // {+name:(( expression  ))*+}
         // {+name:(( expression  ))|default+}
         
         public static AddVarIsExpression of( String text, int lineNumber, VarNameAudit nameAudit )
             throws MarkupException
         {
             if( !text.startsWith( OPEN_TAG ) )
             {
                 throw new MarkupException( "Invalid AddVarExpression : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must start with " 
                     + " \"" + OPEN_TAG + "\" ", text, lineNumber );
             }
             if( !text.endsWith( CLOSE_TAG ) )
             {
                 throw new MarkupException( "Invalid AddVarExpression : " + N 
                     + text + N + "  ... on line ["
                     + lineNumber + "] must end with CLOSE MARK" 
                     + " \"" + CLOSE_TAG + "\" ", text, lineNumber );
             }
             
             // {+name:(( expression  ))+}
             //   ----------------------
             // {+name:(( expression  ))*+}
             //   -----------------------
             
             // {+name:(( expression  ))|default+}
             //   ------------------------------ 
             String tag = text.substring( 
                 OPEN_TAG.length(), 
                 text.indexOf( CLOSE_TAG ) );
             
             boolean isRequired = false;
             String expression = null;
             String defaultValue = null;
             
             if( tag.endsWith( "*" ) )
             {
            	 //chop off the required
            	 // {+name...*+}
            	 // --             OPEN_TAG
            	 //   --------     tag     
            	 //          -     REQUIRED
            	 //           --   CLOSE_TAG
            	 tag = tag.substring( 0, tag.length() -1);
                 //   -------     tag ( remove *
            	 isRequired = true;
             }
             int expressionOpen = tag.indexOf( EXPRESSION_OPEN_TAG  );
             int expressionClose = tag.lastIndexOf( EXPRESSION_CLOSE_TAG ); 
             String name = null;
             if( expressionOpen > 0 && expressionClose > 0 )
             {     
            	// {+name:(( expression  ))|default+}
                //   ------------------------------   tag
             	//   ----                             name            	 
            	//       ---                          EXPRESSION_OPEN_TAG
             	//          -------------             expression 
            	//                       --           EXPRESSION_CLOSE_TAG 
                //                         --------   afterExpression
            	name = tag.substring( 0, expressionOpen ); 
            	expression = tag.substring(
            			expressionOpen + EXPRESSION_OPEN_TAG.length(),
            			expressionClose );
            	String afterExpression = 
            		tag.substring( expressionClose + EXPRESSION_CLOSE_TAG.length() ); 
            	if( afterExpression.length() > 0 )
            	{   // they must have a default
            		if( !afterExpression.startsWith( "|" ) )
            		{
            			 throw new MarkupException(
            	              "Content AFTER AddVarExpression \"" + afterExpression + "\"" +
            			      "must start with '|' (default separator) for \"" +
            			      text + "\", on line [" + lineNumber + "]", 
            	              text, 
            	              lineNumber );
            		}
            		defaultValue = afterExpression.substring( 1 );
            	}
             }
             else  //there is no expression 
             {
            	 int indexOfOr = tag.indexOf( '|' ); 
            	 if( indexOfOr > 0 )
            	 {   //they have a default specified
            		 defaultValue = tag.substring( indexOfOr + 1 );
            		 name = tag.substring( 0, indexOfOr );
            	 }            	 
            	 else
            	 {
            		 name = tag;
            	 }
             }
             try
             { 
            	 nameAudit.audit( name );
             }
             catch( Exception e )
             {
            	 throw new MarkupException(
                         "Invalid AddVarExpression Name \"" + name + "\", on line [" 
                       + lineNumber + "]", 
                       text, 
                       lineNumber, e );
             }
             return new AddVarIsExpression( 
            		 text, 
            		 lineNumber,
            		 name,
            		 isRequired,
            		 expression,
            		 defaultValue );             
         }
    }
    /*
    public static class AddVarInlineMark
    {
        public static final String OPEN_TAG = "{+";
        
        public static final String CLOSE_TAG = "+}";
        
        public static AddVar of( 
            String text, int lineNumber, VarNameAudit nameAudit )
            throws MarkupException
        {
            String name = null;
            boolean isRequired = false;
            String defaultValue = null;
            
            String tag = text.substring( 
                OPEN_TAG.length(), 
                text.indexOf( "}" ) );
    
            String[] nameDefault = Text.Tokenize.byChar( tag, '|' );
            if( nameDefault[ 0 ].endsWith( "*" ) )
            {
                name = nameDefault[ 0 ].substring( 0, nameDefault[ 0 ].length() -1 );
                isRequired = true;            
            }
            else
            {
                name = nameDefault[ 0 ];
                isRequired = false;
            }
            if( nameDefault.length == 2 )
            {    //they specified a default
                 defaultValue = nameDefault[ 1 ];
            }
            else
            {
                defaultValue = null;
            }
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                   "Invalid AddVar (Inline) Name \"" + name + "\", on line [" 
                 + lineNumber + "]", 
                 text, 
                 lineNumber, e );
            }
            return new AddVar( text, lineNumber, name, isRequired, defaultValue );
        }
    }
    */
    
    public static class AddExpressionResultInlineMark
    {
    	public static final String OPEN_TAG = "{+((";  
        public static final String CLOSE_TAG = "))+}";
        
        public static AddExpressionResult of(
        	String text, int lineNumber )
        {
        	if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddExpressionResult : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddExpressionResult : " + N 
                    + text + N
                    + "  ... on line [" + lineNumber + "] must end with CLOSE MARK"
                    + " \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );   
            }
            String expression = text.substring( OPEN_TAG.length(), text.length() - CLOSE_TAG.length() );
            
            return new AddExpressionResult( text, lineNumber, expression );
        }
    }
    
    public static class AddFormMark
    {
        /** Opening mark for a AddForm */
        public static final String OPEN_TAG = "{{+:";
        
        /** Closing mark for a AddForm */
        public static final String CLOSE_TAG = "+}}";
        
        public static AddForm of(
            VarContext parseContext, 
            String text, 
            int lineNumber, 
            ForMLCompiler formCompiler )
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            String formText = 
                text.substring( 
                    OPEN_TAG.length(), 
                    text.length() - CLOSE_TAG.length() );
            
            boolean isRequired = false;
            if( formText.endsWith( "*" ) ) //is it "required?"
            {
            	formText = formText.substring( 0, formText.length() -1 );
            	isRequired = true;
            }
            try
            {
                Form form = formCompiler.fromString( 
                    parseContext,
                    lineNumber, 
                    "", //the name is BLANK 
                    formText );
                return new AddForm( text, lineNumber, isRequired, form );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark: " + N + text + N 
                  + "on line [" + lineNumber + "], form :" + N 
                  + formText + N + "...is invalid",
                    text, 
                    lineNumber, 
                    t );
            }
            
        }
    }
    
    public static class AddFormMark_Alt
    {
        /** Opening mark for a AddForm */
        public static final String OPEN_TAG = "{_+:";
        
        /** Closing mark for a AddForm */
        public static final String CLOSE_TAG = "+_}";
        
        public static AddForm of(
            VarContext parseContext, 
            String text, 
            int lineNumber, 
            ForMLCompiler formCompiler )
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            String formText = 
                text.substring( 
                    OPEN_TAG.length(), 
                    text.length() - CLOSE_TAG.length() );
            

            try
            {
                Form form = formCompiler.fromString( 
                    parseContext,
                    lineNumber, 
                    "", //the name is BLANK 
                    formText );
                return new AddForm( text, lineNumber, false, form ); //NEVER required
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddForm Mark: " + N + text + N 
                  + "on line [" + lineNumber + "], form :" + N 
                  + formText + N + "...is invalid",
                    text, 
                    lineNumber, 
                    t );
            }
            
        }
    }
    
    public static class AddScriptResultIfExpressionMark
    {
    	/** Opening mark for a IfExpressionAddScriptResultMark */
        public static final String OPEN_TAG = "{+?((";
        
        /** Closing mark for a IfExpressionAddScriptResultMark */
        public static final String CLOSE_TAG = ")+}";
        
        // "{+?(( expression )):$scriptName(params)+}"
        public static final String EXPRESSION_SCRIPT_SEPARATOR = ")):$";
        
        // "{+?(( expression )):$scriptName()+}"
    	public static AddScriptResultIfExpression of( String text, int lineNumber )
    	{
    		 if( !text.startsWith( OPEN_TAG ) )
             {            	
                 throw new MarkupException(
                     "Invalid IfExpressionAddScriptResultMark on line [" + lineNumber 
                     + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                     text, 
                     lineNumber );
             }
             if( !text.endsWith( CLOSE_TAG ) )
             {
                 throw new MarkupException(
                     "Invalid IfExpressionAddScriptResultMark on line [" + lineNumber + "], must end "
                     + "with close mark \"" + CLOSE_TAG + "\" ",
                     text, 
                     lineNumber );
             }
             //                              ")):$" 
             int sepIndex = text.indexOf( EXPRESSION_SCRIPT_SEPARATOR );
             if( sepIndex < 0 )
             {
                 throw new MarkupException(
                     "Invalid IfExpressionAddScriptResultMark " + N + text + N + " on line [" 
                     + lineNumber + "], must contain \")):$\" separating expression from script",
                     text, 
                     lineNumber );
             }
             int paramOpen = text.indexOf( '(', sepIndex );
             if( paramOpen < 0 )
             {
                 throw new MarkupException(
                     "Invalid IfExpressionAddScriptResultMark " + N + text + N + " on line [" 
                     + lineNumber + "], must contain '(' AFTER \")):$\" signifying the parameters for script",
                     text, 
                     lineNumber );
             } 
             // "{+?(( expression )):$scriptName(params)+}"
             //  -----                                      OPEN TAG
             //       ------------                          expression
             //                   ----                      SEPARATOR
             //                       ----------            scriptName
             //                                 _           (
             //                                  ------     params
             //                                        ---  CLOSE_TAG              
             String expression = text.substring( OPEN_TAG.length(), sepIndex );
             String scriptName = text.substring( 
            		sepIndex + EXPRESSION_SCRIPT_SEPARATOR.length(), paramOpen );
             String params = text.substring( paramOpen + 1, text.length() - CLOSE_TAG.length() );
             
             return new AddScriptResultIfExpression( 
            		 text, 
            		 lineNumber, 
            		 expression, 
            		 scriptName, 
            		 params );
             
    	}
    }
    
    // "{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}" 
    public static class AddFormIfExpressionMark
    {
    	/** Open tag for a AddFormIfExpression */
        public static final String OPEN_TAG = "{{+?((";
        
        public static final String SEPARATOR = ")):";
        
        /** Close tag for a AddFormIfExpression */
        public static final String CLOSE_TAG = "+}}";
    	
        public static AddFormIfExpression of( VarContext parseContext, String text, int lineNumber )        
        {        
        	if( !text.startsWith( OPEN_TAG ) )
            {            	
                throw new MarkupException(
                    "Invalid AddFormIfExpression :"+ N + text + N + "on line [" + lineNumber 
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfExpression :"+ N + text + N + "on line [" + lineNumber + "], must end "
                    + "with close tag \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int separatorIndex = text.indexOf( SEPARATOR );
            if( separatorIndex < 0 )
            {
                throw new MarkupException(
                        "Invalid AddFormIfExpression:" + N + text + N 
                      + "on line [" + lineNumber + "], must contain "+ " separator \"" + SEPARATOR + "\" ",
                        text, 
                        lineNumber );
            	
            }
            // "{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}"
            //  ------                                                  OPEN_TAG
            //        ------------------                                expression   
            //                          ---                             SEPARATOR
            //                             -------------------------    formText
            //                                                      --- CLOSE_TAG
            
            String expression = text.substring( OPEN_TAG.length(), separatorIndex );
            String formText = text.substring(separatorIndex + SEPARATOR.length(), text.length() - CLOSE_TAG.length() );
            Form form = null;
            try
            {
            	form = FORM_COMPILER.compile( formText );
            }
            catch( Exception t)
            {
            	 throw new MarkupException(
                         "Invalid AddFormIfExpression:" + N + text + N 
                       + "on line [" + lineNumber + "]"
                       + " the form : " + N + formText + N + "is invalid",
                         text, 
                         lineNumber,
                         t );                 
            }
            return new AddFormIfExpression( text, lineNumber, expression, form );
        }        
    }
    
    public static class AddFormIfExpressionMark_Alt
    {
    	/** Open tag for a AddFormIfExpression */
        public static final String OPEN_TAG = "{_+?((";
        
        public static final String SEPARATOR = ")):";
        
        /** Close tag for a AddFormIfExpression */
        public static final String CLOSE_TAG = "+_}";
    	
        public static AddFormIfExpression of( VarContext parseContext, String text, int lineNumber )        
        {        
        	if( !text.startsWith( OPEN_TAG ) )
            {            	
                throw new MarkupException(
                    "Invalid AddFormIfExpression :"+ N + text + N + "on line [" + lineNumber 
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfExpression :"+ N + text + N + "on line [" + lineNumber + "], must end "
                    + "with close tag \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int separatorIndex = text.indexOf( SEPARATOR );
            if( separatorIndex < 0 )
            {
                throw new MarkupException(
                        "Invalid AddFormIfExpression:" + N + text + N 
                      + "on line [" + lineNumber + "], must contain "+ " separator \"" + SEPARATOR + "\" ",
                        text, 
                        lineNumber );
            	
            }
            // "{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}"
            //  ------                                                  OPEN_TAG
            //        ------------------                                expression   
            //                          ---                             SEPARATOR
            //                             -------------------------    formText
            //                                                      --- CLOSE_TAG
            
            String expression = text.substring( OPEN_TAG.length(), separatorIndex );
            String formText = text.substring(separatorIndex + SEPARATOR.length(), text.length() - CLOSE_TAG.length() );
            Form form = null;
            try
            {
            	form = FORM_COMPILER.compile( formText );
            }
            catch( Exception t)
            {
            	 throw new MarkupException(
                         "Invalid AddFormIfExpression:" + N + text + N 
                       + "on line [" + lineNumber + "]"
                       + " the form : " + N + formText + N + "is invalid",
                         text, 
                         lineNumber,
                         t );                 
            }
            return new AddFormIfExpression( text, lineNumber, expression, form );
        }        
    }
    
    /** An "alternative" tag AddFormIfMark */
    public static class AddFormIfVarMark_Alt
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "{_+?";
        
        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "+_}";
        

        public static AddFormIfVar of(
            VarContext parseContext,
            String text, 
            int lineNumber, 
            ForMLCompiler formCompiler )
        {        
            String name;
            Form form;
            String targetValue;
            
            if( !text.startsWith( OPEN_TAG ) )
            {            	
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber 
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar " + N + text + N + " on line [" 
                    + lineNumber + "], must contain ':' separating condition from code",
                    text, 
                    lineNumber );
            }
                            
            String nameEquals = text.substring( OPEN_TAG.length(), colonIndex ); //parseNameEquals( this.text, lineNumber );
            
            int equalsIndex = nameEquals.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = nameEquals.substring( 0, equalsIndex ).trim();
                
                targetValue = nameEquals.substring( 
                    equalsIndex + 2, 
                    nameEquals.length() );          
            }
            else
            {
                equalsIndex = nameEquals.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = nameEquals.substring( 0, equalsIndex ).trim();
                
                    targetValue = nameEquals.substring( 
                        equalsIndex + 1, 
                        nameEquals.length() );          
                }
                else
                {
                    name = nameEquals.trim();
                    targetValue = null;
                }
            }
                
            //"/*{{?ifAddCode:" + "/+** {+javadocComment} *+/ /+* {+comment} *+/" + "}}*/");
            String formText = 
                text.substring( 
                    colonIndex + 1, 
                    text.length() - CLOSE_TAG.length() ); 
            
            try
            {
                form = formCompiler.fromString(
                    parseContext, 
                    lineNumber, 
                    name, 
                    formText );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar:" + N + text + N 
                  + "on line [" + lineNumber + "]"
                  + "the form : " + N
                  + formText + N
                  + "is invalid",
                    text, 
                    lineNumber,
                    t );
            }
            try
            {
                parseContext.getVarNameAudit().audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar name \"" + name 
                    + "\" on line [" + lineNumber + "]",
                    text, 
                    lineNumber, e );
            }
            return new AddFormIfVar( text, lineNumber, name, targetValue, form );
        }        
    }
    
    public static class AddFormIfVarMark
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "{{+?";
        
        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "+}}";

        
        public static AddFormIfVar of(
            VarContext parseContext,
            String text, 
            int lineNumber, 
            ForMLCompiler formCompiler  )
        {        
            String name;
            Form form;
            String targetValue;
            
            if( !text.startsWith( OPEN_TAG ) )
            {            	
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber 
                    + "], must start with open mark \"" + OPEN_TAG + "\" and end with \"" + CLOSE_TAG + "\"",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar " + N + text + N + " on line [" 
                    + lineNumber + "], must contain ':' separating condition from code",
                    text, 
                    lineNumber );
            }
                            
            String nameEquals = text.substring( OPEN_TAG.length(), colonIndex ); //parseNameEquals( this.text, lineNumber );
            
            int equalsIndex = nameEquals.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = nameEquals.substring( 0, equalsIndex ).trim();
                
                targetValue = nameEquals.substring( 
                    equalsIndex + 2, 
                    nameEquals.length() );          
            }
            else
            {
                equalsIndex = nameEquals.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = nameEquals.substring( 0, equalsIndex ).trim();
                
                    targetValue = nameEquals.substring( 
                        equalsIndex + 1, 
                        nameEquals.length() );          
                }
                else
                {
                    name = nameEquals.trim();
                    targetValue = null;
                }
            }
                
            //"/*{{?ifAddCode:" + "/+** {+javadocComment} *+/ /+* {+comment} *+/" + "}}*/");
            String formText = 
                text.substring( 
                    colonIndex + 1, 
                    text.length() - CLOSE_TAG.length() ); 
            
            try
            {
                form = formCompiler.fromString(
                    parseContext, 
                    lineNumber, 
                    name, 
                    formText );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar:" + N + text + N 
                  + "on line [" + lineNumber + "]"
                  + "the form : " + N
                  + formText + N
                  + "is invalid",
                    text, 
                    lineNumber,
                    t );
            }
            try
            {
                parseContext.getVarNameAudit().audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddFormIfVar name \"" + name 
                    + "\" on line [" + lineNumber + "]",
                    text, 
                    lineNumber, e );
            }
            return new AddFormIfVar( text, lineNumber, name, targetValue, form );
        }
    }
    
    public static class AddIfVarMark
    {
        /** Opening mark for a AddIf */
        public static final String OPEN_TAG = "{+?";
        
        /** Closing mark for a AddIf */
        public static final String CLOSE_TAG = "+}";

        public static AddTextIfVar of( String text, int lineNumber )
        {
            return of( text, lineNumber, VarNameAudit.BASE );
        }
        
        public static AddTextIfVar of( 
            String text, int lineNumber, VarNameAudit nameAudit )
        {
            String name = null;
            String targetValue = null;
            String code = null;
            
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddIfVar mark on [" + lineNumber + "], must start "
                    + "with open mark \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddIfVar on line [" + lineNumber + "], must end "
                    + "with close mark \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );
            }
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException(
                    "Invalid AddIfVar " + N + text + " on line [" 
                    + lineNumber + "], Mark must contain ':' separating condition"
                    + "from code",  
                    text, 
                    lineNumber );
            }
            String condition = text.substring( OPEN_TAG.length(), colonIndex );
            //String nameEquals = parseNameEquals( this.text, lineNumber );
            
            int equalsIndex = condition.indexOf( "==" );
            if( equalsIndex > -1 )
            {
                name = condition.substring( 0, equalsIndex ).trim();
                
                targetValue = condition.substring( 
                    equalsIndex + 2, 
                    condition.length() );           
            }
            else
            {
                equalsIndex = condition.indexOf( "=" );
                if( equalsIndex > -1 )
                {
                    name = condition.substring( 0, equalsIndex ).trim();
                
                    targetValue = condition.substring( 
                        equalsIndex + 1, 
                        condition.length() );          
                }
                else
                {
                    name = condition.trim();
                    targetValue = null;
                }
            }           
            //basically everything AFTER : and before close MARK
            //TODO TEST THIS
            code = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid AddIfVar name \"" + name 
                    + "\" on line [" + lineNumber + "]",
                    text, 
                    lineNumber );
            }
            return new AddTextIfVar( text, lineNumber, name, targetValue, code );
        }
    }
    
   
    public static class AddScriptResultMark
    {
        public static final String OPEN_TAG = "{+$";  
        public static final String CLOSE_TAG = "+}"; 
        
        public static AddScriptResult of( 
            String text, 
            int lineNumber )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultMark : " + N 
                   + text + N
                   + "  ... on line [" + lineNumber + "] must end with CLOSE MARK"
                   + " \"" + CLOSE_TAG + "\" ",
                   text, 
                   lineNumber );   
            }

            int openParamIndex = text.indexOf( '(' );
            int closeParamIndex = text.lastIndexOf( ')' );

            // "{+$stripSpaces(f)+}"            
            String scriptName = text.substring( OPEN_TAG.length(), openParamIndex );
            // parameters = null; 
            if( openParamIndex < 0  ||  closeParamIndex < 0 )
            {
                throw new MarkupException(
                    "AddScriptResult Mark : " + N 
                  + text + N 
                  + " with name \"" + scriptName 
                  + "\" on line [" + lineNumber  
                  + "] must have a '(' and ')' to demarcate parameters ",
                    text, 
                    lineNumber );
            }
            if( openParamIndex > closeParamIndex )
            {
                throw new MarkupException(
                    "AddScriptResultMark with script name \"" + scriptName 
                  + "\" on line ["+ lineNumber 
                  + "] must have a '(' BEFORE ')' to demarcate parameters",
                  text, lineNumber);
            } 
            String paramContents = 
                text.substring( openParamIndex + 1 , closeParamIndex );

            boolean isRequired = false;
            if( charIs( text, closeParamIndex + 1, '*' ) ) 
            {
            	isRequired = true;
            }
            return new AddScriptResult( 
                text, 
                lineNumber, 
                scriptName, 
                paramContents,
                isRequired);            
        }
    }
    
    public static class DefineStaticVarAsFormMark
    {   
        public static final String OPEN_TAG = "{{##";
        
        public static final String CLOSE_TAG = "##}}";
        
        public static DefineVarAsForm.StaticVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            // {{##params*:{+fieldName}, ##}}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineStaticVarAsForm cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG) ) ) 
            {
                throw new MarkupException( 
                    "DefineStaticVarAsForm must start with \"" + OPEN_TAG + "\"" 
                  + "\" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsForm must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {{##params*:{+fieldName}, ##}}
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
            	colonIndex = text.indexOf( '=' );
            	if( colonIndex <  0 )
            	{
            		throw new MarkupException( 
            			"DefineStaticVarAsFormMark : " + N + text + N + " must have \":\"" 
            		  + " separating the name from the code form",
            		  text, 
            		  lineNumber );
            	}
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //   {{##params...*:{+fieldName}, ##}}
            //       params...*
            beforeColon = text.substring( 
                OPEN_TAG.length(), 
                colonIndex );
            
            if( beforeColon.endsWith( "*" ) )
            {
                isRequired = true;
                beforeColon = beforeColon.substring( 0, beforeColon.length() - 1 );
            }
            else
            {
                isRequired = false;
            }
            
            name = beforeColon;
            try
            {
            	parseContext.getVarNameAudit().audit( name ); 
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineStaticVarAsForm: " + N
                  + text + N + " on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "}}*/
            //   {{##params...*:{+fieldName}, ##}}
            String form = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {               
                codeForm = formCompiler.fromString( 
                    parseContext, 
                    lineNumber, 
                    name, 
                    form );                
            }
            catch( VarException cme )
            {
                throw new MarkupException(
                    "Invalid form: \"" + form 
                  + "\" for DefineStaticVarAsForm mark on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }            
            return new DefineVarAsForm.StaticVar( 
                text, 
                lineNumber,
                name,    
                codeForm,
                isRequired );
        }
    }
    
    public static class DefineStaticVarAsExpressionResult
    {
    	public static final String OPEN_TAG = "{##";
        
        public static final String CLOSE_TAG = "))##}";
        
        public static DefineVarAsExpressionResult.StaticVar of( 
        	String text, int lineNumber )
        {
            // {##c:((a + b))##}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineStaticVarAsExpressionResult cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG) ) ) 
            {
                throw new MarkupException( 
                    "DefineStaticVarAsExpressionResult must start with \"" + OPEN_TAG + "\"" 
                  + "\" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsExpressionResult must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {##c:((a + b))##}
            int colonIndex = text.indexOf( ":((" );
            if( colonIndex < 0 )
            {
            	// {##c=((a + b))##}
            	colonIndex = text.indexOf( "=((" );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineStaticVarAsExpressionResult : " + N + text + N 
            		  + " must have \":((\"" + " or '=((' separating the name "
            		  + "from the expression",
            		  	text, 
            		  	lineNumber );
            	}
            }
            // {##c:((a + b))##}
            //    ^
            String name = text.substring( OPEN_TAG.length(), colonIndex );
            
            // {##c:((a + b))##}
            //        ^^^^^
            String expression = text.substring( 
            	colonIndex + 3, text.length() - CLOSE_TAG.length() );
            
            return new DefineVarAsExpressionResult.StaticVar( 
            	text, lineNumber, name, expression );        	
        }
    }
    
    public static class DefineStaticVarAsFormMark_Alt
    {   
        public static final String OPEN_TAG = "{_##";
        
        public static final String CLOSE_TAG = "##_}";
        
        public static DefineVarAsForm.StaticVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            // {_##params*:{+fieldName}, ##_}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineStaticVarAsForm cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG) ) ) 
            {
                throw new MarkupException( 
                    "DefineStaticVarAsForm must start with \"" + OPEN_TAG + "\"" 
                  + "\" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsForm must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {_##params*:{+fieldName}, ##_}
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
            	colonIndex = text.indexOf( '=' );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineStaticVarAsFormMark : " + N + text + N + " must have \":\"" 
            		  + " or '=' separating the name from the code form",
            		  	text, 
            		  	lineNumber );
            	}
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //    {_##params*:{+fieldName}, ##_}
            //        params*
            beforeColon = text.substring( 
                OPEN_TAG.length(), 
                colonIndex );
            
            if( beforeColon.endsWith( "*" ) )
            {
                isRequired = true;
                beforeColon = beforeColon.substring( 0, beforeColon.length() - 1 );
            }
            else
            {
                isRequired = false;
            }
            
            name = beforeColon;
            try
            {
            	parseContext.getVarNameAudit().audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineStaticVarAsForm: " + N
                  + text + N + " on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "}}*/
            //   {_##params...*:{+fieldName}, ##_}
            String form = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {
                codeForm = formCompiler.fromString( 
                    parseContext, 
                    lineNumber, 
                    name, 
                    form );                
            }
            catch( VarException cme )
            {
                throw new MarkupException(
                    "Invalid form: \"" + form 
                  + "\" for DefineStaticVarAsForm mark on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }
            
            return new DefineVarAsForm.StaticVar( 
                text, 
                lineNumber,
                name,    
                codeForm,
                isRequired );
        }
    }
    
    public static class DefineInstanceVarAsExpressionResult
    {
    	public static final String OPEN_TAG = "{#";
        
        public static final String CLOSE_TAG = "))#}";
        
        public static DefineVarAsExpressionResult.InstanceVar of( 
        	String text, int lineNumber, VarNameAudit nameAudit )
        {            
            // {#c:((a + b))#}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineInstanceVarAsExpressionResult cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG) ) ) 
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsExpressionResult must start with \"" + OPEN_TAG + "\"" 
                  + "\" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsExpressionResult must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {##c:((a + b))##}
            int colonIndex = text.indexOf( ":((" );
            if( colonIndex < 0 )
            {
            	// {##c=((a + b))##}
            	colonIndex = text.indexOf( "=((" );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineStaticVarAsExpressionResult : " + N + text 
            		  + N + " must have \":((\"" 
            		  + " or '=((' separating the name from the expression",
            		  	text, 
            		  	lineNumber );
            	}
            }
            // {#c:((a + b))#}
            //   ^
            String name = text.substring( OPEN_TAG.length(), colonIndex );
            
            // {#c:((a + b))#}
            //       ^^^^^
            String expression = text.substring( 
            	colonIndex + 3, text.length() - CLOSE_TAG.length() );
            
            return new DefineVarAsExpressionResult.InstanceVar( 
            	text, lineNumber, name, expression );        	            
        }
    }
    
    public static class DefineInstanceVarAsFormMark
    {
        public static final String OPEN_TAG = "{{#";
        
        public static final String CLOSE_TAG = "#}}";
        
        public static DefineVarAsForm.InstanceVar of(
            VarContext compileContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            // {{#params*:{+fieldName}, #}}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineInstanceVarAsForm cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG ) ) ) 
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm must start with \"" + OPEN_TAG + "\"" 
                  + " mark :" + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {{#params*:{+fieldName}, #}}
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
            	// {{#params*={+fieldName}, #}}
            	colonIndex = text.indexOf( '=' );
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineInstanceVarAsForm : " + N + text + N 
            		  + " must have \":\" separating the name from the code form",
            		    text, 
            		    lineNumber );
            	}
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //    {{#params*:{+fieldName}, #}}
            //        params*
            beforeColon = text.substring( 
                OPEN_TAG.length(), 
                colonIndex );
                        
            if( beforeColon.endsWith( "*" ) )
            {
                isRequired = true;
                beforeColon = beforeColon.substring( 0, beforeColon.length() - 1 );
            }
            else
            {
                isRequired = false;
            }
            name = beforeColon;
            try
            {
                compileContext.getVarNameAudit().audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineVarAsForm: " + N
                  + text + N + " on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "#}}"
            //    {{#params*:{+fieldName}, #}}
            String form = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {   
                codeForm = formCompiler.fromString( 
                    compileContext, 
                    lineNumber, 
                    name, 
                    form );                
            }
            catch( VarException cme )
            {
                throw new MarkupException(
                    "Invalid form: \"" + form 
                  + "\" for DefineVarAsForm mark on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }
            
            return new DefineVarAsForm.InstanceVar( 
                text, 
                lineNumber,
                name,    
                codeForm,
                isRequired );
        }
    }
    
    public static class DefineInstanceVarAsFormMark_Alt
    {
        public static final String OPEN_TAG = "{_#";
        
        public static final String CLOSE_TAG = "#_}";
        
        public static DefineVarAsForm.InstanceVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            // {_#params*:{+fieldName}, #_}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineInstanceVarAsForm cannot be null ",
                    text, 
                    lineNumber );
            }
            if( !( text.startsWith( OPEN_TAG ) ) ) 
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm must start with \"" + OPEN_TAG + "\"" 
                  + " mark :" + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // {_#params*:{+fieldName}, #_}
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
            	colonIndex = text.indexOf( '=' );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineInstanceVarAsForm : " + N + text + N 
            		  + " must have ':' or '=' separating the name from the code form",
            		  	text, 
            		  	lineNumber );
            	}
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //     {_#params*:{+fieldName}, #_}
            //        params*
            beforeColon = text.substring( 
                OPEN_TAG.length(), 
                colonIndex );
                        
            if( beforeColon.endsWith( "*" ) )
            {
                isRequired = true;
                beforeColon = beforeColon.substring( 0, beforeColon.length() - 1 );
            }
            else
            {
                isRequired = false;
            }
            name = beforeColon;
            try
            {
            	parseContext.getVarNameAudit().audit( name );                
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineVarAsForm: " + N
                  + text + N + " on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "#_}"
            //   {_#params...*:{+fieldName}, #_}
            String form = text.substring( 
                colonIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            try
            {   
                codeForm = formCompiler.fromString( 
                    parseContext, 
                    lineNumber, 
                    name, 
                    form );                
            }
            catch( VarException cme )
            {
                throw new MarkupException(
                    "Invalid form: \"" + form 
                  + "\" for DefineVarAsForm mark on line ["+ lineNumber + "]",
                    text, 
                    lineNumber );
            }
            
            return new DefineVarAsForm.InstanceVar( 
                text, 
                lineNumber,
                name,    
                codeForm,
                isRequired );
        }
    }
    
    public static class DefineInstanceVarMark
    {
        public static final String OPEN_TAG = "{#";
        
        public static final String CLOSE_TAG = "#}";
        
        public static DefineVar.InstanceVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {   
            // {#separatorChar:.}
            if( text == null )
            {
                throw new MarkupException( 
                    "DefineVar_Instance Mark cannot be null on line [" 
                  + lineNumber + "]",
                  text, 
                  lineNumber );
            }
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( 
                    "DefineVar_Instance Mark must start with \"" + OPEN_TAG + "\"" 
                   +" mark : " + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineVar_Instance Mark must end with \""+ CLOSE_TAG + "\"" 
                   +" mark : " + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            int colonIndex = text.indexOf( ":" );
            if( colonIndex < 0 )
            {
                int equalsIndex = text.indexOf( "=" );
                if( equalsIndex < 0 )
                {
                    throw new MarkupException(
                        "DefineVar Mark: " + N + text + N
                      + " on line [" + lineNumber + "] "  
                      + "must contain \":\" to separate name from value",
                      text, 
                      lineNumber );
                }
                //just treat the = like a :
                colonIndex = equalsIndex;
            }
            
            /*{#markName:baloney}*/
            String value = 
                text.substring( 
                    colonIndex + 1, 
                    text.length() - CLOSE_TAG.length() );
            
            /*{#markName:baloney}*/
            String name = null;
            
            name = text.substring( OPEN_TAG.length(), colonIndex );
            
            try
            {
                nameAudit.audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineVar_Instance on line ["
                  + lineNumber + "]",
                    text, 
                    lineNumber );
            }

            return new DefineVar.InstanceVar( 
                text, 
                lineNumber,
                name,
                value );
        }
    }
    
    public static class DefineStaticVarMark
    {        
        public static final String OPEN_TAG = "{##";
        
        public static final String CLOSE_TAG = "##}";
        
        public static DefineVar.StaticVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {   
            /**{#theDate:date=3}*/
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineVar_Static Mark cannot be null on line [" 
                  + lineNumber + "]",
                  text, 
                  lineNumber );
            }
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( 
                    "DefineVar_Static Mark must start with \"" + OPEN_TAG + "\"" 
                   +" mark : " + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineVar_Static Mark must end with \""+ CLOSE_TAG + "\"" 
                   +" mark : " + N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            int colonIndex = text.indexOf( ":" );
            if( colonIndex < 0 )
            {
                int equalsIndex = text.indexOf( "=" );
                if( equalsIndex < 0 )
                {
                    throw new MarkupException(
                        "DefineVar_Static Mark: " + N + text + N
                      + " on line [" + lineNumber + "] "  
                      + "must contain \":\" to separate name from value",
                      text, 
                      lineNumber );
                }
                //just treat the = like a :
                colonIndex = equalsIndex;
            }
            
            /*{#markName:baloney}*/
            String value = 
                text.substring( 
                    colonIndex + 1, 
                    text.length() - CLOSE_TAG.length() );
            
            /*{#markName:baloney}*/
            /**{#markName:baloney}*/
            String name = null;
            
            name = text.substring( OPEN_TAG.length(), colonIndex );
            
            try
            {
                nameAudit.audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name + "\" for DefineVar on line ["
                  + lineNumber + "]",
                    text, 
                    lineNumber );
            }

            return new DefineVar.StaticVar( 
                text, 
                lineNumber,
                name,
                value );
        }
    }
    
    public static class DefineStaticVarAsScriptResultMark
    {
        public static final String OPEN_TAG = "{##";
        
        public static final String CLOSE_TAG = ")##}";
        
        public static DefineVarAsScriptResult.StaticVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {            
            String name = "";
            String scriptName = "";
            
            // {##theDate:$date(YYYYmmDD)##}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineStaticVarAsScriptResult Mark cannot be null on line [" 
                  + lineNumber + "]",
                  text, 
                  lineNumber );
            }
            if( ! ( text.startsWith( OPEN_TAG ) ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsScriptResult Mark must start with \"" + OPEN_TAG 
                   + "\" value : "+ N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsScriptResult Mark must end with \""+ CLOSE_TAG 
                  + "\"" +" value : \"" + text + "\" is invalid",
                   text, 
                   lineNumber );
            }
            int colonDollarIndex = text.indexOf( ":$" );
            if( colonDollarIndex < 0 )
            {
            	colonDollarIndex = text.indexOf( "=$" );
            	if( colonDollarIndex < 0 )
            	{
            		throw new MarkupException(
            			"DefineStaticVarAsScriptResult Mark must contain "
            		  + "\":$\" or \"=$\" to represent end of name and start "
            		  + "of script name",
                    text, 
                    lineNumber );
            	}            	
            }
            
            // {##theDate:$date(YYYYmmDD)##}
            int openParamIndex = text.indexOf( '(' );
            if( openParamIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsScriptResult must have \"(\"" 
                  + " separating the script name from the argument list \""
                  + text + "\" is invalid",
                  text, 
                  lineNumber );
            }
            // {##theDate:$date(YYYYmmDD)##}
            scriptName = 
                text.substring( colonDollarIndex + 2, openParamIndex );
            
            //get everything AFTER the OPEN TAG but before the ':'
            //
            // {##date:$date(format=YYYYmmDD)##}
            //    date
            String beforeParams = null;
            
            beforeParams = text.substring( 
                OPEN_TAG.length(), 
                colonDollarIndex );
                        
            name = beforeParams;
            try
            {
                nameAudit.audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name 
                  + "\" for DefineStaticVarAsScriptResult on line ["
                  + lineNumber + "]",  
                  text, 
                  lineNumber );
            }

            // {##theDate:$date(YYYYmmDD)##}
            String theParams = text.substring( 
                openParamIndex + 1, 
                text.length() - CLOSE_TAG.length() );
            
            return new DefineVarAsScriptResult.StaticVar( 
                text, 
                lineNumber,
                name,
                scriptName,
                theParams );
        }
    }
    
    public static class DefineInstanceVarAsScriptResultMark
    {
        public static final String OPEN_TAG = "{#";
        
        public static final String CLOSE_TAG = "#}";
        
        public static DefineVarAsScriptResult.InstanceVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {            
            String name = "";
            String scriptName = "";
            
            // {#theDate:$date(YYYYmmDD)#}
            if( text == null )
            {
                throw new MarkupException( 
                    "Text for DefineInstanceVarAsScriptResult Mark cannot be null on line [" 
                  + lineNumber + "]",
                  text, 
                  lineNumber );
            }
            if( ! ( text.startsWith( OPEN_TAG ) ) )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsScriptResult Mark must start with \"" + OPEN_TAG 
                   + "\" value : "+ N + text + N + " is invalid",
                   text, 
                   lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsScriptResult Mark must end with \""+ CLOSE_TAG 
                  + "\"" +" value : \"" + text + "\" is invalid",
                   text, 
                   lineNumber );
            }
            int colonDollarIndex = text.indexOf( ":$" );
            if( colonDollarIndex < 0 )
            {
            	colonDollarIndex = text.indexOf( "=$" );
            	if( colonDollarIndex < 0  )
            	{
            		throw new MarkupException(
            			"DefineInstanceVarAsScriptResult Mark must contain "
            		 + "\":$\" or \"=$\" to represent end of name and start "
            		 + " of script name",
                    text, 
                    lineNumber );
            	}
            }            
            // {#theDate:$date(YYYYmmDD)#}
            int openParamIndex = text.indexOf( '(' );
            if( openParamIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsScriptResult must have \"(\"" 
                  + " separating the script name from the argument list \""
                  + text + "\" is invalid",
                  text, 
                  lineNumber );
            }
            int closeParamIndex = text.lastIndexOf( ')' );
            if( closeParamIndex < openParamIndex )
            {
            	throw new MarkupException( 
                        "DefineInstanceVarAsScriptResult must have ')' " 
                      + " separating the script name from the argument list \""
                      + text + "\" is invalid",
                      text, 
                      lineNumber );
            }
            // {#theDate:$date(YYYYmmDD)#}
            scriptName = 
                text.substring( colonDollarIndex + 2, openParamIndex );
            
            //get everything AFTER the OPEN TAG but before the ':'
            //
            // {#theDate:$date(YYYYmmDD)#}
            //   theDate
            String beforeParams = null;
            
            beforeParams = text.substring( 
                OPEN_TAG.length(), 
                colonDollarIndex );
                        
            name = beforeParams;
            try
            {
                nameAudit.audit( name );
            }
            catch( MarkupException cme )
            {
                throw new MarkupException(
                    "Invalid name \"" + name 
                  + "\" for DefineInstanceVarAsScriptResult on line ["
                  + lineNumber + "]",  
                  text, 
                  lineNumber );
            }

            // {#theDate:$date(YYYYmmDD)#}
            String theParams = text.substring( 
                openParamIndex + 1, 
                closeParamIndex );
            
            boolean isRequired = false;
            
            if( charIs( text, closeParamIndex + 1, '*' ) ) 
            {
            	isRequired = true;
            }
            		
            return new DefineVarAsScriptResult.InstanceVar( 
                text, 
                lineNumber,
                name,
                scriptName,
                theParams,
                isRequired );
        }
    }
    
    public static class CutMark
    {
        public static final String OPEN_TAG = "{-";

        public static final String CLOSE_TAG = "-}";

        public static Cut of( String text, int lineNumber )
        {
            if( !( text.startsWith( OPEN_TAG ) 
                && text.endsWith( CLOSE_TAG ) ) )
            {
                throw new MarkupException( 
                    "Invalid Cut Mark:" + N +  text + N 
                    + " on line [" + lineNumber + "] "
                    + "Cut must start with \"" + OPEN_TAG 
                    + "\" and end with \"" + CLOSE_TAG + "\"",
                    text, 
                    lineNumber );
            }
            
            String internalText = 
                text.substring( 5, text.length() - CLOSE_TAG.length() );

            return new Cut( text, lineNumber, internalText.substring( 1 ) );
        }
    }
   
    public static class EvalExpressionMark
    {
    	public static final String OPEN_TAG = "{((";
        
        public static final String CLOSE_TAG = "))}";
        
        public static EvalExpression of( String markText, int lineNumber )
        {
        	if( ! markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "EvalExpression Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber);
            }
            
            if( ! markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "EvalExpression Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not end with \"" + CLOSE_TAG + "\"",
                    markText, lineNumber);
            }
            
            String expressionText = markText.substring(
            	OPEN_TAG.length(), markText.length() - CLOSE_TAG.length() );
            
            return new EvalExpression( markText, lineNumber, expressionText );
        }
    }
    
    // {$scriptName(input)$}
    public static class EvalScriptMark
    {
        public static final String OPEN_TAG = "{$";
        
        public static final String CLOSE_TAG = "$}";
        
        public static RunScript of( String markText, int lineNumber )
        {
            String scriptName = null;
            String input = null;
            
            if( ! markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber);
            }
            
            if( ! markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not end with \"" + CLOSE_TAG + "\"",
                    markText, lineNumber);
            }
            int indexOfOpenParen = markText.indexOf( '(' );
            if( indexOfOpenParen < 0 )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not contain a requisite '(' ",
                    markText, lineNumber);
            }
            int indexOfCloseParen = markText.indexOf( ')', indexOfOpenParen );
            if( indexOfCloseParen < 0 )
            {
                throw new MarkupException(
                    "EvalScript Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not contain a requisite ')' ",
                    markText, lineNumber);
            }
            scriptName = markText.substring( OPEN_TAG.length(), indexOfOpenParen );
            
            input = markText.substring( indexOfOpenParen + 1, indexOfCloseParen );
            
            boolean isRequired = false;
            if( charIs( markText, indexOfCloseParen + 1, '*' ) ) 
            {
            	isRequired = true;
            }
            return new RunScript( markText, lineNumber, scriptName, input, isRequired );
        }
    }
    
    // {@version=1.03@}
    public static class SetMetadataMark
    {
        public static final String OPEN_TAG = "{@";
        
        public static final String CLOSE_TAG = "@}";
        
        public static final SetMetadata of( String text, int lineNumber )
        {
            if( ! text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( 
                    "SetMetadata mark:" + N + text + N +" does not start with \""
                   + OPEN_TAG + "\" on line [" + lineNumber + "]", text, lineNumber );                    
            }
            if( ! text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "SetMetadataMark: " + N +  text + N  
                   + " does not end with \"" + CLOSE_TAG + "\" on line [" 
                   + lineNumber + "]", text, lineNumber );                    
            }
            String theMetadata = text.substring( 
                OPEN_TAG.length(), 
                text.length() - CLOSE_TAG.length() );
            
            int colonIndex = theMetadata.indexOf( ':' );
            if( colonIndex < 0 )
            {
            	colonIndex = theMetadata.indexOf( '=' );
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"SetMetadata mark:" + N + text + N + " on line [" 
            		  + lineNumber + "] does not contain a ':' or '=' separating "
            		  + "the name from the content", 
                        text, lineNumber );
            	}
            }
            String name = theMetadata.substring( 0, colonIndex ).trim();
            String metadata = theMetadata.substring( colonIndex + 1 );
            //Map<String,String> props = Text.Tokenize.stringMap( theMetadata );
            return new SetMetadata( text, lineNumber, name, metadata ); 
        }        
    }
    
    /*{$$directive()$$}*/
    /*{$$directive$$}*/
    public static class TailorDirectiveMark
    {
    	public static final String OPEN_TAG = "{$$";
        
        public static final String CLOSE_TAG = "$$}";
        
        public static DocDirective of( 
            String text, int lineNumber )
        {
        	if( ! text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException( 
                    "TailorDirective mark:" + N + text + N +" does not start with \""
                   + OPEN_TAG + "\" on line [" + lineNumber + "]", text, lineNumber );                    
            }
            if( ! text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException( 
                    "TailorDirective mark: " + N +  text + N  
                   + " does not end with \"" + CLOSE_TAG + "\" on line [" 
                   + lineNumber + "]", text, lineNumber );                    
            }
            /*{$$directive$$*/
            /*{$$directive()$$}*/
            /*{$$                
                 directive()$$}*/   
            /*
            int openParenIndex = text.indexOf( '(' );
            if( openParenIndex < 0 )
            {
            	throw new MarkupException( 
                    "TailorDirective mark: " + N +  text + N  
                  + " does not contain '(' to delineate parameters on line [" 
                  + lineNumber + "]", text, lineNumber );               
            }
            int closeParenIndex = text.indexOf( ')', openParenIndex );
            if( closeParenIndex < 0 )
            {
            	throw new MarkupException( 
                    "TailorDirective mark: " + N +  text + N  
                  + " does not contain ')' to delineate parameters on line [" 
                  + lineNumber + "]", text, lineNumber );               
            }
            */
            String tag = text.substring( OPEN_TAG.length(), text.length() - CLOSE_TAG.length() );
            if( tag .endsWith("()") )
            {
            	tag = tag.substring(0, tag.length() -2 );
            }
            
            String directiveName = tag; //text.substring( OPEN_TAG.length(), openParenIndex );
            //String input = text.substring( openParenIndex + 1, closeParenIndex );
            //boolean isRequired = false;
            //if( charIs( text, closeParenIndex + 1, '*' ) )
            //{
            //	isRequired = true;
            //}
            /*
            try
            {
                nameAudit.audit( directiveName );
            }
            catch( Exception e )
            {
                throw new MarkupException( 
                    "Invalid TailorDirectiveMark : "+ N + text + N + 
                    " the directive name \""+ directiveName + "\" on line [" + lineNumber 
                    + "] is invalid", 
                  text, 
                  lineNumber, 
                  e );
            }
            */
            return new DocDirective( text, lineNumber, directiveName ); //, input, isRequired );
        }
    }
}
