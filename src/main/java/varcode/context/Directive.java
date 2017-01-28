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
package varcode.context;

/**
 * Marker interface for Directives, which are run during the Lifecycle 
 * (PreProcessing, PostProcessing) of Authoring documents by combining a 
 * {@link varcode.markup.Template}s with a {@link varcode.context.Context}
 * LifeCycle Code run: 
 * <UL>
 *  <LI>PreProcess - before to authoring the Document into text 
 *  <LI>PostProcess - after authoring the Document into text
 * </UL>
 * @author M. Eric DeFazio eric@varcode.io
 */
public interface Directive
{
   
}