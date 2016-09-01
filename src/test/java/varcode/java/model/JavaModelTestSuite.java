package varcode.java.model;

import ex.varcode.java.model.howto.MultipleClassesWithDependencies;
import junit.framework.Test;
import junit.framework.TestSuite;

public class JavaModelTestSuite
{
	public static Test suite() 
	{
		TestSuite suite = new TestSuite( JavaModelTestSuite.class.getName() );

		// $JUnit-BEGIN$
		suite.addTestSuite( _argumentsTest.class );
		suite.addTestSuite( _classTest.class );
		suite.addTestSuite( _cloneTest.class );
		suite.addTestSuite( _codeTest.class );
		suite.addTestSuite( _constructorsTest.class );
		suite.addTestSuite( _enumTest.class );
		suite.addTestSuite( _extendsTest.class );
		suite.addTestSuite( _fieldsTest.class );
		suite.addTestSuite( _importsTest.class );
		suite.addTestSuite( _interfaceTest.class );
		suite.addTestSuite( _javadocTest.class );
		suite.addTestSuite( _literalTest.class );
		suite.addTestSuite( _methodsTest.class );
		suite.addTestSuite( _modifiersTest.class );
		suite.addTestSuite( _nestTest.class );
		suite.addTestSuite( _packageTest.class );
		suite.addTestSuite( _parametersTest.class );
		suite.addTestSuite( _staticBlockTest.class );
		suite.addTestSuite( _throwsTest.class );
		suite.addTestSuite( _typeTest.class );
		suite.addTestSuite( _varTest.class );
		
		suite.addTestSuite( MultipleClassesWithDependencies.class );
		
		return suite;
	}
}
