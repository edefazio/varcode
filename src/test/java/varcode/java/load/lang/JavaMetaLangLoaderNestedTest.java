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
package varcode.java.load.lang;

import varcode.java.load.JavaMetaLangLoader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang._enum;
import varcode.java.metalang._interface;
import varcode.java.metalang._nests;

/**
 * uses the JavaMetaLangLoader to load Nested / Member Classes
 * Enums / Interfaces
 * @author eric
 */
public class JavaMetaLangLoaderNestedTest
    extends TestCase
{
    public static class MemberClass
    {
        
        public static class NestedClass
        {
            public static class DeepNest
            {
                
            }
        }
        
        public interface NestedInterface
        {
            
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testClassNesteds()
    {
        //read and parse a Member class that contains : 
        // NestedClass
        // NestedInterface
        // NestedEnum
        _class _c = JavaMetaLangLoader._Class.from( MemberClass.class );
        //_class _c = JavaLoad._classOf( MemberClass.class );
        _nests ng = _c.getNesteds();
        assertEquals( 3, ng.count() );
        _class _nc = (_class)_c.getNestedByName( "NestedClass");
        assertNotNull( _nc );
        assertNotNull( _c.getNestedByName("NestedInterface") );
        assertNotNull( _c.getNestedByName("NestedEnum") );                        
        assertNotNull( _nc.getNestedByName( "DeepNest" ) );
    }
    
    public interface MemberInterface
    {
        public static class NestedClass
        {
            
        }
        
        public interface NestedInterface
        {
            public static class DeepNest
            {
                
            }
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testInterfaceNesteds()
    {
        _interface _i = 
            JavaMetaLangLoader._Interface.from(  MemberInterface.class );
            //_Java._interfaceFrom( MemberInterface.class );
            //_Load._interfaceOf( MemberInterface.class );
        _class _c = (_class)_i.getNesteds().getByName( "NestedClass" );
        _interface _ni = (_interface)_i.getNesteds().getByName( "NestedInterface" );
        _enum _e = (_enum)_i.getNestedByName( "NestedEnum" );
        
        List<String> names = new ArrayList<String>();
        _i.getAllNestedClassNames( names, _i.getFullyQualifiedClassName() );
        
        //System.out.println( MemberInterface.NestedClass.class.getName().replace( "." +this.getClass().getSimpleName(), "") );
        System.out.println( names );
        assertTrue( names.contains( "varcode.java.load.lang.MemberInterface$NestedClass" ) );
        assertTrue( names.contains( "varcode.java.load.lang.MemberInterface$NestedInterface" ) );
        assertTrue( names.contains( "varcode.java.load.lang.MemberInterface$NestedInterface$DeepNest" ) );
        assertTrue( names.contains( "varcode.java.load.lang.MemberInterface$NestedEnum" ) );
        
        System.out.println( MemberInterface.class );
        System.out.println( MemberInterface.class.getName() );
        System.out.println( MemberInterface.class.getCanonicalName() );
               
    }
    
    enum MemberEnum
    {
        ;
            
        public static class NestedClass
        {
            
        }
        
        public interface NestedInterface
        {
            public static class DeepNest
            {
                
            }
        }
        
        public enum NestedEnum
        {
            ;
        }
    }
    
    public void testEnumNesteds()
    {
        //_enum _e = JavaLoad._enumOf( MemberEnum.class );
        _enum _e = JavaMetaLangLoader._Enum.from( MemberEnum.class );
        
    }
    
}
