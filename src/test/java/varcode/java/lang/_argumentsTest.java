package varcode.java.lang;

import varcode.java.lang._literal;
import varcode.java.lang._args;
import junit.framework.TestCase;
import varcode.context.VarContext;

public class _argumentsTest
	extends TestCase
{
    public void testBindTo()
    {
        
    }

    public void testParameterizedArguments()
    {        
        //I might not have the argument yet, (it may be lazily bound)
        assertEquals("( {+count+} )", _args.of( "{+count+}" ).toString() );
        _args args = _args.of("{+count+}");
        
        args.bind( VarContext.of("count", "a") );
                
        assertEquals( "( a )", args.toString() ); 
    }
    
    public void testReplace()
    {
        _args a = _args.of( "1" );
        assertEquals("( 1 )", a.toString() );
        a.replace("1", "A");
        assertEquals("( A )", a.toString() );
        
        a = new _args();
        a.replace("A", "1"); //ensure repalce works even if the string is null
        assertEquals("(  )", a.toString() );        
    }
    
    public void testBind()
    {
        assertEquals("( {+count+} )", _args.of( "{+count+}" ).toString() );
        
        assertEquals("( 4 )", 
            _args.of("{+count+}").bind( VarContext.of( "count", 4 ) ).author( ) );
        
        assertEquals("(  )", 
            _args.of("{+count+}").bind( VarContext.of( ) ).author( ) );
        
        assertEquals("( 1, 2, 3, 4 )", 
            _args.of("{+count+}").bind( VarContext.of( "count", new int[]{1,2,3,4} ) ).author( ) );
        
    }
    /** Obviously there is a difference between passing in NO arguments
     * and passing in a single null argument we need to ensure it's intuitive
     */
    public void testEmptyOrNull()
    {        
        assertEquals("(  )", _args.of( new Object[ 0 ] ).toString() );
		assertEquals("(  )", _args.of( ).toString() );
		assertEquals("(  )", new _args( ).toString() );
        
        assertEquals("( null )", _args.of( null ).toString() );        		
		assertEquals("( null )", _args.of( "null" ).author( ) );        
		assertEquals("( null )", _args.of( (Object[])null ).author( ) );
		assertEquals("( null )", _args.of( new Object[] {null} ).author( ) );
        assertEquals("( null, null )", _args.of( new Object[] {null, null} ).author( ) );
        assertEquals("( null, 1 )", _args.of( null, 1 ).author( ) ); 
        assertEquals("( 1, null )", _args.of( 1, null ).author( ) ); 
    }
    
    public void testGenerics()
    {
        assertEquals("( new HashMap<Integer,String>() )", 
            _args.of( "new HashMap<Integer,String>()" ).toString() );
        
        //_arguments.of("5", "new HashMap<Integer,String>()", "new Date()", "100.0f" );
    }
    
    public void testLiterals()
    {
        assertEquals("( 'c' )", _args.of( 'c' ).toString() ); 
        assertEquals("( 10.0F )", _args.of( 10.0F ).toString() ); 
        assertEquals("( 10.0d )", _args.of( 10.0d ).toString() ); 
        assertEquals("( 10L )", _args.of( 10L ).toString() ); 
        assertEquals("( true )", _args.of( true ).toString() ); 
        assertEquals("( (short)10 )", _args.of( (short)10 ).toString() ); 
        assertEquals("( (byte)10 )", _args.of( (byte)10 ).toString() ); 
        assertEquals("( 10 )", _args.of( 10 ).toString() ); 
        
        assertEquals("( \"ASTRING\" )", _args.of(_literal.of("ASTRING")).toString() );
        assertEquals("( \"ASTRING\" )", _args.of( "$$ASTRING" ).toString() );
        
        assertEquals("( 'c' )", _args.of( _literal.of( 'c' ) ).toString() ); 
        assertEquals("( 3.0F )", _args.of( _literal.of( 3.0F ) ).toString() ); 
        assertEquals("( 3.0F )", _args.of( _literal.of( 3.0F ) ).toString() ); 
    }
    
	public void testSingleArguments()
	{
		assertEquals("( 10 )", _args.of( "10" ).author( ) );
		assertEquals("( true )", _args.of( "true" ).author( ) );
		assertEquals("( 10.34d )", _args.of( "10.34d" ).author( ) );
		assertEquals("( 10.34d )", _args.of( "10.34d" ).author( ) );
		assertEquals("( \"A\" )", _args.of( "\"A\"" ).author( ) );

		//a Reference
		assertEquals("( A )", _args.of( "A" ).author( ) );
        
		//field reference
		assertEquals("( A.field )", _args.of( "A.field" ).author( ) );
		
        //a method result
		assertEquals("( A.method() )", _args.of( "A.method()" ).author( ) );	
	}
	
	public void testMultipleArguments()
	{
		//test.with(_arguments.of("A", "B", "C" ) ).toString( "( A, B, C )"  );
        
		assertEquals("( A, B, C, D )", _args.of( "A","B","C","D" ).toString() );
		assertEquals("( 1, 2, 3, 4 )", _args.of( 1,2,3,4 ).toString() );
		assertEquals("( 1.0F, 2.0F, 3.0F, 4.0F )", _args.of( 1.0f,2.0f,3.0f,4.0f ).toString() );
		assertEquals("( 'c', 1, true, 3.0F, 4.0d, (byte)2, (short)5, 11504L )", 
			_args.of( 'c',1,true,3.0f,4.0d, (byte)2, (short)5, (long)11504 ).toString() );
		assertEquals("( 1, 2, 3, null )", _args.of( 1,2,3,null ).toString() );
		
	}
	
    public void testAddIncremental()
    {
        _args argu = new _args();
		
        argu.addArgument( "null" );
        assertEquals("( null )", argu.toString());
        argu.addArguments( 1, 2, 3  );
        assertEquals("( null, 1, 2, 3 )", argu.toString());
        
        argu.addArguments( 'c'  );
        assertEquals("( null, 1, 2, 3, 'c' )", argu.toString());
    }
}
