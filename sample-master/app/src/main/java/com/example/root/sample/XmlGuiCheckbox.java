package com.example.root.sample;

/**
 * Created by ANURAG on 26-03-2015.
 **/

      import android.content.Context;
      import android.util.SparseBooleanArray;
      import android.widget.ArrayAdapter;
      import android.widget.LinearLayout;
      import android.widget.TextView;
      import android.widget.ListView;
      import android.util.AttributeSet;

      import java.util.ArrayList;
      import java.util.List;

public class XmlGuiCheckbox extends LinearLayout {

    String tag = XmlGuiCheckbox.class.getName();
    ArrayAdapter<String>  aa;
    TextView label;
    //CheckBox check;
    ListView list;
    String []opts;

    public XmlGuiCheckbox(Context context,String LabelText,String options){

        super(context);
        label = new TextView(context);
        label.setText(LabelText);
        //check = new CheckBox(context);
        list =new ListView(context);
        opts=options.split("\\|");

        aa = new ArrayAdapter<String>( context, android.R.layout.simple_list_item_single_choice,opts);
        list.setChoiceMode(list.CHOICE_MODE_SINGLE);
        list.setAdapter(aa);
        this.addView(label);
        this.addView(list);

    }

    public XmlGuiCheckbox(Context context,AttributeSet attrs){
        super(context,attrs);
    }

   // public String getValue() {return (String) list.getSelectedItem().toString();}

    public List<String> getValue() {
        List<String> l = new ArrayList<String>();
        int len = list.getCount();
        SparseBooleanArray selected = list.getCheckedItemPositions();
        for (int j = 0; j < len; j++) {
            if (selected.get(j)) {
                l.add(opts[j]);
            }
        }
        return l;
    }
}
