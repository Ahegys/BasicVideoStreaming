package org.spatialduck;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080, 0, InetAddress.getByName("IP_PC"));

            System.out.println("Java HTTP Server is listening on IP_PC:8080...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    System.out.println("Java HTTP Server accepted a connection.");

                    String requestLine = reader.readLine();
                    System.out.println("Request line: " + requestLine);

                    if (requestLine != null && requestLine.startsWith("GET / ")) {
                        File file = new File("src/main/resources/index.html");
                        StringBuilder htmlContent = new StringBuilder();

                        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = fileReader.readLine()) != null) {
                                htmlContent.append(line).append("\n");
                            }
                        }

                        String responseMessage = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n" + htmlContent.toString();
                        writer.print(responseMessage);
                    } else {
                        String responseMessage = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nPage not found!";
                        writer.print(responseMessage);
                        System.out.println("Sent response to client: [ " + responseMessage + " ]");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
