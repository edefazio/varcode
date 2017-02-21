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
package varcode.author.lib;

import java.lang.reflect.Array;
import junit.framework.TestCase;
import varcode.translate.TranslateBuffer;

/**
 *
 * @author Eric
 */
public class QuoteTest
    extends TestCase
{
    public void testQuote()
    {
        Object res = Quote.INSTANCE.apply( null );
        assertEquals( null, res );
        
        res = Quote.INSTANCE.apply( "a" );
        assertEquals( "\"a\"", res );
    }
    
    public void testQuoteArray()
    {
        Object res = Quote.INSTANCE.apply( new String[ 0 ] );
        assertTrue( res.getClass().isArray() );
        assertEquals( 0, Array.getLength( res ) );
        
        res = Quote.INSTANCE.apply( new String[ ] {"A"} );
        
        TranslateBuffer tb = new TranslateBuffer();
        assertEquals("\"A\"", tb.translate( res ) );
        
        res = Quote.INSTANCE.apply( new String[ ] {"A", "B", "C"} );
        
        assertEquals("\"A\", \"B\", \"C\"", tb.translate( res ) );        
    }
}
