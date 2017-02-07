/*
 * Copyright 2017 Eric.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package howto;

import howto.author.RunCustomVarScripts;
import howto.author.paramNameTest;
import howto.java.ModelAnnotationType;
import howto.java.ModelClass;
import howto.java.ModelEnum;
import howto.java.ModelInterface;
import howto.java.ModelOfModels;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import varcode.java.Java;

/**
 *
 * @author Eric
 */
public class AllSuite
    extends TestCase
{    
    public static Test suite() 
    {
        TestSuite suite = new TestSuite( AllSuite.class.getName() );
        
        suite.addTestSuite( RunCustomVarScripts.class ); 
        suite.addTestSuite( paramNameTest.class );
        suite.addTestSuite( ModelAnnotationType.class );
        suite.addTestSuite( ModelClass.class );
        suite.addTestSuite( ModelEnum.class );
        suite.addTestSuite( ModelInterface.class );
        suite.addTestSuite( ModelOfModels.class );
        suite.addTestSuite( FailingTest.class );
        return suite;
    }
    
    public static class FailingTest
        extends TestCase
    {
        public void testFail()
        {
            fail("this failed");
        }
    }
    
     
    public static void main( String[] args )
    {
        //Java.call( JUnitCode.class, "runClasses", args)
        
        org.junit.runner.JUnitCore.runClasses( howto.AllSuite.class );
        org.junit.runner.JUnitCore.main( new String[]{"howto.AllSuite"} );
        /*
        Result result = JUnitCore.runClasses( AllSuite.class );

        for( Failure failure : result.getFailures() ) 
        {
            System.out.println( failure.toString() );
        }		
        System.out.println( result.wasSuccessful() );
        */
    }
}
