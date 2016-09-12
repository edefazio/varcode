![alt text](https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true "Ad Hoc Source Code Generation & metaprogramming")
ad-hoc source code generation and metaprogramming (Java)
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
