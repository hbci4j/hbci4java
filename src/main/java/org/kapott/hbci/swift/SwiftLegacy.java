package org.kapott.hbci.swift;

import org.kapott.hbci.exceptions.HBCI_Exception;

public class SwiftLegacy {

    public static String getTagValue(final String st, final String tag, final String[] suffixes, int counter) {
        // TODO: hier noch die parser-Änderungen einbauen, die schon bei
        // getTagValue() drin sind ("\r\n:" anstatt ":" suchen)
        String ret = null;

        int endpos = 0;
        while (true) {
            ret = null;

            int startpos = -1;
            String mytag = null;

            for (final String suffixe : suffixes) {
                final int p = st.indexOf(":" + tag + suffixe + ":", endpos);

                if (p != -1) {
                    if (startpos == -1) {
                        startpos = p;
                        mytag = tag + suffixe;
                    } else {
                        if (p < startpos) {
                            startpos = p;
                            mytag = tag + suffixe;
                        }
                    }
                }
            }

            if (startpos != -1) {
                endpos = st.indexOf("\r\n:", startpos);
                if (endpos == -1) {
                    endpos = st.indexOf("\r\n-", startpos);
                    while (endpos != -1 && endpos + 3 < st.length()) {
                        endpos = st.indexOf("\r\n-", endpos + 1);
                    }
                }
                if (endpos == -1) {
                    throw new HBCI_Exception("*** invalid swift stream - no end of tag found: tag=" + tag);
                }
                ret = st.substring(startpos + mytag.length() + 2, endpos);
            }

            if (counter-- == 0) {
                break;
            }
        }

        return ret;
    }


    public static String getLineFieldValue(final String stream, final String linenum, int fieldnum) {
        // TODO: hier evtl. sauberer parsen

        String ret = null;

        int linepos = 0;
        while (true) {
            if (linepos >= stream.length()) {
                break;
            }

            if (stream.charAt(linepos) == linenum.charAt(0)) {
                int end = stream.indexOf("\r\n", linepos);
                if (end == -1) {
                    end = stream.length();
                }
                final String line = stream.substring(linepos + 1, end);

                int fieldpos = 0;
                for (; fieldnum > 0; fieldnum--) {
                    final int p = line.indexOf("+", fieldpos);
                    if (p == -1) {
                        break;
                    }
                    fieldpos = p + 1;
                }

                if (fieldnum == 0) {
                    int p = line.indexOf("+", fieldpos);
                    if (p == -1) {
                        p = line.length();
                    }
                    ret = line.substring(fieldpos, p);
                    if (ret.length() == 0) {
                        ret = null;
                    }
                }

                break;
            }

            linepos = stream.indexOf("\r\n", linepos);
            if (linepos == -1) {
                break;
            }
            linepos += 2;
        }

        return ret;
    }
}
