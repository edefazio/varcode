package varcode.java;
/*{-*/
import junit.framework.TestCase;
/*-}*/
import varcode.context.VarContext;

public class /*{+cn*/_JavaCaseClassNameTest/*+}*/
/*{-*/	extends TestCase/*-}*/
{
	/*{-*/
	static final JavaCase JAVACASE = JavaCase.of( 		
		_JavaCaseClassNameTest.class, 
		"varcode.java.MyT", VarContext.of( "cn", "MyT") );

	public void testTags()
	{
		
		Object obj = JAVACASE.instance( );
		assertTrue( obj.getClass().getSimpleName().equals( "MyT" ) );
	}
	/*-}*/
}
