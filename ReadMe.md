### About

#### Goal

The goal of this repository is to provide a prototype for improving the developer experience when using Nuxeo Automation.

#### What problem we want to solve

Automation Scripting and it's current integration in Studio has some known limitations:

 - debuging is not easy
 - test cycle are slow
 - there is no documentation associated to scripts, so maintenance is painful
 - unit testing is complex

The goal of this prototype is to provide:

 - interactive execution
 	- direct execution of the script
 - easy access to output
 	- result displayed as html
 	- logs
 - instant reload/refresh
 	- no need for a full hot-reload
 - write unit tests using scripting 
 	- write unit tests as part of the development cycle

The goal is to provide a way to do all steps with one script:

 - prepare the data that is needed in the repository so that the Operation can run
 - code the operation using scripting
 - test the operation


#### Testing an Automation Scripting Operation

**Deploy**

If your script start with `@Operation(id = "<NameOfMyOperation>")` then the Nuxeo server will:

 - compile the code
 - create a new Operation 
 - send a confirmation that the code was compiled and deployed as an operation

Since the operation is kept by the Nuxeo server inside an extension point registry:

 - the operation can be called from any cell, any notebook, or the REST API
 - the operation will be "lost" is the server is restarted

You can update the code of the operation and each execution from the Notebook will:

 - unregister previous code
 - register the new operation
 - flush the Automation Compile cache 
 	- (not sure what the behavior under concurrency will be)

**Setup**

Most of the time, your operation can not work without a context to be established: users, documents, vocabularies ...

For that you can use a script cell that will be responsible for doing the setup: since the side effect on the Nuxeo server are persisted you can use the scripting to do the setup.

**Logs**

There is currently no breakpoint feature, but the `Console` object is overriden.

**Tests**

The idea is to use additional code cells to contain the test cases for your operation.

You can use the `Assert` context helper to generate assertion and get a visual display of your tests results:

