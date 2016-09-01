package varcode.java;

import junit.framework.TestCase;
import varcode.markup.repo.MarkupRepo.MarkupStream;

public class JavaMarkupRepoTest
	extends TestCase
{
	public void testTestClass()
	{
		MarkupStream ms1 = 
			JavaMarkupRepo.INSTANCE.markupStream( 
				this.getClass() );
		
		//verify I can read the source of a TestClass
		MarkupStream ms2 = 
			JavaMarkupRepo.INSTANCE.markupStream( 
				this.getClass().getCanonicalName() + ".java" );
		
		assertEquals( ms1.describe(), ms2.describe() );
		assertEquals( ms1.getMarkupId(), ms2.getMarkupId() );
		
	}
	
	public void testMainClass()
	{
		MarkupStream ms1 = 
			JavaMarkupRepo.INSTANCE.markupStream( 
				JavaNaming.class.getCanonicalName() + ".java" );
		
		MarkupStream ms2 = 
			JavaMarkupRepo.INSTANCE.markupStream( 
				JavaNaming.class );
		
		assertEquals( ms1.describe(), ms2.describe() );
		assertEquals( ms1.getMarkupId(), ms2.getMarkupId() );
	}
}
