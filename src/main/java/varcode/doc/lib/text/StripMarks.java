package varcode.doc.lib.text;

import java.util.BitSet;

import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.FillInTheBlanks;
import varcode.doc.FillInTheBlanks.FillTemplate;
import varcode.doc.Dom;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.WrapsText;

/**
 * Creates a new {@code DocState} where all of the {@code Mark}s are removed and 
 * replaced with the text {@code Dom}  
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum StripMarks
	implements Directive.PreProcessor
{
	INSTANCE;
	
	public static String stripFrom( Dom dom )
	{
		FillTemplate allMarksTemplate = dom.getAllMarksTemplate(); 
		Mark[] markActions = dom.getMarks();
		
		int blanksCount = allMarksTemplate.getBlanksCount();
		StringBuilder sb = new StringBuilder();
		sb.append( allMarksTemplate.getTextBeforeBlank( 0 ) );
		for( int i = 0; i < blanksCount; i++ )
		{
			if( markActions[ i ] instanceof WrapsText )
			{
				WrapsText wc = (WrapsText)markActions[ i ];
				sb.append( wc.getWrappedText() );
			}
			sb.append( allMarksTemplate.getTextAfterBlank( i ) );
		}		
		return sb.toString();
	}
	
	public static String stripAndCut( Dom dom )
	{
		FillTemplate allMarksTemplate = dom.getAllMarksTemplate(); 
		Mark[] markActions = dom.getMarks();
		
		int blanksCount = allMarksTemplate.getBlanksCount();
		StringBuilder sb = new StringBuilder();
		sb.append( allMarksTemplate.getTextBeforeBlank( 0 ) );
		for( int i = 0; i < blanksCount; i++ )
		{
			if( markActions[ i ] instanceof WrapsText )
			{				
				WrapsText wc = (WrapsText)markActions[ i ];
				sb.append( wc.getWrappedText() );
			}
			sb.append( allMarksTemplate.getTextAfterBlank( i ) );
		}		
		return sb.toString();
	}

	public void preProcess( DocState docState ) 
	{
		String markupWithoutMarks = 
			stripFrom(docState.getDom() );
		
		VarContext context = VarContext.of( );
		context.merge(docState.getDom().getDomContext() );
		
		Dom markupSansMarks = 
			new Dom( 
				new FillInTheBlanks.Builder( markupWithoutMarks ).compile(),				
				new Mark[ 0 ],
		        new BitSet(), 
		        context );
	
		docState.setDom( markupSansMarks );
	}
	
	public String toString()
	{
		return this.getClass().getName() +": (removes ALL marks from the Dom)";
	}
}
