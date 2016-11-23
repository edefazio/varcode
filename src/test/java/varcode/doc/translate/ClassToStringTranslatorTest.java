package varcode.doc.translate;

import junit.framework.TestCase;

public class ClassToStringTranslatorTest
	extends TestCase 
{

	public void testTranslate()
    {
    	assertEquals( "java.lang.String", 
    		ClassToStringTranslate.INSTANCE.translate( String.class ) );
    }
	
	
}
