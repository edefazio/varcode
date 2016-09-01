package varcode.markup.bindml;

import varcode.context.VarBindException;
import varcode.context.VarBindException.NullVar;
import varcode.markup.MarkupException;

public class BindMLFunctionalTests_AddVar 
	extends BindMLFunctionalTest
{
	public void testAddVarOneOf()
	{
		is( "", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}" ) );
		is( "a", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "a" ) );
		is( "e", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "e" ) );
		is( "i", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "i" ) );
		is( "o", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "o" ) );
		is( "u", BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "u" ) );
		try
		{
			BindML.tailorCode( "{+vowel:['a','e','i','o','u']+}", "vowel", "S" );
			fail("Expected Exception");
		}
		catch( VarBindException vb )
		{
			//expected
		}
		
		is( "e", BindML.tailorCode( "{+vowel:['a','e','i','o','u']|e+}" ) );
		try
		{
			is( "", BindML.tailorCode( "{+vowel:['a','e','i','o','u']*+}" ) );
			fail("Expected Exception");
		}
		catch( NullVar vr )
		{
			//expected
		}
		
		is( "", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}" ) );
		
		is( "1", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}", "odd", 1) );
		is( "3", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}", "odd", 3) );
		is( "5", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}", "odd", new Integer( 5 ) ) );
		is( "7", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}", "odd", 7 ) );
		is( "9", BindML.tailorCode( "{+odd:[1,3,5,7,9]+}", "odd", 9 ) );
		
		is( "7", BindML.tailorCode( "{+odd:[1,3,5,7,9]|7+}" ) );
		try
		{
			is( "", BindML.tailorCode( "{+odd:[1,3,5,7,9]*+}" ) );
			fail("Expected Exception");
		}
		catch( NullVar vr )
		{
			//expected
		}
	}
	
	//test one of where the types are mixed (int and String)
	public void testAddVarOneOfMixed()
	{
		is( "", BindML.tailorCode( "{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}" ) );
		is( "1", BindML.tailorCode( "{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}", "mix", 1 ) );
		is( "A", BindML.tailorCode( "{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]+}", "mix", "A" ) );
		is( "A", BindML.tailorCode( "{+mix:[1,'A', 3,'B', 5,'C', 7,'D', 9]|A+}" ) );
	}
	
	public void testAddVar()
	{
		is( "", BindML.tailorCode( "{+a+}" ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", "1" ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", 1 ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", new Integer( 1 ) ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", new Short( (short)1 ) ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", new Byte( (byte)1 ) ) );
		is( "1", BindML.tailorCode( "{+a+}", "a", new Long( 1 ) ) );
	}
	
	public void testAddFormIfExpression()
	{
		is( "", BindML.tailorCode("{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}") );
		
		is( "", BindML.tailorCode("{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}", "logLevel", 1 ) );
		
		is( "LOG.debug(1 + 2);", BindML.tailorCode("{{+?(( logLevel > 0 )):LOG.debug({+a+} + {+b+});+}}", "logLevel", 1, "a", 1, "b", 2 ) );		
	}
	
	public void testSimple()
	{
		is( "7", BindML.tailorCode("{+(( 3 + 4 ))+}" ) );
		
		is( "100200", BindML.tailorCode("{#a:100#}{#b:200#}{+(( a + b ))+}" ) );
		
		is( "300", BindML.tailorCode("{#a:((100))#}{#b:((200))#}{+(( a + b | 0 ))+}" ) );
		
		is( "300", BindML.tailorCode("{+(( a + b | 0 ))+}", "a", 100, "b", 200 ) );
		
		//resetting value??
		is( "300", BindML.tailorCode("{##b:1##}{+(( a + b | 0 ))+}", "a", 100, "b", 200 ) );
	}
	
	public void testEvalThenScript()
	{
		//verify that defining an array in EvalEngine (JS) can be operated on
		//through a script
		is( "A, B", BindML.tailorCode("{#arr:((['a','b']))#}{+$^(arr)+}" ) ); //firstCaps
		is( "AA, BB", BindML.tailorCode("{#arr:((['aa','bb']))#}{+$^^(arr)+}" ) ); //allCaps
		is( "aa, bb", BindML.tailorCode("{#arr:(([' aa ',' bb ']))#}{+$trim(arr)+}" ) ); //trim
		
		is( "aa, bb", BindML.tailorCode("{#arr:((['AA','BB']))#}{+$lower(arr)+}" ) ); //lowercase
		is( "aA, bB", BindML.tailorCode("{#arr:((['AA','BB']))#}{+$firstLower(arr)+}" ) ); //first lowercase
		
		is( "aa, bb", BindML.tailorCode("{#arr:((['AA','BB']))#}{+$lower(arr)+}" ) ); //lowercase
		
		
	}
	
	public void testBadVarName_ReservedWord()
	{
		try
		{
			BindML.tailor( "{+int+}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		
		try
		{
			BindML.tailor( "{+int*+}" );
			fail( "expected exception for bad var name " );
		}
		catch( MarkupException me )
		{
			//expected for bad name
		}
		
		try
		{
			BindML.tailor( "{+int|default+}" );
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
			BindML.tailorCode( "{+a*+}" );
			fail( "expected Exception" );
		}
		catch( NullVar rbn )
		{
			
		}
		is( "1", BindML.tailorCode( "{+a*+}", "a", "1" ) );
	}
	
	public void testAddVarDefault()
	{
		is( "2", BindML.tailorCode( "{+a|2+}" ) );
		is( "1", BindML.tailorCode( "{+a|2+}", "a", "1" ) );
		is( "1", BindML.tailorCode( "{+a|2+}", "a", 1 ) );
		
		//NOTE: the Blank String IS NOT null, so we DONT use the default
		is( "", BindML.tailorCode( "{+a|2+}", "a", "" ) );
	}
	
	
	/** We can provide validation expressions that apply to binding variables */
	public void testAddVarExpression()
	{
		//the validation expression is blank AND no value
		is( "", BindML.tailorCode( "{+a:(( ))+}" ) );
		
		is( "", BindML.tailorCode( "{+a:(( a < 100 ))+}") );
		
		// when we trim the validation Expression, it is blank, so we treat it as empty
		is( "2", BindML.tailorCode( "{+a:(( ))+}", "a", 2 ) );
		
		is( "2", BindML.tailorCode( "{+a:(( a < 100 ))+}", "a", 2 ) );
	
		is( "", BindML.tailorCode( "{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}" ) );
		
		//verify that we can set a value that is valid based on all 3 conditions
		is( "2", BindML.tailorCode( "{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}", "a", "2" ) );
		
		try
		{   //                                          "a" must be even
			BindML.tailorCode( "{+a:(( a > 0 && a < 100 && (a % 2 == 0) ))+}", "a", "1" );
			fail( "expected exception for variable present but not valid (must be even)" );
		}
		catch( VarBindException vbe )
		{
			//expected 
		}
		
		try
		{
			BindML.tailorCode( "{+a:(( a < 100 ))+}", "a", 101 );
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
		is("1, 2", BindML.tailorCode("{+a:(( ['1', '2'] ))#})
	}
	*/
	
	public void testAddVarAsScriptResult()
	{
		is( "2", BindML.tailorCode("{#a:(( ['1', '2'] ))#}{+$count(a)+}" ) ); 
	}
	
	
	public void testAddVarExpressionRequried()
	{
		is( "2", BindML.tailorCode( "{+a:(( ))*+}", "a", "2" ) );
		
		try
		{
			BindML.tailorCode( "{+a:(( ))*+}" );
			fail( "Expected exception for var required" );
		}
		catch( NullVar vr )
		{
			//expected 
		}		
	}
	
	public void testAddVarExpressionDefault()
	{
		is( "2", BindML.tailorCode( "{+a:(( ))|2+}" ) );
		is( "2", BindML.tailorCode( "{+a:(( a < 100 ))|2+}" ) );
		is( "1", BindML.tailorCode( "{+a:(( a < 100 ))|2+}", "a", 1 ) );
		
		//the value for a provided DOES NOT satisfy constraint a < 100, so use default
		is( "2", BindML.tailorCode( "{+a:(( a < 100 ))|2+}", "a", 101 ) );
		
		//here the expression is just nuts, BUT there is a default, so expect default
		is( "2", BindML.tailorCode( "{+a:(( wacko == crazy ^23 ))|2+}" ) );
	}
}
