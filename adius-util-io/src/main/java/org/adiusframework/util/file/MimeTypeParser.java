package org.adiusframework.util.file;

import java.io.File;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;

public class MimeTypeParser {
	public static final String ZIPMIMETYPE = "application/zip";

	public static final String TARMIMETYPE = "application/x-tar";

	public static final String UNKNOWNMIMETYPE = "application/octet-stream";

	private static final String MIMETYPEDETECTORCLASS = "eu.medsea.mimeutil.detector.MagicMimeMimeDetector";
	static {
		MimeUtil.registerMimeDetector(MIMETYPEDETECTORCLASS);
	}

	public static MimeType getMimeType(File file) {
		return MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file));
	}

	public static String getMimeTypeText(File file) {
		return MimeTypeParser.getMimeType(file).toString();
	}

	public static boolean isZipFile(String file) {
		return MimeTypeParser.isZipFile(MimeTypeParser.getMimeType(new File(file)));
	}

	public static boolean isZipFile(File file) {
		return MimeTypeParser.isZipFile(MimeTypeParser.getMimeType(file));
	}

	public static boolean isZipFile(MimeType type) {
		return type.toString().equals(ZIPMIMETYPE);
	}

	public static boolean isTarFile(File file) {
		return MimeTypeParser.isTarFile(MimeTypeParser.getMimeType(file));
	}

	public static boolean isTarFile(MimeType type) {
		return type.toString().equals(TARMIMETYPE);
	}

	public static boolean isCompressed(File file) {
		MimeType type = MimeTypeParser.getMimeType(file);
		return MimeTypeParser.isZipFile(type) || MimeTypeParser.isTarFile(type);
	}

	public static boolean isUnknown(File file) {
		return MimeTypeParser.getMimeType(file).toString().equals(UNKNOWNMIMETYPE);
	}
}
