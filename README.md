<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
helps you ***model, "author", compile and run custom java source code at runtime***:
```java
String id = (String) Java.invoke(
    _class.of( "AdHoc" )
        .imports( UUID.class )
        .method( "public String createId",
            "return UUID.randomUUID().toString();" )
        .instance(), 
    "createId" );       
    
// will set id to a UUID like "2184d924-780d-4203-9fff-fa26c0886fc4"    
```
to explain:

1a) First we create a `adHocModel` for a Java class: 
```java
_class adHocModel = _class.of( "AdHoc" )
    .imports( UUID.class )
    .method( "public String createId",
        "return UUID.randomUUID().toString();" );
```
1b) We can print out the .java source of `adHocModel`
```java
System.out.println( adHocModel );

//prints to the console:
import java.util.UUID;

public class AdHoc
{
    public String createId(  )
    {
        return UUID.randomUUID().toString();
    }
}
```
2) calling the `adHocModel.instance( Object...args )` method :
```java
Object adHocInstance = adHocModel.instance( );
```
will: 
* pass the "AdHoc.java" source to the runtime javac compiler to create a `AdHoc.class` file
* load the `AdHoc.class` file in a new ClassLoader
* create a new `adHocInstance` of `AdHoc.class` passing no arguments to the constructor

3) finally, calling `Java.invoke( Object instance, String methodName, Object...args )` method on `adHocInstance`: 
```java
String id = Java.invoke( adHocInstance, "createId" );
```
...will use reflection to call the `createId()` method on the `adHocInstance` passing in no arguments:

varcode has (2) major components:
* a model / templating API for "authoring" source code (for Java or any other text based language)
* an API for compiling, loading and executing "Ad Hoc" Java code at runtime.

varcode will save you tons of time, when generating code.
you can even ***author, compile, load and unit test code in one step.*** 

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
