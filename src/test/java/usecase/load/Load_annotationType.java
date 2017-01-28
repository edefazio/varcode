/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase.load;

import example.ExAnnotationType;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.load._JavaLoad;
import varcode.java.model._annotationType;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class Load_annotationType
    extends TestCase
{
    public void testIt()
    {
        _class _model = _class.of( "public class Model" ).imports( UUID.class )
            .method( "public String createId()",
        "return UUID.randomUUID().toString();" );
        
        System.out.println( _model );
        
    }
    public void test_loadFromClass()
    {
        _annotationType _at = 
            Java._annotationTypeFrom( ExAnnotationType.class );
        
        assertEquals( 
            _at.getPackageName(), 
            ExAnnotationType.class.getPackage().getName() );
    }
    
    public void test_loadFromString()
    {
        _annotationType _at = _JavaLoad._annotationTypeFrom( 
            "public @interface MyAnnType{}" );
        assertEquals( "MyAnnType", _at.getName()  );
    }
    
    public @interface NestedAnnotationType
    {
        int count() default 3;
    };
        
    public void test_loadNestedClass()
    {
        _annotationType _at = 
            Java._annotationTypeFrom( NestedAnnotationType.class );
        
        assertEquals( "3", _at.getProperty( "count" ).getDefaultValue() );
    }
}
