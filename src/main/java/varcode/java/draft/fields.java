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
 * _draft Annotation describing one or more fields
 *
 * @fields( {"public int a;", "public String name=\"Eric\";"} )  <PRE>
     * public class C
 * {
 *
 * }
 * -----------------(Expands to)
 *
 * public class C
 * {
 *    public int a;
 *    public String name = \"Eric\";
 * }
 * </PRE>
 *
 * @fields( {"public int {+fieldName+};"} )  <PRE>
     * public class MyPrototype
 * {
 * }
 * --------(Expands with ("fieldName", "x"))
 * public class MyPrototype
 * {
 *    public int x;
 * }
 * --------(Expands with ("fieldName", new String[]{"x", "y", "z"}))
 * public class MyPrototype
 * {
 *     public int x;
 *     public int y;
 *     public int z;
 * }
 * </PRE>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface fields
{
    String[] value();
}
