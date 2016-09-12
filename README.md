![alt text](https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true "Ad Hoc Source Code Generation & metaprogramming")
ad-hoc source code generation and metaprogramming (Java)
```java
// build a model of the new .java class
_class c = _class.of( "public class HelloInstance" )
.constructor( "public HelloInstance( String name )",
    "System.out.println( \"Hello \" + name + \"!\" );" );
        
// "author" the .java source  
JavaCase helloCase = c.toJavaCase( );
        
//print the ,java source to the console
System.out.println( helloCase );
        
// compile (javac) the .java to a .class, 
// load the .class
// call the constructor with "Eric" to create a new instance
Object helloInstance = helloCase.instance( "Eric" );
```
...will print the generated source:
```java
public class HelloInstance
{
    public HelloInstance( String name )
    {
        System.out.println( "Hello " + name + "!" );
    }
}
```
...will print "Hello Eric!" since it loaded and constructed 
an instance of the "authored" class at runtime.

varcode is the easiest code generator for Java.  
It is natural and intuitive, and it also lets you compile,
load, use and test adhoc classes interactively.

here is larger example:
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
    
//invoke methods on the instance
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
