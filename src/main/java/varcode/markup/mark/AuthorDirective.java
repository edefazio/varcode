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
package varcode.markup.mark;

/**
 * {@code Mark} which names a {@code Directive} to be called during the
 * pre-processing/post-processing authoring stages of the template
 *
 * Some Examples of {@code Directive}s are:
 * <UL>
 * <LI>(PreProcessing) validate the input data provided in the
 * {@code VarContext} 
 * @see ContextValidator}
 * <LI>(PreProcessing) remove all {@code Mark}s from the {@code Template} 
 * {@link StripMarks}
 * <LI>(PostProcessing) formatting the source code for a specific coding
 * conventions
 * <LI>(PostProcessing) run a Lint Checker/FindBugs and export a report on the
 * source (to metadata)
 * <LI>(Pre/PostProcessing) create a Checksum of the {@code Template} and the
 * tailored Code
 * <LI>(PostProcessing) indent each line of the the source code by a tab or
 * certain number of spaces
 * <LI>(PostProcessing) use a compiler to compile and test the code (verify
 * functionality)
 * </UL>
 * @author M. Eric DeFazio eric@varcode.io
 */
//In BindML "()" is optional
//  {$$removeEmptyLines$$}
//  {$$checksum$$} <-- the name "checksum" is "bound" to a Directive in the VarContext 
//  {$$ex.myproject.MyCode$$}  <-- a class that implements Directive 
//                               either with static Field "INSTANCE"
//                               or 
public class AuthorDirective
    extends Mark
{
    /** the name of the Directive to call */
    private final String name;

    public AuthorDirective( String text, int lineNumber, String directiveName )
    {
        super( text, lineNumber );
        this.name = directiveName;
    }

    public String getName()
    {
        return name;
    }
}
