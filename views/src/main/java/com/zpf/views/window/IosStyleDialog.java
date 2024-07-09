package com.zpf.views.window;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.zpf.views.R;

/**
 * @author ZPF
 * 2018/07/10.
 */
public class IosStyleDialog extends AbsCustomDialog {
    private ImageView ivIcon;
    private TextView tvMessage;
    private TextView tvCancel;
    private TextView tvTitle;
    private EditText etInput;
    private TextView tvConfirm;
    private View viewLine;

    public IosStyleDialog(@NonNull Context context) {
        super(context);
    }

    public IosStyleDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void initWindow(@NonNull Window window) {
        super.initWindow(window);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.views_dialog_ios_style);
        ivIcon = findViewById(R.id.iv_icon);
        tvTitle = findViewById(R.id.tv_title);
        viewLine = findViewById(R.id.view_line);
        tvMessage = findViewById(R.id.tv_message);
        tvCancel = findViewById(R.id.btn_cancel);
        tvConfirm = findViewById(R.id.btn_confirm);
        etInput = findViewById(R.id.et_input);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public ImageView getIcon() {
        return ivIcon;
    }

    public TextView getMessage() {
        return tvMessage;
    }

    public TextView getCancel() {
        return tvCancel;
    }

    public View getViewLine() {
        return viewLine;
    }

    public TextView getConfirm() {
        return tvConfirm;
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public EditText getInput() {
        return etInput;
    }

}
