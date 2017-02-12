<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
varcode combines a **code generator** and **ad-hoc tools** to **compile, load, and run .java source code at runtime**.  

## best of both worlds (statically typed code & dynamic runtime behavior) ##
varcode extends what traditional "code generators" do by letting you **compile, load and use** generated .java code in one step. 

varcode works by **invoking the compiler on dynamic java code at runtime**. _(don't worry, we can compile 1000s of classes sub-second, and after compiling the code is regular bytecode.)_    

## how to generate and use .java code at runtime ##
first generate a model: 
```java
_class _model = _class.of( "package mymodel;", 
    "public class Model" )
    .imports( UUID.class )
    .method( "public String createId()",
        "return UUID.randomUUID().toString();" );
```
create a **new instance**:
```java
Object dynamicModel = _model.instance();
```
**run** methods on the dynamic instance:
```java
String id1 = (String)Java.call( dynamicModel, "createId" );    
```
export the **.java source** and **.class** files:
```java
//export "C:\MyApp\src\main\java\mymodel\Model.java"
Export.dir( "C:\\MyApp\\src\\main\\java\\").toFile( _model );

//export "C:\MyApp\traget\classes\mymodel\Model.class"
Export.dir( "C:\\MyApp\\target\\classes\\").toFile( dynamicModel.getClass() );
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

## construct classes step by step##
classes for ( [\_class](https://gist.github.com/edefazio/b491989cd6ef72ad7ea2bc0005895c81), [\_interface](https://gist.github.com/edefazio/adbbd9cd500617d3202b2a2a3c7ebf68), [\_enum](https://gist.github.com/edefazio/0e566868ab5f134720cfde6db24b9b11), [\_annotationType](https://gist.github.com/edefazio/f1bed02ff66524149c215311c6d6f356) ) can be built in a single compound statement or incrementally using simple mutator methods. 
```java 
_class _c = _class.of( "package ex.mutable;",
    _imports.of( Serializable.class ),
    "@Deprecated",     
    "public class MyMutableModel implements Serializable" );
    
_method _m = _method.of( "/** create a random number */", 
    "@Generated",
    "public static final double random()", 
    "return Math.random();" );
    
_c.method( _m );   //add the "random" method to the _class  
    
_field _f = _field.of( "/** field javadoc */", 
    "public static int ID = 100;"); 
    
_c.add( _f ); //add the "ID" field to the _class
```    

## read in, modify, and run existing code ##
varcode makes **metaprogramming** easy. **load a  ```( _class, _enum, _interface, _annotationType )```** from an existing class, modify it, then compile, instantiate and invoke methods on it (no restarting required). 

```java
// 1. build the _class model from the .java source of the Class
_class _c = Java._classFrom( OriginalClass.class ); 

// 2. modify the model
_c.setName("Tailored");// change the class Name
_c.field("private static final int ID = 100;");

// get and modify the "toString" method
_c.getMethod("toString").body( "return getClass().getSimpleName() + ID;")

// 3. compile, instantiate and use it
Object tailored = _c.instance(); // create a new instance of "Tailored"
System.out.println( tailored );  //prints "Tailored100"
```
