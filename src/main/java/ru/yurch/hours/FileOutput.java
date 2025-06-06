package ru.yurch.hours;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileOutput implements Output {

    private String fileName;

    public FileOutput(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public void print(Object obj) {
        try (InputStream in = new BufferedInputStream(new ByteArrayInputStream(obj.toString().getBytes(Charset.forName("WINDOWS-1251"))));
             Writer out = new PrintWriter(new FileWriter(fileName, StandardCharsets.ISO_8859_1, false))) {
            int sym = in.read();
            while (sym > 0) {
                out.write(sym);
                sym = in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
