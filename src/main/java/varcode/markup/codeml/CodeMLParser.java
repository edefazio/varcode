package varcode.markup.codeml;

import java.util.HashMap;
import java.util.Map;

import varcode.VarException;
import varcode.context.VarContext;
import varcode.context.eval.Eval_JavaScript;
import varcode.doc.form.Form;
import varcode.markup.MarkupException;
import varcode.markup.MarkupParser;
import varcode.markup.VarNameAudit;
import varcode.markup.bindml.BindMLParser;
import varcode.markup.forml.ForMLCompiler;
import varcode.markup.mark.AddExpressionResult;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.AddFormIfExpression;
import varcode.markup.mark.AddFormIfVar;
import varcode.markup.mark.AddScriptResult;
import varcode.markup.mark.AddScriptResultIfExpression;
import varcode.markup.mark.AddIfVar;
import varcode.markup.mark.AddVarIsExpression;
import varcode.markup.mark.AddVarOneOf;
import varcode.markup.mark.Cut;
import varcode.markup.mark.CutComment;
import varcode.markup.mark.CutIfExpression;
import varcode.markup.mark.CutIfVarIs;
import varcode.markup.mark.CutJavaDoc;
import varcode.markup.mark.DefineVar;
import varcode.markup.mark.DefineVarAsExpressionResult;
import varcode.markup.mark.DefineVarAsForm;
import varcode.markup.mark.DefineVarAsScriptResult;
import varcode.markup.mark.EvalExpression;
import varcode.markup.mark.RunScript;
import varcode.markup.mark.Mark;
import varcode.markup.mark.ReplaceWithExpressionResult;
import varcode.markup.mark.ReplaceWithForm;
import varcode.markup.mark.ReplaceWithScriptResult;
import varcode.markup.mark.ReplaceWithVar;
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
public class CodeMLParser
    implements MarkupParser
{   
    public static final ForMLCompiler FORM_COMPILER = 
        ForMLCompiler.INSTANCE;
    
    public static final CodeMLParser INSTANCE = 
       new CodeMLParser();
    
    public static final String N = "\r\n";
       
    private CodeMLParser()
    { }
    
    
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
        return CodeMLParser.parseMark( markText ); 
    }

    public Mark parseMark(
        VarContext context,
        String markText, 
        int lineNumber )
        throws MarkupException
    {
        return CodeMLParser.parseMark( 
            context, 
            markText, 
            lineNumber, 
            FORM_COMPILER ); 
    }
    
    
    public static String firstOpenTag( String line )
    {
        int openBraceIndex = line.indexOf( '{' );
        
                                      //the open brace cant be the last char on the line
        while( openBraceIndex >= 0 && openBraceIndex < line.length() -1 )
        {        
            //if the NEXT character AFTER the '{' is a '+', '#', '-', '@' or '{'        
            switch( line.charAt( openBraceIndex + 1 ) )
            {
                case '+' : // "{+"
                       // "/*{+"
                    if( ( openBraceIndex >= 2 ) 
                        && ( line.substring(
                            openBraceIndex - 2, 
                            openBraceIndex ).equals( "/*" ) ) )
                    {
                        return "/*{+";
                    }
                    return "{+"; //this is still a mark (an InlineAdd Mark) stop searching
                case '#' : //{#
                           //   /*{#
                	       //   /*{#$                
                	       //{##
                	       //   /*{##          
                	if( charIs( line, openBraceIndex +2, '#' ) )
                	{
                		return "/*{##";
                	}
                    if( openBraceIndex >= 2 )
                    {
                        String previous2 = line.substring( 
                            openBraceIndex -2, 
                            openBraceIndex ); // /*{#
                        if( previous2.equals( "/*" ) )
                        {
                            return "/*{#";
                        }                   
                    }
                    if( openBraceIndex >= 3 )
                    {
                        String previous3 = line.substring( 
                            openBraceIndex -3, 
                            openBraceIndex ); // /**{#
                        if( previous3.equals( "/**" ) )
                        {
                            return "/**{#";
                        }                   
                    }
                    break; //need to search further in the line for other marks
                case '-' :  //{-
                        //    /*{-
                        //    /**{-
                        //    /*{-?( 
                    if( openBraceIndex >=2 )
                    {
                        String previous2 = line.substring( 
                            openBraceIndex -2, 
                            openBraceIndex ); // /*{#
                        if( previous2.equals( "/*" ) ) // "/*{-"
                        {
                            if( charIs(line, openBraceIndex + 2, '?' ) ) // "/*{-?"
                            {
                            	return "/*{-?";
                                //return "/*{-?(";
                                
                                
                                
                            }
                            return "/*{-";
                        }                   
                    }
                    if( openBraceIndex >= 3 )
                    {
                        String previous3 = line.substring( 
                            openBraceIndex -3, 
                            openBraceIndex ); // /**{#
                        if( previous3.equals( "/**" ) )
                        {
                            return "/**{-";
                        }                   
                    }
                    break; //need to search further in the line for other marks
                case '@' :
                       // "/**{@"
                	   // "/*{@"    
                    if( charIs( line, openBraceIndex - 1, '*' ) )
                    {
                    	if(( charIs( line, openBraceIndex - 2, '*' ) ) &&
                    		 charIs( line, openBraceIndex - 3, '/' ) )                        	
                    	{
                    		return "/**{@";
                    	}
                    	if( charIs( line, openBraceIndex - 2, '/' ) )
                    	{
                    		return "/*{@";
                    	}
                    }
                    break; //need to search further in the line for other marks
                case '$' : //"/*{$"
                   if( openBraceIndex >=2 )
                   {
                       String previous2 = line.substring( 
                           openBraceIndex -2, 
                           openBraceIndex ); // /*{$
                       if( previous2.equals( "/*" ) )
                       {
                           return "/*{$";
                       }                  
                   }
                   break;//MED ADDED
                   
                case '{' : // "{{"
                           //     "/*{{+"  
                           //     "/*{{#"
                           //     "/*{{##"
                	       //      
                    if( openBraceIndex >=2 )
                    {
                        String previous2 = line.substring( 
                            openBraceIndex -2, 
                            openBraceIndex ); // /*{{
                        if( previous2.equals( "/*" ) )
                        {
                            char interior = line.charAt( openBraceIndex + 2 );  
                            if( interior == '+' )
                            {
                                return "/*{{+";
                            }
                            if( interior == '#' )
                            {
                            	if( charIs( line, openBraceIndex +3, '#' ) )
                            	{
                            		return "/*{{##";
                            	}
                                return "/*{{#";
                            }                        
                        }                    
                    }
                    if( openBraceIndex >= 3 && openBraceIndex < line.length() - 2 )
                    {
                        String previous3 = line.substring( 
                            openBraceIndex -3, 
                            openBraceIndex ); // /**{{
                        if( previous3.equals( "/**" ) && 
                            line.charAt( openBraceIndex + 2 ) == '#' ) // "/**{{#"
                        {
                            return "/**{{#";
                        }
                    }
                    break;
                case '(' : // "{(("
                    String p2 = line.substring( 
                        openBraceIndex -2, 
                        openBraceIndex ); // /*{{
                    if( p2.equals( "/*" ) 
                    	&& ( charIs( line, openBraceIndex + 2, '(' ) ) )
                         // "/*{(("
                     {
                         return "/*{((";
                     }
                	 break;
                case '_' : // "{_"
                    //     "/*{_+"  
                    //     "/*{_#"
                    //     "/**{_#"
                	if( openBraceIndex >=2 )
                	{
                		String previous2 = line.substring( 
                			openBraceIndex -2, 
                			openBraceIndex ); // /*{{
                		if( previous2.equals( "/*" ) )
                		{
                			char interior = line.charAt( openBraceIndex + 2 );  
                			if( interior == '+' )
                			{
                				return "/*{_+";
                			}
                			if( interior == '#' )
                			{
                				return "/*{_#";
                			}                        
                		}                    
                	}
                	if( openBraceIndex >= 3 && openBraceIndex < line.length() - 2 )
                	{
                		String previous3 = line.substring( 
                			openBraceIndex -3, 
                			openBraceIndex ); // /**{{
                		if( previous3.equals( "/**" ) && 
                			line.charAt( openBraceIndex + 2 ) == '#' ) // "/**{_#"
                		{
                			return "/**{_#";
                		}
                	}
                	break;                    
            	}
            openBraceIndex = line.indexOf( '{', openBraceIndex + 1 );
        }
        return null;
    }
     
    private static final Map<String, String> OPENTAG_TO_CLOSETAG = 
        new HashMap<String, String>();

    static
    {   
        OPENTAG_TO_CLOSETAG.put(    "{+",     "+}"    ); //AddInline        
        OPENTAG_TO_CLOSETAG.put(  "/*{+",     "+}*/"  ); //Add, Replace, ReplaceWithExpressionResult 
        
        OPENTAG_TO_CLOSETAG.put(  "/*{+$",    "+}*/"  ); //AddScriptResult, ReplaceWithScript
        OPENTAG_TO_CLOSETAG.put(  "/*{+?",    "+}*/"  ); //Add --> AddIf
        
         
        OPENTAG_TO_CLOSETAG.put( "/*{{+",    "+}}*/" ); //ReplaceWithForm, AddForm
        OPENTAG_TO_CLOSETAG.put( "/*{{+?",   "+}}*/" ); //AddFormIf
        
        OPENTAG_TO_CLOSETAG.put( "/*{_+",    "+_}*/" ); //ReplaceWithForm, AddForm
        OPENTAG_TO_CLOSETAG.put( "/*{_+?",   "+_}*/" ); //AddFormIf
        

        OPENTAG_TO_CLOSETAG.put(  "/*{#",      "#}*/"  ); //DefineVar, DefineVarAsScriptResult (Instance)
        OPENTAG_TO_CLOSETAG.put(  "/*{$$",    "$$}*/"  ); //TailorDirective
        
        OPENTAG_TO_CLOSETAG.put( "/*{{#",     "#}}*/" ); //DefineForm (Instance)
        OPENTAG_TO_CLOSETAG.put( "/*{{##",   "##}}*/" ); //DefineForm (Static)
        
        OPENTAG_TO_CLOSETAG.put( "/*{_#",     "#_}*/" ); //DefineForm (Instance)
        OPENTAG_TO_CLOSETAG.put( "/*{_##",   "##_}*/" ); //DefineForm (Static)
        
        
        OPENTAG_TO_CLOSETAG.put( "/*{#",       "#}*/"  ); //DefineVar, DefineVarAsScriptResult  (Static)
        OPENTAG_TO_CLOSETAG.put( "/*{##",     "##}*/"  );
        
        OPENTAG_TO_CLOSETAG.put(  "/*{$",      "$}*/" ); //EvalScriptMark
        OPENTAG_TO_CLOSETAG.put(  "/*{((",      "))}*/" ); //EvalExpressionMark
        
        OPENTAG_TO_CLOSETAG.put(  "/*{-",      "-}*/"  ); //CutComment
        OPENTAG_TO_CLOSETAG.put(  "/*{-*/",  "/*-}*/"  ); //Cut

        OPENTAG_TO_CLOSETAG.put(  "/*{-?",  "/*-}*/" ); //CutIf
          
        OPENTAG_TO_CLOSETAG.put( "/**{-",      "-}*/"  ); //CutJavaDoc
        
        OPENTAG_TO_CLOSETAG.put( "/*{@",       "@}*/"  ); //SetMetadata
        OPENTAG_TO_CLOSETAG.put( "/**{@",      "@}*/"  ); //SetMetadata
        
    }
    
    public static String matchCloseTag( String openTag )
    {
        return OPENTAG_TO_CLOSETAG.get( openTag );
    }

    
    public static boolean charIs( String string, int index, char expect )
    {
        return  string != null  
                && string.length() > index 
                && index > -1
                && string.charAt( index ) == expect; 
    }
    
    
    // "/*{+",     "}*/"  //AddVar, ReplaceWithVar         
    // "/*{+$",    "}*/"  //AddScriptResult, ReplaceWithScriptResult
    // "/*{+?",    "}*/"   //AddIfVar
    // "/*{+(("  "))}*/" //AddExpressionResult   
    
    // "/*{{+",    "}}*/" ); //ReplaceWithForm, AddForm(TBD)
    // "/*{{+",  "/*}}*/" ); //ReplaceWithForm
    // "/*{{+?",   "}}*/" ); //AddFormIf
    // "/*{-",     "}*/"  ); //CutComment
    // "/*{-*/", "/*}*/"  ); //Cut
    // "/*{-?(", "/*}*/"  ); //CutIf
    
    // "/*{#",     "}*/"  ); //DefineInstanceVar, 
                             // DefineInstanceVarAsScriptResult, 
                             // DefineInstanceVarAsExpressionResult
    // "/*{$$",   "$$}*/" ); //TailorDirective
    // "/*{{#",   "}}*/" ); //DefineForm (Instance)
    // 
    // "/*{##",  "##}*/"  ); //DefineVar  (Static)
    
    // "/**{-",   "-}*/"  //CutJavaDoc    
    // "/**{@",    "}*/"  //SetMetadata
    // "/*{$"     "$}*/"  //EvalScript
    // "/*{(("   "))}*/"  //EvalExpression
    
    public static Mark parseMark( String text )
    {
        return parseMark( 
            VarContext.of(), 
            text, 
            -1, 
            FORM_COMPILER );
    }
    
    /** 
     * Given Text return the appropriate Mark
     * 
     * NOTE: I could make this more efficient, but it's fine 
     *
     * @param parseContext context for parsing
     * @param text the text of the entire mark
     * @param lineNumber the line number where the mark appears
     * @param compiler the forMLCompiler
     * @throws MarkupException if the Mark is invalid
     */
    public static Mark parseMark(
        VarContext parseContext,
        String text, 
        int lineNumber,
        ForMLCompiler compiler )
        throws MarkupException
    {
    	VarNameAudit nameAudit = parseContext.getVarNameAudit();
    	
        if( text.startsWith( "/*{" ) )
        {
            // "/*{+",      "+}*/"   //AddVar, ReplaceWithVar         
            // "/*{+$",     "+}*/"   //AddScriptResult, ReplaceWithScript
            // "/*{+?",     "+}*/"   //AddIfVar
            // "/*{{+",     "+}}*/"  //ReplaceWithForm, AddForm
            // "/*{{+",   "/*+}}*/"  //ReplaceWithForm
            // "/*{{+?",    "+}}*/"  //AddFormIf
        	
        	// "/*{@"        @}*/"  //SetMetadata
        	
        	// "/*{$",    ")$}*/"    //RunScript
        	// "/*{((",    "))}*/"   //EvalExpression
        	// "/*{$$",    "$$}*/"  //TailorDirective
        	
        	// "/*{_+",     "_}*/"  //ReplaceWithForm, AddForm
            // "/*{_+",   "/*_}*/"  //ReplaceWithForm
            // "/*{_+?",    "_}*/"  //AddFormIf
        	
            // "/*{-",      "-}*/"    //CutComment
            // "/*{-*/",  "/*-}*/"    //Cut
            // "/*{-?(",  "/*-}*/"    //CutIf
            // "/*{#",      "#}*/"    //DefineVar, DefineVarAsScriptResult
        	
        	// "/*{#"     "))#}*/"    //DefineVarAsExpressionResult.Instance 
        	
            // "/*{{#",    "#}}*/" ); //DefineVarAsForm (Instance)
        	// "/*{_#",    "#_}*/" ); //DefineVarAsForm (Instance)
        	
        	// "/*{_#",     "_}*/" ); //DefineForm (Instance)
            
            //---------------------------------------
            // "/*{+"      "}*/"  ); //AddVar, ReplaceWithVar
            // "/*{+$",    "}*/"  ); //AddScriptResult, ReplaceWithScript
            // "/*{+?",    "}*/"  ); //AddIfVar
            if( charIs( text, 3, '+' ) )
            {
                if( charIs( text, 4, '$' ) )
                {   // "/*{+$",    "}*/"  ); //AddScriptResult, ReplaceWithScript
                    if( text.endsWith( ReplaceWithScriptResultMark.CLOSE_TAG ) //      "/*)+}*/" 
                    	|| text.endsWith( ReplaceWithScriptResultMark.CLOSE_TAG_2 ) ) //"/*)*+}*/" (REQURIED)
                    {   
                        return ReplaceWithScriptResultMark.of( 
                            text, lineNumber, nameAudit );
                    }
                    return AddScriptResultMark.of( text, lineNumber );
                }
                if( charIs( text, 4, '?' ) )
                {   //"/*{+?",    "}*/"  ); //AddIf
                	if( charIs( text, 5, '(' ) )
                	{
                		//"/*{+?(( expression )):$scriptName(params)+}*/"
                		return AddScriptResultIfExpressionMark.of( text, lineNumber );
                	}
                	return AddIfVarMark.of( text, lineNumber, nameAudit );
                }
                if( text.endsWith( "/*+}*/" ) )
                {
                	if( text.contains( "))*/" ) )
                	{   //   /*{+(( (a + b / 2) |0 ))*/ 37 /*+}*/ ReplaceWithExpressionResult
                		return ReplaceWithExpressionResultMark.of( 
                			text, lineNumber );
                	}
                    return ReplaceWithVarMark.of( text, lineNumber, nameAudit );
                }
                if( charIs( text,  4, '(' ) && charIs( text, 5, '(' ) ) // "/*{+((
                {   
                	
                    //   /*{+(( (a + b / 2) |0 ))+} //AddExpressionResult
                	return AddExpressionResultMark.of( text, lineNumber );
                }
                if( text.contains( ":[" ) )
                {
                	return AddVarOneOfMark.of( parseContext, text, lineNumber );
                }
                return AddVarExpressionMark.of( text, lineNumber, nameAudit );
            }
            
            //----------------------------
            // "/*{{+",     "}}*/" ); //AddForm
            // "/*{{+",   "/*}}*/" ); //ReplaceWithForm
            // "/*{{+?",    "}}*/" ); //AddFormIf
            if( text.substring( 3, 5 ).equals( "{+" ) )
            {
                if( charIs( text, 5, '?' ) )
                {
                	if( charIs( text, 6, '(' ) )
                	{
                		return AddFormIfExpressionMark.of( 
                			parseContext, text, lineNumber );
                	}
                    return AddFormIfVarMark.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        FORM_COMPILER  );
                }
                if( text.endsWith( ReplaceWithFormMark.CLOSE_TAG ) )
                {
                    return ReplaceWithFormMark.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        FORM_COMPILER );
                }
                //Add
                return AddFormMark.of( 
                    parseContext, text, lineNumber, FORM_COMPILER );
            }
            
            //---------------------------- ADDED
            // "/*{_+",     "_}*/" ); //AddForm
            // "/*{_+",   "/*_}*/" ); //ReplaceWithForm
            // "/*{_+?",    "_}*/" ); //AddFormIf
            if( text.substring( 3, 5 ).equals( "_+" ) )
            {
                if( charIs( text, 5, '?' ) )
                {
                	if( charIs (text, 6, '(' ) ) 
                	{
                		return AddFormIfExpressionMark_Alt.of( 
                			parseContext, text, lineNumber );
                	}
                    return AddFormIfVarMark_Alt.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        FORM_COMPILER );
                }
                if( text.endsWith( ReplaceWithFormMark_Alt.CLOSE_TAG ) )
                {
                    return ReplaceWithFormMark_Alt.of( 
                        parseContext, 
                        text, 
                        lineNumber, 
                        FORM_COMPILER );
                }
                //Add
                return AddFormMark_Alt.of( 
                    parseContext, text, lineNumber, FORM_COMPILER );
            }
            
            //----------------------------
            // "/*{-",     "-}*/"  ); //CutComment
            // "/*{-*/", "/*-}*/"  ); //Cut
            // "/*{-?     "/*-}*/" ); //CutIfVarIs
            // "/*{-?((", "/*-}*/"  ); //CutIfExpression
            if( charIs( text, 3, '-' ) )
            {
                if( text.endsWith( CutMark.CLOSE_TAG ) )
                {
                    if( charIs( text, 4, '?' ) )
                    {
                    	
                    	if( charIs(text, 5, '(' ) )
                    	{
                    		return CutIfExpressionMark.of( text, lineNumber );
                    	}
                    	return CutIfVarIsMark.of( text,  lineNumber, nameAudit );                    	
                    }
                    return CutMark.of( text, lineNumber );
                }
                return CutCommentMark.of( text, lineNumber );
            }
            
            //--------------------------------
            // "/*{#",     "#}*/"  // DefineVar Instance
            // "/*{#",    ")#}*/"  // DefineVarAsScriptResult
            // "/*{#",    "))#}*/"  // DefineVarAsExpressionResult
            
            
            // "/*{##",     "}*/"  // DefineVar (Static)
            // "/*{##",    ")#}*/"  // DefineVarAsScriptResult (Static)
            // "/*{##",    "))#}*/"  // DefineVarAsExpressionResult (Static)
            if( charIs( text, 3, '#' ) )
            {
            	if( charIs( text, 4, '#' ) )
            	{
            		if( text.endsWith( "))##}*/" ) 
            			&& ( text.contains( ":((" ) ) || ( text.contains( "=((" ) ) )
            		{
            			return DefineStaticVarAsExpressionResult.of( text, lineNumber, nameAudit );
            		}
            		if( text.endsWith( ")##}*/" ) )
            		{
            			return DefineStaticVarAsScriptResultMark.of( text, lineNumber, nameAudit );
            		}
            		return DefineStaticVarMark.of( text, lineNumber, nameAudit );
            	}
            	if( text.endsWith( "))#}*/" ) 
            			&& ( text.contains( ":((" ) ) || ( text.contains( "=((" ) ) )
            	{
            		return DefineInstanceVarAsExpressionResult.of( text, lineNumber, nameAudit );
            	}
            	if( text.endsWith( ")#}*/" ) || text.endsWith( ")*#}*/" ) )
            	{            		
            		return DefineInstanceVarAsScriptResultMark.of( 
                            text, lineNumber, nameAudit );
            	}                
                return DefineInstanceVarMark.of( 
                    text, lineNumber, nameAudit );
            }
            
            //--------------------------------
            // "/*{$",     ")}*/"  // ScriptMark
            // "/*{$print(*)}*/"
            if( charIs( text, 3, '$' ) )
            {
            	if( charIs( text, 4, '$' ) ) //  /*{$$directive()$$}*/ 
        		{
        			return TailorDirectiveMark.of( text, lineNumber, nameAudit );
        		}
                if( text.endsWith( EvalScriptMark.CLOSE_TAG ) )
                {
                    return EvalScriptMark.of( 
                        text, lineNumber, nameAudit );
                }
            }
            
            //--------------------------------
            // "/*{{#",     "#}}*/" ); //DefineVarAsForm (Instance)
            
            // "/*{{##",     "##}}*/" ); //DefineVarAsForm (Static)
            if( text.substring( 3, 5 ).equals( "{#" ) )
            {
            	if( charIs( text, 5, '#') )
            	{
            		return DefineStaticVarAsFormMark.of( 
                            parseContext,
                            text, 
                            lineNumber,  
                            FORM_COMPILER );
            	}
                return DefineInstanceVarAsFormMark.of( 
                    parseContext,
                    text, 
                    lineNumber,  
                    FORM_COMPILER );
            }
            
            //--------------------------------
            // "/*{_#",     "_}*/" ); //DefineVarAsForm (Instance)
            if( text.substring( 3, 5 ).equals( "_#" ) )
            {
            	if( charIs( text, 5, '#') )
            	{
            		return DefineStaticVarAsFormMark_Alt.of( 
                            parseContext,
                            text, 
                            lineNumber,  
                            FORM_COMPILER );
            	}
                return DefineInstanceVarAsFormMark_Alt.of( 
                    parseContext,
                    text, 
                    lineNumber,  
                    FORM_COMPILER  );
            } 
            // "/*{@",     "}*/"  ); //SetMetadata
            if( charIs( text, 3, '@' ) )
            {
                return SetMetadataMark_Alt.of( text, lineNumber );
            }
            
            if( charIs( text, 3, '(' ) && charIs( text, 4, '(' ) )
            {
                return EvalExpressionMark.of( text, lineNumber );
            }
        }
        
        if( text.startsWith( "/**{" ) )
        {         
            // "/**{-",     "}*/"  ); //CutJavaDoc
            if( charIs( text, 4, '-' ) )
            {
                return CutJavaDocMark.of( text, lineNumber );
            }
            
            // "/**{@",     "}*/"  ); //SetMetadata
            if( charIs( text, 4, '@' ) )
            {
                return SetMetadataMark.of( text, lineNumber );
            }            
        }
        if( text.startsWith( "{+" ) )
        {
            if( charIs( text, 2, '$' ) )
            {
                return AddScriptResultInlineMark.of( text, lineNumber, nameAudit );
            }
            if( charIs(text, 2, '(' ) )
            {
            	return AddExpressionResultInlineMark.of( text, lineNumber );
            }
            return AddVarInlineMark.of( text, lineNumber, nameAudit );
        }
        throw new MarkupException(
            "Could not find Open Tag matching Mark :" + N + text + N 
            +"on line [" + lineNumber + "]", text, lineNumber );
    }
    
    // /*{+name:(( expression  ))|default+}*/
    public static class AddVarExpressionMark
    {
    	 public static final String OPEN_TAG = "/*{+";
         public static final String CLOSE_TAG = "+}*/";
         
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
         
         // /*{+name:(( expression  ))+}*/
         // /*{+name:(( expression  ))*+}*/
         // /*{+name:(( expression  ))|default+}*/
         
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
             
             // /*{+name:(( expression  ))+}*/
             //     ----------------------      tag
             // /*{+name:(( expression  ))*+}*/
             //     -----------------------     tag
             
             // /*{+name:(( expression  ))|default+} */
             //     ------------------------------  tag
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
    
    // "/*{+vowel:['a','e','i','o','u']+}*/"
    // "/*{+vowel:['a','e','i','o','u']|'e'+}*/"
    // "/*{+vowel:['a','e','i','o','u']*+}*/"
    public static class AddVarOneOfMark
    {	
    	public static final String OPEN_TAG = "/*{+";
    	
    	public static final String CLOSE_TAG = "+}*/";
    	
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
             if( openIndexOfArr < 5 )
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
             // "/*{+vowel:['a','e','i','o','u']+}*/"
             //  ----                                 OPEN_TAG
             //      -----                            varName 
             //           --                          open arr 
             //             -------------------       arr 
             //                                -      close arr
             //                                 ----  CLOSE_TAG
             
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
             }
             // "{+vowel:['a','e','i','o','u']|'e'+}"
             // "{+vowel:['a','e','i','o','u']*+}"
             return new AddVarOneOf( text, lineNumber, varName, array, arr, isRequired, defaultValue );
    	}
    }
    
    public static class AddVarInlineMark
    {
        public static final String OPEN_TAG = "{+";
        
        public static final String CLOSE_TAG = "+}";
        
        public static AddVarIsExpression of( 
            String text, int lineNumber, VarNameAudit nameAudit )
            throws MarkupException
        {
        	return BindMLParser.AddVarExpressionMark.of( text, lineNumber, nameAudit );
        }
    }
    
    public static class AddFormMark
    {
        /** Opening mark for a AddForm */
        public static final String OPEN_TAG = "/*{{+:";
        
        /** Closing mark for a AddForm */
        public static final String CLOSE_TAG = "+}}*/";
        
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
            if( formText.endsWith( "*" ) )
            {
            	isRequired = true;
            	formText = formText.substring( 0, formText.length() -1 ); 
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
        public static final String OPEN_TAG = "/*{_+:";
        
        /** Closing mark for a AddForm */
        public static final String CLOSE_TAG = "_}*/";
        
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
                return new AddForm( text, lineNumber, form );
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
    
    /** An "alternative" tag AddFormIfMark */
    public static class AddFormIfVarMark_Alt
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "/*{_+?";
        
        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "_}*/";
        

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
    
 // "/*{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}*/" 
    public static class AddFormIfExpressionMark
    {
    	/** Open tag for a AddFormIfExpression */
        public static final String OPEN_TAG = "/*{{+?((";
        
        public static final String SEPARATOR = ")):";
        
        /** Close tag for a AddFormIfExpression */
        public static final String CLOSE_TAG = "+}}*/";
    	
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
            // "/*{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}*/"
            //  --------                                                    OPEN_TAG
            //          ------------------                                  expression   
            //                            ---                               SEPARATOR
            //                               -------------------------      formText
            //                                                        ----- CLOSE_TAG
            
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
        public static final String OPEN_TAG = "/*{_+?((";
        
        public static final String SEPARATOR = ")):";
        
        /** Close tag for a AddFormIfExpression */
        public static final String CLOSE_TAG = "+_}*/";
    	
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
            // "/*{{+?(( logLevel > debug )):LOG.debug({+a+} + {+b+});+}}*/"
            //  --------                                                    OPEN_TAG
            //          ------------------                                  expression   
            //                            ---                               SEPARATOR
            //                               -------------------------      formText
            //                                                        ----- CLOSE_TAG
            
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
    
    public static class AddFormIfVarMark
    {
        /** Opening mark for a IfAddForm */
        public static final String OPEN_TAG = "/*{{+?";
        
        /** Closing mark for a IfAddForm */
        public static final String CLOSE_TAG = "}}*/";

        
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
    
    
    
    public static class AddIfVarMark
    {
        /** Opening mark for a AddIf */
        public static final String OPEN_TAG = "/*{+?";
        
        /** Closing mark for a AddIf */
        public static final String CLOSE_TAG = "}*/";

        //public static AddTextIfVar of( String text, int lineNumber )
        //{
        //    return of( text, lineNumber, VarNameAudit.BASE );
        //}
        
        public static AddIfVar of( 
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
            return new AddIfVar( text, lineNumber, name, targetValue, code );
        }
    }
    
    public static class AddExpressionResultMark
    {
    	public static final String OPEN_TAG = "/*{+((";  
        public static final String CLOSE_TAG = "))+}*/";
        
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
    
    public static class AddScriptResultIfExpressionMark
    {
    	/** Opening mark for a IfExpressionAddScriptResultMark */
        public static final String OPEN_TAG = "/*{+?((";
        
        /** Closing mark for a IfExpressionAddScriptResultMark */
        public static final String CLOSE_TAG = ")+}*/";
        
        // "/*{+?(( expression )):$scriptName(params)+}*/"
        public static final String EXPRESSION_SCRIPT_SEPARATOR = ")):$";
        
        // "/*{+?(( expression )):$scriptName(params)+}*/"
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
             // "/*{+?(( expression )):$scriptName(params)+}*/"
             //  -------                                        OPEN TAG
             //         ------------                            expression
             //                     ----                        SEPARATOR
             //                         ----------              scriptName
             //                                   _             (
             //                                    ------       params
             //                                          -----  CLOSE_TAG              
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
            String expression = text.substring(OPEN_TAG.length(), text.length() - CLOSE_TAG.length() );
            
            return new AddExpressionResult( text, lineNumber, expression );
        }
    }
    
    /**
     * Results in the same as a AddScriptResult, but signified differently
     * (inline)
     */
    public static class AddScriptResultInlineMark
    {
        public static final String OPEN_TAG = "{+$";  
        public static final String CLOSE_TAG = "+}";
        
        public static AddScriptResult of( 
            String text, 
            int lineNumber, 
            VarNameAudit nameAudit )
            throws MarkupException
        {
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultInlineMark : " + N
                    + text + N
                    + "  ... on line [" + lineNumber + "] must start with "
                    + " \"" + OPEN_TAG + "\" ",
                    text, 
                    lineNumber );                
            }
            if( !text.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "Invalid AddScriptResultInlineMark : " + N 
                    + text + N
                    + "  ... on line [" + lineNumber + "] must end with CLOSE MARK"
                    + " \"" + CLOSE_TAG + "\" ",
                    text, 
                    lineNumber );   
            }
            int openParamIndex = text.indexOf( '(' );
            int closeParamIndex = text.lastIndexOf( ')' );

            // "{+$javascript(paramName=value, a, 100)}"            
            String scriptName = text.substring( OPEN_TAG.length(), openParamIndex );
            // parameters = null; 
        if( openParamIndex < 0  ||  closeParamIndex < 0 )
        {
            throw new MarkupException(
                "AddScriptResultMark with name \"" + scriptName 
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
            isRequired );
        }
    }
    
    public static class AddScriptResultMark
    {
        public static final String OPEN_TAG = "/*{+$";  
        public static final String CLOSE_TAG = "}*/"; 
        
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

            // "/*{+$count(fields)}*/"
            // "/*{+$count(fields)*}*/" (REQUIRED)
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
            
            // "/*{+$count(fields)*}*/" (REQUIRED)
            boolean isRequired = false;
            if( charIs( text, closeParamIndex +1, '*' ) )
            {
            	isRequired = true;
            }
            
            return new AddScriptResult( 
                text, 
                lineNumber, 
                scriptName, 
                paramContents,
                isRequired );            
        }
    }
    
    public static class DefineStaticVarAsExpressionResult
    {
    	public static final String OPEN_TAG = "/*{##";
        
        public static final String CLOSE_TAG = "))##}*/";
        
        public static DefineVarAsExpressionResult.StaticVar of( String text, int lineNumber, VarNameAudit nameAudit )
        {            
            // /*{##c:((a + b))##}*/
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
            // /*{##c:((a + b))##}*/
            int colonIndex = text.indexOf( ":((" );
            if( colonIndex < 0 )
            {
            	// {##c=((a + b))##}
            	colonIndex = text.indexOf( "=((" );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineStaticVarAsExpressionResult : " + N + text + N + " must have \":((\"" 
            		  + " or '=((' separating the name from the expression",
            		  	text, 
            		  	lineNumber );
            	}
            }
            // {##c:((a + b))##}
            //    ^
            String name = text.substring( OPEN_TAG.length(), colonIndex );
            
            // {##c:((a + b))##}
            //        ^^^^^
            String expression = text.substring( colonIndex + 3, text.length() - CLOSE_TAG.length() );
            
            return new DefineVarAsExpressionResult.StaticVar( text, lineNumber, name, expression );        	
        }
    }
    
    public static class DefineStaticVarAsFormMark
    {   
        public static final String OPEN_TAG = "/*{{##";
        
        public static final String CLOSE_TAG = "##}}*/";
        
        public static DefineVarAsForm.StaticVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            /*{{#params...*:{+fieldName}, }}*/
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
            /*{{#params...*:{+fieldName}, }}*/
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsFormMark : " + N + text + N + " must have \":\"" 
                  + " separating the name from the code form",
                  text, 
                  lineNumber );
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //   /*{{#params...*:{+fieldName}, }}*/
            //        params...*
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
            //   /*{{#params...*:{+fieldName}, }}*/
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
    
    public static class DefineStaticVarAsFormMark_Alt
    {   
        public static final String OPEN_TAG = "/*{_##";
        
        public static final String CLOSE_TAG = "##_}*/";
        
        public static DefineVarAsForm.StaticVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            /*{_#params...*:{+fieldName}, _}*/
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
            /*{_#params...*:{+fieldName}, _}*/
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineStaticVarAsFormMark : " + N + text + N + " must have \":\"" 
                  + " separating the name from the code form",
                  text, 
                  lineNumber );
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //   /*{_#params...*:{+fieldName}, _}*/
            //        params...*
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
            //   /*{_#params...*:{+fieldName}, _}*/
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
    	public static final String OPEN_TAG = "/*{#";
        
        public static final String CLOSE_TAG = "))#}*/";
        
        public static DefineVarAsExpressionResult.InstanceVar of( 
        	String text, int lineNumber, VarNameAudit nameAudit )
        {            
            // /*{#c:((a + b))#}*/
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
                    "DefineInstanceVarAsExpressionResult must end with \""+ CLOSE_TAG + "\"" 
                   +" mark :" + N + text + N + "\" is invalid",
                   text, 
                   lineNumber );
            }
            // /*{#c:((a + b))#}*/
            int colonIndex = text.indexOf( ":((" );
            if( colonIndex < 0 )
            {
            	// /*{#c=((a + b))#}*/
            	colonIndex = text.indexOf( "=((" );
            	
            	if( colonIndex < 0 )
            	{
            		throw new MarkupException( 
            			"DefineInstanceVarAsExpressionResult : " + N + text + N + " must have \":((\"" 
            		  + " or '=((' separating the name from the expression",
            		  	text, 
            		  	lineNumber );
            	}
            }
            // /*{#c=((a + b))#}*/
            //     ^
            String name = text.substring( OPEN_TAG.length(), colonIndex );
            
             // /*{#c=((a + b))#}*/
            //          ^^^^^
            String expression = text.substring( colonIndex + 3, text.length() - CLOSE_TAG.length() );
            
            return new DefineVarAsExpressionResult.InstanceVar( text, lineNumber, name, expression );        	
        }
    }
    
    public static class DefineInstanceVarAsFormMark
    {
        public static final String OPEN_TAG = "/*{{#";
        
        public static final String CLOSE_TAG = "#}}*/";
        
        public static DefineVarAsForm.InstanceVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            /*{{#params...*:{+fieldName}, }}*/
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
            /*{{#params...*:{+fieldName}, }}*/
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm : " + N + text + N 
                  + " must have \":\" separating the name from the code form",
                  text, 
                  lineNumber );
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //   /*{{#params...*:{+fieldName}, }}*/
            //        params...*
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

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "}}*/
            //   /*{{#params:{+fieldName}, }}*/
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
                    lineNumber, cme );
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
        public static final String OPEN_TAG = "/*{_#";
        
        public static final String CLOSE_TAG = "_}*/";
        
        public static DefineVarAsForm.InstanceVar of(
            VarContext parseContext,
            String text, 
            int lineNumber,
            ForMLCompiler formCompiler )
        {
            String name = null;
            boolean isRequired = false;
            Form codeForm = null;
            
            /*{{#params...*:{+fieldName}, }}*/
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
            /*{_#params...*:{+fieldName}, _}*/
            int colonIndex = text.indexOf( ':' );
            if( colonIndex < 0 )
            {
                throw new MarkupException( 
                    "DefineInstanceVarAsForm : " + N + text + N 
                  + " must have \":\" separating the name from the code form",
                  text, 
                  lineNumber );
            }
            String beforeColon = null;
            //get everything AFTER the OPEN TAG but before the ':'
            //
            //   /*{_#params...*:{+fieldName}, _}*/
            //        params...*
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

            //get the CodeForm, AFTER the ':' and before the CLOSE_TAG "}}*/
            //   /*{_#params...*:{+fieldName}, _}*/
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
                    lineNumber, cme );
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
        public static final String OPEN_TAG = "/*{#";
        
        public static final String CLOSE_TAG = "#}*/";
        
        public static DefineVar.InstanceVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {   
            /*{#theDate:$date(format=YYYYmmDD)}*/
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
        public static final String OPEN_TAG = "/*{##";
        
        public static final String CLOSE_TAG = "##}*/";
        
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
        public static final String OPEN_TAG = "/*{##";
        
        public static final String CLOSE_TAG = ")##}*/";
        
        public static DefineVarAsScriptResult.StaticVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {            
            String name = "";
            String scriptName = "";
            
            /**{#theDate:$date(format=YYYYmmDD)}*/
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
                throw new MarkupException(
                    "DefineStaticVarAsScriptResult Mark must contain \":$\" to "
                  + "represent end of name and start of script name",
                    text, 
                    lineNumber );
            }
            
            /**{#theDate:$date(format=YYYYmmDD)}*/
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
            /**{#theDate:$date(format=YYYYmmDD)}*/
            scriptName = 
                text.substring( colonDollarIndex + 2, openParamIndex );
            
            //get everything AFTER the OPEN TAG but before the ':'
            //
            // /**{#date:$date(format=YYYYmmDD)}*/
            //     params...*
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

            // /*{#date:$date(format=YYYYmmDD)}*/
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
        public static final String OPEN_TAG = "/*{#";
        
        public static final String CLOSE_TAG = "#}*/";
        
        public static DefineVarAsScriptResult.InstanceVar of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit )
        {            
            String name = "";
            String scriptName = "";
            
            /*{#theDate:$date(format=YYYYmmDD)}*/
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
            	if( colonDollarIndex < 0 )
            	{
            		throw new MarkupException(
            			"DefineInstanceVarAsScriptResult Mark : "+ N 
            		   + text + N
            		   + "on line [" + lineNumber + "]" + N
            		   + "must contain \":$\" to "
            		   + "represent end of name and start of script name",
            		  	text, 
            		  	lineNumber );
            	}
            }
            
            /*{#theDate:$date(format=YYYYmmDD)}*/
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
                  + " signifying end of argument list \""
                  + text + "\" is invalid",
                  text, 
                  lineNumber );
            }
            
            /*{#theDate:$date(format=YYYYmmDD)}*/
            scriptName = 
                text.substring( colonDollarIndex + 2, openParamIndex );
            
            //get everything AFTER the OPEN TAG but before the ':'
            //
            // /*{#date:$date(format=YYYYmmDD)}*/
            //     params...*
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
            boolean isRequired = false;
            if( charIs(text, closeParamIndex + 1, '*' ) )
            {
            	isRequired = true;
            }
            // /*{#date:$date(format=YYYYmmDD)}*/
            String theParams = text.substring( 
                openParamIndex + 1, 
                closeParamIndex  );
            
            return new DefineVarAsScriptResult.InstanceVar( 
                text, 
                lineNumber,
                name,
                scriptName,
                theParams,
                isRequired );
        }
    }
    
    /**
     * 
     *
     */
    // /*{-?removeLog==true:*/LOG.debug( "input is" + input );/*-}*/
    // -----                                                          OPEN_TAG
    //      ---------------                                           condition
    //      ---------                                                 varName
    //               --                                               operator 
    //                 ----                                           targetValue
    //                     ---                                        SEPARATOR_TAG
    //                        --------------------------------        conditionalText 
    //                                                        ------  CLOSE_TAG 
    public static class CutIfVarIsMark
    {
    	 public static final String OPEN_TAG = "/*{-?";
         
         public static final String SEPARATOR_TAG =  ":*/";
         
         public static final String CLOSE_TAG = "/*-}*/";
         
         public static CutIfVarIs of( String text, int lineNumber, VarNameAudit nameAudit )
         {
        	 if( !( text.startsWith( OPEN_TAG ) 
                     && text.endsWith( CLOSE_TAG ) ) )
             {
        		 throw new MarkupException( 
                   "Invalid CutIfVarIsMark Mark:" + N +  text + N 
                      + " on line [" + lineNumber + "] "
                      + "CutIfVarIsMark Mark must start with \"" + OPEN_TAG 
                      + "\" and end with \"" + CLOSE_TAG + "\"",
                      text, 
                      lineNumber );
             }
             int midIndex = text.indexOf( SEPARATOR_TAG );
             if( midIndex < 0 )
             {
            	 throw new MarkupException( 
                    "Invalid CutIfVarIsMark Mark:" + N +  text + N 
                    + " on line [" + lineNumber + "] "
                    + "CutIfVarIsMark must contain \"" + SEPARATOR_TAG + "\"",
                    text, 
                    lineNumber );
             }
             String condition = text.substring( 
                OPEN_TAG.length(),
            	midIndex );
             
             String conditionalText = 
                text.substring( midIndex + SEPARATOR_TAG.length(), text.length() - CLOSE_TAG.length() );
             
             System.out.println("THE CONDITON IS" + condition );
             String varName = condition;
             String targetValue = null;
             
             int sepIndex = condition.indexOf( "==" );
             if( sepIndex > 0 )
             {
            	 varName = condition.substring( 0, sepIndex );
            	 targetValue = condition.substring( sepIndex + 2 );
             }
             else 
             {
                sepIndex  = condition.indexOf( '=' );
                if( sepIndex > 0 )
                {
                	varName = condition.substring( 0, sepIndex );
               	 	targetValue = condition.substring( sepIndex + 1 );
                }
                else
                {
                	sepIndex  = condition.indexOf( ':' );
                    if( sepIndex > 0 )
                    {
                    	varName = condition.substring( 0, sepIndex );
                   	 	targetValue = condition.substring( sepIndex + 1 );
                    }                	
                }
             }
             try
             { 
            	 nameAudit.audit( varName );
             }
             catch( Exception e )
             {
            	 throw new MarkupException(
                         "Invalid CutIfVarIs VarName \"" + varName + "\", on line [" 
                       + lineNumber + "]", 
                       text, 
                       lineNumber, e );
             }
             return new CutIfVarIs(text, lineNumber, varName, targetValue, conditionalText );
         }     
    }
    
     //"/*{-?((" + CONDITION + ")):*/" + CONTENT + "/*}*/";
    public static class CutIfExpressionMark
    {     
        public static final String OPEN_TAG = "/*{-?((";
        
        public static final String MID_TAG =  ")):*/";
        
        public static final String CLOSE_TAG = "/*-}*/";
        
        public static CutIfExpression of( String text, int lineNumber )
        {
            if( !( text.startsWith( OPEN_TAG ) 
                && text.endsWith( CLOSE_TAG ) ) )
            {
                throw new MarkupException( 
                   "Invalid CutTextIfExpression Mark:" + N +  text + N 
                 + " on line [" + lineNumber + "] "
                 + "CutIf Mark must start with \"" + OPEN_TAG 
                 + "\" and end with \"" + CLOSE_TAG + "\"",
                 text, 
                 lineNumber );
            }
            int midIndex = text.indexOf( MID_TAG );
            
            if( midIndex < 0 )
            {
                throw new MarkupException( 
                    "Invalid CutTextIfExpression Mark:" + N +  text + N 
                  + " on line [" + lineNumber + "] "
                  + "CutIf Mark must contain MidTag : \"" + MID_TAG 
                  + "\" AFTER open tag \"" + OPEN_TAG + " and BEFORE clsoe tag \"" 
                  + CLOSE_TAG + "\"",
                  text, 
                  lineNumber );
            }            
            // "/*{-?((" + EXPRESSION + ")):*/" + CONTENT + "/*}*/";
            String condition = text.substring( OPEN_TAG.length(), midIndex );
            String content = text.substring( 
                midIndex + MID_TAG.length( ), 
                text.length() - CLOSE_TAG.length() );
            
            return new CutIfExpression( 
                text, 
                lineNumber, 
                condition, 
                content );   
        }
    }
    public static class CutMark
    {
        public static final String OPEN_TAG = "/*{-*/";

        public static final String CLOSE_TAG = "/*-}*/";

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
    
    public static class CutCommentMark
    {
        public static final String OPEN_TAG = "/*{-";
        
        public static final String CLOSE_TAG = "-}*/";
        
        /**
         * @param text
         * @param lineNumber
         * @return
         */
        public static CutComment of( String text, int lineNumber )
        {
            if( !text.startsWith( OPEN_TAG ) ) 
            {
                throw new MarkupException(
                    "Invalid CutComment Mark" + N 
                    + text + N 
                    + "CutComment must start with \""+ OPEN_TAG + "\"",
                    text, 
                    lineNumber );
            }
            if( !text.endsWith( CLOSE_TAG ) )    
            {
                throw new MarkupException(
                    "Invalid CutComment " + N 
                    + text + N 
                    + "CutComment must end with \"" + CLOSE_TAG +"\"", 
                    text, 
                    lineNumber ); 
            }
            String internalText = text.substring( 
                OPEN_TAG.length(), text.length() - CLOSE_TAG.length() 
            );
            return new CutComment( text, lineNumber, internalText );
        }
    }
    
    public static class CutJavaDocMark
    {
        public static final String OPEN_TAG = "/**{-";
        
        public static final String CLOSE_TAG = "-}*/";
        
        private static String extractContent( String text ) 
        {
            return text.substring( 
                OPEN_TAG.length(), 
                text.length() - CLOSE_TAG.length() );
        }
        
        public static CutJavaDoc of( String text, int lineNumber )
        {            
            if( !( text.startsWith( OPEN_TAG ) 
                &&  text.endsWith( CLOSE_TAG ) ) )    
            {
                throw new MarkupException(
                    "Invalid CutJavaDoc:" + N 
                    + text + N
                    + " ...on line [" + lineNumber + "]" + N
                    + "CutJavaDoc must start with \""+ OPEN_TAG 
                    + "\" and end with \"" + CLOSE_TAG +"\"",
                    text, 
                    lineNumber ); 
            }
            
            String internalText = text.substring( 
                OPEN_TAG.length(), text.length() - CLOSE_TAG.length() 
            );
            
            int internalStartIndex = internalText.indexOf( "/*{" );
            int internalEndIndex = internalText.indexOf( "}*/" );
            if( internalStartIndex != -1 || internalEndIndex != -1 )
            {
                throw new MarkupException(
                    "Invalid CutJavaDoc:" +N
                    + text + N
                    + " ...on line [" + lineNumber + "]" + N
                    + "the data in the CutJavaDoc " + N 
                    + "\"" + internalText + "\"" + N 
                    + "must NOT CONTAIN \"/*{\" OR \"}*/\" ",
                    text, 
                    lineNumber );
            }
            return new CutJavaDoc( text, lineNumber, extractContent( text ) );
        }
    }
    
    public static class ReplaceWithExpressionResultMark
    {
        /** signifies start of the Mark */
        public static final String OPEN_TAG = "/*{+((";
        
        /** anything after this tag and before the*/ 
        public static final String EXPRESSION_CLOSE_TAG = "))*/";
        
        /** this marks the end of the mark */
        public static final String CLOSE_TAG = "/*+}*/";
        
        public static ReplaceWithExpressionResult of( 
            String text, 
            int lineNumber )
        {
        	// /*{+((3 + 4))*/20/*+}*/ 
            // ------                   OPEN_TAG
        	//       ^^^^^              expression
        	//            ----          EXPRESSION_CLOSE_TAG
        	//                ^^        wrappedContent
        	//                  ------  CLOSE_TAG
            String expression = null;
            String wrappedContents = null;
            
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "ReplaceWithExpressionResult does not start with OPEN TAG \"" 
                   + OPEN_TAG + "\"",
                  text, 
                  lineNumber );
            }
            
            if( !text.endsWith( CLOSE_TAG ) )
            {
            	throw new MarkupException(
                    "ReplaceWithExpressionResult does not end with CLOSE TAG \"" 
                   + CLOSE_TAG + "\"",
                  text, 
                  lineNumber );
            }
            
            int expressionCloseIndex = text.indexOf( EXPRESSION_CLOSE_TAG );
            if( expressionCloseIndex < 0 )
            {
                throw new MarkupException(
                   "ReplaceWithExpressionResult does not contain Expression Close Tag \"" 
                  + EXPRESSION_CLOSE_TAG + "\"",
                  text, 
                  lineNumber );
            }
            
        	// /*{+((3 + 4))*/20/*+}*/ 
            // ------                   OPEN_TAG
        	//       ^^^^^              expression
        	//            ----          EXPRESSION_CLOSE_TAG
        	//                ^^        wrappedContent
        	//                  ------  CLOSE_TAG
            expression = text.substring( OPEN_TAG.length(), expressionCloseIndex );
            
            wrappedContents = 
            	text.substring( 
            		expressionCloseIndex + EXPRESSION_CLOSE_TAG.length(),   
            		text.length() - CLOSE_TAG.length() );
            
            	
            return new ReplaceWithExpressionResult(
                text, 
                lineNumber, 
                expression,
                wrappedContents );
        }        
    }
    
    public static class ReplaceWithScriptResultMark
    {
        /** signifies start of the Mark */
        public static final String OPEN_TAG = "/*{+$";
        
        /** anything after this tag and before the*/ 
        public static final String REPLACE_OPEN_TAG = "(*/";
        
        /** this marks the end of data being replaced */ 
        public static final String REPLACE_CLOSE_TAG = "/*"; //changed from /*) to /*
        
        /** this marks the end of the mark */
        public static final String CLOSE_TAG = "/*)+}*/";
        
        /** this marks the end of the mark */
        public static final String CLOSE_TAG_2 = "/*)*+}*/";
        
        public static ReplaceWithScriptResult of( 
            String text, 
            int lineNumber,
            VarNameAudit nameAudit)
        {
            /** i.e. "accept" code that represents fields, 
             *  and create getters and setters*/
            /*{+$getters(*/
            //  int anInt;
            //  private String someName; 
            /*)}*/
            
            /*{$tab(*/
            //  a bunch of code
            //  that uses tabs, but could be replaced based on the Environment 
            //  Settings for what a "tab" is 
            /*,sep=1,b=3)}*/
            
            String name = null;
            String wrappedContents = null;
            
            if( !text.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "ReplaceWithScriptResult does not start with OPEN TAG \"" 
                   + OPEN_TAG + "\"",
                  text, 
                  lineNumber );
            }
            boolean isRequired = false;
            
            if( text.endsWith( CLOSE_TAG ) || text.endsWith( CLOSE_TAG_2 ) )
            {
            	if( text.endsWith( CLOSE_TAG_2 ) )
            	{
            		isRequired = true;
            	}
            }
            else
            {
                throw new MarkupException(
                    "ReplaceWithScriptResult does not end with CLOSE TAG \"" 
                   + CLOSE_TAG + "\" or \"" + CLOSE_TAG_2 + "\"",
                  text, 
                  lineNumber );
            }
            
            int replaceOpenIndex = text.indexOf( REPLACE_OPEN_TAG );
            if( replaceOpenIndex < 0 )
            {
                throw new MarkupException(
                   "ReplaceWithScriptResult does not contain Replace Open Tag \"" 
                  + REPLACE_OPEN_TAG + "\"",
                  text, 
                  lineNumber );
            }
            int replaceCloseIndex = text.lastIndexOf( REPLACE_CLOSE_TAG );
            if( replaceCloseIndex < 0 )
            {
                throw new MarkupException(
                    "ReplaceWithScriptResult must have \"" + REPLACE_OPEN_TAG + "\""
                  + " tag BEFORE \"" + REPLACE_CLOSE_TAG + "\" tag",
                  text, 
                  lineNumber );
            }                        
            name = text.substring( OPEN_TAG.length(), replaceOpenIndex );
            try
            {
                nameAudit.audit( name );
            }
            catch( Exception e )
            {
                throw new MarkupException(
                    "Invalid Name \"" + name 
                  + "\" for ReplaceWithScriptResult mark on ["
                  + lineNumber + "]", 
                  text, 
                  lineNumber, 
                  e );
            }        
            if( text.endsWith( CLOSE_TAG ) ) 
            {
            	wrappedContents = 
            		text.substring( 
            			replaceOpenIndex + REPLACE_OPEN_TAG.length(),   
            			text.length() - CLOSE_TAG.length() );
            }
            else
            {
            	wrappedContents = 
                		text.substring( 
                			replaceOpenIndex + REPLACE_OPEN_TAG.length(),   
                			text.length() - CLOSE_TAG_2.length() );
            }
            	
            return new ReplaceWithScriptResult(
                text, 
                lineNumber, 
                name,
                wrappedContents,
                isRequired);
        }        
    }
    
    /*{(( 3 + 4 ))}*/ 
    public static class EvalExpressionMark
    {
    	public static final String OPEN_TAG = "/*{((";
        
        public static final String CLOSE_TAG = "))}*/";
        
        public static EvalExpression of(
           String markText, 
           int lineNumber )
        {
            if( ! markText.startsWith( OPEN_TAG ) )
            {
            	throw new MarkupException(
            		"EvalExpressionMark on line [" + lineNumber + "] :" + N
                   + markText + N 
                   + "... does not start with \"" + OPEN_TAG + "\"",
                     markText, lineNumber);
            }
            
            if( ! markText.endsWith( CLOSE_TAG ) )
            {
            	throw new MarkupException(
            		"EvalExpressionMark on line [" + lineNumber + "] :" + N
                   + markText + N 
                   + "... does not end with \"" + CLOSE_TAG + "\"",
                     markText, lineNumber);
            }
            String expression = 
            	markText.substring( OPEN_TAG.length(), markText.length() - CLOSE_TAG.length() );
            
            return new EvalExpression( markText, lineNumber, expression );
        }
    }
    
    /*{$scriptName(input)*/
    public static class EvalScriptMark
    {
        public static final String OPEN_TAG = "/*{$";
        
        public static final String CLOSE_TAG = "$}*/";
        
        public static RunScript of(
            //VarContext parseContext,
            String markText, 
            int lineNumber,
            VarNameAudit nameAudit )
        {
            String scriptName = null;
            String input = null;
            
            if( ! markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "ScriptMark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber);
            }
            
            if( ! markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                    "ScriptMark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not end with \"" + CLOSE_TAG + "\"",
                    markText, lineNumber);
            }
            int indexOfOpenParen = markText.indexOf( '(' );
            if( indexOfOpenParen < 0 )
            {
                throw new MarkupException(
                    "ScriptMark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not contain a requisite '(' ",
                    markText, lineNumber);
            }
            
            int indexOfCloseParen = markText.indexOf( ')', indexOfOpenParen );
            if( indexOfCloseParen < 0 )
            {
                throw new MarkupException(
                    "ScriptMark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not contain a requisite ')' ",
                    markText, lineNumber);
            }
            
            scriptName = markText.substring( OPEN_TAG.length(), indexOfOpenParen );
            //input = markText.substring( indexOfOpenParen + 1, markText.length()- CLOSE_TAG.length() );
            input = markText.substring( indexOfOpenParen + 1, indexOfCloseParen );
            
            boolean isRequired = false;
            if( charIs( markText, indexOfCloseParen + 1, '*' ) )
            {
            	isRequired = true;
            }
            return new RunScript( markText, lineNumber, scriptName, input, isRequired );
        }
    }
    
    public static class ReplaceWithFormMark
    {
        public static final String OPEN_TAG = "/*{{+";
        
        public static final String CLOSE_TAG = "/*+}}*/";
        
        public static ReplaceWithForm of(
            VarContext parseContext,
            String markText, 
            int lineNumber, 
            ForMLCompiler formCompiler )
        {
            String name = null;
            String wrappedContent = null;
            boolean isRequired = false;
            
            if( !markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber);
            }
            if( !markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                   "ReplaceWithForm Mark on line [" + lineNumber + "] :" + N 
                   + markText + N 
                   + "... does not end with \"" + CLOSE_TAG + "\"", 
                   markText, lineNumber );
            }
            int firstCloseIndex = markText.indexOf( "*/" );
            
            // the first "*/" is the last "*/" (we are missing "close form" Tag)
            if( firstCloseIndex == markText.lastIndexOf( "*/" ) )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + "] is missing " 
                  + "intermediate \"Close Form\" tag \"*/\" between the END of " 
                  + "the form declaration, and start of the code to be replaced ",
                  markText, lineNumber);
            }
            wrappedContent = markText.substring( 
                firstCloseIndex + 3, 
                markText.length() - ( CLOSE_TAG.length() + 1 ) );
            
 
            int colonIndex = markText.indexOf( ':' );
            if( colonIndex == -1 )
            {
                throw new MarkupException( 
                    "ReplaceWithForm Mark on line [" + lineNumber 
                    + "] must have ':' separating name " 
                    + "from Form" + markText,
                    markText, lineNumber );
            }       
            String theName =  
                markText.substring( OPEN_TAG.length(), colonIndex ).trim();
            
            if( theName.endsWith( "*" ) )
            {
                name = theName.substring( 0, theName.length() - 1 );
                isRequired = true;
            }
            else
            {
                name = theName;
                isRequired = false;
            }       
            if( name.length() != 0)
            {
                try
                {
                	parseContext.getVarNameAudit().audit( name );
                }
                catch( Exception e )
                {
                    throw new MarkupException(
                        "ReplaceWithForm Mark on line [" + lineNumber + 
                        "] has invalid name \"" + name+ "\"", 
                        markText, 
                        lineNumber, 
                        e ); 
                }
            }
            else
            {   //just assign an anonymous name
                name = "_";
            }
            
            String formText = markText.substring( 
                colonIndex + 1,
                markText.indexOf( "*/" ) );
            //remove prefix control characters
            char[] chars = formText.toCharArray();
            int startIndex = 0;
            
            //remove the carriage return AFTER : before the form
            for( int i = 0; i < 2; i++ )
            {
                if ( ( chars[ i ] == '\n' ) || ( chars[ i ] )== '\r' )
                {
                    startIndex++;
                }
            }
            
            formText =  
                    formText.substring( startIndex );            
            try
            {
                Form form = formCompiler.fromString(
                    parseContext,
                    lineNumber, 
                    name,           
                    formText );
                
                return new ReplaceWithForm( 
                    markText, 
                    lineNumber,
                    name,
                    form,
                    isRequired,
                    wrappedContent );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + 
                    "] with form :" + N + formText + N
                  + "is invalid", 
                    markText, 
                    lineNumber, 
                    t );
            }            
        }
    }
    
    public static class ReplaceWithFormMark_Alt
    {
        public static final String OPEN_TAG = "/*{_+";
        
        public static final String CLOSE_TAG = "/*+_}*/";
        
        public static ReplaceWithForm of(
            VarContext parseContext,
            String markText, 
            int lineNumber, 
            ForMLCompiler formCompiler )
        {
            String name = null;
            String wrappedContent = null;
            boolean isRequired = false;
            
            if( !markText.startsWith( OPEN_TAG ) )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + "] :" + N
                    + markText + N 
                    + "... does not start with \"" + OPEN_TAG + "\"",
                    markText, lineNumber);
            }
            if( !markText.endsWith( CLOSE_TAG ) )
            {
                throw new MarkupException(
                   "ReplaceWithForm Mark on line [" + lineNumber + "] :" + N 
                   + markText + N 
                   + "... does not end with \"" + CLOSE_TAG + "\"", 
                   markText, lineNumber );
            }
            int firstCloseIndex = markText.indexOf( "*/" );
            
            // the first "*/" is the last "*/" (we are missing "close form" Tag)
            if( firstCloseIndex == markText.lastIndexOf( "*/" ) )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + "] is missing " 
                  + "intermediate \"Close Form\" tag \"*/\" between the END of " 
                  + "the form declaration, and start of the code to be replaced ",
                  markText, lineNumber);
            }
            wrappedContent = markText.substring( 
                firstCloseIndex + 3, 
                markText.length() - ( CLOSE_TAG.length() + 1 ) );
            
 
            int colonIndex = markText.indexOf( ':' );
            if( colonIndex == -1 )
            {
                throw new MarkupException( 
                    "ReplaceWithForm Mark on line [" + lineNumber 
                    + "] must have ':' separating name " 
                    + "from Form" + markText,
                    markText, lineNumber );
            }       
            String theName =  
                markText.substring( OPEN_TAG.length(), colonIndex ).trim();
            
            if( theName.endsWith( "*" ) )
            {
                name = theName.substring( 0, theName.length() - 1 );
                isRequired = true;
            }
            else
            {
                name = theName;
                isRequired = false;
            }       
            if( name.length() != 0)
            {
                try
                {
                	parseContext.getVarNameAudit().audit( name );
                }
                catch( Exception e )
                {
                    throw new MarkupException(
                        "ReplaceWithForm Mark on line [" + lineNumber + 
                        "] has invalid name \"" + name+ "\"", 
                        markText, 
                        lineNumber, 
                        e ); 
                }
            }
            else
            {   //just assign an anonymous name
                name = "_";
            }
            
            String formText = markText.substring( 
                colonIndex + 1,
                markText.indexOf( "*/" ) );
            //remove prefix control characters
            char[] chars = formText.toCharArray();
            int startIndex = 0;
            
            //remove the carriage return AFTER : before the form
            for( int i = 0; i < 2; i++ )
            {
                if ( ( chars[ i ] == '\n' ) || ( chars[ i ] )== '\r' )
                {
                    startIndex++;
                }
            }
            
            formText =  
                    formText.substring( startIndex );            
            try
            {
                Form form = formCompiler.fromString(
                    parseContext,
                    lineNumber, 
                    name,           
                    formText );
                
                return new ReplaceWithForm( 
                    markText, 
                    lineNumber,
                    name,
                    form,
                    isRequired,
                    wrappedContent );
            }
            catch( Throwable t )
            {
                throw new MarkupException(
                    "ReplaceWithForm Mark on line [" + lineNumber + 
                    "] with form :" + N + formText + N
                  + "is invalid", 
                    markText, 
                    lineNumber, 
                    t );
            }            
        }
    }
    
    public static class SetMetadataMark_Alt
    {
        public static final String OPEN_TAG = "/*{@";
        
        public static final String CLOSE_TAG = "}*/";
        
        public static SetMetadata of( String text, int lineNumber )
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
                throw new MarkupException( 
                    "SetMetadata mark:" + N + text + N + " on line [" 
                    + lineNumber + "] does not contain a ':' separating the name from the content", 
                    text, lineNumber );
            }
            String name = theMetadata.substring( 0, colonIndex ).trim();
            String metadata = theMetadata.substring( colonIndex + 1 );
            //Map<String,String> props = Text.Tokenize.stringMap( theMetadata );
            return new SetMetadata( text, lineNumber, name, metadata ); 
        }        
    }
    
    public static class SetMetadataMark
    {
        public static final String OPEN_TAG = "/**{@";
        
        public static final String CLOSE_TAG = "}*/";
        
        public static SetMetadata of( String text, int lineNumber )
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
                throw new MarkupException( 
                    "SetMetadata mark:" + N + text + N + " on line [" 
                    + lineNumber + "] does not contain a ':' separating the name from the content", 
                    text, lineNumber );
            }
            String name = theMetadata.substring( 0, colonIndex ).trim();
            String metadata = theMetadata.substring( colonIndex + 1 );
            //Map<String,String> props = Text.Tokenize.stringMap( theMetadata );
            return new SetMetadata( text, lineNumber, name, metadata ); 
        }        
    }
    
    /*{$$directiveName$$}*/
    /*{$$directiveName()$$}*/ 
    public static class TailorDirectiveMark
    {
    	public static final String OPEN_TAG = "/*{$$";
        
        public static final String CLOSE_TAG = "$$}*/";
        
        public static DocDirective of( 
            String text, int lineNumber, VarNameAudit nameAudit )
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
            /*{$$directiveName$$}*/
            /*{$$directiveName()$$}*/
            /*{$$                
                 directiveName()$$}*/   
            String tag = text.substring( OPEN_TAG.length(), text.length() - CLOSE_TAG.length() );
            if( tag.endsWith("()") )
            {
            	tag = tag.substring(0, tag.length() -2 );
            }
            return new DocDirective( text, lineNumber, tag ); //, input, isRequired );
        }
    }
    
    
    /**
     * Creates and Returns a Replace Command from the Replacemark
     */    
    /*{+replace*/ /*some content*/ /*+}*/
    public static class ReplaceWithVarMark
    {
        public static final String OPEN_TAG = "/*{+";
        
        public static final String SEPARATOR_TAG = "*/";
        
        public static final String CLOSE_TAG = "/*+}*/";

       private static String getWrappedContent( String text ) 
       {
           return text.substring(
               text.indexOf( "*/" )+ "*/".length(),
               text.length() - CLOSE_TAG.length() );
        		   
       }
       
       public static ReplaceWithVar of( 
            String text, int lineNumber, VarNameAudit nameAudit )
        {
    	    if( !text.startsWith( OPEN_TAG ) )
    	    {
    	    	 throw new MarkupException( 
    	             "ReplaceWithVarMark mark:" + N + text + N +" does not start with \""
    	            + OPEN_TAG + "\" on line [" + lineNumber + "]", text, lineNumber );  
    	    }
    	    if( !text.endsWith( CLOSE_TAG ) )
    	    {
   	    	     throw new MarkupException( 
    	             "ReplaceWithVarMark mark:" + N + text + N +" does not end with \""
    	            + CLOSE_TAG + "\" on line [" + lineNumber + "]", text, lineNumber );  
    	    }
            //super( Text.replaceComment( markText ), lineNumber );
            String between = getWrappedContent( text );
            
            String wrappedContent = between;
            String defaultValue;
            boolean isRequired = false;
            
            
            String name = null;
            
            String tag = text.substring( OPEN_TAG.length(), 
                text.indexOf( SEPARATOR_TAG ) );
            
            if( tag.endsWith( "|" ) ) //there is a "default"
            {               
                // this means I want to "conditionally replace" the value 
                // where the default is wrapped in a tag
                name = tag.substring( 0, tag.length() -1 ); //everything except the '('
                if( name.endsWith( "*" ) )
                {
                    throw new MarkupException(
                        "ReplaceWithVar " + N 
                        + text + N 
                        + "on line [" + lineNumber + 
                        "] cannot have BOTH default '|' AND be required '*'",
                        text, 
                        lineNumber );
                }               
                defaultValue = between;
                isRequired = false;
            }
            else 
            {   //there is no default specified                         
                if( tag.endsWith( "*" ) )
                {
                    name = tag.substring( 0, tag.length() -1 );
                    isRequired = true;
                }
                else
                {
                    name = tag;
                    isRequired = false;
                }           
                defaultValue = null;
            }           
            try
            {
                nameAudit.audit( name );
                return new ReplaceWithVar( 
                    text, 
                    lineNumber, 
                    name,
                    wrappedContent,
                    defaultValue,
                    isRequired );
            }
            catch( Exception e )
            {
                throw new MarkupException( 
                    "Invalid ReplaceWithVar : " + N + text+ N + 
                    "Name \"" + name + "\", on line ["                    
                    + lineNumber + "]", text, lineNumber,  e );
            }
        }
    }
}
