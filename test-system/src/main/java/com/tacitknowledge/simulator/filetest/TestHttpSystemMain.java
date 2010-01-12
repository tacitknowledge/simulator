package com.tacitknowledge.simulator.filetest;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;

/**
 * Class that will create an Http Server.
 * Used for REST and SOAP transport testing
 */
public class TestHttpSystemMain {

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(9000);

        HttpHandler handler = new HttpHandler(){

            public void handle(HttpExchange httpExchange) throws IOException {
                byte[] response = "<?xml version=\"1.0\"?>\n<resource id=\"1234\" name=\"test\" />\n".getBytes();
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,
                        response.length);
                httpExchange.getResponseBody().write(response);
                System.out.println("Request received: " + httpExchange);
                httpExchange.close();
            }
        };

        HttpServer server = HttpServer.create(addr, 0);
        server.createContext("/testapp", handler);
        server.start();
        InputStreamReader reader = new InputStreamReader(System.in);
        char input;
        while((input = (char)reader.read()) != 'q'){

        }
        server.stop(0);
    }
}
