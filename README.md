
This was migrated from svn.tacitknowledge.com:/common/trunk/simulator from revision numbers 1493 to 2258.

This README is more a generalized todo/next steps list

Adapters are mostly threadsafe now, as long as the convention to only change instance variables at factory instantiation
will be adhered.

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

# Simulator

#Simulator Transports

## File Transport

File Transport has following configuration parameters:
* __directoryName__ - The underlying directory path to poll from/write to. Required parameter.
* __fileName__ - Name of the file to listen for/write to. Optional parameter.
* __fileExtension__ - Extension of files the transport will only poll from. Optional parameter.
* __regexFilter__ - Only file name matching the provided regex will be polled. Optional parameter.
* __pollingInterval__ - Milliseconds before the next poll of the directory. Optional parameter.
* __deleteFile__ - Determines if file should be deleted after processing. Optional parameter.


The following snippet is an example of a file type transport configuration:

```properties
type=file

directoryName=input
fileName=input.xml
fileExtension=
regexFilter=
deleteFile=false
pollingInterval=1000
``` 

In the above file transport configuration the directory __input__ will be scanned for file __input.xml__ every __1000__ milliseconds. Found files will not be deleted but anyway these will be moved inside __.camel__ folder(automatically created inside __input__ directory).

## FTP Transport

FTP transport has the following config parameters:
* __host__ - Host name parameter is a required one.
* __port__ - Port parameter has default value of 21 for FTP and 22 for SFTP, thus it is an optional one.
* __binary__ - Binary parameter determines transfer mode. A true value corresponds to BINARY and false value corresponds to ASCII. Default value is false and this parameter is optional.
* __username__ - Username parameter indicates the name of the used for accessing remote ftp resources and is optional.
* __password__ - Password parameter is optional.

FTP transport extends file transport and all file transport configuration parameters are available here too.


Below is presented config example for FTP Transport:

```properties
type=ftp

host=127.0.0.1
port=2121
username=admin
password=admin

directoryName=input
fileName=input.xml
fileExtension=
regexFilter=
deleteFile=true
pollingInterval=10000
```

In the above __ftp transport__ example is indicated __127.0.0.1__ as host and __2121__ as port. Username and password are set to __admin/admin__. __input.xml__ file will be searched in __input__ folder every __10000__ milliseconds and after processing the file will be deleted.

## SFTP Transport

SFTP Transport inherit configuration parameters from FTP Transport. The only difference is in type parameter which is sftp like in example below:

```properties
type=sftp

host=127.0.0.1
port=2121
username=admin
password=admin

directoryName=input
fileName=input.xml
fileExtension=
regexFilter=
deleteFile=true
pollingInterval=10000
```

##FTPS Transport

FTPS Transport will allow to transfer data over ftps protocol.

Most configuration parameters are inherited from FTP Transport. Type parameter is ftps as in the example below:

```properties
type=ftps

host=10.101.1.106
port=990
username=ociobanu
password=password

directoryName=/var/ftp/input/
fileName=input.xml
fileExtension=
regexFilter=
deleteFile=true
pollingInterval=10000

format=xml
validate=false
```

##Http Transport

Http Transport will allow to transport data on http protocol. Configuration parameters are presented below:

* __isSSL__ this parameter will indicate if communication will happen over ssl.
* __keyStoreFile__ path to file containing X.509 certificate
* __keyPassword__ the key password, which is used to access the certificate's key entry in the keystore 
* __storePassword__ the store password, which is required to access the keystore file.
* __port__ port parameter will indicate the port on which the server will listen incoming requests
* __resourceURI__ resource uri at which this service will be available
* __httpOut__ If this transport is an HTTP OUT, Camel route is just ended, so the result from the execution script will fill HTTP response body

```properties
type=http

host=127.0.0.1
port=8080
resourceURI=/testHelloService

# ssl parameters
#isSSL=false
# keyStoreFile=filename.jks
# keyPassword=password
# storePassword=password

format=PLAIN TEXT
```

In the above example the service will be accessible at the following address http://127.0.0.1:8080/testHelloService.

##JMS Transport

JMS Transport has following configuration parameters:
* __destinationName__ JMS destination name is a required parameter.
* __brokerUrl__ Broker URL is a required parameter.
* __userName__ JSM broker user name is an optional parameter.
* __password__ JSM broker password is an optional parameter.
* __activeMQ__ Active MQ parameter. Determines if JMS is Apache ActiveMQ (true) or generic JMS (false). Default value is false.
* __isTopic__ JMS topic name parameter is optional.

Below is presented an example for jms transport 

```properties
type=jms

activemq=true

isTopic=true

destinationName=test

brokerUrl=tcp://localhost:61616

userName=admin

password=admin

format=PLAIN TEXT
```

##REST Transport

Rest Transport inherits all properties from HTTP Transport. Configuration parameter type has value rest. Below is presented an example of rest transport configuration:

```properties
type=rest

host=127.0.0.1
port=8085
resourceURI=/testHelloService

format=PLAIN TEXT
```

##Simulator Formats

Following Formats are available:

##CSV Format

Following configuration parameters are available for CSV format:

* __isFirstRowHeader__ This configuration parameter will not indicate if first row is header(true) or not(false).
* __rowContent__ String describing what each row represents. REQUIRED if isFirstRowHeader is false. This will be used as bean name during the simulation. e.g.: employee, order, product, etc.
* __csvContent__ String describing what are the CSV contents. This configuration parameter is required. This will be used as the SimulatorPojo root's record key. e.g.: employees, orders, products, etc.

Below config example indicates that first row of the csv file will be header and csv content will be named "names":

```properties
format=CSV
csvContent=names
isFirstRowHeader=true
```

##JSON Format

Json format has following configuration parameters:

* __jsonContent__ Describes what are the JSON contents. This parameter is required. e.g.: employee(s), order(s), product(s), etc.
* __isArray__ Determines if the JSON content is an Array. This parameter is optional.Defaults to false (JSON Object). If this parameter is true, it's recommended that "jsonContent" uses a plural word and "jsonArrayContent" uses its singular form.
* __jsonArrayContent__ Describes each array element content. Optional parameter. Required if "isArray" is true. e.g.: employee, order, product, etc.

A Json configuration example  may be found below:

```properties
format=JSON

jsonContent=employees
isArray=true
jsonArrayContent=employee
```

Every element in the array will be named as employee and all array may be referenced as employees.

Content example that may be served by this json format config is presented below:

```json
[{ "firstName":"John" , "lastName":"Doe" }, { "firstName":"Anna" , "lastName":"Smith" },{ "firstName":"Peter" , "lastName":"Jones" }]
```