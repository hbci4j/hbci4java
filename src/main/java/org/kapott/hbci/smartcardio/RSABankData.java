/**
 * 
 */
package org.kapott.hbci.smartcardio;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.kapott.hbci.exceptions.HBCI_Exception;

/**
 * @author axel
 *
 */
public class RSABankData {
    
    private int index;
    private String country;
    private String bankCode;
    private String userId;
    private int comService;
    private String comAddress;
    private String comSuffix;
    private String bankId;
    private String systemId;
    private String customerId;
    
    public RSABankData() {
    }
    
    public RSABankData(int index, byte[] record, byte[] customerIdData) {
      
      final Charset cs = SmartCardService.CHARSET;
      
        this.index = index;
        this.country = new String(record, 0, 3, cs).trim();
        this.bankCode = new String(record, 3, 30, cs).trim();
        this.userId = new String(record, 33, 30, cs).trim();
        this.comService = record[63];
        this.comAddress = new String(record, 64, 28, cs).trim();
        this.comSuffix = new String(record, 92, 2, cs).trim();
        this.bankId = new String(record, 94, 30, cs).trim();
        this.systemId = new String(record, 124, 30, cs).trim();
        this.customerId = customerIdData == null ? "" :new String(customerIdData, cs).trim();
    }
    
    private void fillRecord(byte[] record, int offset, int length, String value) {
        byte[] bytes = value.getBytes(SmartCardService.CHARSET);
        if (bytes.length > length)
            throw new HBCI_Exception("string value for bank data record at offset " + offset + " is " + bytes.length + " bytes long but must not be longer than " + length + " bytes");
        
        Arrays.fill(record, offset, offset + length, (byte) 0x20);
        System.arraycopy(bytes, 0, record, offset, bytes.length);
    }
    
    public byte[] toRecord() {
        byte[] result = new byte[154];
        fillRecord(result, 0, 3, country);
        fillRecord(result, 3, 30, bankCode);
        fillRecord(result, 33, 30, userId);
        result[63] = (byte) (comService & 0xFF);
        fillRecord(result, 64, 28, comAddress);
        fillRecord(result, 92, 2, comSuffix);
        fillRecord(result, 94, 30, bankId);
        fillRecord(result, 124, 30, systemId);
        return result;
    }
    
    public byte[] toCustomerIdData() {
        byte[] result = new byte[30];
        fillRecord(result, 0, 30, customerId);
        return result;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getComService() {
        return comService;
    }

    public void setComService(int comService) {
        this.comService = comService;
    }

    public String getComAddress() {
        return comAddress;
    }

    public void setComAddress(String comAddress) {
        this.comAddress = comAddress;
    }

    public String getComSuffix() {
        return comSuffix;
    }

    public void setComSuffix(String comSuffix) {
        this.comSuffix = comSuffix;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "index=" + index
             + " country=" + country
             + " bankCode=" + bankCode
             + " userId=" + userId
             + " comService=" + comService
             + " comAddress=" + comAddress
             + " comSuffix=" + comSuffix
             + " bankId=" + bankId
             + " systemId=" + systemId
             + " customerId=" + customerId;
    }

}
