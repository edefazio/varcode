/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._annotations;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author Eric
 */
public class _annotationsTest
    extends TestCase
{
 
    public void testEquals()
    {
        _annotations _anns = new _annotations();
        _anns.add("@Deprecated", "@Override" );
        
        _annotations _anns2 = new _annotations();
        _anns2.add("@Deprecated", "@Override" );
        
        assertEquals( _anns, _anns2 );
        
        
    }
    public void testEmpty()
    {
        _annotations _a = new _annotations();
        assertEquals( "", _a.toString() );
        
        _a = new _annotations( true );
        assertEquals( "", _a.toString() );
        
        /*
        _a = _annotations.of( );
        assertEquals( "", _a.toString() );
        
        _a = _annotations.inlineAnnotationsOf( );
        assertEquals( "", _a.toString() );
        */
        
    }
    
    public void testName()
    {
        _annotations a = new _annotations( );
        a.add("@Deprecated");
        assertEquals( "@Deprecated" + System.lineSeparator() , a.toString() );
        
        a = new _annotations( true );
        a.add("@Deprecated");
        
        /*
        a = _annotations.of( "@Deprecated" );
        a.add("@Deprecated" + System.lineSeparator(), a.toString() );
        
        a = _annotations.inlineAnnotationsOf( "@Deprecated" );
        assertEquals( "@Deprecated", a.toString() );
        */
    }    
    
    public void testCommonAnns()
    {
        assertEquals( "@java.lang.Deprecated ", _annotations._annotation.DEPRECATED.author() );
        assertEquals( "@java.lang.Override ", _annotations._annotation.OVERRIDE.author() );
        assertEquals( "@java.lang.FunctionalInterface ", _annotations._annotation.FUNCTIONAL_INTERFACE.author() );
        assertEquals( "@java.lang.SafeVarargs ", _annotations._annotation.SAFE_VARARGS.author() );
        assertEquals( "@java.lang.SuppressWarnings ",  _annotations._annotation.SUPPRESS_WARNINGS.author() );
        
        assertEquals( "@javax.annotation.Generated(value = \"varcode\") ",
            _annotations._annotation.generated( "varcode" ).author() );
        
        assertEquals( 
            "@java.lang.SuppressWarnings({ \"deprecation\", \"unchecked\" }) ",
            _annotations._annotation.suppressWarnings("deprecation", "unchecked" ).author() );
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
        _annotations._attributes at = _annotations._attributes.of();
        assertEquals( 0, at.count() );
        assertTrue( at.isEmpty() );       
        
        assertEquals( "", at.toString() );
    }
    
    public void testAttributesSingleValue()
    {
        _annotations._attributes at = _annotations._attributes.of( "value" );
        assertEquals( 1, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "value", at.toString() );
    }
    
    public void testAttributesOne()
    {
        _annotations._attributes at = _annotations._attributes.of( "name", "value" );
        assertEquals( 1, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "name = value", at.toString() );
    }
    
    public void testAttributesMany()
    {
        _annotations._attributes at = _annotations._attributes.of( "name", "\"value\"", "a", "1");
        assertEquals( 2, at.count() );
        assertFalse( at.isEmpty() );        
        assertEquals( "name = \"value\", a = 1", at.toString() );
    }
    
    public void testAnnotationAtts()
    {
        //test single attribute
        _annotations._annotation ann = _annotations._annotation.of( "Hey", "gabba" );
        assertEquals( "@Hey(gabba) ", ann.toString() );
        
        //test name = value attribute
        ann = _annotations._annotation.of( "Hey", "gabba", "\"gabba\"" );
        assertEquals( "@Hey(gabba = \"gabba\") ", ann.toString() );
       
        ann = _annotations._annotation.of( "Hey", "gabba", 1 );
        assertEquals( "@Hey(gabba = 1) ", ann.toString() );
        
        ann = _annotations._annotation.of( 
            "Hey", 
            "int", 1, 
            "long", 1L,
            "char", 'c', 
            "bool", true, 
            "float", 1.0f, 
            "double", 1.0d );
        assertEquals( "@Hey(int = 1, long = 1L, char = 'c', bool = true, float = 1.0F, double = 1.0d) ", ann.toString() );
        
        //test multi-attribute
        ann = _annotations._annotation.of( "Hey", "gabba", "\"gabba\"", "oderous", "\"urungus\"" );
        assertEquals( "@Hey(gabba = \"gabba\", oderous = \"urungus\") ", ann.toString() );
        
        ann = _annotations._annotation.of( "@Hey", "gabba", "\"gabba\"", "oderous", "\"urungus\"" );
        assertEquals( "@Hey(gabba = \"gabba\", oderous = \"urungus\") ", ann.toString() );        
    }
    
    
   
    
    public void testOne()
    {
        _annotations al = new _annotations();
        al.add( "@Path(\"book\")" );
        assertEquals("@Path(\"book\")" + "\r\n", al.author( ) );        
        
        assertEquals( 1, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 1, al.getAnnotations().size() );
    }
    
    public void testMany()
    {
        _annotations al = new _annotations();
        al.add( "@Path(\"/{id}\")", "@GET");
        assertEquals(
            "@Path(\"/{id}\")" + "\r\n" +
            "@GET" + "\r\n", al.author( ) );     
        
        assertEquals( 2, al.count() );
        assertFalse( al.isEmpty() );
        assertEquals( 2, al.getAnnotations().size() );
    }
    
    public void testAnnotationsAuthor()
    {
        _annotations _anns = new _annotations();
        assertEquals( "", _anns.author() );
        
        _anns.add( "@Deprecated" );
        
        assertEquals( "@Deprecated" + System.lineSeparator(), _anns.toString() );
        
        
        _anns.add( "@Homogonized" );        
        assertEquals( "@Deprecated" + System.lineSeparator() + 
            "@Homogonized" + System.lineSeparator(), 
            _anns.toString() );
        
        //_anns.add( _annotation.generated( "Eric" ) );        
        //System.out.println( _anns );
        
    }
    
    public void testAnnotationsInline()
    {
        _annotations _anns = new _annotations( true );
        assertEquals( "", _anns.author() );
        
        _anns.add( "@Deprecated" );
        
        assertEquals( "@Deprecated ", _anns.toString() );
        
        _anns.add( "@Homogonized" );        
        
        assertEquals( "@Deprecated @Homogonized ", 
            _anns.toString() );
    }
}
