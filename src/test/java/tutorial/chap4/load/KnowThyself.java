/*
 * Copyright 2016 Eric DeFazio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tutorial.chap4.load;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import junit.framework.TestCase;
import varcode.java.lang._class;
import varcode.java.load._java;
import varcode.load.SourceLoader.SourceStream;

/**
 *
 * @author Eric DeFazio
 */
public class KnowThyself
    extends TestCase
{
    public void testLoadSource()
    {        
        //Load the Source that makes up this class at runtime
        SourceStream ss = _java.sourceFrom( KnowThyself.class );
        System.out.println( ss.asString() );
    }
    
    
    public void testLoadAST()
    {
        //Load the AST Model that represents this class at runtime
        // the ROOT contains package, and import information AS well as
        // the KnowThyself class Declaration
        CompilationUnit astRoot = _java.astFrom( KnowThyself.class );
        System.out.println( astRoot );
    }
    
    public void testLoadASTTypeDeclaration()
    {
        //Load the AST model of the TypeDeclaration ( this is an AST node
        // that is a CHILD of the astRoot CompilationUnit )
        TypeDeclaration astClassDef = _java.astDeclarationFrom( KnowThyself.class );        
        System.out.println( astClassDef );
    }
    
    public void testLoad_LangModel()
    {   
        //Load a _LangModel based on the code (from the AST)
        _class _c = _java._classFrom( KnowThyself.class );
        assertEquals( "KnowThyself", _c.getName() );
        assertEquals( "tutorial.chap4.load", _c.getClassPackage().getName() );
        assertNotNull( _c.getMethodNamed( "testLoad_LangModel" ) );
    }
}
