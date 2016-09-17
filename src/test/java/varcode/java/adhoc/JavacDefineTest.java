/*
 * Copyright 2016 eric.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import junit.framework.TestCase;
import varcode.VarException;
import static varcode.java.adhoc.Workspace.JAVAC;

/**
 *
 * @author eric
 */
public class JavacDefineTest
    extends TestCase
{
    public void testNone()
    {
        
    }
    
    /**
     * Compile This Source code to a .class, then 
     * define it in the existing classLoader
     */
    public void aTestDefineClass( )
    {
        //this( JAVAC.getStandardFileManager( 
        //        null, //use default DiagnosticListener
        //        null, //use default Locale
        //        null ), //use default CharSet    
                
        //    adHocClassLoader,
        //    "AdHoc",
        //    adHocJavaFiles ); 	
        
        //StandardJavaFileManager sfm = 
          
        AdHocFileManager ahfm = 
            new AdHocFileManager( 
                Workspace.JAVAC.getStandardFileManager( 
                null, //use default DiagnosticListener
                null, //use default Locale
                null ),//use default CharSet        
                this.getClass().getClassLoader() );
        
        Iterable<String> options = JavacOptions.optionsFrom(  );
			
		DiagnosticCollector<JavaFileObject> diagnostics = 
			new DiagnosticCollector<JavaFileObject>();
		
        AdHocJavaFile adHocCode= new AdHocJavaFile(
            "ex.varcode", "SomeClass", "public class SomeClass{ }"); 
        
        List<AdHocJavaFile> codeList = new ArrayList<AdHocJavaFile>();
        
		JavaCompiler.CompilationTask task = 
            JAVAC.getTask(
                null, //use System.err if the tool fails 
                ahfm,
                diagnostics, 
                null, 
			    null, // NO annotation processors classes (at this time) 
                codeList );
			
		boolean compiledNoErrors = task.call();
			
	    if( !compiledNoErrors )
	    {
            throw new JavacException( 
	        	codeList,  
	        	diagnostics );
	    }	        	
        try
        {
        	ahfm.close();
        }
        catch( IOException ioe )
        {
            //LOG.warn( "Error closing BaseFileManager", ioe);            	
        }
        
        //
        System.out.println ( ahfm.getAdHocClassFilesMap() );
    }
    
     /**
     * File Manager for the AdHoc Workspace
     * (note this is private b/c I dont want the internals leaking onto
     * the Workspace API)
     */
    private static class AdHocFileManager
        extends ForwardingJavaFileManager<JavaFileManager>      
    {
        
        private final Map<String, AdHocClassFile> adHocClassFilesMap;
        
        private final ClassLoader classLoader;
        
        public AdHocFileManager( 
            StandardJavaFileManager fileManager,
            ClassLoader classLoader )
        {
            super( fileManager );
            this.classLoader = classLoader;
            this.adHocClassFilesMap = new HashMap<String, AdHocClassFile>();
        }
                
        public Map<String, AdHocClassFile> getAdHocClassFilesMap()
        {
            return this.adHocClassFilesMap;
        }
        
        
        @Override
        public JavaFileObject getJavaFileForOutput(
            JavaFileManager.Location location, 
            String className, 
            JavaFileObject.Kind kind, 
            FileObject sibling ) 
        {    	
            AdHocClassFile adHocClass =
                this.adHocClassFilesMap.get( className );	
        
            if( adHocClass != null )
            {
                return adHocClass;
            }
            try
            {
                adHocClass = new AdHocClassFile( className );
                this.adHocClassFilesMap.put( className, adHocClass );
                //this.adHocClassLoader.introduce( adHocClass );
                return adHocClass;
            }
            catch( Exception e )
            {
                throw new VarException( 
                    "Unable to create output class for class \"" + className + "\"" );
            }
        }
    }
}
