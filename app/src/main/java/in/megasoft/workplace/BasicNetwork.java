package in.megasoft.workplace;

import static com.android.volley.VolleyLog.DEBUG;
import com.android.volley.Request;
import com.android.volley.VolleyLog;

public class BasicNetwork {
    private long SLOW_REQUEST_THRESHOLD_MS;
    private void logSlowRequests(
            long requestLifetime, Request<?> request, byte[] responseContents, int statusCode) {
        if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d(
                "HTTP response for request=<%s> [lifetime=%d], [size=%s], "
                        + "[rc=%d], [retryCount=%s]",
                request,
                requestLifetime,
                responseContents != null ? responseContents.length : "null",
                statusCode,
                request.getRetryPolicy().getCurrentRetryCount());
        }
    }
}
