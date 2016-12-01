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
package howto.java_metalang;

import junit.framework.TestCase;
import varcode.java._Java;
import varcode.load.SourceLoader.SourceStream;

public class LoadJavaSource 
    extends TestCase
{
    /**
     * This will load source code from the "normal" locations that
     * Java source code appears.
     * 
     * under the covers it will load using the strategy defined in:
     * {@code varcode.java.load.JavaSourceLoader}
     */
    public void testLoadSourceFromDefaultLoader()
    {
        SourceStream ss = _Java.sourceFrom( LoadJavaSource.class );
        String theSourceAsAString = ss.asString();
        assertTrue( theSourceAsAString.contains( "theSourceAsAString" ) );
    }
}
