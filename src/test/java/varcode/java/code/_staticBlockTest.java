/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import junit.framework.TestCase;
import varcode.context.VarContext;

/**
 *
 * @author eric
 */
public class _staticBlockTest
    extends TestCase
{
    public void testEmptyStaticBlock()
    {
        _staticBlock sb = new _staticBlock();
        assertEquals( "", sb.author(  ) );
        assertEquals( "", sb.bindIn( VarContext.of() ).author( ) );
        
        
        assertTrue( sb.isEmpty() );
        _code c = sb.getBody();
        assertTrue( c.isEmpty() );                                
        
        sb.replace("A", "Z");
        assertEquals( "", sb.author(  ) );
        assertEquals( "", sb.bindIn( VarContext.of() ).author() );
    }
    
    public static final String N = "\r\n";
    
    public void testAddHeadTailStaticBlock()
    {
        _staticBlock sb = new _staticBlock();
        sb.addHeadCode("LOG.debug(\"head of static block\");");
        assertEquals(
            "static" + N +
            "{" + N + 
            "    LOG.debug(\"head of static block\");" + N +
            "}", sb.author( ) );
        
        assertFalse( sb.isEmpty() );
        _code c = sb.getBody();
        assertFalse( c.isEmpty() );
        
        //replace
        sb.replace("LOG","log");
        
        assertEquals(
            "static" + N +
            "{" + N + 
            "    log.debug(\"head of static block\");" + N +
            "}", sb.author( ) );
        
        sb.addTailCode("log.error(i);");
        
        assertEquals(
            "static" + N +
            "{" + N + 
            "    log.debug(\"head of static block\");" + N +
            "    log.error(i);" + N +        
            "}", sb.author( ) );
        
        sb.addTailCode("log.error(i);");
        
    }
    public void testParameterizedStaticBlock()
    {
        _staticBlock sb = new _staticBlock();
        sb.addHeadCode("LOG.debug(\"{+message+}\");");
        
        assertEquals(
            "static" + N +
            "{" + N + 
            "    LOG.debug(\"{+message+}\");" + N +
            "}", sb.author( ) );
        
        assertEquals(
            "static" + N +
            "{" + N + 
            "    LOG.debug(\"HELLO\");" + N +
            "}", sb.bindIn( VarContext.of( "message", "HELLO" ) ).author() );        
        
         
    }
    
}
