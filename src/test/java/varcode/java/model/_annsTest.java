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
package varcode.java.model;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _annsTest
    extends TestCase
{
 
    public void testEquals()
    {
        _anns _anns = new _anns();
        _anns.add( _ann.of("@Deprecated"), _ann.of("@Override") );
        
        _anns _anns2 = new _anns();
        _anns2.add( _ann.of("@Deprecated"), _ann.of("@Override") );
        
        assertEquals( _anns, _anns2 );
        
        
    }
    public void testEmpty()
    {
        _anns _a = new _anns();
        assertEquals( "", _a.toString() );
        
        _a = new _anns( true );
        assertEquals( "", _a.toString() );        
    }
    
    public void testName()
    {
        _anns a = new _anns( );
        a.add( _ann.of( "@Deprecated" ) );
        assertEquals( "@Deprecated" + System.lineSeparator() , a.toString() );
        
        a = new _anns( true );
        a.add( _ann.of( "@Deprecated" ) );
        
    }    
    
    public void testCommonAnns()
    {
        assertEquals( "@java.lang.Deprecated", _ann.DEPRECATED.author() );
        assertEquals( "@java.lang.Override", _ann.OVERRIDE.author() );
        assertEquals( "@java.lang.FunctionalInterface", _ann.FUNCTIONAL_INTERFACE.author() );
        assertEquals( "@java.lang.SafeVarargs", _ann.SAFE_VARARGS.author() );        
    }
    /*
    public void testAnnotationBindTo()
    {
        _annotation ann = _annotation.of("@Deprecated");
        ann.bind( VarContext.of("a", 100 ) );
        System.out.println( ":" +  ann.toString()+ ":" );
        assertEquals( "@Deprecated ", ann.toString() );
        
        ann = _annotation.of("@{+ann+}");
        ann.bind( VarContext.of("ann", "Ayy" ) );
        assertEquals( "@Ayy ", ann.toString() );        
    }
    */
    
    public void testAttributesEmpty()
    {
        _anns at = new _anns();
        assertEquals( 0, at.count() );
        assertTrue( at.isEmpty() );       
        
        assertEquals( "", at.toString() );
    }
    

    
    
   
    
    public void testOne()
    {
        _ann al = new _ann( "@Path" );
        al.addAttr("\"book\"" );
        assertEquals("@Path(\"book\")", al.author( ) );        
        
        
        assertEquals( 1, al.attributes.count() );
        assertFalse( al.attributes.isEmpty() );
    }
    
    public void testGET()
    {
        _ann _a = _ann.of( "@GET" );
        assertEquals( "@GET", _a.author() );
        
        _anns _as = _anns.of( _ann.of( "@GET" ), _ann.of( "@GET" ) );
        
        _a = _as.getAt( 0 );
        assertEquals( "@GET", _a.author() );
        assertEquals( "@GET", _a.toString() );
        
        _a = _as.getAt( 1 );
        assertEquals( "@GET", _a.author() );
        assertEquals( "@GET", _a.toString() );
        
        System.out.println( _as );
        
    }
    public void testMany()
    {
        _anns al = new _anns( false );
        al.add( _ann.of("@Path(\"/{id}\")"), _ann.of("@GET") );
        
        System.out.println("*"+ al + "*");
        
        assertEquals(
            "@Path(\"/{id}\")" + System.lineSeparator() +
            "@GET", al.author( ).trim() );     
        
        assertEquals( 2, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 2, al.getAnnotations().size() );
    }
    
    public void testAnnotationsAuthor()
    {
        _anns _anns = new _anns();
        assertEquals( "", _anns.author() );
        
        _anns.add( _ann.of( "@Deprecated" ) );
        
        assertEquals( "@Deprecated" + System.lineSeparator(), _anns.toString() );
        
        
        _anns.add( _ann.of( "@Homogonized") );        
        assertEquals( "@Deprecated" + System.lineSeparator() + 
            "@Homogonized" + System.lineSeparator(), 
            _anns.toString() );
        
        //_anns.add( _annotation.generated( "Eric" ) );        
        //System.out.println( _anns );
        
    }
    
    public void testAnnotationsInline()
    {
        _anns _anns = new _anns( true );
        assertEquals( "", _anns.author() );
        
        _anns.add( _ann.of("@Deprecated") );
        
        assertEquals( "@Deprecated ", _anns.toString() );
        
        _anns.add( _ann.of( "@Homogonized") );        
        
        assertEquals( "@Deprecated @Homogonized ", 
            _anns.toString() );
    }
}

