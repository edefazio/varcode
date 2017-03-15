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
package varcode.java.draft;

import junit.framework.TestCase;
import varcode.java.Java;

/**
 *
 * @author Eric
 */
public class _draftUnEncodeStringTest
    extends TestCase
{
    //when we use annotations, we have to escape Strings
    
    public static class MyClass
    {
        //when we read the String from body,we want it to read
        // "System.out.println("Hi");
        // and not
        // "System.out.println(\"Hi\");
        @body("System.out.println(\"Hi\");")
        public static void main(String[] args)
        {
            
        }
        
        @body({"System.out.println( \"Escaped String\");", "return 1;"})
        public static int getId()
        {
            return -1;
        }
    }
    
    public void testUnEncodeString()
    {
        //build and load a MyClass, call themain method, which should print "Hi"
        Class c = Draft._classOf( MyClass.class )
            .setModifiers( "public" ).loadClass();
        Java.callMain( c );
        
    }
}
