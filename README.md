
This was migrated from svn.tacitknowledge.com:/common/trunk/simulator from revision numbers 1493 to 2258.

This README is more a generalized todo/next steps list

Adapters are mostly threadsafe now, as long as the convention to only change instance variables at factory instantiation
will be adhered.

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

# Simulator

**SimulatorTransports**

## File Transport

File Transport has following configuration parameters:
*directoryName - The underlying directory path to poll from/write to. Required parameter.
*fileName - Name of the file to listen for/write to. Optional parameter.
*fileExtension - Extension of files the transport will only poll from. Optional parameter.
*regexFilter - Only file name matching the provided regex will be polled. Optional parameter.
*pollingInterval - Milliseconds before the next poll of the directory. Optional parameter.
*deleteFile - Determines if file should be deleted after processing. Optional parameter.
