usage
=====

Example 1: example.Example
---------------------------

### original version (URLParse.java)

        synchronized String getVal(String key) {
		int keyPos = url.indexOf(key);
		int valPos = url.indexOf("=", keyPos) + 1;
		int ampPos = url.indexOf("&", keyPos);
		if (ampPos < 0)
			ampPos = url.length();

                                                                   void replaceVal(String key, String newVal) {
                                                                           int keyPos = url.indexOf(key);
                                                                           int valPos = url.indexOf("=", keyPos) + 1;
                                                                           int ampPos = url.indexOf("&", keyPos);
                                                                           if (ampPos < 0)
                                                                               ampPos = url.length();
                                                                           url = url.substring(0, valPos) 
                                                                                       + newVal + url.substring(ampPos);
                                                                   }

		return url.substring(valPos, ampPos);
        }

### insufficiently patched version

        synchronized String getVal(String key) {
		int keyPos = url.indexOf(key);
		int valPos = url.indexOf("=", keyPos) + 1;
		int ampPos = url.indexOf("&", keyPos);
		if (ampPos < 0)
			ampPos = url.length();

                                                                   void replaceVal(String key, String newVal) {
                                                                           int keyPos = url.indexOf(key);
                                                                           int valPos = url.indexOf("=", keyPos) + 1;
                                                                           int ampPos;
                                                                           synchronized(this){

                                                                           ampPos = url.indexOf("&", keyPos);
                                                                           if (ampPos < 0)
                                                                               ampPos = url.length();
                                                                           }
                                                                           url = url.substring(0, valPos) 
                                                                                       + newVal + url.substring(ampPos);
                                                                   }

		return url.substring(valPos, ampPos);
        }

### how to examine it

```bash
# 1. compile
javac -cp ./orig_version orig_version/example/Example.java 
# 2. instrumentation
java -Xmx1024m -jar swan.jar --transform example.Example --class-path ./orig_version 
# 3. record an buggy execution
java -jar swan.jar --record --test-case "example.Example" --class-path ./transformed_version_example_Example 
# 4. reproduce the buggy execution (optional)
java -jar swan.jar --replay --test-case "example.Example" --class-path ./transformed_version_example_Example --trace ./orig.trace.gz 
```

The above operations should be performed after a bug is reported and before a fix. Therefore, they do not belong to Swan. Since Swan needs the buggy execution these operations output, we provide the features for users. Nowadays, there are many techniques to reproduce bugs, which can be used here.

```bash
# 1. compile
javac -cp ./insufficiently_patched_version insufficiently_patched_version/example/Example.java 
# 2. instrumentation with patch
#    if you synchronize a method, use the last line number (the line number of '}') as the argument, e.g.
#    java -Xmx1024m -jar swan.jar --transform example.Example --class-path ./sufficiently_patched_version --patch :27
#    if swan does not report that it detects your patched synchronization, please read soot's documents to see rules of line numbers.
java -Xmx1024m -jar swan.jar --transform example.Example --class-path ./insufficiently_patched_version --patch :22,:26 
# 3. reproduce the buggy execution and record another one that contain the patch information
java -jar swan.jar --replay-record --test-case "example.Example" --class-path ./transformed_version_with_patches_example_Example --trace ./orig.trace.gz 
# 4. re-generate the orig trace using synchronization information
java -jar swan.jar --generate --trace ./orig_patch.trace.gz 
# 5. replay to examine fixes
java -jar swan.jar --replay-examine --test-case "example.Example" --class-path ./transformed_version_with_patches_example_Example --trace ./new.1.trace.gz 
```

### sufficiently patched version


        synchronized String getVal(String key) {
		int keyPos = url.indexOf(key);
		int valPos = url.indexOf("=", keyPos) + 1;
		int ampPos = url.indexOf("&", keyPos);
		if (ampPos < 0)
			ampPos = url.length();

                                                                   synchronized void replaceVal(String key, String newVal) {
                                                                           int keyPos = url.indexOf(key);
                                                                           int valPos = url.indexOf("=", keyPos) + 1;
                                                                           int ampPos = url.indexOf("&", keyPos);
                                                                           if (ampPos < 0)
                                                                               ampPos = url.length();
                                                                           url = url.substring(0, valPos) 
                                                                                       + newVal + url.substring(ampPos);
                                                                   }

		return url.substring(valPos, ampPos);
        }

Example 2: stringbuffer.StringBufferTest
------------------------------------------

### original version (StringBuffer.java)

        synchronized StringBuffer delete(int start, int end) {
            ...
                                                                    synchronized StringBuffer append(StringBuffer sb) {
                                                                            ...
                                                                            int newcount = *count* + len;
            *count* -= len;
                                                                              
                                                                            if (newcount > value.length)
                                                                                 ...
                                                                                 sb.getChars(0, len, value, *count*);
                                                                                 ...
                                                                            }
            ...
            return this;
        }


### insufficently patched version

        synchronized StringBuffer delete(int start, int end) {
            ...
                                                                    synchronized StringBuffer append(StringBuffer sb) {
                                                                            ...
                                                                            int newcount = *count* + len;
            *count* -= len;
                                                                            synchronized(sb){  
                                                                            if (newcount > value.length)
                                                                                 ...
                                                                                 sb.getChars(0, len, value, *count*);
                                                                                 ...
                                                                            }
                                                                            }
            ...
            return this;
        }

### how to examine it

```bash
# 1. compile
javac -cp ./orig_version orig_version/stringbuffer/StringBufferTest.java 
# 2. instrumentation
java -Xmx1024m  -jar swan.jar -t stringbuffer.StringBufferTest -P ./orig_version 
# 3. record an buggy execution
java -jar swan.jar -r -c "stringbuffer.StringBufferTest" -P ./transformed_version_stringbuffer_StringBufferTest 
# 4. reproduce the buggy execution (optional)
java -jar swan.jar -R -c "stringbuffer.StringBufferTest" -P ./transformed_version_stringbuffer_StringBufferTest -T ./orig.trace.gz 
```

```bash
# 1. compile
javac -cp ./insufficiently_patched_version insufficiently_patched_version/stringbuffer/StringBufferTest.java 
# 2. instrumentation with patch
java -Xmx1024m -jar swan.jar -t stringbuffer.StringBufferTest -P ./insufficiently_patched_version -p :443,:448
# 3. reproduce the buggy execution and record another one that contain the patch information
java -jar swan.jar -e -c "stringbuffer.StringBufferTest" -P ./transformed_version_with_patches_stringbuffer_StringBufferTest -T ./orig.trace.gz 
# 4. re-generate the orig trace using synchronization information
java -jar swan.jar -g -T ./orig_patch.trace.gz 
# 5. replay to examine fixes
java -jar swan.jar --replay-examine --test-case "stringbuffer.StringBufferTest" --class-path ./transformed_version_with_patches_stringbuffer_StringBufferTest --trace ./new.1.trace.gz 
```

### sufficiently patched version

        synchronized StringBuffer delete(int start, int end) {
            ...
                                                                    synchronized StringBuffer append(StringBuffer sb) {
                                                                            ...
                                                                            synchronized(sb){ 
                                                                            int newcount = *count* + len;
            *count* -= len;
                                                                             
                                                                            if (newcount > value.length)
                                                                                 ...
                                                                                 sb.getChars(0, len, value, *count* );
                                                                                 ...
                                                                            }
                                                                            }
            ...
            return this;
        }

