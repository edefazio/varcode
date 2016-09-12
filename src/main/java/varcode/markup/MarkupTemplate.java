package varcode.markup;

import java.util.BitSet;

import varcode.context.VarContext;
import varcode.doc.FillInTheBlanks.FillTemplate;
import varcode.form.VarForm;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.BlankFiller;

/**
 * A Compiled DOM (Document Object Model) containing {@code Mark}s
 * which provides an API to to perform logic and late Binding
 * (Fills) within the Document. (Similar to a W3C HTML DOM is manipulated by
 * code with JQuery)
 * 
 * <UL>
 *  <LI>Compile-Time: 
 *      <UL>
 *       <LI>read in the text "markup" (line-by-line)  
 *       <LI>parse {@code Mark} (objects) into the Dom 
 *       <LI>reserve "blanks" for any {@code Mark.BlankFiller}s (to be filled at "Tailor-Time")
 *       <LI>derive / define (static) vars for Marks using : 
 *          <UL>
 *            <LI>{@code VarScript}s            
 *            <LI>{@code TailorDirective}s
 *            <LI>{@code VarForm}s
 *           </UL> 
 *        <LI>populate any {@code Metadata} for any {@code Mark}s            
 *      </UL> 
 *  <LI>Tailor-Time (Runtime):
 *    <UL>
 *      <LI>receive a {@code VarContext} containing any vars /components/ scripts needed for "tailoring"
 *      <LI>Copy all statically defined vars (from the {@code Dom} to the {@code VarContext})
 *      <LI>Pre-process all {@code TailorDirective}s
 *      <LI>Derive / Define all (instance) vars
 *      <LI>Fill in all reserved "blanks" with the Derived vars / forms
 *      <LI>Post Process all {@code TailorDirective}s    
 *    </UL>  
 * </UL>
 * @see Mark
 * @see VarContext
 * @see VarForm
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface MarkupTemplate
{
	/**
	 * Name bound to the Markup Stream property 
	 * (Information about where the Markup was read from)
	 */
    public static final String MARKUP_STREAM_NAME = 
        "markup.inputstream";
        
    /** Which Markup Language is the Stream contain ("BindML" "CodeML", ...) */
    public static final String MARKUP_LANGUAGE_NAME = 
        "markup.language";
        
    /** 
     * Identification of the Markup Source (i.e. the fully qualified Java class name like:
     * "varcode.markup.codeml.CodeML.java"
     */
    public static final String MARKUP_ID_NAME = 
        "markup.id";
        
    /** Target Language (Java, C, C++) */
    public static final String LANG_NAME = 
        "lang"; 
        
    /** The Time (in milliseconds) that the DOM was compiled from MarkupStream */
    public static final String DOM_COMPILE_TIMESTAMP_NAME = 
        "dom.compile.timestamp";
        
    /** gets of the {@code Mark}s in the template */
    Mark[] getMarks();

    /** {@code Mark.BlankFiller}s (fill text into the {@code Dom} at Tailor-Time */
    BlankFiller[] getBlankFillers();

    /** the number of blanks within the markup template */
    int getBlanksCount();

    /** get the indices for all {@code Mark}s within template */
    BitSet getMarkIndicies();

    /** 
     * char indexes of "Blanks" within static text associated with 
     * {@code FillAction} Marks. 
     */
    FillTemplate getFillTemplate();
}