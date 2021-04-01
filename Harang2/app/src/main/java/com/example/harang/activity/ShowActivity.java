package com.example.harang.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.harang.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends ListActivity {

    File file;
    List myList;
    //String[] name = {"hello","MySQL","Hate","you"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myList = new ArrayList();

        // String path = getExternalFilesDir(null).toString();
        //String path = Environment.getExter
        // nalStorageDirectory().getAbsolutePath().toString() + "/files/";
        String path = getExternalFilesDir(null).toString()  + "/";
        //String path = "/storage/3064-3263/Download";
        // String path = "/sdcard/Android/data/com.amazonaws.demo.s3transferutility/files";

        file = new File(path);
        File[] list = file.listFiles();

        for(int i = 0; i<list.length; i++){
            myList.add(list[i].getName());

        }
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, myList));
    }

    protected void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l,v,position,id);

        File tmp = new File(file, (String) myList.get(position));

        if(!tmp.isFile()){
            file = new File(file, (String) myList.get(position));
            File list[] = file.listFiles();

            myList.clear();

            for(int i=0; i<list.length; i++){
                myList.add(list[i].getName());
            }
            Toast.makeText(getApplicationContext(),file.toString(),Toast.LENGTH_LONG).show();
            setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, myList));
        }
        String selection = l.getItemAtPosition(position).toString();
        Toast.makeText(this,selection,Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("title", selection);
        startActivity(intent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        String parent = file.getParent().toString();
        file = new File(parent);
        File list[] = file.listFiles();

        myList.clear();

        for(int i=0; i<list.length; i++){
            myList.add(list[i].getName());
        }

        Toast.makeText(getApplicationContext(),file.toString(),Toast.LENGTH_LONG).show();
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, myList));
    }
}
