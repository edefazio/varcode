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
package varcode.java.ast;

import com.github.javaparser.ast.visitor.VoidVisitor;

/**
 * A visitor that can be passed into ASTNodes to collect and properly
 * format Java code.
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface JavaCodeFormatVisitor
    extends VoidVisitor<Object>
{
    /**
     * Return the Formatted Java Source Code
     * (After visiting the AST nodes and collecting the code)
     * 
     * @return the formatted Java source code (as a String)
     */
    public String getSource();
    
    /**
     * Returns the code buffer
     * @return the buffer containing the existing source code 
     */
    public JavaCodeIndentBuffer getCodeBuffer();
}
