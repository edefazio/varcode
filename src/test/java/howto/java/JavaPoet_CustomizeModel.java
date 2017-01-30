/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package howto.java;

import varcode.author.Author;
import varcode.java.model._methods;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class JavaPoet_CustomizeModel
{
       
    private static Template METHOD_SIG = 
        BindML.compile( "public int {+name*+}()" );
    
    private static Template RANGE_BODY = 
        BindML.compileLines(
            "int result = 0;",                
            "for( int i = {+from*+}; i < {+to*+}; i++ )",
            "{",
            "    return = result {+op+} i;",
            "}" );
    
    private static _methods._method computeRange(
        String name, int from, int to, String op )
    {
        return _methods._method.of( 
            Author.fillSeries( METHOD_SIG, name ),
            Author.fillSeries( RANGE_BODY, from, to, op )
        );        
    }
    
    
    public static _methods._method methodParameterized_Model()
    {
        String name = "multiply10To20";
        int from = 10;
        int to = 20;
        String op = "*";
        
        _methods._method _range2 = computeRange( name, from, to, op );        
        //System.out.println( _range2 );
        return _range2;
    }
}
