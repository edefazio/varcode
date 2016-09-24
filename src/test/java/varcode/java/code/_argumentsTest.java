package varcode.java.code;

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
        assertEquals( "( {+count+} )", _arguments.of( "{+count+}" ).toString() );
        _arguments args = _arguments.of("{+count+}");
        
        args.bindIn( VarContext.of("count", "a") );
                
        assertEquals( "( a )", args ); 
    }
    
    public void testReplace()
    {
        _arguments a = _arguments.of( "1" );
        assertEquals("( 1 )", a.toString() );
        a.replace("1", "A");
        assertEquals("( A )", a.toString() );
        
        a = new _arguments();
        a.replace("A", "1"); //ensure repalce works even if the string is null
        assertEquals("(  )", a.toString() );        
    }
    
    public void testBind()
    {
        assertEquals( "( {+count+} )", _arguments.of( "{+count+}" ).toString() );
        
        assertEquals( "( 4 )", 
            _arguments.of("{+count+}").bind( VarContext.of( "count", 4 ) ) );
        
        assertEquals( "(  )", 
            _arguments.of("{+count+}").bind( VarContext.of( ) ) );
        
        assertEquals( "( 1, 2, 3, 4 )", 
            _arguments.of("{+count+}").bind( VarContext.of( "count", new int[]{1,2,3,4} ) ) );
        
    }
    /** Obviously there is a difference between passing in NO arguments
     * and passing in a single null argument we need to ensure it's intuitive
     */
    public void testEmptyOrNull()
    {        
        assertEquals("(  )", _arguments.of( new Object[ 0 ] ).toString() );
		assertEquals("(  )", _arguments.of( ).toString() );
		assertEquals("(  )", new _arguments( ).toString() );
        
        assertEquals("( null )", _arguments.of( null ).toString() );        		
		assertEquals("( null )", _arguments.of( "null" ).author( ) );        
		assertEquals("( null )", _arguments.of( (Object[])null ).author( ) );
		assertEquals("( null )", _arguments.of( new Object[] {null} ).author( ) );
        assertEquals("( null, null )", _arguments.of( new Object[] {null, null} ).author( ) );
        assertEquals("( null, 1 )", _arguments.of( null, 1 ).author( ) ); 
        assertEquals("( 1, null )", _arguments.of( 1, null ).author( ) ); 
    }
    
    public void testGenerics()
    {
        assertEquals( "( new HashMap<Integer,String>() )", 
            _arguments.of( "new HashMap<Integer,String>()" ).toString() );
        
        //_arguments.of("5", "new HashMap<Integer,String>()", "new Date()", "100.0f" );
    }
    
    public void testLiterals()
    {
        assertEquals("( 'c' )", _arguments.of( 'c' ).toString() ); 
        assertEquals("( 10.0F )", _arguments.of( 10.0F ).toString() ); 
        assertEquals("( 10.0d )", _arguments.of( 10.0d ).toString() ); 
        assertEquals("( 10L )", _arguments.of( 10L ).toString() ); 
        assertEquals("( true )", _arguments.of( true ).toString() ); 
        assertEquals("( (short)10 )", _arguments.of( (short)10 ).toString() ); 
        assertEquals("( (byte)10 )", _arguments.of( (byte)10 ).toString() ); 
        assertEquals("( 10 )", _arguments.of( 10 ).toString() ); 
        
        assertEquals("( \"ASTRING\" )", _arguments.of(_literal.of("ASTRING")).toString() );
        assertEquals("( \"ASTRING\" )", _arguments.of( "$$ASTRING" ).toString() );
        
        assertEquals("( 'c' )", _arguments.of( _literal.of( 'c' ) ).toString() ); 
        assertEquals("( 3.0F )", _arguments.of( _literal.of( 3.0F ) ).toString() ); 
        assertEquals("( 3.0F )", _arguments.of( _literal.of( 3.0F ) ).toString() ); 
    }
    
	public void testSingleArguments()
	{
		assertEquals("( 10 )", _arguments.of( "10" ).author( ) );
		assertEquals("( true )", _arguments.of( "true" ).author( ) );
		assertEquals("( 10.34d )", _arguments.of( "10.34d" ).author( ) );
		assertEquals("( 10.34d )", _arguments.of( "10.34d" ).author( ) );
		assertEquals("( \"A\" )", _arguments.of( "\"A\"" ).author( ) );

		//a Reference
		assertEquals("( A )", _arguments.of( "A" ).author( ) );
        
		//field reference
		assertEquals("( A.field )", _arguments.of( "A.field" ).author( ) );
		
        //a method result
		assertEquals("( A.method() )", _arguments.of( "A.method()" ).author( ) );	
	}
	
	public void testMultipleArguments()
	{
		//test.with(_arguments.of("A", "B", "C" ) ).toString( "( A, B, C )"  );
        
		assertEquals( "( A, B, C, D )", _arguments.of( "A","B","C","D" ).toString() );
		assertEquals( "( 1, 2, 3, 4 )", _arguments.of( 1,2,3,4 ).toString() );
		assertEquals( "( 1.0F, 2.0F, 3.0F, 4.0F )", _arguments.of( 1.0f,2.0f,3.0f,4.0f ).toString() );
		assertEquals( "( 'c', 1, true, 3.0F, 4.0d, (byte)2, (short)5, 11504L )", 
			_arguments.of( 'c',1,true,3.0f,4.0d, (byte)2, (short)5, (long)11504 ).toString() );
		assertEquals( "( 1, 2, 3, null )", _arguments.of( 1,2,3,null ).toString() );
		
	}
	
    public void testAddIncremental()
    {
        _arguments argu = new _arguments();
		
        argu.addArgument( "null" );
        assertEquals("( null )", argu.toString());
        argu.addArguments( 1, 2, 3  );
        assertEquals("( null, 1, 2, 3 )", argu.toString());
        
        argu.addArguments( 'c'  );
        assertEquals("( null, 1, 2, 3, 'c' )", argu.toString());
    }
}
