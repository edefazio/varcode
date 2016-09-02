package varcode.context;

import varcode.VarException;

/**
 * Exception when binding var data within a VarContext 
 * when Authoring with a {@code Dom}
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class VarBindException 
	extends VarException 
{
	private static final long serialVersionUID = 5755043763680702571L;

	public static final String N = System.lineSeparator();
	
	public VarBindException( String message, Throwable throwable ) 
	{
		super( message, throwable );
	}

	public VarBindException( String message ) 
	{
		super( message );
	}
	
	public VarBindException( Throwable throwable ) 
	{
		super( throwable );
	}
	
	/** The Dom requires the Script/Expression to be non-null, but it returned a null*/ 
	public static class NullResult
		extends VarBindException
	{
		private static final long serialVersionUID = -4144904828522592133L;
		
		private final String scriptName;
		
		private final String markText;
		
		private final int lineNumber;
		
		public NullResult( String scriptName, String markText, int lineNumber )
		{
			super( "Required* script \"" + scriptName 
		            + "\" for mark: " + N + markText + N + "on line [" 
		            + lineNumber + "] result is null" );
			this.scriptName = scriptName;
			this.markText = markText;
			this.lineNumber = lineNumber;
		}

		public NullResult( 
            String scriptName, String scriptInput, String markText, int lineNumber )
		{
			super( "Required* script \"" + scriptName +"\" with input (" + scriptInput + ")"
		            + " for mark: " + N + markText + N + "on line [" 
		            + lineNumber + "] result is null" );
			this.scriptName = scriptName;
			this.markText = markText;
			this.lineNumber = lineNumber;
		}
		
		public String getScriptName() 
		{
			return scriptName;
		}

		public String getMarkText() 
		{
			return markText;
		}

		public int getLineNumber() 
		{
			return lineNumber;
		}    
	}
	
	/** The Dom requires a value for a Var, but it is null */ 
	public static class NullVar
		extends VarBindException
	{
		private static final long serialVersionUID = 8363867570858757199L;
		
		private final String varName;
		private final String markText;
		private final int lineNumber;
		
		
		public NullVar( 
				String varName, String markText )
		{
			super( "Required* var \"" + varName 
			     + "*\" for mark: " + N + markText + N + "... is null" );
			this.varName = varName;
			this.markText = markText;
			this.lineNumber = -1;
		} 
		
		public NullVar( 
			String varName, String markText, String type, int lineNumber )
		{
			super( "Required* var \"" + varName 
		            + "*\" for " + type + ": " + N + markText + N + "on line [" 
		            + lineNumber + "] is null" );
			this.varName = varName;
			this.markText = markText;
			this.lineNumber = lineNumber;
		}
		
		public NullVar( String varName, String markText, int lineNumber )
		{
			this( varName, markText, "mark", lineNumber );		
		}

		public String getVarName() 
		{
			return varName;
		}

		public String getMarkText() 
		{
			return markText;
		}

		public int getLineNumber() 
		{
			return lineNumber;
		}    
	}
}
