/*
 * Copyright 2016 M. Eric DeFazio.
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
package varcode.java.code;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import varcode.context.VarContext;
import varcode.doc.Directive;
import varcode.java.JavaCase;
import varcode.java.JavaCase.JavaCaseAuthor;
import varcode.java.JavaNaming;
import varcode.java.code._fields._field;

/**
 * Creates an ad-hoc dto (Data Transfer Object)
 * 
 * Data Transfer Objects are similar to "structs" in Java
 * they have private fields that can be accessed (read, updated)
 * using getters and setters.
 * 
 * Final Fields (that have no init)  MUST be passed in on the constructor i.e.
 * <UL>
 *   <LI>"private final int a;" <-- this is a final field with no init 
 *    (and a value for a must be passed in in constructor)
 *   <LI>"private final int b = 100;" <-- this is a fienal field with an init
 *</UL>  
 * Final fields have only a getter (cant be set)
 */
public class _dto
    implements JavaCaseAuthor
{
    private _class theClass;
    
    /**
     * Create a dto at the full pathName:<PRE>
     *  _dto.of("ex.varcode.dto.MyDto");</PRE>
     * 
     * @param fullClassName
     * @return 
     */
    public static _dto of( String fullClassName )
    {
         String[] packageAndClassName = 
            JavaNaming.ClassName.extractPackageAndClassName( fullClassName );
        
        return new _dto( packageAndClassName[ 0 ], packageAndClassName[ 1 ] );
    }
    
    /**
     * _dto myDto = 
     *     _dto.of( "ex.varcode.dto.MyDto" )
     *         .field( "public String name" )
     *         .field( "public Map<String,Integer> nameToCount")
     * 
     * 
     * @param packageName
     * @param className 
     */
    public _dto( String packageName, String className )
    {
       this.theClass = _class.of( packageName, "public class "+className );        
    }
    
    
    private static final String firstUpper( String s )
    {
        return Character.toUpperCase( s.charAt(0) ) + s.substring( 1 );
    }
    
    
    public _dto imports( Class clazz )
    {
        this.theClass.imports( clazz );
        return this;
    }
    
    /** 
     * Adds a field with class and name, and getter and setter 
     * (also imports the class if need be)
     * i.e.
     * <PRE>
     * dto myDto = _dto.of( "MyDto" );
     * myDto.field( BigDecimal.class, "value" );
     *  // creates: 
     * 
     * //adds the import
     * import java.math.BigDecimal;
     * 
     * public class MyDto
     * {
     *    //adds the field
     *    <B>private BigDecimal value;
     * 
     *    //adds the getter
     *    public BigDecimal getValue()
     *    {
     *        return this.value;
     *    }
     *    //adds the setter
     *    public void setValue( BigDecimal value )
     *    {
     *        this.value = value;
     *    }</B>
     * }
     * </PRE>
     */
    public _dto field( Class clazz, String name )
    {
        this.theClass.imports( clazz ); //import the clazz if necessary
        _field f = new _field( 
            _modifiers.of("private"), clazz.getCanonicalName(), name ); 
        this.theClass.getFields().addFields( f );  
        
         //add the getter
        this.theClass.method(
            "public " + f.getType() + " get" + firstUpper( f.getName() )+"()",
            "return this." + f.getName() +";");    
        
        //add a setter        
        this.theClass.method(
            "public void set" + firstUpper( f.getName() ) + "( " 
                    + f.getType() + " " + f.getName()+" )",
            "this." + f.getName() + " = " + f.getName() + ";" );    
        
        return this;        
    }
    
    /**
     * Creates a field along with getter and setter
     * 
     * 
     * field( "private String name" );
     * 
     * @param fieldDef
     * @return this
     */
    public _dto field( String fieldDef )
    {
        //first add the field
        _field f = _field.of( fieldDef );
        this.theClass.field( f );
        
        //add the getter
        this.theClass.method(
            "public "+f.getType() + " get" + firstUpper( f.getName() )+"()",
            "return this."+f.getName() );    
        
        //if the field is Final, if NOT add a setter        
        if( !f.getModifiers().contains( Modifier.FINAL ) )
        {
            this.theClass.method(
            "public void set" + firstUpper( f.getName() ) + "( " +f.getType()+" "+f.getName()+" )",
            "this." + f.getName() + " = " + f.getName() + ";" );    
        }        
        return this;        
    }
    
    


    @Override
    public JavaCase toJavaCase(Directive... directives)
    {
        return toJavaCase( null, directives );
    }

    @Override
    public JavaCase toJavaCase(VarContext context, Directive... directives)
    {
        //verify that all fields are either
        // nonfinal and they are final With an initializer
        List<_field>uninitializedFinalFields = new ArrayList<_field>();
        
        _fields fields = this.theClass.getFields();
        String[] fieldNames = fields.getFieldNames();
        for( int i = 0; i < fieldNames.length; i++ )
        {
            _field f = fields.getByName( fieldNames[ i ] );
            if( f.getModifiers().contains(Modifier.FINAL ) &&
                !f.hasInit() )
            {
                uninitializedFinalFields.add( f ); 
            }    
        }
        if( uninitializedFinalFields.size() > 0 )
        {
            //we need a constructor with all the uninitialized final fields
            String paramList = "";
            _code finalInitCode = new _code();
            for(int i=0; i<uninitializedFinalFields.size(); i++ )
            {
                _field f = uninitializedFinalFields.get( i );
                if( i > 0 )
                {
                    paramList += ", ";
                }
                paramList +=  f.getType()+" "+ f.getName();
                finalInitCode.addTailCode( 
                    "this." + f.getName() + " = " + f.getName()+";" );
            }
            _class adHoc = _class.from( theClass );
            
            String constructorSig = 
                "public "+this.theClass.getSignature().getName()+"( "+paramList+" )";
            adHoc.constructor( constructorSig, finalInitCode );
        
            if( context == null )
            {
                return adHoc.toJavaCase( directives );
            }
            else
            {
                return adHoc.toJavaCase( context, directives );
            }
        }        
        if( context == null )
        {
            return theClass.toJavaCase( directives );
        }
        else
        {
            return theClass.toJavaCase( context, directives );
        }
    }
    
}
