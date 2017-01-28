package varcode.source;

import varcode.load.SourceLanguages;
import junit.framework.TestCase;

public class SourceLanguagesTest
    extends TestCase
{

    public void testGet()
    {
        assertEquals( SourceLanguages.JAVA, SourceLanguages.fromFileExtension( ".java" ) );
        assertEquals( SourceLanguages.JAVASCRIPT, SourceLanguages.fromFileExtension( ".js" ) );
        assertEquals( SourceLanguages.C, SourceLanguages.fromFileExtension( ".c" ) );
        assertEquals( SourceLanguages.CPP, SourceLanguages.fromFileExtension( ".cpp" ) );
        assertEquals( SourceLanguages.CSHARP, SourceLanguages.fromFileExtension( ".cs" ) );
        assertEquals( SourceLanguages.D, SourceLanguages.fromFileExtension( ".d" ) );
        assertEquals( SourceLanguages.FSHARP, SourceLanguages.fromFileExtension( ".fs" ) );
        assertEquals( SourceLanguages.GO, SourceLanguages.fromFileExtension( ".go" ) );
        assertEquals( SourceLanguages.GROOVY, SourceLanguages.fromFileExtension( ".groovy" ) );
        assertEquals( SourceLanguages.KOTLIN, SourceLanguages.fromFileExtension( ".kt" ) );
        //LUA       ( "Lua",         ".lua" ), //need a custom parser we'll get to these later...
        assertEquals( SourceLanguages.OBJECTIVEC, SourceLanguages.fromFileExtension( ".m" ) );
        //PASCAL    ( "Pascal",      ".pas" ), //need a custom parser we'll get to these later...
        assertEquals( SourceLanguages.PHP, SourceLanguages.fromFileExtension( ".php" ) );
        assertEquals( SourceLanguages.RUST, SourceLanguages.fromFileExtension( ".rs" ) );
        assertEquals( SourceLanguages.SWIFT, SourceLanguages.fromFileExtension( ".swift" ) );
        assertEquals( SourceLanguages.VERILOG, SourceLanguages.fromFileExtension( ".v" ) );
    }

    public void testFromMarkupId()
    {
        assertEquals( SourceLanguages.JAVA, SourceLanguages.fromSourceId( "com.sun.btw.Hello.java" ) );
        assertEquals( SourceLanguages.CPP, SourceLanguages.fromSourceId( "Hello.cpp" ) );
        assertEquals( SourceLanguages.JAVASCRIPT, SourceLanguages.fromSourceId( "Hello.js" ) );
        assertEquals( SourceLanguages.C, SourceLanguages.fromSourceId( "Hello.c" ) );
        assertEquals( SourceLanguages.CSHARP, SourceLanguages.fromSourceId( "Hello.cs" ) );
        assertEquals( SourceLanguages.D, SourceLanguages.fromSourceId( "Hello.d" ) );
        assertEquals( SourceLanguages.FSHARP, SourceLanguages.fromSourceId( "Hello.fs" ) );
        assertEquals( SourceLanguages.GO, SourceLanguages.fromSourceId( "Hello.go" ) );
        assertEquals( SourceLanguages.GROOVY, SourceLanguages.fromSourceId( "Hello.groovy" ) );
        assertEquals( SourceLanguages.KOTLIN, SourceLanguages.fromSourceId( "Hello.kt" ) );
        assertEquals( SourceLanguages.OBJECTIVEC, SourceLanguages.fromSourceId( "Hello.m" ) );
        assertEquals( SourceLanguages.PHP, SourceLanguages.fromSourceId( "Hello.php" ) );
        assertEquals( SourceLanguages.RUST, SourceLanguages.fromSourceId( "Hello.rs" ) );
        assertEquals( SourceLanguages.SWIFT, SourceLanguages.fromSourceId( "Hello.swift" ) );
        assertEquals( SourceLanguages.VERILOG, SourceLanguages.fromSourceId( "Hello.v" ) );

    }

    public void testPaths()
    {
        String markupId = "com.oracle.jrockit.jfr.AClass.java";

        assertEquals( "com\\oracle\\jrockit\\jfr\\AClass.java",
            SourceLanguages.fromSourceId( markupId ).resolvePath( markupId ) );

        markupId = "AVXInstructions.c";

        assertEquals( "AVXInstructions.c",
            SourceLanguages.fromSourceId( markupId ).resolvePath( markupId ) );

        markupId = "AVXISA.anotherV.c";

        assertEquals( "AVXISA.anotherV.c",
            SourceLanguages.fromSourceId( markupId ).resolvePath( markupId ) );

    }
}
