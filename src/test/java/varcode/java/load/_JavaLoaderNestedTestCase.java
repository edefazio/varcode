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
package varcode.java.load;

import varcode.java.load._JavaLoader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.lang._enum;
import varcode.java.lang._interface;
import varcode.java.lang._nesteds;

/**
 *
 * @author eric
 */
public class _JavaLoaderNestedTestCase
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
        _class _c = _JavaLoader._Class.from( MemberClass.class );
        _nesteds ng = _c.getNesteds();
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
            _JavaLoader._Interface.from( MemberInterface.class );
        _class _c = (_class)_i.getNesteds().getByName( "NestedClass" );
        _interface _ni = (_interface)_i.getNesteds().getByName( "NestedInterface" );
        _enum _e = (_enum)_i.getNestedByName( "NestedEnum" );
        
        List<String> names = new ArrayList<String>();
        _i.getAllNestedClassNames( names, _i.getFullyQualifiedClassName() );
        
        //System.out.println( MemberInterface.NestedClass.class.getName().replace( "." +this.getClass().getSimpleName(), "") );
        System.out.println( names );
        assertTrue( names.contains( "varcode.java.model.load.MemberInterface$NestedClass" ) );
        assertTrue( names.contains( "varcode.java.model.load.MemberInterface$NestedInterface" ) );
        assertTrue( names.contains( "varcode.java.model.load.MemberInterface$NestedInterface$DeepNest" ) );
        assertTrue( names.contains( "varcode.java.model.load.MemberInterface$NestedEnum" ) );
        
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
        _enum _e = _JavaLoader._Enum.from( MemberEnum.class );
        
    }
    
}
