package org.adiusframework.util.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.file.FileUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

	public static final int FTP_DATA_TIMEOUT = 20000;

	public static final String FTP_PROTOCOL_PREFIX = "ftp://";

	public static final String FTP_ROOT = "//";

	private static final String FTP_FOLDER_DELIMITER = "/";

	public static boolean isFtpUrl(String url) {
		return url.startsWith(FTP_PROTOCOL_PREFIX);
	}

	public static String copyToFtpServer(FtpConnectionContainer connection, File localeFile, String ftpPath) {

		// first we have to trim the protocol prefix
		LOGGER.debug("Copy to ftp server called with " + localeFile + " and " + ftpPath);
		String tmpFtpPath = trimProtocol(ftpPath);
		int pos = tmpFtpPath.lastIndexOf(FTP_FOLDER_DELIMITER);
		String ftpFolder = FTP_ROOT;
		if (pos > -1)
			ftpFolder = tmpFtpPath.substring(0, pos);
		String ftpFile = tmpFtpPath.substring(pos + 1, tmpFtpPath.length());
		LOGGER.debug("Copy to ftp folder " + ftpFolder + " and file " + ftpFile);
		return copyToFtpServer(connection, localeFile, ftpFolder, ftpFile);
	}

	public static String copyToFtpServer(FtpConnectionContainer connection, File localeFile) {
		return copyToFtpServer(connection, localeFile, null, null);
	}

	private static boolean checkFileSize(FTPClient client, String ftpFile, File localeFile) throws IOException {
		boolean equal = true;
		client.setDataTimeout(FTP_DATA_TIMEOUT);
		LOGGER.debug("Determining file size of " + ftpFile);
		FTPFile[] files = client.listFiles(ftpFile);
		if (files.length > 0)
			equal = files[0].getSize() != localeFile.length();
		return equal;
	}

	public static String copyToFtpServer(FtpConnectionContainer connection, File localeFile, String ftpFolder,
			String ftpFileName) {
		FTPClient client = createAndConnectClient(connection);
		InputStream input = null;
		try {

			// first we determine the working directory and change to it
			String tmpFtpFolder = ftpFolder;
			if (tmpFtpFolder == null || tmpFtpFolder.isEmpty())
				tmpFtpFolder = FTP_ROOT;
			String tmpFtpFileName = ftpFileName;
			if (tmpFtpFileName == null || tmpFtpFileName.isEmpty())
				tmpFtpFileName = localeFile.getName();
			LOGGER.debug("Changing directory to " + tmpFtpFolder);
			if (!client.changeWorkingDirectory(tmpFtpFolder)) {
				if (!client.makeDirectory(tmpFtpFolder))
					throw new FtpConnectionException("Making of directory failed: " + client.getReplyString());
				if (!client.changeWorkingDirectory(tmpFtpFolder))
					throw new FtpConnectionException("Changing of directory failed: " + client.getReplyString());
			}

			// next we check if the file already exists, if so we check if the
			// file size has changed. the copy process is started if this is the
			// case.
			boolean copy = checkFileSize(client, tmpFtpFileName, localeFile);
			if (copy) {
				client.setFileType(FTP.BINARY_FILE_TYPE);
				input = new FileInputStream(localeFile);
				LOGGER.debug("Storing " + localeFile + " at " + tmpFtpFileName + " on ftp " + tmpFtpFolder);
				if (!client.storeFile(tmpFtpFileName, input))
					throw new FtpConnectionException("Copy to ftp server failed, reply of server: "
							+ client.getReplyString());
			} else
				LOGGER.info("File is not copied, because file size matches.");

			// building the string to connect the file again
			return FTP_PROTOCOL_PREFIX
					+ (tmpFtpFolder.startsWith(FTP_ROOT) ? tmpFtpFolder.substring(FTP_ROOT.length()) : tmpFtpFolder)
					+ (tmpFtpFolder.endsWith(FTP_FOLDER_DELIMITER) ? "" : FTP_FOLDER_DELIMITER) + tmpFtpFileName;
		} catch (SocketException e) {
			LOGGER.debug("SocketException: " + e.getMessage());
			throw new FtpConnectionException(e.getMessage());
		} catch (IOException e) {
			LOGGER.debug("IOException: " + e.getMessage());
			throw new FtpConnectionException(e.getMessage());
		} finally {
			try {
				if (input != null)
					input.close();
				if (client.isConnected())
					client.disconnect();
			} catch (IOException e) {
				throw new FtpConnectionException(e.getMessage());
			}
		}
	}

	public static String copyToFile(FtpConnectionContainer connection, String ftpFile, String localeFile) {

		// create a client, connect and login
		FTPClient client = createAndConnectClient(connection);
		OutputStream outStream = null;
		try {

			// check if protocol prefix is given in file
			String tmpFtpFile = trimProtocol(ftpFile);
			boolean copy = checkFileSize(client, tmpFtpFile, new File(localeFile));
			if (!copy) {
				LOGGER.info("File is not copied, because file size matches.");
				return localeFile;
			}

			// get file in binary mode
			client.setFileType(FTP.BINARY_FILE_TYPE);
			outStream = new FileOutputStream(localeFile);

			// at least copy the file to the local file
			LOGGER.debug("Retrieving file " + tmpFtpFile + " on FTP, storing to " + localeFile);
			client.setDataTimeout(FTP_DATA_TIMEOUT);
			if (!client.retrieveFile(tmpFtpFile, outStream))
				throw new FtpConnectionException("Retrieving of file " + tmpFtpFile + " failed (reply code "
						+ client.getReplyCode() + "), correct ftp file path?");
			return localeFile;
		} catch (SocketException e) {
			LOGGER.debug("SocketException: " + e.getMessage());
			throw new FtpConnectionException(e.getMessage());
		} catch (IOException e) {
			LOGGER.debug("IOException: " + e.getMessage());
			throw new FtpConnectionException(e.getMessage());
		} finally {
			try {
				if (outStream != null)
					outStream.close();
				if (client.isConnected())
					client.disconnect();
			} catch (IOException e) {
				throw new FtpConnectionException(e.getMessage());
			}
		}
	}

	public static String copyToTemporaryFile(FtpConnectionContainer connection, String ftpFile) {
		return copyToTemporaryFile(connection, ftpFile, System.getProperty("java.io.tmpdir"));
	}

	public static String copyToTemporaryFile(FtpConnectionContainer connection, String ftpFile, String tmpFolder) {

		// check if protocol prefix is given in file
		String tmpFtpFile = trimProtocol(ftpFile);
		String tmpFile = FileUtil.generateTemporaryFile(FileUtil.TMP_FILE_PREFIX,
				"." + FileUtil.getFileExtension(tmpFtpFile), tmpFolder);
		return copyToFile(connection, ftpFile, tmpFile);
	}

	public static void delete(FtpConnectionContainer connection, String ftpFile, boolean directory) {

		// create a client, connect and login
		FTPClient client = createAndConnectClient(connection);
		try {
			String tmpFtpFile = trimProtocol(ftpFile);
			LOGGER.debug("Deleting file " + ftpFile + " from FTP");
			if (!client.deleteFile(tmpFtpFile))
				throw new FtpConnectionException("Deletion of file " + tmpFtpFile + " failed: "
						+ client.getReplyString());
			if (directory) {
				int index = 0;
				String folder = tmpFtpFile;
				while ((index = folder.lastIndexOf(FTP_FOLDER_DELIMITER)) > -1) {
					folder = folder.substring(0, index);
					if (client.listFiles(folder).length == 0) {
						LOGGER.debug("Deleting folder " + folder);
						if (!client.removeDirectory(folder))
							throw new FtpConnectionException("Deletion of folder " + folder + " failed.");
					} else
						break;
				}
			}
		} catch (SocketException e) {
			throw new FtpConnectionException(e.getMessage());
		} catch (IOException e) {
			throw new FtpConnectionException(e.getMessage());
		} finally {
			try {
				if (client.isConnected())
					client.disconnect();
			} catch (IOException e) {
				throw new FtpConnectionException(e.getMessage());
			}
		}
	}

	private static String trimProtocol(String ftpFile) {
		String tmpFtpFile = ftpFile;
		if (ftpFile.startsWith(FTP_PROTOCOL_PREFIX))
			tmpFtpFile = ftpFile.substring(FTP_PROTOCOL_PREFIX.length());
		return tmpFtpFile;
	}

	private static FTPClient createAndConnectClient(FtpConnectionContainer connection) {
		FTPClient client = new FTPClient();
		try {

			// first we connect to the client
			LOGGER.debug("Connecting to server " + connection.getHost() + " on port " + connection.getPort());
			client.connect(connection.getHost(), connection.getPort());
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				client.disconnect();
				throw new FtpConnectionException("Connection to FTP server failed, reply code " + reply);
			}

			// now we can login
			LOGGER.debug("Login to client using " + connection.getUser());
			if (client.login(connection.getUser(), connection.getPassword())) {
				LOGGER.debug("Login successfully completed (reply code " + client.getReplyCode() + ")");
				return client;
			}
			throw new FtpConnectionException("Login to FTP server failed, wrong user or password?");
		} catch (SocketException e) {
			throw new FtpConnectionException(e.getMessage());
		} catch (IOException e) {
			throw new FtpConnectionException(e.getMessage());
		}
	}
}
