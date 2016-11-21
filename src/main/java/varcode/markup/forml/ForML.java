package varcode.markup.forml;

import varcode.context.VarContext;
import varcode.doc.form.Form;
import varcode.markup.mark.Mark;

public enum ForML 
{
	;

	private static final VarContext PARSE_CONTEXT = VarContext.of(  );
	
	public static Form compile( String forMLDoc )
	{
		return ForMLCompiler.INSTANCE.compile( forMLDoc );
	}
	
	public static Mark parseMark( String forMLMark )
	{
		return ForMLParser.INSTANCE.parseMark( PARSE_CONTEXT, forMLMark, -1 );
	}
	
    /**
     * builds Mark Text given parameters
     */
	public enum MarkText
	{
		;
		public static final String REQUIRED = "*";
		
		public static final String THAN = ":";
		
		public static final String OR_DEFAULT = "|";
		/*
		 * <UL>
		 * 
		 * 
		 * <LI><CODE>"{{+:{+fieldType+} {+fieldName+}+}}"</CODE> {@code AddForm}
		 * <LI><CODE>"{_+:{+fieldType+} {+fieldName+}+_}"</CODE> {@code AddForm}
		 * 
		 * <LI><CODE>"{+?var:addThis+}"</CODE>  {@code AddIfVar}
		 * <LI><CODE>"{{+?a==1: implements {+impl+}+}}"</CODE> {@code AddFormIfVar}
		 * <LI><CODE>"{_+?a==1: implements {+impl+}+_}"</CODE> {@code AddFormIfVar}		 
		 * </UL>
		 */
		
		/** <CODE>"{+varName+}"</CODE> */
		public static String addVar( String varName )
		{
			return ForMLParser.AddVarMark.OPEN_TAG 
				+ varName 
				+ ForMLParser.AddVarMark.CLOSE_TAG; 
		}
		
		/** <CODE>"{+varName*+}"</CODE> */
		public static String addVar( String varName, boolean isRequired )
		{
			if( isRequired )
			{
				return ForMLParser.AddVarMark.OPEN_TAG 
					+ varName + REQUIRED 
					+ ForMLParser.AddVarMark.CLOSE_TAG;
			}
			return addVar( varName );
		}
		
		/** <CODE>"{+name|defaultValue+}"</CODE>  */
		public static String addVar( String varName, String defaultValue )
		{
			return ForMLParser.AddVarMark.OPEN_TAG 
				+ varName + OR_DEFAULT + defaultValue 
				+ ForMLParser.AddVarMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+$script()+}"</CODE> */
		public static String addScriptResult( String scriptName )
		{
			if( scriptName.endsWith( ")" ) )
			{   //the ()s have already been passed in
				return ForMLParser.AddScriptResultMark.OPEN_TAG 
					+ scriptName  
				    + ForMLParser.AddScriptResultMark.CLOSE_TAG;
			}
			return ForMLParser.AddScriptResultMark.OPEN_TAG 
				+ scriptName + "()" 
		        + ForMLParser.AddScriptResultMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+$script()*+}"</CODE> */
		public static String addScriptResult( String scriptName, boolean isRequired )
		{
			if( isRequired )
			{
				if( scriptName.endsWith( ")" ) )
				{
					return ForMLParser.AddScriptResultMark.OPEN_TAG 
						+ scriptName + "*" 
						+ ForMLParser.AddScriptResultMark.CLOSE_TAG;
				}
				return ForMLParser.AddScriptResultMark.OPEN_TAG 
					+ scriptName + "()*" 
					+ ForMLParser.AddScriptResultMark.CLOSE_TAG;
			}
			return addScriptResult( scriptName );
		}
		
		public static String addScriptResult( String scriptName, String params )
		{
			return ForMLParser.AddScriptResultMark.OPEN_TAG 
					+ scriptName + "(" + params + ")" 
					+ ForMLParser.AddScriptResultMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+$script(parm1,param2)*+}"</CODE> */
		public static String addScriptResult( 
			String scriptName, String parameters, boolean isRequired )
		{
			if( isRequired )
			{
				return ForMLParser.AddScriptResultMark.OPEN_TAG 
					+ scriptName 
					+ "(" + parameters + ")*" 
					+ ForMLParser.AddScriptResultMark.CLOSE_TAG;
			}
			return ForMLParser.AddScriptResultMark.OPEN_TAG 
				+ scriptName 
				+ "(" + parameters + ")" 
				+ ForMLParser.AddScriptResultMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+((Math.PI * r * r))+}"</CODE>*/
		public static String addExpressionResult( String expression )
		{
			return ForMLParser.AddExpressionResultMark.OPEN_TAG 
					+ expression 
					+ ForMLParser.AddExpressionResultMark.CLOSE_TAG;
		}
		
		
		/** <CODE>"{+?log:import org.slf4j.Logger;+}</CODE> */
		public static String addIfVar( String varName, String conditionalText )
		{
			return ForMLParser.AddIfVarMark.OPEN_TAG 
				+ varName + THAN + conditionalText 
				+ ForMLParser.AddIfVarMark.CLOSE_TAG;
		}
		
		/** <CODE>"{+?log==true:import org.slf4j.Logger;+}</CODE> */
		public static String addIfVar( String varName, String varValue, String conditionalText )
		{
			return ForMLParser.AddIfVarMark.OPEN_TAG 
				+ varName + "==" + varValue + THAN + conditionalText 
				+ ForMLParser.AddIfVarMark.CLOSE_TAG;
		}		
	}
}
