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
package varcode_v_codegen;

import varcode.java.Java;
import varcode.java._Java;
import varcode.java.metalang._class;

/**
 *
 * @author Eric
 */
public class varcode_example 
{
    public static void main( String[] args )
    {
        _class _c = 
            _class.of(
                "org.example", "public class BasicExample" )
                .field( "public static final String BASIC_MESSAGE = \"Hello, world!\";" )
                .mainMethod( "System.out.println( BASIC_MESSAGE );" )
                .javadoc( "Write some documentation here" );
                
        System.out.println( _c.author() ); //write the code to System out
        Object instance = _c.instance( ); //create instance
        
        //TODO add an invokeMain( Class or Object );
        
        _Java.invokeMain( instance );
        //Java.invoke(instance, methodName, args)
        
        
        
        /*
          This is the Speedment Codegen example from :
        <A HREF="https://github.com/speedment/speedment/tree/master/common-parent/codegen">
        Speedment Codegen</A> 
        
            System.out.println(new JavaGenerator().on(
            File.of("org/example/BasicExample.java")
                .add(Class.of("BasicExample")
                .add(GENERATED)
                .public_()
                .add(
                    Field.of("BASIC_MESSAGE", String.class)
                    .public_().final_().static_()
                    .set(Value.ofText("Hello, world!"))
                )
                .add(
                    Method.of("main", void.class)
                    .set(Javadoc.of(
                        "This is a vary basic example of ",
                        "the capabilities of the Code Generator."
                    ))
                    .public_().static_()
                    .add(Field.of("params", String[].class))
                    .add("System.out.println(BASIC_MESSAGE);")
                )
            ).call(new AutoJavadoc<>())
            ).get()
        );
        */
    }
}
