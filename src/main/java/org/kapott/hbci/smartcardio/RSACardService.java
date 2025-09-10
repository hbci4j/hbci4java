/**
 * 
 */
package org.kapott.hbci.smartcardio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CommandAPDU;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * @author axel
 *
 */
public class RSACardService extends HBCICardService
{
    private byte[] cid;
    
    /**
     * @see org.kapott.hbci.smartcardio.SmartCardService#init(javax.smartcardio.Card)
     */
    @Override
    protected void init(Card card)
    {
        super.init(card);
        
//        ATR atr = card.getATR();
//        if (!Arrays.equals(atr.getBytes(), new byte[]{
//            (byte) 0x3b,
//            (byte) 0xb7,
//            (byte) 0x94,
//            (byte) 0x00,
//            (byte) 0x81,
//            (byte) 0x31,
//            (byte) 0xfe,
//            (byte) 0x65,
//            (byte) 0x53,
//            (byte) 0x50,
//            (byte) 0x4b,
//            (byte) 0x32,
//            (byte) 0x33,
//            (byte) 0x90,
//            (byte) 0x00,
//            (byte) 0xd1
//            })) {
//            throw new HBCI_Exception("card has wrong ATR");
//        }
        
        this.selectFile(0x3F00);
        this.selectFile(0x2F02);
        byte[] data = readBinary(0, 0);
        
        SimpleTLV tlv = new SimpleTLV(data);
        if (tlv.isMalformed() || tlv.getSize() != 1 || tlv.getTag(0) != 0x5A) {
            throw new HBCI_Exception("malformed tlv for fid 0x2F02");
        }
        byte[] content = tlv.getContent(0);
        if (content.length != 10) {
            throw new HBCI_Exception("malformed tlv for fid 0x2F02");
        }
        
        this.cid = new byte[5];
        System.arraycopy(content, 4, cid, 0, 5);
        
        this.selectFile(0xA600);
    }
    
    /**
     * @see org.kapott.hbci.smartcardio.HBCICardService#getCID()
     */
    @Override
    public String getCID()
    {
      return new String(this.cid,SmartCardService.CHARSET);
    }
    
    /**
     * @see org.kapott.hbci.smartcardio.HBCICardService#createPINVerificationDataStructure(int)
     */
    @Override
    protected byte[] createPINVerificationDataStructure(int pwdId) throws IOException {
        ByteArrayOutputStream verifyCommand = new ByteArrayOutputStream();
        verifyCommand.write(0xff); // bTimeOut
        verifyCommand.write(0x00); // bTimeOut2
        verifyCommand.write(0x82); // bmFormatString
        verifyCommand.write(0x00); // bmPINBlockString
        verifyCommand.write(0x00); // bmPINLengthFormat
        verifyCommand.write(new byte[] {(byte) 0x20,(byte) 0x00}); // PIN size (max/min)
        verifyCommand.write(0x02); // bEntryValidationCondition
        verifyCommand.write(0x01); // bNumberMessage
        verifyCommand.write(new byte[] { 0x04, 0x09 }); // wLangId
        verifyCommand.write(0x00); // bMsgIndex
        verifyCommand.write(new byte[] { 0x00, 0x00, 0x00 }); // bTeoPrologue
        byte[] verifyApdu = new byte[] {
            SECCOS_CLA_STD, // CLA
            SECCOS_INS_VERIFY, // INS
            0x00, // P1
            (byte) (SECCOS_PWD_TYPE_DF | pwdId), // P2
            0x08, // Lc = 8 bytes in command data
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20,
            (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20 };
        verifyCommand.write(verifyApdu.length & 0xff); // ulDataLength[0]
        verifyCommand.write(0x00); // ulDataLength[1]
        verifyCommand.write(0x00); // ulDataLength[2]
        verifyCommand.write(0x00); // ulDataLength[3]
        verifyCommand.write(verifyApdu); // abData
        return verifyCommand.toByteArray();
    }
    
    //
    // this seems to be needed only when data on chip card should be changed
    //
//    private void verifyModifyPIN(int pwdId) {
//        byte[] body = new byte[] {(byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, 
//                                  (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20};
//        
//        System.arraycopy(cid, 0, body, 0, cid.length);
//        
//        CommandAPDU command = new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_VERIFY,
//                                              (byte) 0x00, (byte) (SECCOS_PWD_TYPE_DF | pwdId),
//                                              body);
//        send(command);
//    }
    
    /**
     * @see org.kapott.hbci.smartcardio.HBCICardService#verifySoftPIN(int, byte[])
     */
    @Override
    public void verifySoftPIN(int pwdId, byte[] softPin) {
        if (softPin.length > 8)
            throw new HBCI_Exception("illegal PIN size");
        
        byte[] body = new byte[] {(byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20, 
                                  (byte) 0x20, (byte) 0x20, (byte) 0x20, (byte) 0x20};
        
        System.arraycopy(softPin, 0, body, 0, softPin.length);
        
        CommandAPDU command = new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_VERIFY,
                                              (byte) 0x00, (byte) (SECCOS_PWD_TYPE_DF | pwdId),
                                              body);
        send(command);
    }
    
    /**
     * @param idx
     * @return
     */
    public RSABankData readBankData(int idx) {
        selectFile(0xA603);
        byte[] rawData  = readRecordBySFI(0x00, idx);
        if (rawData == null)
            return null;
        
        byte[] customerIdData = null;
        try {
            selectFile(0xA604);
            byte[] prefix = readBinary(0, 1);
            HBCIUtils.log("A604 prefix = " + prefix[0], HBCIUtils.LOG_DEBUG);
            int max = prefix[0] >> 4;
            byte[] states = readBinary(1, max);
            for (int n = 0; n < max; n++) {
                if ((states[n] >> 4) == (idx + 1) && (states[n] & 0x01) != 0) {
                    customerIdData = readBinary(1 + max + (n * 3 * 30), 30);
                    break;
                }
            }
        } catch (HBCI_Exception e) {
            HBCIUtils.log(e, HBCIUtils.LOG_DEBUG);
            // properly there are no information about customer id on this card
            customerIdData = null;
        }
        
        return new RSABankData(idx, rawData, customerIdData);
    }
    
    /**
     * @param idx
     * @param bankData
     */
    public void writeBankData(int idx, RSABankData bankData) {
        byte[] rawData = bankData.toRecord();
        
        HBCIUtils.log("bankData=" + toHex(rawData), HBCIUtils.LOG_DEBUG);
//        selectFile(0xA603);
//        updateRecordBySFI(0x00, idx, rawData);
    }
    
    /**
     * @param idx
     * @return
     */
    public RSAKeyData[] readKeyData(int idx) {
        selectFile(0xB300);
        byte[] verifyPKData = null;
        byte[] encipherPKData = null;
        byte[] signPKData = null;
        byte[] decipherPKData = null;
        byte[] pkCount = readBinary(0, 1);
        if (pkCount != null && pkCount.length == 1) {
            for (int n = 0; n < pkCount[0]; n++) {
                byte[] pkData = readBinary(1 + (n * 0x79), 0x79);
                if (pkData != null && pkData.length == 0x79) {
                    if (pkData[0] == (byte) (0x91 + idx))
                        verifyPKData = pkData;
                    if (pkData[0] == (byte) (0x96 + idx))
                        encipherPKData = pkData;
                    if (pkData[0] == (byte) (0x81 + idx))
                        signPKData = pkData;
                    if (pkData[0] == (byte) (0x86 + idx))
                        decipherPKData = pkData;
                }
            }
        }
        selectFile(0xA602);
        byte[] rawData = readBinary(1 + (idx * 4 * 8), 4 * 8);
        if (rawData == null)
            return null;
        return new RSAKeyData[]{
                        new RSAKeyData(idx, RSAKeyData.Type.VERIFY, rawData, verifyPKData),
                        new RSAKeyData(idx, RSAKeyData.Type.ENCIPHER, rawData, encipherPKData),
                        new RSAKeyData(idx, RSAKeyData.Type.SIGN, rawData, signPKData),
                        new RSAKeyData(idx, RSAKeyData.Type.DECIPHER, rawData, decipherPKData)
                        };
    }
    
    /**
     * @param idx
     * @return
     */
    public int readSigId(int idx) {
        selectFile(0xA601);
        byte[] rawData  = readRecordBySFI(0x00, idx);
        if (rawData == null)
            return 0;
        return ((rawData[0] & 0xFF) << 24) | ((rawData[1] & 0xFF) << 16) | ((rawData[2] & 0xFF) << 8) | (rawData[3] & 0xFF);
    }
    
    /**
     * @param idx
     * @param sigId
     */
    public void writeSigId(int idx, int sigId) {
        // Dont' write anything! SigId is incremented by card itself.
//        byte[] rawData=new byte[4];
//        rawData[0]=(byte) ((sigId >> 24) & 0xFF);
//        rawData[1]=(byte) ((sigId >> 16) & 0xFF);
//        rawData[2]=(byte) ((sigId >>  8) & 0xFF);
//        rawData[3]=(byte)  (sigId        & 0xFF);
//        
//        HBCIUtils.log("sidId=" + toHex(rawData), HBCIUtils.LOG_DEBUG);
//        //selectFile(0xA601);
//        //updateRecordBySFI(0x00, idx, rawData);
    }
    
    /**
     * @param idx
     * @param data
     * @return
     */
    public byte[] sign(int idx, byte[] data) {
        // MANAGE SE (activate my sig key)
        send(new CommandAPDU(SECCOS_CLA_STD, 0x22, 0x41, 0xB6 /*signature*/, new byte[]{
                        (byte) 0x84, // private key
                        (byte) 0x01, // length
                        (byte) (0x81 + idx), // kid
                        (byte) 0x83, // public key
                        (byte) 0x01, // length
                        (byte) (0x81 + idx), // kid
                        (byte) 0x80, // algorithm
                        (byte) 0x01, // length
                        (byte) 0x25  // HBCI
                        }));
        // PUT HASH
        send(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x90, 0x81, data));
        // SIGN
        return receive(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x9e, 0x9a, 256));
    }
    
    /**
     * @param idx
     * @param data
     * @param sig
     * @return
     */
    public boolean verify(int idx, byte[] data, byte[] sig) {
        // MANAGE SE (activate inst sig key)
        send(new CommandAPDU(SECCOS_CLA_STD, 0x22, 0x41, 0xB6 /*signature*/, new byte[]{
                        (byte) 0x84, // private key
                        (byte) 0x01, // length
                        (byte) (0x81 + idx), // kid
                        (byte) 0x83, // public key
                        (byte) 0x01, // length
                        (byte) (0x91 + idx), // kid
                        (byte) 0x80, // algorithm
                        (byte) 0x01, // length
                        (byte) 0x25  // HBCI
                        }));
        // PUT HASH
        send(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x90, 0x81, data));
        // VERIFY
        try {
            send(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x00, 0xa8, sig));
        } catch (HBCI_Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * @param idx
     * @param data
     * @return
     */
    public byte[] encipher(int idx, byte[] data) {
        // MANAGE SE (activate inst enc key)
        send(new CommandAPDU(SECCOS_CLA_STD, 0x22, 0x41, 0xB8 /*cipher*/, new byte[]{
                        (byte) 0x84, // private key
                        (byte) 0x01, // length
                        (byte) (0x86 + idx), // kid
                        (byte) 0x83, // public key
                        (byte) 0x01, // length
                        (byte) (0x96 + idx) // kid
                        }));
        // ENCIPHER
        byte[] buffer = receive(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x86, 0x80, data, 256));
        // strip padding indicator
        byte[] result = new byte[buffer.length - 1];
        System.arraycopy(buffer, 1, result, 0, result.length);
        return result;
    }
    
    /**
     * @param idx
     * @param data
     * @return
     */
    public byte[] decipher(int idx, byte[] data) {
        // insert padding indicator
        byte[] buffer = new byte[data.length + 1];
        buffer[0] = 0;
        System.arraycopy(data, 0, buffer, 1, data.length);
        // MANAGE SE (activate my enc key)
        send(new CommandAPDU(SECCOS_CLA_STD, 0x22, 0x41, 0xB8 /*cipher*/, new byte[]{
                        (byte) 0x84, // private key
                        (byte) 0x01, // length
                        (byte) (0x86 + idx), // kid
                        (byte) 0x83, // public key
                        (byte) 0x01, // length
                        (byte) (0x86 + idx) // kid
        }));
        // DECIPHER
        return receive(new CommandAPDU(SECCOS_CLA_STD, 0x2a, 0x80, 0x86, buffer, 256));
    }
    
    private static class SimpleTLV {
        
        private final boolean malformed;
        private final byte[] data;
        private final byte[] tags;
        private final byte[][] contents;
        
        public SimpleTLV(byte[] data) {
            List<Byte> tags = new ArrayList<Byte>();
            List<byte[]> contents = new ArrayList<byte[]>();
            
            int pos = 0;
            while (pos < data.length) {
                if (data[pos] == (byte) 0x00 || data[pos] == (byte) 0xFF) {
                    pos++;
                } else {
                    if (data.length < (pos + 2)) {
                        pos = Integer.MAX_VALUE;
                        break;
                    }
                    byte tag = data[pos++];
                    byte len = data[pos++];
                    int length;
                    if (len == (byte) 0xFF) {
                        if (data.length < (pos + 2)) {
                            pos = Integer.MAX_VALUE;
                            break;
                        }
                        byte len1 = data[pos++];
                        byte len2 = data[pos++];
                        length = ((len1 & 0xFF) << 8) | (len2 & 0xFF);
                    } else {
                        length = len & 0xFF;
                    }
                    if (data.length < (pos + length)) {
                        pos = Integer.MAX_VALUE;
                        break;
                    }
                    byte[] content = new byte[length];
                    System.arraycopy(data, pos, content, 0, length);
                    pos += length;
                    tags.add(tag);
                    contents.add(content);
                }
            }
            
            byte[] tempTags = new byte[tags.size()];
            for (int n = 0; n < tags.size(); n++)
                tempTags[n] = tags.get(n);
            
            this.malformed = pos == Integer.MAX_VALUE;
            this.data = data.clone();
            this.tags = this.malformed ? new byte[0] : tempTags;
            this.contents = this.malformed ? new byte[0][0] : contents.toArray(new byte[contents.size()][]);
        }
        
        public boolean isMalformed() {
            return malformed;
        }
        
        public byte[] getData() {
            return data.clone();
        }
        
        public int getSize() {
            return tags.length;
        }
        
        public byte getTag(int idx) {
            return tags[idx];
        }
        
        public byte[] getContent(int idx) {
            return contents[idx].clone();
        }
        
    }

}
