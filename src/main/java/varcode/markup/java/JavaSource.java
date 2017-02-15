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
package varcode.markup.java;

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
    
    /** Markup to be processed to generate the name of the package
     * i.e.
     * "io.typeframe.{+project+}"
     */
    public @interface packageName
    {
        String value();
    }
    
    /** Markup to generate the static block
     * 
     */
    public @interface staticBlock
    {
        String value();
    }
    
    /**
     * Specification for multiple fields on a model 
     * (_class, _enum, _interface, _annotationType)
     * i.e.
     * 
     * --- The Prototype
     * <CODE><PRE>
     * @add(fields="public int {+fieldName+};")
     * public class MyPrototype
     * {
     * 
     * }
     * </PRE></CODE>
     * --- The Specialization<BR>
     * IF we provide the context:
     * 
     * <CODE>VarContext.of("fieldName", new String[]{"x", "y"});</CODE><BR><BR>
     * 
     * for the specialization...we would produce the Tailored:
     * <PRE>
     * public class MyPrototype
     * {
     *    public int x;
     *    public int y;
     * }
     * </PRE>
     * 
     */
    public @interface fields
    {
        String value();
    }

    public @interface add
    {
        String[] imports() default{};
        String[] fields() default {};
        String[] annotations() default {};
    }
    
    public @interface annotation 
    {
        String[] values();
    }
    
    public @interface imports
    {
        String[] retain() default 
        {
            "*"
        };

        String[] remove();

        String[] add() default 
        {
        };
    }

    /**
     * Annotation applied to the signature of class, enum, interface
     * definitions, methods, constructors, fields... contains the markup to be
     * compiled to a {@link Template} for creating the signature:
     *
     * @sig("public static {+returnType+} doThisMethod( {{+:{+type+} {+name+},
     * +}} )") public static MyObj doThisMethod( String f1, int f2 ) { //... }
     */
    public @interface sig
    {
        /**
         * the BindML markup used to create a Template for the signature
         */
        String value();
    }

    /**
     * Applied to methods, and constructors, defines the template for the body
     * text
     *
     * @body("return {{+:{+FIELDNAME+}.storeState( {+name+} ) | +}};" ) public
     * long store( Boolean value1, Boolean value2 ) { return FIELD1.storeState(
     * value1 ) | FIELD2.storeState( value2 ); }
     */
    public @interface body
    {
        /**
         * the BindML markup used to create a Template for the body
         */
        String value();
    }

    /**
     * The component marked with this annotation will be removed
     * when tailoring
     */
    public @interface remove
    {
    }
}
