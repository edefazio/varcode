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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.java.lang._class;
import varcode.java.load.JavaMetaLangLoader;

/**
 * Loads the models (_class, _enum, _interface) 
 * from existing source code
 * 
 * @author M. Eric
 */
public class _3_Load_class
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger(_3_Load_class.class );
    
    //load a "top level class" (stored in "VarException.java")
    public void testLoad_class()
    {
        _class _c = JavaMetaLangLoader._Class.from( VarException.class );
        //_class c = JavaLoad._classOf( VarException.class );
        LOG.debug( _c.author( ) ); 
    }

    private static class MemberClass
    {
        public int count = 1;
        
        public static final String NAME = "THENAME";
        
        /** method comment */
        public static final String someMethod( int a, String b )
        {
            return b + a;
        }
    }
    
    /** 
     * load a "member" Class: 
     * tutorial.varcode.chap3.load._2_LoadModelByClass.$MemberClass 
     * stored in "\tutorial\varcode\chap3\load\_2_LoadModelByClass.java"
     */
    public void testLoadMember_class()
    {        
        _class _c = JavaMetaLangLoader._Class.from( MemberClass.class );
        //_class c = JavaLoad._classOf( MemberClass.class );
        assertEquals("MemberClass", _c.getName() );
        _c.getModifiers().containsAll( "private", "static" );
        assertTrue(_c.getMethodNamed( "someMethod" )
                .getModifiers().containsAll( "public", "static", "final" ) );
    }    
}
