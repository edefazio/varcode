/*
 * Copyright 2017 M. Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this toJavaFile except in compliance with the License.
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
package varcode.java;

import varcode.java.naming.ClassName;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import varcode.author.Author;
import varcode.author.AuthorState;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarContext;
import varcode.context.VarScope;
import varcode.java.adhoc.JavaSourceFile;
import varcode.java.ast.JavaAst;
import varcode.markup.Template;

/**
 * An specialization of {@link Author} for authoring {@code AdHocJavaFile}s 
 * specifically for creating AdHocJavFiles that are usable within the 
 * {@link varcode.java.adhoc.*} toolkit.
 * 
 * (instead of {@link Author.toString()} to a String or AuthorState)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaAuthor
{
    /** The property name for the fullyQuliafied Java Class name */
    public static final String JAVA_CLASS_NAME = "fullyQualifieldJavaClassName";
    
    public static final String JAVA_SIMPLE_CLASS_NAME = "className";
    
    public static final String JAVA_PACKAGE_NAME = "packageName";
    
    /**
     * Authors java code to a String and returns the authored String
     * @param template the toState 
     * @param context data & to specialize the toState
     * @param directives
     * @return 
     */
    public static String toString( 
        Template template, Context context, Directive...directives )
    {
        return Author.toString( template, context, directives );
    }
    
    /**
     * 
     * @param className the fully qualified class name
     * @param template the toState to be populated
     * @param keyValuePairs keyValuePairs for parameters
     * @return an AdHocJavaFile containing the authored .java source code
     */
    public static JavaSourceFile toJavaFile( 
        String className, Template template, Object...keyValuePairs )
    {
        return JavaAuthor.toJavaFile( className, template, VarContext.of( keyValuePairs ) );
    }
    
    /**
     * 
     * @param javaSourceCode Java source code as a String
     * @return 
     */
    public static JavaSourceFile toJavaFile( CharSequence javaSourceCode )
    {
        try
        {
            CompilationUnit astRoot = JavaAst.astFrom( javaSourceCode );
            
            String packageName = "";
            if( astRoot.getPackage() != null ) 
            {
                packageName = astRoot.getPackage().getPackageName();
            }
            
            TypeDeclaration td = JavaAst.findRootTypeDeclaration( astRoot );
            String className = td.getName();
            return new JavaSourceFile( 
                packageName, 
                className, 
                javaSourceCode.toString() );            
        }
        catch( ParseException pe )
        {
            throw new JavaException( "unable to parse Java code into AST", pe );
        }
    }
    
    /**
     * Authors and returns an {@code AdHocJavaFile} with name {@code className} 
     * using a {@code Dom} and based on the specialization provided in the
     * {@code context} and {@code directives}
     * 
     * @param className the class name to be authored (i.e. "io.varcode.ex.MyClass") 
     * @param template the document object toState to be specialized
     * @param context the specialization/functionality to be applied to the Dom
     * @param directives optional pre and post processing commands 
     * @return an AdHocJavaFile
     */
    public static JavaSourceFile toJavaFile(
    	String className, Template template, Context context, Directive...directives )    
    {       	
    	AuthorState authorState = 
            new AuthorState( template, context, directives ); 
        
        String[] pckgClass = 
            ClassName.extractPackageAndClassName( className );
        
    	context.set( JAVA_CLASS_NAME, className, VarScope.STATIC );
    	context.set( JAVA_PACKAGE_NAME, pckgClass[ 0 ], VarScope.STATIC );
    	context.set( JAVA_SIMPLE_CLASS_NAME, pckgClass[ 1 ], VarScope.STATIC );
    	
        //author the Document by binding the {@code Dom} with the {@code context}
        authorState = Author.toState( authorState );
        
        if( pckgClass[ 0 ] != null )
        {
            JavaSourceFile adHocJavaFile =
                new JavaSourceFile( 
                    pckgClass[ 0 ], 
                    pckgClass[ 1 ], 
                    authorState.getTranslateBuffer().toString() );
                
            //LOG.debug( "Authored : \"" + pckgClass[ 0 ] + "." + pckgClass[ 1 ] + ".java\"" );
            return adHocJavaFile;
        }
        //LOG.debug( "Authored : \"" + pckgClass[ 1 ] + ".java\"" );
        return new JavaSourceFile( 
            pckgClass[ 1 ], authorState.getTranslateBuffer().toString() ); 
    }
    
}
