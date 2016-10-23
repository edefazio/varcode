package tutorial.varcode.chap2.markup;

import junit.framework.TestCase;
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
    
    public static final Dom ClassDom = 
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
        AdHocJavaFile javaFile = Java.author( "MyClass", ClassDom, 
            "className", "MyClass" );
        
        System.out.println( javaFile );
        
        AdHocClassLoader adHoc = Workspace.compileNow( javaFile );

        javaFile = Java.author( "MyClass", ClassDom, 
            "className", "MyClass", 
            "fieldType", int.class,
            "fieldName", "count" );        
        
        //compile a new class with a single field
        adHoc = Workspace.compileNow( javaFile );
        
        javaFile = Java.author( "MyClass", ClassDom, 
            "className", "MyClass", 
            "fieldType", new Object[]{int.class, String.class},
            "fieldName", new String[]{"count","name"} );

        // a class with (2) fields "int count;" and "String name"
        System.out.println( javaFile );
        
        adHoc = Workspace.compileNow( javaFile );
    }
    
    //concepts
    // 1) BindML is a simple markup language for binding data into documents
    // 2) BindML will compile a Dom (Document Object Model) from a String 
    //    "template" containing text and Marks
    // 3) "{+<name>+}" Marks will bind a variable with a <name> into the 
    //    Document we bind 
    // 4) "{{+: ... +}} Marks bind repeating patterns within the document.
    //    "{{+:{+type+} {+name+};
    //     +}}"
    
    //    ...if "type" -> "int", and "name" -> "count"
    //    ...will print: 
    //    "int count;" 
    
    //    ...if "type" -> ["int","String"];and name -> {"count", "name"};   
    //    ...will print:
    //     "int count;
    //     String name;"
    //    
    
}
