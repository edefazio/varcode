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
package varcode.java.load;

import varcode.load.SourceLoader;

/**
 * A Source Loader that can resolve the source code (.java) given a Java Class
 * 
 * @author M. Eric DeFazio 
 */
public interface SourceForJavaClassLoader 
    extends SourceLoader 
{
    /** 
     * load the sourceStream from the runtime Java Class 
     * @param clazz runtime Java class to load .java source
     * @return the SourceStream for this class
     */
    public SourceStream sourceStream( Class<?> clazz );
}
