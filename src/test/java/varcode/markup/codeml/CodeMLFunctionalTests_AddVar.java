package varcode.markup.codeml;

import varcode.context.VarBindException;
import varcode.context.VarBindException.NullVar;
import varcode.context.VarContext;
import varcode.markup.MarkupException;
import varcode.markup.mark.AddForm;
import varcode.markup.mark.Mark;

public class CodeMLFunctionalTests_AddVar 
	extends CodeMLFunctionalTest
{
	//TODO add ReplaceMark Variant
	public void testAddVarOneOf()
	{
		is( "", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/" ) );
		is( "a", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "a" ) );
		is( "e", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "e" ) );
		is( "i", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "i" ) );
		is( "o", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "o" ) );
		is( "u", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "u" ) );
		try
		{
			CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']+}*/", "vowel", "S" );
			fail("Expected Exception");
		}
		catch( VarBindException vb )
		{
			//expected
		}
		
		is( "e", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']|e+}*/" ) );
		try
		{
			is( "", CodeML.tailorCode( "/*{+vowel:['a','e','i','o','u']*+}*/" ) );
			fail("Expected Exception");
		}
		catch( NullVar vr )
		{
			//expected
		}
		
		is( "", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/" ) );
		
		is( "1", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/", "odd", 1) );
		is( "3", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/", "odd", 3) );
		is( "5", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/", "odd", new Integer( 5 ) ) );
		is( "7", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/", "odd", 7 ) );
		is( "9", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]+}*/", "odd", 9 ) );
		
		is( "7", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]|7+}*/" ) );
		try
		{
			is( "", CodeML.tailorCode( "/*{+odd:[1,3,5,7,9]*+}*/" ) );
			fail("Expected Exception");
		}
		catch( NullVar vr )
		{
			//expected
		}
	}
	
    public void testAddForm()
    {
        String form = "/*{{+:{+type+} {+value+}, +}}*/";
        Mark m = CodeML.parseMark( form );
        assertTrue( m instanceof AddForm );
        AddForm af = (AddForm)m;
        assertEquals( "", af.derive( VarContext.of() ) );
        assertEquals( "int 3", af.derive(VarContext.of("type", int.class, "value", 3) ) );
        assertEquals( "int 3, String 4", af.derive(
            VarContext.of( "type", new Class[]{int.class, String.class}, "value", new Object[]{3, 4}) ) );        
    }
	//test one of where the types are mixed (int and String)
	public void testAddVarOneOfMixed()
	{
		is( "", CodeML.tailorCode( "/*{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}*/" ) );
		is( "1", CodeML.tailorCode( "/*{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}*/", "mix", 1 ) );
		is( "A", CodeML.tailorCode( "/*{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}*/", "mix", "A" ) );
		is( "A", CodeML.tailorCode( "/*{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]|A+}*/" ) );
	}
	
	public void testAddVar()
	{
		is( "", CodeML.tailorCode( "/*{+a+}*/" ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", "1" ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", 1 ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", new Integer( 1 ) ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", new Short( (short)1 ) ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", new Byte( (byte)1 ) ) );
		is( "1", CodeML.tailorCode( "/*{+a+}*/", "a", new Long( 1 ) ) );
	}
	
	public void testAddFormIfExpression()
	{
		is( "", CodeML.tailorCode("/*{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}*/") );
		
		is( "", CodeML.tailorCode("/*{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}*/", "logLevel", 1 ) );
		
		is( "LOG.debug(1 + 2);", CodeML.tailorCode("/*{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}*/", "logLevel", 1, "a", 1, "b", 2 ) );		
	}
	
	public void testSimple()
	{
		is( "7", CodeML.tailorCode( "/*{+(( 3 + 4 ))+}*/" ) );
		
		is( "100200", CodeML.tailorCode( "/*{#a:100#}*//*{#b:200#}*//*{+(( a + b ))+}*/" ) );
		
		is( "300", CodeML.tailorCode( "/*{#a:((100))#}*//*{#b:((200))#}*//*{+(( a + b | 0 ))+}*/" ) );
		
		is( "300", CodeML.tailorCode( "/*{+(( a + b | 0 ))+}*/", "a", 100, "b", 200 ) );
		
		//resetting value??
		is( "300", CodeML.tailorCode( "/*{##b:1##}*//*{+(( a + b | 0 ))+}*/", "a", 100, "b", 200 ) );
	}
	
	public void testEvalThenScript()
	{
		//verify that defining an array in EvalEngine (JS) can be operated on
		//through a script
		is( "A, B", CodeML.tailorCode( "/*{#arr:((['a','b']))#}*//*{+$^(arr)+}*/" ) ); //firstCaps
		is( "AA, BB", CodeML.tailorCode( "/*{#arr:((['aa','bb']))#}*//*{+$^^(arr)+}*/" ) ); //allCaps
		is( "aa, bb", CodeML.tailorCode( "/*{#arr:(([' aa ',' bb ']))#}*//*{+$trim(arr)+}*/" ) ); //trim
		
		is( "aa, bb", CodeML.tailorCode( "/*{#arr:((['AA','BB']))#}*//*{+$lower(arr)+}*/" ) ); //lowercase
		is( "aA, bB", CodeML.tailorCode( "/*{#arr:((['AA','BB']))#}*//*{+$firstLower(arr)+}*/" ) ); //first lowercase
		
		is( "aa, bb", CodeML.tailorCode( "/*{#arr:((['AA','BB']))#}*//*{+$lower(arr)+}*/" ) ); //lowercase
		
		
	}
	
	public void testBadVarName_ReservedWord()
	{
		try
		{
			CodeML.tailor( "/*{+int+}*/" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		
		try
		{
			CodeML.tailor( "/*{+int*+}*/" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		
		try
		{
			CodeML.tailor( "/*{+int|default+}*/" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
	}
	
	public void testAddVarRequired()
	{
		try
		{
			CodeML.tailorCode( "/*{+a*+}*/" );
			fail( "expected Exception" );
		}
		catch( NullVar rbn )
		{
			
		}
		is( "1", CodeML.tailorCode( "/*{+a*+}*/", "a", "1" ) );
	}
	
	public void testAddVarDefault()
	{
		is( "2", CodeML.tailorCode( "/*{+a|2+}*/" ) );
		is( "1", CodeML.tailorCode( "/*{+a|2+}*/", "a", "1" ) );
		is( "1", CodeML.tailorCode( "/*{+a|2+}*/", "a", 1 ) );
		
		//NOTE: the Blank String IS NOT null, so we DONT use the default
		is( "", CodeML.tailorCode( "/*{+a|2+}*/", "a", "" ) );
	}
	
	
	/** We can provide validation expressions that apply to binding variables */
	public void testAddVarExpression()
	{
		//the validation expression is blank AND no value
		is( "", CodeML.tailorCode( "/*{+a:(( ))+}*/" ) );
		
		is( "", CodeML.tailorCode( "/*{+a:(( a < 100 ))+}*/") );
		
		// when we trim the validation Expression, it is blank, so we treat it as empty
		is( "2", CodeML.tailorCode( "/*{+a:(( ))+}*/", "a", 2 ) );
		
		is( "2", CodeML.tailorCode( "/*{+a:(( a < 100 ))+}*/", "a", 2 ) );
	
		is( "", CodeML.tailorCode( "/*{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}*/" ) );
		
		//verify that we can set a value that is valid based on all 3 conditions
		is( "2", CodeML.tailorCode( "/*{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}*/", "a", "2" ) );
		
		try
		{   //                                          "a" must be even
			CodeML.tailorCode( "/*{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}*/", "a", "1" );
			fail( "expected exception for variable present but not valid (must be even)" );
		}
		catch( VarBindException vbe )
		{
			//expected 
		}
		
		try
		{
			CodeML.tailorCode( "/*{+a:(( a < 100 ))+}*/", "a", 101 );
			fail( "expected exception for variable present but not valid for expression " );
		}
		catch( VarBindException vbe )
		{
			//expected 
		}
	}

	/*
	public void testExpressionArray()
	{
		is("1, 2", CodeML.tailorCode("{+a:(( ['1', '2'] ))#})
	}
	*/
	
	public void testAddVarAsScriptResult()
	{
		is( "2", CodeML.tailorCode("/*{#a:(( ['1', '2'] ))#}*//*{+$count(a)+}*/" ) ); 
	}
	
	
	public void testAddVarExpressionRequried()
	{
		is( "2", CodeML.tailorCode( "/*{+a:(( ))*+}*/", "a", "2" ) );
		
		try
		{
			CodeML.tailorCode( "/*{+a:(( ))*+}*/" );
			fail( "Expected exception for var required" );
		}
		catch( NullVar vr )
		{
			//expected 
		}		
	}
	
	public void testAddVarExpressionDefault()
	{
		is( "2", CodeML.tailorCode( "/*{+a:(( ))|2+}*/" ) );
		is( "2", CodeML.tailorCode( "/*{+a:(( a < 100 ))|2+}*/" ) );
		is( "1", CodeML.tailorCode( "/*{+a:(( a < 100 ))|2+}*/", "a", 1 ) );
		
		//the value for a provided DOES NOT satisfy constraint a < 100, so use default
		is( "2", CodeML.tailorCode( "/*{+a:(( a < 100 ))|2+}*/", "a", 101 ) );
		
		//here the expression is just nuts, BUT there is a default, so expect default
		is( "2", CodeML.tailorCode( "/*{+a:(( wacko == crazy ^23 ))|2+}*/" ) );
	}
}
