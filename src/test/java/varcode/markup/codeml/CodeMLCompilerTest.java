package varcode.markup.codeml;

import varcode.doc.Dom;
import varcode.markup.codeml.CodeMLCompiler;
import junit.framework.TestCase;

public class CodeMLCompilerTest 
	extends TestCase
{
	public static final  String N = System.lineSeparator();
	
	public void testAllMarks()
	{
		String allMarksDoc =
		"{+$scriptName()+}" + N +// instanceof AddScriptResult );
        "{+$scriptName()*+}" + N +// instanceof AddScriptResult ); //REQUIRED
        "/*{(( 3 + 5 ))}*/" + N +// instanceof EvalExpression
        "{+((3 + 5))+}" + N +  //AddExpressionResult
        "/*{+((3+5))*/REPLACE/*+}*/" + N + //ReplaceWitExpressionResult
    	"/*{$$directiveName()$$}*/" + N +// instanceof TailorDirective );   
        "/*{$$directiveName()*$$}*/" + N +// instanceof TailorDirective ); //REQUIRED
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
        "/*{#varName:((a + b))#}*/" + N +
    	"/*{#varName=((a + b))#}*/" + N +
    	"/*{##a:1##}*/" + N +
    	"/*{##varName:((a))##}*/" + N +
    	"/*{##varName=((a))##}*/" + N +    	
        "/*{{#phone:({+areacode+})-{+first3+}-{+last4+}#}}*/"  + N + // instanceof DefineVarAsForm.InstanceVar );        
        //"/*{{##phone:({+areacode*+})-{+first3*+}-{+last4*+}##}}*/"  + N + // instanceof DefineVarAsForm.StaticVar );
        
        "/*{_#phone:({+areacode+})-{+first3+}-{+last4+}#_}*/"  + N + //instanceof DefineVarAsForm.InstanceVar );        
        //"/*{_##phone:({+areacode*+})-{+first3*+}-{+last4*+}##_}*/"  + N + // instanceof DefineVarAsForm.StaticVar );
        
        "/*{##varName:value##}*/"  + N + // instanceof DefineVar.StaticVar );
        
        "/*{##name:$count(varName)##}*/"  + N + // instanceof DefineVarAsScriptResult );
        
        "/*{-*/ cut this /*-}*/"  + N + // instanceof Cut );
        "/*{-?((fred==dead)):*/cut this comment/*-}*/"  + N + // instanceof CutIfExpression );
        "/*{-  cut this comment -}*/"  + N + // instanceof CutComment );
        
        "/**{-  cut this Javadoc comment -}*/"  + N + // instanceof CutJavaDoc );
        
        "/**{@metadata:value@}*/"  + N + // instanceof SetMetadata );
        "/*{@metadata:value@}*/"; //instanceof SetMetadata );
		
		Dom allMarks = CodeMLCompiler.fromString( allMarksDoc );		
		assertTrue( allMarks != null );
	}
}
