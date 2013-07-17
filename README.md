
This README is more a generalized todo/next steps list

Some adapters need thread safety fixes.  Currently these instance variables are questionable:
   XMLAdapter's doc is thread specific.  useFullyQualifiedNodeNames needs review but should be fine.
   SoapAdapter's soapMessage, payloadNS, and payloadNSUri.  the wsdl and availableOps should be reviewed, but probably OK
   DocLiteralWrappedSoapAdapter as SoapAdapter. It has phantom instance variables from a copy/paste of SoapAdapter
   FullResponseSoapAdapter contains no phantoms and should be fixed when SoapAdapter superclass is fixed.
   CsvAdapter: colNames seems thread specific unless it only handles one set of csv formats.  But that seems more scenario specific
   All the other adapters seem fine
   Transports appear fine.  Only comment is on a few things which might be made final if a different config subsystem was used.
     This could be improved with constructor injection from the factory.
   ScenarioImpl is fine.  All mutable instance variables are config driven.
      This could be improved with constructor injection from the factory.

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

Lastly, we plan on integrating the github/tacitknowledge/perf-degradation library into the system for performance
testing against failing integration points.  This will be greatly eased by the refactoring and framework introduction
above for instance lifecycle management.

