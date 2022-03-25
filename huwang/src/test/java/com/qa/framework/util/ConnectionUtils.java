package com.qa.framework.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharStreams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import static com.google.common.collect.Iterables.isEmpty;

/**
 * Utils for Connection testing
 *
 * @author Steve Phillips
 */
public class ConnectionUtils {
    private static Logger logger = Logger.getLogger(ConnectionUtils.class);

	private static final long POLL_PERIOD_MS = 10000;
	private static final Set<Integer> HTTP_SUCCESS_CODES = ImmutableSet.of(200);

	/**
	 * Continuously attempt to connect to an Http endpoint, until timeout or success.
	 *
	 * @param url Http URL
	 * @param minutes       number of minutes before giving up and throwing RuntimeException
	 */
	public static void blockUntilHttpEndpointActiveOrTimeOut(URL url, int minutes) {
		logger.info("Waiting for endpoint " + url);
		logger.trace("Checking for activity from " + url + " every " + POLL_PERIOD_MS + "ms for " + minutes + " minutes");

		// this is approximate...
		long loops = (minutes * 60 * 1000) / POLL_PERIOD_MS;
		for (int i = 0; i < loops; i++) {
			try {
				Thread.sleep(POLL_PERIOD_MS);
			} catch (InterruptedException e) {
				// noop
			}

			if (isHttpEndPointAccessible(url)) {
				logger.info("Endpoint " + url + " available");
				return;
			}
		}
		throw new RuntimeException("Failed to contact endpoint " + url);
	}

	/**
	 * Check whether an Http endpoint is active (i.e. a connection can be opened).  If it is not available, then RuntimeException is thrown.
	 *
	 * @param url Http URL
	 */
	public static void checkHttpConnection(URL url) {
		checkHttpConnection(url, Collections.<Integer>emptySet());
	}

	/**
	 * Check whether an Http endpoint returns a success code for a GET request.  If not, then a RuntimeException is thrown.
	 * <p/>
	 * Success codes are just '200'....
	 *
	 * @param url Http URL
	 */
	public static void checkHttpConnectionAndSuccessResponseCode(URL url) {
		checkHttpConnection(url, HTTP_SUCCESS_CODES);
	}

	/**
	 * Check whether an Http endpoint returns a success code in the supplied set, for a GET request.  If not, then a RuntimeException is thrown.
	 *
	 * @param url URL
	 * @param successCodes  set of return codes to be considered as successful
	 */
	public static void checkHttpConnection(URL url, Set<Integer> successCodes) {
		HttpResponse response = getHttpResponseOrThrow(url);

		checkValidStatusCode(url, response, successCodes);
	}

	/**
	 * Check whether an Http endpoint is accessible, i.e. a connection can be opened.
	 *
	 * @param url Http URL
	 * @return true if a connection can be opened to the address
	 */
	public static boolean isHttpEndPointAccessible(URL url) {
		return null != getHttpResponse(url, false);
	}

	private static HttpResponse getHttpResponseOrThrow(URL url) {
		return getHttpResponse(url, true);
	}

	private static HttpResponse getHttpResponse(URL url, boolean throwOnConnectionFailure) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet;
        try {
            httpGet = new HttpGet(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
		} catch (Exception e) {
			if (throwOnConnectionFailure)
				throw new RuntimeException("Failed to execute http get on url: " + url, e);
			else
				return null;
		} finally {
			httpGet.releaseConnection();
		}

		return response;
	}

	private static void checkValidStatusCode(URL url, HttpResponse response, Set<Integer> successCodes) {
		if (isEmpty(successCodes))
			return;

		int statusCode = response.getStatusLine().getStatusCode();
		if (!successCodes.contains(statusCode)) {
			String msg = "Received return code " + statusCode + " from " + url;
			logger.error(msg);
			logger.error("Status line: " + response.getStatusLine());

            try {
                InputStream is = response.getEntity().getContent() ;
                logger.error("Body: " + CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8))) ;
            } catch (IOException e) {
                // already an error, so do nothing here.
            }
 			throw new RuntimeException(msg);
		}
	}

}
