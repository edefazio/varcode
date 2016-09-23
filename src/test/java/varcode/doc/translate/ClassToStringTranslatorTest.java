package varcode.doc.translate;

import junit.framework.TestCase;
import varcode.doc.translate.ClassToStringTranslate;

public class ClassToStringTranslatorTest
	extends TestCase 
{

	public void testTranslate()
    {
    	assertEquals( "String", 
    		ClassToStringTranslate.INSTANCE.translate( String.class ) );
    }
	
	
}
