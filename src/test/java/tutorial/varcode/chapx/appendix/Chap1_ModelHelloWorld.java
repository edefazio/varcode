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
package tutorial.varcode.chapx.appendix;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.java.lang._class;

/**
 *
 * @author eric
 */
public class Chap1_ModelHelloWorld
    extends TestCase
{ 
    public void testHello( )
    {
        //model, author, compile, load, and instatiate a new AdHocClass
        Object helloInstance = _class.of( "tutorial.varcode.chapx.appendix", 
            "public class HelloWorld" )
            .method( "public static void main( String[] args )",
                "System.out.println(\"Hello World\");" )
            .instance();
        
        _Java.invoke( helloInstance, "main", (Object)new String[ 0 ] );
    }
}
