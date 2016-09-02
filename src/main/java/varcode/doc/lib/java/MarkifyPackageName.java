package varcode.doc.lib.java;

import varcode.VarException;
import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.text.MarkTarget;
import varcode.java.JavaNaming;

/**
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class MarkifyPackageName
	implements Directive.PreProcessor
{
	private final String varName;
	private MarkTarget markTarget;
	
	public MarkifyPackageName(  )
	{
		this( "packageName" );		
	}
	
	public MarkifyPackageName( String varName  )
	{
		this.varName = varName;		
	}
	
	public void preProcess( DocState docState ) 
	{
		if( this.markTarget == null )
		{
			//                                                       JavaCase.MARKUP_CLASS_VAR_NAME
			Class<?> clazz = (Class<?>)docState.getContext().resolveVar( 
                "markup.class" );
			//String className = null;
			String packageName = null;
			if( clazz != null )
			{
				packageName = clazz.getPackage().getName();				
			}
			else
			{
				String className = JavaNaming.ClassName.extractFromSource(docState.getDom().getMarkupText() );
				try 
				{
					clazz = Class.forName( className );
				} 
				catch( Exception ve ) 
				{
					throw new VarException( 
                        "Could not get Class Name from Source ", ve );
				}
				packageName = clazz.getPackage().getName();
			}
			//Class.forName( className ).getPackage().getName();
			this.markTarget = new MarkTarget( packageName, varName );
		}
		this.markTarget.preProcess( docState );
	}

	
	public String toString()
	{
		return this.getClass().getName(); //+ this.markTarget.toString() ; 
	}
}
