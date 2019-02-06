package com.example.common;

import java.util.ArrayList;

public class FileListServer extends AbstractMessage {
    private ArrayList<String> fileList;

    public FileListServer(ArrayList<String> list) {
        this.fileList = list;
    }

    public ArrayList<String> getFileList() {
        return fileList;
    }
}
