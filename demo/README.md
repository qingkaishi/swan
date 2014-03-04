usage
=====

* Example 1: example.Example

```bash
javac example/Example.java # compile
java -Xmx1024m  -jar swan.jar --transform example.Example --class-path . # instrumentation
java -jar swan.jar --record --test-case "example.Example" --class-path ./transformed_version_example_Example # record an buggy execution
```

* Example 2: stringbuffer.StringBufferTest

```bash
javac stringbuffer/StringBufferTest.java # compile
java -Xmx1024m  -jar swan.jar -t stringbuffer.StringBufferTest -P . # instrumentation
java -jar swan.jar -r -c "stringbuffer.StringBufferTest" -P ./transformed_version_stringbuffer_StringBufferTest # record an buggy execution
```
