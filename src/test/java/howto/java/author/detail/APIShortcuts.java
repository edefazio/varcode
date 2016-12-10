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
package howto.java.author.detail;

import junit.framework.TestCase;
import varcode.java.lang._annotations._annotation;
import varcode.java.lang._class;
import varcode.java.lang._constructors._constructor;
import varcode.java.lang._fields._field;
import varcode.java.lang._javadoc;
import varcode.java.lang._methods._method;
import varcode.java.lang._package;

/**
 * varcode API wants to make developers productive, while also
 * providing flexible and consistent API to create readable code.
 * 
 * Ideally, we should be able to define (_class, _enum, and _interface) 
 * abstractions in a single "fluent-style" statement (to avoid having to 
 * unnecessarily create static initializer blocks)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class APIShortcuts
    extends TestCase
{
    public static _class _a = _class.of(
        "package howto.java.author.detail;",      //set the package
        "/* API Shortcuts to create a _class */", // add _javadoc 
        "@Deprecated",                            // add a annotation  
        "@CustomAnnotation{a=100}",               // add another annotation
        
        "public class APIShortcut " +             //set the class signature      
            "extends BaseClass implements Serializable, AnotherInterface",
        _field.of( "/* a number */",  //add a field w/ javadoc
            "@Deprecated",    
            "public final int count;" ),
        _constructor.of( 
            "/* multi-line constructor comment " + System.lineSeparator() +
            "@param count the number" + System.lineSeparator() +        
            "@throws IOException"         
          + "*/",    
            "@Deprecated",    
            "public APIShortcut( int count ) throws IOException",
            "this.count = count;" ),
        _method.of(  
            "/* method comment */", 
            "@Deprecated",    
            "public int getCount() throws MyException",
            "return this.count;" )    
        );
    
    public void testIt()
    {
        
    }
    /*
    public _class _c = _class.of(
        _javadoc.of( "this is the class comment" ),
        _package.of( "howto.java.author.detail" ),
        _annotation.of( "@Deprecated" ),
        "public class DeclareShort extends BaseClass implements Serializable",
        _field.of( "public final int count;"),
        _constructor.of( "public DeclareShort( int count )",
            "this.count = count;" ),
        _method.of(  "public int getCount()",
            "return this.count;" )    
        );
    */
    
    
}
