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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaReflection
{
    /**
     * Gets the "top level" class (the one having a file name) that contains the
     * source/ declaration of the <CODE>clazz</CODE>
     *
     * @param clazz the class to retrieve
     * @return the top Level Class for this class
     */
    public static Class getTopLevelClass( Class clazz )
    {
        if( clazz.getDeclaringClass() == null )
        {
            return clazz;
        }
        return getTopLevelClass( clazz.getDeclaringClass() );
    }

    public static <E extends Enum<E>> Enum loadEnumConst( 
        Class<E> enumClass, String constantName )
    {
        Object[] enumConstants = enumClass.getEnumConstants();
        for( int i = 0; i < enumConstants.length; i++ )
        {
            Enum e = (Enum)enumConstants[ i ];
            if( e.name().equals( constantName ) )
            {
                return e;
            }
        }
        throw new JavaException(
            "No enum constant named \"" + constantName+"\"" );
    }
        
    /**
     * <UL>
     * <LI>creates an instance of the tailored class constructor (given the
     * constructor params)
     * <LI>returns an instance of the Tailored Class.
     * </UL>
     *
     * @param theClass the class to create an instance of
     * @param constructorArgs params passed into the constructor
     * @return an Object instance of the tailored class
     */
    public static Object instance(
        Class<?> theClass, Object... constructorArgs )
    {
        Constructor<?>[] constructors = theClass.getConstructors();
        List<Constructor<?>> sameArgCount = new ArrayList<Constructor<?>>();

        if( constructors.length == 1 )
        {
            return construct( constructors[ 0 ], constructorArgs );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            //noinspection Since15
            if( constructors[ i ].getParameters().length == constructorArgs.length )
            {
                sameArgCount.add( constructors[ i ] );
            }
        }
        if( sameArgCount.size() == 1 )
        {
            return construct( sameArgCount.get( 0 ), constructorArgs );
        }
        for( int i = 0; i < constructors.length; i++ )
        {
            //Class<?>[] paramTypes = constructors[ i ].getParameterTypes();
            //if( allArgsAssignable( paramTypes, constructorArgs ) )
            if( allArgsAssignable( constructors[ i ], constructorArgs ) )
            {
                return construct( constructors[ i ], constructorArgs );
            }
        }
        throw new JavaException( "Could not find a matching constructor for input" );
    }

    public static void invokeMain( Class targetClass, String... arguments )
    {
        Method mainMethod = JavaReflection.getMainMethod( targetClass );
        if( mainMethod == null )
        {
            throw new JavaException( "Could not find main(String[] args) method " );
        }
        try
        {
            if( arguments.length == 0 )
            {
                mainMethod.invoke( null, (Object)new String[ 0 ] );
            }
            else
            {
                mainMethod.invoke( null, (Object)arguments );
            }
        }
        catch( IllegalAccessException iae )
        {
            System.out.println( iae );
            throw new JavaException(
                "Illegal Access calling \"" + targetClass
                + ".main( String[] args );\"", iae );
        }
        catch( IllegalArgumentException iae )
        {
            throw new JavaException(
                "Illegal Access calling \"" + targetClass
                + ".main( String[] args );\"", iae );
        }
        catch( InvocationTargetException ite )
        {
            throw new JavaException(
                ite.getTargetException().getMessage() + " calling \""
                + targetClass.getName() + ".main( String[] args )",
                ite.getTargetException() );
        }
    }

    /**
     * Invokes the instance method and returns the result
     *
     * @param target the target instance to invoke the method on
     * @param methodName the name of the method
     * @param arguments the parameters to pass to the method
     * @return the result of the call
     */
    public static Object invoke(
        Object target, String methodName, Object... arguments )
    {
        if( methodName.indexOf( "(" ) > 0 )
        {
            methodName = methodName.substring( 0, methodName.indexOf( "(" ) );
        }
        Method method = null;
        try
        {
            if( target instanceof Class )
            {
                return invokeStatic( (Class<?>)target, methodName, arguments );
            }

            method = getMethod(
                target.getClass().getMethods(), methodName, arguments );

            if( method == null )
            {
                throw new JavaException(
                    "Could not find method \"" + methodName + "\" on \"" + target + "\"" );
            }
            return method.invoke( target, arguments );
        }
        catch( SecurityException iae )
        {
            throw new JavaException(
                "Security Exception not call " + method, iae );
        }
        catch( IllegalAccessException iae )
        {
            throw new JavaException(
                "Illegal Access Could not call " + method, iae );
        }
        catch( IllegalArgumentException iae )
        {
            throw new JavaException(
                "IllegalArgument Could not call " + method, iae );
        }
        catch( InvocationTargetException iae )
        {
            throw new JavaException(
                "Invocation Target Exception on" + method, iae.getCause() );
        }
    }

    /**
     * Gets the value of a static field from a Field
     *
     * @param field the field definition
     * @return the value of the static field
     */
    public static Object getStaticFieldValue( Field field )
    {
        try
        {
            return field.get( null );
        }
        catch( IllegalArgumentException e )
        {
            throw new JavaException( "Illegal Argument for field " + field, e );
        }
        catch( IllegalAccessException e )
        {
            throw new JavaException( "Illegal Acccess for field " + field, e );
        }
    }

    /**
     *
     * @param clazz the clazz to get the field from
     * @param fieldName the name of the field
     * @return the value of the field on the class
     * @throws JavaException if a reflection exception occurrs
     */
    public static Object getStaticFieldValue( Class<?> clazz, String fieldName )
    {
        try
        {
            Field f = clazz.getField( fieldName );
            return f.get( null );
        }
        catch( NoSuchFieldException e )
        {
            throw new JavaException( "No Such Field \"" + fieldName + "\"", e );
        }
        catch( SecurityException e )
        {
            throw new JavaException( "Security Exception on Field \"" + fieldName + "\"", e );
        }
        catch( IllegalArgumentException e )
        {
            throw new JavaException( "Illegal Argument for field " + fieldName, e );
        }
        catch( IllegalAccessException e )
        {
            throw new JavaException( "Illegal Access to field " + fieldName, e );
        }
    }

    /**
     * Strategy for: 
     * <OL>
     * <LI>setting a field OR 
     * <LI>calling a Set method for a field
     * </OL>
     * for a field/property of a class
     * 
     * i.e.<PRE>
     * public class FSet
     * {
     *     public String name;
     * }
     * 
     * ...in this case, we 
     * </PRE>
     * @param instance the instance to invoke set on
     * @param name the name of the field to be set (via direct or setter access)
     * @param value the value to call set with
     */
    public static void set( Object instance, String name, Object value ) 
    {
        //first try set a Field of this name
        try
        {
            setFieldValue( instance, name, value );            
            return;
        }
        catch( JavaException e )
        {
            //ok setting field 
            if( !name.startsWith( "set" ) )
            {
                name = "set" + 
                    Character.toUpperCase( name.charAt( 0 ) ) 
                    + name.substring( 1 );
            }
            List<Method> setMethods = 
                getMethods( instance.getClass(), name );
            if( setMethods.isEmpty() )
            {
                throw new JavaException( 
                    "Could not find set method named \"" + name + "\"" );
            }
            // HOPEFULLY only one method, if not, 
            // brute force it (if there is more than one, just try all
            // of them until one passes
            Exception lastException = null;
            for( int i = 0; i < setMethods.size(); i++ )
            {
                Method m = setMethods.get( i );
                try
                {
                    m.invoke( instance, value );
                    return;
                }
                catch( Exception ex )
                {
                    lastException = ex;
                }
            }
            throw new JavaException( lastException );
        }
        
    }
    
    /**
     * Sets the value of a field on an instance
     *
     * @param instance the instance
     * @param fieldName the name of the field
     * @param value the value to set the field
     * @throws JavaException if any reflective exception occur locating/setting
     * the field
     */
    public static void setFieldValue( 
        Object instance, String fieldName, Object value )
    {
        try
        {
            Field f = instance.getClass().getField( fieldName );
            f.set( instance, value );
        }
        catch( NoSuchFieldException e )
        {
            throw new JavaException(
                "No Such Field \"" + fieldName + "\" as " + value, e );
        }
        catch( SecurityException e )
        {
            throw new JavaException(
                "Security Exception for \"" + fieldName + "\" as " + value, e );
        }
        catch( IllegalArgumentException e )
        {
            throw new JavaException(
                "IllegalArgument for \"" + fieldName + "\" as " + value, e );
        }
        catch( IllegalAccessException e )
        {
            throw new JavaException(
                "Illegal Access to set \"" + fieldName + "\" as " + value, e );
        }
    }

    /**
     * Gets the value of a field (or calls a get method to retrieve the value
     * by a getter)
     * for this class:
     * <PRE>
     * public class GetByField
     * {
     *    public field count = 100;
     * }
     * 
     * GetByField gbf = new GetByField();
     * 
     * //if we call
     * Object o = Java.get( gbf, "count" );
     * //we would reflectively get the count <B>by the "count" field</B> 
     * </PRE>
     * for this class:
     * <PRE>
     * public class GetByMethod
     * {
     *     public final String[] names = {"A", "B"};
     * 
     *     public int getCount()
     *     {
     *        return names.length;
     *     }
     * }
     * GetByMethod gbm = new GetByMethod();
     * 
     * //if we call
     * Object o = Java.get( gbm, "count" );
     * //we would reflectively get the count <B>by the "getCount()" method</B> 
     * </PRE>
     * 
     * </PRE>
     * @param instanceOrClass
     * @param name
     * @return 
     */
    public static Object get( Object instanceOrClass, String name )
    {
        if( instanceOrClass instanceof Class )
        {
            //System.out.println( "CLASS" );
            try
            {
                return getStaticFieldValue( (Class)instanceOrClass, name );
            }            
            catch( JavaException je )
            {
                //dont fail, just try getting it by 
                return invokeStatic( 
                    (Class)instanceOrClass, 
                    "get" + Character.toUpperCase(  name.charAt( 0 ) ) + name.substring( 1 ),
                    null );
            }            
        }
        //System.out.println( "INSTANCE" );
        try
        {
            return getFieldValue( instanceOrClass, name );
        }
        catch( JavaException je )
        {
            return invoke( 
                instanceOrClass, 
                "get" + Character.toUpperCase(  name.charAt( 0 ) ) + name.substring( 1 ),
                null );
        }
    }
    
    /**
     * Gets the value of the Field
     *
     * @param instanceOrClass
     * @param fieldName
     * @return
     */
    public static Object getFieldValue( Object instanceOrClass, String fieldName )
    {
        if( instanceOrClass instanceof Class )
        {
            //if( LOG.isDebugEnabled() )
            //{
            //    LOG.debug( "getting static field \"" + fieldName + "\"" );
            //}
            return getStaticFieldValue( (Class<?>)instanceOrClass, fieldName );
        }
        try
        {
            Field f = instanceOrClass.getClass().getField( fieldName );
            return f.get( instanceOrClass );
        }
        catch( IllegalAccessException iae )
        {
            throw new JavaException( "Illegal Access for \"" + fieldName + "\"", iae );
        }
        catch( NoSuchFieldException e )
        {
            throw new JavaException( "No such Field exception for \"" + fieldName + "\"", e );
        }
        catch( SecurityException e )
        {
            throw new JavaException( "SecurityException for field \"" + fieldName + "\"", e );
        }
        catch( IllegalArgumentException e )
        {
            throw new JavaException( "Illegal Argument for field \"" + fieldName + "\"", e );
        }
    }

    
    public static Object invokeStatic( Method method, Object...args )
    {
        try
        {
            if( !method.isVarArgs() )
            {
                return method.invoke( null, args );
            }
            //varargs methods, need to condense
            Parameter[] ps = method.getParameters();
            if( args.length < ps.length - 1 )
            {
                throw new JavaException( "too few arguments to invoke " + method );
            }
            if( ps.length == args.length || ps.length == args.length - 1 )
            {
                return method.invoke( null, args ); //the vararg part is null
            }
            Parameter varArgParameter = ps[ ps.length -1 ];
            System.out.println( "handling varargs for parameter "+ varArgParameter );
            
            Class vType = varArgParameter.getType().getComponentType();
            
            System.out.println( args[0].getClass() );
            int vIndex = ps.length - 1;
            
            System.out.println( "COMPONENT TYPE " + varArgParameter.getType().getComponentType() );
            
            int vargCount = args.length - ( ps.length - 1 );
            if( vIndex > 0 )
            {
                //trailing varagrs
                //Object[] newArgs = 
                throw new JavaException (" NOT DONE YET");
            }
            if( vType.isPrimitive() )
            {
                //System.out.println( "PRIMITVE" );
                if( int.class.isAssignableFrom( vType )  )
                {            
                    //return method.invoke( null, (Integer[])args );
                    return method.invoke( null, (int[])ofInt( args ) );
                }
                if( vType.isAssignableFrom( byte.class)  )
                {
                    return method.invoke( null, (byte[])ofByte( args ) );
                }
                if( vType.isAssignableFrom( long.class)  )
                {
                    return method.invoke( null, (long[])ofLong( args ) );
                }
                if( vType.isAssignableFrom( short.class)  )
                {
                    return method.invoke( null, (short[])ofShort( args ) );
                }
                if( vType.isAssignableFrom( char.class )  )
                {
                    return method.invoke( null, (char[])ofChar( args ) );
                }
                if( vType.isAssignableFrom( float.class)  )
                {
                    return method.invoke( null, (float[])ofFloat( args ) );
                }
                if( vType.isAssignableFrom( double.class)  )
                {
                    return method.invoke( null, (double[])ofDouble( args ) );
                }
                return method.invoke(  null, (boolean[])ofBoolean( args ) );
            }
            return method.invoke( null, (Object[])args );            
        }
        catch( IllegalAccessException ex )
        {
            throw new JavaException(
                "Illegal Access calling \"" + method+ "();\"", ex );
        }
        catch( IllegalArgumentException ex )
        {
            throw new JavaException(
                "Illegal Argument calling \"" + method+ "\"", ex );
        }
        catch( InvocationTargetException ex )
        {
            throw new JavaException(
                "Illegal Access calling \"" + method+ "();\"", ex.getTargetException() );
        }
    }

    private static byte[] ofByte( Object[] Bytes )
    {
        byte[] bytes = new byte[ Bytes.length ];
        for( int i=0; i< Bytes.length; i++ )
        {
            bytes[i] = (Byte)Bytes[ i ];
        }
        return bytes;
    }
    
    private static short[] ofShort( Object[] Shorts )
    {
        short[] shorts = new short[ Shorts.length ];
        for( int i=0; i< Shorts.length; i++ )
        {
            shorts[i] = (Short)Shorts[ i ];
        }
        return shorts;
    }
        
    
    private static boolean[] ofBoolean( Object[] Booleans )
    {
        boolean[] bools = new boolean[ Booleans.length ];
        for(int i=0; i< Booleans.length; i++ )
        {
            bools[i] = (Boolean)Booleans[ i ];
        }
        return bools;
    }
    
    private static int[] ofInt( Object[] Ints )
    {
        int[] ints = new int[ Ints.length ];
        for(int i=0; i< Ints.length; i++ )
        {
            ints[i] = (Integer)Ints[ i ];
        }
        return ints;
    }
    /*
    private static int[] from( Integer[] Ints )
    {
        int[] ints = new int[ Ints.length ];
        for(int i=0; i< Ints.length; i++ )
        {
            ints[i] = Ints[ i ];
        }
        return ints;
    }
    */
    
    private static char[] ofChar( Object[] Chars )
    {
        char[] chars = new char[ Chars.length ];
        for(int i=0; i< Chars.length; i++ )
        {
            chars[i] = (Character)Chars[ i ];
        }
        return chars;
    }
    
    private static double[] ofDouble( Object[] Doubles )
    {
        double[] doubles = new double[ Doubles.length ];
        for(int i=0; i< Doubles.length; i++ )
        {
            doubles[i] = (Double)Doubles[ i ];
        }
        return doubles;
    }
    
    private static float[] ofFloat( Object[] Floats )
    {
        float[] floats = new float[ Floats.length ];
        for(int i=0; i< Floats.length; i++ )
        {
            floats[i] = (Float)Floats[ i ];
        }
        return floats;
    }
        
    private static long[] ofLong( Object[] Longs )
    {
        long[] longs = new long[ Longs.length ];
        for(int i=0; i< Longs.length; i++ )
        {
            longs[i] = (Long)Longs[ i ];
        }
        return longs;
    }
    
    
    
    public static Object invokeStatic(
        Class<?> clazz, String methodName, Object... args )
    {
        try
        {
            Method method = getMethod( clazz.getMethods(), methodName, args );
            if( method == null )
            {
                throw new JavaException(
                    "Could not find method \"" + methodName + "\" on \""
                    + clazz.getName() + "\"" );
            }
            return method.invoke( clazz, args );
        }
        catch( IllegalAccessException iae )
        {
            throw new JavaException(
                "Illegal Access calling \"" + clazz.getName() + "." + methodName + "();\"", iae );
        }
        catch( IllegalArgumentException iae )
        {
            throw new JavaException(
                "Illegal argument calling \"" + clazz.getName() + "." + methodName + "();\"", iae );
        }
        catch( InvocationTargetException ite )
        {
            throw new JavaException(
                ite.getTargetException().getMessage() + " calling \""
                + clazz.getName() + "." + methodName + "();\"",
                ite.getTargetException() );
        }
    }

    public static Method getMainMethod( Class clazz )
    {
        Method[] methods = clazz.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            if( Modifier.isStatic( methods[ i ].getModifiers() )
                && methods[ i ].getName().equals( "main" )
                && methods[ i ].getParameterCount() == 1
                && methods[ i ].getParameterTypes()[ 0 ].isArray()
                && methods[ i ].getParameterTypes()[ 0 ].getComponentType().equals( String.class ) )
            {
                return methods[ i ];
            }
        }
        return null;
    }

    /**
     * matches and returns a static method with the methodName that matches
     * arguments
     *
     * @param methods all methods to search
     * @param methodName the target method name
     * @param args the arguments to the method
     * @return the java.lang.reflect.Method
     */
    public static Method getStaticMethod(
        Method[] methods, String methodName, Object[] args )
    {
        for( int i = 0; i < methods.length; i++ )
        {
            if( Modifier.isStatic( methods[ i ].getModifiers() )
                && methods[ i ].getName().equals( methodName ) )
            {
                //if( allArgsAssignable( methods[ i ].getParameterTypes(), args ) )
                if( allArgsAssignable( methods[ i ], args ) )
                {
                    return methods[ i ];
                }
            }
        }
        return null;
    }

    /**
     * given a Class, find all methods with a given name and return them
     *
     * @param clazz
     * @param methodName
     * @return all methods of a given name
     */
    public static List<Method> getMethods( Class clazz, String methodName )
    {
        List<Method> methodsOfName = new ArrayList<Method>();
        Method[] methods = clazz.getMethods();
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                methodsOfName.add( methods[ i ] );
            }
        }
        return methodsOfName;
    }

    public static Method getMethod(
        Method[] methods, String methodName, Object... args )
    {
        List<Method>pots = new ArrayList<Method>();
        for( int i = 0; i < methods.length; i++ )
        {
            if( methods[ i ].getName().equals( methodName ) )
            {
                //System.out.println( "FOUND METHOD " );
                //    "Try Method " + methods[ i ]+" for "+  args[0] +" " + args[0].getClass() );
                //if( allArgsAssignable( methods[ i ].getParameterTypes(), args ) )
                if( allArgsAssignable( methods[ i ], args ) )
                {
                    return methods[ i ];
                }
                else if( methods[ i ].getParameterCount() == args.length )
                {   //this is a fallback, since argument assignment is an inexact art
                    pots.add( methods[ i ] );
                }
            }
        }
        //maybe its a generically defined method??
        if( pots.size() == 1 )
        {   //if there was only (1) method with the same name and # args, 
            //return it
            return pots.get( 0 );
        }        
        return null;
    }

    /**
     * Construct a "new" java instance by calling the constructor
     *
     * @param constructor the constructor
     * @param args arguments passed in the constructor
     * @return the new instance
     */
    private static Object construct(
        Constructor<?> constructor, Object[] args )
    {
        try
        {
            if( args.length > 0 )
            {
                //LOG.debug( "Calling constructor >"
                //    + constructor + " with [" + args.length + "] arguments" );
                return constructor.newInstance( args );
            }
            else
            {
                //LOG.debug( "Calling no-arg constructor > " + constructor );
                return constructor.newInstance();
            }
            //
        }
        catch( InstantiationException e )
        {
            throw new JavaException( "Instantiation Exception to construct", e );
        }
        catch( IllegalAccessException e )
        {
            throw new JavaException( "Illegal Access to construct", e );
        }
        catch( IllegalArgumentException e )
        {
            throw new JavaException( "Illegal Argument to construct", e );
        }
        catch( InvocationTargetException e )
        {
            throw new JavaException( "Invocation Target Exception for construct", e.getCause() );
        }
    }

    private static void addPropertyIfNonNull( StringBuilder sb,
        String propertyName )
    {
        String propertyValue = System.getProperty( propertyName );
        if( propertyValue != null && propertyValue.trim().length() > 0 )
        {
            sb.append( propertyName );
            sb.append( " = " );
            sb.append( propertyValue );
            sb.append( "\r\n" );
        }
    }

    /**
     * Describes the Current Java Environment (at Runtime)
     *
     * @return a String that details particulars about the Java Runtime
     */
    public static String describeEnvironment()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "--- Current Java Environment --- " );
        sb.append( "\r\n" );
        addPropertyIfNonNull( sb, "java.vm.name" );
        addPropertyIfNonNull( sb, "java.runtime.version" );
        addPropertyIfNonNull( sb, "java.library.path" );
        addPropertyIfNonNull( sb, "java.vm.version" );
        addPropertyIfNonNull( sb, "sun.boot.library.path" );
        sb.append( "--------------------------------- " );
        sb.append( "\r\n" );
        return sb.toString();
    }

    /**
     * THIS IS ALL FOR ARE ALL ARGS ASSIGNABLE, ITS A MESS, DOESNT WORK WITH
     * VARARGS
     */
    /**
     * Try and "match" the arguments of a method with the arguments provided
     *
     * TODO I NEED TO HANDLE VARARGS
     *
     * @param target the target arguments
     * @param args the actual arguments
     * @return
     */
    protected static boolean allArgsAssignable( Class<?>[] target,
        Object... args )
    {
        if( args == null || args.length == 0 )
        {
            return target.length == 0;
        }
        if( target == null )
        {
            return args.length == 0;
        }
        if( target.length == 0 )
        {
            return args.length == 0;
        }
        if( target.length == args.length )
        {   //they have the same number of arguments, but are they type compatible?
            //System.out.println( "Same Arg count" );
            for( int pt = 0; pt < target.length; pt++ )
            {
                //System.out.println( "test target "+ target[ pt ]+" against args "+ args[pt] );
                //if( !isArgAssignable( args[ pt ].getClass(), target[ pt ] ) )
                if( args[ pt ] == null )
                {
                    continue;
                }
                if( target[ pt ].equals( args[ pt ].getClass() ) )
                {
                    continue;
                }
                if( !isArgAssignable( target[ pt ], args[ pt ] ) )
                {
                    return false;
                }
            }
            return true;
        }
        //TODO handle varargs

        return false;
    }

    protected static boolean allArgsAssignable( Constructor ctor, Object... args )
    {
        if( ctor.isVarArgs() )
        {
            //System.out.println( "VARARGS " + ctor ); 
            if( args.length > 0 )
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else
        {
            return allArgsAssignable( ctor.getParameterTypes(), args );
        }
    }

    /**
     * gotta check if varargs
     */
    protected static boolean allArgsAssignable( Method method, Object... args )
    {
        if( method.isVarArgs() )
        {
            //System.out.println( "VARARGS" + method ); 
            Type[] at = method.getParameterTypes();
            int minArgs = at.length - 1;
            //System.out.println( "MINARGS" + minArgs ); 

            if( minArgs == 0 )
            {
                if( args.length == 0 )
                {
                    //System.out.println( "NO ARGS!" );
                    return true;
                }
                for( int i = 0; i < args.length -1; i++ )
                {
                    System.out.println( "TRYING arg[i]" + args[ i ] +" on "+ method);
                    return true;
                    /*
                    
                    if( !isArgAssignable( at[ Math.min( i, at.length -1) ].getClass(), args[ i ] ) )
                    {
                        return false;
                    }
                    return true;
                    */
                }
            }
            return false;
        }
        else
        {
            return allArgsAssignable( method.getParameterTypes(), args );
        }
    }

    /**
     * is this arg assignable to the target class?
     *
     * @param targetArg the target method class
     * @param arg
     * @return
     */
    protected static boolean isArgAssignable( Class<?> targetArg, Object arg )
    {
        if( arg == null )
        {
            return true;
        }
        if( targetArg.isPrimitive() || arg.getClass().isPrimitive() )
        {
            //System.out.println( arg + " is *** primitive " + targetArg );
            //return translatesTo( arg, targetArg );
            return translatesTo( arg, targetArg );
        }
        if( arg instanceof Class )
        {
            boolean isInst = ((Class)arg).isAssignableFrom( targetArg );
            //System.out.println( arg + " is *** instance of " + targetArg +" "+ isInst );
            return isInst;
        }
        if( arg.getClass().isInstance( targetArg ) )
        {
            //System.out.println( arg.getClass() + " is *** instance of " + targetArg );
            //System.out.println( arg + " is *** instance of " + targetArg );
            return true;
        }
        return false;
    }

    private static final Map<Class<?>, Set<Class<?>>> SOURCE_CLASS_TO_TARGET_CLASSES
        = new HashMap<Class<?>, Set<Class<?>>>();

    static
    {
        Set<Class<?>> byteMapping = new HashSet<Class<?>>();
        byteMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    byte.class, Byte.class, short.class, Short.class, int.class,
                    Integer.class, long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( byte.class, byteMapping );

        Set<Class<?>> ByteMapping = new HashSet<Class<?>>();
        ByteMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    byte.class, Byte.class, short.class, Short.class, int.class,
                    Integer.class, long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( Byte.class, ByteMapping );

        Set<Class<?>> shortMapping = new HashSet<Class<?>>();
        shortMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    short.class, Short.class, int.class,
                    Integer.class, long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( short.class, shortMapping );

        Set<Class<?>> ShortMapping = new HashSet<Class<?>>();
        ShortMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    short.class, Short.class, int.class,
                    Integer.class, long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( Short.class, ShortMapping );

        Set<Class<?>> intMapping = new HashSet<Class<?>>();
        intMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    int.class, Integer.class, long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( int.class, intMapping );

        Set<Class<?>> longMapping = new HashSet<Class<?>>();
        longMapping.addAll(
            Arrays.asList(
                new Class<?>[]
                {
                    long.class, Long.class
                } ) );

        SOURCE_CLASS_TO_TARGET_CLASSES.put( long.class, longMapping );
    }

    protected static boolean translatesTo( Object source, Class<?> target )
    {
        if( source.getClass() == target )
        {
            return true;
        }
        if( target.isPrimitive() )
        {
            if( source instanceof Long || source.getClass() == long.class )
            {
                return target == long.class;
            }
            if( source instanceof Integer || source.getClass() == int.class )
            {
                return target == long.class || target == int.class;
            }
            if( source instanceof Short || source.getClass() == short.class )
            {
                return target == long.class || target == int.class || target == short.class;
            }
            if( source instanceof Byte || source.getClass() == byte.class )
            {
                return target == long.class || target == int.class || target == short.class || target == byte.class;
            }
            if( source instanceof Character || source.getClass() == char.class )
            {
                return target == char.class;
            }
            if( source instanceof Boolean || source.getClass() == boolean.class )
            {
                return target == boolean.class;
            }
            if( source instanceof Float || source.getClass() == float.class )
            {
                return target == float.class;
            }
            if( source instanceof Double || source.getClass() == double.class )
            {
                return target == double.class;
            }
        }
        Set<Class<?>> clazzes = SOURCE_CLASS_TO_TARGET_CLASSES.get( source.getClass() );
        if( clazzes != null )
        {
            return clazzes.contains( target );
        }
        return false;
    }
}
