package varcode.doc.lib.text;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.dom.Dom;
import varcode.markup.codeml.CodeML;
import varcode.markup.codeml.CodeMLParser.ReplaceWithVarMark;

/**
 * Creates a new {@code TailorState} where all of the {@code Mark}s are removed and 
 * replaced with the text {@code Dom}  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class MarkTarget
	implements Directive.PreProcessor
{
	public final String[] targets;
	
	public final String[] varNames;
	
	public final boolean useDefault;
	
	public final boolean isRequired;
	
	public final String[] targetMarks;
	
	private static MarkTarget of( String[] targetVarNamePairs, boolean isDefault, boolean isRequired )
	{
		if( targetVarNamePairs.length == 0 || targetVarNamePairs.length % 2 != 0 )
		{
			throw new VarException(
				"Markify expects pairs of [target, varName],[target, varName]..."+
			    "received String[" + targetVarNamePairs.length + "] "+ 
				"not > 0 AND divisible by 2" );
		}
		String[] targets = new String[ targetVarNamePairs.length / 2 ];
		String[] varNames = new String[ targetVarNamePairs.length / 2 ];
		
		for( int i = 0; i < targets.length; i++ )
		{
			targets[ i ] = targetVarNamePairs[ i * 2 ];
			varNames[ i ] = targetVarNamePairs[ ( i * 2 ) + 1 ];
			
		}
		return new MarkTarget( targets, varNames, isDefault, isRequired );
	}
	
	public static MarkTarget of( String...targetVarNamePairs )
	{
		return of( targetVarNamePairs, true, false );
	}
	
	public static MarkTarget ofRequired( String...targetVarNamePairs )
	{
		return of( targetVarNamePairs, false, true );
	}
	
	public MarkTarget( String target, String varName )
	{
		this( new String[]{ target }, new String[]{ varName }, true, false );
	}

	public MarkTarget( String target, String varName, boolean useDefault )
	{
		this( new String[]{ target }, new String[]{ varName }, useDefault, false );			
	}

	public MarkTarget( String target, String varName, boolean useDefault, boolean isRequired )
	{
		this( new String[]{ target }, new String[]{ varName }, useDefault, isRequired );
	}
	
	public MarkTarget( 
		String[]targets, String[] varNames, boolean useDefault, boolean isRequired )
	{
		if( targets.length != varNames.length )
		{
			throw new VarException 
				( "Target Length [" + targets.length + "] must equal varNames Length [" +
				varNames.length + "]" );
		}
		this.targets = targets;
		this.varNames = varNames;
		this.useDefault = useDefault;
		this.isRequired = isRequired;
		
		targetMarks = new String[ targets.length ];
		for( int i = 0; i < targetMarks.length; i++ )
		{
			targetMarks[ i ] = replaceWithVarMark( 
				varNames[ i ], 
				targets[ i ], 
				useDefault,
				isRequired );
		}			
	} 
	
	/** 
	 * Creates a CodeML {@code ReplaceWithVarMark} 
	 * 
	 * @param varName
	 * @param toBeReplaced
	 * @param useDefault
	 * @return
	 */
	public static String replaceWithVarMark( 
		String varName, 
		String toBeReplaced, 
		boolean useDefault, 
		boolean isRequired )
	{
		if( useDefault && isRequired )
		{
			throw new VarException( "Cannot be BOTH required AND have Default" );
		}
		String theDefault = "|";
		if( !useDefault )
		{
			theDefault = "";
		}
		String required = "*";
		if( !isRequired )
		{
			required = "";
		}
		return ReplaceWithVarMark.OPEN_TAG 
			+ varName + theDefault + required
			+ ReplaceWithVarMark.SEPARATOR_TAG  
			+ toBeReplaced				
			+ ReplaceWithVarMark.CLOSE_TAG;
	}
	
	public void preProcess( DocState tailorState ) 
	{
		//put the Dom back into Text form
		String codeML = tailorState.getDom().getMarkupText();
		
		//replace the target with the target Mark 
		for( int i = 0; i < targetMarks.length; i++ )
		{
			codeML = codeML.replace( this.targets[ i ] , this.targetMarks[ i ] );
		}		
		Dom modifiedDom = CodeML.compile( codeML );
		tailorState.setDom( modifiedDom );
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for( int i = 0; i < targetMarks.length; i++ )
		{
			sb.append( "\r\n" );
			sb.append( "    \"" );
			sb.append( this.targets[ i ] );
			sb.append( "\" -> \"" );			
			sb.append( this.targetMarks[ i ] );
			sb.append( "\"" );			
		}	
		return this.getClass().getName()  
			+ sb.toString();
	}
}
