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

import java.util.ArrayList;
import java.util.List;
import varcode.java.model._class;

/**
 * Auto-Programming Macros that can be "applied" to {@link _class} models.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface _autoApply
{
        
    /** 
     * apply the _automatic modification and return the modified _class
     * @param _c model of a class to be applied
     * @return the modified _class      
     */
    _class apply( _class _c );
    
    /**
     * An ordered list of auto-programming "macros" that can be applied to 
     * any _class model.
     */
    public static class _autoMacro
        implements _autoApply
    {
        /** list of _autoApply macros to be applied in order*/
        private List<_autoApply>toApply = new ArrayList<_autoApply>();
        
        public static _autoMacro of( _autoApply... applyInOrder )
        {
            return new _autoMacro( applyInOrder );
        }
        
        /** 
         * construct a ordered list of auto programming macros to be applied
         * in order
         * @param macros 
         */
        public _autoMacro( _autoApply...macros )
        {
            for( int i = 0; i < macros.length; i++ )
            {
                toApply.add( macros[ i ] );
            }            
        }

        /** 
         * add more macros to the end
         * @param macro
         * @return this (after adding the _autoApply macros)
         */
        public _autoMacro add( _autoApply...macro )
        {
            for( int i = 0; i < macro.length; i++ )
            {
                toApply.add(macro[ i ] );
            }
            return this;
        }
        
        @Override
        public _class apply( _class _c )
        {
            for( int i = 0; i < toApply.size(); i++ )
            {
                toApply.get( i ).apply( _c );
            }
            return _c;
        }     
        
        public _class to( _class _c )
        {
            return apply( _c );
        }
    }
}
