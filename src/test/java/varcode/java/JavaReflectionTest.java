/*
 * Copyright 2016 Eric.
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
package varcode.java;

import java.lang.reflect.Method;
import junit.framework.TestCase;

/**
 *
 * @author Eric
 */
public class JavaReflectionTest 
    extends TestCase
{
    public void testGetMainMethod()
    {
        Method m = JavaReflection.getMainMethod( JavaReflectionTest.class );
        System.out.println( m );
    }
    public void testMainMethod()
    {
        JavaReflection.invokeMain( JavaReflectionTest.class );
    }
    
    public static void main( String[] args )
    {
        System.out.println( "called main" );
    }
    
    public void testMainMethodWithArgs()
    {
        JavaReflection.invokeMain( MainWithStringArgs.class, "A", "B", "C" );
    }
    
    public static class MainWithStringArgs
    {
        public static void main(String[] args )
        {
            for(int i=0; i< args.length; i++ )
            {
                System.out.println( args[ i ] );
            }
        }
    }
    
     
}
