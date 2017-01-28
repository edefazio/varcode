/*
 * Copyright 2017 M. Eric DeFazio.
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
package varcode.java;

import varcode.load.Source.SourceLoader;
import varcode.load.Source.SourceStream;

/**
 * Implementation of a {@code SourceLoader} specifically for trying to
 * load Java code from within an Editor's runtime.
 * 
 * The General idea is that, sometimes developers will be calling this from 
 * within an editor on their local classes, so it will check:
 * 
 * In addition to looking for source code (as a Resource) on the classpath
 * (i.e. in some XXX-src.jar) this abstraction will look into 
 * 
 * ${user.dir} ( the value in the System.properties set by the editor )
 * and certain subdirectories:
 * <PRE>
 * ${user.dir}/varcode
 * ${user.dir}/src
 * ${user.dir}/src/java
 * ${user.dir}/src/resources
 * ${user.dir}/src/main/java
 * ${user.dir}/test
 * ${user.dir}/src/test/java
 * ${user.dir}/src/test/resources
 * </PRE>
 * This is not appropriate in all editor environments, but one can easily build
 * a custom abstraction in this vein to load source files. 
 * 
 * <BLOCKQUOTE>
 * THE MOST SIMPLE DIRECT SOLUTION IS TO INCLUDE THE SOURCE FILES AS A JAR 
 * OR DIRECTORY IN THE RUNTIME CLASSPATH.
 * </BLOCKQUOTE>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaSourceLoader
    extends SourceLoader 
{    
    /** 
     * load the sourceStream from the runtime Java Class 
     * @param clazz runtime Java class to load .java source
     * @return the SourceStream for this class
     */
    public SourceStream sourceStream( Class<?> clazz );
    

}
