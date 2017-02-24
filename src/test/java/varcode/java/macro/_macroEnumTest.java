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
package varcode.java.macro;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import varcode.java.Java;
import varcode.java.model._enum;

public class _macroEnumTest
    extends TestCase
{
        //the @imports() annotation allows the annotations to be add or removed
        // explicitly or by patterns
        public void testExpandImports()
        {   //load this class as a _macro, 
            _enum _c = _macroEnum.of(_macroEnumDraft.class ).expand();

            assertTrue( _c.getImports().contains( "java.util.Map" ) );
            assertTrue( _c.getImports().contains( "java.util.UUID" ) );

            //"author" the imports ot a Struing and verify we DONT have any 
            // imports that are
            String authored = _c.getImports().author();

            //verify there are no varcode.java imports
            assertTrue( !authored.contains( "varcode.java" ) );
            assertTrue( !authored.contains( "junit" ) );

            //add imports 
            _c = _macroEnum.of(_macroEnumDraft.class ).expand( "addImports", "java.util.HashSet" );
            assertTrue( _c.getImports().contains( "java.util.HashSet" ) );

            //add mutliple imports by a single parameter
            _c = _macroEnum.of(_macroEnumDraft.class ).expand( "addImports",
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
                = _macroEnum.of( _macroEnumDraft.ClassName.class ).expand( "name", "eric" );

            assertEquals( "Eric", _c.getName() );
        }

        public void testPackageStatic()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.EPackageStatic.class ).expand();
            assertEquals( "ex.mypackage", _c.getPackageName() );
        }

        public void testPackageExpand()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ExpandPackage.class ).expand( "subpkg", "mysub" );
            assertEquals( "ex.mypackage.mysub", _c.getPackageName() );
        }

        public void testExpandStaticBlock()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ExpandStaticBlock.class ).expand();

            assertTrue( _c.getStaticBlock().author().contains( "out.println" ) );
        }

        public void testExpandClassAnnotations()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ExpandClassAnnotation.class ).expand();

            assertEquals( 1, _c.getAnnotations().get( "Drafted" ).size() );

            _c = _macroEnum.of( _macroEnumDraft.ExpandClassAnnotation.class ).expand( "classAnnotations", "@MyAnnotation" );

            assertEquals( 1, _c.getAnnotations().get( "Drafted" ).size() );
            assertEquals( 1, _c.getAnnotations().get( "MyAnnotation" ).size() );
        }

        public void testCopyClassAnnotations()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.CopyClassAnnotations.class ).expand();
            assertTrue( _c.getAnnotation( Deprecated.class ) != null );
            assertTrue( _c.getAnnotation( _macroEnumDraft.Draft.class ) != null );
            //assertNull( _c.getAnnotation( sig.class ) );
        }

        public void testCopyStaticBlock()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.CopyStaticBlock.class ).expand();
            assertTrue( _c.getStaticBlock().author().contains( "In static Block" ) );
        }

        public void testClassSig()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ClassSig.class ).expand( "Name", "MyClass" );
            assertEquals( "MyClass", _c.getName() );
        }

        public void testClassFields()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ClassFields.class )
                .expand( "type", int.class, "name", "blah" );
            System.out.println( _c );
            assertEquals( "int", _c.getField( "blah" ).getType() );

            //create multiple fields
            _c = _macroEnum.of( _macroEnumDraft.ClassFields.class )
                .expand( "type", new Class[]
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
            _enum _c = _macroEnum.of( _macroEnumDraft.ClassFieldsMultiple.class )
                .expand( "name", "TheName" );

            assertEquals( "int", _c.getField( "TheNameDim" ).getType() );
            assertEquals( "int", _c.getField( "id" ).getType() );
        }

        public void testSimple()
        {
            _enum _q
                = _macroEnum.of( _macroEnumDraft.AField.class ).expand( "name", "theName" );
            assertEquals( "AField", _q.getName() );
            assertEquals( "int", _q.getField( "theName" ).getType() );
        }

        public void testAField()
        {
            _macroEnum _cm = _macroEnum.of( Java._enumFrom( _macroEnumDraft.AField.class ) );
            _enum _c = _cm.expand( "name", "y" );
            assertEquals( "int", _c.getField( "y" ).getType() );

            //expand a single field macro to mutliple fields (3) fields (x, y, and z)
            _c = _cm.expand( "name", new String[]
            {
                "x", "y", "z"
            } );
            assertEquals( "int", _c.getField( "x" ).getType() );
            assertEquals( "int", _c.getField( "y" ).getType() );
            assertEquals( "int", _c.getField( "z" ).getType() );
        }

        public void testMethodCopy()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.CopyMethod.class ).expand();
            assertNotNull( _c.getMethod( "sourceMethod" ) );
        }

        public void testMethodParameterize()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.ParameterizeMethod.class )
                .expand( "pname", "varName" );
            assertTrue( _c.getMethod( "aMethod" ).getSignature().author().contains( "varName" ) );
            assertTrue( _c.getMethod( "aMethod" ).getBody().author().contains( "varName" ) );
        }

        public void testMethodSigAndBody()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.MethodSigAndBody.class )
                .expand();
            assertTrue( _c.getMethod( "itBurns" ).getBody().author().contains( "HI" ) );
        }

        public void testMethodSigOnly()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.MethodSigOnly.class )
                .expand();
            assertNotNull( _c.getMethod( "itBurns" ) );
        }

        public void testMethodBodyOnly()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.MethodBodyOnly.class )
                .expand();
            assertTrue( _c.getMethod( "original" ).getBody().author().contains( " HI " ) );
        }

        public void testMethodForm()
        {
            _enum _c = _macroEnum.of( _macroEnumDraft.MethodForm.class )
                .expand( "name", "fieldName" );

            System.out.println( _c );
            assertTrue( _c.getMethod( "toString" ).getBody().author()
                .contains( "sb.append( fieldName );" ) );

            // verify that I 
            Java.call( _c.loadConstant( "INSTANCE" ), "toString" );

            // now create a 
            _c = _macroEnum.of( _macroEnumDraft.MethodForm.class )
                .expand( "name", new String[]
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
