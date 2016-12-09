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
import varcode.context.Bootstrap;
import varcode.java._Java;
import varcode.java.lang._enum;
import varcode.java.load.JavaMetaLangLoader;

/**
 *
 * @author eric
 */
public class _4_Load_enum
    extends TestCase
{
    public void testLoadEnum()
    {
        _enum _e = _Java._enumFrom( Bootstrap.class );
        System.out.println ( _e );
    }
    
    enum MemberEnum
    {
        A(0), B(1), C(2);
        
        private final int number;
        
        private MemberEnum( int num )
        {
            this.number = num;
        }
        
        public final int getNum()
        {
            return this.number;
        }
        
    }
    
    public void testLoadMemberEnum()
    {
        //_enum e = JavaLoad._enumOf( MemberEnum.class );
        _enum e = _Java._enumFrom( MemberEnum.class );
        System.out.println( e );
    }
}
