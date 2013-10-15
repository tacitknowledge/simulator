package com.tacitknowledge.simulator.load;

import org.apache.http.*;
import static org.junit.Assert.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: 6/26/13
 * Time: 10:51 AM
 * This is not a unit test, but exists to apply concurrent load against the simulator
 * for performance testing and tuning
 *
 */
public class TestLoadOnHttpEndpoint {

    @Ignore
    @Test
    public void testRunLoad() throws IOException, InterruptedException {
        final int TOTAL_JOBS = 1000;

        ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);


        for (int i = 0; i<TOTAL_JOBS; i++) {
        Runnable post = new Runnable() {
            public void run() {
                 DefaultHttpClient httpclient = new DefaultHttpClient();
                 HttpHost target = new HttpHost("127.0.0.1", 8030, "http");

                HttpGet req = new HttpGet("/PostcodeAnywhere?Address=TW9");

                System.out.println("executing request to " + target);
                HttpResponse rsp = null;
                try {
                    rsp = httpclient.execute(target, req);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                HttpEntity entity = rsp.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(rsp.getStatusLine());
                Header[] headers = rsp.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    System.out.println(headers[i]);
                }
                assertEquals("HTTP/1.1 200 OK",headers[0].toString().trim());
                System.out.println("----------------------------------------");
                if (entity != null) {
                    try {
                        System.out.println(EntityUtils.toString(entity));
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

            }
        };
        service.submit(post);

        }


        while(service.getCompletedTaskCount() != TOTAL_JOBS) {
            Thread.currentThread().sleep(100);
        }
        Thread.currentThread().sleep(5000);
        service.shutdown();
        assertEquals("Did not complete all jobs",TOTAL_JOBS,service.getCompletedTaskCount());
    }
    @Ignore
    @Test
    public void testRunOnce() throws IOException, InterruptedException {
        final int TOTAL_JOBS = 1;

        ThreadPoolExecutor service = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);


        for (int i = 0; i<TOTAL_JOBS; i++) {
        Runnable post = new Runnable() {
            public void run() {
                 DefaultHttpClient httpclient = new DefaultHttpClient();
                 HttpHost target = new HttpHost("127.0.0.1", 8030, "http");

                HttpGet req = new HttpGet("/PostcodeAnywhere?Address=TW9");

                System.out.println("executing request to " + target);
                HttpResponse rsp = null;
                try {
                    rsp = httpclient.execute(target, req);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                HttpEntity entity = rsp.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(rsp.getStatusLine());
                Header[] headers = rsp.getAllHeaders();
                for (int i = 0; i < headers.length; i++) {
                    System.out.println(headers[i]);
                }
                assertEquals("HTTP/1.1 200 OK",headers[0].toString().trim());
                System.out.println("----------------------------------------");
                if (entity != null) {
                    try {
                        System.out.println(EntityUtils.toString(entity));
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

            }
        };
        service.submit(post);

        }


        while(service.getCompletedTaskCount() != TOTAL_JOBS) {
            Thread.currentThread().sleep(100);
        }
        Thread.currentThread().sleep(5000);
        service.shutdown();
        assertEquals("Did not complete all jobs",TOTAL_JOBS,service.getCompletedTaskCount());
    }

static public class NHttpClient {

    public static void main(String[] args) throws Exception {
        // HTTP parameters for the client
        HttpParams params = new SyncBasicHttpParams();
        params
            .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
            .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
            .setParameter(CoreProtocolPNames.USER_AGENT, "Test/1.1");
        // Create HTTP protocol processing chain
        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Use standard client-side protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});
        // Create client-side HTTP protocol handler
        HttpAsyncRequestExecutor protocolHandler = new HttpAsyncRequestExecutor();
        // Create client-side I/O event dispatch
        final IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(protocolHandler, params);
        // Create client-side I/O reactor
        final ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        // Create HTTP connection pool
        BasicNIOConnPool pool = new BasicNIOConnPool(ioReactor, params);
        // Limit total number of connections to just two
        pool.setDefaultMaxPerRoute(2);
        pool.setMaxTotal(2);
        // Run the I/O reactor in a separate thread
        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    // Ready to go!
                    ioReactor.execute(ioEventDispatch);
                } catch (InterruptedIOException ex) {
                    System.err.println("Interrupted");
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                }
                System.out.println("Shutdown");
            }

        });
        // Start the client thread
        t.start();
        // Create HTTP requester
        HttpAsyncRequester requester = new HttpAsyncRequester(
                httpproc, new DefaultConnectionReuseStrategy(), params);
        // Execute HTTP GETs to the following hosts and
        HttpHost[] targets = new HttpHost[] {
                new HttpHost("www.apache.org", 80, "http"),
                new HttpHost("www.verisign.com", 443, "https"),
                new HttpHost("www.google.com", 80, "http")
        };
        final CountDownLatch latch = new CountDownLatch(targets.length);
        for (final HttpHost target: targets) {
            BasicHttpRequest request = new BasicHttpRequest("GET", "/");
            requester.execute(
                    new BasicAsyncRequestProducer(target, request),
                    new BasicAsyncResponseConsumer(),
                    pool,
                    new BasicHttpContext(),
                    // Handle HTTP response from a callback
                    new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response) {
                    latch.countDown();
                    System.out.println(target + "->" + response.getStatusLine());
                }

                public void failed(final Exception ex) {
                    latch.countDown();
                    System.out.println(target + "->" + ex);
                }

                public void cancelled() {
                    latch.countDown();
                    System.out.println(target + " cancelled");
                }

            });
        }
        latch.await();
        System.out.println("Shutting down I/O reactor");
        ioReactor.shutdown();
        System.out.println("Done");
    }
}
}
