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
package varcode.java;

import varcode.VarException;

/**
 * An Exception that occurs in the Java Language Infrastructure / Runtime 
 * (usually when loading/invoking/reflecting on dynamically authored/compiled / 
 * linked Java code.
 * 
 * May be subclassed to provide more specificity (i.e. Reflection, Linking, 
 * ClassLoading, etc.)
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public class JavaException
    extends VarException
{
    private static final long serialVersionUID = 4495417336149528283L;

    public JavaException( String message, Throwable throwable ) 
    {
	super( message, throwable );
    }
		
    public JavaException( String message ) 
    {
	super( message );
    }
	
    public JavaException( Throwable throwable  ) 
    {
	super( throwable );
    }	
    
    /**
     * 
     */
    public static class NameException
        extends JavaException
    {
        public NameException( String message, Throwable throwable ) 
        {
            super( message, throwable );
        }
		
        public NameException( String message ) 
        {
            super( message );
        }
	
        public NameException( Throwable throwable  ) 
        {
            super( throwable );
        }	
    }
}
