package varcode.doc.lib.java;

import varcode.doc.Directive;
import varcode.doc.DocState;
import varcode.doc.lib.text.MarkTarget;
import varcode.java.JavaNaming;

/**
 * Looks at the source of a Class and tries to add a Mark on the text around the 
 * className
 * 
 * for instance if we have a class <PRE>
 * public class MyClass()
 * {
 *     private final int age;
 * 
 *     public MyClass( int age )
 *     {
 *        this.age = age;
 *     } 
 * }
 * 
 * will convert the text to:
 * public class /*{+className*+/MyClass()/+*}*+/
 * {
 *     private final int age;
 * 
 *     public /*{+className*+/MyClass/+*}*+/( int age )
 *     {
 *        this.age = age;
 *     } 
 * }
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
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
	
	public void preProcess( DocState docState ) 
	{
		if( this.markTarget == null )
		{
			//tailorState.getTextBuffer().append( "/*{$" + "!className(" + varName + ")*$}*/" );
			//                                                       JavaCase.MARKUP_CLASS_VAR_NAME
			Class<?> clazz = (Class<?>)docState.getContext().resolveVar( "markup.class" );
			String className = null;
			if( clazz != null )
			{
				className = clazz.getSimpleName();
			}
			else
			{
				className = JavaNaming.ClassName.extractFromSource(
                    docState.getDom().getMarkupText() );
			}
			this.markTarget = new MarkTarget( className, varName, false, isRequired );
		}
		this.markTarget.preProcess(docState );
	}

	
	public String toString()
	{
		return this.getClass().getName(); // + ": " + this.markTarget.toString() ;  
	}
}
