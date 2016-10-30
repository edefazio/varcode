package varcode.java.model.cs;

import varcode.context.VarContext;
import varcode.doc.Compose;
import varcode.doc.Directive;
import varcode.doc.Dom;
import varcode.markup.bindml.BindML;
import varcode.Model;
import varcode.java.model._code;

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
    implements Model
{        
    public static _forCount count( int count )
    {
        return _forCount.up( count );
    }
    
    public static _forCount count( int min, int max )
    {
        return _forCount.up( min, max );
    }
    
    public static _forCount count( String varName, int min, int max )
    {
        return _forCount.up( varName, min, max );
            //"int " + varName + " = " + min, varName + " <= " + max, varName + "++" );
    }
    
    public static _forCount count( String varName, int count )
    {
        return _forCount.up( varName, count );
    }
    
    public static _forEach each( Class elementType, String elementName, String array )
    {
        return _forEach.of( elementType, elementName, array );
    }
    
    public static _forEach each( String type, String elementName, String array )
    {
        return _forEach.of( type, elementName, array );
    }
    
    public static _for of( String init, String condition, String update )
    {
        return new _for( init, condition, update );
    }
    
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
     * @param code
     * @return this (modified)
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
     * @param count the start count
     * @return new _for model
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
        return Compose.asString( FOR, getContext(), directives );
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
    public _for bind( VarContext context )
    {
        this.init = Compose.asString( BindML.compile( this.init ), context );
        this.condition = Compose.asString( BindML.compile( this.condition ), context );
        this.code = this.code.bind( context );
        this.update = Compose.asString( BindML.compile( this.update ), context );
        return this;
    }
    
    @Override
    public _for replace( String target, String replacement)
    {
        this.init = this.init.replace( target, replacement );
        this.code.replace( target, replacement );
        this.condition = this.condition.replace(target, replacement);
        this.update = this.update.replace( target, replacement );
        return this;
    }
   
}
