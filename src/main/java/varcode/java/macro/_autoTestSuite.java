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
package varcode.java.macro;

import java.util.ArrayList;
import java.util.List;
import varcode.java.model._JavaFileModel;
import varcode.java.model._Java;
import varcode.java.model._class;
import varcode.java.model._methods._method;

/**
 *
 * How to run the TestSuite
 * Result result = JUnitCore.runClasses( AllSuite.class );
 * 
 * JUnitCore.main( new String[]{"howto.AllSuite"} );
 * 
 * @author Eric
 */
public class _autoTestSuite
{
    public static Class TestCaseClass;
    
    static
    {
        try
        {
            TestCaseClass = Class.forName( "junit.framework.TestCase" );
        }
        catch( ClassNotFoundException cnfe )
        {
            throw new RuntimeException( 
                "Cannot call _autoTestCase without junit.framework.TestCase" );
        }            
    }
    
    /**
     * Given a bunch of Classes, create a TestSuite calling 
     * all classes that extend TestCase
     * @param classes
     * @return 
     */
    public static _class from( Class... classes )
    {
        List<Class>testClasses = new ArrayList<Class>();
        
        for( int i = 0; i < classes.length; i++ )
        {      
            if( TestCaseClass.isAssignableFrom( classes[ i ] ) )
            {
                testClasses.add( classes[ i ] );
            }
        }
        _class _testSuite = _class.of( "package test;", "public class AutoTestSuite extends TestCase" );
        _testSuite.imports( 
            "import junit.framework.Test;", 
            "import junit.framework.TestCase;",
            "import junit.framework.TestSuite;")
            .imports( testClasses );
        
        //the method and first few lines
        _method _m = _method.of( "public static Test suite()",
            "TestSuite suite = new TestSuite( );",
            "// $JUnit-BEGIN$" );
        
        _testSuite.add( _m );
        
        for( int i = 0; i < testClasses.size(); i++ )
        {
            _m.add( "suite.addTestSuite( " + 
                testClasses.get( i ).getSimpleName()+ ".class );" );
        }
        _m.add( "// $JUnit-END$",
            "return suite;" );        
        return _testSuite;
    }    
    
    
    /**
     * Given a bunch of Classes, create a TestSuite calling 
     * all classes that extend TestCase
     * @param models
     * @return 
     */
    public static _class from( _JavaFileModel... models )
    {
        List<_class>_testClasses = new ArrayList<_class>();
        
        for( int i = 0; i < models.length; i++ )
        {   
            if( models[ i ] instanceof _class )
            {
                _class _c = (_class)models[ i ];
                if( _c.getExtends().getList().contains( "TestCase" ) || 
                    _c.getExtends().getList().contains( "junit.framework.TestCase" ) )
                {
                    _testClasses.add( _c );
                }
            }    
        }
        _class _testSuite = _class.of( "package test;", "public class AutoTestSuite extends TestCase" );
        _testSuite.imports( 
            "import junit.framework.Test;", 
            "import junit.framework.TestCase;",
            "import junit.framework.TestSuite;")
            .imports( _testClasses );
        
        //the method and first few lines
        _method _m = _method.of( "public static Test suite()",
            "TestSuite suite = new TestSuite( );",
            "// $JUnit-BEGIN$" );
        
        _testSuite.add( _m );
        
        for( int i = 0; i < _testClasses.size(); i++ )
        {
            _m.add( "suite.addTestSuite( " + 
                _testClasses.get( i ).getName()+ ".class );" );
        }
        _m.add( "// $JUnit-END$",
            "return suite;" );        
        return _testSuite;
    }
}
