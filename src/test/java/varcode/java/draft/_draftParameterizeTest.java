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
package varcode.java.draft;

import junit.framework.TestCase;
import varcode.java.draft.$;
import varcode.java.draft.draftAction.ExpandField;
import varcode.java.model._class;
import varcode.java.model._fields._field;
/**
 *
 * @author Eric
 */
public class _draftParameterizeTest
    extends TestCase
{
     
    public void testParameterizeField()
    {
        
        _field _f = _field.of( "public int count = 100;" );
        ExpandField tf = 
            ExpandField.parameterize( _f, "100", "count" );
        _class _c = _class.of("public class A");
                
        
        //this should work fine
        //tf.draftTo( _c, Context.EMPTY );
        
        tf.draftTo( _c, "count", 1 );
        assertEquals( "1", 
            _c.getField("count").getInit().getCode().toString() );
        
        
        //System.out.println( _c );        
        //tf.draftTo( _c, Context.EMPTY );
    }
    
    public void testParameterizeFields()
    {
        _field _f = _field.of( "public int count;" );
        ExpandField tf = 
            ExpandField.parameterize( _f, "int", "type", "count", "name" );
        
        _class _c = _class.of("public class A");
        tf.draftTo( _c, 
            "type", long.class, 
            "name", "a" );
        
        assertEquals( "long", _c.getField("a").getType() );
        
        _c = _class.of("public class A");
        tf.draftTo( _c, "name", new String[]{"a", "b"}, "type", new Class[]{long.class, int.class} );
        assertEquals( "long", _c.getField("a").getType() );
        assertEquals( "int", _c.getField("b").getType() );
        
        _c = _class.of("public class A");
        
        //multi _field
        // this will add (3) int fields
        // @$({"count", "name"})
        _f = _field.of( "public int count;" );
        tf = ExpandField.parameterize( _f, "count", "name" );
        tf.draftTo( _c, "name", new String[]{"x", "y", "z"} );        
        assertEquals( "int", _c.getField( "x" ).getType() );
        assertEquals( "int", _c.getField( "y" ).getType() );
        assertEquals( "int", _c.getField( "z" ).getType() );        
    }
    
    /*
    public void testParamOptional()
    {
         _field _f = _field.of( "public int count = 100;" );
         ExpandField tf = ExpandField.parameterizeOptional( _f, " = 100", "value" );
         _class _c = _class.of("A");
         tf.draftTo( _c, Context.EMPTY );
         
         System.out.println( _c );
    }
    */
    
    
    
    @$({"Inner", "postfix"})
    public class ParameterizedInner
    {
        @$({"value", "name"})
        public final String key = "value";
        
        public String getName()
        {
            return "MyInner";
        }
    }
    
    public void testTailorField()
    {
        _field _f = _field.of( 
            "@$({\"value\", \"name\"})", 
            "public final String key = value;" );        
    }
}
