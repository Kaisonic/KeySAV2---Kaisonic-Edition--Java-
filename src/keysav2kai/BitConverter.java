/*
    Copyright 2015 Thomas DeBell (Kaisonic)

    This file is part of KeySAV2 - Kaisonic Edition.

    KeySAV2 - Kaisonic Edition is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    KeySAV2 - Kaisonic Edition is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This code is adapted from https://github.com/peace-maker/lysis-java/blob/master/src/lysis/BitConverter.java
 */

package keysav2kai;

import java.nio.ByteBuffer;

public class BitConverter {
	public static int ToInt16(byte[] bytes, int offset) {
		int result = ((int)bytes[offset]&0xff);
        result |= ((int)bytes[offset+1]&0xff) << 8;
        return (result & 0xffff);
    }
	
	public static int ToUInt16(byte[] bytes, int offset) {
		int result = ((int)bytes[offset+1]&0xff);
        result |= ((int)bytes[offset]&0xff) << 8;
        return result & 0xffff;
    }
    
    public static int ToUInt16LE(byte[] bytes, int offset) {
		int result = ((int)bytes[offset+1]&0xff) << 8;
        result |= ((int)bytes[offset]&0xff);
        return result & 0xffff;
    }
	
	public static int ToInt32(byte[] bytes, int offset) {
		int result = ((int)bytes[offset]&0xff);
        result |= ((int)bytes[offset+1]&0xff) << 8;
        result |= ((int)bytes[offset+2]&0xff) << 16;
        result |= ((int)bytes[offset+3]&0xff) << 24;
        return result;
    }
	
	public static long ToUInt32(byte[] bytes, int offset) {
        long result = ((int)bytes[offset]&0xff);
        result |= ((int)bytes[offset+1]&0xff) << 8;
        result |= ((int)bytes[offset+2]&0xff) << 16;
        result |= ((int)bytes[offset+3]&0xff) << 24;
        return result & 0xFFFFFFFFL;
    }
	
	public static long ToUInt64(byte[] bytes, int offset) {
        long result = 0;
        for (int i = 0; i <= 56; i += 8) {
			result |= ((int)bytes[offset++]&0xff) << i;
		}
        return result;
    }

	public static byte[] GetBytes(int value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >>> 24);
		bytes[1] = (byte) (value >>> 16);
		bytes[2] = (byte) (value >>> 8);
		bytes[3] = (byte) (value);
		return bytes;
	}
    
    public static byte[] GetBytesBE(int value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value);
		bytes[1] = (byte) (value >>> 8);
		bytes[2] = (byte) (value >>> 16);
		bytes[3] = (byte) (value >>> 24);
		return bytes;
	}
	
	public static byte[] GetBytes(long value) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (value >> 24);
		bytes[1] = (byte) (value >> 16);
		bytes[2] = (byte) (value >> 8);
		bytes[3] = (byte) (value);
		return bytes;
	}
	
	public static float ToSingle(byte[] b, long offset) {
		ByteBuffer buf = ByteBuffer.wrap(b, (int) offset, 4);
		float outp = buf.getFloat();
		return outp;
	}
}