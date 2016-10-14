/*
 * Copyright 2016 eric.
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
package varcode.doc.translate;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import varcode.doc.translate.TypeTranslateTest.AClass;

/**
 *
 * @author eric
 */
public class JavaTranslateTest
    extends TestCase
{
    public void testTranslatePrimitives()
    {
        //assertEquals( JTranslate.INSTANCE.translate( 'c' ), "'c'");
        //assertEquals( JTranslate.INSTANCE.translate( 3.14f ), "3.14f" );
    }
    public static JavaTranslate T = JavaTranslate.INSTANCE;
    
    
    //I dunno, I *THINK* I want to do this....
    public void testLiterals()
    {
        //assertEquals("'a'", T.translate('a' ) );
        //assertEquals("1.0", T.translate( 1.0f ) );
    }
    
    
    public void testTypes() throws NoSuchMethodException
    {
        AnnotatedType at = AClass.class.getMethod( "doNothing", String.class )
            .getAnnotatedParameterTypes()[0]; 
        
        assertEquals( "String", 
            T.translate( at ) );
        
        Type pt = AClass.class.getMethod( "doNothing", String.class )
            .getParameterTypes()[0]; 
        
        assertEquals( "String", 
            T.translate( pt ) );
        
        
        String annParam = "";
        String param = "";
        Method[] methods = JavaTranslateTest.class.getMethods();
        for(int i=0; i< methods.length; i++ )
        {
            
            if( methods[i].getName().equals("manyGenericParameters") )
            {
                annParam = T.translate( methods[i].getAnnotatedParameterTypes() );
                param = T.translate( methods[i].getParameterTypes() );
            }            
        }
        assertEquals(
            "java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>, java.lang.String, int", annParam );
        
        assertEquals( 
            "java.util.Map, String, int", param );
        
        
        assertEquals( "int", 
            T.translate( 
                JavaTranslateTest.class.getMethod( "gen", int.class )
                    .getAnnotatedParameterTypes()[0] ) );
        
        
        
    }
    
    public static void manyGenericParameters( 
        Map<String, List<Integer>> mapList, 
        String s, int y )
    {
        
    }
    public static List<String> gen( @Deprecated int a)
    {
        return null;
    }   
    
    public void translateClasses()
    {
        assertEquals("int", T.translate( int.class ) );
        assertEquals("short", T.translate( short.class ) );
        assertEquals("float", T.translate( float.class ) );
        assertEquals("byte", T.translate( byte.class ) );
        assertEquals("double", T.translate( double.class ) );
        assertEquals("float", T.translate( float.class ) );
        assertEquals("long", T.translate( long.class ) );
        assertEquals("boolean", T.translate( boolean.class ) );
        assertEquals("char", T.translate( char.class ) );
        
        //array classes
        assertEquals("int[]", T.translate( int[].class ) );
        assertEquals("short[]", T.translate( short[].class ) );
        assertEquals("float[]", T.translate( float[].class ) );
        assertEquals("byte[]", T.translate( byte[].class ) );
        assertEquals("double[]", T.translate( double[].class ) );
        assertEquals("float[]", T.translate( float[].class ) );
        assertEquals("long[]", T.translate( long[].class ) );
        assertEquals("boolean[]", T.translate( boolean[].class ) );
        assertEquals("char[]", T.translate( char[].class ) );
        
        assertEquals("String[]", T.translate(String[].class ) );
        
        assertEquals("String[]", T.translate(String[].class ) );
        
        assertEquals("String", T.translate(String.class ) );
        assertEquals("Float", T.translate(Float.class ) );
        
        assertEquals("java.util.Map", T.translate( Map.class ) );
        
        
        Class[] classes = new Class[]{ String.class, int.class, Map.class};
        assertEquals( "String, int, java.util.Map", T.translate(classes) );
    }
    
    
}
