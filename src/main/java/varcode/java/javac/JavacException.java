package varcode.java.javac;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import varcode.VarException;

/**
 * Compiler Exception Derived from running Javac at runtime
 * and interpreting the Diagnostics
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public final class JavacException
	extends VarException
{
    private static final String N = System.lineSeparator();
	
    private static final long serialVersionUID = 1L;

	public final List<Diagnostic<? extends JavaFileObject>> diagnostics;
	
	public static String composeStackTrace( 
		Collection<AdHocJavaFile> javaCode, 	
		DiagnosticCollector<JavaFileObject>diagnosticsCollector )
	{			
		List<Diagnostic<? extends JavaFileObject>> diagnostics = 
			diagnosticsCollector.getDiagnostics();
				
		StringBuilder sb = new StringBuilder();
		
		for( int i = 0; i < diagnostics.size(); i++ )
		{	
			Diagnostic<? extends JavaFileObject>  d = diagnostics.get( i );
			
			//System.out.println( d.getKind() );
			if( d.getKind() == Kind.NOTE || d.getKind() == Kind.WARNING ) 
			{
				continue;
			}
			sb.append( d.getMessage( null ) );
		
			String className = ((AdHocJavaFile)d.getSource()).getClassName(); 
			sb.append( N );
			sb.append( className ); //javaCode.className );
			sb.append( ".class" );		
				if( d.getLineNumber() >= 0 )
				{										
					sb.append( " at line" );
					sb.append( "[ " );				
					sb.append( d.getLineNumber() );
					sb.append( " ]: ");					
					try
					{
						BufferedReader br = 
							new BufferedReader( d.getSource().openReader( true ) );
					
						for( int l = 0; l < d.getLineNumber() -1; l++ )
						{
							br.readLine();						 						
						}
						String theLine = br.readLine();
						
						sb.append( theLine );
						sb.append( System.lineSeparator() );					  
					}
					catch( Exception e )
					{
						//swallow this one
					}
				}
			}			
			return sb.toString();			
		}
	
	public JavacException(
	    String workspaceName,
		Collection<AdHocJavaFile> javaCode,
		DiagnosticCollector<JavaFileObject> diagnostics )
	{
		super(
			"Failed Compilation of workspace \"" + workspaceName + "\"" + N
		   + composeStackTrace( javaCode, diagnostics ) );
		this.diagnostics = diagnostics.getDiagnostics();		
	}
	
	public JavacException( String message )
	{
		super( message );
		this.diagnostics = null;
	}
	
	public JavacException( String message, Throwable e )
	{
		super( message, e );
		this.diagnostics = null;
	}
}