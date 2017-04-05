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
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import varcode.java.Java;
import varcode.java.model._enum;

public class _draftEnumTest
    extends TestCase
{
    //the @imports() annotation allows the annotations to be add or removed
    // explicitly or by patterns
    public void testExpandImports()
    {   //load this class as a _macro, 
        _enum _c = draftEnum.of( _enumDraft.class ).draft();

        assertTrue( _c.getImports().contains( "java.util.Map" ) );
        assertTrue( _c.getImports().contains( "java.util.UUID" ) );

        //"author" the imports ot a Struing and verify we DONT have any 
        // imports that are
        String authored = _c.getImports().author();

        //verify there are no varcode.java imports
        assertTrue( !authored.contains( "varcode.java" ) );
        assertTrue( !authored.contains( "junit" ) );

        //add imports 
        _c = draftEnum.of( _enumDraft.class ).draft( "addImports", "java.util.HashSet" );
        assertTrue( _c.getImports().contains( "java.util.HashSet" ) );

        //add mutliple imports by a single parameter
        _c = draftEnum.of( _enumDraft.class ).draft( "addImports",
            new Object[]
            {
                "java.util.Calendar", "java.util.Date"
            } );

        assertTrue( _c.getImports().contains( "java.util.Calendar" ) );
        assertTrue( _c.getImports().contains( "java.util.Date" ) );
    }

    /**
     * verify that we can "match" {+Name+} with the "name" value and apply
     * FirstCaps on it
     */
    public void testNameLower()
    {
        _enum _c
            = draftEnum.of( _enumDraft.ClassName.class ).draft( "name", "eric" );

        assertEquals( "Eric", _c.getName() );
    }

    public void testPackageStatic()
    {
        _enum _c = draftEnum.of( _enumDraft.EPackageStatic.class ).draft();
        assertEquals( "ex.mypackage", _c.getPackageName() );
    }

    public void testPackageExpand()
    {
        _enum _c = draftEnum.of( _enumDraft.ExpandPackage.class ).draft( "subpkg", "mysub" );
        assertEquals( "ex.mypackage.mysub", _c.getPackageName() );
    }

    public void testExpandStaticBlock()
    {
        _enum _c = draftEnum.of( _enumDraft.ExpandStaticBlock.class ).draft();

        assertTrue( _c.getStaticBlock().author().contains( "out.println" ) );
    }

    public void testExpandClassAnnotations()
    {
        _enum _c = draftEnum.of( _enumDraft.ExpandClassAnnotation.class ).draft();

        assertEquals( 1, _c.getAnnotations().get( "Drafted" ).size() );

        _c = draftEnum.of( _enumDraft.ExpandClassAnnotation.class ).draft( "classAnnotations", "@MyAnnotation" );

        assertEquals( 1, _c.getAnnotations().get( "Drafted" ).size() );
        assertEquals( 1, _c.getAnnotations().get( "MyAnnotation" ).size() );
    }

    public void testCopyClassAnnotations()
    {
        _enum _c = draftEnum.of( _enumDraft.CopyClassAnnotations.class ).draft();
        assertTrue( _c.getAnnotation( Deprecated.class ) != null );
        assertTrue( _c.getAnnotation( _enumDraft.Draft.class ) != null );
        //assertNull( _c.getAnnotation( sig.class ) );
    }

    public void testCopyStaticBlock()
    {
        _enum _c = draftEnum.of( _enumDraft.CopyStaticBlock.class ).draft();
        assertTrue( _c.getStaticBlock().author().contains( "In static Block" ) );
    }

    public void testClassSig()
    {
        _enum _c = draftEnum.of( _enumDraft.ClassSig.class ).draft( "Name", "MyClass" );
        assertEquals( "MyClass", _c.getName() );
    }

    public void testClassFields()
    {
        _enum _c = draftEnum.of( _enumDraft.ClassFields.class )
            .draft( "type", int.class, "name", "blah" );
        System.out.println( _c );
        assertEquals( "int", _c.getField( "blah" ).getType() );

        //create multiple fields
        _c = draftEnum.of( _enumDraft.ClassFields.class )
            .draft( "type", new Class[]
            {
                int.class, float.class
        },
                "name", new String[]
                {
                    "x", "y"
            } );

        assertEquals( "int", _c.getField( "x" ).getType() );
        assertEquals( "float", _c.getField( "y" ).getType() );
    }

    public void testClassFieldM()
    {
        _enum _c = draftEnum.of( _enumDraft.ClassFieldsMultiple.class )
            .draft( "name", "TheName" );

        assertEquals( "int", _c.getField( "TheNameDim" ).getType() );
        assertEquals( "int", _c.getField( "id" ).getType() );
    }

    public void testSimple()
    {
        _enum _q
            = draftEnum.of( _enumDraft.AField.class ).draft( "name", "theName" );
        assertEquals( "AField", _q.getName() );
        assertEquals( "int", _q.getField( "theName" ).getType() );
    }

    public void testAField()
    {
        draftEnum _cm = draftEnum.of( Java._enumFrom( _enumDraft.AField.class ) );
        _enum _c = _cm.draft( "name", "y" );
        assertEquals( "int", _c.getField( "y" ).getType() );

        //expand a single field macro to mutliple fields (3) fields (x, y, and z)
        _c = _cm.draft( "name", new String[]
        {
            "x", "y", "z"
        } );
        assertEquals( "int", _c.getField( "x" ).getType() );
        assertEquals( "int", _c.getField( "y" ).getType() );
        assertEquals( "int", _c.getField( "z" ).getType() );
    }

    public void testMethodCopy()
    {
        _enum _c = draftEnum.of( _enumDraft.CopyMethod.class ).draft();
        assertNotNull( _c.getMethod( "sourceMethod" ) );
    }

    public void testMethodParameterize()
    {
        _enum _c = draftEnum.of( _enumDraft.ParameterizeMethod.class )
            .draft( "pname", "varName" );
        assertTrue( _c.getMethod( "aMethod" ).getSignature().author().contains( "varName" ) );
        assertTrue( _c.getMethod( "aMethod" ).getBody().author().contains( "varName" ) );
    }

    public void testMethodSigAndBody()
    {
        _enum _c = draftEnum.of( _enumDraft.MethodSigAndBody.class )
            .draft();
        assertTrue( _c.getMethod( "itBurns" ).getBody().author().contains( "HI" ) );
    }

    public void testMethodSigOnly()
    {
        _enum _c = draftEnum.of( _enumDraft.MethodSigOnly.class )
            .draft();
        assertNotNull( _c.getMethod( "itBurns" ) );
    }

    public void testMethodBodyOnly()
    {
        _enum _c = draftEnum.of( _enumDraft.MethodBodyOnly.class )
            .draft();
        assertTrue( _c.getMethod( "original" ).getBody().author().contains( " HI " ) );
    }

    public void testMethodForm()
    {
        _enum _c = draftEnum.of( _enumDraft.MethodForm.class )
            .draft( "name", "fieldName" );

        System.out.println( _c );
        assertTrue( _c.getMethod( "toString" ).getBody().author()
            .contains( "sb.append( fieldName );" ) );

        // verify that I 
        Java.call( _c.loadConstant( "INSTANCE" ), "toString" );

        // now create a 
        _c = draftEnum.of( _enumDraft.MethodForm.class )
            .draft( "name", new String[]
            {
                "theFirstField", "theSecondField"
        } );

        //make sure we created (2) fields
        assertTrue( _c.getField( "theFirstField" ).getType().equals( "String" ) );
        assertTrue( _c.getField( "theSecondField" ).getType().equals( "String" ) );

        //make sure the toString method prints out "theFirstField" and "theSecondField"
        // the defaults for both fields
        String res = (String)Java.call( _c.loadConstant( "INSTANCE" ), "toString" );
        assertTrue( res.contains( "theFirstField" ) );
        assertTrue( res.contains( "theSecondField" ) );
    }
}
