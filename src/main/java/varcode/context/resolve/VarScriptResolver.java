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
import varcode.context.Context;
import varcode.context.VarScript;

/**
 * Resolves VarScripts
 * 
 * Knows how to resolve a VarScript or static Java method based on the
 * scriptName and scriptInput String
 */
public interface VarScriptResolver
    extends Resolve
{
    public VarScript resolveScript(
        Context context, String scriptName, String scriptInput );

    public enum SmartScriptResolver
        implements VarScriptResolver
    {
        INSTANCE;

        private static VarScript findStaticMethod(
            Context context,
            Class<?> clazz,
            String methodName,
            String scriptInput )
        {
            try
            {
                Method m = Resolve.SilentReflection.tryAndGetMethod(
                    clazz, methodName, Context.class, String.class );
                if( m != null )
                {
                    return new StaticMethodVarScriptAdapter( m, context, scriptInput );
                }
                m = Resolve.SilentReflection.tryAndGetMethod(
                    clazz, methodName, Context.class );
                if( m != null )
                {
                    return new StaticMethodVarScriptAdapter( m, context );
                }
                m = Resolve.SilentReflection.tryAndGetMethod(
                    clazz, methodName, String.class );
                if( m != null )
                {
                    return new StaticMethodVarScriptAdapter( m, scriptInput );
                }

                m = Resolve.SilentReflection.tryAndGetMethod(
                    clazz, methodName, Object.class );
                if( m != null )
                {
                    return new StaticMethodVarScriptAdapter( m, scriptInput );
                }
                m = Resolve.SilentReflection.tryAndGetMethod( clazz, methodName );
                if( m != null )
                {
                    return new StaticMethodVarScriptAdapter( m );
                }
                return null;
            }
            catch( Exception e )
            {
                return null;
            }
        }

        /**
         * Return the Singleton INSTANCE VarScript
         */
        private static VarScript getSingletonField( Class<?> clazz )
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
            catch( NoSuchFieldException e )
            {
                return null;
            }
            catch( SecurityException e )
            {
                return null;
            }
            catch( IllegalArgumentException e )
            {
                return null;
            }
            catch( IllegalAccessException e )
            {
                return null;
            }
        }

        @Override
        public VarScript resolveScript(
            Context context, String scriptName, String scriptInput )
        {
            //if( LOG.isTraceEnabled() )
            //{
            //    LOG.trace( "   resolving script \"" + scriptName + "\"" );
            //}

            // 1) see if the script is loaded in the context
            String scriptLookupName = "$" + scriptName;
            //if( LOG.isTraceEnabled() )
            //{
            //    LOG.trace( "   1) checking context for \"" + scriptLookupName + "\"" );
            //}
            Object vs = context.get( scriptLookupName );
            if( vs != null )
            {
                if( vs instanceof VarScript )
                {
                    //LOG.trace( "   Found script \"" + scriptLookupName
                    //    + "\" in context " + vs );
                    return (VarScript)vs;
                }
            }

            //2) check if there is a Resolve Base Class Registered that has a 
            //method of this name
            Object resolveBaseClass = context.get( Resolve.BASECLASS_PROPERTY );

            if( resolveBaseClass != null )
            {
                //if( LOG.isTraceEnabled() )
                //{
                //    LOG.trace( "   2) checking Resolve.BaseClass \""
                //        + resolveBaseClass + "\" for static method \"" + scriptName + "\"" );
                // }
                VarScript resolveBaseStaticMethod = findStaticMethod( context,
                    (Class<?>)resolveBaseClass,
                    scriptName,
                    scriptInput );

                if( resolveBaseStaticMethod != null )
                {
                    //if( LOG.isTraceEnabled() )
                    //{
                    //    LOG.trace( "   Found VarScript as method \""
                    //        + scriptName + "\" from \"resolve.baseclass\" = \""
                    //        + resolveBaseClass + "\"" );
                    //}
                    return resolveBaseStaticMethod;
                }
            }
            else
            {
                //if( LOG.isTraceEnabled() )
                //{
                //    LOG.trace( "   2) no \"resolve.baseclass\" property set in VarContext" );
                //}
            }

            //I COULD have ScriptBindings where I "manually" Register/
            // assign scripts (i.e. Script)s to names
            // i.e. "java.util.UUID.randomUUID"();
            // IF the name contains a '.' (run (2) and (3) 
            int indexOfLastDot = scriptName.lastIndexOf( '.' );

            if( indexOfLastDot > 0 )
            {
                //if( LOG.isTraceEnabled() )
                //{
                //    LOG.trace( "   3) checking for fully qulified script name with class \"" + scriptName + ".class\"" );
                //}
                Class<?> clazz = Resolve.SilentReflection.getClassForName( scriptName );
                if( clazz != null && VarScript.class.isAssignableFrom( clazz ) )
                {
                    //LOG.trace( "      a) returning VarScript class " );
                    if( clazz.isEnum() )
                    {
                        //LOG.trace( "      b) returning VarScript Enum" );
                        return (VarScript)clazz.getEnumConstants()[ 0 ];
                    }
                    VarScript instanceField = getSingletonField( clazz );
                    if( instanceField != null )
                    {
                        //LOG.trace( "      c) resolved VarScript INSTANCE Field" );
                        return instanceField;
                    }
                }

                //maybe they passed in a class and method name
                String theMethodName = scriptName.substring(
                    indexOfLastDot + 1,
                    scriptName.length() );

                String theClassName = scriptName.substring( 0, indexOfLastDot );
                //if( LOG.isTraceEnabled() )
                //{
                //    LOG.trace( "   4) checking for class \"" + theClassName
                //        + "\" for static method \"" + theMethodName + "\"" );
                //}

                clazz = Resolve.SilentReflection.getClassForName( theClassName );

                if( clazz != null )
                {
                    //if( LOG.isTraceEnabled() )
                    //{
                    //    LOG.trace( "  resolved class \"" + clazz + "\"" );
                    //}
                    //does the class implement VarScript?
                    if( VarScript.class.isAssignableFrom( clazz ) )
                    {
                        //if( LOG.isTraceEnabled() )
                        // {
                        //    LOG.trace( "  class \"" + clazz + "\" is a VarScript" );
                        //}
                        if( clazz.isEnum() )
                        {
                            //if( LOG.isTraceEnabled() )
                            //{
                            //    LOG.trace( "  class \"" + clazz + "\" is a VarScript & an enum " );
                            //}
                            return (VarScript)clazz.getEnumConstants()[ 0 ];
                        }
                        Object singleton = getSingletonField( clazz );
                        //if( singleton != null && LOG.isTraceEnabled() )
                        //{
                        //    LOG.trace( "  returning INSTANCE field on \"" + clazz + "\" as VarScript" );
                        //}
                        try
                        {
                            //LOG.trace( "  trying to create (no-arg) instance of \"" + clazz + "\" as VarScript" );
                            return (VarScript)clazz.newInstance();
                        }
                        catch( InstantiationException e )
                        {
                            //LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz + "\" as VarScript", e );
                            return null;
                        }
                        catch( IllegalAccessException e )
                        {
                            //LOG.trace( "  failed creating a (no-arg) instance of \"" + clazz + "\" as VarScript", e );
                            return null;
                        }
                    }
                    else
                    {   //found a class, now find the method, (just chooses the first one by this name)
                        //if( LOG.isTraceEnabled() )
                        //{
                        //    LOG.trace( "  resolving static Method \"" + theMethodName
                        //        + "\" on class \"" + clazz + "\"" );
                        //}
                        return findStaticMethod(
                            context, clazz, theMethodName, scriptInput );
                    }
                }
            }
            //LOG.trace( "  no script bound for \"" + scriptName + "\" with input \"" + scriptInput + "\"" );
            return null;
        }
    }
}
