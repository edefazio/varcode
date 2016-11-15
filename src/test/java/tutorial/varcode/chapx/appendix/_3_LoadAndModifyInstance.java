/*
 * Copyright 2016 Eric DeFazio.
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
import varcode.java.model._class;
import varcode.java.model.load._Load;

/**
 * varcode can create ad hoc Object instances
 *
 * @author Eric DeFazio
 */
public class _3_LoadAndModifyInstance
    extends TestCase
{
    public class SkeletonBean
    {
        public int id;
        
        public int getId()
        {
            return id;
        }
        
        public void setId( int id )
        {
            this.id = id;
        }
    }
    
    public void testAuthorDynamicInstance()
    {
        _class _c = _Load._classOf( SkeletonBean.class );
        _c.setName( "AuthoredBean" );
        _c.property( "private String name;" );//add field, getter setter
        _c.property( "private final int count;" );
        
        _c.constructor( "public AuthoredBean( int count )", 
            "this.count = count;" );
        
        System.out.println( _c );
        
        Object adHocInstance = _c.instance( 100 );
        
    }    
}
