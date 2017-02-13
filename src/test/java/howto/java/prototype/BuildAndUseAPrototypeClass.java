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
package howto.java.prototype;

import java.io.Serializable;
import java.util.List;
import varcode.java.Java;
import varcode.java.model._ann;
import varcode.java.model._annotations;
import varcode.java.model._annotations._annotation;
import varcode.java.model._class;
import varcode.markup.Template.sig;

/**
 *
 * @author Eric
 */
public class BuildAndUseAPrototypeClass
{
    //@import
    @sig("public static class {+Name+} implements Serializeable")
    public static class Prototype implements Serializable
    {
        
    }
    
    public static void main(String[] args )
    {
        //load the model for this class
        _class _c = Java._classFrom( Prototype.class );
        
        //process the class annotations
        List<_ann> s = _c.getAnnotations().getNamed( "@sig" );
        
        System.out.println(  s  );
        _annotation _at = _annotation.of( s.get(0) );
        System.out.println( "NAME " + _at.getName() );
        System.out.println( "ATTRS" + _at.getAttributes() );
        
    }
}
