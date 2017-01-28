/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java;

import junit.framework.TestCase;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class _JavaTest
    extends TestCase
{   
    public void testModel()
    {
        //we can build a _class (model) manually 
        _class _c = _class.of( "public class A" )
            .packageName( "metalang.java;") 
            .field( "public int ID = 3;" );
        
        assertEquals( "int", _c.getFieldByName( "ID" ).getType() );
        
        Object instanceA = _c.instance(  );//create an instance from model
        assertEquals( 3, Java.getFieldValue( instanceA, "ID" ) );
    }
        
        
    public void testModelFromString()
    {
        //we can load a _class (metamodel) from a String
        _class _c = Java._classFrom( 
            "package metalang.java; public class A{ public int ID=1; }" );                
        assertEquals( "int", _c.getFieldByName( "ID" ).getType() );
        
        Object instanceA = _c.instance(  );//create an instance from model
        assertEquals( 1, Java.getFieldValue( instanceA, "ID" ) );
    }       
}
