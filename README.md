# Simulator

##Overview
Simulator is a functional tool that allows handling of different transports and their content based on predefined criteria and outputting expected results based on the situation being modeled. This tool may help simulate the behavior of a third party service like paypal or cybersource and from which depends the functionality of a given application, thus Simulator may help test it.

In order to simulate a third party service you have to define the inbound and outbound transport protocol for that service. Also it is possible to use a scripting language to execute conditionally certain operations over incoming data and to determine what data will be presented to output. In below sections are presented more details pertaining to inbound/outbound protocol configuration and scripting possibilities with examples.    

# How To Start Simulator

1. cd to simulator installation folder in a console
2. mvn clean install
3. cd ./simulator-core
4. java -DsystemsDirectory=[configuration folder path] -jar ./target/simulator-core.one-jar.jar 

* __[configuration folder path]__ should be the path to the systems folder, that is described below in __Configuration Folder Structure__ section. 

# Configuration Folder Structure

Configuration folder has one or more system folders. The notion of system is just for grouping of conversations. One ore more conversations are grouped in one system. All systems' folders are grouped in one folder. In the example presented below is present one folder systems, that contains all configured systems, in the below example is present only one system named system1. System1 has following conversations: fileConversation, httpConversation, jmsConversation, restConversation, soapConversation. Every conversation has required files: inbound.properties, outbound.properties and at least one scenario file. This systems folder should be indicated when Simulator application is started.

Below is presented an example of a configuration folder structure:

```config
systems
`-- system1
    |-- fileConversation
    |   |-- inbound.properties
    |   |-- outbound.properties
    |   `-- scenario1.scn
    |-- httpConversation
    |   |-- inbound.properties
    |   |-- outbound.properties
    |   `-- scenario1.scn
    |-- jmsConversation
    |   |-- inbound.properties
    |   |-- outbound.properties
    |   `-- scenario1.scn
    |-- restConversation
    |   |-- inbound.properties
    |   |-- outbound.properties
    |   `-- scenario1.scn
    `-- soapConversation
        |-- inbound.properties
        |-- outbound.properties
        `-- scenario1.scn
```

As it may be seen from folder structure every conversation has three types of config files: inbound.properties, outbound.properties and scenario files like scenario1.scn, scenario2.scn, etc. Inbound.properties file contains configuration for inbound communication protocol/transport and format of the incoming data. Outbound.properties contains configuration for outbound communication protocol/transport and format of the outgoing data. Scenario files contain some instructions for execution of certain operations over incoming data in case of certain conditions and also the output data is indicated.

Example of inbound.properties is presented below:

```properties
type=file

directoryName=input
fileName=input.yaml
fileExtension=
regexFilter=
deleteFile=false
pollingInterval=1000

format=yaml

yamlContent=invoice
```    

Example of scenario file also maybe found below:
```properties
[language]
javascript

[when]
true

[execute]
invoice;
```

Here JavaScript language is used. This script will always be executed and this script just will output "invoice" variable. Any BSF-compatible scripting language is supported.

## Conversation Required Configurations

For every conversation it is necessary to have configured inbound transport, inbound format, outbound transport, outbound format and at least one scenario file,  respectively there should be present at least following files:
* __inbound.properties__,
* __outbound.properties__, 
* __scenario.scn__ 

If these preconditions are not met, errors should be expected in log files. 

## Scenario File Structure

As mentioned above every conversation may have one or more scenario files. Every scenario file has a three blocks:
* __[language]__ this block mentions one of BSF-compatible scripting languages.
* __[when]__ condition for execution of the statements indicated in [execute] block
* __[execute]__ statements to be executed if conditions indicated in [when] block are true  

Examples of for various configurations can be found in 

```properties
simulator-core/src/main/resources/systems-sample/ConversationSystemExample
```

#Simulator Transports

Simulator supports a set of transports that may be configured for input and for output. In the case of http input transport, simulator will start jetty web server that will handle incoming requests. The same is true for rest transport. For other transport types like ftp or jsp, Simulator will try to access an existing ftp server as for input messages, as for output messages. Below are described supported transports:

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

#Simulator Formats

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

##Plain Text Format

This format does not need any config parameters. The only thing to keep in mind is the fact that text content may be referenced as "text".

##Properties Format

This format has only one configuration parameter:

* __propertySeparator__ Property level separator parameter name, which is optional. Defaults to dot (".")

Following properties content was accepted by Simulator:

```properties
person.lastName=Doe
person.firstName=John
```
with the following result:

```properties
nativeobject.lastName=Doe
nativeobject.firstName=John
```

[TO DO] Resulting properties content should not contain nativeobject as the root element.

##XML Format

XML Format configuration parameters:

* __validate__ Validate parameter. If the XML text should DTD validated. This parameter is optional. Defaults to false (no DTD validation).
* __rootTagName__ Override root tag name if one is provided.

XML Format example presented below will override root tag name to users, otherwise it will be "nativeobject".

```properties
format=xml
rootTagName=users
```

##YAML Format

YAML Format configuration parameters:

* __yamlContent__ String describing what are YAML contents. This parameter is required. This will be used as the SimulatorPojo root's record key. e.g.: employees, orders, products, etc.
* __isArray__ IsArray parameter determines if YAML content is an Array. This parameter is optional. Defaults to false. If this parameter is true, it's recommended that "yamlContent" uses a plural word and "yamlArrayContent" its singular form.
* __yamlArrayContent__ Yaml array content parameter describes each array element content. This parameter is optional. Required if "isArray" parameter is true. e.g.: employee, order, product, etc.

Configuration example for yaml format:

```properties
format=yaml
yamlContent=invoice
```

##Rest Format

Rest Format configuration parameters:

* __objectName__ Extracted object name parameter is optional. Default value is obj.
* __extractionPattern__  pattern to extract values from http request

Rest format configuration example:

```properties
format=rest
extractionPattern=testHelloService/:name/:surname
objectName=person
```

The following request may be used for accessing the rest format configuration presented above http://127.0.0.1:8085/testHelloService/Vasea/Turcanu . As result we get name=Vasea and surname=Turcanu that may be referenced in the following way:

```javascript
person['request']['params'];
```

##SOAP Format

SOAP Format will handle SOAP 1.2 protocol messages.

SOAP Format configuration parameters:

* __wsdlURL__ real wsdl url to simulated web service, This is needed for validation of incoming messages.

Below is presented a configuration for soap format with a certain wsdl url to validate against:

```properties
format=soap
wsdlURL=http://127.0.0.1/testHelloService/wsdl
```

##Doc Literal Soap Format

Configuration parameters:
* __wsdlURL__ real wsdl url to simulated web service, This is needed for validation of incoming messages.

Configuration example for doc literal soap format

```properties
format=DOCLITERALSOAP
wsdlURL=http://127.0.0.1/testHelloService/wsdl
```

##SOAP Full Response Format

Configuration parameters:

* __wsdlURL__ real wsdl url to simulated web service, This is needed for validation of incoming messages.

Configuration example:

```properties
format=SOAPFULLRESPONSE
wsdlURL=http://127.0.0.1/testHelloService/wsdl
```

#TO DO

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

#Origins

This was migrated from svn.tacitknowledge.com:/common/trunk/simulator from revision numbers 1493 to 2258.



