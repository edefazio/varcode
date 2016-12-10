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
package varcode.java.lang;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _constructorTest 
    extends TestCase
{

    public void testDeclare_ctorStrings()
    {
        _constructors._constructor _ctor = _constructors._constructor.of( 
            "/*comment*/", 
            "@Deprecated", 
            "public MyClass( int param )",
            "this.param = param;" ); 
        
        //System.out.println( "MODS " + _ctor.getModifiers() );
        assertTrue( _ctor.getModifiers().containsAll( "public"  ) );
        
        assertEquals("MyClass", _ctor.getName() );
        assertEquals("int", _ctor.getParameters().getAt( 0 ).getType() );
        assertEquals("param", _ctor.getParameters().getAt( 0 ).getName() );
        
        assertTrue(_ctor.getJavadoc().getComment().equals( "comment" ) );
        
        assertTrue(_ctor.getAnnotations().getAt( 0 ).toString().equals( "@Deprecated " ) );
        
        assertEquals( "this.param = param;", _ctor.getBody().author() );          
    }    
}
