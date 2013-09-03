package org.kapott.hbci.GV.generators;



import java.io.ByteArrayOutputStream;

import org.kapott.hbci.GV.HBCIJob;

public interface ISEPAGenerator {
	public void generate(HBCIJob job, ByteArrayOutputStream os) throws Exception; 
}
