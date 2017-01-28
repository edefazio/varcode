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
package varcode.context.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interfaces and Implementations for resolving references to entities by name
 * <UL>
 *  <LI>vars (key value pairs)  (for marks like "{+PREFIX+}")
 *     need to Resolve the value of a var named "PREFIX"
 *  <LI>methods / {@code VarScript}s, (for marks like "{+$indent(a)+}")
 *     need to Resolve the method/script named "indent(...)"
 *  <LI>{@code DocDirective}s (for marks like "{removeBlankLines()$$}")
 *     need to Resolve the {@code DocDirective} (pre or post processor) 
 *     named "directive()"
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Resolve 
{	
    /** 
     * name of a Property in the {@code VarContext} that specifies a Class that 
     * can define: 
     * <UL>
     *   <LI>vars (static variables linked/used in Compose(ing) documents
     *   <LI>methods (static methods used in Compose(ing) documents
     *   <LI>directives (static classes used in Compos(ing) documents
     * </UL>
     * for example:
     * 
     * public class TheResolveClass 
     * {
     *     public static final String someVar = "Eric";
     * 
     *     public static final String salutation( VarContext context, String varName )
     *     {
     *         //this doesnt "really" make sense, we are just getting the
     *         // value of someVar, ("Eric") here, but I'm just trying to illustrate 
     *         // how to Resolve uses the "resolve.baseclass" property to 
     *         // resolve BOTH methods and variable values
     *         // i.e. for : "{+salutation(someVar)+}" 
     *         String theValue = (String)context.getValue( varName );         
     *         return "Dear Mr. " + theValue.trim();
     *     }
     * 
     *     public static void main( String[] args )
     *     {
     *         Dom d = BindML.compile( "{+salutation(someVar)+}" );
     *         
     *         //here we set the resolve.baseclass, which signifies
     *         // that I can uses static variables, methods and class instances
     *         // that are resident on the TheResolveClass.class 
     *         // when processing the mark "{+salutation(someVar)+}" 
     *         //    I need to resolve a method/script named : "salutation"
     *         //    I need to resolve a var named "someVar"     
     *         // ...both are found as static fields /methods on TheResolveClass 
     *         VarContext vc = VarContext.of( 
     *             "resolve.baseclass", TheResolveClass.class );
     * 
     *         System.out.println( Compose.toString( d, vc ) );
     *     }
     * }
     */
    public static final String BASECLASS_PROPERTY = "resolve.baseclass";
    
    /**
     * Does reflective Lookups, and Swallows Exceptions (May log) 
     * Silently returns
     * <A HREF="https://www.youtube.com/watch?v=jhat-xUQ6dw">
     * A Little silent Lucidity</A>
     */
    public static class SilentReflection
    {
        public static Method tryAndGetMethod( 
            Class<?> clazz, String name, Class<?>...params )
        {
            try 
            {
                return clazz.getMethod( name, params );
            } 
            catch( Exception e ) 
            {
                return null;
            }
        }
	
        public static Class<?> getClassForName( String className )
        {
            try 
            {
                Class<?>c = Class.forName( className ); 
			
                return c;
            } 
            catch( ClassNotFoundException e ) 
            {
                return null;
            }
        }
        
        /**
         * Gets the value of a static field from a Field
         * @param field the field definition
         * @return the value of the static field
         */
        public static Object getStaticFieldValue( Field field )
        {
            try 
            {
                return field.get( null );
            } 
            catch( Exception e ) 
            {
                return null;
            }	    
        }
    }
    
   
}
