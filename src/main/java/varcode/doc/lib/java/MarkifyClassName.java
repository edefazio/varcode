package varcode.doc.lib.java;

import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.text.MarkTarget;
import varcode.java.JavaNaming;

public class MarkifyClassName
	implements Directive.PreProcessor
{
	private final String varName;
	private final boolean isRequired;
	
	private MarkTarget markTarget;
	
	public MarkifyClassName( String varName )
	{
		this( varName, true );
	}
	
	public MarkifyClassName( String varName, boolean isRequired  )
	{
		this.varName = varName;		
		this.isRequired = isRequired;
	}
	
	public void preProcess( DocState tailorState ) 
	{
		if( this.markTarget == null )
		{
			
			//tailorState.getTextBuffer().append( "/*{$" + "!className(" + varName + ")*$}*/" );
			//                                                       JavaCase.MARKUP_CLASS_VAR_NAME
			Class<?> clazz = (Class<?>)tailorState.getContext().resolveVar( "markup.class" );
			String className = null;
			if( clazz != null )
			{
				className = clazz.getSimpleName();
			}
			else
			{
				className = JavaNaming.ClassName.extractFromSource( 
					tailorState.getDom().getMarkupText() );
			}
			this.markTarget = new MarkTarget( className, varName, false, isRequired );
		}
		this.markTarget.preProcess( tailorState );
	}

	
	public String toString()
	{
		return this.getClass().getName(); // + ": " + this.markTarget.toString() ;  
	}
}
