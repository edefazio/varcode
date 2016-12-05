/*
 * Copyright 2016 Eric.
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
package tutorial.chap5.macro;

import junit.framework.TestCase;
import varcode.java.metalang._class;
import varcode.java.metalang.macro._autoJavadocMethod;

/**
 * {@link _class}
 * 
 * @author Eric
 * @since 0.1
 * @version 0.1
 */
public class Use_AutoJavadocMethods 
    extends TestCase
{
    // method.annDeprecate();
    // method.annOverride();
    // method.annGenerated();
    // method.annSuppressWarnings(...);
    
    //method.docLink(...) {@link varcode.
    //method.docSince()
    //method.docVersion()
    //method.docAuthor()
    //method.docParam( String paramName, String...documentation)
    //method.docReturn( String...documentation )
    //method.docSeeLocalMethod( methodName ) //@see #customizeProxyFactory
    //method.docLink
    //method.docThrows(
    //method.doc
    //method.seeDoc( ... )
    //method.paramDoc( String paramName, String...documentation)
    //method.returnDoc( String..documentation )
    //method.throwsDoc( String exception, String comment)
    public void testNoMethods()
    {
        _class _c = _class.of( "ex.varcode", "public class MyClass" );
        String before = _c.author();
        
        _autoJavadocMethod.ofClass( _c );
        String after = _c.author();
        
        assertEquals( before, after );   
    }
        
    //verify that if a method already has a Javadoc, does not try to
    // add to javadoc
    public void testExistingJavadoc()
    {
        _class _c = _class.of( "ex.varcode", "public class MyClass" )
            .method( "public String getName( int count ) throws IOException" );
        
        _c.getMethodNamed( "getName" ).javadoc( "Existing javadoc" );
        String before = _c.author();
        
        _autoJavadocMethod.ofClass( _c );
        String after = _c.author();
        
        assertEquals( before, after );        
    }
    

    
    public void testAutoJavadocMethods()
    {
        _class _c = _class.of( "ex.varcode", "public class MyClass" )
            .method( "public String getName( int count ) throws IOException" );
        
        String before = _c.author();
        _autoJavadocMethod.ofClass( _c );
        String after = _c.author();
        
        assertTrue( !before.equals( after ) );
        
        System.out.println( _c );
    }
}
