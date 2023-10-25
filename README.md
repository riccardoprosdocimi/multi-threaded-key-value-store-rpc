# Multi-threaded key-value store (RPC/RMI) #
> A client-server architecture that communicates with Remote Procedure Calls (RPC). Implemented using Java RMI. The server is multi-threaded such that it can handle multiple outstanding client requests at once.
### It's set up to allow multiple concurrent clients to communicate with it and perform three basic operations:
- PUT(key, value)
- GET(key)
- DELETE(key)
- - -
#### Usage (locally):
1) Open up two terminal windows and navigate to `/Project2/src`
2) In one window, type `javac server/*.java utils/*.java` (hit <kbd>↩</kbd>), followed by `java server.Main <Port#>`, where `<Port#>` is the port number the registry is located at (hit <kbd>↩</kbd>)
3) The server is now running
4) In the other window, type `javac client/*.java utils/*.java` (hit <kbd>↩</kbd>), followed by `java client.Main <Port#>`, where `Port#` is the server's registry's port number (***it has to match the server's***) (hit <kbd>↩</kbd>)
5) The client is now running
6) The predefined protocol is:
    * `PUT:key:value`(hit <kbd>↩</kbd>)
    * `GET:key`(hit <kbd>↩</kbd>)
    * `DELETE:key`(hit <kbd>↩</kbd>)
7) To shut down the application, type `stop`(hit <kbd>↩</kbd>) or `shutdown`(hit <kbd>↩</kbd>)
