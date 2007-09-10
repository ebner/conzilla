/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

	private static String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;

		if ((in == null) || (in.length < 1)) {
			return null;
		}

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0);
			ch = (byte) (ch >>> 4);
			ch = (byte) (ch & 0x0F);
			out.append(pseudo[(int) ch]);
			ch = (byte) (in[i] & 0x0F);
			out.append(pseudo[(int) ch]);
			i++;
		}

		String rslt = new String(out);

		return rslt;
	}

	public static String md5(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(source.getBytes());
			return byteArrayToHexString(bytes);
		} catch (NoSuchAlgorithmException nsae) {
			return null;
		}
	}

	public static String sha(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] bytes = md.digest(source.getBytes());
			return byteArrayToHexString(bytes);
		} catch (NoSuchAlgorithmException nsae) {
			return null;
		}
	}

}