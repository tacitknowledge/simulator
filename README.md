
As of svn commit 2235, the simulator should be entering functional testing for the BB project.  This module
lacked a README until now.  I'm not going to focus on usage here, but next steps in refactoring

The XML and SOAP adapters need stronger unit tests for responses.  They *might* benefit from XSD and DTD support to
aid in generating proper responses with tools like JAXB.

Currently, the BaseAdapter and its many subclasses needs decomposition into delegates.  Its handling too many
responsibilities and has become difficult to extend.

Second, the configuration and adapters would probably benefit from integration of an IoC mechanism plus a more
flexible configuration layer.

Lastly, we plan on integrating the github/tacitknowledge/perf-degradation library into the system for performance
testing against failing integration points.  This will be greatly eased by the refactoring and framework introduction
above.

