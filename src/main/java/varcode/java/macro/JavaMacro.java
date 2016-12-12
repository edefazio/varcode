/*
 * Copyright 2016 Eric.
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

/**
 * Defines categories of Java MetaLang Model Macros
 * 
 * JavaMacros can provide "shortcuts" for generating and modifying 
 * {@code JavaMetaLang._models}.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaMacro
{
    /**
     * Macros that takes a _model,
     * mutates it and 
     * returns the updated _model
     */
    public interface Mutator
        extends JavaMacro
    {
        
    }
    
    /** a Macro that takes (one or more) _models 
     * (_class, _enum, _interface), or _facets 
     * and builds other _models or _facets:
     * 
     * For example: <PRE>
     * _class _a = _class.of(
     *      "public class A", 
     *      _field.of( "public final int count;" ) );
     * 
     * //automatically build a constructor for _a
     * _constructor _ctor = <B>_autoConstructor.of( _a )</B>;
     * 
     * _a.constructor( _ctor );
     * 
     * </PRE>
     * {@code _autoConstructor} is a _generator that accepts 
     * a {@code _class} as input and it creates / returns a 
     * {@code _constructor} for _a (it will include arguments 
     * and set all the final fields that are on the _class.
     */
    public interface Generator
        extends JavaMacro
    {
        
    }
    
    /** 
     * a Macro that provides a "new" API that accepts parameters 
     * and mediates the creating one or more {@code _models 
     * ( _class, _enum, _interface)} and /or {@code _facets}.
     * 
     * In short its an API using a "builder pattern" internally
     * for building _models.
     * 
     * for example:<PRE>
     * _class _myDto = 
     *     <B>_autoDto.ofClass</B>("ex.mycode.MyPoint",
     *        "public int x",
     *        "public int y", 
     *        "public String name");
     * <PRE>
     * _autoDto provides an API for building the sterotypical Dto
     * objects given the (String) definitions of the fields 
     * ...where _myDto is modeled as:<PRE>
     * 
     * package ex.mycode;
     * 
     * public class MyPoint
     * {
     *    public int x;
     *    public int y;
     *    public String name;
     * 
     *    public int getX()
     *    {
     *        return this.x;
     *    }
     *    public int getY()
     *    {
     *        return this.y;
     *    }
     *    public String getName()
     *    {
     *        return this.name;
     *    }
     *    public void setX( int x )
     *    {
     *         this.x = x;
     *    }
     *    public void setY( int y )
     *    {
     *         this.y = y;
     *    }
     *    public void setName( String name )
     *    {
     *         this.name = name;
     *    }
     * }
     * </PRE>
     * 
     * StereoTypes are about exposing a simple API to 
     * hide the modeling complexity / boilerplate and complex 
     * interactions amongst generated code entities.
     */ 
    public interface StereoType
        extends JavaMacro
    {
        
    }
    
    /**
     * Sometimes you might develop _facets of code independent
     * of the _model... for instance, you might want to define
     * a SLF4J logger implementation {@code PortableField} 
     */
    public interface Port
        extends JavaMacro
    {
        
    }        
    
    /** 
     * Change that is applied to a _model via a Macro
     * 
     * We *COULD* maintain a changelist of what a macro did
     * since we often apply multiple macros to a given model...
     * 
     * This allows traceability into what happened, so if
     * something failed, we can still generate Partial results.
     */ 
    
    
    /* 
    This is "half baked", but the idea is that Macros
    produce a ChangeList that allows changes to be accumulated
    and understood on a more precise level than trying to 
    "diffing code"
    
    public interface _edit
    {
        
    }
    
    public static class AddConstructorEdit
        implements _edit
    {
        public final _model component;
        public final _constructor constructor;
        
        public AddConstructorEdit( _model component, _constructor constructor )
        {
            this.component = component;
            this.constructor = constructor;
        }
    }
    
    public static class AddMethodEdit
        implements _edit
    {
        public final _model component;
        public final _method method;
        
        public AddMethodEdit( _model component, _method method )
        {
            this.component = component;
            this.method = method;
        }
    }
    */
}
