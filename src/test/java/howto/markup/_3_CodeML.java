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
package howto.markup;

import junit.framework.TestCase;
import varcode.doc.Compose;
import varcode.doc.Dom;
import varcode.markup.codeml.CodeML;

/**
 *
 * @author Eric
 */
public class _3_CodeML 
    extends TestCase
{
    public void testCodeML()
    {
        
        //If (some expression) evaluates to true, then print result of the script
        Dom d = CodeML.compile(
            "/*{+?(( a )):$>(a)+}*/" //if( a is non null) print result of calling ">" indent with a                         
            + "/*{+?(( a == 1 )):$>(a)+}*/" //if a == 1, result of calling ">" (indent) script with a
        );
        
        System.out.println( Compose.asString( d, "a", 1 ) );  
    }
}
