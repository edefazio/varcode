package varcode.eval;

import javax.script.SimpleBindings;

import junit.framework.TestCase;
import varcode.context.ScopeBindings;
import varcode.context.VarBindings;
import varcode.dom.Dom;
import varcode.java.Java;

/*{$init(
var fun1 = function( name ) 
{
    print('Hi there from Javascript, ' + name);
    return "greetings from javascript";
};

var fun2 = function( object ) 
{
    print("JS Class Definition: " + Object.prototype.toString.call(object) );
};
)$}*/

/*{((
var fun1 = function( name ) 
{
    print('Hi there from Javascript, ' + name);
    return "greetings from javascript";
};

var fun2 = function( object ) 
{
    print("JS Class Definition: " + Object.prototype.toString.call(object) );
};
))}*/
/** 
 * @author eric
 *
 */
public class Eval_JavaScriptTest
	extends TestCase
{

	String functionLib = 
	"var fun1 = function(name) { " + System.lineSeparator() +
	"print('Hi there from Javascript, ' + name);" + System.lineSeparator() + 
	"return \"greetings from javascript\";" + System.lineSeparator() +
	"};"+ System.lineSeparator() +
	System.lineSeparator() +
	"var fun2 = function (object) {" + System.lineSeparator() +
	"print(\"JS Class Definition: \" + Object.prototype.toString.call(object));"+ System.lineSeparator() +
	"};";
	
	public void testLoadAFunction()
	{
		SimpleBindings sb = new SimpleBindings();
		sb.put( "myFunction", "function myFunction(p1, p2){ return p1* p2;}" );
		
		//Object res = 
	    //		ExpressionEvaluator_JavaScript.INSTANCE.evaluate( sb, "myFunction(2, 4);" );
		Eval_JavaScript.INSTANCE.evaluate( sb, functionLib );
		
		System.out.println( 
			Eval_JavaScript.INSTANCE.evaluate( sb, "fun1('Eric')" ) );
		//System.out.println( "res" );
	}
	
	 public void testEvalNoInput()
	    {
	        Evaluator scriptEval = Eval_JavaScript.INSTANCE;
	        Object res = scriptEval.evaluate( 
	            new ScopeBindings(), "3 + 4" );
	        assertEquals( res, 7 );
	        
	        try
	        {
	            scriptEval.evaluate( new ScopeBindings(), "sdklfjasdoifu is read" );
	            fail("expected Exception for Bad Script");
	        }
	        catch( EvalException se )
	        {
	            //expected
	        }        
	    }
	    
	    public void testEvalBinding()
	    {
	        Evaluator scriptEval = Eval_JavaScript.INSTANCE;
	        VarBindings vb = new VarBindings();
	        vb.put( "A", 100 );
	        vb.put( "B", 500 );
	        Object res = 
	        	scriptEval.evaluate( vb, "(A + B) | 0" ); //use | 0 to cast float to int
	        assertEquals( 600, res ); 
	    }
	    
	public static void main( String[] args )
	{
		Dom dom = Java.compileDom( Eval_JavaScriptTest.class ); 
		System.out.println( dom );
	}
}
