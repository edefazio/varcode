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
 * _draft annotation to define the signature Annotation applied to the signature
 * of class, enum, interface definitions, methods, constructors, fields...
 * contains the markup to be compiled to a {@link Template} for creating the
 * signature:
 * <PRE>
 * @sig("public class A")
 * class B
 * {
 * }
 * ------------
 * public class A
 * {
 *
 * }
 * </PRE> //a method with as yet undefined arguments sig("public static final
 * int getValue( {{+:{+type+} {+name+}+}} )") public static final int getValue(
 * int a, int b ) {
 *
 * }
 * --------------(with "type" and "name" == null) public static final int
 * getValue( ) {
 *
 * }
 * --------------(with ("type", "String", "name", "label" )) public static final
 * int getValue( String label ) {
 *
 * }
 *
 * @sig("public static {+returnType+} doThisMethod( {{+:{+type+} {+name+}, +}}
 * )") public static MyObj doThisMethod( String f1, int f2 ) { //... }
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
    ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.ANNOTATION_TYPE
})
public @interface sig
{
    /**
     * BindML markup used to create a Template for the signature
     */
    String value();
}
