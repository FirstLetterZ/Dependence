package com.zpf.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class BottomMenuView extends LinearLayout {
    private TextView tvBottom;
    private ListView menuListView;
    private final List<MenuItemInfo> menuList = new ArrayList<>();
    private BaseAdapter menuAdapter;
    private Style viewStyle = new Style();
    private GradientDrawable listBg;
    private GradientDrawable bottomBg;
    private ColorDrawable dividerDrawable;

    public BottomMenuView(Context context) {
        super(context);
        initView(context);
    }

    public BottomMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BottomMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    protected void initView(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        setPadding((int) (10 * density), 0, (int) (10 * density), (int) (10 * density));
        setOrientation(LinearLayout.VERTICAL);
        dividerDrawable = new ColorDrawable(Color.LTGRAY);
        menuListView = new ListView(context);
        menuListView.setOverScrollMode(OVER_SCROLL_NEVER);
        menuListView.setDivider(dividerDrawable);
        menuListView.setDividerHeight((int) (0.5f * density));
        menuListView.setVerticalScrollBarEnabled(false);
        menuAdapter = new BottomDialogListAdapter();
        menuListView.setAdapter(menuAdapter);
        listBg = new GradientDrawable();
        listBg.setColor(Color.WHITE);
        menuListView.setBackground(listBg);
        Space space = new Space(context);
        space.setLayoutParams(new LayoutParams(0, (int) (10 * density)));
        tvBottom = new TextView(context);
        bottomBg = new GradientDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_pressed};
            states[1] = EMPTY_STATE_SET;
            final int[] colorList = new int[]{Color.parseColor("#EEEEEE"), Color.WHITE};
            ColorStateList colorStateList = new ColorStateList(states, colorList);
            bottomBg.setColor(colorStateList);
        } else {
            bottomBg.setColor(Color.WHITE);
        }
        tvBottom.setBackground(bottomBg);
        addView(menuListView);
        addView(space);
        addView(tvBottom);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        menuListView.setOnItemClickListener(itemClickListener);
    }

    public void setBottomClickListener(OnClickListener bottomClickListener) {
        tvBottom.setOnClickListener(bottomClickListener);
    }

    public void build(@Nullable Style style, @Nullable List<? extends MenuItemInfo> list) {
        menuList.clear();
        if (list != null && list.size() > 0) {
            menuList.addAll(list);
        }
        viewStyle.merge(style);
        if (style != null) {
            viewStyle = style;
        }
        updateListViewHeight();
    }

    @Nullable
    public MenuItemInfo getItemMenuInfo(int position) {
        if (position >= 0 && position < menuList.size()) {
            return menuList.get(position);
        }
        return null;
    }

    private void updateListViewHeight() {
        float size = menuList.size();
        if (size > viewStyle.maxLine) {
            size = viewStyle.maxLine - 0.5f;
        }
        int height = (int) (viewStyle.itemHeight * size);
        menuListView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        menuAdapter.notifyDataSetChanged();
        listBg.setCornerRadius(viewStyle.cornerRadius);
        updateBottomItemStyle(tvBottom);
    }

    private void updateListItemStyle(TextView itemView) {
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewStyle.itemHeight);
            itemView.setLayoutParams(layoutParams);
        } else {
            if (layoutParams.height != viewStyle.itemHeight) {
                layoutParams.height = viewStyle.itemHeight;
                itemView.setLayoutParams(layoutParams);
            }
        }
        itemView.setGravity(Gravity.CENTER);
        itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, viewStyle.listTextSize);
        itemView.setTextColor(viewStyle.listTextColor);
    }

    private void updateBottomItemStyle(TextView itemView) {
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewStyle.itemHeight);
            itemView.setLayoutParams(layoutParams);
        } else {
            if (layoutParams.height != viewStyle.itemHeight) {
                layoutParams.height = viewStyle.itemHeight;
                itemView.setLayoutParams(layoutParams);
            }
        }
        itemView.setGravity(Gravity.CENTER);
        itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, viewStyle.bottomTextSize);
        itemView.setTextColor(viewStyle.bottomTextColor);
        itemView.setText(viewStyle.bottomTextValue);
        bottomBg.setCornerRadius(viewStyle.cornerRadius);
        dividerDrawable.setColor(viewStyle.dividerColor);
    }

    private class BottomDialogListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return menuList.size();
        }

        @Override
        public MenuItemInfo getItem(int position) {
            return menuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView itemView;
            if (convertView instanceof TextView) {
                itemView = (TextView) convertView;
            } else {
                itemView = new TextView(parent.getContext());
            }
            updateListItemStyle(itemView);
            itemView.setText(getItem(position).getMenuItemDisplay());
            return itemView;
        }
    }

    public static class Style {
        public int itemHeight;
        public int cornerRadius;
        public int listTextSize;
        public int listTextColor;
        public int bottomTextSize;
        public int bottomTextColor;
        public int dividerColor;
        public int maxLine;
        public String bottomTextValue;

        public Style() {
            float density = Resources.getSystem().getDisplayMetrics().density;
            itemHeight = (int) (44 * density);
            cornerRadius = (int) (10 * density);
            listTextSize = 16;
            listTextColor = Color.DKGRAY;
            bottomTextSize = 16;
            bottomTextColor = Color.RED;
            dividerColor = Color.LTGRAY;
            maxLine = 6;
            bottomTextValue = "";
        }

        void merge(@Nullable Style newStyle) {
            if (newStyle == null) {
                return;
            }
            if (newStyle.itemHeight > 0) {
                itemHeight = newStyle.itemHeight;
            }
            if (newStyle.cornerRadius > 0) {
                cornerRadius = newStyle.cornerRadius;
            }
            if (newStyle.listTextSize > 0) {
                listTextSize = newStyle.listTextSize;
            }
            if (newStyle.bottomTextSize > 0) {
                bottomTextSize = newStyle.bottomTextSize;
            }
            if (newStyle.listTextColor != 0) {
                listTextColor = newStyle.listTextColor;
            }
            if (newStyle.bottomTextColor != 0) {
                listTextColor = newStyle.bottomTextColor;
            }
            if (newStyle.dividerColor != 0) {
                dividerColor = newStyle.dividerColor;
            }
            if (newStyle.maxLine > 0) {
                maxLine = newStyle.maxLine;
            }
            if (newStyle.bottomTextValue != null && newStyle.bottomTextValue.length() > 0) {
                bottomTextValue = newStyle.bottomTextValue;
            }
        }
    }

    public interface MenuItemInfo {
        @NonNull
        String getMenuItemDisplay();
    }

    public static class BaseMenuItemInfo implements MenuItemInfo {
        private final String display;

        public BaseMenuItemInfo(@NonNull String display) {
            this.display = display;
        }

        @Override
        @NonNull
        public String getMenuItemDisplay() {
            return display;
        }
    }

    @Nullable
    public static List<? extends MenuItemInfo> asList(@Nullable List<String> list) {
        List<MenuItemInfo> dataList = null;
        if (list != null && list.size() > 0) {
            dataList = new ArrayList<>();
            for (String menu : list) {
                dataList.add(new BaseMenuItemInfo(menu));
            }
        }
        return dataList;
    }

    @Nullable
    public static List<? extends MenuItemInfo> asList(@Nullable String[] list) {
        List<MenuItemInfo> dataList = null;
        if (list != null && list.length > 0) {
            dataList = new ArrayList<>();
            for (String menu : list) {
                dataList.add(new BaseMenuItemInfo(menu));
            }
        }
        return dataList;
    }
}