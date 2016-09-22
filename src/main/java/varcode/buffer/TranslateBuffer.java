package varcode.buffer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	/** Translates some input source text and returns the translated output text*/
    public interface Translator
    {
        /** given the source, translate and return the translation*/
        Object translate( Object source );
    }
    
	private final StringBuilder buffer; 

    private final List<Translator> translators = new ArrayList<Translator>();
    
    private static final List<Translator> DEFAULT_TRANSLATE = new ArrayList<Translator>();
    
    static
    {
    	DEFAULT_TRANSLATE.add( ClassToStringTranslate.INSTANCE );
    	DEFAULT_TRANSLATE.add( CollectionToArrayTranslate.INSTANCE );
    	DEFAULT_TRANSLATE.add( JSArrayToArrayTranslate.INSTANCE );    	
    }
    
    public TranslateBuffer()
    {
    	this( DEFAULT_TRANSLATE.toArray( new Translator[ 0 ] ) );
    }
    
    public List<Translator> getTranslators()
    {
    	return translators;
    }
    
    public TranslateBuffer( Translator...translators )
    {
    	this.buffer = new StringBuilder(); //new FillBuffer();                                	
    	this.translators.addAll( Arrays.asList( translators) );
    }
    
    public void addTranslator( Translator translator )
    {
    	this.translators.add( translator );
    }
    
    /**
     * Sometimes you JUST want to translate WITHOUT adding
     * 
     * @param input the input
     * @return the translated String
     */
    public String translate( Object input )
    {
        return JavaElementTranslate.INSTANCE.translate( input );
    }
    
    /** TODO REMOVE THIS */
    public String oldTranslate( Object input )
    {
    	if( input == null )
        {
    		return "";
    	}
    	if( input instanceof CharSequence )
    	{
    		return (String)input;    		
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	
    	if( input.getClass().isArray() )
        {
    		int len = Array.getLength( input );
         			
         	for( int i = 0; i < len; i++ )
         	{
         		if( i > 0 )
         		{
         			sb.append( ", " );
         		}
         		Object o = Array.get( input, i );
                Object translated = o;
             	for( int j = 0; j < this.translators.size(); j++ )
             	{
             		translated = this.translators.get( j ).translate( translated );
             	}
         		sb.append( translated ); 
         	}
         	return sb.toString();
        }
    	Object translated = input;
    	for( int i = 0; i < this.translators.size(); i++ )
    	{
    		translated = this.translators.get( i ).translate( translated );
    	}
    	
    	//Object translated = translate( input );
    	if( !( translated.equals( input ) ) && !( translated instanceof String ) )
    	{   //it was translated, but not into a String, might need further translation
    		return translate( translated );
    	}
    	//if there was no translation, just append
    	sb.append( translated );
        return sb.toString();
    }
    
    public TranslateBuffer append( Object input )
    {
    	buffer.append( translate( input ) );
    	return this;
    }
        
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
     */
    public TranslateBuffer replaceBuffer( String str )
    {
    	clear();
    	this.buffer.append( str );
    	return this;
    }
}