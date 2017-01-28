/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.model;

import varcode.java.model._javadoc;
import junit.framework.TestCase;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public class _javadocTest
    extends TestCase
{
    public static final String N = System.lineSeparator();
    
     /**
   * Hides {@code message} from the caller's history. Other
   * participants in the conversation will continue to see the
   * message in their own history unless they also delete it.
   *
   * <p>Use {@link #delete(Conversation)} to delete the entire
   * conversation for all participants.
   */
    public void testExample()
    {
        _method.of(  
            _javadoc.of(  "Hides {@code message} from the caller's history. Other",
                "participants in the contersation will continue to see the ",
                "message in their history unless they also delete it.",
                "",
                "<P>Use {@link #delete(Conversation)} to delete the entire",
                "conversation for all participants."
            ),
            "void dismiss(Message message)" );
            
            
            
    }
    public void testNone()
    {
        _javadoc _jd = _javadoc.of( );
        assertEquals( "", _jd.author() );
        _jd = _javadoc.of( "");
        assertEquals( "", _jd.author() );
        
    }
    
    //TODO I COULD FIX THIS TO BE ONE LINE /** a */
    public void testOneLine()
    {
        _javadoc _jd = _javadoc.of( "a" );
        assertEquals( "/**" + N + 
                      " * a" + N + 
                      " */" + N, _jd.author() );
    }
    
    public void testMultiLine()
    {
        _javadoc _jd = _javadoc.of( "a"+ N + "b" );
        assertEquals( "/**" + N + 
                      " * a" + N + 
                      " * b" + N + 
                      " */" + N, _jd.author() );
    }
}
