/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.can.impl.canbus.converter.streams;

/**
 * write bits-at-a-time where the number of bits is between 1 and 32
 * Client programs must call <code>flush</code> or
 * <code>close</code> when finished writing or not all bits will be written.
 * <P>
 * Updated for version 2.0 to extend java.io.OutputStream
 * @author Owen Astrachan
 * @version 1.0, July 2000
 * @version 2.0, October 2004
 */

import java.io.*;

public class BitOutputStream extends OutputStream {

	private OutputStream myOutput;
	private int myBuffer;
	private int myBitsToGo;

	private static final int bmask[] = { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f,
			0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff,
			0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff,
			0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
			0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

	private static final int BITS_PER_BYTE = 8;

	/**
	 * Required by OutputStream subclasses, write the low 8-bits to the
	 * underlying outputstream
	 */
	public void write(int b) throws IOException {
		myOutput.write(b);
	}

	public BitOutputStream(OutputStream out) {
		myOutput = out;
		initialize();
	}

	private void initialize() {
		myBuffer = 0;
		myBitsToGo = BITS_PER_BYTE;
	}

	/**
	 * Construct a bit-at-a-time output stream with specified file name
	 * 
	 * @param filename
	 *            is the name of the file being written
	 */
	public BitOutputStream(String filename) {
		try {
			myOutput = new BufferedOutputStream(new FileOutputStream(filename));
		} catch (FileNotFoundException fnf) {
			System.err.println("could not create " + filename + " " + fnf);
		} catch (SecurityException se) {
			System.err.println("security exception on write " + se);
		}
		initialize();
	}

	/**
	 * Flushes bits not yet written, must be called by client programs if
	 * <code>close</code> isn't called.
	 * 
	 */
	public void flush() {
		if (myBitsToGo != BITS_PER_BYTE) {
			try {
				write((myBuffer << myBitsToGo));
			} catch (java.io.IOException ioe) {
				System.err.println("error writing bits on flush " + ioe);
			}
			myBuffer = 0;
			myBitsToGo = BITS_PER_BYTE;
		}

		try {
			myOutput.flush();
		} catch (java.io.IOException ioe) {
			System.err.println("error on flush " + ioe);
		}
	}

	/**
	 * releases system resources associated with file and flushes bits not yet
	 * written. Either this function or flush must be called or not all bits
	 * will be written
	 * 
	 */
	public void close() {
		flush();
		try {
			myOutput.close();
		} catch (IOException ioe) {
			System.err.println("error closing BitOutputStream " + ioe);
		}
	}

	/**
	 * write bits to file
	 * 
	 * @param howManyBits
	 *            is number of bits to write (1-32)
	 * @param value
	 *            is source of bits, rightmost bits are written
	 */

	public void write(int howManyBits, int value) {
		value &= bmask[howManyBits]; // only right most bits valid

		while (howManyBits >= myBitsToGo) {
			myBuffer = (myBuffer << myBitsToGo)
					| (value >> (howManyBits - myBitsToGo));
			try {
				write(myBuffer);
			} catch (java.io.IOException ioe) {
				System.err.println("error writing bits " + ioe);
			}

			value &= bmask[howManyBits - myBitsToGo];
			howManyBits -= myBitsToGo;
			myBitsToGo = BITS_PER_BYTE;
			myBuffer = 0;
		}

		if (howManyBits > 0) {
			myBuffer = (myBuffer << howManyBits) | value;
			myBitsToGo -= howManyBits;
		}
	}
}