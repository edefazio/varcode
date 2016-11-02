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
package tutorial.varcode.chap3.load;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.UUID;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.VarException;
import varcode.java.model._class;
import varcode.java.model._constructors;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._parameters._parameter;
import varcode.java.model.load._JavaLoader;

/**
 * Loads the models (_class, _enum, _interface) 
 * from existing source code
 * 
 * @author M. Eric
 */
public class _2_LoadModel
    extends TestCase
{
    private static final Logger LOG = 
        LoggerFactory.getLogger(_2_LoadModel.class );
    
    //load a "top level class" (stored in "VarException.java")
    public void testLoadClass()
    {
        _class c = _JavaLoader._Class.from( VarException.class );        
        LOG.debug ( c.author( ) ); 
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
    public void testLoadMemberJavaClassAsModel()
    {        
        _class c = _JavaLoader._Class.from( MemberClass.class );
        
        assertEquals( "MemberClass", c.getName() );
        c.getModifiers().containsAll( "private", "static" );
        assertTrue( 
            c.getMethodNamed( "someMethod" )
                .getModifiers().containsAll( "public", "static", "final" ) );
    }
    
    public void testLoadInterface()
    {
        
    }
    
}
