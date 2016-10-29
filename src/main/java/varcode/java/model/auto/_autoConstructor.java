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
package varcode.java.model.auto;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.java.model._class;
import varcode.java.model._code;
import varcode.java.model._constructors._constructor;
import varcode.java.model._fields;

/**
 * Given fields on an Object, creates a constructor
 * that accepts all final non-initialized fields.
 * ...
 * 
 * so if I have the following class:
 * <PRE>
 * _class c = _class.of("MyBean")
 *     .field("public int a;")
 *     .field("private final int b = 100;")
 *     .field("private final int c;");
 * 
 * _constructor c = _autoConstructor.
 * </PRE>
 * @author M. Eric DeFazio eric@varcode.io
 */
public class _autoConstructor
{
    
    /**
     * Returns a constructor based on which final, non initializexd fields
     * are on the classModel
     * @param classModel the model for the class
     * @return a constructor
     */
    public static _constructor of( _class classModel )
    {
        return of( classModel.getSignature().getName(), classModel.getFields() );
    }
    
    /**
     * 
     * @param className the name of the class to construct
     * @param fs member fields of the class
     * @return constructor with all final non-initialized fields being set
     */
    public static _constructor of( String className, _fields fs )
    {
         //we need a constructor with all the uninitialized final fields
        //verify that all fields are either
        // nonfinal and they are final With an initializer
        List<_fields._field>uninitializedFinalFields = new ArrayList<_fields._field>();
        
        String[] fieldNames = fs.getFieldNames();
        
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _fields._field f = fs.getByName( fieldNames[ i ] );
            if( f.getModifiers().contains( Modifier.FINAL ) && 
                !f.hasInit() )
            {
                uninitializedFinalFields.add( f );
            }
        }
        
        String paramList = "";
        _code finalInitCode = new _code();
        
        for( int i = 0; i < uninitializedFinalFields.size(); i++ )
        {
            _fields._field f = uninitializedFinalFields.get( i );
            if( i > 0 )
            {
                paramList += ", ";
            }
            paramList +=  f.getType() + " " + f.getName();
            
            finalInitCode.addTailCode( 
                "this." + f.getName() + " = " + f.getName() + ";" );
        }
            
        String constructorSig = 
            "public " + className + "( " + paramList + " )";
        
        return _constructor.of( constructorSig, finalInitCode );        
    }
}
