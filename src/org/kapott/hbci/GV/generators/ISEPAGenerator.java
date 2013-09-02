package org.kapott.hbci.GV.generators;

import java.io.OutputStream;

import org.kapott.hbci.GV.HBCIJob;

public interface ISEPAGenerator {
	public void generate(HBCIJob job, OutputStream os); 
}
