package org.kapott.hbci.structures;

import java.io.Serializable;
import java.math.BigDecimal;

public class BigDecimalValue implements Serializable {

    private BigDecimal value;
    
    private String curr;
    
    public BigDecimalValue(long value, String curr) {
        this(value, 2, curr);
    }
    
    public BigDecimalValue(long value, int scale, String curr) {
        this.value = new BigDecimal(value).scaleByPowerOfTen(-scale);
        this.curr = curr;
    }
    
    public BigDecimalValue(BigDecimal value, String curr) {
        this.value = value;
        this.curr = curr;
    }
    
    public BigDecimalValue(String value, String curr) {
        this.value = new BigDecimal(value);
        this.curr = curr;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public String getCurr() {
        return curr;
    }
    
    public void setCurr(String curr) {
        this.curr = curr;
    }
    
    @Override
    public String toString() {
        // TODO : Formatieren
        return value.toPlainString()+" "+curr;
    }
}
