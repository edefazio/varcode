<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
"authors" java source code at runtime:
```java
_class hello = _class.of("HelloWorld")
    .method( "public static final void main(String[] args)",
        "System.out.println(\"Hello World !\");");
System.out.println( hello );        
```
...will print out:
```java
public class HelloWorld
{
    public static final void main( String[] args )
    {
        System.out.println("Hello World !");
    }
}
```
varcode makes it easy to ***compile, load and use "authored" code at runtime***:
```java
//"author", compile, load, and instantiate a new instance
Object authored = 
    _class.of( "AuthoredClass" )
        .imports( UUID.class )
        .method( "public String getId",
            "return UUID.randomUUID().toString();" )
        .instance();                
        
//invoke a method on the authored instance
System.out.println( Java.invoke( authored, "getId" ) );
```  
varcode will save you tons of time, you can 
***author, compile, load and unit test authored code in one step.*** 

```java
public static _class MyBean = 
    _class.of("public class MyBean implements Serializable")
        .imports( Serializable.class, Date.class )
        .field("private final Date date;")
        .constructor("public MyBean( Date date )",
            "this.date = date;")
        .method("public Date getDate()",
            "return this.date;");

public void testMyBeanSerializable()
{
    ObjectOutputStream oos = null;
    try 
    {
        Date d = new Date();
        //compile and load the authored class
        Class myBeanClass = MyBean.loadClass();            
        //create a new instance
        Object instance = Java.instance( myBeanClass, d );
            
        //serialize the instance to bytes    
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream( baos );
        oos.writeObject( instance );
                
        //deserialize from bytes        
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );                
        ObjectInputStream ois = 
            new AdHocObjectInputStream( 
                (AdHocClassLoader)myBeanClass.getClassLoader(), bais );
        Object deserialized = ois.readObject();
        
        //verify that the date field is the same        
        assertEquals( d, Java.invoke( deserialized, "getDate" ) );              
    }
    catch( Exception ex ) 
    {
        fail( "could not Serialize/ Deserialize Authored class" );
    }
}
```
varcode will get you up and running in no-time, it has minimal dependencies (SLF4J), 
and will work on modern JDKs (1.6 or later), and is a natural fit within your editor 
of choice (eclipse, IntelliJ, Netbeans, notepad), or within frameworks and tools 
(like Spring) or build scripts like maven or gradle.  

varcode a great alternative to an in-house "roll your own" code generator for everything from
simple javabeans, to complex multi-class workspaces with interdependencies
```java        
// author new source code (a new .java file)
_class c = 
    _class.of( "com.foo", "public class Bar" )
      .field("private Long foo;")
      .field("private double bar;")
      .method("public Long getFoo()",
        "return foo;" )
      .method("public void setFoo( Long foo )",
        "this.foo = foo;")
      .method("public double getBar()",
        "return bar;")
      .method("public void setBar( double bar )",
        "this.bar = bar;")
      .nest(
        _enum.of("public enum REPORT_COLUMNS")
          .value("FOO_BAR", _literal.of("fooBar"), true )
          .field("private final String column;")
          .field("private final boolean filterable;")
          .constructor(
            "private REPORT_COLUMNS(String column, boolean filterable)",
            "this.column = column;",
            "this.filterable = filterable;")
          .method("public String getColumn()",
            "return column;")
          .method("public boolean isFilterable()",
            "return filterable;")
      );    
      
//now compile the generated .java source (to a class) and load it  
Class theClass = c.toJavaCase().loadClass();
  
//create a new instance
Object inst = Java.instance(theClass);
    
//Test methods on the authored instance
Java.invoke( inst, "setFoo", 12345L );
assertEquals( 12345L, Java.invoke(inst, "getFoo" ) );   
```
The generated .java source
```java
package com.foo;

public class Bar
{
    private double bar;
    private Long foo;

    public void setBar( double bar )
    {
        this.bar = bar;
    }
    public double getBar(  )
    {
        return bar;
    }
    public void setFoo( Long foo )
    {
        this.foo = foo;
    }
    public Long getFoo(  )
    {
        return foo;
    }
    public enum REPORT_COLUMNS
    {
        FOO_BAR( "fooBar", true );

        private final String column;
        private final boolean filterable;
        private REPORT_COLUMNS( String column, boolean filterable )
        {
            this.column = column;
            this.filterable = filterable;
        }

        public boolean isFilterable(  )
        {
            return filterable;
        }
        public String getColumn(  )
        {
            return column;
        }
    }
}
```
