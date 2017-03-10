package org.kapott.hbci.structures;

import java.math.BigDecimal;


public class TypedValue extends BigDecimalValue {
    public static final int TYPE_STCK=1;
    public static final int TYPE_WERT=2;
    public static final int TYPE_PROZENT=3;
    
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
        String sType;
        switch (type) {
        case TYPE_STCK:
            sType = "Stück";
            break;
        case TYPE_PROZENT:
            sType = "%";
            break;
        default:
            sType = "";
        }
        return getValue().toPlainString()+" "+getCurr() + " " + sType;
    }
}