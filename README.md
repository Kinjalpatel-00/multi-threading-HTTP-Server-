# multi-threading-HTTP-Server-


**This implemented project is a multi-threading HTTP Server that supports GET and HEAD methods requests.**

**The implementation of code:**
- The implemented HTTP server supports any Html requested file from any browser(Client). In the case where the user does not request any Html file, the program will display the welcome page of the java server. The program will return a 404 error message (fileNotFound.html page) if the file does not exist in the resource folder. The server will respond with 501 error (notImplemented.html) if the client requests any method other than ‘GET’ or‘HEAD’.  

**Required input:**
- As input, the user has to provide the correct folder path of Html files and port number to run the server. 


**To run the program**

- To start the server (as command line)
  - javac WebServerMain.java
  - java WebServerMain ../www 12345
- After successfully starting the server, the Html pages can be accessed by any browser window by typing, 
  - http://localhost:12345/index.html (change file name to access another file)
    
**Java File description:**

- WebServerMain.java
	- This file includes the main method of the project. This file will establish the server connection on the provided port number. Also starts the thread to handle the multiple client's requests at the same time. 
	 - The file will call the httpServer class to successfully run the client-server functionally. 

- HttpServer.java
	- This file extends the ‘thread’ class and the ‘run’ method will be executed while calling from the main method. The program will provide the correct file or error message to the client ‘GET’ and ‘HEAD’ requests. 
      
**References:**

- lecture videos of course and java examples files of CS5001 have been used as a reference. Additionally, the below references were being used to understand the concepts. 

   - https://www.tutorialspoint.com/javaexamples/net_multisoc.htm
   -  https://www.tutorialspoint.com/http/http_responses.htm
   -  https://www.tutorialspoint.com/http/http_methods.htm





