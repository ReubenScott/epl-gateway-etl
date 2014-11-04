package com.covidien.etl.download;

import java.util.Vector;

import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class FtpUtilTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testConnection() throws Exception {
		FtpUtil ftp = new FtpUtil();
		ChannelSftp sftp = ftp.getFtpConnection("10.243.118.76", 22, "root",
				"123456");
		try {
			Vector<LsEntry> v = sftp.ls("/var/sftp");
			for (LsEntry s : v) {
				System.out.println(s);
			}			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sftp.disconnect();
			ftp.getSession().disconnect();
		}
	}
}
