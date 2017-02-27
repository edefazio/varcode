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
 * Model of source code.
 * <A HREF="https://en.wikipedia.org/wiki/Metalanguage">metalanguage</A>
 (specifically in the case of Java, a Hierarchial MetaLangauge)
 
 An IR (Intermediate Representation) of a Hierarchal 
 Model of an entity that is represented in structured text
 (in short, textual code, File formats, configuration files,
 HTML, CSS, SQL, SVG, etc.)
 
 The purpose for the Model is to provide an intuitive API
 for analyzing, querying, and mutating a model based on source code 
 (i.e. Java code, an HTML Table, a CSS styleSheet, a Log4J config file, 
 a Maven POM file).
 
 MetaL provides a simple API that can read and interpret code and provides
 a programmer friendly model. 
 
 Having a mutable MetaL model is much more intuitive than:
 <UL>
 *  <LI>building source code from scratch using +=, sb.append(...) into 1BS ("1 Big String")
 *  <LI>manipulating 1BS ("1 Big String") representing source code
 *  <LI>manually parsing and manipulating the AST( Abstract Syntax Tree) for code
 * </UL>
 * 
 * A good analogy for a Meta Model is a HTML DOM (document object model):
 * from <A HREF="https://www.w3.org/DOM/">w3c.org</A><BR>
 * <BLOCKQUOTE>
 * "The Document Object Model is a platform- and language-neutral 
 * interface that will allow programs and scripts to dynamically 
 * access and update the content, structure and style of documents." 
 * </BLOCKQUOTE>
 * 
 * Meta Models can be written to handle:
 * <UL>
 *  <LI>SQL
 *  <LI>IDL Data Format
 *  <LI>Contract
 *  <LI>SVG Graphics
 *  <LI>Schema / DDL
 *  <LI>Property File
 *  <LI>Build Script
 *  <LI>Container Configuration
 *  <LI>etc...
 * </UL>
 * 
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Model
{	
    /** 
     * Strings passed in with this prefix signify they are 
     * Literals and not a String representation of an entity
     */
    public static final String STRING_LITERAL_PREFIX = "$$";
    
}
