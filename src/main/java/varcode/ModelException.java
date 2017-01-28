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
 * An exception in the creation, mutation or modeling of a MetaLangModel. (i.e.
 * The Target Language does not support the model)
 *
 * Examples:
 * <UL>
 * <LI>trying to set the Class Name of a _class to "*&*^$&*^@#$")
 * <LI>trying to create a _class that extends from more than one baseClass
 * </UL>
 */
public class ModelException
    extends VarException
{
    public ModelException( String message, Throwable throwable )
    {
        super( message, throwable );
    }

    public ModelException( String message )
    {
        super( message );
    }

    public ModelException( Throwable throwable )
    {
        super( throwable );
    }
}
