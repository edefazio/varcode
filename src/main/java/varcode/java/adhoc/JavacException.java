/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package varcode.java.adhoc;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * Exception from running the Javac Java source to Class compiler at runtime
 * and interpreting the Diagnostics.
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public final class JavacException
    extends AdHocException
{
    private static final String N = "\r\n";
	
    private static final long serialVersionUID = 1L;

    public final List<Diagnostic<? extends JavaFileObject>> diagnostics;
	
    public static String composeStackTrace( 
        //Collection<AdHocJavaFile> javaCode, 	
        DiagnosticCollector<JavaFileObject>diagnosticsCollector )
    {			
        List<Diagnostic<? extends JavaFileObject>> diagnostics = 
            diagnosticsCollector.getDiagnostics();
				
        StringBuilder sb = new StringBuilder();
		
        for( int i = 0; i < diagnostics.size(); i++ )
        {	
            Diagnostic<? extends JavaFileObject> d = diagnostics.get( i );
			
            if( d.getKind() == Kind.NOTE || d.getKind() == Kind.WARNING ) 
            {
                continue;
            }            
            sb.append( d.getMessage( null ) );
		
            if( ! ( d.getSource() instanceof JavaSourceFile ) )
            {   //the source that originated the error is not available 
                return sb.toString();
            }
            String className = ((JavaSourceFile)d.getSource()).getQualifiedName(); 
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
                    sb.append( "\r\n" );
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
        //Iterable<AdHocJavaFile> javaCode,
        DiagnosticCollector<JavaFileObject> diagnostics )
    {
        super( "Failed Compilation of workspace" + N
            + composeStackTrace( //(Collection)javaCode, 
                diagnostics ) );
        
         this.diagnostics = diagnostics.getDiagnostics();
    }
    
    public JavacException( 
        Collection<JavaSourceFile> javaCode,
        DiagnosticCollector<JavaFileObject> diagnostics )
    {
        super( "Failed Compilation of workspace" + N
            + composeStackTrace( 
                //javaCode, 
                diagnostics ) );
       
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