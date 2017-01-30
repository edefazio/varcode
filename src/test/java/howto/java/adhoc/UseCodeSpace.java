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
package howto.java.adhoc;

import java.util.Date;
import junit.framework.TestCase;
import varcode.java.adhoc.CodeSpace;
import varcode.java.model._class;
import varcode.java.model.auto._auto;

/**
 *
 * @author Eric
 */
public class UseCodeSpace
    extends TestCase
{
    public static void main(String[] args)
    {
        CodeSpace cs = CodeSpace.of( "System.out.println( \"Hi\");" );
        cs.eval( );
        
        CodeSpace.of( 
            _class.of("public class A").packageName( "cs" )
                .method( "public static String createId()" ,
                    "return java.util.UUID.randomUUID().toString();" ), 
            "String s = A.createId();", 
            "System.out.println( s );").eval();
        
        CodeSpace.of( 
            _auto.macro.IMMUTABLE_DATA_CLASS.apply( 
                _class.of( "package io", "A" )
                    .imports( Date.class )
                .fields( "final int a", 
                    "final String b", 
                    "final double c",
                    "final Date d" ) ),            
            "Date now = new Date();",
            "// build an instance with constructor",
            "A inst = new A(10, \"bee\", 3.14d, now);",
            
            "// build an instance with builder",
            "ABuilder builder = A.builder();",
            "A builtInst = builder.a(10).b(\"bee\").c(3.14d).d(now).build();",
            "assert(inst.equals(builtInst));",
            "assert(\"a\".equals( inst ) );")
            .eval();
    }
     
}
