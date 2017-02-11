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

import junit.framework.TestCase;
import varcode.java.adhoc.Javac.JavacOptions;

/**
 *
 * @author Eric
 */
public class ClassPathTest
    extends TestCase
{
    public void testClassPathDescribe()
    {
        JavacOptions.ClassPath cp = JavacOptions.ClassPath.of( System.getProperty( "java.class.path" ) );
        
        System.out.println( cp.toString() );
    }
}
