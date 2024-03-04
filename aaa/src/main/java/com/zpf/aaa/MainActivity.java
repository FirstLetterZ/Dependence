package com.zpf.aaa;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zpf.tool.EmojiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test1).setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
//            startActivity(new Intent(this, TestActivity.class));
        });
        findViewById(R.id.btn_test2).setOnClickListener(v -> {
            startActivity(new Intent(this, Test2Activity.class));
//            startActivity(new Intent(this, TestActivity.class));
        });
        findViewById(R.id.btn_test3).setOnClickListener(v -> {
            startActivity(new Intent(this, Test3Activity.class));
//            startActivity(new Intent(this, TestActivity.class));
        });
        final StringBuilder builder = new StringBuilder();
        final List<String> list = new ArrayList<>();
        EditText etInput = findViewById(R.id.et_input);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                for (char c : content.toCharArray()) {
                    Log.e("ZPF", "code=" + ((int) c) + ";type=" + Character.getType(c)+ ";block=" + Character.UnicodeBlock.of(c));
                }
                EmojiUtil.pickAllEmoji(content, builder, list);
                Log.e("ZPF", "content=" + content + ";display=" + builder);
                printList(list);
            }
        });

        etInput.postDelayed(new Runnable() {
            @Override
            public void run() {
//                etInput.setText("󠁧󠁢󠁥󠁮󠁧󠁿🏴󠁧󠁢󠁥󠁮󠁧󠁿🏴👋🏽");
//                etInput.setText("󠁧\uD83C\uDFF4")
                String str = "\uD83D\uDC4B\uD83C\uDFFD";
//                String str = new String(new int[]{0x1F3F4, 0xE0067, 0xE0062, 0xE0065, 0xE006E, 0xE0067, 0xE007F}, 0, 7);
//                etInput.setText(str);
                etInput.setText("曙");

//                etInput.setText("\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F");
            }
        }, 1000);
    }

    public static <T> void printList(List<T> list) {
        StringBuilder builder = new StringBuilder();
        builder.delete(0, builder.length());
        builder.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(list.get(i));
        }
        builder.append("]");
        Log.e("ZPF", "list=" + builder.toString());
    }

}
