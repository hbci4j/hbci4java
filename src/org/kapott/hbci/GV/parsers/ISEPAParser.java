package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public interface ISEPAParser {
    public void parse(InputStream xml, ArrayList<Properties> sepaResults);
}
