package tutorial.varcode.chap2.markup;

import junit.framework.TestCase;
import varcode.doc.Compose;
import varcode.dom.Dom;
import varcode.markup.bindml.BindML;

/**
 * BindML is capable of modeling any structured text (not just Java files)
 * Here is an example of creating a Class / Model {@code MavenPomDependency}
 * to represent an XML dependency within a Maven POM file.
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public class _2_BindML_PomDependency
    extends TestCase
{
    public static String N = System.lineSeparator();
    
    /** This is a model of a Maven Pom dependency */
    public static class MavenPomDependency        
    {
        public static final Dom Dom = 
            BindML.compile(
                "<dependency>" + N + 
                "    <groupId>{+groupId*+}</groupId>" + N +
                "    <artifactId>{+artifactId*+}</artifactId>" + N +
                "    <version>{+version*+}</version>" + 
                "{{+?type:" + N + 
                "    <type>{+type+}</type>" +        
                "+}}{{+?scope:" + N + 
                "    <scope>{+scope+}</scope>" + N +
                "+}}" + N +  
                "</dependency>"           
            );
        
        private String groupId;
        private String artifactId;
        private String version;
        private String type;
        private String scope;

        public String getGroupId() 
        {
            return groupId;
        }

        public MavenPomDependency setGroupId(String groupId) 
        {
            this.groupId = groupId;
            return this;
        }

        public String getArtifactId() 
        {
            return artifactId;
        }

        public MavenPomDependency setArtifactId(String artifactId) 
        {
            this.artifactId = artifactId;
            return this;
        }

        public String getVersion() 
        {
            return version;
        }

        public MavenPomDependency setVersion(String version) 
        {
            this.version = version;
            return this;
        }

        public String getType() 
        {
            return type;
        }

        public MavenPomDependency setType(String type) 
        {
            this.type = type;
            return this;
        }

        public String getScope() 
        {
            return scope;
        }

        public void setScope(String scope) 
        {
            this.scope = scope;
        }
        
        public String compose()
        {
            return Compose.asString( Dom, 
                "groupId", this.groupId,
                "artifactId", this.artifactId,
                "version", this.version,
                "type", this.type,
                "scope", this.scope );
        }
        
        public String toString()
        {
            return compose();
        }
    }
    
    public void testBindMLPomDependency()
    {
        MavenPomDependency dep = new MavenPomDependency();
        dep.setGroupId( "junit" )
           .setArtifactId( "junit")
           .setVersion( "4.11" );
        System.out.println( dep );
        
        dep.setType( "jar" );
        System.out.println( dep );
        dep.setScope( "test" );
        System.out.println( dep );        
    }
    
    //concepts
    // 1) BindML allows binding variables into any structured text Documents
    //    (not just Java files)
    // 2) The {{+?<name>:...+}} Mark represents a conditional pattern... if there
    //    is a non-null value for <name> then the pattern (inside the ...) is 
    //    evaluated
    // 3) Compose.asString(...) takes a (Compiled) Dom and key Value Pairs
    //    and Composes / Binds a target document. 
} 
