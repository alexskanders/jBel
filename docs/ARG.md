# Arg
The Arg package contains classes for easily reading in arguments from the command line, and from a file.

- [Args](#Args)
- [ArgFile](#ArgFile)

## Args

Args takes the command line `String[] argValues` as well as any amount of strings, or a string array `String... argKeys` and maps the given values to the given keys. 

- When parsing the values Args will look for any value starting with `-` to designate a Args key, a non `-prefixed` value after would be its corresponding value.

~~~
-name value // name=value
~~~

- When a `-prefixed` key is followed by another `-prefixed` key then the first value is flagged as a true value

~~~
-name -name2 value // name=FLAG name2=value
~~~

- In a more complete example you can see how the following values map to the given keys
~~~javascript
  argValues = '-file text.txt -secure -size 1000'
  argKeys   = ["file", "secure", "size"]

  // Would have the map

  {
    file: "text.txt"
    secure: FLAG
    size: "1000" // All values map to String
  }
~~~

- To get a value from Args just use `Args.get(key)` to get the corresponding value. If the given key was not in the key list a Exception will be thrown, If the value was not given by the user, or was just a flagged then null will be returned.
 
- To get a flag value use `Args.isTrue(key)` this returns true if the value was flagged, or false if it was either missing or given a distinct value.

- To check a missing value use `Args.isMissing(key)` this returns true if the value was not supplied or flagged by a user.

- When dealing with keys (`-prefixed`) Args will ignore the `-`. Meaning when using `Args.get()` either `Args.get("-file")` or `Args.get("file")` will work. This is the same when declaring the key list `["file", "-secure", "-size"]` will work with `Args.get("secure")`


### Full Example

~~~javascript
public static void main(String[] args)
{
  Args argsList = Args.parse(args, "file", "secure", "size");

  if (argsList.isMissing("-file") || argsList.isMissing("size")) // Using file or -file would work interchangeably
    throw new RunTimeException("Missing parameters!")

  if (argsList.isTrue(secure))
    openSecurely(Args.get("file"), argsList.get("size"))
  else
    open(argsList.get("file"), argsList.get("size"))
}
~~~

## ArgFile

ArgFile parses a file for `key=value` on seprate lines. File format is as follows

~~~
key=value
key2=value2
key3=value3
~~~ 

- Trailing spaces are NOT trimmed. As such do not leave spaces around `=`

~~~
file= file.txt // This gives file a value of " file.txt" WITH leading space
~~~

- ArgFile will store all values in `char[]` to allow for Stringless retrieval of values. Values are read from the file into a `char[]` buffer then removed from the buffer.

- ArgFile implements `AutoCloseable` and will zero out all values when closed. Either use a try-with-resources block (Highly Recommended) or use .close() direclty (Not recommended as may lead to cases where ArgFile is not closed)


~~~javascript
  //Recomended

  try (ArgFile argFile = ArgFile.parse("argFile.txt"))
  {
    ...
  }

  //Not recomended

  ArgFile argFile = ArgFile.parse("argFile.txt")
    ...
  argFile.close();
~~~

- To get values as a char[] use `argFile.get(key)`

- There is a helper function to get char arrays as strings `argFile.copyAsString(key)`, BUT NOTE: This copies the char TO A STRING, meaning even after argFile closes there is still a string copy of the value.
