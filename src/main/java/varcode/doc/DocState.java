package varcode.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import varcode.VarException;
import varcode.buffer.TranslateBuffer;
import varcode.context.VarContext;
import varcode.doc.Directive.PostProcessor;
import varcode.doc.Directive.PreProcessor;
import varcode.dom.Dom;
import varcode.markup.mark.DocDirective;
import varcode.markup.mark.Mark;

/**
 * The State maintained when tailoring the {@code Dom} via {@code Mark}s
 * and {@code VarContext})  
 * 
 *  @author M. Eric DeFazio eric@varcode.io
 */
public class DocState
{
	/** the immutable {@code Dom} containing 
	 * {@code Mark}s and {@code FillInTheBlanks.FillTemplate} */
    private Dom dom;
     
    /** 
     * Bound input vars (name/value pairs), components and a mutable workspaces
     * for deriving / binding instance data used for filling in the
     * used whiled tailoring the {@code Dom}, contains:
     * <UL>
     * <LI>input data for deriving and filling in the {@code FillInTheBlanks.FillTemplate}
     * <LI>functionality ({@code ExpressionEvaluator}, {@code VarNameAudit} 
     * <LI>bound {@code VarScript}s 
     * <LI>{@code Metadata}
     * </UL> 
     */
    private VarContext docContext;
     
    /** 
     * Translates Objects to text and writes text to document buffer
     */
    private TranslateBuffer translateBuffer; 
    
    /**
     * Directives CAN be specified within the {@code Dom} AND/OR
     * in this fashion, (directives in the Dom and these in the 
     * TailorState will be evaluated.
     * 
     * We store Pre Processors and POst Procesors in a single Array, since SOME
     * Directives may have a PreProcess and Post Process Component
     */
    private Directive[] directives; 
    
    public DocState( 
        Dom dom, VarContext context, Directive...directives )
    {
    	this( dom, context, new TranslateBuffer(), directives );
    }
    
    public DocState( 
        Dom dom, VarContext context, TranslateBuffer translateBuffer, Directive...directives )
    {
        this.dom = dom;
        context.merge( dom.getDomContext() );
        this.docContext = context;
        this.translateBuffer = translateBuffer;
        this.directives = collectDirectives( dom, this.docContext, directives );
    }

    /** convenience Method to get markup class if it has been set*/
    public Class<?> getMarkupClass()
    {
    	Object markupClass = this.docContext.get( "markup.class" );
    	return (Class<?>)markupClass;    		
    }
    
    public Directive[] getDirectives()
    {
    	return this.directives;
    }
    
    public PreProcessor[] getPreProcessors( )
    {
    	if( directives == null )
    	{
    		return new PreProcessor[ 0 ];
    	}
    	List<PreProcessor> preProcessors = new ArrayList<PreProcessor>();
    	for( int i = 0; i < directives.length; i++ )
    	{
    		if( directives[ i ] instanceof PreProcessor )
    		{
    			preProcessors.add( (PreProcessor) directives[ i ] );
    		}
    	}
    	return preProcessors.toArray( new PreProcessor[ 0 ] );
    }

    public PostProcessor[] getPostProcessors( )
    {
    	if( directives == null )
    	{
    		return new PostProcessor[ 0 ];
    	}
    	List<PostProcessor> postProcessors = new ArrayList<PostProcessor>();
    	for( int i = 0; i < directives.length; i++ )
    	{
    		if( directives[ i ] instanceof PostProcessor )
    		{
    			postProcessors.add( (PostProcessor) directives[ i ] );
    		}
    	}
    	return postProcessors.toArray( new PostProcessor[ 0 ] );
    }
    
    public Dom getDom() 
	{
		return dom;
	}

	public void setDom( Dom dom ) 
	{
		this.dom = dom;
	}

	public VarContext getContext() 
	{
		return docContext;
	}
	
	public void setContext( VarContext varContext ) 
	{
		this.docContext = varContext;
	}

	public TranslateBuffer getTranslateBuffer() 
	{
		return translateBuffer;
	}

	public void setTranslateBuffer( TranslateBuffer translateBuffer ) 
	{
		this.translateBuffer = translateBuffer;
	}        
	
	private static Directive[] collectDirectives( 
        Dom dom, VarContext context, Directive[] directives )
	{
	    List<Directive> allDirectives = 
	        new ArrayList<Directive>();
	    
	    Mark[] allMarks = dom.getMarks();
	    
	    //add all of the Dom directives
	    for( int i = 0; i < allMarks.length; i++ )
	    {   
	    	if( allMarks[ i ] instanceof DocDirective )
	        {
	    		DocDirective directiveMark = (DocDirective)allMarks[ i ];	
	    		Directive d = context.getDirective( directiveMark.getName() );
	    		if( d == null )
	    		{
	    			throw new VarException( 
	    			   "Could not find Directive by name \"" 
                     + directiveMark.getName() + "\"" 
	    		     + " for Mark : " + System.lineSeparator()
	    		     + directiveMark.getText() + System.lineSeparator() 
                     + "on line [" + directiveMark.getLineNumber() + "]"  );
	    		}
	    		allDirectives.add( d );
	        }
	    }
	    //add all of the  passed in directives
	    allDirectives.addAll( Arrays.asList( directives ) );
	    
	    return allDirectives.toArray( new Directive[ 0 ] );
	}
}	