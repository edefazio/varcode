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
package tutorial.varcode.chap3.loader;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.UUID;
import junit.framework.TestCase;
import varcode.VarException;
import varcode.java.model._class;
import varcode.java.model._constructors;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods;
import varcode.java.model._methods._method;
import varcode.java.model._parameters._parameter;
import varcode.java.model.load.JavaModelLoader;

/**
 * Loads the model from existing source code
 * 
 * @author M. Eric
 */
public class _1_LoadModelByClass
    extends TestCase
{
    /** 
     * We want to load the _class model for this class
     * then specialize it at runtime
     */
    public static class PrefixCreateId 
        implements Serializable
    {
        public final String prefix;
        
        public PrefixCreateId( String prefix )
        {
            this.prefix = prefix;
        }
        
        public String createId()
        {
            return this.prefix + UUID.randomUUID().toString();
        }
    }
    
    public void testLoadModelOfTopLevelClass()
    {
        //load the _class model for VarException.class
        _class c = JavaModelLoader.ClassModel.fromClass( VarException.class );
        assertEquals( "varcode.VarException",  c.getFullyQualifiedClassName() );
        assertEquals( "varcode", c.getClassPackage().getName() );
        assertEquals( 1, c.getSignature().getExtends().count() );
        assertEquals( "RuntimeException", c.getSignature().getExtends().get( 0 ) );
        assertEquals( 0, c.getSignature().getImplements().count() );
        assertTrue( c.getSignature().getModifiers().containsAll( "public" ) );
        
        //private static final long serialVersionUID = 4495417336149528283L;
        _fields fields = c.getFields();
        assertEquals( 1, fields.count() );
        _field f = fields.getAt( 0 );
        assertEquals( "serialVersionUID", f.getName() );
        assertTrue( 
            f.getModifiers().containsAll(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL) );
        assertEquals( "long", f.getType() );
        assertTrue( f.hasInit() );
        assertEquals( " = 4495417336149528283L", f.getInit().toString() );         
    }
    
    public void tLoadModelOfMemberClass()
    {
        //load the "model" of the PrefixCreateIdClass( from the source )
        _class _c = 
            JavaModelLoader.ClassModel.fromClass( PrefixCreateId.class );
        
        assertTrue( _c.getSignature().getModifiers().containsAll( "public", "static" ) );
        assertEquals( "PrefixCreateId", _c.getSignature().getName() );
        assertEquals( 1, _c.getSignature().getImplements().count() );
        assertTrue( _c.getSignature().getImplements().contains( Serializable.class ) );
        
        assertEquals( 1, _c.getFields().count() );        
        _field prefix = _c.getFields().getByName( "prefix" );
        assertTrue( prefix.getModifiers().containsAll( Modifier.PUBLIC, Modifier.FINAL ) );
        assertEquals( "String", prefix.getType() );
        
        _constructors ctors = _c.getConstructors();
        assertEquals( 1, ctors.count() );
        
        _constructor ctor = ctors.getAt( 0 );
        assertEquals( "PrefixCreateId", ctor.getSignature().getClassName() );
        assertTrue( ctor.getSignature().getModifiers().containsAll( Modifier.PUBLIC ) );
        assertEquals( 1, ctor.getSignature().getParameters().count() );
        _parameter param = ctor.getSignature().getParameters().getAt( 0 );
        assertEquals( "String", param.getType() );
        assertEquals( "prefix", param.getName() );
        
        _methods methods = _c.getMethods();
        assertEquals( 1, methods.count() );
        _method createId = _c.getMethodsByName( "createId" ).get( 0 );        
        assertEquals( "String", createId.getSignature().getReturnType() );
    }
}
