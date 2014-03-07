usage
=====

* Example 1: example.Example

```bash
javac -cp ./orig_version orig_version/example/Example.java # compile
java -Xmx1024m -jar swan.jar --transform example.Example --class-path ./orig_version # instrumentation
java -jar swan.jar --record --test-case "example.Example" --class-path ./transformed_version_example_Example # record an buggy execution
java -jar swan.jar --replay --test-case "example.Example" --class-path ./transformed_version_example_Example --trace ./orig.trace.gz # reproduce the buggy execution
```

The above operations should be performed after a bug is reported and before a fix. Therefore, they do not belong to Swan. Since Swan needs the buggy execution these operations output, we provide the features for users. Nowadays, there are many techniques to reproduce bugs, which can be used here.

```bash
javac -cp ./insufficiently_patched_version insufficiently_patched_version/example/Example.java # compile
java -Xmx1024m -jar swan.jar --transform example.Example --class-path ./insufficiently_patched_version --patch :22,:27 # instrumentation with patch
java -jar swan.jar --replay-record --test-case "example.Example" --class-path ./transformed_version_example_Example --trace ./orig.trace.gz # reproduce the buggy execution and record another one that contain the patch information
java -jar swan.jar --generate --trace ./orig_patch.trace.gz # re-generate the orig trace using synchronization information
```

* Example 2: stringbuffer.StringBufferTest

```bash
javac -cp ./orig_version orig_version/stringbuffer/StringBufferTest.java # compile
java -Xmx1024m  -jar swan.jar -t stringbuffer.StringBufferTest -P ./orig_version # instrumentation
java -jar swan.jar -r -c "stringbuffer.StringBufferTest" -P ./transformed_version_stringbuffer_StringBufferTest # record an buggy execution
java -jar swan.jar -R -c "stringbuffer.StringBufferTest" -P ./transformed_version_stringbuffer_StringBufferTest -T ./orig.trace.gz # reproduce the buggy execution
```

```bash
javac -cp ./insufficiently_patched_version insufficiently_patched_version/stringbuffer/StringBufferTest.java # compile
java -Xmx1024m -jar swan.jar -t stringbuffer.StringBufferTest -P ./insufficiently_patched_version -p :443,:448 # instrumentation with patch
java -jar swan.jar -e -c "stringbuffer.StringBufferTest" -P ./transformed_version_example_Example -T ./orig.trace.gz # reproduce the buggy execution and record another one that contain the patch information
```
