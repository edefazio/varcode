/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.load.complex;

import com.github.javaparser.ast.CompilationUnit;
import example.complex.ComplexAnnotationType;
import example.complex.ComplexClass;
import example.complex.ComplexEnum;
import example.complex.ComplexEnumWithConstantBody;
import example.complex.ComplexInterface;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.load._JavaLoad;
import varcode.java.model._annotationType;
import varcode.java.model._class;
import varcode.java.model._enum;
import varcode.java.model._interface;

/**
 *
 * @author Eric
 */
public class Load_ComplexExamples
    extends TestCase
{
    /** This is a multi
     * line
     * java 
     * doc
     */
    static class JD { }
    
    public void testLoadJavadoc()
    {
        _class _c = Java._classFrom( JD.class );
        System.out.println( _c );
    }
    
    /* Works now */
    public void testLoad_class()
    {
         _class _c = Java._classFrom( ComplexClass.class );
         _class _c2 = _class.cloneOf( _c );
        
         System.out.println( _c );
         System.out.println( _c2 );
         
         assertEquals( _c, _c2 );
         
        
    }
    
    public void testLoad_enum()
    {
        CompilationUnit astRoot = Java.astFrom( ComplexEnum.class );
        
        
        //System.out.println( astRoot );
        
        _enum _e1 = _JavaLoad._enumFrom( 
            astRoot, 
            ComplexEnum.class, 
            new FormatJavaCode_AllmanScanStyle() );
        
        assertNotNull( 
            _e1.getConstant( "A" ).getAnnotation( Deprecated.class ) );
        //System.out.println( _e1 );
        
        _enum _e2 = _enum.cloneOf( _e1 );
        assertEquals( _e1, _e2 );
        //_enum _e = Java._enumFrom( ComplexEnum.class );
        //System.out.println( _e );
    }
    
    public void testLoad_enumConstBody()
    {
        _enum _e = Java._enumFrom( ComplexEnumWithConstantBody.class );
        
        System.out.println( _e ); 
        _enum _e2 = _enum.cloneOf( _e );
        System.out.println( _e2 ); 
        assertEquals( _e, _e2 );
               
    }
    
    public void testLoad_interface()
    {
        CompilationUnit cu = Java.astFrom( ComplexInterface.class );
        System.out.println( "CU" + cu );
        
        _interface _i = Java._interfaceFrom( ComplexInterface.class );
        System.out.println( _i );    
        
        _interface _i2 = _interface.cloneOf( _i );
        
        assertEquals( _i, _i );
        assertEquals( _i2, _i2 );
        assertEquals( _i, _i2 );
    }
    
    public void testLoad_annotationType()
    {
        _annotationType _at = Java._annotationTypeFrom( 
            ComplexAnnotationType.class );
        _at.getProperty( "count" );
        _at.getProperty( "arr" );
        _at.getProperty( "e" );
            
        System.out.println( _at );
    }
}
