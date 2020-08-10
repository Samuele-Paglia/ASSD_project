package it.unisannio.assd.project.util;

import java.util.ArrayList;
import java.util.List;

import it.unisannio.assd.tkn.key.ReportVerificationKey;
import it.unisannio.assd.tkn.key.TemporaryContactKey;
import it.unisannio.assd.tkn.key.TemporaryContactNumber;
import it.unisannio.assd.tkn.report.Report;
import it.unisannio.assd.tkn.report.ReportData;

public class TCNReport {

	private List<String> uuidList;
	private String memo;

	public TCNReport() { }

	public TCNReport(String hexString) {
		ReportData reportData =  Report.Companion.readReportDataFromByteArray(Util.hexStringToByteArray(hexString));
		TemporaryContactKey tck = reportData.getTck();
		ReportVerificationKey rvk = reportData.getRvk();
		short from = reportData.getFrom();
		short until = reportData.getUntil();
		this.memo = reportData.getMemo();
		this.uuidList = new ArrayList<String>();
		for (TemporaryContactNumber t : tck.contactNumbersBetween(rvk, from, until))
			uuidList.add(t.toString());
	}

	public List<String> getUuidList() {
		return uuidList;
	}

	public String getMemo() {
		return memo;
	}
}

