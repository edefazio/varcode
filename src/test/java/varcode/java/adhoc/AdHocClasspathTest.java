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
package varcode.java.adhoc;

import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class AdHocClasspathTest
    extends TestCase
{
    public void testCurrent()
    {
        AdHocClassPath curr = AdHocClassPath.current();
        System.out.println( curr );
        
        //lets assume they have junit in the current classpath, since it's running
        // a unit test
        
        List<String> pathWithJunit = curr.findAllWith( "junit" );
        assertTrue( pathWithJunit.size() > 0 );        
        
        //lets remove Junit from the classpath
        curr.removeAll( pathWithJunit );
        
        //lets verify there are no more left
        pathWithJunit = curr.findAllWith( "junit" );
        assertTrue( pathWithJunit.size() == 0 );        
        
        //curr.removeAllWith( "junit" );        
    }
}
 