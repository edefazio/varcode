<img src="https://github.com/edefazio/varcode/blob/master/varcode_greenOnWhite.png?raw=true" width="60"/>
***model, compile and run custom Java code at runtime***

build, compile and run a simple snippet of code:
```java 
Snippet product = Snippet.of( "(int a,int b){return a * b;}" );
int prod1 = (int)product.call(2,3);
int prod2 = (int)product.call(5,4);
```
model, compile, and use a new AdHoc Class at runtime:
```java
//model a new class at runtime 
_class _adHoc = _class.of( "AdHoc" ).imports(UUID.class)
    .method( "public String createId",
        "return UUID.randomUUID().toString();" );
        
//author and compile the model to a new .class and create and instance at runtime
Class adHocClass = _adHoc.instance();

//call a method on the instance
String id = (String)Java.call( adHocClass, "createId" );
    
// will set id to a UUID like "2184d924-780d-4203-9fff-fa26c0886fc4"    
```

