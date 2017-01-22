<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
## **model** based **source** code generation ##

varcode provides models of java language constructs _(a varcode ```_class``` models a java ```class```)_. 
we can build these model and **generate and run source code dynamically**.  
 
build a model:
```java
_class _model = _class.of( "package mymodel;", 
    "public class Model" )
    .imports( UUID.class )
    .method( "public String createId()",
        "return UUID.randomUUID().toString();" );
```
export the .java source of the model:
```java
Export.dir( "C:\\myapp\\src\\main\\java\\").toFile( _model );
```
...```Export(...)``` will create the file ```C:\\myapp\\src\\main\\java\\mymodel\\Model.java``` containing:
```java
package mymodel;
import java.util.UUID;

public class Model
{
    public String createId(  )
    {
        return UUID.randomUUID().toString();
    }
}
```
to create a **new instance** of the model dynamically:
```java
Object dynamicModel = _model.instance();
```
invoke methods on the dynamic instance:
```java
String id1 = (String)Java.call( dynamicModel, "createId" );
String id2 = (String)Java.call( dynamicModel, "createId" );     
```
##building & mutating models of models##
building a model for a ( [\_class](https://gist.github.com/edefazio/b491989cd6ef72ad7ea2bc0005895c81), [\_interface](https://gist.github.com/edefazio/adbbd9cd500617d3202b2a2a3c7ebf68), [\_enum](https://gist.github.com/edefazio/0e566868ab5f134720cfde6db24b9b11), [\_annotationType](https://gist.github.com/edefazio/f1bed02ff66524149c215311c6d6f356) ) can be done in a single compound statement, or by calling individual mutator methods on the model.  "components" of models are themseleves models, i.e. we model (```_methods, _fields,  _parameters, _annotations, _imports...```)
```java 
_class _c = _class.of("package ex.mutable;",
    _import.of(Serializable.class),
    "@Deprecated",     
    "public class MyMutableModel implements Serializable" );
    
_method _m = _method.of( "/** create a random number */", 
    "@Generated",
    "public static final double random()", 
    "return Math.random();" );
    
_field _f = _field.of( "/** field javadoc */", 
    "public static int ID = 100;"); 

_c.add( _m ); //add the "random" method to the _class 
_c.add( _f ); //add the "ID" field to the _class
```    

##metaprogramming##
to support metaprogramming, varcode can load the .java source of (class, enum, interface, or annotationType) and **build the  ```( _class, _enum, _interface, _annotationType )``` automatically**. 

the metaprogramming "process" in varcode is simple:
 1. load and build a model from the .java source 
 2. modify the model 
 3. compile, instantiate and use the "ad hoc" modified model

```java
// 1. build the _class model from the .java source
_class _c = Java._classFrom( OriginalClass.class ); //find the .java source 

// 2. modify the model
_c.setName("Tailored");// change the class Name on the model
_c.field("private static final int ID = 100;");
_c.getOnlyMethodNamed("toString").body( "return getClass().getSimpleName() + ID;") //modify the toString method

// 3. compile, instantiate and use the "adhoc" model
Object tailored = _c.instance(); // create a new instance of "Tailored"
System.out.println( tailored );  //prints "Tailored100"
```
