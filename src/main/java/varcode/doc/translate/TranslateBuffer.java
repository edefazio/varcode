package varcode.doc.translate;

/**
 * A {@code TextBuffer} that is designed to appropriately write/print:
 * <UL>
 * <LI>Objects 
 * <LI>(and nulls) 
 * <LI>Java Class Objects, (int.class, java.util.Date.class, ...)
 * <LI>Javascript Array Objects
 * <LI>Arrays of primitives, Objects, 
 * <LI>Collections of Objects
 * </UL> 
 * 
 * The TranslateBuffer tries to write out "the right thing"
 * to the buffer... for instance
 * <UL>
 *   <LI>If the input is null, will print "" (empty string) not null
 *   <LI>If the input is a Java primitive Class( int.class, short.class, long.class...)
 *      will print "int"
 *   <LI>If the input is a Class that is in the "java.lang" package, it will print the "Simple name"
 *       i.e. if the input is String.class, will print "String", not "java.lang.String"
 *   <LI>If the input is a Javascript array, 
 *      prints out the contents of the array (not [object Array])
 * </UL>          
 */
public class TranslateBuffer
{    
	private final StringBuilder buffer; 
    private final Translator translator;
    
    public TranslateBuffer( )
    {
    	this.buffer = new StringBuilder();                            	    	
        this.translator = JavaTranslate.INSTANCE;
    }
    
    /**
     * Sometimes you JUST want to translate WITHOUT adding
     * 
     * @param input the input
     * @return the translated String
     */
    public String translate( Object input )
    {
        return (String)translator.translate( input );
    }
    
    public TranslateBuffer append( Object input )
    {
    	buffer.append( translator.translate( input ) );
    	return this;
    }
        
    @Override
    public String toString()
    {
    	return buffer.toString();
    }
    
    public String docAsString()
    {
    	return buffer.toString();
    }
        
    public TranslateBuffer clear()
    {
    	//this.buffer.clear();
    	this.buffer.delete( 0, buffer.length() );
        return this;
    }    
    
    /** replaces the entire contents of the buffer 
     * (NOTE: DOES NOT ATTEMPT TO TRANSLATE the str)
     * @param str
     * @return the updated translateBuffer
     */
    public TranslateBuffer replaceBuffer( String str )
    {
    	clear();
    	this.buffer.append( str );
    	return this;
    }
}