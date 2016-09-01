package varcode;

import junit.framework.TestCase;

public class LangTest
    extends TestCase
{

    public void testGet()
    {
        assertEquals( Lang.JAVA, Lang.fromFileExtension( ".java" ) );
        
        assertEquals( Lang.JAVASCRIPT, Lang.fromFileExtension( ".js" ) );
        
    
        assertEquals( Lang.C, Lang.fromFileExtension( ".c" ));
        assertEquals( Lang.CPP, Lang.fromFileExtension( ".cpp" ));
        assertEquals( Lang.CSHARP, Lang.fromFileExtension( ".cs" ));
        assertEquals( Lang.D, Lang.fromFileExtension( ".d" ));
        assertEquals( Lang.FSHARP, Lang.fromFileExtension( ".fs" ));
        assertEquals( Lang.GO, Lang.fromFileExtension( ".go" ));
        assertEquals( Lang.GROOVY, Lang.fromFileExtension( ".groovy" ));
        assertEquals( Lang.KOTLIN, Lang.fromFileExtension( ".kt" ));
        //LUA       ( "Lua",         ".lua" ), //need a custom parser we'll get to these later...
        assertEquals( Lang.OBJECTIVEC, Lang.fromFileExtension( ".m" ));
        //PASCAL    ( "Pascal",      ".pas" ), //need a custom parser we'll get to these later...
        assertEquals( Lang.PHP, Lang.fromFileExtension( ".php" ));
        assertEquals( Lang.RUST,Lang.fromFileExtension( ".rs" ));
        assertEquals( Lang.SWIFT, Lang.fromFileExtension( ".swift" ));
        assertEquals( Lang.VERILOG, Lang.fromFileExtension( ".v" ));
    }
    
    public void testFromMarkupId()
    {
    	assertEquals( Lang.JAVA, Lang.fromCodeId( "com.sun.btw.Hello.java" ) );
    	assertEquals( Lang.CPP, Lang.fromCodeId( "Hello.cpp" ) );
    	assertEquals( Lang.JAVASCRIPT, Lang.fromCodeId( "Hello.js" ) );
    	assertEquals( Lang.C, Lang.fromCodeId( "Hello.c" ) );
    	assertEquals( Lang.CSHARP, Lang.fromCodeId( "Hello.cs" ) );
    	assertEquals( Lang.D, Lang.fromCodeId( "Hello.d" ) );
    	assertEquals( Lang.FSHARP, Lang.fromCodeId( "Hello.fs" ) );
    	assertEquals( Lang.GO, Lang.fromCodeId( "Hello.go" ) );
    	assertEquals( Lang.GROOVY, Lang.fromCodeId( "Hello.groovy" ) );
    	assertEquals( Lang.KOTLIN, Lang.fromCodeId( "Hello.kt" ) );
    	assertEquals( Lang.OBJECTIVEC, Lang.fromCodeId( "Hello.m" ) );
    	assertEquals( Lang.PHP, Lang.fromCodeId( "Hello.php" ) );
    	assertEquals( Lang.RUST, Lang.fromCodeId( "Hello.rs" ) );
    	assertEquals( Lang.SWIFT, Lang.fromCodeId( "Hello.swift" ) );
    	assertEquals( Lang.VERILOG, Lang.fromCodeId( "Hello.v" ) );
    	
    }
    
    public void testPaths()
    {
		String markupId = "com.oracle.jrockit.jfr.AClass.java";
		
        assertEquals( 
        	"com\\oracle\\jrockit\\jfr\\AClass.java",
        	Lang.fromCodeId( markupId ).resolvePath( markupId ) );
        	 
        markupId = "AVXInstructions.c";
        
        assertEquals( "AVXInstructions.c",		
        	Lang.fromCodeId( markupId ).resolvePath( markupId ) );
        
        markupId = "AVXISA.anotherV.c";
        
        assertEquals( "AVXISA.anotherV.c",		
        	Lang.fromCodeId( markupId ).resolvePath( markupId ) );
        
    }
}
