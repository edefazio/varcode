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
package tutorial.chap4.load;

import java.io.Serializable;
import junit.framework.TestCase;
import varcode.Model;
import varcode.java.lang._interface;
import varcode.java.load._JavaLoader;

/**
 *
 * @author eric
 */
public class _3_LoadModel_interface
    extends TestCase
{
     public void testLoad_interface()
    {
        _interface i = _JavaLoader._Interface.from( Model.class );
        //_interface i = JavaLoad._interfaceOf( Model.class );        
    }
    
    public interface MemberInterface
        extends Serializable
    {
        
        void doIt(); 
        
    }
    
    public void testLoadMember_interface()
    {
        _interface i = _JavaLoader._Interface.from( MemberInterface.class );
        //_interface i = JavaLoad._interfaceOf( MemberInterface.class );        
    }
}
