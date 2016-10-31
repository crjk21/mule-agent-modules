
package com.mulesoft.agent.monitoring.publisher.ingest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mulesoft.agent.clients.AuthenticationProxyClient;
import com.mulesoft.agent.monitoring.publisher.ingest.model.IngestApplicationMetricPostBody;
import com.mulesoft.agent.monitoring.publisher.ingest.model.IngestTargetMetricPostBody;
import com.ning.http.client.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;

/**
 * Monitoring Ingest API Client
 */
public class AnypointMonitoringIngestAPIClient
{

    private static final Logger LOGGER = LogManager.getLogger(AnypointMonitoringIngestAPIClient.class);
    private static final String APPLICATION_NAME_HEADER = "X-APPLICATION-NAME";

    private final String targetMetricsPath;
    private final String applicationMetricsPath;

    private final AuthenticationProxyClient authProxyClient;

    private AnypointMonitoringIngestAPIClient(String apiVersion, AuthenticationProxyClient authProxyClient)
    {
        this.targetMetricsPath = String.format("/monitoring/ingest/api/v%s/targets", apiVersion);
        this.applicationMetricsPath  = String.format("/monitoring/ingest/api/v%s/applications", apiVersion);
        this.authProxyClient = authProxyClient;
    }

    public static AnypointMonitoringIngestAPIClient create(String apiVersion, AuthenticationProxyClient authProxyClient)
    {
        return new AnypointMonitoringIngestAPIClient(apiVersion, authProxyClient);
    }

    public Response postTargetMetrics(final IngestTargetMetricPostBody body)
    {
        return this.authProxyClient.post(this.targetMetricsPath, body);
    }

    public Response postApplicationMetrics(final String applicationName, final IngestApplicationMetricPostBody body)
    {
        HashMap<String, Collection<String>> headers = Maps.newHashMap();
        headers.put(APPLICATION_NAME_HEADER, Lists.newArrayList(applicationName));
        return this.authProxyClient.post(this.applicationMetricsPath, body, headers);
    }

}