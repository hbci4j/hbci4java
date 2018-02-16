package org.kapott.hbci.postprocessor;

import java.util.HashMap;
import java.util.Map;

/**
 * PostProcessor Klasse welche es erlaubt z.B. die Customer Id
 * während sie gesetzt wird zu manipulieren. Dies ermöglicht es
 * eine automatische Korrektur vorzunehmen wie beispielsweise bei
 * der Consorsbank welche am Ende der Customer Id eine "001" benötigt.
 */
public abstract class PostProcessor {

    private static final Map<String, PostProcessor> postProcessors = new HashMap<>();

    static {
        new ConsorsbankPostProcessor();
    }

    public PostProcessor(String blz) {
        if (postProcessors.containsKey(blz))
            throw new IllegalStateException(String.format("PostProcessor for BLZ %s already registered", blz));
        postProcessors.put(blz, this);
    }

    public static PostProcessor getPostProcessor(String blz) {
        return postProcessors.get(blz);
    }

    public String processHBCIVersion(String hbciVersion) {
        return hbciVersion;
    }
    public String processCountry(String country) {
        return country;
    }

    public String processBLZ(String blz) {
        return blz;
    }

    public String processHost(String host) {
        return host;
    }

    public Integer processPort(Integer port) {
        return port;
    }

    public String processFilterType(String filterType) {
        return filterType;
    }

    public String processUserId(String userid) {
        return userid;
    }

    public String processCustomerId(String customerid) {
        return customerid;
    }
}
