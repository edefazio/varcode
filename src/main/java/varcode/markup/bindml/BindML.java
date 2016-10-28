package varcode.markup.bindml;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.DocState;
import varcode.doc.Dom;
import varcode.markup.bindml.BindMLParser.AddVarExpressionMark;
import varcode.markup.forml.ForML;
import varcode.markup.mark.Mark;
import varcode.markup.repo.MarkupRepo.MarkupStream;
/** 
 *  <A HREF="https://en.wikipedia.org/wiki/Markup_language">Markup Language</A> for 
 *  logically binding data into text to produce "tailored documents". 
 * 
 * Aggregates the components of the Bind Markup Language
 * (an implementations for producing {@code Markup})   
 * BindML expands upon ForML in being able to:
 *  
 * <UL>
 * <LI> define {# #} {##  ##} entities (that aren't immediately added to the document)
 * <LI> build and use expressions (( )) and call scripts {$ $} 
 * to have MORE logic to the document
 * <LI> define and embed FORMS {{ }} 
 * <LI> include metadata {@ @} about the document
 * </UL>
 * 
 * Whereas a ForML Markup document is immediate and straightforward 
 * (the purpose of all ForML marks is to adds variable text into a preset document).  
 * A BindML Markup document contains a layer of logic, for more complex document bindings
 * ( the Markup itself may define its own variables, derive and mutate input data,
 * scripts for validating the input, and manipulating the variables in the context 
 * BOTH when the Markup is being compiled (statically) and when the Markup 
 * is being Tailored (dynamically).   
 * 
 * Bind Markup Language supports the following Marks for Binding Text:
 * <UL>
 * <LI><CODE>"{+name+}"</CODE>  {@code AddVarExpression}
 * <LI><CODE>"{+name:(( name.length > 1 ))+}"</CODE>  {@code AddVarExpression}
 * <LI><CODE>"{+$script()+}"</CODE>   {@code AddScriptResult}
 * <LI><CODE>"{+((Math.PI * r * r))+}"</CODE>   {@code AddExpressionResult}
 * <LI><CODE>"{+?var:addThis+}"</CODE>  {@code AddIfVar}
 * <LI><CODE>"{{+:{+fieldType+} {+fieldName+}+}}"</CODE> {@code AddForm}
 * <LI><CODE>"{_+:{+fieldType+} {+fieldName+}+_}"</CODE> {@code AddForm}
 * <LI><CODE>"{{+?a==1: implements {+impl+}+}}"</CODE> {@code AddFormIfVar}
 * <LI><CODE>"{_+?a==1: implements {+impl+}+_}"</CODE> {@code AddFormIfVar}
 * <LI><CODE>"{- some text -}"</CODE>  {@code Cut}
 * <LI><CODE>"{#a=1#}"</CODE>  {@code DefineVar.DynamicVar}
 * <LI><CODE>"{#a:$count(a)#}"</CODE>  {@code DefineVarAsScriptResult.DynamicVar}
 * <LI><CODE>"{#$removeEmptyLines()#}"</CODE>  {@code TailorDirective}
 * <LI><CODE>"{##a=1##}"</CODE>  {@code DefineVar.StaticVar}
 * <LI><CODE>"{##a:$count(blah)##}"</CODE> {@code DefineVarAsScriptResult.StaticVar}
 * <LI><CODE>"{{#assign:{+fieldName+} = {+fieldValue+};#}}"</CODE> {@code DefineVarAsForm.DynamicVar}  
 * <LI><CODE>"{_#assign:{+fieldName+} = {+fieldValue+};#_}"</CODE> {@code DefineVarAsForm.DynamicVar} 
 * <LI><CODE>"{{##assign:{+fieldName+} = {+fieldValue+};##}}"</CODE> {@code DefineVarAsForm.StaticVar}
 * <LI><CODE>"{_##assign:{+fieldName+} = {+fieldValue+};##_}"</CODE> {@code DefineVarAsForm.StaticVar} 
 * <LI><CODE>"{{##className:IntFormOf{+count+}##}}"</CODE> {@code DefineVarAsForm.StaticVar}        	
 * <LI><CODE>"{_##className:IntFormOf{+count+}##_}"</CODE> {@code DefineVarAsForm.StaticVar}
 * <LI><CODE>"{$print(*)$}"</CODE>  {@code RunScript}		
 * <LI><CODE>"{@meta:data@}"</CODE> {@code SetMetadata}
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum BindML 
{		
	;
	
	/**
	 * Given a String that has BindML markup, compile it and return the Dom
	 * @param bindMLMarkup markup
	 * @return the Dom representation
	 */
	public static Dom compile( String bindMLMarkup )
	{
		return BindMLCompiler.fromString( bindMLMarkup );
	}
	
	public static Dom compile( MarkupStream markupStream )
	{
		return BindMLCompiler.fromMarkupStream( markupStream );
	}
	
	public static Mark parseMark( String markText )
	{
		return parseMark( VarContext.of( ) , markText );
	}

	public static Mark parseMark( VarContext context, String markText )
	{
		return parseMark( context, markText, -1 );
	}

	public static Mark parseMark(  
		VarContext context,
		String markText, 
		int lineNumber )
	{
		return BindMLParser.INSTANCE.parseMark( context, markText, lineNumber );
	}
	
	/**
	 * 
	 * @param bindMLMarkup text that contains BindML Marks
	 * @param keyValuePairs pairs of Key-values
	 * @return the result of compiling the {@code Dom} from the BindML
	 * and tailoring the result with the keyValuePairs
	 */
	public static String tailorCode( String bindMLMarkup, Object...keyValuePairs )
	{
		Dom dom = compile( bindMLMarkup );
		return Compose.asString( dom, keyValuePairs );
	}
	
	/**
	 * 
	 * @param bindMLMarkup text that contains BindML Marks
	 * @param keyValuePairs pairs of Key-values
	 * @return the result of compiling the {@code Dom} from the BindML
	 * and tailoring the result with the keyValuePairs
	 */
	public static DocState tailor( String bindMLMarkup, Object...keyValuePairs )
	{
		Dom dom = compile( bindMLMarkup );
		return Compose.toState( dom, VarContext.of( keyValuePairs ) );
	}
	
	public enum Marks
	{
		;		
		public static final String REQUIRED = "*";
		
		public static final String OR_DEFAULT = "|";

		
		/** <CODE>"{+varName+}"</CODE> */
		public static String addVar( String varName )
		{
			return ForML.Markup.addVar( varName ); 			 
		}
		
		/** <CODE>"{+varName*+}"</CODE> */
		public static String addVar( String varName, boolean isRequired )
		{
			return ForML.Markup.addVar( varName, isRequired ); 			
		}
		
		/** <CODE>"{+name|defaultValue+}"</CODE>  */
		public static String addVarWithDefault( String varName, String defaultValue )
		{
			return ForML.Markup.addVar( varName, defaultValue ); 			
		}
		
		/** <CODE>"{+name:(( name.length() > 2 ))|defaultValue+}"</CODE>  */
		public static String addVarWithValidation( String varName, String validationExpression )
		{
			return addVarWithValidation( varName, validationExpression, false );
		}
		
		public static String addVarWithValidation( String varName, String validationExpression, boolean isRequired )
		{
			String required = "";
			if( isRequired )
			{
				required = "*";
			}
			return 
				AddVarExpressionMark.OPEN_TAG +
				varName +
				AddVarExpressionMark.EXPRESSION_OPEN_TAG +
				" " +
				validationExpression +
				" " +
				AddVarExpressionMark.EXPRESSION_CLOSE_TAG +				
				required +
				AddVarExpressionMark.CLOSE_TAG;
		}
		
		public static String addVarWithValidationAndDefault( 
			String varName, String validationExpression, String defaultValue )
		{
			
			return 
				AddVarExpressionMark.OPEN_TAG +
				varName +
				AddVarExpressionMark.EXPRESSION_OPEN_TAG +
				" " +
				validationExpression +
				" " +
				AddVarExpressionMark.EXPRESSION_CLOSE_TAG +				
				"|" +
				defaultValue +
				AddVarExpressionMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+$script()+}"</CODE> */
		public static String addScriptResult( String scriptName )
		{
			return ForML.Markup.addScriptResult( scriptName );			
		}
		
		/** <CODE>"{+$script()*+}"</CODE> */
		public static String addScriptResult( String scriptName, boolean isRequired )
		{
			return ForML.Markup.addScriptResult( scriptName, isRequired );			
		}
		
		/** <CODE>"{+$script(parm1,param2)*+}"</CODE> */
		public static String addScriptResult( 
			String scriptName, String parameters, boolean isRequired )
		{
			return ForML.Markup.addScriptResult( scriptName, parameters, isRequired ); 				
		}
		
		/** <CODE>"{+((Math.PI * r * r))+}"</CODE>*/
		public static String addExpressionResult( String expression )
		{
			return ForML.Markup.addExpressionResult( expression ); 	
		}
		
		/** <CODE>"{{+:{+fieldType+} {+fieldName+}+}}"</CODE> */
		public static String addForm( String forMLText )
		{
			//TODO should I validate the Form Text??
			return BindMLParser.AddFormMark.OPEN_TAG + forMLText 
				+ BindMLParser.AddFormMark.CLOSE_TAG;
		}		
	}
}
