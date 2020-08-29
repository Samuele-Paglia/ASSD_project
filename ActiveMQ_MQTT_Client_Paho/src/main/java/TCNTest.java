import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.unisannio.assd.tkn.key.ReportVerificationKey;
import it.unisannio.assd.tkn.key.TemporaryContactKey;
import it.unisannio.assd.tkn.key.TemporaryContactNumber;
import it.unisannio.assd.tkn.report.Report;
import it.unisannio.assd.tkn.report.ReportData;

public class TCNTest {

	public static void main(String[] args) {
		
		long time = 1595258141198L;
		Date date = new Date(time);
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		System.out.println(date);
		System.out.println(df.format(date));
		
		String reportContent = "fd8deb9d91a13e144ca5b0ce14e289532e040fe0bf922c6e3dadb1e4e2333c"
				+ "78df535b90ac99bec8be3a8add45ce77897b1e7cb1906b5cff1097d3cb142f"
				+ "d9d002000a00000c73796d70746f6d206461746131078ec5367b67a8c793b7"
				+ "40626d81ba904789363137b5a313419c0f50b180d8226ecc984bf073ff89cb"
				+ "d9c88fea06bda1f0f368b0e7e88bbe68f15574482904";
		
		ReportData reportData =  Report.Companion.readReportDataFromByteArray(hexStringToByteArray(reportContent));
		TemporaryContactKey tck = reportData.getTck();
		ReportVerificationKey rvk = reportData.getRvk();
		short from = reportData.getFrom();
		short until = reportData.getUntil();
		String memo = reportData.getMemo();
		ArrayList<String> list = new ArrayList<String>();
		
		for (TemporaryContactNumber t : tck.contactNumbersBetween(rvk, from, until))
			list.add(t.toString());
		
		for (String uuid : list)
			System.out.println(uuid);
//		Report report = Report.Companion.readReportFromByteArray(reportContent.getBytes());
//		System.out.println(report.toString());
		System.out.println(memo);
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
}
