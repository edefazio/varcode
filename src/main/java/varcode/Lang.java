package varcode;

import java.io.File;

/**
 * Markup source can be in multiple programming languages this encapsulates the
 * high level language conventions.
 *
 * Primarily used to determine the Language of the Document given the document
 * file name extension
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public enum Lang
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

    /**
     * the common name of the language
     */
    private final String name;

    /**
     * the file extension used by the language for source files
     */
    private final String sourceFileExtension;

    private final PathResolver pathResolver;

    private Lang( 
        String name, String sourceFileExtension, PathResolver pathResolver )
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
    public static Lang fromFileExtension( String fileExtension )
    {
        for( int i = 0; i < Lang.values().length; i++ ) 
        {
            if( Lang.values()[ i ].getSourceFileExtension().equals( fileExtension) ) 
            {
                return Lang.values()[i];
            }
            //they MAY have passed in "js" instead of ".js" (that works too)
            if( Lang.values()[ i ].getSourceFileExtension().endsWith( fileExtension ) ) 
            {
                return Lang.values()[ i ];
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
        Lang theLang = Lang.fromFileExtension( fileExtension );
        String path = null;
        if( theLang == null ) 
        {
            //assume '.'s just are part of file Name
            path = sourceId;
            /*
            throw new VarException(
                "Could not determine Lang from markupId with extension \""
                + fileExtension + "\"" );
            */
        }
        else
        {

        //PathResolver pathResolver = LANG_RESOLVER_MAP.get( theLang );
        //if( pathResolver == null )
        // {
        //    throw new VarException(
        //       "No PathResolver registered for Lang \"" + theLang +"\"" );
        // }
            
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
     * @param codeId (i.e. "io.typeframe.field.BitField32.java")
     * @return the Lang (or null if not recognized)
     */
    public static Lang fromCodeId( String codeId )
    {
        for( Lang value : Lang.values() ) 
        {
            if( codeId.endsWith( value.getSourceFileExtension() ) ) 
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

    public interface PathResolver
    {
        public String pathTo( String markupId );
    }

    public enum DotPathResolver
        implements PathResolver
    {
        INSTANCE;

        @Override
        public String pathTo( String markupId )
        {
            return markupId.replace( '.', File.separatorChar );
        }
    }

    public enum FlatPathResolver
            implements PathResolver
    {
        INSTANCE;

        @Override
        public String pathTo( String markupId )
        {
            return markupId;
        }
    }
}
