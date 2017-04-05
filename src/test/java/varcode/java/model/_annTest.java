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
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _annTest
    extends TestCase
{
    public void testSimpleAnn()
    {
        _ann _a= _ann.of("@a");
        assertEquals(0, _a.attributes.count());
        assertEquals(0, _a.attributes.keys.size());
        assertEquals(0, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("a",_a.name);
        assertEquals(null, _a.getAttrString() );
        assertEquals(null, _a.getAttrStringArray( "key" ) );        
    }
    
    //an annotation with a single attribute (implicit key)
    public void testAnnAttr()
    {
        _ann _a = _ann.of("@a(1)");
        assertEquals(1, _a.attributes.count());
        assertEquals(0, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("a",_a.name);
        assertEquals("1", _a.getAttrString() );   
        
        _a = _ann.of("@a(\"1\")");
        assertEquals(1, _a.attributes.count());
        assertEquals(0, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("a",_a.name);
        assertEquals("1", _a.getAttrString() );     
        assertEquals("1", _a.getAttrStringArray()[0] );     
    }
    
    public void testAnnAttrStringArray()
    {
        _ann _a = _ann.of("@a({1,2})");
        assertEquals(1, _a.attributes.count());
        assertEquals(0, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("a",_a.name);
        assertEquals("{ 1, 2 }", _a.getAttrString() );
        assertEquals("1", _a.getAttrStringArray()[0] );     
        assertEquals("2", _a.getAttrStringArray()[1] );  
        
        
        _a = _ann.of("@a({\"1\",\"2\"})");
        assertEquals(1, _a.attributes.count());
        assertEquals(0, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("a",_a.name);
        assertEquals("{ \"1\", \"2\" }", _a.getAttrString() );
        assertEquals("1", _a.getAttrStringArray()[0] );     
        assertEquals("2", _a.getAttrStringArray()[1] );          
    }

    public void testAnnKeyValue()
    {
        _ann _a = _ann.of("@a(k=v)");
        assertEquals(1, _a.attributes.count());
        assertEquals(1, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("v", _a.attributes.getValueOf( "k" ) );
        assertEquals("a",_a.name);
        assertEquals("v", _a.getAttrString("k") );
        assertEquals("v", _a.getAttrStringArray("k")[0] );                     
    }

    public void testAnnKeyValueStr()
    {
        _ann _a = _ann.of("@a(k=\"v\")");
        assertEquals(1, _a.attributes.count());
        assertEquals(1, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("\"v\"", _a.attributes.getValueOf( "k" ) );
        assertEquals("a",_a.name);
        assertEquals("v", _a.getAttrString("k") );
        assertEquals("v", _a.getAttrStringArray("k")[0] );                     
    }    

    public void testAnnKeyValueStrArray()
    {
        _ann _a = _ann.of("@a(k={\"v1\", \"v2\"})");
        assertEquals(1, _a.attributes.count());
        assertEquals(1, _a.attributes.keys.size());
        assertEquals(1, _a.attributes.values.size());
        assertEquals(null, _a.attributes.getValueOf( "key" ) );
        assertEquals("{ \"v1\", \"v2\" }", _a.attributes.getValueOf( "k" ) );
        assertEquals("a",_a.name);
        // fail? assertEquals("v", _a.getAttrString("k") );
        
        assertEquals("v1", _a.getAttrStringArray("k")[0] );
        assertEquals("v2", _a.getAttrStringArray("k")[1] );
    }
    
    @interface a
    {
        int k1();
        int k2();
    }
    
    @a(k1=1,k2=2)
    public void testMultiAttributes()
    {
        _ann _a = _ann.of("@a(k1=1,k2=2)");
        assertEquals("1", _a.getAttrString( "k1" ) );
        assertEquals("1", _a.getAttrStringArray( "k1" )[0] );
        assertEquals("2", _a.getAttrStringArray( "k2" )[0] );
        
        _a = _ann.of("@a(k1=\"1\",k2=\"2\")");
        assertEquals("1", _a.getAttrString( "k1" ) );
        assertEquals("2", _a.getAttrString( "k2" ) );        
        
        assertEquals("1", _a.getAttrStringArray( "k1" )[0] );
        assertEquals("2", _a.getAttrStringArray( "k2" )[0] );
    }
    
    public void testMultiAttrributesArray()
    {
        _ann _a = _ann.of("@a(k1={1,2,3,4,5},k2=2)");
        assertEquals("{ 1, 2, 3, 4, 5 }", _a.getAttrString( "k1" ) );
        assertEquals("1", _a.getAttrStringArray( "k1" )[0] );
        assertEquals("2", _a.getAttrStringArray( "k1" )[1] );
        assertEquals("3", _a.getAttrStringArray( "k1" )[2] );
        assertEquals("4", _a.getAttrStringArray( "k1" )[3] );
        assertEquals("5", _a.getAttrStringArray( "k1" )[4] );
        
        assertEquals("2", _a.getAttrStringArray( "k2" )[0] );        
    }
    
    enum Topping
    {
        SPRINKLES,
        CARMEL,
        CHOCOLATE,
        BUTTERSCOTCH
    }
    
    @interface SingleDip
    {
        int scoops();
        Topping t () default Topping.BUTTERSCOTCH;
        Topping[] ts ();
    }
    
    @interface MultiDip
    {
        int scoops();
        Topping[] ts ();
    }
    
    public void testEnumAttr()
    {
        _ann _a = _ann.of("@a(Topping.SPRINKLES)");
        assertEquals( "Topping.SPRINKLES", _a.getAttrString());
        assertEquals( "Topping.SPRINKLES", _a.getAttrStringArray()[0]);
        assertEquals( "Topping.SPRINKLES", _a.getAttrs().values.get( 0 ) );        
    }
    
    public void testEnumAttrs()
    {
        _ann _a = _ann.of("@a({Topping.SPRINKLES, Toppings.CHOCOLATE})");
        assertEquals( "{ Topping.SPRINKLES, Toppings.CHOCOLATE }", _a.getAttrString());
        assertEquals( "Topping.SPRINKLES", _a.getAttrStringArray()[0]);
        assertEquals( "Toppings.CHOCOLATE", _a.getAttrStringArray()[1]);
        
        assertEquals( "{ Topping.SPRINKLES, Toppings.CHOCOLATE }", _a.getAttrs().values.get( 0 ) );              
    }
    
    public void testMultiAttrArray()
    {
        _ann _a = _ann.of(
            "@a(flavor=\"vanilla\", scoops=2,toppings={Topping.SPRINKLES, Toppings.CHOCOLATE})");
        assertEquals(3, _a.getAttrs().count());
        assertEquals("\"vanilla\"", _a.getAttr("flavor" ) );
        assertEquals("vanilla", _a.getAttrString( "flavor" ) );
        assertEquals("2", _a.getAttrString( "scoops" ) );
        assertEquals("{ Topping.SPRINKLES, Toppings.CHOCOLATE }", _a.getAttrString( "toppings" ) );
        assertEquals("Topping.SPRINKLES", _a.getAttrStringArray( "toppings" )[0] );
        assertEquals("Toppings.CHOCOLATE", _a.getAttrStringArray( "toppings" )[1] );        
    }
    
    public void testKeyValueAttributes()
    {
        _ann _a= _ann.of("@a(key=1)");
        assertTrue( _a.getAttrs().keys.contains( "key" ) );
        assertTrue( _a.getAttrs().values.contains( "1" ) );
        assertEquals( "1", _a.getAttrString( "key" ) );        
    }
    
    public void testAttributesStringArray()
    {
        String[] ar = _ann._attrs.parseStringArray( "\"public static {+type+} {+name+};\"" );
        assertEquals( 1, ar.length );
        assertEquals( "public static {+type+} {+name+};", ar[ 0 ] );
            
        String[] arr = _ann._attrs.parseStringArray( "{\"A\"}" );
        assertEquals( 1, arr.length );
        assertEquals( "A", arr[0] );
        
        arr = _ann._attrs.parseStringArray( "{\"A\", \"B\"}" );
        System.out.println( arr[ 0 ] );
        assertEquals( 2, arr.length );
        assertEquals( "A", arr[0] );
        assertEquals( "B", arr[1] );        
        
        //now read the literal string array from the annotation model
        _fields._field _f = _fields._field.of( "@$({\"a\", \"name\"})", "public int a;" );        
        _ann _as = _f.getAnnotations().getOne( varcode.java.draft.$.class );
        
        String[] keyValues = _ann._attrs.parseStringArray( 
            _as.getAttrs().values.get( 0 ) );
        
        assertTrue( keyValues.length == 2 );
        assertEquals( "a", keyValues[ 0 ]);
        assertEquals( "name", keyValues[ 1 ]);
    }
    
    public void testNoAttrs()
    {
        _ann a = _ann.of("@a");
        assertEquals( "@a", a.author() );        
        assertEquals( "a", a.getName() );
        assertTrue( a.getAttrs().isEmpty() );
        assertEquals( 0, a.getAttrs().count() );        
    }
    
    public void testOneValueOnlyAttr()
    {
        _ann a = _ann.of( "@a(1)" );
        assertEquals( "@a(1)", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "1", a.getAttrs().author() );
        assertFalse( a.getAttrs().isEmpty() );
        assertEquals( 1, a.getAttrs().count() );
    }
    
    public void testOneKeyValueAttr()
    {
        _ann a = _ann.of("@a(key=1)");
        assertEquals( "@a(key = 1)", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "key = 1", a.getAttrs().author() );
        assertFalse( a.getAttrs().isEmpty() );
        assertEquals( 1, a.getAttrs().count() );
        assertEquals( "key", a.getAttrs().keys.get( 0 ) );
        assertEquals( "1", a.getAttrs().values.get( 0 ) );
    }
    
    public void testOneValueArrayAttr()
    {
        _ann a = _ann.of("@a({1,2,3})");
        assertEquals( "@a({ 1, 2, 3 })", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "{ 1, 2, 3 }", a.getAttrs().author() );
        assertFalse( a.getAttrs().isEmpty() );
        assertEquals( 1, a.getAttrs().count() );
        //assertEquals( "key", a.getAttrs().keys.get( 0 ) );
        assertEquals( "{ 1, 2, 3 }", a.getAttrs().values.get( 0 ) );
    }
     
    public void testMultiAttrWithArr()
    {
        _ann a = _ann.of( "@a(a = 1,b = { int.class, String.class })");
        assertEquals( "@a(a = 1, b = { int.class, String.class })", a.author() );
        assertEquals( "a", a.getName() );
        assertEquals( "a = 1, b = { int.class, String.class }", a.getAttrs().author() );
        assertFalse( a.getAttrs().isEmpty() );
        assertEquals( 2, a.getAttrs().count() );
        
        assertEquals( "a", a.getAttrs().keys.get( 0 ) );
        assertEquals( "b", a.getAttrs().keys.get( 1 ) );
        assertEquals( "1", a.getAttrs().values.get( 0 ) );
        assertEquals( "{ int.class, String.class }", a.getAttrs().values.get( 1 ) );
        
    }
    
    public static final void main( String[] args )
    {
        _ann.of( "@Classes(\"value\")" );
        _ann.of( "@Classes({int.class, String.class})");        
        _ann.of( "@Classes(classes = {int.class, String.class})");        
        _ann.of( "@Classes(a=1)");
        _ann.of( "@ann(a=1,b=2)");
        _ann.of( "@ann(a=1,b=\"big giant monster\")");
        _ann.of( "@ann(a=1,b={1,2,3,4,5})");
        _ann.of( "@ann(a=1,b={int.class, String.class})");
    }
}
