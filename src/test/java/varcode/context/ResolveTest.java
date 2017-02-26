package varcode.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
//import varcode.context.Resolve.SmartScriptResolver;
import varcode.author.Author;
import varcode.markup.Template;
import varcode.markup.bindml.BindML;
import varcode.context.resolve.VarScriptResolver.SmartScriptResolver;
import varcode.context.resolve.VarResolver.SmartVarResolver;
import varcode.java.model._fields._field;
import varcode.markup.form.Form;
import varcode.markup.forml.ForML;

public class ResolveTest
    extends TestCase
{    
    static final SmartScriptResolver ssr = SmartScriptResolver.INSTANCE;


    public void testSmartScriptResolver()
    {
        assertEquals(
            null, ssr.resolveScript( VarContext.of(), "not found", null ) );

        assertNotNull(
            ssr.resolveScript(
                VarContext.of( "A", new VarScript()
                {
                    public Object eval( Context context, String input )
                    {
                        return null;
                    }

                    public void collectAllVarNames( Set<String> collection,
                        String input )
                    {
                    }
                } ),
                 "A", null ) );

        assertNotNull(
            ssr.resolveScript( VarContext.of(), "java.util.UUID.randomUUID", null ) );
    }

    public void testResolveDotPath()
    {
        Context context = VarContext.of( "field.name", "count" );
        assertEquals( "count", context.resolveVar( "field.name" ) );
        
        Template t = BindML.compile( "{+field.type+} {+field.name+}");
        String str = 
            Author.toString( t, "field.type", int.class, "field.name", "count" );
        
        assertEquals( "int count", str );
        
        Template tf = BindML.compile( "{{+:{+field.type+} {+field.name+}, +}}");
        
        str = 
            Author.toString( tf, "field.type", int.class, "field.name", "count" );
        assertEquals( "int count", str );
        
        str = 
            Author.toString( tf, 
                "field.type", new Object[]{int.class, String.class}, 
                "field.name", new String[]{"count", "name"} );
        
        System.out.println( str );
        
        assertEquals( "int count, java.lang.String name", str );        
    }
    
    public void testFormResolveDot()
    {
        Form f = ForML.compile("{+field.type+} {+field.name+}, ");
        
        VarContext vc = VarContext.of(
            "field.type", new Object[]{int.class, String.class}, 
            "field.name", new String[]{"count", "name"} );
        
        assertEquals( 2, f.getCardinality( vc ) );
        
        String s = f.author( vc );
        assertEquals("int count, java.lang.String name", s);
        //System.out.println("S : \""+ s +"\"" );        
    }
    
    public void testResolveDotDereference()
    {
        Context ctx = VarContext.of( "field", _field.of( "public int count;" ) ); 
        assertEquals("count", ctx.resolveVar( "field.name" ) );
    }
    public void testResolveDereferenceDot()
    {
        _field _count = _field.of( "public int count;" );
        
        
        Form f = ForML.compile("{+field.type+} {+field.name+}, ");
        
        String str = f.author( 
            VarContext.of("field", _count ) );
         
        assertEquals( "int count", str );
    }

    /**
     * Verify that I can resolve variables within the context 
     * using a "." notation
     * 
     * "
     */
    public void testResolveDereferenceDotArray()
    {
        _field _count = _field.of( "public int count;" );
        _field _name = _field.of( "public String name;" );
        
        Form f = ForML.compile( "{+field.type+} {+field.name+}, ");
        
        assertEquals( 2, f.getCardinality( 
            VarContext.of( "field", new _field[]{_count, _name} ) ) );
        
        String str = f.author( 
            VarContext.of( "field", new _field[]{_count, _name} ) );
        
        assertEquals( "int count, String name", str );
    }

    /**
     * Verify that I can resolve variables within the context 
     * using a "." notation
     * 
     * "
     */
    public void testResolveDereferenceDotCollection()
    {
        _field _count = _field.of( "public int count;" );
        _field _name = _field.of( "public String name;" );
        
        Form f = ForML.compile( "{+field.type+} {+field.name+}, ");
        
        List<_field> l = new ArrayList<_field>();
        l.add( _count );
        l.add( _name );
        
        assertEquals( 2, f.getCardinality( 
            VarContext.of( "field", l ) ) );
        
        String str = f.author( 
            VarContext.of( "field", l ) );
        
        assertEquals( "int count, String name", str );
    }
    
    
        
    public void testCaps()
    {
        Context context = VarContext.of( "name", "eric" );
        SmartVarResolver svr = SmartVarResolver.INSTANCE;
        
        //assertEquals( "ERIC", svr.resolveVar( context, "NAME" ) );
        assertEquals( "eric", context.resolveVar( "name" ) );
        assertEquals( "Eric", context.resolveVar( "Name" ) );
        assertEquals( "ERIC", context.resolveVar( "NAME" ) );
        
        //test mid caps
        context = VarContext.of( "fieldName", "length" );
        assertEquals( "length", context.resolveVar( "fieldName" ) );
        assertEquals( "Length", context.resolveVar( "FieldName" ) );
        assertEquals( "LENGTH", context.resolveVar( "FIELDNAME" ) );
        
    }
    
    public void testSmartVarResolver()
    {
        //verify bindings in the Context take precedence
        System.setProperty( "A", "Setski" );
        assertEquals( 1,
            SmartVarResolver.INSTANCE.resolveVar( VarContext.of( "A", 1 ), "A" ) );

        //verify we can get System Properties
        System.setProperty( "A", "Setski" );
        assertEquals( "Setski",
            SmartVarResolver.INSTANCE.resolveVar( VarContext.of(), "A" ) );

        //we can get values that are set in expressions
        Template d = BindML.compile( "{+A+}" );
        assertEquals( "Setski", Author.toString( d ) ); //verify I can resolve a variable that is set as expression

        d = BindML.compile( "{+A+}" );
        TreeSet<Integer> ts = new TreeSet<Integer>();
        ts.add( 1 );
        ts.add( 2 );
        ts.add( 3 );
        ts.add( 4 );
        assertEquals( "1, 2, 3, 4", Author.toString( d, "A", ts ) );

        /*
        //I need a Better way of printing Maps 
        // right now = {A=1, B=2, C=3}
        // should be A=1; B=2; C=3;
        Map<String, Integer> m = new HashMap<String, Integer>();
        m.put( "A", 1 );
        m.put( "B", 2 );
        m.put( "C", 3 );
        System.out.println( Author.toString( d, "A", m ) );

        d = BindML.compile( "{+A+}" );
        assertEquals( "1, 2, 3, 4", Author.toString( d ) ); //verify I can resolve a variable that is set as expression
*/

    }
}
