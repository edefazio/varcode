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
package _dev;

import java.util.HashMap;
import java.util.Map;
import varcode.java._Java;
import varcode.java.lang._annotations;
import varcode.java.lang._class;
import varcode.java.lang._methods;
import varcode.java.lang._methods._method;

/**
 * Basically I want to 
 */
public class ReadInClassMethodEtcAnnotationsMark 
{
    /**
     * Basically, we create this prototype
     * only to "house" the @$template$ entities
     */
    private static class Prototype
    {
        public class $type$ {};
        
        public $type$ $name$;
        
        /** gets $name$ */
        @$template$
        public $type$ get$Name$()
        {
            return this.$name$;
        }
        
        /** sets $name$ */
        @$template$
        public void set$Name$( $type$ $name$ )
        {
            this.$name$ = $name$;
        }
    }
    
    public static void main( String[] args )
    {
        _class _c = _Java._classFrom( Prototype.class );
        
        System.out.println( _c );        
        _methods _ms = _c.getMethods();
        
        //at the moment, we dont allow overloaded methods here
        Map<String, _method> templateMethodsMap = 
            new HashMap<String, _method>();
        
        for( int i = 0; i < _ms.count(); i++ )
        {
            _method _m = _ms.getAt( i );
            _annotations _anns = _m.getAnnotations();
            for( int j = 0; j < _anns.count(); j++ )
            {
                if( _anns.getAt( j ).toString().equals( "@$template$" ) )
                {
                    //System.out.println( "FOUND" + _m );
                    _method _mc = _method.cloneOf( _m );
                    _mc.getAnnotations().removeAt( j ); //remove the @$template$
                    templateMethodsMap.put( _mc.getName(), _mc );                        
                }
            }
        }
        System.out.println( templateMethodsMap );
        
    }
    
    /**
     * Signifies that the entity (static block, method, etc.)
     * contains variables and text demarcated by $...$ which will be replaced:
     * 
     * for example:
     * 
     * /**
     *  *
     *  * /
     * @$Template$
     * public static $type$ get$Name$( )
     * {
     *     return this.$name$;
     * }
     * 
     */
    @interface $template$
    {
        
    }
    
}
