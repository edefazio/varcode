/*
 * Copyright 2016 Eric DeFazio.
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
package quickstart.java;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.lang._class;

/**
 * Building Java _class "models" from scratch is the easiest 
 * way to get started with varcode. After building a _class 
 * model, you can load a Class representing the model, or you
 * can create a new instance of a class based on the _class. 
 *  
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _1_CodeFromScratch
    extends TestCase
{
    public void testDynamicClass()
    {
        Class dynamicClass = _class.of( "public class A" )
            .method( "public static int getCount()", "return 100;" )
            .loadClass();
        
        assertEquals( Java.invoke( dynamicClass, "getCount"), 100 );
    }
    
    public void testDynamicInstance()
    {
       Object dynamicInstance = _class.of( "public class A" )
          .method( "public String toString()", "return \"Hello World!\";" )
          .instance();
       
       assertEquals( "Hello World!", dynamicInstance.toString() );
    }
}
