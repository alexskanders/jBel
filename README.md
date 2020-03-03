# jBel
## The Java BackEnd Library

[![license badge](https://img.shields.io/github/license/alexskanders/jBel?logo=apache)](https://github.com/alexskanders/jBel/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.skanders.jbel/jbel)](https://search.maven.org/search?q=g:%22com.skanders.jbel%22%20AND%20a:%22jbel%22)
[![javadoc](https://javadoc.io/badge2/com.skanders.jbel/jbel/javadoc.svg)](https://javadoc.io/doc/com.skanders.jbel/jbel)
[![Build Status](https://travis-ci.org/alexskanders/jBel.svg?branch=master)](https://travis-ci.org/alexskanders/jBel)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/alexskanders/jBel)](https://www.codefactor.io/repository/github/alexskanders/jbel/overview/master)

Maven:

~~~xml
    <dependency>
        <groupId>com.skanders.jbel</groupId>
        <artifactId>jbel</artifactId>
        <version>0.9.0</version>
    </dependency>
~~~

Gradle:
~~~java
    implementation 'com.skanders.jbel:jbel:0.9.0'
~~~

## Tools

- [Arg](#Arg)
- [AtSQL](#AtSQL)
- [Bytes](#Bytes)
- [Config](#Config)
- [Convert](#Convert)
- [Model](#Model)
- [Result](#Result)
- [Socket](#Socket)
- [Worker](#Worker)

## Arg

#### Args 

- Easily parse commandline args

~~~java
public static main void(String[] args) // args = ["-file", "myFile.txt", "-type", "text", "-secure"]
{
    Args comLine = Args.parse(args, "file", "type", "secure");
  
    if (comLine.isMissing("file", "type"))
        throw new RunTimeException("Missing arguments!");
  
    if (comLine.isTrue("secure"))
        secureOpen(comLine.get("file"), comLine.get("type"))
    else
        open(comLine.get("file"), comLine.get("type"))
}
~~~

#### ArgFile
 - Read lines from a file with automatic variable 'zero out' 
 - All values read in as char[] then cleared after `.close()` or `try-with-resources`

~~~java
// Argfile.txt:
// username=myUserName
// password=SecretPassword
public static main void(String[] args)
{
    String username;
    char[] password;
        
    try (ArgFile argFile = ArgFile.parse("argFile.txt"))
    {
        username = argFile.copyAsString("username"); // Creates a string copy
        password = argFile.get("password"); // Gives pointer to same char[]
        
        connect(username, password);
    }
    // username still exists here
    // password cleared here (Make sure to copy if needed!)
}
~~~

## AtSQL

#### AtSQLFactory

- Easily create AtSQL instances with HikariCP supplying connections and AtSQL managing the connections
- Powered by HikariCP and JDBC

~~~java
AtSQL atSQL = AtSQLFactory
    .newInstance("username", "password", /*lifeTime*/ 30000, /*maxPoolSize*/ 10)
    .withJdbcURL("jdbc:mysql://127.0.0.1:3306/db") // or .withDriver()
    .withMySQLPerformanceSettings() // HikariCP recommendations and Batch Statments
    .build();
~~~

#### AtSQLQuery

- Easily execute SQL updates without needing to manage connections and exceptions

~~~java
Resulted<Integer> resultedQuery = atSQL
    .createQuery("INSERT INTO student (id, name, date) VALUES (?,?, ?);")
    .setList(0, "Student", Timestamp.from(Instant.now()))
    .executeUpdate();
    
if (resultedQuery.notValid())
    System.err.println("Query failed! Reason: " + resultedQuery.toThrowable());
else
    System.out.println("Query completed! Rows updated: " + resultedQuery.value());
~~~

- Easily parse ResultSets

~~~java
AtSQLQuery atSQLQuery = atSQL
    .createQuery("SELECT name FROM student WHERE id = ?;")
    .setList(Types.INTEGER, 0); // may use .setList(Object...) or .set(Type, Object) for each value

try (Resulted<AtSQLResult> resultedQuery = atSQLQuery.executeQuery()) // Closes connectiontion after work
{
    if (resultedQuery.notValid())
        throw resultedQuery.toThrowable(); // Throws runtime exception SkandersException
    
    ResultSet rs = resultedQuery.value().getResultSet();
    
    while(rs.next())
       System.out.println(rs.getString("name"));
}
~~~

#### AtSQLBatch

- Easily execute SQL batch updates

~~~java
Resulted<int[]> resultedBatch = atSQL
    .createBatch("INSERT INTO student (id, name, date) VALUES (?,?, ?)")
    .setList(0, "Student Zero", Timestamp.from(Instant.now()))
    .setList(1, "Student One", Timestamp.from(Instant.now()))
    .setList(2, "Student Three", Timestamp.from(Instant.now()))
    .setList(3, "Student Four", Timestamp.from(Instant.now()))
    .setList(4, "Student Five", Timestamp.from(Instant.now()))
    .executeBatch();
    
if (resultedQuery.notValid())
    System.err.println("Query failed! Reason: " + resultedBatch.toThrowable());
else {
    System.out.println("Query completed!"));
    
    for (int i : resultedBatch.value())
        System.out.println("Status: " + i);
}
~~~

#### AtSQLMultiBatch

- Easily execute Multiple Update SQL statements

~~~java
Resulted<int[]> resultedBatch = atSQL.createMultiBatch()
    .setQuery("INSERT INTO classes (id, name) VALUES (?,?);")
    .setList(124, "CS") // Only one list per query for now
    .setQuery("INSERT INTO student (id, name, date) VALUES (?,?, ?);")
    .setList(0, "Student Zero", Timestamp.from(Instant.now())) 
    .setQuery("INSERT INTO enroll (id, studentId) VALUES (?,?);")
    .setList(124, 0)
    .executeBatch();
    
if (resultedQuery.notValid())
    System.err.println("Query failed! Reason: " + resultedBatch.toThrowable());
else {
    System.out.println("Query completed!"));
    
    for (int i : resultedBatch.value())
        System.out.println("Status: " + i);
}
~~~

## Bytes

#### SecureBytes

- Retrieve bytes using java's SecureRandom and encodes using google guava's encoders
- BaseEncoders: 16, 32, 32Hex, 64, 64Url

~~~java
byte[] randomBytes = SecureBytes.getBytes(64);

String base16 = SecureBytes.encode16(randomBytes);
byte[] decodedBytes = SecureBytes.decode16(base16);

String base32 = SecureBytes.gen32(64);
~~~

## Config

#### Config

- Reads in a yaml formated file and allows for easy value extraction
- Functions avaible for required and option values
- Mapping to Boolean, Integer, Long, Double, String, Lists, Maps, POJOs
- Allows for easily reading in encrypted values using JASYPT
- Powered by Jackson for parsing, and JASYPT for decryption

~~~java
// config.yaml
// keys:
//   key: enc=SECRETKEYENCRYPTED
// value:
//   stringRequired: StringValue!
//   intOptional: 100
// map:
//   valueOne: 1
//   valueTwo: 2
// list:
//   - 1
//   - 2
// pojo:
//   value:
//   map:
//     valueOne: 1
//     valueTwo: 2
//   array:
//     - 1
//     - 2
Config config = Config.fromEncrypted("config.yaml", "PBEWITHHMACSHA512ANDAES_256", "Password");

String key = Config.getReqStr("keys.key");
String stringValue = Config.getReqStr("value.string");

Integer intValue = Config.getInt("value.intOptional");
Integer defaultValue = Config.getInt("value.missingvalue", /* Default Value if missing. */ 10);

List<Integer> list = Config.getList("list", Integer.class);
Map<String, Integer> map = Config.getMap("map", String.class, Integer.class);
POJO pojo = Config.getPOJO("pojo", POJO.class);
~~~

## Convert

#### FromJson

- Acts as a wrapper for Jackson's JsonNode to help convert values easily
- Select JSON key easily with dot delimted argument "key.key.key.value"
- Mapping to Boolean, Integer, Long, Double, String, Lists, Maps, POJOs
- Powered by Jackson

~~~java
//json:
// {
//    student: {
//         id: 0,
//         name: "Student",
//         classes: [{
//             cs: {
//                 name: "Computer Science",
//                 units: 4
//             }
//             cs2: {
//                 name: "Computer Science Two",
//                 units: 4
//             }
//         ]
//    }
// }
JsonNode node = getJsonNode();

// When grabbing multiple entitys from same node use FromNode.toNode() to make parsing more effiecent
String str = FromNode.toStr(node, "student.name");
Integer id = FromNode.toInt(node, "student.id"); 

// More Effiecent
JsonNode student = FromNode.toNode(node, "student");
System.out.println(FromNode.toStr(student, "name"));
System.out.println(FromNode.toInt(student, "id"));

List<JsonNode> classNodes = FromNode.toList(node, "student.classes");

for (JsonNode class : classNodes)
    System.out.println(FromNode.toStr(class, "name"))
~~~

#### ToPOJO

- Acts as a wrapper for Jackson's ObjectMappers
- Mapping From JSON, XML, and YAML values in the form of String, byte[], InputStream, File and JsonNode
- Powered by Jackson

~~~java
Resulted<POJO> filePOJO = ToPojo.fromJSON(new File("student.json"), POJO.class));

if (filePOJO.notValid())
    System.err.println(filePOJO.result().message());
    
Resulted<POJO> stringPOJO = ToPojo.fromJSON("{ \"student\": {}}", POJO.class));

if (stringPOJO.notValid())
    System.err.println(stringPOJO.result().message());
~~~

## Model

#### RequestModel 
- Abstract base for incoming REST requests
- Validator to return custom Results depending on request data

~~~java
public StudentRequest extends RequestModel
{
    @JsonProperty("name")
    private String name;
    @JsonProperty("age")
    private Integer age;
    
    @JsonCreator
    public StudentRequest(
        @JsonProperty("name") String name,
        @JsonProperty("age") Integer age)
    {
        this.name = name;
        this.age = age;
    }
    
    @Overrides
    public Result validate()
    {
        Result result;
        
        result = Validate.name(name);
        if (result.notValid())
            return result;
        
        result = Validate.age(age);
        if (result.notValid())
            return result;
        
        return Result.VALID;
    }
}
~~~

#### ResponseModel 
- Abstract base for outgoing REST responses
- toResponse() for easily creating responses out of POJO's

~~~java
public StudentResponse extends ResponseModel
{ 
    public StudentResponse()
    {
        super();
    }
    
    public StudentResponse(@Notnull Result result)
    {
        super(result);
    }
}

@Path("student")
@POST
public Response studentRequest(StudentRequest request)
{
    StudentResponse response = new StudentResponse();
    
    Result result = request.validate();
    
    if (result.notValid(response)) // Automatically loads in Result if Result is not Result.valid
        return response.toResponse()
    
    StudentHandler.handle(request, response);
    
    return response.toResponse();
}
~~~

## Result

#### Result 
- Small Object containing either Result.VALID or a custom Result object with a code, message, and HTTP Status

~~~java
public static final Result INVALID_STUDENT_NAME  = Result.declare(10, "Invalid student name given!");
public static final Result STUDENT_NAME_TOO_LONG = Result.declare(11, "Student name is too long!");

public static final Result INVALID_STUDENT_AGE   = Result.declare(20, "Invalid student name given!");
public static final Result STUDENT_AGE_NEGATIVE  = Result.declare(21, "Student name is too long!");

public Result name(String name)
{
    if (name == null || name.length() == 0) {
        return INVALID_STUDENT_NAME;
    } else if (name.length() > 256) {
        return STUDENT_NAME_TOO_LONG;
    } else {
        return Result.VALID
    }
}

public Result age(Integer age)
{
    if (name == null || age == 0) {
        return INVALID_STUDENT_AGE;
    } else if (name < 0) {
        return STUDENT_AGE_NEGATIVE;
    } else {
        return Result.VALID
    }
}
~~~

#### Resulted
- Easily check function values for errors 
- Simple user response can be created from Resulted

~~~java
public static final Result REAMINDER_FOUND = Result.declare(100, "Numbers produce a remainder!");
public static final Result NULL_VALUES     = Result.declare(101, "Numbers cant be null!");
public static final Result COMPLETE        = Result.declare(102, "Completed Successfully!");

public DivideResponse extends ResponseModel
{
    @JsonProperty("value")
    private Integer value;
    
    public void setValue(Integer value)
    {
        this.value = value;
    }
    
    public Resulted<Integer> divideNoRemainder(int a, int b)
    {
        try {
            if (a % b != 0)
                return Resulted.inResult(remainderFound);
            
            int answer = a / b;
        
            return Resulted.inValue(answer);
        
        } catch (ArithmeticException e) {
            return Resulted.inException(e);
        
    }
}

}
@Path("divide")
@GET
public Response divide(@QueryParam("a") Integer a, @QueryParam("b") Integer b)
{
    DivideResponse response = new DivideResponse(); // Extends ResponseModel
    
    Resulted<Integer> resulted = divideNoRemainder(a, b);
    
    if (resulted.notValid(response))
        return response.toResponse();
        
    response.load(resulted.value());
    response.setResult(COMPLETE);
    
    return response.toResponse();
}
~~~

## Socket

#### HttpSocketFactory 
- Create multiple HttpSockets for diffrent endpoints of the same service quickly
- Powered by Jackson's JacksonJaxbJsonProvider and Javax's Client

~~~java
HttpSocketFactory sockFactory = HttpSocketFactory
    .newInstance("http://mybackendurl.domain:123/", MediaType.APPLICATION_JSON);
~~~

- Easily add SSLContexts or Custom Clients

~~~java
HttpSocketFactory sockFactory = HttpSocketFactory
    .newInstance("http://mybackendurl.domain:123/", MediaType.APPLICATION_JSON)
    .withSSLContext(createSSLContext());
~~~

~~~java
HttpSocketFactory sockFactory = HttpSocketFactory
    .newInstance("http://mybackendurl.domain:123/", MediaType.APPLICATION_JSON)
    .withClient(createClient());
~~~

#### HttpSocket
- Create multiple HttpSockets for diffrent endpoints of the same service quickly

~~~java
HttpSocketFactory sockFactory = HttpSocketFactory
    .newInstance("http://mybackendurl.domain:123/", MediaType.APPLICATION_JSON);
    
MultivaluedMap<String, Object> headers = myHeaders();
    
try (Response response = sockFactory.createSocket("/getEndpoint")
        .headers(headers)
        .header("single-header", "value")
        .query("query", "value")
        .get())
{
    //... Response work
}

try (Response response = sockFactory.createSocket("/postEndpoint")
        .query("query", "value")
        .post(Entity.json(createJson())))
{
    //... Response work
}
~~~


## Worker

### CycleWorker
- Make time based cycle workers

### TaskWorker
- Make queue based task workers


#### Dependencies
- [Javax WS RS api](https://mvnrepository.com/artifact/javax.ws.rs/javax.ws.rs-api)
- [Jackson Databind](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind)
- [Jackson JAXRS JSON](https://mvnrepository.com/artifact/com.fasterxml.jackson.jaxrs/jackson-jaxrs-json-provider)
- [Jackson Dataformat YAML](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml)
- [Jackson Dataformat XML](https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml)
- [HikariCP](https://mvnrepository.com/artifact/com.zaxxer/HikariCP)
- [Google Guava](https://mvnrepository.com/artifact/com.google.guava/guava)
- [JASYPT](https://mvnrepository.com/artifact/org.jasypt/jasypt)
