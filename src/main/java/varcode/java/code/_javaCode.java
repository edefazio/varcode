/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varcode.java.code;

import java.util.ArrayList;
import java.util.List;
import varcode.Template;
import varcode.doc.Directive;

/**
 *
 * There are multiple "variants" of code structures
 * <PRE>
 * _javaCode jc = _javaCode.of();
 * //so, when we print out 
 * .log(
 * .logDebug("got here {+className+}.{+methodName+}") //create a Log add to tail
 * .logInfo("blah {+className+} {+packageName+}")
 * 
 * .ifBlock("g == 5", Object...code );
 * .whileBlock("i < 100"
 * 
 * 
 * _codeStructure.catchHandleException( 
 *    IOException.class, 
 *    String handleCode );
 * 
 * tryCatchBlock:
 * .tryCatchBlock( _codeStructure,  )
 * try
 * {
 *     //code in here
 * }
 * catch(E ...e)
 * { 
 *    
 * }
 * 
 * forBlock:
 * 
 * for(int i=0; i<100; i++)
 * {
 *    //code in here
 * }
 * 
 * </PRE>
 * 
 * @author eric
 */
public class _javaCode
    extends Template.Base
{
    public List<Object>sequence = new ArrayList<Object>();

    @Override
    public String author(Directive... directives)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //nice to have would be "FIND" which could return like a Cursor
    // and then insert at cursor
    public interface _codeStructure
    {
        /** the parent can call replace on some code */
        //public void replace( String target, String replacement );
        
        public void addHead( _codeStructure structure );
        
        public void addTail( _codeStructure structure );
    }
    
    //adds the String instruction 
    //code to the tail of the existing code
    public _javaCode add( Object... code )
    {
        for( int i=0; i<code.length; i++)
        {
            this.sequence.add( code[ i ] );
        }
        return this;
    }
    
    
    public _javaCode countBlock( int count, Object...forBlock )
    {
        String countStatement = 
            "for( int count = 0; count < " + count + "; count++ )";
        
        this.add( countStatement, "{" ); 
        
        _javaCode theBlock = new _javaCode();
        theBlock.add( forBlock );
        
        this.add( theBlock, "}" );
          
        return this;        
    }
    
    /**
     * ex forBlock("i=0", "i<100", "i++", "System.out.println(i);");
     * <PRE>
     * for(int i=0; i<100; i++)
     * {
     *     System.out.println(i);
     * }
     * </PRE>
     * 
     * nested blocks
     * 
     * @param init
     * @param condition
     * @param increment
     * @param code
     * @return 
     */
    public _javaCode forBlock( 
        String init, String condition, String increment, Object...codeBlock )
    {
        String forStatement = "for( "+init+"; "+ condition+"; "+ increment+" )";
        this.add( forStatement, "{" );
        
        _javaCode theBlock = new _javaCode();
        theBlock.add( codeBlock );
        
        this.add( theBlock, "}" );
        
        return this;
    }       
    
    public String toString()
    {
        return toString( 0 );
    }
    
    public String toString( int indent )
    {
        StringBuilder sb = new StringBuilder();
        
        for( int i = 0; i < sequence.size(); i++ )
        {
            if( i > 0 )
            {
                sb.append( "\r\n" );
            }
            if( sequence.get( i ) instanceof _javaCode )
            {
                sb.append( 
                    ((_javaCode)sequence.get( i ) ).toString( indent + 1 ) );
            }
            else
            {
                sb.append( indent( indent) );
                sb.append( sequence.get( i ).toString() );
            }
        }
        
        return sb.toString();
    }
    
   
    /*
    private static String indent( int count )
    {
        return INDENT[ count ];
    }
    */
    
    public static void main( String[] args )
    {
        _javaCode jc = new _javaCode();
        jc.add( "int i=0;" );
        //System.out.println( jc );
        
        jc.add( "String name = \"name\";" );
        System.out.println( jc );
        
        jc.countBlock( 100, "System.out.println( count );" );
        System.out.println( jc );
        
        jc.forBlock( "i=0", "i<100", "i++", "System.out.println(i);" );
        System.out.println( jc );
        
        _for f = _for.of("i=0", "i<100", "i++", "System.out.println(i);" );
        System.out.println( f );        
    }
    
}
