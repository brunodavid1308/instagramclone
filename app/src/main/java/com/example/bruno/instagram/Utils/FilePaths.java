package com.example.bruno.instagram.Utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by User on 7/24/2017.
 */

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    //"storage/emulated/0"

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    //public String STORIES = ROOT_DIR + "/Stories";


}
