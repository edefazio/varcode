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
package varcode.java.naming;

/**
 * Representation of a Class that have fully qualified names (They represent a 
 * specific form of a Java Class (a Class, Enum, Interface, AnnotationType) 
 * and could be:.
 * <UL>
 *   <LI>_class
 *   <LI>_enum
 *   <LI>AdHocClassFile
 *   <LI>AdHocJavaFile
 * </UL>
 * 
 * These disparate models all can return the qualified class that they represent
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface ClassNameQualified
{
    /** gets the "qualified" class name of the entity 
     * i.e. "java.lang.Map"
     * @return the qualified name of the class being represented
     */
    public String getQualifiedName();
    
}
