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
import varcode.context.Context;
import varcode.java.model._class;
import varcode.java.model._constructors._constructor;

/**
 *
 * @author Eric
 */
public class _draftConstructorsTest
    extends TestCase 
{
    public void testCtorCopy()
    {
        //we copy constructors 
        assertTrue( 
            _draftConstructors.processConstructor( 
                _constructor.of( "public MyClass()" ) )
            instanceof DraftAction.CopyConstructor );
    }

    public void testCtorRemove()
    {
        assertNull( 
            _draftConstructors.processConstructor( 
                _constructor.of( "@remove", "public MyClass()" ) )
            );
    }
    
    public void testCtorStaticSig()
    {
        DraftAction td = _draftConstructors.processConstructor( 
            _constructor.of( "@sig(\"public TheClass()\")", "public MyClass()" ) );
        _class _c = _class.of("TheClass");
        td.draftTo( _c, Context.EMPTY );
        
        assertNotNull( _c.getConstructor( 0 ) );
        assertEquals( "TheClass", _c.getConstructor( 0 ).getName() );
    }
    
    public void testCtorStaticBody()
    {
        DraftAction td = _draftConstructors.processConstructor( 
            _constructor.of(
                "@body(\"System.out.println(getClass().getName());\" )", 
                "public MyClass()" ) );
        
        _class _c = _class.of("MyClass");
        td.draftTo( _c, Context.EMPTY );
        assertNotNull( _c.getConstructor( 0 ) );
        assertEquals( "MyClass", _c.getConstructor( 0 ).getName() );
        assertTrue( _c.getConstructor( 0 ).getBody().author().contains( 
            "System.out.println" ) );        
    }
    
    public void testCtorStaticSigBody()
    {
        DraftAction td = _draftConstructors.processConstructor( 
            _constructor.of( "@sig(\"public TheClass()\")", 
                "@body(\"System.out.println(getClass().getName());\" )", 
                "public MyClass()" ) );
        
        _class _c = _class.of("TheClass");
        td.draftTo( _c, Context.EMPTY );
        assertNotNull( _c.getConstructor( 0 ) );
        assertEquals( "TheClass", _c.getConstructor( 0 ).getName() );
        assertTrue( _c.getConstructor( 0 ).getBody().author().contains( 
            "System.out.println" ) );        
    }
    
    public void testCtorDraftSigBody()
    {
        DraftAction td = _draftConstructors.processConstructor( 
            _constructor.of( "@sig(\"public {+ClassName+}()\")", 
                "@body(\"System.out.println( {+$quote(saying)+} );\" )", 
                "public MyClass()" ) );
        
        _class _c = _class.of("TheClass");
        td.draftTo( _c, 
            "className", "theClass", 
            "saying", "Ask not what you can do" );
        assertNotNull( _c.getConstructor( 0 ) );
        assertEquals( "TheClass", _c.getConstructor( 0 ).getName() );
        assertTrue( _c.getConstructor( 0 ).getBody().author().contains( 
            "Ask not what" ) );        
        
    }
}
