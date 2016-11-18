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
import varcode.java.load._JavaLoader.ModelLoadException;
import varcode.load.BaseSourceLoader;
import varcode.load.SourceLoader;

/**
 * Loads the source from a Top Level class, enum or interface 
 * OR member class, enum or interface.
 * 
 * public class TopLevelClass
 * {
 *     public static class memberClass
 *     {
 *     }
 * 
 *     public interface memberInteface
 *     {
 *     }
 * 
 *     public enum memberEnum
 *     {
 *     }
 * }
 * @author M. Eric DeFazio eric@varcode.io
 */
public class MemberSourceLoader
    implements SourceLoader
{
    private final SourceLoader sourceLoader;
    
    /** Loads Java code member classes, enums and interfaces */
    public static final MemberSourceLoader BASE_INSTANCE = 
        new MemberSourceLoader( BaseSourceLoader.INSTANCE );
    
    /**
     * Loads Member classes, 
     * @param sourceLoader 
     */
    public MemberSourceLoader( SourceLoader sourceLoader )
    {
        this.sourceLoader = sourceLoader;
    }

    
    public SourceStream fromClass( Class<?> memberClass )
    {
        SourceStream declaringClassStream  = sourceStream( 
            memberClass.getDeclaringClass().getCanonicalName() + ".java" ); 
        
        CompilationUnit cu = null;
        try
        {
            cu = JavaParser.parse( declaringClassStream.getInputStream() );
            TypeDeclaration td = _JavaParser.findMemberNode( cu, memberClass );
            MemberSourceStream sss = new MemberSourceStream( 
                declaringClassStream, memberClass.getCanonicalName(), td.toString() );
            return sss;
        }
        catch( ParseException pe )
        {
            throw new ModelLoadException(
                "could not load model for "+ memberClass, pe );
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
                TypeDeclaration td = _JavaParser.findMemberNode( cu, name );
                MemberSourceStream sss = new MemberSourceStream( 
                    declareSource, sourceId, td.toString() );
            return sss;
        }
        catch( ParseException pe )
        {
            throw new ModelLoadException(
                "could not load model for "+ sourceId, pe );
        }
        }
    }

    @Override
    public String describe()
    {
        return "MemberSourceLoder from[" + this.sourceLoader + "]";
    }
    
    /**
     * A Source Stream that aliases a Member Class withinin a Declared Class
     * Source code
     */
    public static class MemberSourceStream
        implements SourceStream
    {
        public final SourceStream parentSourceStream;
        public final String sourceId;
        public final String memberSource;
    
        public MemberSourceStream( 
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
        
        public SourceStream getParentSourceStream()
        {
            return this.parentSourceStream;
        }
    }
    
}
