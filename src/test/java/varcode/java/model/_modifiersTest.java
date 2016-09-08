package varcode.java.model;


import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class _modifiersTest
	extends TestCase
{
  
	public void testModifiers()
	{
		int mod = Modifier.ABSTRACT | Modifier.FINAL | Modifier.NATIVE | 
				Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC | Modifier.STRICT | 
				Modifier.SYNCHRONIZED | Modifier.TRANSIENT | Modifier.VOLATILE;
		assertTrue( Modifier.isPublic( mod ) );
		assertTrue( Modifier.isPrivate( mod ) );
		assertTrue( Modifier.isTransient( mod ) );
		assertTrue( Modifier.isVolatile( mod ) );
		
		System.out.println( Integer.toBinaryString( mod ) );
		
		assertFalse( _modifiers.isValid( mod ) );
		
		assertTrue( _modifiers.isValid( Modifier.FINAL | Modifier.NATIVE | Modifier.STATIC | Modifier.PRIVATE ) );
		assertFalse( _modifiers.isValid( 1 << 13 ) ); //bit out of range
		assertFalse( _modifiers.isValid( Modifier.PRIVATE | Modifier.PUBLIC ) ); //two accesses
		assertFalse( _modifiers.isValid( Modifier.ABSTRACT | Modifier.FINAL ) );
		//assertTrue( new _modifiers().setPrivate().containsAny( access.PRIVATE ))
	}
	
	/*
	public void testMods()
	{
		_mods ms = new _mods();
		String[] keywords = ms.KEYWORD_TO_BIT_MAP.keySet().toArray(new String[0]);
		
		
		
		System.out.println( Integer.toBinaryString( ms.mods ) );
			ms.set( 
			"abstract", "final", "native", "public", "private", "protected", "static", "strictfp", "synchronized",
			"transient", "volatile", "interface" );
		System.out.println( ms );
	}
	*/
	
	public void testMods()
	{
		_modifiers m = _modifiers.of("private");
		assertEquals( "private ", m.author( ) );
		
	}
	
	public static void main(String[] args)
	{
		System.out.println( new _modifiers().setPrivate() );
		System.out.println( new _modifiers().setFinal().setSynchronized().author() );
		System.out.println( new _modifiers().setPrivate().setFinal().setSynchronized().author() );
	}
	
	

	
}
