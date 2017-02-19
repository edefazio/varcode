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
package varcode.java.macro;

/**
 * Annotations  applied to Java Source code that allows the tailoring of
 * Source code.
 * 
 * Define "How" to customizations are to be made to an it
 * (Class, Enum, Interface, AnnotationType), including the addition of dynamic
 * fields, methods, static blocks, etc.
 * 
 * What happens: 
 * <OL>READ IN the .java source of a Java Class into a prototype _class
 * <LI>WE "interpret" the JavaSource Annotations which apply "mutations" to the 
 * prototype (NOTE: we DO NOT mutate the Prototype itslef, but rather build a "clone"
 * of the prototype and copy over or mutate the 
 * 
 * @see _classTailor
 */
public enum JavaSource
{
    ;
    
    
}
