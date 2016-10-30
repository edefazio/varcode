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
package varcode.java.model.edit;

import java.util.ArrayList;
import java.util.List;

public class Expect
{
    public final List<MethodEditor> modelVerifiers = 
        new ArrayList<MethodEditor>(); 
           
    public Expect()
    {
    }
            
    //expect the model has a static field 
    public Expect staticField( String type, String name  )
    {
        this.modelVerifiers.add( new ExpectStaticField( type, name ) );
        return this;
    }
            
    //expect the model has a static field 
    public Expect instanceField( String type, String name )
    {
        this.modelVerifiers.add( new ExpectInstanceField( type, name ) );
        return this;
    }
            
    public Expect argument( String type, String name )
    {
        this.modelVerifiers.add( new ExpectArgument( type, name ) );
        return this;
    }            
            
    /*
    public Expect addImports ( Object... imports )
    {
        this.modelVerifiers.add( new AddImports( imports ) );
        return this;
    }
    */
}
