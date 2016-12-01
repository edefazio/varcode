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
package varcode.java.load;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import varcode.load.LoadException;
import varcode.java.ast.JavaASTParser;
import varcode.java.ast.JavaASTParser;
import varcode.load.SourceLoader;

/**
 * Loads the source from a Top Level class, enum or interface 
 * OR member class, enum or interface.
 * 
 * public class TopLevelClass
 * {
 *     public static class NestedClass
 *     {
 *     }
 * 
 *     public interface NestedInteface
 *     {
 *     }
 * 
 *     public enum NestedEnum
 *     {
 *     }
 * }
 * @author M. Eric DeFazio eric@varcode.io
 */
public class NestedJavaSourceLoader
    implements SourceLoader
{
    private final SourceLoader sourceLoader;
    
    /** Loads Java code member classes, enums and interfaces */
    public static final NestedJavaSourceLoader BASE_INSTANCE = 
        new NestedJavaSourceLoader( JavaSourceLoader.INSTANCE );
    
    /**
     * Loads Member classes, 
     * @param sourceLoader 
     */
    public NestedJavaSourceLoader( SourceLoader sourceLoader )
    {
        this.sourceLoader = sourceLoader;
    }

    private static Class getTopLevelClass( Class clazz )
    {
        Class declaringClass = clazz.getDeclaringClass();
        if( declaringClass == null )
        {
            return clazz;
        }
        return declaringClass;
    }
    
    /**
     * Can retrieve the source of a top-level or nested class
     * @param nestedClass
     * @return 
     */
    public SourceStream fromClass( Class<?> nestedClass )
    {
        Class topLevelClass = getTopLevelClass( nestedClass );
        
        if( topLevelClass == nestedClass )
        {
            //we are loading a top level class, go
            return this.sourceStream( 
                topLevelClass.getCanonicalName() + ".java" );
        }
        // nestedClass is NOT a top level class, it is declared within 
        // another class, I need to Load/Parse the declaring class to find
        // and return only the nested source 
        SourceStream declaringClassStream  = 
            sourceStream( topLevelClass.getCanonicalName() + ".java" ); 
        
        CompilationUnit cu = null;
        try
        {
            cu = JavaParser.parse( declaringClassStream.getInputStream() );
            
            //find the declaration of the nested class by recursively 
            // checking the nodes until I find a TypeDeclaration with this name
            TypeDeclaration td = JavaASTParser.findTypeDeclaration( 
                cu, nestedClass.getSimpleName() );
            
            NestedJavaSourceStream njss = new NestedJavaSourceStream( 
                declaringClassStream, 
                nestedClass.getCanonicalName(), 
                td.toString() );
            return njss;
        }
        catch( ParseException pe )
        {
            throw new LoadException(
                "could not load model for "+ nestedClass, pe );
        }
    }
    
    @Override
    public SourceStream sourceStream( String sourceId )
    {
        // pack.age.with.DeclaringClass$memberClass
        int indexOf$ = sourceId.indexOf( "$" );
        if( indexOf$ < 0 )
        {
            return sourceLoader.sourceStream( sourceId );
        }
        else
        {
            String declaringClass = sourceId.substring( 0, indexOf$ );
            SourceStream declareSource = 
                this.sourceLoader.sourceStream( declaringClass + ".java" );
            CompilationUnit cu = null;
            try
            {
                //create the AST for the source
                cu = JavaParser.parse( declareSource.getInputStream() );
                
                String name = sourceId.substring( sourceId.lastIndexOf( "$" ) + 1 );
                TypeDeclaration td = JavaASTParser.findTypeDeclaration( cu, name );
                NestedJavaSourceStream sss = new NestedJavaSourceStream( 
                    declareSource, sourceId, td.toString() );
            return sss;
            }
            catch( ParseException pe )
            {
                throw new LoadException(
                    "could not load model for "+ sourceId, pe );
            }
        }
    }

    @Override
    public String describe()
    {
        return "NestedJavaSourceLoder from[" + this.sourceLoader + "]";
    }
    
    /**
     * A Source Stream that aliases a Member Class within a Declared Class
     * Source code
     */
    public static class NestedJavaSourceStream
        implements SourceStream
    {
        /** the sourceStream of the parent (declaring class) */
        public final SourceStream parentSourceStream;
        
        /** the id of the source */
        public final String sourceId;
        
        /** the member source as a String*/
        public final String memberSource;
    
        public NestedJavaSourceStream( 
            SourceStream parentSourceStream, String sourceId, String memberSource )
        {
            this.parentSourceStream = parentSourceStream;
            this.sourceId = sourceId;
            this.memberSource = memberSource;
        }
            
        @Override
        public InputStream getInputStream()
        {
            return new ByteArrayInputStream( memberSource.getBytes() );
        }

        @Override
        public String getSourceId()
        {
            return sourceId;
        }

        @Override
        public String describe()
        {
            return "[MemberSourceStream of " + sourceId + " in " + parentSourceStream.describe() +"]";
        }

        @Override
        public String asString()
        {
            return memberSource;
        }    
        
        @Override
        public String toString()
        {
            return asString();
        }
        /** the parent Source Stream (the declaring class' source) */
        public SourceStream getParentSourceStream()
        {
            return this.parentSourceStream;
        }
    }    
}
