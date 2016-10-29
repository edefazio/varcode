/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.code.auto._autoDtoTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * @author eric
 */
public class _ALL
{
    public static Test suite() 
    {
        TestSuite suite = new TestSuite( _ALL.class.getName() );
        suite.addTestSuite( _argumentsTest.class );
        suite.addTestSuite( _classTest.class );
        suite.addTestSuite( _codeTest.class );
        suite.addTestSuite( _constructorsTest.class );
        suite.addTestSuite(_autoDtoTest.class );
        suite.addTestSuite( _enumTest.class );
        suite.addTestSuite( _extendsTest.class );        
        suite.addTestSuite( _fieldsTest.class );
        
        suite.addTestSuite( _interfaceTest.class );
        suite.addTestSuite( _implementsTest.class );
        suite.addTestSuite( _importsTest.class );
        //javacode
        suite.addTestSuite( _javadocTest.class );
        
        suite.addTestSuite( _methodsTest.class );
        suite.addTestSuite( _modifiersTest.class );
        
        suite.addTestSuite( _packageTest.class );
        suite.addTestSuite( _parametersTest.class );
        suite.addTestSuite( _staticBlockTest.class );
        suite.addTestSuite( _throwsTest.class );
        
        // $JUnit-END$
        return suite;
    }
}
