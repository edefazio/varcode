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
package varcode;

/**
 * Exception loading a resource (.java code, .class Source, Binary, etc.)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class LoadException 
   extends VarException
{    
    public LoadException( String message, Throwable throwable )  
    {
        super( message, throwable );
    }
    
    public LoadException( String message )  
    {
        super( message );
    }
    
    public LoadException( Throwable throwable )  
    {
        super( throwable );
    }
}
