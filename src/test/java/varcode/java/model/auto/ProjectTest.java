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

import junit.framework.TestCase;
import varcode.java.adhoc.Project;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class ProjectTest
    extends TestCase
{
    public void testProjectNoTests()
    {
        _class _testable = 
            _class.of( "package ex.proj", "public class Testable")
                .method("public int getCount()",
                 "return 1;" );
        
        Project p = Project.of( "MyProject" )
            .add( _testable ); 
        
        //p.buildExport( "C:\\temp\\" );
        
        Class[] classes = p.build();        
        assertEquals( 1, classes.length );        
        assertEquals( "Testable", classes[0].getSimpleName() );
        assertEquals( "ex.proj.Testable", classes[0].getCanonicalName() );
    }
    
    public void testProjectWithTests()
    {
        _class _testable = 
            _class.of( "package ex.proj", "public class Testable")
                .method("public int getCount()",
                 "return 1;" );
        
        _class _test = _class.of("package ex.proj", "public class TheTest extends TestCase")
            .imports("junit.framework.TestCase;")            
            .method( "public void testGetCount()",
                "Testable t = new Testable();",
                "assertEquals( 1, t.getCount() );" );
        
        Project p = Project.of( "MyProject" )
            .add( _testable )
            .test( _test );
        
        
        Class[] classes = p.build();        
        assertEquals( 1, classes.length );        
        assertEquals( "Testable", classes[0].getSimpleName() );
        assertEquals( "ex.proj.Testable", classes[0].getCanonicalName() );
    }
}
