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
package varcode.load;

import varcode.load.Source.SourcePathResolver;
import varcode.load.Source.SourcePathResolver.DotPathResolver;
import varcode.load.Source.SourcePathResolver.FlatPathResolver;

/**
 * Defines the conventions used by multiple computer languages on 
 * how source code is stored or loaded based on a "sourceId":
 * 
 * for instance: <PRE>
 * if we have a sourceId String:
 * "varcode.load.SourceLanguage.java"
 * 
 * we can interpret this as a "Java" language source file, that exists
 * in the "/varcode/load/" relative path with a file name : "SourceLanguage.java"
 * 
 * 
 * We want to be able to construct a "SourceId" and interpret a "sourceId" 
 * using multiple language conventions.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum SourceLanguages
    implements Source.SourcePathResolver
{
    JAVA( "Java", ".java", DotPathResolver.INSTANCE ),
    JAVASCRIPT( "JavaScript", ".js", DotPathResolver.INSTANCE ),
    C( "C", ".c", FlatPathResolver.INSTANCE ),
    CPP( "C++", ".cpp", FlatPathResolver.INSTANCE ),
    CSHARP( "C#", ".cs", DotPathResolver.INSTANCE ),
    D( "D", ".d", DotPathResolver.INSTANCE ),
    DART( "Dart", ".dart", DotPathResolver.INSTANCE ),
    FSHARP( "F#", ".fs", DotPathResolver.INSTANCE ),
    GO( "Go", ".go", FlatPathResolver.INSTANCE ),
    GROOVY( "Groovy", ".groovy", DotPathResolver.INSTANCE ),
    KOTLIN( "Kotlin", ".kt", DotPathResolver.INSTANCE ),
    //LUA       ( "Lua",         ".lua" ), //need a custom parser we'll get to these later...
    OBJECTIVEC( "Objective-C", ".m", FlatPathResolver.INSTANCE ),
    //PASCAL    ( "Pascal",      ".pas" ), //need a custom parser we'll get to these later...
    PHP( "PHP", ".php", FlatPathResolver.INSTANCE ),
    RUST( "Rust", ".rs", FlatPathResolver.INSTANCE ),
    SWIFT( "Swift", ".swift", FlatPathResolver.INSTANCE ),
    VERILOG( "Verilog", ".v", FlatPathResolver.INSTANCE );

    /** the common name of the language */
    private final String name;

    /** file extension used by the language for source files */
    private final String sourceFileExtension;

    /** how the sourceId relates to the path to the source file */
    private final SourcePathResolver pathResolver;

    /**
     * Associates a Language Name to a FileExtension and SourcePathResolver
     * SourceLanguage( 
     *     "Java",    //the name of the language is Java
     *     ".java",   // Java source files use the ".java" extension
     *      DotPathResolver.INSTANCE // "."'s in sourceIds relate to paths
     * 
     * @param name the name of the language
     * @param sourceFileExtension the file extension used for Source Files
     * @param pathResolver resolves a path to a file based on the "sourceId"
     */
    private SourceLanguages( 
        String name, 
        String sourceFileExtension, 
        SourcePathResolver pathResolver )
    {
        this.name = name;
        this.sourceFileExtension = sourceFileExtension;
        this.pathResolver = pathResolver;
    }

    /**
     * return the Lang from the File Extension
     *
     * @param fileExtension (i.e. ".java", ".rs", , ".js")
     * @return the Lang
     */
    public static SourceLanguages fromFileExtension( String fileExtension )
    {
        for( int i = 0; i < SourceLanguages.values().length; i++ ) 
        {
            if( SourceLanguages.values()[ i ].getSourceFileExtension().equals( fileExtension) ) 
            {
                return SourceLanguages.values()[i];
            }
            //they MAY have passed in "js" instead of ".js" (that works too)
            if( SourceLanguages.values()[ i ].getSourceFileExtension().endsWith( fileExtension ) ) 
            {
                return SourceLanguages.values()[ i ];
            }
        }
        return null;
    }

    /**
     * Resolves the Hierarchical Directory "path" to the sourceId: for
     * example:<BR>
     * <PRE>"java.util.Map.java"</PRE> should be at path:<BR>
     * <PRE>"java/util/Map.java"</PRE> ...and
     * <PRE>"someCProgram.c"</PRE> doesn't have a heirarchial path, but should
     * be in a file
     * <PRE>"someCProgram.c"</PRE>
     *
     * @param sourceId identifies the Markup file to be loaded from the repo
     * @return the relative path to the markup file resource
     */
    public static String resolvePath( String sourceId )
    {
        int lastDot = sourceId.lastIndexOf( '.' );
        String fileExtension = sourceId.substring( lastDot );
        SourceLanguages theLang = 
            SourceLanguages.fromFileExtension( fileExtension );
        String path = null;
        if( theLang == null ) 
        {
            //assume '.'s just are part of file Name
            path = sourceId;
        }
        else
        {
            path = theLang.pathResolver.pathTo( 
                sourceId.substring( 0, sourceId.length() - fileExtension.length() ) )
                + fileExtension;
        }
        return path;
    }

    /**
     * Given a codeId (i.e. io.typeframe.field.BitField32.java), returns the
     * appropriate Lang
     *
     * @param sourceId (i.e. "io.typeframe.field.BitField32.java")
     * @return the Lang (or null if not recognized)
     */
    public static SourceLanguages fromSourceId( String sourceId )
    {
        for( SourceLanguages value : SourceLanguages.values() ) 
        {
            if( sourceId.endsWith( value.getSourceFileExtension() ) ) 
            {
                return value;
            }
        }
        return null;
    }

    public String getName()
    {
        return name;
    }

    public String getSourceFileExtension()
    {
        return sourceFileExtension;
    }
    
    public String pathTo( String sourceId )
    {
        return pathResolver.pathTo( sourceId );
    }

}
