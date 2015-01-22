package com.aakportfolio.www.fbla2015;

        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.View;
        import android.content.Intent;
        import android.view.MenuItem;
        import android.widget.ListView;
        import java.util.ArrayList;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    ArrayList<MHSEvent> Events = new ArrayList<MHSEvent>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mamaroneck High School Events");
        //TODO download TSV or use local if needed. if no local, used hardcoded string or have extracted TSV
        //TODO Parse TSV to fill listview. Don't forget to add wait progress bar on activity, or as dialoge
        //TODO Fill ListView with titles from parsed TSV
        final String Names[]={"USA","RUSSIA","ENGLAND","AUSTRALIA","JAPAN"};

        ListView LV=(ListView) findViewById(R.id.listView);

        LV.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub

                sendMessage(arg1,position);
                }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    public void sendMessage(View view, int arrPOS) {
        Intent intent = new Intent(this, eventDummy.class);
        //We will try to launch the activity instead with what was selected from
        intent.putExtra("titleBar","Untitled"); //TODO Replace with selected OBJECT, looked up by position (in arraylist and listview)
        startActivity(intent);
    }
}
