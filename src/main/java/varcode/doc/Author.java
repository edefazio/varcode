package varcode.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.VarException;
import varcode.doc.translate.TranslateBuffer;
import varcode.context.LazyBindQueueContext;
import varcode.context.VarContext;
import varcode.doc.Directive.PostProcessor;
import varcode.doc.Directive.PreProcessor;
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
public enum Author
{
    ; //singleton enum idiom
    
	private static final Logger LOG = LoggerFactory.getLogger( Author.class );
	
	
	public static String code( Dom dom, Object...keyValuePairs )
    {
		return code( dom, VarContext.of( keyValuePairs ) );
    }

    public static String code( Class<?>templateClass, Dom dom, VarContext context, Directive...directives )
    {
    	DocState docState = new DocState( 
    		dom, 
    		context,  
    		directives );
    	
    	docState.getContext().set( "markup.class", templateClass );
    	
    	bind( docState );
    	return docState.getTranslateBuffer().toString();
    }
    
    public static String code( Dom dom, VarContext context, Directive...directives )
    {
    	DocState docState = new DocState( 
    		dom, 
    		context, 
    		directives );
    	
    	bind( docState );
        
    	return docState.getTranslateBuffer().toString();
    }
    
    public static DocState bind( Dom dom, VarContext context, Directive...directives )
    {
    	DocState docState = new DocState( 
    		dom, 
    		context, 
    		directives );
    	return bind( docState );
    }
    
    /**
     * Tailors the {@code Dom} using the {@code VarContext} 
     * 
     * @param docState the state used for compiling the {@code Dom}
     * and tailoring the source
     * @return the updated DocState
     */ 
    public static DocState bind( DocState docState )
        throws VarException 
    {    	
        PreProcessor[] preProcessors = docState.getPreProcessors();
        LOG.trace( "1) Pre-process (" + preProcessors.length + ") directives" );
        
        if( preProcessors.length > 0 )
        {
        	for( int i = 0; i < preProcessors.length; i++ )
        	{        		
        		if( LOG.isTraceEnabled() ) { LOG.trace( "   pre-process [" + i + "]: " + preProcessors[ i ] ); }
        		preProcessors[ i ].preProcess( docState );
        	}
        }
        
        TranslateBuffer logValueBuffer = new TranslateBuffer(); //translate Objects to Strings for the purposes of Logging
        
        LOG.trace( "2) Derive / Bind instance vars" );
        Mark[] marks = docState.getDom().getMarks();
        
        for( int i = 0; i < marks.length; i++ )
        {   //derive and bind all the dynamically defined Vars in the VarContext
            if( marks[ i ] instanceof Mark.BoundDynamically )
            {            	
                BoundDynamically dynamicBound = (BoundDynamically)marks[ i ];
                dynamicBound.bind( docState.getContext() ); //this will derive the var, then update the context
                if( LOG.isTraceEnabled() ) 
                { 
                	String name = dynamicBound.getVarName();
                	Object varValue = docState.getContext().resolveVar( name );
                	logValueBuffer.append( varValue );
                	LOG.trace( "  bound: " + marks[ i ] +" as : \"" + name + "\"->" + logValueBuffer.toString() );
                	logValueBuffer.clear();
                }
            }
            //it might be derived but not bound (i.e. input validation scripts, EvalScript)
            else if ( marks[ i ] instanceof Mark.Derived 
                && !( marks[ i ] instanceof BlankFiller ) //don't derive fillers until they are to be populated 
                && !( marks[ i ] instanceof BoundStatically ) ) //we don't need to derive static vars
            {            	
                Mark.Derived dd = (Mark.Derived) marks[ i ];
                Object derived = dd.derive( docState.getContext() );
                
                if( LOG.isTraceEnabled() ) 
                {
                	logValueBuffer.append( derived );
                	LOG.trace( "  derived: " + marks[ i ] + " as \"" + logValueBuffer.toString() + "\"" );
                	logValueBuffer.clear();
                }
            }
        }        
        BlankFiller[] blankFillers = docState.getDom().getBlankFillers();
        LOG.trace( "3) Fill-in template with (" + blankFillers.length + ") blanks" );
        
        Object[] fillSequence = new Object[ blankFillers.length ];
        for( int i = 0; i < blankFillers.length; i++ )
        {
        	Object derived = blankFillers[ i ].derive( docState.getContext() );
        	fillSequence[ i ] = derived;
        	if( LOG.isTraceEnabled() ) 
        	{
        		logValueBuffer.append( derived );
        		LOG.trace( "   filled[" + i + "]: " + blankFillers[ i ].toString() + " with \"" + logValueBuffer.toString()  + "\"");
        		logValueBuffer.clear();
        	}
            
        }
        docState.getDom().getFillTemplate().fill( docState.getTranslateBuffer(), fillSequence );
        
        //5) Post Processing All Directives        
        PostProcessor[] postProcessors = docState.getPostProcessors( );
        LOG.trace( "4) Post-process (" + postProcessors.length + ") directives" );
        if( postProcessors.length > 0 )
        {
        	//if( LOG.isTraceEnabled() ) { LOG.trace( "Post-Processing (" + allDirectives.length + ") Directives " ); }
        	for( int i = 0; i < postProcessors.length; i++ )
        	{
        		if( LOG.isTraceEnabled() ) { LOG.trace( "   post-process[" + i + "]: " + postProcessors[ i ] ); }
        		postProcessors[ i ].postProcess( docState );
        	}
        }             
        return docState;
    }
    
    public static DocState fillBind( Dom dom, Object...fills )
	{		
		DocState docState = new DocState( 
	        dom, 
	    	new LazyBindQueueContext( fills ) );
		
	    return bind( docState );
	}
    
    public static String fillCode( Dom dom, Object...fills )
	{		
		DocState docState = fillBind( dom, fills );
	    return docState.getTranslateBuffer().toString();
	}
}
