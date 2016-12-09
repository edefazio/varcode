package tutorial.varcode.chapx.appendix;

import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import varcode.context.VarContext;
import varcode.java._Java;
import varcode.java.lang._class;
import varcode.java.lang.cs._for;
import varcode.java.lang.cs._thread;
import varcode.java.lang.cs._while;

/**
 *
 * @author eric
 */
public class Chap1_ModelFor_If_AndAnnotations
    extends TestCase
{
    public static _class _Authored =
        _class.of("com.hascode.tutorial", 
            "CustomerService")
            .method("public String greetCustomer(String name)",
                "return \"Welcome \"+name;");
    
    public static _class _Count = 
       _class.of("com.hascode.tutorial",
            "Counter")
            .method("public int count()",
                "int total = 0;",
                _for.count( 10 )
                    .body( "total += i;" ), 
                "return total;");
    
    public static _class _NumberUtil = 
        _class.of("com.hascode.tutorial", 
            "NumberUtil")
            .method("public long {+methodName*+}( long number )",
                "return number * 2;")
            .method("public void printDoubleNumber( long number)",
                "System.out.println(\"your number doubled is: \" + {+methodName+}(number) );");
    
    public _class _BookService =
        _class.of("com.hascode.tutorial",
            "BookService")
            .imports("javax.ws.rs.GET",
                "javax.ws.rs.Path",    
                "javax.ws.rs.PathParam",
                "javax.ws.rs.core.Response")
            .annotate( "Path(\"book\")" )
            .method("public Response findById( @PathParam(\"id\") final long id )",
                "return Response.ok().build();" );
    
    //Authored Class that creates and invokes a thread
    public _class _Outer = 
        _class.of("com.hascode.tutorial",
            "Outer")
            .constructor("public Outer( final String str )", 
                "System.out.println( \"outer created with \" + str );")
            .nest(
                _class.of("public static class Inner")
                    .imports(Logger.class, LoggerFactory.class, AtomicBoolean.class)
                    .field("static Logger LOG = LoggerFactory.getLogger(\"MYLOG\");") 
                    .field("static AtomicBoolean TRIP = new AtomicBoolean(false);")
                    .method("public void runInner()",
                        "LOG.info(\"Starting a Thread\");",
                        _thread.run(
                            "LOG.error(\"inner runs in new thread\");",
                            "TRIP.set(true);" )
                            .start(),                        
                        "LOG.info(\"Back in main thread\");",
                        _while.is( "!TRIP.get()", 
                            "LOG.info(\"\"+TRIP.get());")
                    )
            );       
    
    public void testIt( )
    {        
        System.out.println( 
            _Java.invoke( _Authored.instance( ), "greetCustomer", "Eric" ) );
        
        assertEquals( 45, _Java.invoke( _Count.instance( ), "count" ) );
        
        /*
        Object inst = 
            _NumberUtil.bindInstance( VarContext.of( "methodName", "doubleNumber" ) );
        */
        Object inst = 
            _NumberUtil.bind( VarContext.of( "methodName", "doubleNumber" ) ).instance( );
        
        assertEquals( 4L, _Java.invoke( inst, "doubleNumber", 2 ) );
        _Java.invoke( inst, "printDoubleNumber", 2 );        
        
        _BookService.getMethodsByName("findById")
            .get( 0 ) //there is only 1 method with this name
            .annotate("@Path(\"/{id}\")", "@GET" );
        
        System.out.println( _BookService );
        
        //lets build an outer
        System.out.println( _Outer );
        Class c = _Outer.loadClass();
        Class[] classes = c.getDeclaredClasses();
        assertEquals( 1, classes.length );
        assertEquals( "Inner", classes[ 0 ].getSimpleName() );
        
        
        Object inner = _Java.instance( classes[ 0 ] );
        _Java.invoke( inner, "runInner" );
        //Outer.instance( "Eric" );
        
    }    
}
