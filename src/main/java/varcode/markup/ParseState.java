package varcode.markup;

/** 
 * mutable state of the markup as it is being 
 * read / parsed / compiled into a {@code Dom}.
 */
public interface ParseState
{    	
	/** Reserve a "blank" for a {@code Mark} at Parse-time, 
	 *  to be filled in at "Tailor-Time" */
	void reserveBlank();
	
	
	/** Simple Functionality Comment to VarDocuments */
	public enum Lines
    {	
		;
		
   	    /** 
         * Counts the total number of lines that exist in the {@code source} 
         * String.
         * 
         * @param source the source string
         * @return the number of lines (1...n) in the source String or
         * 0 if {@code source} is null or empty.
         */
        public static int countTotal( String source ) 
        {
            if( source == null || source.isEmpty() )
            {
                return 0;
            }
            int lines = 1;
            int pos = 0;
            while( ( pos = source.indexOf( "\n", pos ) + 1 ) != 0 ) 
            {
                lines++;
            }
            return lines;
        }
    }
}