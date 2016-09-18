package varcode.java.code;

import varcode.Template;
import varcode.context.VarContext;
import varcode.doc.Author;
import varcode.doc.Directive;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * Construct Simple for... loops with a _code body
 * 
 * instead of putting together a String like this within code:
 * 
 * String forLoop = "for (int i=0; i<100; i++)" + N +
 * "{" + N 
 * "    LOG.debug( i );" + N 
 * "}" + N;
 * 
 * we create the _for model that will print itself as a String that represents
 * the for loop:
 * 
 * _for.count("i", 100).body("LOG.debug( i );");
 * 
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _for 
    extends Template.Base
{
    private String init;
    private String condition;
    private String update;
    private _code code;
    
    public _for( String init, String condition, String update )
    {
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.code = new _code();
    }
    
    //"for( int i = startIndex; i < startIndex + partitionCount; i++ )",
    //"for( int i = {+init*+}; i {+terminal*+}; i++ )"
    //
    public static _for count( int count )
    {
        return count( "i", count );
    }
    
    public static _for count( int min, int max )
    {
        return count( "i", min, max );
    }
    
    public static _for count( String varName, int min, int max )
    {
        return new _for( 
            "int " + varName + " = " + min, varName + " <= " + max, varName + "++" );
    }
    
    public static _for count( String varName, int count )
    {
        return new _for( 
            "int " + varName + " = 0", varName + " < " + count, varName + "++" );
    }
    
    /**
     * Adds code to the head of the for loop body
     * @param code
     * @return 
     */
    public _for head( Object...code )
    {
        this.code.addHeadCode( code );
        return this;
    }
    
    /** 
     * Add code to the end of the body of the for loop and returns 
     */
    public _for body( Object... code )
    {
        this.code.addTailCode( code );
        return this;
    }
    
    
    /**
     * <PRE>
     * countDown(10);
     * 
     * for( int i = 10; i > 0; i--)
     * {
     * }
     * </PRE>
     */
    public static _for countDown( int count )
    {
        return countDown( "i", count );
    }
    
    public static _for countDown( String varName, int count )
    {
        return new _for( 
            "int " + varName + " = " + count, varName + " > 0", varName + "--" );
    }
        
    public static final Dom FOR = 
        BindML.compile( 
            "for( {+init+}; {+condition+}; {+update+} )" + N 
          + "{" + N          
          + "{{+?code:{+$>(code)+}" + N +"+}}"  
          + "}" + N );        
    
    @Override
    public String toString( )
    {
        return author( );
    }
    
    @Override
    public String author( Directive... directives )
    {
        return Author.code( FOR, getContext(), directives );
    }
    
    public VarContext getContext()
    {
        return VarContext.of(
            "init", this.init,
            "condition", this.condition,
            "update", this.update,
            "code", this.code );
    }

    @Override
    public _for replace(String target, String replacement)
    {
        this.init = this.init.replace( target, replacement );
        this.code.replace( target, replacement );
        this.condition = this.condition.replace(target, replacement);
        this.update = this.update.replace( target, replacement );
        return this;
    }
   
}
