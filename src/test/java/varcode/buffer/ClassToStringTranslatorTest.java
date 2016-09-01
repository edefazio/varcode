package varcode.buffer;

import junit.framework.TestCase;
import varcode.buffer.ClassToStringTranslate;

public class ClassToStringTranslatorTest
	extends TestCase 
{

	public void testTranslate()
    {
    	assertEquals( "String", 
    		ClassToStringTranslate.INSTANCE.translate( String.class ) );
    }
	
	
}
