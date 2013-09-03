package org.kapott.hbci.GV.generators;

import java.io.ByteArrayOutputStream;

import org.kapott.hbci.GV.GVUebSEPA;
import org.kapott.hbci.GV.HBCIJob;

public class GenUebSEPA00100102 implements ISEPAGenerator{

	@Override
	public void generate(HBCIJob job, ByteArrayOutputStream os)
			throws Exception {
		generate((GVUebSEPA)job, os);
	}
	
	private void generate(GVUebSEPA job, ByteArrayOutputStream os){
		
		
	}
}
