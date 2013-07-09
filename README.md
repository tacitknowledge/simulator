
This README is more a generalized todo/next steps list
Next Steps:
1. Needs a FullResponseSoapAdapterIntegrationTest similar to what's used on Beaverbrooks
2. Object lifecycle management and IoC needs review and changes.  This will alter the configuration system.

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

Lastly, we plan on integrating the github/tacitknowledge/perf-degradation library into the system for performance
testing against failing integration points.  This will be greatly eased by the refactoring and framework introduction
above for instance lifecycle management.

