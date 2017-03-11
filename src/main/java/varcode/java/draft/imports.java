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
 * _draft Annotation to add / remove imports
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface imports
{
    /**
     * Remove all imports containing these "patterns"
     * Note: if you say @imports(remove="junit")
     * it will remove all imports with the String "junit" in them
     * i.e. it will remove both:
     * import junit.framework.Logger;
     * import junit.framework.LoggerFactory;
     */
    String[] remove() default {};

    /**
     * Add all imports expanded by these BinML markup strings
     */
    String[] add() default {};
}
