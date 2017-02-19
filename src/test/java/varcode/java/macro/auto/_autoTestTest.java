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
package varcode.java.macro.auto;

import howto.java.adhoc.AdHocTest;
import java.util.UUID;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import varcode.java.Java;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class _autoTestTest    
{

    public static void main( String[] args )
    {
        _class _c = _class.of("package g;", "public class ID")
            .method( "public int getId()",
                "return 100;" );
        
        AdHocTest.of( _c,
            "ID id = new ID();",
            "assertEquals(100, id.getId());" ).verify();        
        
        //_at.verify();
        //TestResult tr = _at.run();
        
        
        //Java.callMain( c );
        
        //TestResult tr = (TestResult) Java.call( c, "doTest" );
        
        //System.out.println( tr );
        /*
        TestResult tr = new TestResult();
        TestSuite ts = new TestSuite( c );
        ts.run( tr );
        */
    }    
}
