/*
 * Copyright 2017 Eric.
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
package varcode.java.draft;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.draft.DraftAction.*;

/**
 * Rough Draft
 * -> changes
 * 
 * <LI>
 _workspaceMacro wsm = DraftAction.match( 
  "all classes in package...", Type.Class, Name.contains, Name.statsWith )
    .removeField( "LOG" )
    .removeImport( "
 .remove
 
 _draftClass.of( MyClass.class )
     .removeField( "fieldName" )
     .removeField( _f )
     .addField("public static final MASK = 
 
 
 This is something else
 So I could pass in something that would: 
 1) Match a particular method MatchMethod
 2) Match a particular sequence of code MatchCodeSequence
 3) 
 * 
 * @author Eric
 */
@imports( remove={"varcode.java", "junit"}, add={"java.util.Map", "java.util.UUID", "{+addImports+}"} )
public class _draftClassTest
    extends TestCase
{   
    
    //the @imports() annotation allows the annotations to be add or removed
    // explicitly or by patterns
    public void testExpandImports()
    {   //load this class as a DraftAction, 
        _class _c = _draftClass.of(_draftClassTest.class ).draft( ); 
        
        assertTrue( _c.getImports().contains( "java.util.Map" ) );
        assertTrue( _c.getImports().contains( "java.util.UUID" ) );
        
        //"author" the imports ot a Struing and verify we DONT have any 
        // imports that are
        String authored = _c.getImports().author();
        
        //verify there are no varcode.java imports
        assertTrue( !authored.contains( "varcode.java" ) );
        assertTrue( !authored.contains( "junit" ) );
        
        //add imports 
        _c = _draftClass.of(_draftClassTest.class ).draft( "addImports", "java.util.HashSet" ); 
        assertTrue( _c.getImports().contains( "java.util.HashSet" ) );
        
        //add mutliple imports by a single parameter
        _c = _draftClass.of(_draftClassTest.class ).draft( "addImports", 
            new Object[] {"java.util.Calendar", "java.util.Date"} ); 
        
        assertTrue( _c.getImports().contains( "java.util.Calendar" ) );
        assertTrue( _c.getImports().contains( "java.util.Date" ) );        
    }
        
    
    //TODO parameterize the class
    @sig("public class {+Name+}")
    public static class ClassName
    {
        
    }
    
    /** verify that we can "match" {+Name+} with the "name" value and 
     * apply FirstCaps on it
     */
    public void testNameLower()
    {
        _class _c = 
            _draftClass.of( ClassName.class ).draft( "name", "eric" );
        
        assertEquals( "Eric", _c.getName( ) );
    }
    
    
    @packageName( "ex.mypackage" )
    public static class EPackageStatic { }
    
    public void testPackageStatic()
    {
        _class _c = _draftClass.of( EPackageStatic.class ).draft(  );
        assertEquals( "ex.mypackage", _c.getPackageName() );
    }
    
    @packageName( "ex.mypackage.{+subpkg+}" )
    public static class ExpandPackage{}
    
    public void testPackageExpand()
    {
        _class _c = _draftClass.of(ExpandPackage.class ).draft("subpkg", "mysub" );
        assertEquals( "ex.mypackage.mysub", _c.getPackageName() );
    }
     
    
    //singe line static block
    @staticBlock( "System.out.println( \"Hello from Static Block\");" )
    public static class ExpandStaticBlock{}
    
    public void testExpandStaticBlock()
    {
        _class _c = _draftClass.of( ExpandStaticBlock.class ).draft();
        
        assertTrue( _c.getStaticBlock().author().contains( "out.println" ) );
    }
    
    
    @Deprecated
    @annotations(remove={"Deprecated"}, add={"@Drafted", "{+classAnnotations+}"} )
    public static class ExpandClassAnnotation{ }
    
    public void testExpandClassAnnotations()
    {
        _class _c = _draftClass.of( ExpandClassAnnotation.class ).draft( );
        
        assertEquals( 1,  _c.getAnnotations().get( "Drafted" ).size() );
        
        _c = _draftClass.of( ExpandClassAnnotation.class ).draft( "classAnnotations", "@MyAnnotation" );
        
        assertEquals( 1,  _c.getAnnotations().get( "Drafted" ).size() );
        assertEquals( 1,  _c.getAnnotations().get( "MyAnnotation" ).size() );        
    }
    
    @interface Draft{}
        
    @sig("public static class TheClass")
    @Deprecated
    @Draft
    public static class CopyClassAnnotations{}
    
    public void testCopyClassAnnotations()
    {
        _class _c = _draftClass.of( CopyClassAnnotations.class ).draft( );
        assertTrue( _c.getAnnotation( Deprecated.class ) != null );
        assertTrue( _c.getAnnotation( Draft.class ) != null );
        //assertNull( _c.getAnnotation( sig.class ) );
    }
    public static class CopyStaticBlock
    {
        static
        {
            System.out.println( "In static Block" );
        }
    }
    
    public void testCopyStaticBlock()
    {
        _class _c = _draftClass.of(  CopyStaticBlock.class ).draft( );
        assertTrue( _c.getStaticBlock().author().contains( "In static Block" ) );
    }
    
    @sig("public class {+Name+}")
    public static class ClassSig{ }
    
    public void testClassSig()
    {
        _class _c = _draftClass.of( ClassSig.class ).draft( "Name", "MyClass" );
        assertEquals( "MyClass", _c.getName() ); 
    }
    
    @fields("public static {+type+} {+name+};")
    public static class ClassFields{}
    
    public void testClassFields()
    {
        _class _c = _draftClass.of( ClassFields.class )
            .draft( "type", int.class, "name", "blah" );
        System.out.println( _c );
        assertEquals( "int", _c.getField("blah").getType() );
        
        //create multiple fields
        _c = _draftClass.of( ClassFields.class )
            .draft( "type", new Class[]{int.class, float.class}, 
                     "name", new String[]{"x", "y"} );
        
        assertEquals( "int", _c.getField("x").getType() );
        assertEquals( "float", _c.getField("y").getType() );
    }
    
    @fields({"public int {+name+}Dim;", "private static String id;"})    
    public static class ClassFieldsMultiple
    { }
    
    public void testClassFieldM()
    {
        _class _c = _draftClass.of( ClassFieldsMultiple.class )
            .draft( "name", "TheName" );
        
        assertEquals( "int",_c.getField( "TheNameDim" ).getType() );
        assertEquals( "String",_c.getField( "id" ).getType() );
    }
    
    public static class AField
    {
        @$({"x", "name"})
        public int x;
    }
    
    public void testSimple()
    {
        _class _q = 
            _draftClass.of(AField.class ).draft( "name", "theName" );
        assertEquals( "AField", _q.getName() );
        assertEquals( "int", _q.getField( "theName" ).getType() );
    }
    
    public void testAField()
    {
        _draftClass _cm = _draftClass.of(Java._classFrom(AField.class ) );
        _class _c = _cm.draft( "name", "y" );
        assertEquals( "int", _c.getField("y").getType() );
        
        //expand a single field macro to mutliple fields (3) fields (x, y, and z)
        _c = _cm.draft( "name", new String[]{"x", "y", "z"} );
        assertEquals( "int", _c.getField("x").getType() );
        assertEquals( "int", _c.getField("y").getType() );
        assertEquals( "int", _c.getField("z").getType() );        
    }   
    
    public static class CopyMethod
    {
        public static String sourceMethod()
        {
            return "STRING";
        }
    }
    
    public void testMethodCopy()
    {
        _class _c = _draftClass.of(CopyMethod.class ).draft(  );
        assertNotNull( _c.getMethod( "sourceMethod" ) );
    }
    
    public static class ParameterizeMethod
    {
        @$({"name", "pname"})
        public static String aMethod( String name )
        {
            return "Hello "+ name;
        }
    }
    
    public void testMethodParameterize()
    {
        _class _c = _draftClass.of( ParameterizeMethod.class )
            .draft( "pname", "varName" );
        assertTrue( _c.getMethod( "aMethod" ).getSignature().author().contains( "varName") );
        assertTrue( _c.getMethod( "aMethod" ).getBody().author().contains( "varName") );        
    }
    
    public static class MethodSigAndBody
    {
        @sig( "public static final void itBurns()" )
        @body("System.out.println( \" HI \" );" )  //here body is single line  
        void original()
        {}
    }
    
    public void testMethodSigAndBody()
    {
        _class _c = _draftClass.of( MethodSigAndBody.class )
            .draft( );
        assertTrue( _c.getMethod( "itBurns" ).getBody().author().contains( "HI" ) );
    }
    
    /**
     * A Class that uses the body tag with multiple lines
     */
    public static class MethodBodyMultiline
    {
        @body( { "System.out.println(\"Hey\");",   //<-- will add line breaks 
            "System.out.println( \"line 2\");" } ) //between each array element
        void methodBody()
        {
            
        }
    }
    public void testBodyMultiLine()
    {
        _class _c = _draftClass.of( MethodBodyMultiline.class ).draft();
        
        System.out.println( _c.getMethod( "methodBody" ) );
        
        assertTrue(_c.getMethod( "methodBody" )
            .getBody().author().contains( "line 2" ) );
    }
    
    
    public static class MethodSigOnly
    {
        @sig( "public static final void itBurns()" )
        void original()
        {}
    }
    
    public void testMethodSigOnly()
    {
        _class _c = _draftClass.of( MethodSigOnly.class )
            .draft( );
        assertNotNull( _c.getMethod( "itBurns" ) );
    }
    
    public static class MethodBodyOnly
    {
        @body("System.out.println( \" HI \" );" )    
        void original()
        {}
    }
    
    public void testMethodBodyOnly()
    {
        _class _c = _draftClass.of( MethodBodyOnly.class )
            .draft( );
        assertTrue( _c.getMethod( "original" ).getBody().author().contains(" HI ") );
    }
    
    @fields( "public String {+name+} = {+$quote(name)+};" )
    public class MethodForm
    {        
        @form( {"sb.append( {+name+} );", "sb.append(System.lineSeparator());"} )
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append( this.getClass().getName() );            
            form:
            sb.append( "Each of the fields will be printed here, this will be replaced" );            
            /*}*/            
            return sb.toString();
        }
    }
    
    public void testMethodForm()
    {
        _class _c = _draftClass.of( MethodForm.class )
            .draft( "name", "fieldName" );
        
        System.out.println( _c );
        assertTrue( _c.getMethod( "toString" ).getBody().author()
            .contains( "sb.append( fieldName );" ) );
        
        // verify that I 
        Java.call( _c.instance( ), "toString" );
        
        // now create a 
        _c = _draftClass.of( MethodForm.class )
            .draft( "name", new String[]{"theFirstField", "theSecondField"});
        
        //make sure we created (2) fields
        assertTrue( _c.getField("theFirstField" ).getType().equals( "String" ) );
        assertTrue( _c.getField("theSecondField" ).getType().equals( "String" ) );
        
        //make sure the toString method prints out "theFirstField" and "theSecondField"
        // the defaults for both fields
        String res = (String) Java.call( _c.instance(), "toString" );
        assertTrue( res.contains( "theFirstField" ) );
        assertTrue( res.contains( "theSecondField" ) );
        
    }
    
}
