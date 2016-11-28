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
package use.java.langmodel;

import junit.framework.TestCase;
import varcode.java.langmodel._methods._method;

/**
 *
 * @author eric
 */
public class Methods
    extends TestCase
{
    _method _abstractMethod = _method.of("public abstract int absMethod( String input )");
    
    _method _varArgsMethod = _method.of( 
        "public int varArgsMethod( String... inputs )",
        "return inputs.length;" );
    
    public void testMethods()
    {
        
    }
}
