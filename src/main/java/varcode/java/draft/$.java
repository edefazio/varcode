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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * _draft Annotation to Replace a key with a parameter
 * <PRE>
 * @$({"100", "nameCount"})
 * public final int names = 100;
 * ---------- produces the BindML:
 * public final int names = {+nameCount+};
 *
 * ----with ( "nameCount", 5 )
 * public final int names = 5;
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface $
{
    String[] value();
}
