package com.zpf.aaa;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zpf.tool.text.MoneyTextRule;
import com.zpf.tool.text.RuleTextWatcher;
import com.zpf.tool.text.SplitTextRule;
import com.zpf.wheelpicker.picker.DateInfo;
import com.zpf.wheelpicker.picker.DatePickerModel;
import com.zpf.wheelpicker.view.WheelsLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        final RuleTextWatcher ruleTextWatcher = new RuleTextWatcher();
        ruleTextWatcher.addRuleChecker(new MoneyTextRule(12));
        SplitTextRule str = new SplitTextRule(new int[]{3}, new char[]{','});
        str.setStartPoint('.', 0);
        ruleTextWatcher.addRuleChecker(str);
//        et_money.addTextChangedListener(new MoneyTextWatcher(1));

        EditText etMoney = findViewById(R.id.et_money);
        etMoney.addTextChangedListener(ruleTextWatcher);

//        etMoney.addTextChangedListener(new SplitTextWatcher(new int[]{3, 4, 4}));

        WheelsLayout wheelsLayout = findViewById(R.id.wheels);
        List<ProvinceBean> list = new Gson().fromJson(this.getCityJson(this), (new TypeToken<List<ProvinceBean>>() {
        }).getType());

//        LinkageSelectorModel<AddressBean> model = new LinkageSelectorModel<>(3, new LinkageSelectorModel.DataSource<AddressBean>() {
//
//            @Override
//            public void onDependChanged(List<AddressBean> selects, int currentColumn, AddressBean currentData, LinkageSelectorModel.Callback<AddressBean> callback) {
//                AddressBean addressBean = selects.get(selects.size() - 1);
//                List<? extends AddressBean> addressBeanList = null;
//                if (addressBean instanceof ProvinceBean) {
//                    addressBeanList = ((ProvinceBean) addressBean).getChildren();
//                } else if (addressBean instanceof ProvinceBean.CityBean) {
//                    addressBeanList = ((ProvinceBean.CityBean) addressBean).getChildren();
//                }
//                callback.changeData(addressBeanList, 0);
//            }
//
//            @Override
//            public List<? extends AddressBean> firstColumn() {
//                return list;
//            }
//        });
//        model.setOnChangedListener(new OnColumnChangedListener<AddressBean>() {
//            @Override
//            public void onColumnDataChanged(int column, int position, AddressBean data) {
//
//                Log.e("ZPF", "onChanged==>column=" + column + ";position=" + position + ";data=" + data.getPickerViewText());
//            }
//        });
//        BoundaryTimePickerDataModel model = new BoundaryTimePickerDataModel(10);
//        model.setBoundary(new DateInfo(2022, 10, 17), new DateInfo(2022, 12, 22));

        DatePickerModel model = new DatePickerModel(true, true, true, false, false);
        model.setBoundary(new DateInfo(2020, 1, 15), new DateInfo(2022, 9, 10));
        model.setInitData(new DateInfo(2022, 3, 15));
        model.setOverstepRollback(true);

//        LinkagePickerModel<AddressBean> model = new LinkagePickerModel<AddressBean>( 3,new IDataSource<AddressBean>() {
//            @Override
//            public List<? extends AddressBean> getColumnList(List<AddressBean> selects, int column) {
//                Log.e("ZPF","getColumnList===>column="+column+";selects="+ selects.toString());
//                if (column == 0) {
//                    return list;
//                }
//                AddressBean lastColumn = selects.get(column - 1);
//                if (lastColumn instanceof ProvinceBean) {
//                    return ((ProvinceBean) lastColumn).getChildren();
//                } else if (lastColumn instanceof ProvinceBean.CityBean) {
//                    return ((ProvinceBean.CityBean) lastColumn).getChildren();
//                }
//                return null;
//            }
//        });
        ArrayList<List<NumberInfo>> dataList =new ArrayList<>();
        dataList.add(new ArrayList<>());
        dataList.add(new ArrayList<>());
        dataList.add(new ArrayList<>());
        for (int i = 0; i < 20; i++) {
            for (List<NumberInfo> numberInfos : dataList) {
                numberInfos.add(new NumberInfo((i+1)));
            }
        }
//
//        RollbackDatePickerModel<NumberInfo> model = new RollbackDatePickerModel<>(3, (selects, column) ->
//                dataList.get(column));

        wheelsLayout.getOptions().setItemsVisibleCount(5);
//        wheelsLayout.getOptions().isLoop = true;
        wheelsLayout.setDataManager(model);


        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                model.setInitData(new DateInfo(2022, 3, 15));
//                model.refreshDataList();
//                model.setInitData(Arrays.asList(new AddressBean("江苏省"),new AddressBean("苏州市"),new AddressBean("昆山市")));
//                model.refreshDataList();
//                Log.e("TAG", "getDisplay=" + ruleTextWatcher.getDisplay() + ";getValue=" + ruleTextWatcher.getValue());
            }
        });
    }

    private String getCityJson(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(context.getAssets().open("region.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return stringBuilder.toString();
    }

}
