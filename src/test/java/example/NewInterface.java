/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.ast.FormatJavaCode_AllmanScanStyle;
import varcode.java.ast.JavaAst;
import varcode.java.load._JavaLoad;
import varcode.java.load._JavaAstPort;
import varcode.java.model._interface;
import varcode.java.model._methods._method;

/**
 *
 * @author Eric
 */
public interface NewInterface
    
{
    
    public default String getId( String prefix )
    {
        System.out.println( prefix );
        return prefix;
    }
    
    public static void main( String[] args )
        throws ParseException
    {
        
        _method m = _method.of( 
            "public default void doThis(String s)", 
            "System.out.printnl( \"Hi\");" );
        
        //System.out.println( m );
        
        CompilationUnit cu = Java.astFrom( NewInterface.class );
        
        _interface _i = _JavaLoad._interfaceFrom( NewInterface.class );
        
        //System.out.println( _i );
        
        TypeDeclaration td = JavaAst.findRootTypeDeclaration( cu );
        
        //System.out.println( cu );
        //MethodDeclaration[] mds = JavaAst.findAllMethods( td );
        
        MethodDeclaration md = JavaAst.astMethodFrom( 
            "public default String getId( String prefix) {"+ System.lineSeparator() + 
            "return \"Hi\"; }" ); 
        
        System.out.println( md.isDefault() );
        _method _m = _JavaAstPort._methodFromAST( md, new FormatJavaCode_AllmanScanStyle() );
        
        System.out.println( _m );
        
        
    }
}
