/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase.adhoc;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.adhoc.AdHoc;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocClassPublisher;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class AdHocToParentClassLoad
    extends TestCase
{
    
    
    public void testPromoteAdHocClasses()
        throws Exception
    {
        _class _c = _class.of( "public class DynaPromoteClass" )
            .method( "public String toString()",
                "return \"Hello DynamicThenPromoteClass\";" );
        
        AdHocClassLoader adHocClassLoader = AdHoc.compile( 
            Thread.currentThread().getContextClassLoader(), _c );
        
        //
        ClassLoader parent = adHocClassLoader.getParent();
        
        
        AdHocClassPublisher.publishToParent( adHocClassLoader );
        
        Class cl = adHocClassLoader.loadClass( _c.getQualifiedName() );
        
        Class cl2 = parent.loadClass(_c.getQualifiedName() );
        
        //verify 
        assertEquals( cl, cl2 );
        
        //create an instance 
        Object instance = Java.instance( cl2 );
        
        //verify that when I create an instance, the ClassLoader returned is the 
        //PARENT ClassLoader NOT the AdHocClassLoader
        assertEquals( parent, instance.getClass().getClassLoader() );
        
    }
    
    public void testDefineClassInSystemClassLoader()
    {
        //define the class model
        _class _c = _class.of( "public class DynaClass" )
            .method( "public String toString()",
                "return \"Hello DynamicClass\";" );
        
        //compile class (temporarily to a new AdHocClassLoader)
        AdHocClassLoader adHocCL = AdHoc.compile( _c );
        
        //get the System classLoader
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        
        
        AdHocClassPublisher.publishClass( 
            systemClassLoader, 
            _c.getQualifiedName(),
            adHocCL.findClassFile( _c ).toByteArray() );                
    }
    
    public void testDefineClassInParentClassLoader()
    {
        _class _c = _class.of( "public class DynaClass" )
            .method( "public String toString()",
                "return \"Hello DynamicClass\";" );

        //temporarily compile it to an AdHocClassLoader
        AdHocClassLoader adHocCL = AdHoc.compile( _c );
        
        //get the parent classLoader (depends on environment)
        ClassLoader parentClassLoader = adHocCL.getParent();
        
        //define the class in the parent ClassLoader
        AdHocClassPublisher.publishClass( 
            parentClassLoader, 
            _c.getQualifiedName(), 
            adHocCL.findClassFile( _c.getQualifiedName() ).toByteArray() );
        
        try
        {
            Class dynaClass = parentClassLoader.loadClass( _c.getName() );
            assertNotNull( dynaClass );
            Object dynaInstance = Java.instance( dynaClass );
            assertEquals( "Hello DynamicClass", Java.call( dynaInstance, "toString" ));
            //System.out.println( "GOT CLASS "+ dynaClass );
        }
        catch( Exception e )
        {
            fail("Exception getting class from parent classloader" + e );
            //System.out.println( "EXCEPTION DEFINING CLASS " + e );
        }
    }
}
