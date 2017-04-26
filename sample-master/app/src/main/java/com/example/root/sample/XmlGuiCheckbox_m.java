package com.example.root.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANURAG on 27-03-2015.
 */

//package com.msi.ibm;

/**
 * Created by ANURAG on 26-03-2015.
 **/

public class XmlGuiCheckbox_m extends LinearLayout {

    String tag = XmlGuiCheckbox_m.class.getName();
    ArrayAdapter<String> aa;
    TextView label;
    //CheckBox check;
    ListView list;
    String[] opts;

    public XmlGuiCheckbox_m(Context context, String LabelText, String options) {

        super(context);
        label = new TextView(context);
        label.setText(LabelText);
        //check = new CheckBox(context);
        list = new ListView(context);
        opts = options.split("\\|");

        aa = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, opts);
        list.setChoiceMode(list.CHOICE_MODE_MULTIPLE);
        list.setAdapter(aa);
        this.addView(label);
        this.addView(list);

    }

    public XmlGuiCheckbox_m(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //public String getValue() {return (String) list.getSelectedItem().toString();}

    public List<String> getValue() {
        List<String> l = new ArrayList<String>();
        int len = list.getCount();
        SparseBooleanArray selected = list.getCheckedItemPositions();
        for (int j = 0; j < len; j++) {
            if (selected.get(j)) {
                System.out.println("<<<<<<<<<<<<<<<<<$$$$$$$$" + opts[j]);
                l.add(opts[j]);
            }
        }
        return l;
    }

    public String getVal() {
        getValue();
        String data = "";
        for (int j = 0; j < getValue().size(); j++) {
            System.out.println("the"+getValue().get(j));
            data += getValue().get(j);
            data += ",";
        }
        System.out.println(data+"sfdgdsgfsgsfgf");
        return data;
    }

}