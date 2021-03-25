import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;

/** Http server class - which provides the requesed file or error message of client requests.
 * The program handles 'GET' and 'HEAD' requested methods from the client.
 */
public class HttpServer extends Thread {
    // static variable declartion from main class
    private static Socket socketConnect;
    private static File resourcesFolder;

    // variables decleration of responce headers
    private String OkHeader = "HTTP/1.1 200 OK";
    private String fileNotFoundHeader = "HTTP/1.1 404 Not Found";
    private String notImplementedHeader = "HTTP/1.1 501 Not Implemented";

    // variables decleration of input and output streams
    private OutputStream os;
    private InputStreamReader isr;
    private BufferedReader br;
    private BufferedOutputStream bos;
    private PrintWriter pw;

    /** The constructor of the class.
     * @param sc - Socket parameter.
     * @param rf - File parameter of file resource folder.
     */
    public HttpServer(Socket sc, File rf) {
        socketConnect = sc;
        resourcesFolder = rf;
        // block for initializing streams
        try {
            // to send back the data to client
            os = socketConnect.getOutputStream();
            //to get the data from client on the socket
            isr = new InputStreamReader(socketConnect.getInputStream());
            // buffer reader to read the data from client request
            br = new BufferedReader(isr);
            // to send back the data as response to client request
            bos = new BufferedOutputStream(os);
            // to read the header from client request
            pw = new PrintWriter(os);
        }
        catch(IOException ioe) {
            System.err.print(ioe);
        }
    }

    /** The override method from thread class.
     * this method will be executed on call from main class.
     */
    @Override
    public void run() {
        System.out.println("New http request.. ");
        // try block to responce the client requested file
        try {
            findRequestedFile();
        }
        catch (Exception e) {
            System.err.println("Unable to load the requesed file ..: " + e.getMessage());
        }
        // calling method to close all streams
        closeConnection();
    }

    /** This method close the all streams connection.
     * method throws an error if one of the stream is unable to close.
     */
    private void closeConnection() {
        try {
            os.close();
            isr.close();
            br.close();
            bos.close();
            pw.close();
            // closing method to close the socket connection
            closeSocket();
        } catch (IOException e) {
            System.err.print("Closing error:  " + e.getMessage());
        }
        System.err.println("All connctions have been closed, Response has been sent to client");
    }

    /** This method close the socket connection.
     * method throws an error for unclosed socket connection.
     */
    private void closeSocket() {
        try {
            socketConnect.close();
        } catch (IOException e) {
            System.err.println("Error for closing socket " + e.getMessage());
        }
    }

    /** This method sends back response with requested file from client.
     * @throws Exception - if client requesed file is not in resocuse folder.
     */
    private void findRequestedFile() throws Exception {
        // read the request from client over socket
        String token = br.readLine();
        String[] tokenList = token.split(" ");
        // storing the header from client request
        String header = tokenList[0];
        // reading a client requesed file name
        String clientRequestedFileName = tokenList[1].substring(1);

        // 'HEAD' and 'GET' method check
        if (header.equals("HEAD") || header.equals("GET")) {
            // send back welcome file if client does not request for perticular file
            if (clientRequestedFileName.equals("")) {
                clientRequestedFileName += "WelcomeJavaServer.html";
            }

            File clientRequestFile = new File(resourcesFolder, clientRequestedFileName);
            // if file doesnt exist then return file not found error page to client
            if (!(clientRequestFile.exists())) {
                fileNotFoundError(pw, bos);
            }
            int fileLength = (int) clientRequestFile.length();
            //reading file content in bytes
            byte[] fileContent = readRequestedFile(clientRequestFile);

            // prints header if client request method is HEAD or GET
            printHeader(pw, OkHeader ,fileLength);

            // send back requested file to client when method is GET
            if (header.equals("GET")) {
                while (fileContent.length != -1) {
                    bos.write(fileContent, 0, fileLength);
                }
                bos.flush();
            }
        }
        else {
            // not implemneted error page will be send back to client if client request for any other method than GET or HEAD
            notImplementedMethod(pw, bos);
        }
    }

    /** This method prints the header to client request.
     * @param pw - printWriter to write the header as response to client.
     * @param header - the String of a header.
     * @param fileLength - the file length in bytes.
     */
    private void printHeader (PrintWriter pw, String header, int fileLength) {
        pw.println(header + "\n" +
        "Server: Simple Java Http Server\n" + 
        "Content-Type: " + "text/html\n" +
        "Content-Length: " + fileLength + "\n");
        pw.println();
        pw.flush();
    }

    /** To read the qequested file in bytes.
     * @param requestedFile - File type variable of requested file.
     * @return the array of bytes.
     * @throws IOException - throws an exception if unable to read the file.
     */
    private byte[] readRequestedFile (File requestedFile) throws IOException {
        FileInputStream fileDataInput = new FileInputStream(requestedFile);
        int fileLength = (int) requestedFile.length();
        byte[] fileContent = null;

        // read file till last
        if (fileLength != -1 ) {
            fileContent = new byte[fileLength];
            fileDataInput.read(fileContent);
        }
        else {
            System.err.println("Error: Unable to read the requested file");
        }
        fileDataInput.close();
        return fileContent;
    }

    /** The method when user request method other than GET and HEAD.
     * @param pw - printWriter to write the header as response to client.
     * @param bos - BufferedOutputStream to send back the file data to client.
     */
    private void notImplementedMethod(PrintWriter pw, BufferedOutputStream bos) {
        try {
            File clientRequestFile = new File(resourcesFolder,  "notImplemented.html");
            int fileLength = (int) clientRequestFile.length();
            byte[] fileContent = readRequestedFile(clientRequestFile);
            printHeader(pw, notImplementedHeader, fileLength);
            writeRequesedfile(fileContent, fileLength);
        }
        catch (IOException ioe) {
            System.err.println("501: Http method not implemented"); 
        }
    }

    /** The method when user request for file which does not exist in resource folder.
     * @param pw - printWriter to write the header as response to client.
     * @param bos - BufferedOutputStream to send back the file data to client.
     */
    private void fileNotFoundError(PrintWriter pw, BufferedOutputStream bos) {
        try {
            File clientRequestFile = new File(resourcesFolder, "fileNotFound.html");
            int fileLength = (int) clientRequestFile.length();
            byte[] fileContent = readRequestedFile(clientRequestFile);
            printHeader(pw, fileNotFoundHeader,fileLength);
            writeRequesedfile(fileContent, fileLength);
            System.out.print("404: File Not found");
        }
        catch (IOException ioe) {
            System.err.println("404: Requested file not found .. " + ioe.getMessage());
        }
    }

    /** this method to send the data back to client using BufferedOutputStream.
     * @param fileContent - the file content as bytes.
     * @param fileLength - the file length.
     */
    private void writeRequesedfile(byte[] fileContent, int fileLength) {
        try {
            bos.write(fileContent, 0, fileLength);
            bos.flush();
        }
        catch (IOException ioe) {
            System.out.println("Error: Unable to write the requesed file:" + ioe.getMessage());
        }
       
    }
}
