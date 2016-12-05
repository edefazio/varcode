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
package varcode.java.metalang;

import varcode.Model.MetaLang;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaMetaLang 
    extends MetaLang
{    
    /**
     * A "Brute Force" replace for the content within the Java MetaLanguage Model
     * @param target the target string to look for within the model
     * @param replacement the replacement string 
     * @return the modified JavaMetaModel, (if it is mutable) or a modified clone
     */
    JavaMetaLang replace( String target, String replacement ); 
        
}
