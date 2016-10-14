package varcode.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.context.LazyBindQueueContext;
import varcode.context.VarContext;
import varcode.doc.Directive.PostProcessor;
import varcode.doc.Directive.PreProcessor;
import varcode.doc.translate.JavaTranslate;
import varcode.dom.Dom;
import varcode.markup.mark.Mark;
import varcode.markup.mark.Mark.BlankFiller;
import varcode.markup.mark.Mark.BoundDynamically;
import varcode.markup.mark.Mark.BoundStatically;

/**
 * Specialize the {@code Dom} using functionality and data bound to 
 * the {@code VarContext} to author text documents. 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Compose 
{
    ; //singleton enum idiom
    
	private static final Logger LOG = LoggerFactory.getLogger( Compose.class );
	
	/** 
     * Composes the Document to a String 
     * @param dom the dom model for the document
     * @param keyValuePairs data as Key Value Pairs for filling in the document
     * @return the composed document as a String
     */
	public static String asString( Dom dom, Object...keyValuePairs )
    {
		return asString( dom, VarContext.of( keyValuePairs ) );
    }

    /** 
     * Composes the Document to a String 
     * @param dom the dom model for the document
     * @param context data (and functionality) used for filling in the document
     * @param directives pre and post processing routines for the document
     * (for instance I might want to run a code LINT AFTER creating code)
     * @return the composed document as a String
     */
    public static String asString( 
        Dom dom, VarContext context, Directive...directives )
    {
    	DocState docState = new DocState( 
    		dom, 
    		context, 
    		directives );
    	
    	toState( docState );
        
    	return docState.getTranslateBuffer().toString();
    }
    
    /**
     * Composes the Document and returns the DocState
     * @param dom the dom model for the document
     * @param context data (and functionality) used for filling in the document
     * @param directives pre and post processing routines for the document
     * (for instance I might want to run a code LINT AFTER creating code)
     * @return the DocState containing the Document 
     */
    public static DocState toState( 
        Dom dom, VarContext context, Directive...directives )
    {
    	DocState initialDocState = new DocState( 
    		dom, context, directives );
        
    	return toState( initialDocState );
    }
    
    private static final JavaTranslate translate = JavaTranslate.INSTANCE;
    /**
     * Composes the Document and returns the {@code DocState}
     * 
     * @param initState the state used for compiling the {@code Dom}
     * and tailoring the source
     * @return the updated DocState
     */ 
    public static DocState toState( DocState initState )
        throws VarException 
    {    	
        PreProcessor[] preProcess = initState.getPreProcessors();
        LOG.trace("1) Pre-process (" + preProcess.length + ") directives" );
        
        if( preProcess.length > 0 )
        {
        	for( int i = 0; i < preProcess.length; i++ )
        	{        		
        		if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace("   pre-process [" + i + "]: " + preProcess[ i ] ); 
                }
        		preProcess[ i ].preProcess( initState );
        	}
        }        
        LOG.trace( "2) Derive / Bind instance vars" );
        Mark[] marks = initState.getDom().getMarks();
        
        for( int i = 0; i < marks.length; i++ )
        {   //derive and bind all the dynamically defined Vars in the VarContext
            if( marks[ i ] instanceof Mark.BoundDynamically )
            {            	
                BoundDynamically dynamicBound = (BoundDynamically)marks[ i ];
                dynamicBound.bind(initState.getContext() ); //this will derive the var, then update the context
                if( LOG.isTraceEnabled() ) 
                { 
                	String name = dynamicBound.getVarName();
                	Object varValue = initState.getContext().resolveVar( name );
                	//logValueBuffer.append( varValue );
                	LOG.trace( "  bound: " + marks[ i ] +" as : \"" + name 
                            + "\"->" + translate.translate( varValue ) );
                }
            }
            //it might be derived but not bound (i.e. input validation scripts, EvalScript)
            else if ( marks[ i ] instanceof Mark.Derived 
                && !( marks[ i ] instanceof BlankFiller ) //don't derive fillers until they are to be populated 
                && !( marks[ i ] instanceof BoundStatically ) ) //we don't need to derive static vars
            {            	
                Mark.Derived dd = (Mark.Derived) marks[ i ];
                Object derived = dd.derive( initState.getContext() );
                
                if( LOG.isTraceEnabled() ) 
                {
                	LOG.trace( "  derived: " + marks[ i ] + 
                        " as \"" + translate.translate( derived ) + "\"" );
                }
            }
        }        
        BlankFiller[] blankFillers = initState.getDom().getBlankFillers();
        LOG.trace( "3) Fill-in template with (" + blankFillers.length + ") blanks" );
        
        Object[] fillSequence = new Object[ blankFillers.length ];
        for( int i = 0; i < blankFillers.length; i++ )
        {
        	Object derived = blankFillers[ i ].derive( initState.getContext() );
        	fillSequence[ i ] = derived;
        	if( LOG.isTraceEnabled() ) 
        	{        		
        		LOG.trace( "   filled[" + i + "]: " 
                    + blankFillers[ i ].toString() + " with \"" 
                    + translate.translate( derived ) + "\"");
        	}            
        }
        initState.getDom().getFillTemplate().fill(
            initState.getTranslateBuffer(), fillSequence );
        
        //5) Post Processing All Directives        
        PostProcessor[] postProcessors = initState.getPostProcessors( );
        LOG.trace( "4) Post-process (" + postProcessors.length + ") directives" );
        if( postProcessors.length > 0 )
        {
        	for( int i = 0; i < postProcessors.length; i++ )
        	{
        		if( LOG.isTraceEnabled() ) 
                { 
                    LOG.trace( "   post-process[" + i + "]: " + postProcessors[ i ] ); 
                }
        		postProcessors[ i ].postProcess(initState );
        	}
        }             
        return initState;
    }
    
    /**
     * Composes the Document by lazily in-order fills the {@code Mark}s
     * within the {@code Dom} returns the {@code DocState}.
     * <PRE>
     * for instance:
     * Dom dom = BindML.compile("{+a+} {+b+} {+a+} {+c+} {+b+}");
     * 
     * DocState filledState = Compose.fillToState( dom, 1, 2, 3 );
     * 
     *  we have a total of (5) marks, but there are only (3) unique marks
     * ( {+a+}, {+b+}, {+c+} )
     * 
     * we use the fills like a "queue", where we enqueue 1,2,3.
     * 
     * when filling the Dom and it attempts to resolve the first mark {+a+}, 
     * it will check if {+a+} is already been lazily bound, it isn't so it 
     * dequeues 1 from the queue, Lazily binds "a" -> 1, 
     *  
     *  1 {+b+} {+a+} {+c+} {+b+}
     * and goes to the next mark "b", it is also not bound, so it dequeues 2
     * and lazily binds "b" -> 2.
     * 
     *  1 2 {+a+} {+c+} {+b+}
     * 
     * ..next it comes to another {+a+}, it <B>IS</B> bound to 1, so we write 1
     *  1 2 1 {+c+} {+b+}
     * 
     * next is {+c+} which is not bound, so dequeue c and bind it: "c" -> 3
     * then write "c"
     *  
     *  1 2 1 3 {+b+}
     * 
     * lastly we come to {+b+} which is already bound {+b+} -> 2, so write it 
     * and finish
     *  1 2 1 3 2
     * </PRE>
     * @param dom the dom model for the document
     * @param fills the 
     * @return the DocState containing the Document 
     */
    public static DocState inlineToState( Dom dom, Object...fills )
	{		
		DocState docState = new DocState( 
	        dom, 
	    	new LazyBindQueueContext( fills ) );
		
	    return toState( docState );
	}
    
    /**
     * Composes the Document by lazily in-order fills the {@code Mark}s
     * within the {@code Dom} returns the document as a String.
     * <PRE>
     * for instance:
     * Dom dom = BindML.compile("{+a+} {+b+} {+a+} {+c+} {+b+}");
     * 
     * DocState filledState = Compose.fillToState( dom, 1, 2, 3 );
     * 
     *  we have a total of (5) marks, but there are only (3) unique marks
     * ( {+a+}, {+b+}, {+c+} )
     * 
     * we use the fills like a "queue", where we enqueue 1,2,3.
     * 
     * when filling the Dom and it attempts to resolve the first mark {+a+}, 
     * it will check if {+a+} is already been lazily bound, it isn't so it 
     * dequeues 1 from the queue, Lazily binds "a" -> 1, 
     *  
     *  1 {+b+} {+a+} {+c+} {+b+}
     * and goes to the next mark "b", it is also not bound, so it dequeues 2
     * and lazily binds "b" -> 2.
     * 
     *  1 2 {+a+} {+c+} {+b+}
     * 
     * ..next it comes to another {+a+}, it <B>IS</B> bound to 1, so we write 1
     *  1 2 1 {+c+} {+b+}
     * 
     * next is {+c+} which is not bound, so dequeue c and bind it: "c" -> 3
     * then write "c"
     *  
     *  1 2 1 3 {+b+}
     * 
     * lastly we come to {+b+} which is already bound {+b+} -> 2, so write it 
     * and finish
     *  1 2 1 3 2
     * </PRE>
     * @param dom the dom model for the document
     * @param fills the fills lazily-bound to the document
     * @return the DocState containing the Document 
     */
    public static String inlineToString( Dom dom, Object...fills )
	{		
		DocState docState = inlineToState( dom, fills );
	    return docState.getTranslateBuffer().toString();
	}
}
