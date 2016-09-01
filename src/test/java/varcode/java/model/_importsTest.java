package varcode.java.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;

public class _importsTest
	extends TestCase
{
	public void testImports()
	{
		Map<UUID, Date>ud = new HashMap<UUID, Date>();
		
		_imports i = new _imports();
		i.addImport( String.class );
		assertEquals( 0, i.count() );		
		i.addImport( int.class );
		assertEquals( 0, i.count() );
		i.addImport( String[].class );
		assertEquals( 0, i.count() );
		i.addImport( int[].class );
		assertEquals( 0, i.count() );
		i.addImport( Map.class );
		assertEquals( 1, i.count() );
		i.addImport( "java.util.*" );
		assertEquals( 2, i.count() );
		
		i.addImports( Float.class, "java.util.*", "varcode.io.BlahDeBlah", UUID.class, Date.class );
		assertEquals( 5, i.count() );
		
		i.addImport( ud.getClass() );
		assertEquals( 6, i.count() );
		
		i.addStaticImport( UUID.class );
		assertEquals( 7, i.count() );
		
		//System.out.println( i.toCode() );
	}
	
	public void testRender()
	{
		_imports i = new _imports();
		i.addImport( String.class );
		assertEquals("", i.toString() );
		i.addStaticImport(Long.class);
		assertEquals("", i.toString() );
		
		i.addStaticImport(Map.class);
		assertEquals("import static java.util.Map.*;" + System.lineSeparator(), i.toString() );
	}
}
