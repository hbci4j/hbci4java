package org.kapott.hbci.structures;

import java.math.BigDecimal;


public class TypedValue extends BigDecimalValue {
    public static final int TYPE_STCK=1;
    public static final int TYPE_WERT=2;
    
    private int type;
    
    public TypedValue(BigDecimal value, String curr, int type) {
        super(value, curr);
        this.type=type;
    }

    public TypedValue(String value, String curr, int type) {
        super(value, curr);
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return getValue().toPlainString()+" "+getCurr() 
                        + (type==TYPE_STCK ? " Stück" : "");
    }
}