package varcode.doc.lib.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import varcode.doc.lib.text.StripMarks;
import varcode.doc.Dom;
import varcode.markup.codeml.CodeMLCompiler;
import junit.framework.TestCase;

public class StripMarksTest
	extends TestCase
{
	private static final Logger LOG = 
	    LoggerFactory.getLogger( StripMarksTest.class );
	
	public static final  String N = System.lineSeparator();
	
	public void testNoMarks()
	{
		String theString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Dom allMarks = 
			CodeMLCompiler.fromString( theString  );		
		LOG.info( StripMarks.stripFrom( allMarks ) );
		assertEquals( theString, StripMarks.stripFrom( allMarks ) );
	}
	
	
	public void testAllMarks()
	{
		String allMarksDoc =
		"{+$scriptName()+}" + N +// instanceof AddScriptResult );
        "{+$scriptName()*+}" + N +// instanceof AddScriptResult ); //REQUIRED
    	"/*{$$directiveName()$$}*/" + N +// instanceof TailorDirective );   
        "/*{$scriptName()$}*/" + N + // instanceof RunScript );
        "/*{$scriptName()*$}*/" + N + //RunScript ); //REQUIRED
        "{+varName+}" + N + //instanceof AddVar );
        "{+varName|default+}" +  N + //instanceof AddVar );
        "{+varName*+}" + N + //instanceof AddVar );
        "/*{+varName+}*/" + N + //instanceof AddVar );
        "/*{+varName|default+}*/" + N + //instanceof AddVar );
        "/*{+varName*+}*/" + N + //instanceof AddVar );
        "/*{+?varName:conditionalText+}*/" + N + //instanceof AddIfVar );
        "/*{+?varName=1:conditionalText+}*/" + N + //instanceof AddIfVar );
        "/*{+?varName==1:conditionalText+}*/" + N + //instanceof AddIfVar );
        "/*{+varName*/replace/*+}*/" + N + //instanceof ReplaceWithVar );
        "/*{+varName|*/replace default /*+}*/" + N + //instanceof ReplaceWithVar );
        "/*{+varName**/   replace   /*+}*/" + N + //instanceof ReplaceWithVar );
        "/*{+varName*/   replace   /*+}*/" + N + //instanceof ReplaceWithVar );
        "/*{+varName*/    \"replace\"   /*+}*/" + N + //instanceof ReplaceWithVar );
        "/*{#varName:value#}*/" + N + //instanceof DefineVar.InstanceVar );
        "/*{#varName:$scriptName()#}*/" + N + //instanceof DefineVarAsScriptResult.InstanceVar );
        "/*{##varName:$count(a)##}*/" + N + // instanceof DefineVarAsScriptResult.StaticVar );
        "/*{{+:({+areacode+})-{+first3+}-{+last4+}+}}*/" + N + //instanceof AddForm );
        "/*{_+:({+areacode+})-{+first3+}-{+last4+}+_}*/" + N + //instanceof AddForm );
        "/*{{+?showPhone:({+areacode+})-{+first3+}-{+last4+}+}}*/" + N + //instanceof AddFormIfVar );
        "/*{_+?showPhone:({+areacode*+})-{+first3*+}-{+last4*+}+_}*/" + N + //instanceof AddFormIfVar );
        
        "/*{{#phone:({+areacode+})-{+first3+}-{+last4+}#}}*/"  + N + // instanceof DefineVarAsForm.InstanceVar );        
        //"/*{{##phone:({+areacode*+})-{+first3*+}-{+last4*+}##}}*/"  + N + // instanceof DefineVarAsForm.StaticVar );
        
        "/*{_#phone:({+areacode+})-{+first3+}-{+last4+}#_}*/"  + N + //instanceof DefineVarAsForm.InstanceVar );        
        //"/*{_##phone:({+areacode*+})-{+first3*+}-{+last4*+}##_}*/"  + N + // instanceof DefineVarAsForm.StaticVar );
        
        "/*{##varName:value##}*/"  + N + // instanceof DefineVar.StaticVar );

        "/*{##name:$count(a)##}*/"  + N + // instanceof DefineVarAsScriptResult );
        
        "/*{-*/ cut this /*-}*/"  + N + // instanceof Cut );
        "/*{-?((fred==dead)):*/cut this comment/*-}*/"  + N + // instanceof CutIf );
        "/*{-  cut this comment -}*/"  + N + // instanceof CutComment );
        
        "/**{-  cut this Javadoc comment -}*/"  + N + // instanceof CutJavaDoc );
        
        "/**{@metadata:value@}*/"  + N + // instanceof SetMetadata );
        "/*{@metadata:value@}*/"; //instanceof SetMetadata );
		
		Dom allMarks = CodeMLCompiler.fromString( allMarksDoc );
		
		LOG.info( StripMarks.stripFrom( allMarks ) );
	}
}
