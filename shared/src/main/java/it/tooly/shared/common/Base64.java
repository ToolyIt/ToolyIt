package it.tooly.shared.common;

/*
 Copyright (c) 2009, Mark Renouf
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of the <organization> nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY Mark Renouf ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL <copyright holder> BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Custom Base64 encode/decode implementation suitable for use in GWT applications (uses only translatable classes).
 */
public final class Base64 {

	/** The Constant HEX_3F. */
	private static final int HEX_3F = 0x3f;

	/** The Constant DEC_6. */
	private static final int DEC_6 = 6;

	/** The Constant HEX_03. */
	private static final int HEX_03 = 0x03;

	/** The Constant HEX_0F. */
	private static final int HEX_0F = 0x0f;

	/** The Constant DEC_4. */
	private static final int DEC_4 = 4;

	/** The Constant DEC_64. */
	private static final int DEC_64 = 64;

	/** The Constant HEX_7F. */
	private static final int HEX_7F = 0x7f;

	/**
	 * Private default constructor.
	 */
	private Base64() {

	}

	/** The Constant etab. */
	private static final String ETAB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	/** The dtab. */
	private static final byte[] DTAB =
	        { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	                        -1, -1, -1, -1, -1,
	                        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57,
	                        58, 59, 60, 61, -1, -1, -1,
	                        64, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
	                        21, 22, 23, 24, 25, -1, -1,
	                        -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
	                        45, 46, 47, 48, 49, 50, 51,
	                        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	/**
	 * Decode.
	 * 
	 * @param data the data
	 * @return the string
	 */
	public static byte[] decode(final byte[] data) {
		StringBuffer out = new StringBuffer();

		if (data != null) {
			// length must be multiple of 4 (with padding)
			if (data.length % DEC_4 != 0) {
				return new byte[0];
			}

			for (int i = 0; i < data.length;) {
				byte e0 = DTAB[data[i++] & HEX_7F];
				byte e1 = DTAB[data[i++] & HEX_7F];
				byte e2 = DTAB[data[i++] & HEX_7F];
				byte e3 = DTAB[data[i++] & HEX_7F];

				// Invalid characters in input
				if (e0 == -1 || e1 == -1 || e2 == -1 || e3 == -1) {
					return new byte[0];
				}

				byte d0 = (byte) ((e0 << 2) + (e1 >>> DEC_4 & HEX_03));
				byte d1 = (byte) ((e1 << DEC_4) + (e2 >>> 2 & HEX_0F));
				byte d2 = (byte) ((e2 << DEC_6) + (e3 & HEX_3F));

				out.append(Character.toString((char) d0));
				if (e2 != DEC_64) {
					out.append(Character.toString((char) d1));
				}
				if (e3 != DEC_64) {
					out.append(Character.toString((char) d2));
				}
			}
			return out.toString().getBytes();
		}

		return null;
	}

	/**
	 * Decode.
	 * 
	 * @param data the data
	 * @return the string
	 */
	public static String decode(final String data) {
		StringBuffer out = new StringBuffer();

		if (data != null) {
			// length must be multiple of 4 (with padding)
			if (data.length() % DEC_4 != 0) {
				return "";
			}

			for (int i = 0; i < data.length();) {
				byte e0 = DTAB[data.charAt(i++) & HEX_7F];
				byte e1 = DTAB[data.charAt(i++) & HEX_7F];
				byte e2 = DTAB[data.charAt(i++) & HEX_7F];
				byte e3 = DTAB[data.charAt(i++) & HEX_7F];

				// Invalid characters in input
				if (e0 == -1 || e1 == -1 || e2 == -1 || e3 == -1) {
					return "";
				}

				byte d0 = (byte) ((e0 << 2) + (e1 >>> DEC_4 & HEX_03));
				byte d1 = (byte) ((e1 << DEC_4) + (e2 >>> 2 & HEX_0F));
				byte d2 = (byte) ((e2 << DEC_6) + (e3 & HEX_3F));

				out.append(Character.toString((char) d0));
				if (e2 != DEC_64) {
					out.append(Character.toString((char) d1));
				}
				if (e3 != DEC_64) {
					out.append(Character.toString((char) d2));
				}
			}
			return out.toString();
		}

		return null;
	}

	/**
	 * Encode.
	 * 
	 * @param data the data
	 * @return the string
	 */
	public static String encode(final String data) {
		if (data != null) {
			StringBuffer out = new StringBuffer();

			int i = 0;
			int r = data.length();
			while (r > 0) {
				byte d0, d1, d2;
				byte e0, e1, e2, e3;

				d0 = (byte) data.charAt(i++);
				--r;
				e0 = safeUnsignedRightShift(d0, 2);
				e1 = (byte) ((d0 & HEX_03) << DEC_4);

				if (r > 0) {
					d1 = (byte) data.charAt(i++);
					--r;
					e1 += safeUnsignedRightShift(d1, DEC_4);
					e2 = (byte) ((d1 & HEX_0F) << 2);
				} else {
					e2 = DEC_64;
				}

				if (r > 0) {
					d2 = (byte) data.charAt(i++);
					--r;
					e2 += safeUnsignedRightShift(d2, DEC_6);
					e3 = (byte) (d2 & HEX_3F);
				} else {
					e3 = DEC_64;
				}
				out.append(ETAB.charAt(e0));
				out.append(ETAB.charAt(e1));
				out.append(ETAB.charAt(e2));
				out.append(ETAB.charAt(e3));
			}

			return out.toString();
		}
		return null;
	}

	/**
	 * Encode.
	 * 
	 * @param data the data
	 * @return the string
	 */
	public static byte[] encode(final byte[] data) {
		if (data != null) {
			StringBuffer out = new StringBuffer();

			int i = 0;
			int r = data.length;
			while (r > 0) {
				byte d0, d1, d2;
				byte e0, e1, e2, e3;

				d0 = data[i++];
				--r;
				e0 = safeUnsignedRightShift(d0, 2);
				e1 = (byte) ((d0 & HEX_03) << DEC_4);

				if (r > 0) {
					d1 = data[i++];
					--r;
					e1 += safeUnsignedRightShift(d1, DEC_4);
					e2 = (byte) ((d1 & HEX_0F) << 2);
				} else {
					e2 = DEC_64;
				}

				if (r > 0) {
					d2 = data[i++];
					--r;
					e2 += safeUnsignedRightShift(d2, DEC_6);
					e3 = (byte) (d2 & HEX_3F);
				} else {
					e3 = DEC_64;
				}
				out.append(ETAB.charAt(e0));
				out.append(ETAB.charAt(e1));
				out.append(ETAB.charAt(e2));
				out.append(ETAB.charAt(e3));
			}

			return out.toString().getBytes();
		}
		return null;
	}

	/**
	 * Safe unsigned right shift (>>> is unsafe with bytes and shorts) .
	 * 
	 * @param b the b
	 * @param offset the offset
	 * @return the byte
	 */
	private static byte safeUnsignedRightShift(final byte b, final int offset) {
		byte b2;
		if (b >= 0) {
			b2 = (byte) (b >> offset);
		} else {
			b2 = (byte) ((b >> offset) + (2 << ~offset));
		}
		return b2;
	}
}
