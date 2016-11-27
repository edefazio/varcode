package varcode.source;

import varcode.java.load.BaseSourceLoader;
import junit.framework.TestCase;
import varcode.java.JavaNaming;
import varcode.load.SourceLoader.SourceStream;

public class JavaMarkupRepoTest
	extends TestCase
{
	public void testTestClass()
	{
		SourceStream ms1 = 
			BaseSourceLoader.INSTANCE.sourceStream( 
				this.getClass() );
		
		//verify I can read the source of a TestClass
		SourceStream ms2 = 
			BaseSourceLoader.INSTANCE.sourceStream( 
				this.getClass().getCanonicalName() + ".java" );
		
		assertEquals( ms1.describe(), ms2.describe() );
		assertEquals( ms1.getSourceId(), ms2.getSourceId() );
		
	}
	
	public void testMainClass()
	{
		SourceStream ms1 = 
			BaseSourceLoader.INSTANCE.sourceStream( 
				JavaNaming.class.getCanonicalName() + ".java" );
		
		SourceStream ms2 = 
			BaseSourceLoader.INSTANCE.sourceStream( 
				JavaNaming.class );
		
		assertEquals( ms1.describe(), ms2.describe() );
		assertEquals( ms1.getSourceId(), ms2.getSourceId() );
	}
}
