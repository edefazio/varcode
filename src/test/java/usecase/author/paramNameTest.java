package usecase.author;

import junit.framework.TestCase;
import varcode.author.Author;
import varcode.context.VarContext;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric
 */
public class paramNameTest
    extends TestCase
{
    public void testReservedWordVarNames()
    {
        Template t = BindML.compile(
            "{+extends+} {+implements+} {+class+} {+import+} {+package+}" );
        String s = Author.toString( t, 
            VarContext.of(
                "extends", "ex", 
                "implements", "implements", 
                "class", "clazz",
                "import", "import", 
                "package", "pack" ) 
        );  
         
        System.out.println( s );
    }
}
