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
package varcode.java.metalang.auto;

import varcode.java.metalang.auto._autoExternalizable;
import java.util.Arrays;
import junit.framework.TestCase;
import varcode.java.CloneInstance;
import varcode.java._Java;
import varcode.java.metalang._class;

/**
 *
 * @author eric
 */
public class _autoExternalizableTest
    extends TestCase
{
    
    public void testTransient()
    {
        _class c = _class.of("TransientFields")
           .field("public transient int count = 1;");
        
        _class e = _autoExternalizable.of( c );
        
        System.out.println( e );
        
    }
    
    public void testAllPrimitives()
    {        
        _class c = _class.of("AllPrimitiveFields");
        c.field("public int aInt = Integer.MIN_VALUE;");
        c.field("public byte aByte = Byte.MIN_VALUE;");
        c.field("public short aShort = Short.MIN_VALUE;");
        c.field("public boolean aBoolean = true;");
        c.field("public char aChar = 'a';");
        c.field("public long aLong = Long.MIN_VALUE;");
        c.field("public float aFloat = Float.MIN_VALUE;");
        c.field("public double aDouble = Double.MIN_VALUE;");
        
        _class e = _autoExternalizable.of( c );
        System.out.println( e );
        Object instance = e.instance( );
        
        // cloner serializes and Deserializes using the externalizable interface
        // and writeExternal/ readExternal methods
        Object clone = CloneInstance.clone( instance );
        
        assertEquals( Integer.MIN_VALUE, _Java.getFieldValue(clone, "aInt"));
        assertEquals( Byte.MIN_VALUE, _Java.getFieldValue(clone, "aByte"));
        assertEquals( Short.MIN_VALUE, _Java.getFieldValue(clone, "aShort"));
        assertEquals( true, _Java.getFieldValue(clone, "aBoolean"));
        assertEquals( 'a', _Java.getFieldValue(clone, "aChar"));
        assertEquals( Long.MIN_VALUE, _Java.getFieldValue(clone, "aLong"));
        assertEquals( Float.MIN_VALUE, _Java.getFieldValue(clone, "aFloat"));
        assertEquals( Double.MIN_VALUE, _Java.getFieldValue(clone, "aDouble"));        
    }
    
    //verify that we can handle externalizing primitive array fields
    public void testAllPrimitiveArrays()
    {        
        _class c = _class.of("AllPrimitiveFields");
        c.field("public int[] intArr = new int[]{Integer.MIN_VALUE};");
        c.field("public byte[] byteArr = new byte[]{Byte.MIN_VALUE};");
        c.field("public short[] shortArr = new short[]{Short.MIN_VALUE};");
        c.field("public boolean[] booleanArr = new boolean[]{true};");
        c.field("public char[] charArr = new char[]{'a'};");
        c.field("public long[] longArr = new long[]{Long.MIN_VALUE};");
        c.field("public float[] floatArr = new float[]{Float.MIN_VALUE};");
        c.field("public double[] doubleArr = new double[]{Double.MIN_VALUE};");
        
        _class e = _autoExternalizable.of( c );
        //System.out.println( e );
        Object instance = e.instance( );
        
        //cloner serializes and Deserializes using the externalizable interface
        // and writeExternal/ readExternal methods
        Object clone = CloneInstance.clone( instance );
        
        assertTrue( Arrays.equals( new int[]{Integer.MIN_VALUE}, 
            (int[])_Java.getFieldValue(clone, "intArr") ));
        
        assertTrue( Arrays.equals( new byte[]{Byte.MIN_VALUE}, 
            (byte[])_Java.getFieldValue(clone, "byteArr") ));
        
        assertTrue( Arrays.equals( new short[]{Short.MIN_VALUE}, 
            (short[])_Java.getFieldValue(clone, "shortArr") ));
        
        assertTrue( Arrays.equals( new boolean[]{true}, 
            (boolean[])_Java.getFieldValue(clone, "booleanArr") ));
        
        assertTrue( Arrays.equals( new long[]{Long.MIN_VALUE}, 
            (long[])_Java.getFieldValue(clone, "longArr") ));
        
        assertTrue( Arrays.equals( new float[]{Float.MIN_VALUE}, 
            (float[])_Java.getFieldValue(clone, "floatArr") ));
        
        assertTrue( Arrays.equals( new double[]{Double.MIN_VALUE}, 
            (double[])_Java.getFieldValue(clone, "doubleArr") ));
        
        assertTrue( Arrays.equals( new char[]{'a'}, 
            (char[])_Java.getFieldValue(clone, "charArr") )); 
        
        //what if they are all nulls??
        
        c = _class.of("AllArraysPrimitive");
        c.field("public int[] intArr");
        c.field("public byte[] byteArr");
        c.field("public short[] shortArr");
        c.field("public boolean[] booleanArr");
        c.field("public char[] charArr");
        c.field("public long[] longArr");
        c.field("public float[] floatArr");
        c.field("public double[] doubleArr");        
        e = _autoExternalizable.of( c );
        
        instance = e.instance( );
        
        //cloner serializes and Deserializes using the externalizable interface
        // and writeExternal/ readExternal methods
        clone = CloneInstance.clone( instance );
        
        assertTrue( Arrays.equals( null, 
            (int[])_Java.getFieldValue(clone, "intArr") ));
        
        assertTrue( Arrays.equals( null, 
            (byte[])_Java.getFieldValue(clone, "byteArr") ));
        
        assertTrue( Arrays.equals( null, 
            (short[])_Java.getFieldValue(clone, "shortArr") ));
        
        assertTrue( Arrays.equals( null, 
            (boolean[])_Java.getFieldValue(clone, "booleanArr") ));
        
        assertTrue( Arrays.equals( null, 
            (long[])_Java.getFieldValue(clone, "longArr") ));
        
        assertTrue( Arrays.equals( null, 
            (float[])_Java.getFieldValue(clone, "floatArr") ));
        
        assertTrue( Arrays.equals( null, 
            (double[])_Java.getFieldValue(clone, "doubleArr") ));
        
        assertTrue( Arrays.equals( null, 
            (char[])_Java.getFieldValue(clone, "charArr") )); 
        
    }    
}
