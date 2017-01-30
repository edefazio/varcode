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
package varcode.java.model.auto;

import varcode.author.Author;
import varcode.java.model._class;
import varcode.java.model._fields;
import varcode.java.model._fields._field;
import varcode.java.model._methods._method;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;

/**
 * Creates a Fluent style Setter
 * <PRE>
 * public MyBean setCount( int count )
 * {
 *     this.count = count;
 *     return this;
 * }
 * </PRE> ...that has the set method return the containing class so you can
 * toString together set methods like this
 * <PRE>
 MyBean b = new MyBean().setA("1").setB("2").setC("3");

 //instead to this:

 MyBean b = new MyBean();
 b.setA("1");
 b.setB("2");
 b.setC("3");
 </PRE>
 */
public enum _autoSettersFluent
    implements _autoApply
{
    INSTANCE;
    
    @Override
    public _class apply( _class _c )
    {
        return to( _c );
    }
    
    /**
     * Fluent-Style set method
     *
     * @param className
     * @param f
     * @return
     */
    public static _method of( String className, _field f )
    {
        return of( className, f.getName(), f.getType() );
    }

    /**
     * Fluent-Style set method
     * <PRE>
     * <CODE>
     * public MyBean
     * {
     *    private int a;
     *    private int b;
     * 
     *    // the "fluent" setter returns the object itself (not void) 
     *    // after setting the value, this allows method chaining to initialize
     *    // instances in a single compound chained statement like:
     *    // MyBean mb = new MyBean().setA(1).setB(2);
     *    public MyBean setA( int a)
     *    {
     *        this.a = a;
     *        return this;
     *    }
     *    public MyBean setB( int b )
     *    {
     *         this.b= b;
     *         return this;
     *    }
     * }
     * </PRE>
     * </CODE>
     * @param className the name to the class returned from the set method     
     * @param type the type to the field to set
     * @param fieldName the name to the field
     * @return fluent setter _method given input
     */
    public static _method of( String className, Object type, String fieldName )
    {
        return _method.of( 
            Author.fillSeries( SIGNATURE, className, fieldName, type ),
            Author.fillSeries( BODY, fieldName ) );
    }
        
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static final Template SIGNATURE = BindML.compile(
        "public {+className+} set{+Name+}( {+type+} {+name+} )" );

    public static final Template BODY = BindML.compile(
        "this.{+name+} = {+name+};" + "\r\n"
      + "return this;" );
    
    //creates fluent Setter methods for all non-final fields on theClass
    public static _class to( _class _c )
    {
        //_class _c = _class.cloneOf( theClass );
        _fields fields = _c.getFields();
        for( int i = 0; i < fields.count(); i++ )
        {
            if( fields.getAt( i ).getModifiers().contains( "final" ) )
            {
                continue;
            }
            _c.method( 
                of( _c.getName(), 
                    fields.getAt( i ).getType(),
                    fields.getAt( i ).getName() ) );
        }

        return _c;
    }
}
