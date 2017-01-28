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
import java.lang.reflect.Modifier;
import varcode.author.StaticMethodPreProcessAdapter;
import varcode.context.Context;
import varcode.context.Directive;
import varcode.context.VarScript;

/**
 * Resolves a {@code DocDirective} instance from a name. For instance for a mark
 * like:
 * <PRE>
 * "{$$removeEmptyLines()$$}"
 * </PRE> "{$$varcode.author.lib.PrefixWithLineNumber.
 *
 * tries to resolve a {@link Directive} by name "removeEmptyLines()"
 *
 */
public interface DirectiveResolver
    extends Resolve
{
    public Directive resolveDirective(
        Context context, String directiveName );

    /**
     * Resolves a {@link Directive} {@link PreProcessor} or
     * {@link PostProcessor} instance from a name. For instance for a mark like:
     * <UL>
     * <LI><PRE>"{$$removeEmptyLines()$$}"</PRE> ...which finds the Directive
     * that is bound to the var "removeEmptyLines" within the {@link Context}
     * <LI>"{$$varcode.author.lib.PrefixWithLineNumber$$}" ...which loads the
     * enum INSTANCE Class of "varcode.author.lib.PrefixWithLineNumber" and uses
     * it
     * </UL>
     *
     * tries to resolve a {@code Directive} by name "removeEmptyLines()"
     *
     * @author M. Eric DeFazio eric@varcode.io
     */
    public enum SmartDirectiveResolver
        implements DirectiveResolver
    {
        INSTANCE;

        @Override
        public Directive resolveDirective( Context context, String directiveName )
        {
            // check the Context
            String directiveLookupName = "$" + directiveName;
            Object directive = //MED CHANGED FROM GET
                context.resolveVar( directiveLookupName );

            if( directive != null && directive instanceof Directive )
            {
                return (Directive)directive;
            }

            /**
             * Fully qualifying a directive as a static method call?
             */
            int indexOfLastDot = directiveName.lastIndexOf( '.' );

            if( indexOfLastDot > 0 )
            {
                String theMethodName = directiveName.substring(
                    indexOfLastDot + 1,
                    directiveName.length() );

                String theClassName = directiveName.substring( 0, indexOfLastDot );

                Class<?> clazz = Resolve.SilentReflection.getClassForName(
                    theClassName );

                if( clazz != null )
                {
                    //does the class implement Directive?
                    if( Directive.class.isAssignableFrom( clazz ) )
                    {
                        if( clazz.isEnum() )
                        {
                            return (Directive)clazz.getEnumConstants()[ 0 ];
                        }
                        Object singleton = getSingletonINSTANCEField( clazz );
                        try
                        {
                            return (Directive)clazz.newInstance();
                        }
                        catch( Exception e )
                        {
                            return null;
                        }
                    }
                    else
                    {   //found a class, now find the method, 
                        //(just chooses the first one by this name)                   
                        return findStaticMethodAsDirective(
                            context, clazz, theMethodName );
                    }
                }
            }
            return null;
        }

        /**
         * Return the Singleton INSTANCE VarScript
         */
        private static VarScript getSingletonINSTANCEField( Class<?> clazz )
        {
            try
            {
                Field field = clazz.getField( "INSTANCE" );
                if( Modifier.isStatic( field.getModifiers() ) )
                {
                    return (VarScript)field.get( null );
                }
                return null;
            }
            catch( Exception e )
            {
                return null;
            }
        }

        /**
         *
         * @param context
         * @param clazz
         * @param methodName
         * @return
         */
        private static Directive findStaticMethodAsDirective(
            Context context, Class<?> clazz, String methodName )
        {
            try
            {
                Method m = Resolve.SilentReflection.tryAndGetMethod(
                    clazz, methodName, Context.class );
                if( m != null )
                {
                    return new StaticMethodPreProcessAdapter(
                        m, context );
                }

                m = Resolve.SilentReflection.tryAndGetMethod( clazz, methodName );
                if( m != null )
                {
                    return new StaticMethodPreProcessAdapter( m );
                }
                return null;
            }
            catch( Exception e )
            {
                return null;
            }
        }
    }
}
