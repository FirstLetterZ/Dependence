package com.zpf.apptest;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptest.R;
import com.google.gson.reflect.TypeToken;
import com.zpf.tool.type.TypeTokenUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private    List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickOne(View view) {
        try {
            TypeToken<List<Pair<String, Integer>>> token = new TypeToken<List<Pair<String, Integer>>>() {
            };
            Type type1 = token.getType();

            Pair<String, Integer> item = new Pair<String, Integer>("1", 1);
            list.add(item);
            Class<?> listCls = list.getClass();
            Class<?> tokenCls = token.getClass();
            Class<?> itemCls = item.getClass();
            Log.e("ZPF", "listCls=" + listCls.toString() + ";tokenCls=" + tokenCls.toString()+";itemCls="+itemCls);

            Type type2 = TypeTokenUtil.getClassType(itemCls);
            Type type3 = TypeTokenUtil.getClassType(listCls);
            Log.e("ZPF", "type1=" + type1.toString() + ";type2=" + type2.toString() + ";type3=" + type3.toString());
        } catch (Exception e) {
            Log.e("ZPF", "Exception==>" + e.getMessage());
        }
    }
    public void clickTwo(View view) {

    }
}