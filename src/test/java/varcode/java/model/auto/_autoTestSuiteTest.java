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
package varcode.java.model.auto;

import howto.author.RunCustomVarScripts;
import howto.author.paramNameTest;
import junit.framework.TestCase;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import varcode.java.Java;

import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class _autoTestSuiteTest
    extends TestCase
{
    
    private static _class passingTestClass( String packageName, String className )
    {
        return _class.of( "package " + packageName + ";", 
            "public class " + className+ " extends TestCase" )
            .method( "public void testOne()", 
                "assertTrue( 1 == 1 ); " );
    }
    public void testAutoTestSuiteFromModels()
    {
        _class _ats = _autoTestSuite.from( 
            Java._classFrom( RunCustomVarScripts.class ),
            Java._classFrom( paramNameTest.class ) );
        
        //now load the testSuite Class 
        Class testSuiteClass = _ats.loadClass(  );
        
        //now "run" the testSuite
        Result result = 
            JUnitCore.runClasses( testSuiteClass );
        
        System.out.println( result );
    }
    
    public void testCreateTestSuiteFromClasses()
    {
        _class _c = _autoTestSuite.from( 
            RunCustomVarScripts.class, 
            paramNameTest.class );
        
        System.out.println( _c );
        
        Class testSuiteClass = _c.loadClass(  );
        Result result = 
            JUnitCore.runClasses( testSuiteClass );
        
        System.out.println( result );
        /*
        boolean success = result.wasSuccessful();
        if(!success )
        {
            for(int i=0; i< result.getFailures().size(); i++ )
            {
                
            }
        assertTrue(  );
        */
    }
}
