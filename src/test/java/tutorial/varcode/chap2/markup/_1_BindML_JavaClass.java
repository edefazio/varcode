package tutorial.varcode.chap2.markup;

import junit.framework.TestCase;
import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.java.Java;
import varcode.java.adhoc.AdHocClassLoader;
import varcode.java.adhoc.AdHocJavaFile;
import varcode.java.adhoc.Workspace;
import varcode.markup.bindml.BindML;

/**
 *
 * @author Eric DeFazio
 */
public class _1_BindML_JavaClass
    extends TestCase
{
    public static String N = System.lineSeparator();
    
    public static final Dom SimpleBindClass = 
        BindML.compile( 
            "public class {+className*+}" + N + 
            "{" + N +
            "    {{+:public {+fieldType+} {+fieldName+};" + N +
            "    +}}" + N +        
            "    public static void main(String[] args)" + N +
            "    {" + N +
            "         System.out.println(\"{+message+}\");" + N +
            "    }" + N +
            "}" );        
                    
    
    public void testBindMLJavaFile()
    {
        AdHocJavaFile javaFile = Java.author( 
            "MyClass", SimpleBindClass, "className", "MyClass" );
        
        System.out.println( javaFile );
        
        AdHocClassLoader adHoc = Workspace.compileNow( javaFile );
        
    }
    
}
