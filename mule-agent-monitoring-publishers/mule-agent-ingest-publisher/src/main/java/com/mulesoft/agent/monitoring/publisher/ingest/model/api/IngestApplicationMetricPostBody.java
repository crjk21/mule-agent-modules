
package com.mulesoft.agent.monitoring.publisher.ingest.model.api;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents the body of the post to publish application metrics to the monitoring ingest API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "message-count",
        "response-time",
        "error-count"
})
public class IngestApplicationMetricPostBody
{

    @JsonProperty("message-count")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<IngestMetric> messageCount = new LinkedHashSet<IngestMetric>();

    @JsonProperty("response-time")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<IngestMetric> responseTime = new LinkedHashSet<IngestMetric>();

    @JsonProperty("error-count")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<IngestMetric> errorCount = new LinkedHashSet<IngestMetric>();

    @JsonProperty("flows")
    @JsonDeserialize(as = java.util.HashMap.class)
    private Map<String, IngestFlowMetrics> flows = new HashMap<String, IngestFlowMetrics>();

    /**
     * No args constructor for use in serialization.
     */
    public IngestApplicationMetricPostBody()
    {
    }

    /**
     *
     * @param messageCount Samples for message count.
     * @param responseTime Samples for response time.
     * @param errorCount Samples for error count.
     * @param flows Flow metrics.
     */
    public IngestApplicationMetricPostBody(Set<IngestMetric> messageCount,
                                           Set<IngestMetric> responseTime,
                                           Set<IngestMetric> errorCount,
                                           Map<String, IngestFlowMetrics> flows)
    {
        this.messageCount = messageCount;
        this.responseTime = responseTime;
        this.errorCount = errorCount;
        this.flows = flows;
    }

    @JsonProperty("message-count")
    public Set<IngestMetric> getMessageCount()
    {
        return messageCount;
    }

    @JsonProperty("message-count")
    public void setMessageCount(Set<IngestMetric> messageCount)
    {
        this.messageCount = messageCount;
    }

    @JsonProperty("response-time")
    public Set<IngestMetric> getResponseTime()
    {
        return responseTime;
    }

    @JsonProperty("response-time")
    public void setResponseTime(Set<IngestMetric> responseTime)
    {
        this.responseTime = responseTime;
    }

    @JsonProperty("error-count")
    public Set<IngestMetric> getErrorCount()
    {
        return errorCount;
    }

    @JsonProperty("error-count")
    public void setErrorCount(Set<IngestMetric> errorCount)
    {
        this.errorCount = errorCount;
    }

    @JsonProperty("flows")
    public Map<String, IngestFlowMetrics> getFlows()
    {
        return flows;
    }

    @JsonProperty("flows")
    public void setFlows(Map<String, IngestFlowMetrics> flows)
    {
        this.flows = flows;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(messageCount).append(responseTime).append(errorCount).append(flows).toHashCode();
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof IngestApplicationMetricPostBody))
        {
            return false;
        }
        IngestApplicationMetricPostBody rhs = ((IngestApplicationMetricPostBody) other);
        return new EqualsBuilder().append(messageCount, rhs.messageCount).append(responseTime, rhs.responseTime)
                .append(errorCount, rhs.errorCount).append(flows, rhs.flows).isEquals();
    }

}
