usage
=====

```bash
javac example/Example.java # compile
java -Xmx1024m  -jar swan.jar -t example.Example -P . # instrumentation
java -jar swan.jar --record --test-case "example.Example" -P ./transformed_version_example_Example
```

```bash
javac stringbuffer/StringBufferTest.java # compile
java -Xmx1024m  -jar swan.jar -t stringbuffer.StringBufferTest -P . # instrumentation
```
