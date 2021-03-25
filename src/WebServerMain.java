import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


/** This program will establish the connection between server and client.
 *  Throws required error messages.
 */
public class WebServerMain {
    public static void main(String[] args) {
        // exit the system if user does not provied the correct input
        if (args.length < 2) {
            System.err.println("Usage: java WebServerMain <document_root> <port>");
            System.exit(0);
        }
        else {
            // to check the existtance of html files resourses folder
            File resourcesFolder = null;
            if (checkResouceFolder(args[0])) {
                resourcesFolder = new File(args[0]);
            }
            // portnumber to listen connections
            int portNumber = Integer.parseInt(args[1]);
            try ( ServerSocket serverConn = new ServerSocket(portNumber)) { // server socket the listen the client connection requests
                System.out.println("Server started on port " + portNumber);
                while (true) {
                    // waiting for client requests
                    Socket conn = serverConn.accept();
                    System.out.println("Connected to server, Request from: " + conn.getInetAddress());
                    // craeting an object of HttpServer class
                    HttpServer httpServer = new HttpServer(conn, resourcesFolder);
                    // creating thread based on client requests
                    startThreads(httpServer);
                }
            } catch (SocketException se) {
                System.err.println("Socket error: " + se.getMessage());
            } catch (IOException exception) {
                System.err.println("Oops, Server Connection Error: " + exception.getMessage());
            }
        }
    }

    /** Method to craete a new Thread on client requests.
     * @param hs httpServer class object.
     */
    private static void startThreads(HttpServer hs) {
        try {
            Thread thread = new Thread(hs);
            thread.start();
        }
        catch (Exception e) {
            System.err.println("Error: Unable to start the thraed " + e.getMessage());
        }
    }

    /** boolean method to check the existinace of html file resource folder.
     * @param resouceFolder - String parameter of folder path.
     * @return true of false based on existinace of html file resource folder.
     */
    private static boolean checkResouceFolder(String resouceFolder) {
        File filePath = new File(resouceFolder);
        if (!filePath.exists()) {
            System.err.println("Error: Please check the folder path of resouse files ");
            return false;
        }
        return true;
    }
}
