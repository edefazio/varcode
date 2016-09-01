package varcode.java;

import junit.framework.TestCase;
import varcode.context.VarContext;

public class JavaCaseTest 
	extends TestCase
{

	public void testJavaCaseNoMarks()
	{   //read this file in which has no marks
		JavaCase jc = JavaCase.of( 
			JavaCaseTest.class, 
			"varcode.java.JavaCaseTest", VarContext.of() );
		assertNotNull( jc.javaCode() );
		assertNotNull( jc.instance( ) );		
		//jc = JavaCase.of( JavaCaseTest.class, keyValuePairs)
	}
}
