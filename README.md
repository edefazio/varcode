![alt text](https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true "Ad Hoc Source Code Generation & metaprogramming")

varcode can "author", compile and use Java source code at runtime:
```java
//author source for a new class, compile & load it, create a new instance
static Object ez = _class.of( "EZClass" ).toJavaCase( ).instance( );
```
varcode is easy to learn, use and understand. It saves you time, and produces beautiful readable code.  

varcode's fluent model API is familiar and the code is easy to understand, test, and maintain.
```java
// 1) build a model of the new .java class
_class c = _class.of( "public class HelloWorld" )
    .constructor( "public HelloWorld( String name )",
        "System.out.println( \"Hello \" + name + \"!\" );" );
        
// 2) "author" the .java source from the model 
JavaCase helloCase = c.toJavaCase( );
        
// 3) print the .java source to the console
System.out.println( helloCase );
        
// 4) compile (javac) the .java source to a .class, 
// load the .class
// call the `HelloInstance` constructor with "Eric" to create a new instance
Object helloInstance = helloCase.instance( "Eric" );
```
...will print the generated source:
```java
public class HelloWorld
{
    public HelloWorld( String name )
    {
        System.out.println( "Hello " + name + "!" );
    }
}
```
...will print `Hello Eric!` since it ***compiled, loaded, and constructed*** 
a new instance of `HelloWorld` class at runtime.

varcode will get you up and running in no-time, it has minimal dependencies (SLF4J), 
and will work on modern JDKs (1.6 or later), and is easily used from within build scripts
like maven or gradle.  

varcode a great alternative to an in-house "roll your own" code generator for everything from
simple javabeans, to complex workspaces 
(multiple generated Java classes, enums, and interfaces
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
