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
package varcode.java.draft;

import junit.framework.TestCase;
import varcode.java.Java;
import varcode.java.model._enum;
import varcode.java.draft._draft.*;

/**
 * Rough Draft -> changes
 *
 * <LI>
 _workspaceMacro wsm = _draft.match( "all classes in package...", Type.Class,
 Name.contains, Name.statsWith ) .removeField( "LOG" ) .removeImport( "
 .remove

 _macroEnum.of( MyClass.class ) .removeField( "fieldName" ) .removeField( _f )
 .addField("public static final MASK =


 This is something else So I could pass in something that would: 1) Match a
 particular method MatchMethod 2) Match a particular sequence of code
 MatchCodeSequence 3)
 *
 * @author M. Eric DeFazio
 */
@imports(remove={"varcode.java", "junit"},add={"java.util.Map", "java.util.UUID", "{+addImports+}"})
public enum _enumDraft
{
    INSTANCE;
    
    //TODO parameterize the class
    @sig("public enum {+Name+}")
    public enum ClassName
    {
        INSTANCE;
    }

    @packageName("ex.mypackage")
    public enum EPackageStatic
    {
        INSTANCE;
    }

    @packageName("ex.mypackage.{+subpkg+}")
    public enum ExpandPackage
    {
        INSTANCE;
    }

    @staticBlock("System.out.println( \"Hello from Static Block\");")
    public enum ExpandStaticBlock
    {
        INSTANCE;
    }

    @Deprecated
    @annotations(remove ={"Deprecated"}, add ={"@Drafted", "{+classAnnotations+}"})
    public enum ExpandClassAnnotation
    {
        INSTANCE;
    }

    @interface Draft
    {
    }

    @sig("public static class TheClass")
    @Deprecated
    @Draft
    public enum CopyClassAnnotations
    {
        INSTANCE;
    }

    public enum CopyStaticBlock
    {
        INSTANCE;
        static
        {
            System.out.println( "In static Block" );
        }
    }

    @sig("public enum {+Name+}")
    public enum ClassSig
    {
        INSTANCE;
    }

    @fields("public static {+type+} {+name+};")
    public enum ClassFields
    {
        INSTANCE;
    }

    @fields({"public static final int {+name+}Dim;", "private static int id;"})
    public enum ClassFieldsMultiple
    {
        INSTANCE;
    }

    public enum AField
    {
        INSTANCE;
        
        @$({"x", "name"})
        public int x;
    }

    public enum CopyMethod
    {
        INSTANCE;
        
        public static String sourceMethod()
        {
            return "STRING";
        }
    }

    public enum ParameterizeMethod
    {
        INSTANCE;
        
        @$({"name", "pname"})
        public static String aMethod( String name )
        {
            return "Hello " + name;
        }
    }

    public enum MethodSigAndBody
    {
        INSTANCE;
        
        @sig("public static final void itBurns()")
        @body("System.out.println( \" HI \" );")
        void original()
        {
        }
    }

    public enum MethodSigOnly
    {
        INSTANCE;
        
        @sig("public static final void itBurns()")
        void original()
        {
        }
    }

    public enum MethodBodyOnly
    {
        INSTANCE;
        
        @body("System.out.println( \" HI \" );")
        void original()
        {
        }
    }

    @fields("public String {+name+} = {+$quote(name)+};")
    public enum MethodForm
    {
        INSTANCE;
        
        @form({"sb.append( {+name+} );", "sb.append(System.lineSeparator());"})
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append( this.getClass().getName() );
            form:
            sb.append( "Each of the fields will be printed here, this will be replaced" );
            /*}*/
            return sb.toString();
        }
    }

}
