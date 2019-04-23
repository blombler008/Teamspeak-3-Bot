/*
 * MIT License
 *
 * Copyright (c) 2019 blombler008
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.blombler008.teamspeak3bot.console;

import java.io.*;

public class PrintStreamLogger extends PrintStream {

    public PrintStream lg;
    public PrintStream out;

    public PrintStreamLogger(OutputStream out) {
        super(out);
    }

    public PrintStreamLogger(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public PrintStreamLogger(OutputStream out, boolean autoFlush, String encoding)
            throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public PrintStreamLogger(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public PrintStreamLogger(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public PrintStreamLogger(File file) throws FileNotFoundException {
        super(file);
    }

    public PrintStreamLogger(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void flush() {
        lg.flush();
        out.flush();
    }

    @Override
    public void close() {
        lg.close();
        out.close();
    }

    @Override
    public void write(int b) {
        lg.write(b);
        out.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        lg.write(buf, off, len);
        out.write(buf, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        lg.write(b);
        out.write(b);
    }

    public void writeSeparate(String str, boolean console) {

        if (console) {
            print(str);
        } else {
            lg.print(str);
        }
    }
}
