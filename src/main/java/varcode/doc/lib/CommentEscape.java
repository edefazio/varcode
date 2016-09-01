package varcode.doc.lib.java;

import java.util.HashMap;
import java.util.Map;

import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.text.PostReplace;

public enum CommentEscape 
	implements Directive.PostProcessor
{
	INSTANCE;
	
    private static final Map<String, String>CommentEscapeMap = new HashMap<String, String>();
    
    static
    {
    	CommentEscapeMap.put( "/+*", "/*" );
    	CommentEscapeMap.put( "*+/", "*/" );
    }
    
    private static final PostReplace COMMENT_REPLACE = new PostReplace( CommentEscapeMap );


	public void postProcess( DocState tailorState ) 
	{
		COMMENT_REPLACE.postProcess( tailorState );
	}
	
	public String toString()
	{
		return this.getClass().getName()+" \"/+*\" -> \"/*\" & \"*+/\" -> \"*/\" ";
	}
}
