package varcode.doc.translate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;
import varcode.doc.translate.TranslateBuffer;
import varcode.doc.translate.Translator;
import varcode.context.VarBindings;
import varcode.context.eval.Eval_JavaScript;

public class TranslateBufferTest
	extends TestCase
{
	
	public void testTranslateOnly()
	{
		TranslateBuffer tb = new TranslateBuffer();
		
		assertEquals(
			"boolean, char, int, float, byte, short, long, double",	
			tb.translate( (Object)new Object[]
			    { boolean.class, char.class, int.class,float.class, byte.class, short.class, long.class, double.class} )
			);
		//verify nothing was added to the buffer
		assertEquals( "", tb.toString() );
	}
	
    public void testNullTranslateBuffer()
    {
        TranslateBuffer tb = new TranslateBuffer( ); 
        assertEquals( "", tb.toString() );
        tb.append( null );
        assertEquals( "", tb.toString() );
        
        tb.append( "a" );
        assertEquals( "a", tb.toString() );
        tb.append( null );
        assertEquals( "a", tb.toString() );
        
        tb.append( "b" );
        assertEquals( "ab", tb.toString() );        
    } 
    
	public void testTBuffer()
	{
	    TranslateBuffer fb = new TranslateBuffer();
	    assertEquals( "", fb.toString() ); 
	    fb.append( "a" );
	    assertEquals( "a", fb.toString() );
	    fb.append( "b" );
	    assertEquals( "ab", fb.toString() );
	        
	    fb.append( null ); 
	    assertEquals( "ab", fb.toString() );        
	}
	  
	public enum MapTranslator
		implements Translator
	{
		INSTANCE;
		
		public Object translate( Object source ) 
		{
			if( source instanceof Map )
			{
				return "MAP";
			}
			return source;
		}		
	}
	
    /*
	public void testTranslateAddTranslator()
	{
		TranslateBuffer tb = new TranslateBuffer();
		Map<String,Integer>map = new HashMap<String,Integer>();
		map.put( "A", 1 );
		String defaultTranslate = tb.translate ( map );
		
		assertEquals( "{A=1}", defaultTranslate );
		
		tb.addTranslator( MapTranslator.INSTANCE );
		String mapTranslate = tb.translate ( map );
		
		assertEquals( "MAP", mapTranslate );		
	}
	*/
	public void testArrayClasses()
	{
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( new Object[]
			{ boolean.class, char.class, int.class,float.class, byte.class, short.class, long.class, double.class} );		
		assertEquals( 
			"boolean, char, int, float, byte, short, long, double", sb.toString() );		
	}
	
	public void testPrimitiveArrayClasses()
	{
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( new Class<?>[]
			{ boolean.class, char.class, int.class,float.class, byte.class, short.class, long.class, double.class} );		
		assertEquals( 
			"boolean, char, int, float, byte, short, long, double", sb.toString() );		
	}
	
	public void testJavaLangPackage()
	{
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( new Class<?>[] { 
			Boolean.class, Character.class, Integer.class, Float.class, Byte.class, Short.class, Long.class, Double.class, String.class });
		
		assertEquals( 
			"Boolean, Character, Integer, Float, Byte, Short, Long, Double, String", sb.toString() );
	}
	
	public void testClassNonJavaLang() 
	{
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( new Class<?>[] { Map.class, UUID.class} );
		assertEquals( "java.util.Map, java.util.UUID", sb.toString() );
	}
	
	public void testArray()
	{
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( new int[0] );
		assertEquals("", sb.toString() );
		sb.clear();
		
		sb.append( new int[] {1,2,3,4,5,6} );
		assertEquals( "1, 2, 3, 4, 5, 6", sb.toString() );
		sb.clear();
		assertEquals( "", sb.toString() );
		
		sb.append( new char[] {'a','b','c'} );
		assertEquals( "a, b, c", sb.toString() );
		
	}
	
	public void testCollection()
	{
		TranslateBuffer sb = new TranslateBuffer();
		List<Integer>iList = new ArrayList<Integer>();
		
		sb.append( iList );
		
		assertEquals( "", sb.toString() );
		sb.clear();
		
		iList.add( 1 );
		sb.append( iList );
		assertEquals( "1", sb.toString() );
		sb.clear();
		
		iList.add( 2 );
		sb.append( iList );
		assertEquals( "1, 2", sb.toString() );		
	}
	
	public void testExpression()
	{
		Object jsArray = 
			Eval_JavaScript.INSTANCE.evaluate(new VarBindings(), "A=[1,2,3,4,5]");
		TranslateBuffer sb = new TranslateBuffer();
		sb.append( jsArray );
		assertEquals( "1, 2, 3, 4, 5", sb.toString() );
	}
}
