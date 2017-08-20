package cn.com.telecomphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 17-8-17.
 */

public class SetPhoneBlack extends Activity implements View.OnClickListener {
    private DBOperate dbOperate;
    private Button allBtn, othersBtn, noBtn, deleteBtn, inputBtn;
    private CheckBox checkBox;
    private TextView chooseNum;
    private EditText inputEdit;
    private ListView listView;
    private List<Person> list = new ArrayList<>();
    private int num;
    private PersonAdapter adapter;
    private Toast toast;
    public static boolean isChoose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_phone);
        dbOperate = DBOperate.getDbOperate(this);
        initView();
        setButtonSee();
        dbOperate.getBlackName(list);
        adapter = new PersonAdapter(SetPhoneBlack.this, R.layout.balck_name_item, list);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isChoose) {

                    list.get(position).setCheck(true);
                    // 调整选定条目
                    num++;
                    adapter.notifyDataSetChanged();
                    isChoose = true;
                    setButtonSee();
                    chooseNum.setText("一共选中" + num + "个黑名单联系人");
                    return true;
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isChoose) {
                    // 绑定listView的监听器
                    // 取得ViewHolder对象
                    PersonAdapter.ViewHolder viewHolder = (PersonAdapter.ViewHolder) view.getTag();
                    // 改变CheckBox的状态
                    viewHolder.checkBox.toggle();
                    // 将CheckBox的选中状况记录下来
                    list.get(position).setCheck(viewHolder.checkBox.isChecked());
                    // 调整选定条目
                    if (viewHolder.checkBox.isChecked() == true) {
                        num++;
                    } else {
                        num--;
                    }
                    // 用TextView显示
                    chooseNum.setText("一共选中" + num + "个黑名单联系人");
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                String s = inputEdit.getText().toString();
                if (isPhone(s)) {
                    if (dbOperate.saveBlackName(s)) {
                        dbOperate.getBlackName(list);
                        adapter.notifyDataSetChanged();
                        showToast("添加成功！");
                        inputEdit.setText("");
                    } else {
                        showToast("黑名单已存在此号码！");
                    }

                } else {
                    showToast("手机号码格式错误！");
                }
                break;

            case R.id.all_choose:
                num = 0;
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(true);
                    num++;
                }
                chooseNum.setText("一共选中" + num + "个黑名单联系人");
                adapter.notifyDataSetChanged();
                break;


            case R.id.others_choose:
                num = list.size() - num;
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(!list.get(i).isCheck());
                }

                chooseNum.setText("一共选中" + num + "个黑名单联系人");
                adapter.notifyDataSetChanged();

                break;


            case R.id.no_choose:
                num = 0;
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(false);
                }
                chooseNum.setText("一共选中" + num + "个黑名单联系人");
                adapter.notifyDataSetChanged();
                break;


            case R.id.delete_choose:
                if (num != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isCheck()) {
                            dbOperate.deleteBlackName(list.get(i).getPhoneNumber());
                            list.remove(i);
                            i--;
                        }

                    }
                    isChoose = false;
                    setButtonSee();
                    num = 0;
                    chooseNum.setText("一共选中" + num + "个黑名单联系人");
                    adapter.notifyDataSetChanged();
                } else {
                    showToast("请选择要删除的联系人！");
                }

                break;
            default:
                break;

        }
    }

    public void setButtonSee() {
        if (!isChoose) {
            allBtn.setVisibility(View.GONE);
            noBtn.setVisibility(View.GONE);
            othersBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            inputEdit.setVisibility(View.VISIBLE);
            inputBtn.setVisibility(View.VISIBLE);
        } else {
            inputEdit.setVisibility(View.GONE);
            inputBtn.setVisibility(View.GONE);
            allBtn.setVisibility(View.VISIBLE);
            noBtn.setVisibility(View.VISIBLE);
            othersBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
    }

    public boolean isPhone(String phoneNum) {
        Pattern p = Pattern
                .compile("^(0|86|17951)?(13[0-9]|15[0-9]|17[0-9]|18[0-9]|14[0-9])[0-9]{8}$");
        Matcher m = p.matcher(phoneNum);
        return m.matches();

    }

    public void showToast(String s) {
        if (toast != null) {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(s);
            toast.show();
        } else {
            toast = Toast.makeText(SetPhoneBlack.this, s, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }


    public void initView() {

        othersBtn = getView(R.id.others_choose);
        noBtn = getView(R.id.no_choose);
        deleteBtn = getView(R.id.delete_choose);
        checkBox = getView(R.id.checkbox);
        listView = getView(R.id.black_lv);
        chooseNum = getView(R.id.choose_num_text);
        inputBtn = getView(R.id.add_btn);
        inputEdit = getView(R.id.input_phone);
        allBtn = getView(R.id.all_choose);

        inputBtn.setOnClickListener(this);
        allBtn.setOnClickListener(this);
        othersBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);


    }


    @Override
    public void onBackPressed() {
        if (!isChoose)
            super.onBackPressed();
        else {
            num = 0;
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setCheck(false);
            }
            isChoose = false;
            chooseNum.setText("一共选中" + num + "个黑名单联系人");
            adapter.notifyDataSetChanged();
            setButtonSee();
        }


    }
}
