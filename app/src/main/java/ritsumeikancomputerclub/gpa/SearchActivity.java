package ritsumeikancomputerclub.gpa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> spotTextArray = new ArrayList<String>();

    EditText editText;
    String[] trainSamples =  {"京都","大阪","南草津","大阪"};
    String[] busSamples =  {"パナソニック東口","南草津駅","南田山","小野山"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView)findViewById(R.id.trainListView);
        editText = (EditText)findViewById(R.id.editText);

        spotTextArray.clear();
        for(String s : trainSamples) {
            spotTextArray.add("(電車)"+s);
        }

        for(String s : busSamples) {
            spotTextArray.add("(バス)"+s);
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,spotTextArray);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
                String item = (String)adapter.getItem(i);
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void search(View v){
        ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
        String[] trainTextList = {"a","b","c","d","e","f"};
        String[] busTextList = {"g","h","i","j","k","l","m"};

        spotTextArray.clear();
        for(String s : trainTextList) {
            spotTextArray.add("(電車)"+s);
        }
        for(String s : busTextList) {
            spotTextArray.add("(バス)"+s);
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,spotTextArray);
        listView.setAdapter(adapter);

    }


}
