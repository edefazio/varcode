package varcode.markup.bindml;

import varcode.context.VarBindException.NullResult;
import varcode.context.VarContext;
import varcode.context.VarBindException.NullVar;
import varcode.doc.Compose;
import varcode.markup.bindml.BindML;
import junit.framework.TestCase;

/**
 * Test that Required Works as Expected for BindML
 * 
 * @author eric
 */
public class TestRequiredBindML
	extends TestCase
{
	/** 
	 * Verify that an attempt to tailor the markup will fail
	 * with a precise RequiredButNull Exception
	 * 
	 * @param mark the mark to Compile and Tailor
	 */
	private static void verifyThrowsVarRequired( String mark )
	{
		try
		{
			Compose.asString( BindML.compile( mark ), VarContext.of() );
			fail( "expected RequiredButNull" );
		}
		catch( NullVar rbn )
		{	
			//expected
			//System.out.println(rbn );
		}
	}

	private static void verifyThrowsResultRequired( String mark )
	{
		try
		{
			Compose.asString( BindML.compile( mark ), VarContext.of() );
			fail( "expected RequiredButNull" );
		}
		catch( NullResult rbn )
		{	
			//expected
			//System.out.println(rbn );
		}
	}
	
	public void testTags()
	{
		verifyThrowsVarRequired( "{+requiredButNull*+}" ); //here the var is not bound
		verifyThrowsVarRequired( "{+$script()*+}" ); //here the script is not bound
		verifyThrowsResultRequired( "{+$count(notFound)*+}" ); //the var is not bound
		
		verifyThrowsVarRequired( "{{+:{+fieldType*+} {+fieldName+}+}}" );
		verifyThrowsVarRequired( "{{+:{+fieldType+} {+fieldName*+}+}}" );
		verifyThrowsResultRequired( "{{+:{+fieldType+} {+fieldName+}*+}}" );
		
		verifyThrowsVarRequired( "{_+:{+fieldType*+} {+fieldName+}+_}" );
		verifyThrowsVarRequired( "{_+:{+fieldType+} {+fieldName*+}+_}" );
		
		assertEquals("",Compose.asString( BindML.compile( "{_+:{+fieldType+} {+fieldName+}*+_}" ), VarContext.of() ) );
		
		//verifyThrowsResult(  ); //get rid of this logic
		
		/*
		 * <LI><CODE>"{- some text -}"</CODE>  {@code Cut}
		 * <LI><CODE>"{#a=1#}"</CODE>  {@code DefineVar.DynamicVar}
		 * <LI><CODE>"{#a:$count(a)#}"</CODE>  {@code DefineVarAsScriptResult.DynamicVar}
		 * <LI><CODE>"{#$removeEmptyLines()#}"</CODE>  {@code TailorDirective}
		 * <LI><CODE>"{##a=1##}"</CODE>  {@code DefineVar.StaticVar}
		 * <LI><CODE>"{##a:$count(blah)##}"</CODE> {@code DefineVarAsScriptResult.StaticVar}
		 * <LI><CODE>"{{#assign:{+fieldName+} = {+fieldValue+};#}}"</CODE> {@code DefineVarAsForm.DynamicVar}  
		 * <LI><CODE>"{_#assign:{+fieldName+} = {+fieldValue+};#_}"</CODE> {@code DefineVarAsForm.DynamicVar} 
		 * <LI><CODE>"{{##assign:{+fieldName+} = {+fieldValue+};##}}"</CODE> {@code DefineVarAsForm.StaticVar}
		 * <LI><CODE>"{_##assign:{+fieldName+} = {+fieldValue+};##_}"</CODE> {@code DefineVarAsForm.StaticVar} 
		 * <LI><CODE>"{{##className:IntFormOf{+count+}##}}"</CODE> {@code DefineVarAsForm.StaticVar}        	
		 * <LI><CODE>"{_##className:IntFormOf{+count+}##_}"</CODE> {@code DefineVarAsForm.StaticVar}
		 * <LI><CODE>"{$print(*)$}"</CODE>  {@code RunScript}		
		 * <LI><CODE>"{@meta:data@}"</CODE> {@code SetMetadata}
		 */
	}
}
