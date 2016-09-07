package varcode.markup;

import java.util.ArrayList;
import java.util.List;

import varcode.context.VarContext;
import varcode.markup.mark.Mark;

/**
 * Parses {@code Mark}s from within markup.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface MarkupParser 
{
	/**
	 * Scan the line for a valid "open tag" and return the first one in the line
	 * -or- null if no tag is found on the line
	 * 
	 * @param markup a line of markup text
	 * @return the first Open Tag in the string, -or- null if no open tags are
	 *    found in the String
	 */
	String getFirstOpenTag( String markup );

	/**
	 * Get the matching close Tag for an open tag of a Mark within varcode
	 * source...
	 * 
	 * @param openTag the open Tag
	 * @return the close tag
	 */
	String closeTagFor( String openTag )
		throws MarkupException;

	/**
	 * Given the textual representation of a Mark in Markup, 
	 * create the appropriate {@code Mark} (for the {@code Dom}).
	 * 
	 * @param parseContext contains variables and scripts previously encountered / loaded
	 * @param markText text that represents a {@code MarkAction}
	 * @param lineNumber the line number the mark text appears on
	 * @return the {@code MarkAction} for the markText
	 * @throws MarkupException
	 */
	Mark parseMark(
		VarContext parseContext, 
		String markText, 
		int lineNumber )
		throws MarkupException;
	
	
	 /**
     * Given a Tag (which may contain)
     * <UL>
     *   <LI> a name ONLY ( i.e. {+name} )
     *   <LI> a name and a default separated by '|' (i.e. {+name|default} ) 
     * </UL>
     * returns a String[ 2 ] where the 
     * <UL>
     *  <LI>{@code String[ 0 ]} is the name
     *  <LI>{@code String[ 1 ]} is the default (NULLABLE)
     * </UL> 
     *
     * @return a String[ 2 ] where:
     * <UL>
     *  <LI>{@code String[ 0 ]} is the name
     *  <LI>{@code String[ 1 ]} is the default (NULLABLE)
     * </UL>          
     */ 
	public enum Tokenize
	{
		;
		
        public static String[] byChar( String text, char separator )
        {
            if( text == null )
            {
                return new String[ 0 ];
            }
            if( text.length() == 1 )
            {
                if( text.charAt( 0 ) == separator )
                {
                    return new String[]{""};
                }
                return new String[] { text };
            }
            List<String> tokens = new ArrayList<String>();
            int previousSeparatorIndex = 0;
            int nextSeparatorIndex = text.indexOf( separator );
            
            while( nextSeparatorIndex > -1 )
            {
                String between = text.substring( 
                    previousSeparatorIndex, 
                    nextSeparatorIndex );
                if( between.length() > 0 )
                {
                    tokens.add( between );                
                }
                previousSeparatorIndex = nextSeparatorIndex + 1;
                nextSeparatorIndex = 
                    text.indexOf( separator, previousSeparatorIndex );
            }
            if( previousSeparatorIndex < text.length() -1 )
            {
                tokens.add( text.substring ( previousSeparatorIndex ) );
            }
            return tokens.toArray( new String[ 0 ] );
        }
	}
}