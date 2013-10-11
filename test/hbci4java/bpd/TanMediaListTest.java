package hbci4java.bpd;

import org.junit.Test;
import hbci4java.AbstractTest;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci.rewrite.Rewrite;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.lang.reflect.Constructor;

public class TanMediaListTest extends AbstractTest {

	@Test
	public void test() throws Exception {
	    String data = getFile("bpd/bpd-tanmedialist.txt");
	    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");

	    
	    Rewrite.setData("msgName","CustomMsg");
      // liste der rewriter erzeugen
      String rewriters_st=HBCIUtils.getParam("kernel.rewriter");
      ArrayList<Rewrite> al=new ArrayList<Rewrite>();
      StringTokenizer tok=new StringTokenizer(rewriters_st,",");
      while (tok.hasMoreTokens()) {
          String rewriterName=tok.nextToken().trim();
          if (rewriterName.length()!=0) {
              Class cl=this.getClass().getClassLoader().loadClass("org.kapott.hbci.rewrite.R"+
                                                                  rewriterName);
              Constructor con=cl.getConstructor((Class[])null);
              Rewrite rewriter=(Rewrite)(con.newInstance((Object[])null));
              al.add(rewriter);
          }
      }
      Rewrite[] rewriters= al.toArray(new Rewrite[al.size()]);

	    kernel.rawNewMsg("CustomMsg");
	    
	    MsgGen gen = kernel.getMsgGen();
	    
      // alle patches für die unverschlüsselte nachricht durchlaufen
	    String newmsgstring = data;
	    
	    
      for (int i=0;i<rewriters.length;i++) {
          newmsgstring=rewriters[i].incomingClearText(newmsgstring,gen);
      }

	    MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes",newmsgstring,newmsgstring.length(),gen);
	    Hashtable<String,String> ht = new Hashtable<String,String>();
	    msg.extractValues(ht);  
	}

}
