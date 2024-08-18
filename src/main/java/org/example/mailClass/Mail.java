package org.example.mailClass;

public class Mail {
    String from;
    String to;
    String object;
    String content;
    boolean file;
    String fileName;
    String Trace;

    public Mail(String from, String to, String object, String content, boolean file,String fileName,String Trace) {
        this.from = from;
        this.to = to;
        this.object = object;
        this.content = content;
        this.file = file;
        this.fileName = fileName;
        this.Trace = Trace;
    }
    public Mail(String from, String to, String object, String content, boolean file,String Trace) {
        this.from = from;
        this.to = to;
        this.object = object;
        this.content = content;
        this.file = file;
        this.fileName = "";
        this.Trace = Trace;
    }

    public String getTrace(){return Trace;}

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getObject() {
        return object;
    }

    public String getContent() {
        return content;
    }

    public boolean hasFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return  "From: "+ from + " - Subj:" + object;
    }
}
