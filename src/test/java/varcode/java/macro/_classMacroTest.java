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

import java.util.UUID;
import junit.framework.TestCase;
import varcode.context.Context;
import varcode.java.model._class;
import varcode.java.model._class._signature;
import varcode.java.model._fields._field;
import varcode.java.model._imports;
import varcode.java.model._methods._method;
import varcode.java.model._staticBlock;
import varcode.java.macro.Macro.CopyClassSignature;
import varcode.java.macro.Macro.CopyField;
import varcode.java.macro.Macro.CopyImports;
import varcode.java.macro.Macro.CopyMethod;
import varcode.java.macro.Macro.CopyPackage;
import varcode.java.macro.Macro.CopyStaticBlock;
import static varcode.java.macro._classMacro.*;
import varcode.java.model._ann;

/**
 *
 * @author Eric
 */
public class _classMacroTest
    extends TestCase
{
    
    
    
    /*
    public void testField()
    {
        _classMacro.parseField( _field f );
    }
    */
    /*
    public static void main(String[] args)
    {
        _class source = _class.of( "public class A" );
        _method _m = _method.of("public void doIt()", "System.out.println( \"Hey\");"); 
        CopyMethod fcm = new CopyMethod( _m );
        
        _class _tailored = new _class( _signature.cloneOf(  source.getSignature() ) );
        fcm.transfer( _tailored, null );
        
        
        CopyPackage fp = new CopyPackage( "" );
        fp.transfer(_tailored, null );
        
        source.packageName("ex.mypkg;");
        fp.transfer(  _tailored, null );
        
        
        System.out.println( _tailored );
    }
    */
}
