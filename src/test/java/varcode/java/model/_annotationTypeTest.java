/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this toState file, choose Tools | Templates
 * and open the toState in the editor.
 */
package varcode.java.model;

import varcode.java.model._javadoc;
import varcode.java.model._annotationType;
import varcode.java.model._package;
import varcode.java.model._modifiers;
import varcode.java.model._imports;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.author.Author;
import varcode.java.model._annotationType._annotationProperty;
import varcode.java.model._annotations._annotation;

/**
 *
 * @author Eric
 */
public class _annotationTypeTest
    extends TestCase
{
    
    public void testSimpleThingsSimple()
    {
        _annotationType _at = _annotationType.of( "A" );
        System.out.println( _at );        
        
    }
    
    enum SomeEnum
    {
        A,B,C,D;
    }
    
    /**
     * The Annotation Type Javadoc
     */
    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.METHOD )
    private @interface ClassPreamble 
    {
        /** a Property that is a double*/
        double d();
        
        long l();
        int i();
        byte b();
        char c();
        boolean bool();
        float f();
        SomeEnum e();
        double[] darr();
        long[] larr();
        int[] intarr();
        byte[] bytearr();
        char[] carr();
        boolean[] barr();
        float[] farr();
        
        String s();
        String[] sarr();
        
        String author();
        String date();
        int currentRevision() default 1;
        String lastModified() default "N/A";
        String lastModifiedBy() default "N/A";
        // Note use of array
        String[] reviewers();
    }
    
    public void testInit()
    {        
        //_annotationProperties _aps = new _annotationProperties();
        //System.out.println( "*APS*" + _aps ); 
        
        _annotationType _a = _annotationType.of(
            "package blah;",             
            "/**javadoc*/", 
            "@Anno",
            Map.class,
            "public @interface A" );
        
        assertEquals( "A", _a.getName() );
        assertEquals( "blah", _a.getPackageName() );
        assertEquals( 1, _a.getImports().count() );
        assertEquals( "javadoc", _a.getJavadoc().getComment() );
        
        assertEquals( 1, _a.getAnnotations().count() );
        assertEquals( "@Anno", _a.getAnnotations().getAt( 0 ).toString().trim() );
    }
    
    public void testInit2()
    {
        _annotationType _a = _annotationType.of( 
            _package.of("blah"), 
            _javadoc.of("javadoc"),
            _annotation.of( "@Anno" ),
            _imports.of( Map.class ),            
            "public @interface A"
        );
        
        assertEquals( "A", _a.getName() );
        assertEquals( "blah", _a.getPackageName() );
        assertEquals( 1, _a.getImports().count() );
        assertEquals( "javadoc", _a.getJavadoc().getComment() );
        
        assertEquals( 1, _a.getAnnotations().count() );
        assertEquals( "@Anno", _a.getAnnotations().getAt( 0 ).toString().trim() );
        
        System.out.println( _a );
    }
    
    public void testInitPropsAdd()
    {
        _annotationType _a = _annotationType.of( 
            _package.of("blah"), 
            _javadoc.of("javadoc"),
            _annotation.of( "@Anno" ),
            _imports.of( Map.class ),            
            "public @interface A"
        ).add( _annotationProperty.of( "String", "label" ) );
        
        _annotationProperty _ap = _a.getProperty( "label" );
        assertEquals( "label", _ap.getName() );
        assertEquals( "String", _ap.getType() );
        assertEquals( null, _ap.getDefaultValue() );
        
        _a.property( int.class, "count", 1 );        
        _ap = _a.getProperty( "count" );
        
        assertEquals( "count", _ap.getName() );
        assertEquals( "int", _ap.getType() );
        assertEquals( "1", _ap.getDefaultValue() );
        
        _a.property( float.class, "avg", 1.0f );        
        _ap = _a.getProperty( "avg" );
        
        assertEquals( "avg", _ap.getName() );
        assertEquals( "float", _ap.getType() );
        assertEquals( "1.0f", _ap.getDefaultValue() );
        
        
        System.out.println( _a );
        
    }
    public void doOther()
    {
        
        _annotationType _at = new _annotationType( 
            new _annotationType._signature( "blah" ) );
        System.out.println( _at );
        
        
        _at.imports( Map.class );
        System.out.println( _at );
        
        _at.imports( UUID.class );
        System.out.println( _at );
        
        _at.annotate( "@Retention( RetentionPolicy.RUNTIME )" );
        System.out.println( _at );
        
        _at.annotate( "@Target( ElementType.METHOD )" );
        System.out.println( _at );
        
        _at.property( int.class, "count", "1" );
        _at.property( int[].class, "counts" );
        _at.property( String[].class, "names" );
        _at.property( String.class, "name" );
        System.out.println( _at );
        
        System.out.println( Author.toString( _annotationType._annotationProperty.PROPERTY, 
            "type", int.class, "name", "count" ) );
        
        System.out.println( Author.toString( _annotationType._annotationProperty.PROPERTY, 
            "type", String.class, 
            "name", "name",
            "defaultValue", "\"Mr. Pickels\"" ) );
        
        _annotationType._signature _s = new _annotationType._signature( "ann" );
        System.out.println( _s.author() );
        _s = new _annotationType._signature( _modifiers.of( "protected"), "ann" );
        System.out.println( _s.author() );
        
    }
}
