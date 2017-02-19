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
package varcode.java.macro;

import java.util.List;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.macro.Macro.ExpandField;
import varcode.java.macro.Macro._typeExpansion;
import varcode.java.macro.Macro.$;

import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;

/**
 *
 * @author Eric
 */
public class MacroFieldsTest
    extends TestCase
{
    public void testCopyMacroField()
    {
        _typeExpansion te = 
            _classMacro.processField( _field.of( "public int a;" ) );
        _class _c = _class.of("A");
        te.expandTo( _c );
        
        assertEquals( "int", _c.getField("a"). getType() );        
    }
    
    @$({"a", "name"})
    public void testTailorField()
    {
        _field _f = _field.of( "@$({\"a\",\"name\"})", "public int a;" );
        Macro.ExpandField te = 
            (ExpandField)_classMacro.processField( _f );
        _class _c = _class.of("A");
        te.expandTo( _c, "name", "myFieldName" );
        
        System.out.println( _c );
        assertEquals( "int", _c.getField("myFieldName").getType() );             
    }
    
    @$({"int", "type", "a", "name"})
    public void testTailorFields()
    {
        _field _f = _field.of( "@$({\"int\", \"type\", \"a\",\"name\"})", "public int a;" );
        Macro.ExpandField te = 
            (ExpandField)_classMacro.processField( _f );
        _class _c = _class.of("A");
        te.expandTo( _c, "type", "String", "name", "label" );
        
        System.out.println( _c );
        assertEquals( "String", _c.getField("label").getType() );             
    
        //reset the class
        _c = _class.of("A");
        te.expandTo( _c, "type", new Object[]{ int.class, "String"}, 
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
    
    public void testReadAndProcess()
    {
        _class _c = Java._classFrom( inner.class );
        
        List<_typeExpansion>te = _classMacro.processFields( _c.getFields() );
        
        _class _target = _class.of("Target");
        for(int i=0; i< te.size(); i++ )
        {
            te.get( i ).expandTo( _target, "type", String.class, "name", "theName" );            
        }
        assertEquals( "java.lang.String", _target.getField( "theName" ).getType() );
    }
}
