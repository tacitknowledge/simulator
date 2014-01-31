
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
* __sftp__ - Sftp parameter indicates if the protocol will be FTP or SFTP. A true value corresponds to SFTP and a false value corresponds to FTP. Default value is false and sftp parameter is optional.
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
