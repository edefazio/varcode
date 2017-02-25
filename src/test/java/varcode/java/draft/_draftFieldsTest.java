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

import varcode.java.draft._draft;
import varcode.java.draft._draftFields;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.draft._draft.DraftField;
import varcode.java.draft._draft.$;

import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.draft._draft._typeDraft;

/**
 *
 * @author Eric
 */
public class _draftFieldsTest
    extends TestCase
{
    public void testCopyMacroField()
    {
        _typeDraft te = 
            _draftFields.prepareField( _field.of( "public int a;" ) );
        _class _c = _class.of("A");
        te.draftTo( _c );
        
        assertEquals( "int", _c.getField("a"). getType() );        
    }
    
    @$({"a", "name"})
    public void testTailorField()
    {
        _field _f = _field.of( "@$({\"a\",\"name\"})", "public int a;" );
        _draft.DraftField te = 
            (DraftField)_draftFields.prepareField( _f );
        _class _c = _class.of("A");
        te.draftTo( _c, "name", "myFieldName" );
        
        System.out.println( _c );
        assertEquals( "int", _c.getField("myFieldName").getType() );             
    }
    
    @$({"int", "type", "a", "name"})
    public void testTailorFields()
    {
        _field _f = _field.of( "@$({\"int\", \"type\", \"a\",\"name\"})", "public int a;" );
        _draft.DraftField te = 
            (DraftField)_draftFields.prepareField( _f );
        _class _c = _class.of("A");
        te.draftTo( _c, "type", "String", "name", "label" );
        
        System.out.println( _c );
        assertEquals( "String", _c.getField("label").getType() );             
    
        //reset the class
        _c = _class.of("A");
        te.draftTo( _c, "type", new Object[]{ int.class, "String"}, 
                         "name", new String[]{ "x", "label"} );
        
        System.out.println( _c );
        assertEquals( "String", _c.getField("label").getType() );             
        assertEquals( "int", _c.getField("x").getType() );                     
    }    

    public static class inner
    {
        @$({"int", "type", "x", "name"})
        public int x;
    }
    
    public void testReadAndProcessFields()
    {
        _class _c = Java._classFrom( inner.class );
        
        List<_typeDraft>te = new ArrayList<_typeDraft>();
        //List<_typeExpansion>te = 
        _draftFields.prepareFields( te, _c.getFields() );
        
        _class _target = _class.of("Target");
        for( int i = 0; i < te.size(); i++ )
        {
            te.get( i ).draftTo( _target, "type", String.class, "name", "theName" );            
        }
        assertEquals( "java.lang.String", _target.getField( "theName" ).getType() );
        
        _class _t2 = _class.of("Target");
        
        for( int i = 0; i < te.size(); i++ )
        {
            te.get( i ).draftTo( _t2, 
                "type", new Class[]{int.class, String.class}, 
                "name", new String[]{"y", "theName"} );            
        }
        assertEquals( "java.lang.String", _t2.getField( "theName" ).getType() );
        assertEquals( "int", _t2.getField( "y" ).getType() );
        
        System.out.println( _t2 );
        //_class _c = Java._classFrom( inner.class )        
    }
}
