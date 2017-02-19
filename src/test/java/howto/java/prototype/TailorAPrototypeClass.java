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
import varcode.context.VarContext;
import varcode.java.Java;
import varcode.java.model._class;
import varcode.java.macro.Macro.fields;
import varcode.java.macro.Macro.remove;
import varcode.java.macro._classMacro;
import varcode.java.macro.Macro.sig;

/**
 *
 * @author Eric
 */
public class TailorAPrototypeClass
{
    //@import
    //@imports( re)
    
    @sig("public class {+Name*+} implements Serializeable")
    @fields("public {+fieldType+} {+fieldName+};")
    public static class Prototype implements Serializable
    {
        public int x;
        @remove public int y;
    }
    
    //retain and remove and add imports
    public static void main( String[] args )
    {
        _classMacro cm = _classMacro.of( Java._classFrom( Prototype.class ) );        
        _class _t = cm.expand( VarContext.of( 
            "Name", "Tailored", 
            "fieldName", "a", 
            "fieldType", int.class ) );
        
        System.out.println( _t );
        
        
        /*
        //load the model for this class
        _class _c = Java._classFrom( Prototype.class );
        
        //process the class annotations
        List<_ann> classSigAnns = _c.getAnnotations().getNamed( "sig" );
        _classOriginator cs = null;
        if( classSigAnns.size() > 0 )
        {
            _ann sig = classSigAnns.get( 0 );
            String form = sig.getAttributes().toString();
            form = form.substring( 1, form.length() -1 ); //remove the " "
            cs = new TailorClassSignature( form );            
        }
        else
        {
            cs = new CopyClassSignature( );            
        }
        
        System.out.println( cs );
        System.out.println( cs.create( _c, VarContext.of( "Name", "My" ) ) );
        //System.out.println( sig  );
        //System.out.println("NAME " + sig.getName() );
        //System.out.println("ATTRS" + sig.getAttributes() );        
        //Template classSig = BindML.compile( sig.getAttributes().toString() );        
        //Set<String>VarNames = new HashSet<String>();        
        */
    }
    
}
