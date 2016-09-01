/*
 * Copyright 2016 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.doc;

import java.util.BitSet;

import varcode.VarException;
import varcode.buffer.TranslateBuffer;

/**
 * Builds a "FillInTheBlanks"-type containing static text and "blanks" which can
 * be filled with text.  
 * 
 * <UL>
 * <LI>the {@code Builder} captures text and "blanks" ("indexes" where "blanks"
 * will be that may be filled in later) the <B>builder is not thread-safe</B>. 
 * (It is the "larval" stage of the object,  when the document is completed, it 
 * can be compiled() to a thread safe entity). <LI>The {@code FillOrder} allows 
 * many threads to share and populate unique documents by "filling in 
 * the blanks" with the {@code fill(...)} methods.
 * </UL>
 * 
 * <H3>Usage Example:</H3><BR>
 * <PRE><CODE>
 * FillTemplate s = 
 *     FillInTheBlanks.of( null, null, null, null, null ); //5 blanks
 * 
 * System.out.println ( s.fill("A", "B", "C", "D", "E") ); //produces "ABCDE"
 * System.out.println ( s.fill(" ", " ", " ", " ", " ") ); //produces "       "
 * System.out.println ( s.fill("Don't ", "disturb ", "the ", "man ", "outside") ); 
 * //produces "Don't disturb the man outside"
 * 
 * FillInTheBlanks.FillTemplate c2 = 
 *     FillInTheBlanks.of( "four score and ", null, " years ago");
 *           
 * System.out.println ( c2.fill( "seven" ) ); //produces "four score and seven years ago"
 * System.out.println ( c2.fill( "7" ) ); //produces "four score and 7 years ago"
 * </CODE></PRE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum FillInTheBlanks 
{			
	INSTANCE;
	
	/**
	 * Merges many {@code FillTemplate}s into a single {@code FillTemplate}
	 * 
	 * @param fillTemplates multiple fillTemplates to create a larger fillTemplate from
	 * @return 
	 */
	public static FillTemplate merge( FillTemplate...fillTemplates )
	{
		Builder builder = new Builder();
		for( int i = 0; i < fillTemplates.length; i++ )
		{
			FillTemplate thisTemplate =  fillTemplates[ i ];
			int blankCountInThisTemplate = thisTemplate.getBlanksCount();
			for( int b = 0; b < blankCountInThisTemplate; b++ )
			{
				String staticText = thisTemplate.getTextBeforeBlank( b );
				builder.text( staticText );
				builder.blank();				
			}
			builder.text( thisTemplate.getTextAfterBlank( blankCountInThisTemplate - 1 ) );						
		}
		return builder.compile();		
	}
	
	/** 
	 * Uses the Builder to build a FillTemplate...
	 * 
	 * Here is the BLANK Rules:
	 * <OL>
	 *   <LI> Rule: (NULL ADDS BLANK)<BR> 
	 *   if we encounter a null element, <B>ADD A BLANK</B>
	 *   <LI> Rule: (BLANKS BETWEEN NON-NULL ELEMENTS)<BR>
	 *   if we encounter a non-null String element, <BR>
	 *        AND there is another element AFTER it <BR>
	 *        AND the element AFTER it is NON-NULL <BR>
	 *        <B>ADD A BLANK</B>
	 * </OL>       
	 * 
	 * Use Cases:
	 * Rule 1) (Add a blank when null element encountered)
	 * <PRE>FillTemplate FIVE_BLANKS = compose( null, null, null, null, null);</PRE>
	 * 
	 * Rule 2) (Add Blank between non-null elements)
	 * <PRE>FillTemplate ALPHA = compose( "A", "C", "D", "F");</PRE>
	 * 
	 * will produce a FillTemplate:<BR><PRE> 
	 *   "A_C_D_F"
	 *  // ^ ^ ^
	 *  // blanks </PRE>
	 *  
	 * To add a double-blank (or multiple blanks in a row) use a null
	 * <PRE>
	 * 
	 * </PRE>
	 * 
	 * <PRE>FillTemplate  
	 * @param data
	 * @return
	 */
	public static FillTemplate of( String...data )
	{
		Builder b = new Builder();
		
		for( int i = 0; i < data.length; i++ )
		{
			if( data[ i ] == null )
			{   
				b.blank();							
			}
			else
			{
				b.text( data[ i ] );
				if( i < data.length - 1 
					&& data[ i + 1 ] != null )
				{   //add a blank AFTER text provided there is ANOTHER data element AFTER this one
					b.blank();
				}
			}
		}
		return b.compile();
	}
	
	/** 
	 * NOTE: NOT THREAD SAFE!!
	 * (use a separate builder when creating a Builder() )
	 * 
	 * Incremental "builder" of the larval stage of the FillInTheBlanks
	 * document it is STATEFUL
	 */
	public static class Builder
	{		
		/** contains all static text contiguously */
		private final StringBuilder sb;
		
		/** the current character index */
		private int cursorIndex;
		
		/** the character indexes where the blanks are within the text */
		private BitSet blankIndexes = new BitSet();
		
		public Builder()
		{
			this( new String[ 0 ] );
		}
		
		public Builder( String...strings )
		{
			sb = new StringBuilder();
			cursorIndex = 0;
			of( strings );
		}
		
		public Builder of( String ...params )
		{
			for( int i = 0; i < params.length; i++ )
			{
				if( params[ i ] == null )
				{
					blank();
				}
				else
				{
					text( params[ i ] );
				}
			}
			return this;
		}
		
		public Builder object( Object object )
		{
		    if( object == null )
		    {
		        return this;
		    }
		    String stringified = object.toString();
		    return text( stringified );
		}
		
		/** 
		 * ADDS text to the varcode and returns the updated builder 
		 */
		public Builder text( String staticText )
		{			
			if( staticText == null )
			{
				return this;
			}
			cursorIndex += staticText.length();
			sb.append( staticText ); //Text.replaceComment( staticText ) );
			return this;
		}
		
		public Builder blank()
		{	
			blankIndexes.set( cursorIndex );
			cursorIndex++;
			return this;
		}		
	
		public FillTemplate compile()
		{
			return FillTemplate.of( sb.toString(), blankIndexes );
		}		
	}
	
	/**
	 * Fill all the blanks of the document at once.
	 * for instance: <BR><BR>
	 *   
	 * Parsing the following text sequence:
	 *  <PRE>
	 *  "__ killed __ in the __ with the __";
	 *  </PRE>
	 *  The {@code FillSequence} maintains the static text 
	 *  intermingled with indicators of where data is to be filled in:
	 *   
	 *  text   : " killed  in the  with the ";
	 *  blanks[] : 0,8,16,26 //the character indexes where a data is inserted.
	 *  
	 *  <PRE>
	 *  we store the blank indexes as a {@code BitSet} indexed into the static text:
	 * " killed  in the  with the "
	 * "  eht htiw  eht in dellik "
	 *  100000000010000000100000001
	 *  |         |       |       |[0] the 1st "blank" is at the 1st character
	 *  |         |       |[8] the 2nd "blank" is at the 8th character
	 *  |         |[16] the 3rd "blank" is at the 16th character
	 *  |[26] the 4th blank is at the 26th character
	 * 
	 *  putting it all together (parsing and filling a Sequence):
	 *  
	 *  String filled = 
	 *      FillInTheBlanks.parse( "__ killed __ in the __ with the __" )
	 *          .fill( "Col. Mustard", "Professor Plum", "study", "lead pipe" );
	 * </PRE>
	 * 
	 *  ...will produce: <BR>
	 *  <PRE>
	 *  "Col. Mustard killed Professor Plum in the study with the lead pipe"
	 *  </PRE>
	 */
	public static class FillTemplate
	{		
		/** 
		 * All of the "Static" Text for the Sequence
		 * For instance if we had:
		 * <PRE>
		 * FillInTheBlanks.Sequence s = FillInTheBalnks.parse(
		 *  "Mary had a __ lamb, __ lamb, __ lamb", "__" );
		 * </PRE>
		 * the text would be:
		 * text = "Mary had a  lamb,  lamb,  lamb";
		 */
		private final String text;
		
		/** 
		 * Indexes within the Static text where text is to be inserted
		 * For instance if we had:
	 	 * <PRE>
		 * FillInTheBlanks.Sequence s = FillInTheBalnks.parse(
		 *  "Mary had a __ lamb, __ lamb, __ lamb", "__" );
		 * 
		 * the text would be:
		 * text = "Mary had a  lamb,  lamb,  lamb";
		 *  
		 * there are "Blanks" where test can be inserted at indexes:
		 * 
		 * text = "Mary had a  lamb,  lamb,  lamb";
		 *                   ^      ^      ^
		 *                 [10]   [17]   [24]
		 *                 
		 * so we represent all of the characters in the fill in the blanks as a 
		 * BitSet, where a 0 means a static character in {@code text}, and a 1 
		 * means a "blank" where text can be inserted, so we end up with a 
		 * BitSet with the following contents:
		 *   
		 * text = "Mary had a  lamb,  lamb,  lamb";
		 *         000000000010000001000000100000   // NOTE: string indexes goes Left to right
		 *                                          // i.e. "M" is at text[0], "a" is at text[1]
		 *                                          
		 *         000000000010000001000000100000   // so the binary stored in the BitSet is actually
		 *                                          // transposed (if we wanted to look at the binary value):                                        
		 *         000001000000100000010000000000   
		 *         ^                            ^ 
		 *         |                            |
		 *        [29]                         [0]    
		 *         
		 *  how each character matches the binary is:
		 *  
		 *  "Mary had a  lamb,  lamb,  lamb" (transpose the indexes,
		 *  "bmal  ,bmal  ,bmal  a dah yraM"
		 *   000001000000100000010000000000
         *   ^                            ^ 
		 *   |                            |
		 *  [29]                         [0]   
		 */
		private final BitSet blankIndexes;
		
		public static FillTemplate of( String text, BitSet blankIndexes )
		{
			return new FillTemplate( 
			    text, 
			    blankIndexes );
		}
		
		/**
		 * 
		 * @param text
		 * @param blankIndexes Bitset where each bit represents: 
		 * <UL>
		 *   <LI>a static character (if 0)
		 *   <LI>a "blank" where text can be inserted. (if 1)
		 * </UL>   
		 */
		public FillTemplate( String text, BitSet blankIndexes )
		{
			this.text = text;
			this.blankIndexes = blankIndexes;
		}
		
		/** the number of blanks in the FillTemplate */
		public int getBlanksCount()
		{
			return blankIndexes.cardinality();
		}
		
		/** fills and returns the filled document as a String */
		public String fill( Object...fillsInOrder )
		{
			TranslateBuffer buff = 
			   new TranslateBuffer(); 
			fill( buff, fillsInOrder );
			return buff.toString();
		}
		
		public void fill( TranslateBuffer out, Object... fillsInOrder )
		    throws VarException
		{
			if( fillsInOrder == null )
			{
				if( blankIndexes.cardinality() != 1 )
				{
					throw new VarException( 
		                "fill parameter count (1)" 
		                + " must match blanks count (" 
		                + blankIndexes.cardinality() + ")" );
				}
				fillsInOrder = new Object[] { null };
			}
		    if( fillsInOrder.length < blankIndexes.cardinality() )
            {
                throw new VarException( 
                    "fill parameter count (" + fillsInOrder.length 
                    + ") must match blanks count (" 
                    + blankIndexes.cardinality() + ")" );
            }
            
            int currentTextCharAt = 0; //current char index in the document
            int previousBlankAt = -1; // previous blank index in the document
            
            int nextBlankAt = blankIndexes.nextSetBit( 0 );
            int fillIndex = 0;
            int charsBetweenCount = 
                ( nextBlankAt - previousBlankAt ) -1; //count characters BETWEEN previous blank and next blank
            
            while( nextBlankAt >= 0 )
            {               
                if( charsBetweenCount > 0 )
                {   //there is text between the previous blank and the next blank 
                    //(need to fill in the text) 
                    String prepend = 
                        text.substring( 
                            currentTextCharAt, 
                            currentTextCharAt + charsBetweenCount 
                        );          
                    out.append( prepend );
                    currentTextCharAt += charsBetweenCount;                 
                }               
                //fill in the next blank
                if( fillsInOrder[ fillIndex ] != null )
                {   //we only fill if non-null (i,.e. dont append the string "null"
                    out.append( fillsInOrder[ fillIndex ] ); 
                }
                fillIndex++;                
                
                previousBlankAt = nextBlankAt;
                nextBlankAt = blankIndexes.nextSetBit( nextBlankAt + 1 );
                charsBetweenCount = ( nextBlankAt - previousBlankAt ) -1; //                
            }           
            out.append( text.substring( currentTextCharAt ) );
		}
		

		/**
		 * Returns the static (unchanging) text
		 * @return
		 */
		public String getStaticText() 
		{
			return text;
		}		

		/**
		 * Returns the Static Text and the blanks annotated as 
		 * {<1>, <2>, <3>, ...}
		 */
		public String toString()
		{
		    Object[] fillMarkers = new String[ blankIndexes.cardinality() ];
		    for( int i = 0; i < fillMarkers.length; i++ )
		    {
		        fillMarkers[ i ] = "<" + ( i + 1 ) +">";
		    }
		    return fill( fillMarkers ).toString();
		}

		/** 
		 * Bitset where set bits represent a "blank" to be filled by
		 * text.
		 * @return
		 */
		public BitSet getBlanks()
		{
			return this.blankIndexes;
		}
		/** 
		 * given an index of a blank, find the character index within 
		 * the static {@code text} where the blank would be placed
		 * 
		 * @param index the index of the blank (0-based) {0, 1,,2, ...}
		 * @return the character index within the {@code text} where the 
		 * blank would be
		 */
		public int getCharIndexOfBlank( int index )
		{
		    if( index >= this.blankIndexes.cardinality() || index < 0 )
		    {
		        return -1;
		    }
		    //find the index of the first blank
		    int blankIndex = this.blankIndexes.nextSetBit( 0 );
		    
		    //iterate until we find the location of the indexth blank
		    for( int i = 1; i <= index; i++ )
            {   //go to the index of the NEXT BLANK
                blankIndex = this.blankIndexes.nextSetBit( blankIndex + 1 );
            }		    
		    if( index == 0 )
		    {		         
		        return blankIndex;
		    }
		    // we need to ADJUST the fillIndex to be a character index, 
            // since the String staticText does not contain ANY character 
            // place holder for a blank, so we need to backup by index 
            // characters
		    return blankIndex -( index );	    
		}
		
		/** 
         * Retrieves the Text Before the {@code index}<SUP>th</SUP> blank 
         * (0-based)
         * for example, Assume we had the following 
         * (where __ represents a Blank)
         * <PRE>
         * FillOrder fo = FillInTheBlanks.parse( 
         *        "__ sells __ shells __ the __ shore");
         * //      [0]      [1]       [2]    [3] 
         * 
         * String first fo.getTextBeforeBlank( 0 ); // ""
         * String second fo.getTextBeforeBlank( 1 ); // " sells "
         * String third fo.getTextBeforeBlank( 2 ); // " shells "
         * String fourth fo.getTextBeforeBlank( 3 ); // " the "
         * 
         * String fifth fo.getTextAfterBlank( 4 ); // ""
         * String hundreth fo.getTextAfterBlank( 100 ); // ""
         * </PRE>
         * 
         * @param index the 0-based blank index that demarcates the text to 
         * retrieve
         * @return the text between the {@index}<SUP>th</SUP> blank and the 
         * blank after 
         * (or the remaining text if the {@index}<SUP>th</SUP> blank is the last 
         * blank  
         */
	    public String getTextBeforeBlank( int blankIndex )
	    {
	        if ( getBlanksCount() == 0 )
	        {
	            return text;
	        }
	        if( blankIndex >= getBlanksCount() )
	        {
	            return "";
	        }
	        if( blankIndex < 0 )
	        {
	            return "";
	        }
	        if( blankIndex == 0 )
	        {
	            int end = getCharIndexOfBlank( blankIndex );
	            return getStaticText().substring( 0, end );
	        }
	        int start = getCharIndexOfBlank( blankIndex - 1 );
	        int end = getCharIndexOfBlank( blankIndex );
	        return getStaticText().substring( start, end );
	    }

	    /** 
         * Retrieves the Text After the {@code index}<SUP>th</SUP> blank (0-based)
         * 
         * for example, Assume we had the following (where __ represents a Blank)
         * <PRE>
         * FillOrder fo = FillInTheBlanks.parse( 
         *     "__ sells __ shells __ the __ shore");
         *  //  [0]      [1]       [2]    [3]
         * 
         * String first fo.getTextAfterBlank( 0 ); // " sells "
         * String second fo.getTextAfterBlank( 1 ); // " shells "
         * String third fo.getTextAfterBlank( 2 ); // " the "
         * String fourth fo.getTextAfterBlank( 3 ); // " shore"
         * 
         * String fifth fo.getTextAfterBlank( 4 ); // ""
         * String hundreth fo.getTextAfterBlank( 100 ); // ""
         * </PRE>
         * 
         * @param index the 0-based blank index that demarcates the text to retrieve
         * @return the text between the {@index}<SUP>th</SUP> blank and the blank after 
         * (or the remaining text if the {@index}<SUP>th</SUP> blank is the last blank  
         */
	    public String getTextAfterBlank( int blankIndex )
	    {
	        if( blankIndex >= getBlanksCount() )
	        {
	            return "";
	        }
	        if( blankIndex < 0 )
	        {
	            return "";
	        }
	        if( blankIndex == getBlanksCount() - 1 )
	        { //last blank
	            int start = getCharIndexOfBlank( blankIndex );
	            return getStaticText().substring( start );
	        }
	        int start = getCharIndexOfBlank( blankIndex );
	        int end = getCharIndexOfBlank( blankIndex + 1 );
	        return getStaticText().substring( start, end );
	    }
	}	
}
