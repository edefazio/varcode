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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * draft Annotation manipulating the contents of a static Block
 * 
 * <PRE>
 * a constant static block:
 * @staticBlock({"System.out.println( \"Hi\" );}) //constant static block
 * 
 * a variable static block:
 * @staticBlock("{+init*+}"); //content contained in var "init"
 * 
 * remove the static block:
 * @staticBlock(remove=true)
 * </PRE>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface staticBlock
{
    /** Defines the (Form) contents to use win the Static block */ 
    String[] value() default {};
    
    /** Remove the static block entirely (if true) */
    boolean remove() default false; 
}
