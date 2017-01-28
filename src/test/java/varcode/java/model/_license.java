/*
 * Copyright 2016 Eric.
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
package varcode.java.model;

import varcode.context.VarContext;
import varcode.author.Author;
import varcode.context.Directive;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.java.model._Java;
import varcode.java.model._Java;

/**
 *
 * @author Eric
 */
public class _license
    implements _Java
{
    public static _license of( Template frame, String... keyValues )
    {
        return new _license( frame, keyValues );
    }

    public static final Template APACHE_2_0 = BindML.compile(
        "/*" + N
        + " * Copyright 2016 {+author+}." + N
        + " *" + N
        + " * Licensed under the Apache License, Version 2.0 (the \"License\");" + N
        + " * you may not use this file except in compliance with the License." + N
        + " * You may obtain a copy of the License at" + N
        + " *" + N
        + " *      http://www.apache.org/licenses/LICENSE-2.0" + N
        + " *" + N
        + " * Unless required by applicable law or agreed to in writing, software" + N
        + " * distributed under the License is distributed on an \"AS IS\" BASIS," + N
        + " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." + N
        + " * See the License for the specific language governing permissions and" + N
        + " * limitations under the License." + N
        + " */" + N );

    private Template frame;
    private VarContext context;

    public _license( Template frame, String... keyValues )
    {
        this.frame = frame;
        this.context = VarContext.of( (Object[])keyValues );
    }

    @Override
    public _Java replace( String target, String replacement )
    {
        return this;
    }

    @Override
    public void visit( ModelVisitor visitor )
    {
        visitor.visit(this);
    }

    @Override
    public String author()
    {
        return author( new Directive[ 0 ] );
    }

    @Override
    public String author( Directive... directives )
    {
        return Author.toString( frame, this.context, directives );
    }

}
