package varcode.java.lib;

import java.util.HashMap;
import java.util.Map;

import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.text.PostReplace;

/**
 * Replaces instances of "/+*" and "*+/" within a document to be 
 * "real comments"
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum CommentEscape 
	implements Directive.PostProcessor
{
	INSTANCE;
	
    private static final Map<String, String>CommentEscapeMap = 
        new HashMap<String, String>();
    
    static
    {
    	CommentEscapeMap.put( "/+*", "/*" );
    	CommentEscapeMap.put( "*+/", "*/" );
    }
    
    private static final PostReplace COMMENT_REPLACE = 
        new PostReplace( CommentEscapeMap );


    @Override
	public void postProcess( DocState tailorState ) 
	{
		COMMENT_REPLACE.postProcess( tailorState );
	}
	
    @Override
	public String toString()
	{
		return this.getClass().getName()+" \"/+*\" -> \"/*\" & \"*+/\" -> \"*/\" ";
	}
}
