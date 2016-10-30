/*
 * Copyright 2016 eric.
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
package varcode.java.model;

import varcode.java.model.cs._thread;
import junit.framework.TestCase;
import varcode.java.model.cs._thread._runnable;

/**
 *
 * @author eric
 */
public class _threadTest
    extends TestCase
{
    static final String N = "\r\n";
    
    public void testSimple()
    {
        _runnable r = _thread.run( "System.out.println(\"In thread\");" );
        
        
        //System.out.println ( r );
        assertEquals(
            "new Thread(" + N +
            "    new Runnable()" + N +
            "    {" + N +
            "        public void run()" + N +
            "        {" + N +        
            "            System.out.println(\"In thread\");" + N +
            "        }" + N +
            "    }" + N +
            ")",   
            r.toString().trim() );       
        
        //author the code to start the thread
        r.start();
        
        assertEquals(
            "new Thread(" + N +
            "    new Runnable()" + N +
            "    {" + N +
            "        public void run()" + N +
            "        {" + N +        
            "            System.out.println(\"In thread\");" + N +
            "        }" + N +
            "    }" + N +
            ").start();",   
            r.toString().trim() );       
    }
    
}
