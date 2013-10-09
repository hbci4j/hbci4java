package org.kapott.hbci.GV_Result;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

public class GVRTANMediaList extends HBCIJobResultImpl {
	
	public static class TANMediaInfo implements Serializable {
		public String 	mediaCategory;
		public String 	status;
		public String 	cardNumber;
		public String 	cardSeqNumber;
		public Integer	cardType;
		public Date 	validFrom;
		public Date 	validTo;
		public String 	tanListNumber;
		public String 	mediaName;
		public String	mobileNumber;
		public String	mobileNumberSecure;
		public Integer	freeTans;
		public Date		lastUse;
		public Date		activatedOn;
	}
	
	public GVRTANMediaList() {
		super();
		mediaList = new ArrayList<TANMediaInfo>();
		tanOption = -1;
	}
	
	private List<TANMediaInfo> mediaList;
	private Integer tanOption;

	public void add(TANMediaInfo info)
	{
		mediaList.add(info);
	}
	
	public List<TANMediaInfo> mediaList()
	{
		return mediaList;
	}
	
	public Integer getTanOption()
	{
		return tanOption;
	}
	
	public void setTanOption(Integer option)
	{
		tanOption = option;
	}
}
