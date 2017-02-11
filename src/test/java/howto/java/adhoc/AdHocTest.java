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
package howto.java.adhoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import junit.framework.TestFailure;

import junit.framework.TestResult;
import varcode.author.lib.PrefixWithLineNumber;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocException;
import varcode.java.adhoc.JavaSourceFolder;
import varcode.java.adhoc.JavacException;
import varcode.java.model._JavaFileModel;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._imports;
import varcode.java.model._methods._method;

/**
 * Shorthand way of building a dynamic JUnit Test for interacting 
 * with ad hoc models... 
 * 
 * i.e. with a _class model:
 * _class _c = _class.of("public class AdHocClass")
 * .import( UUID.class )
 * .method("public String getId()",
 *     "return UUID.randomUUID().toString();");
 * 
 * //build and run a simple test
 * AdHocTest.of( _c )
 *     .test( "AdHocClass ad = new AdHocClass();",
 *         "assertNotNull( ad.getId() );" )
 *     .run();
 * 
 * @author Eric
 */
public class AdHocTest
{
    public List<_JavaFileModel> models = 
        new ArrayList<_JavaFileModel>();
    
    public _imports imports;
    public _code code = new _code();
    
    
    public static AdHocTest of( _JavaFileModel model, String...code )
    {
        AdHocTest aht = new AdHocTest( model );
        aht.code( code );
        return aht;
    }
    
    public static AdHocTest of( _JavaFileModel...models )
    {
        return new AdHocTest( models );
    }
    
    public AdHocTest( _JavaFileModel...models )
    {
        this.models = Arrays.asList( models );
    }
    
    public AdHocTest imports( Object...imports )
    {
        this.imports = _imports.of( imports );
        return this;
    }
    
    public AdHocTest code( String...code )
    {
        this.code = _code.of( (Object[])code );
        return this;
    }
    
    public _class buildTestModel()
    {
        _class _c = _class.of( "public class AdHocTest_" + 
                Integer.toHexString( code.hashCode() ) + " extends TestCase")
                .imports( "junit.framework.TestCase", 
                    "junit.framework.TestSuite", "junit.framework.TestResult" ) 
            .imports(  this.imports );
        try
        {                       
            for( int i = 0; i < this.models.size(); i++ )
            {   //import the models AND all imports used by the models
                _c.imports( this.models.get( i ) );
                _c.imports( this.models.get( i ).getImports() );
            }
        
            _method _test = _method.of( "public void testIt()" );
            _test.body( code );
            _c.method(_test );
            
            _method _suite = _method.of( "public static TestResult doTest()", 
                "TestResult tr = new TestResult();",
                "TestSuite ts = new TestSuite( " + _c.getName() + ".class );",
                "ts.run( tr );",
                "return tr;" );
            _c.method( _suite );
        
           _c.mainMethod( "doTest();" );
            return _c;
        }
        catch( Exception e )
        {
            throw new AdHocException( "Unable to build test model " + _c, e );
        }
    }

    public boolean verify()
    {
        TestResult tr = run();
        if( tr.wasSuccessful() )
        {
            return true;
        }
        throw new AdHocTestFailures( buildTestModel(), tr );
    }
        
    
    public TestResult run()
        throws AdHocException
    {
        _class _c = buildTestModel();
        Class c = null;
        try
        {
            JavaSourceFolder jsf = 
            JavaSourceFolder.of( this.models ).add( _c );
            
            AdHocClassLoader loaded = AdHoc.compile( jsf );        
            c = loaded.findClass( _c );                    
        }
        catch( JavacException je )
        {
            throw new JavacException( 
                _c.author( PrefixWithLineNumber.INSTANCE ), je );
        }

        TestResult testResult = null;
        try
        {
            testResult = (TestResult)Java.call( c, "doTest" );
            return testResult;
        }
        catch( Exception e )
        {
            throw new AdHocException(
                "Unable to run doTest method on "+ System.lineSeparator() + _c);
        }
        //if( (Boolean)Java.call( testResult, "wasSuccessful") )
        //{
        //    return true;
        //}         
        //throw new AdHocTestFailures( _c, testResult );
    }        
    
    public static class AdHocTestFailures
        extends AdHocException
    {
        private TestResult testResults;
        private _class code;
        
        public AdHocTestFailures( _class testCode, TestResult testResults )
        {
            super( "Test failed :" + System.lineSeparator()  
                + testCode.getMethod( "testIt" ).getBody().author( PrefixWithLineNumber.INSTANCE )
                + System.lineSeparator()
                + describe( testResults ) 
                + System.lineSeparator() );
            this.testResults = testResults;
        }
        
        private static final String describe( TestResult tr )
        {
            
            System.out.println( tr.failures() );
            StringBuilder sb = new StringBuilder();
            
            Enumeration<TestFailure> tfs = tr.errors();
            while( tfs.hasMoreElements() )
            {
                TestFailure tf = tfs.nextElement();
                sb.append( tf.trace() );
                sb.append( System.lineSeparator() );
            }
            Enumeration<TestFailure> tes = tr.failures();
            while( tes.hasMoreElements() )
            {
                sb.append( tes.nextElement().trace() );
                sb.append( System.lineSeparator() );
            }
            return sb.toString();
        }
    }
}
