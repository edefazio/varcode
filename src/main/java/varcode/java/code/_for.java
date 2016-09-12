/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import varcode.Template;
import varcode.doc.Directive;

/**
 * Ideally I'd have a way of peeking into the for loop to get the loop 
 * variables
 * 
 * Lets just say, I COULD do this
 * 
 * code.add( "for(int i=0; i<100; i++)" ); 
 * 
 * ...And it would KNOW that this is a for loop, it would parse it
 * 
 * 
 * 
 * 
 * forVars
 *    String varName (int i)
 *    Init           (=100);
 *    
 * @author eric
 */
public class _for
    extends Template.Base
{
    public final _javaCode block;
    public final Object init;
    public final Object condition;
    public final Object loopCode;
        
    public static _for of( Object init, Object condition, Object loopCode, Object...codeBlock )
    {
        return new _for( init, condition, loopCode, codeBlock );
    }
        
    public _for( Object init, Object condition, Object loopCode, 
        Object...codeBlock )                
    {
        this.init = init;
        this.condition = condition;
        this.loopCode = loopCode;
        this.block = new _javaCode();
        block.add( codeBlock );        
    }
        
    public String toString()
    {
        return toString( 0 );            
    }
        
    public String toString( int indent )
    {
        StringBuilder sb = new StringBuilder();
            
        sb.append( indent( indent ) );
        sb.append( "for( " ).append( init ).append( "; " )
          .append( condition ).append( "; " )
          .append( loopCode ).append( " )" );
        sb.append( "\r\n" );
            
        sb.append( indent( indent ) );            
        sb.append( "{" );
        sb.append( "\r\n" );
            
        sb.append( this.block.toString( indent + 1 ) );
        sb.append( "\r\n" );            
        sb.append( indent( indent ) );            
        sb.append( "}" );
            
        return sb.toString();
    }

    @Override
    public String author( Directive... directives )
    {
        //TODO fix this
        return toString( 0 ); 
    }
}