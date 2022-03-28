package com.zpf.aaa;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.window.SplashScreenView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.zpf.tool.text.MoneyTextRule;
import com.zpf.tool.text.RuleTextWatcher;
import com.zpf.tool.text.SplitTextRule;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final RuleTextWatcher ruleTextWatcher = new RuleTextWatcher();
        ruleTextWatcher.addRuleChecker(new MoneyTextRule(12));
        SplitTextRule str = new SplitTextRule(new int[]{3}, new char[]{','});
        str.setStartPoint('.', 0);
        ruleTextWatcher.addRuleChecker(str);
//        et_money.addTextChangedListener(new MoneyTextWatcher(1));

        EditText etMoney = findViewById(R.id.et_money);
        etMoney.addTextChangedListener(ruleTextWatcher);

//        etMoney.addTextChangedListener(new SplitTextWatcher(new int[]{3, 4, 4}));
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "getDisplay=" + ruleTextWatcher.getDisplay() + ";getValue=" + ruleTextWatcher.getValue());
            }
        });
    }
}
