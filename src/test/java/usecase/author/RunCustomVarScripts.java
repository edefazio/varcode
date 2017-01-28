/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase.author;

import junit.framework.TestCase;
import varcode.author.Author;
import varcode.context.Context;
import varcode.context.VarBindException;
import varcode.context.VarContext;
import varcode.context.VarScript;

/**
 *
 * @author Eric
 */
public class RunCustomVarScripts
    extends TestCase
    implements VarScript
{
    public void testInstanceScript()
    {
        String authored = Author.toString( 
            "{+$callInstanceScript()+}", 
            VarContext.of( "callInstanceScript", this ) );
        
        assertEquals( "returnedFromInstanceScript", authored );
    }

    @Override
    public Object eval( Context context, String input )
        throws VarBindException
    {
        return "returnedFromInstanceScript";
    }
    
    public enum StaticClassScript
        implements VarScript
    {
        INSTANCE;
        
        @Override
        public Object eval( Context context, String input )
            throws VarBindException
        {
            return "returnedFromEnumInstance";
        }        
    }
    
    public void testResolveScriptByFullName()
    {
        String addScriptResultMark = 
           "{+$" + RunCustomVarScripts.class.getCanonicalName() + ".eval()+}";
        
        System.out.println( addScriptResultMark );
        
        String authored = Author.toString( 
            addScriptResultMark, 
            VarContext.of(  ) );
        
        assertEquals( "returnedFromInstanceScript", authored );
    }
    
    public static final void main(String[] args)
    {
        String s = StaticClassScript.class.getName();
        System.out.println( StaticClassScript.class.getName() );
        System.out.println( StaticClassScript.class.getCanonicalName() );
    }
}
