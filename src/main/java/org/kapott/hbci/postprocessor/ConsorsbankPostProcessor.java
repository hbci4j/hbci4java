package org.kapott.hbci.postprocessor;

/**
 * PostProcessor für Consorsbank Konten
 */
public class ConsorsbankPostProcessor extends PostProcessor {

    public ConsorsbankPostProcessor() {
        super("76030080");
    }

    @Override
    public String processCustomerId(String customerId) {
        return customerId.length() < 10 ? customerId + "001" : customerId;
    }

    @Override
    public String processUserId(String userId) {
        return userId.length() < 10 ? userId + "001" : userId;
    }
}
