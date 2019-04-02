package com.tattyhost.teamspeak3bot.utils;

import java.io.*;
import java.util.Locale;

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

    public PrintStreamLogger(String fileName, String csn)
        throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public PrintStreamLogger(File file) throws FileNotFoundException {
        super(file);
    }

    public PrintStreamLogger(File file, String csn)
        throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override public void flush() {
        lg.flush();
        out.flush();
    }

    @Override public void close() {
        lg.close();
        out.close();
    }

    @Override public boolean checkError() {
        lg.checkError();
        out.checkError();
        return out.checkError();
    }

    @Override public void write(int b) {
        lg.write(b);
        out.write(b);
    }

    @Override public void write(byte[] buf, int off, int len) {
        lg.write(buf, off, len);
        out.write(buf, off, len);
    }

    @Override public void print(boolean b) {
        lg.print(b);
        out.print(b);
    }

    @Override public void print(char c) {
        lg.print(c);
        out.print(c);
    }

    @Override public void print(int i) {
        lg.print(i);
        out.print(i);
    }

    @Override public void print(long l) {
        lg.print(l);
        out.print(l);
    }

    @Override public void print(float f) {
        out.print(f);
        lg.print(f);
    }

    @Override public void print(double d) {
        out.print(d);
        lg.print(d);
    }

    @Override public void print(char[] s) {
        out.print(s);
        lg.print(s);
    }

    @Override public void print(String s) {
        out.print(s);
        lg.print(s);
    }

    @Override public void print(Object obj) {
        out.print(obj);
        lg.print(obj);
    }

    @Override public void println() {
        out.println();
        lg.println();
    }

    @Override public void println(boolean x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(char x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(int x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(long x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(float x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(double x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(char[] x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(String x) {
        out.println(x);
        lg.println(x);
    }

    @Override public void println(Object x) {
        out.println(x);
        lg.println(x);
    }

    @Override public PrintStream printf(String format, Object... args) {
        lg.printf(format, args);
        return out.printf(format, args);
    }

    @Override public PrintStream printf(Locale l, String format, Object... args) {
        lg.printf(l, format, args);
        return out.printf(l, format, args);
    }

    @Override public PrintStream format(String format, Object... args) {
        lg.format(format, args);
        return out.format(format, args);
    }

    @Override public PrintStream format(Locale l, String format, Object... args) {
        lg.format(l, format, args);
        return out.format(l, format, args);
    }

    @Override public PrintStream append(CharSequence csq) {
        lg.append(csq);
        return out.append(csq);
    }

    @Override public PrintStream append(CharSequence csq, int start, int end) {
        lg.append(csq, start, end);
        return out.append(csq, start, end);
    }

    @Override public PrintStream append(char c) {
        lg.append(c);
        return out.append(c);
    }

    @Override public void write(byte[] b) throws IOException {
        lg.write(b);
        out.write(b);
    }

}
