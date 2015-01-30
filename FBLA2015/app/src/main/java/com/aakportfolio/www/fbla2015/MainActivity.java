package com.aakportfolio.www.fbla2015;

        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.View;
        import android.content.Intent;
        import android.view.MenuItem;
        import android.widget.ArrayAdapter;
        import android.widget.ListAdapter;
        import android.widget.ListView;

        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;

        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.Toast;
        import android.view.MenuInflater;


public class MainActivity extends ActionBarActivity {
    ListView LV;
    ArrayList<MHSEvent> Events;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity_main);
        Events = new ArrayList<MHSEvent>();
        for(int i = 0; i < 10; i++){
            Events.add(new MHSEvent("I am an event"+i,"This is my description, I have one!!!!"+i,
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date())));
        }
        orderAndRemoveEvents();

        LV=(ListView) findViewById(R.id.listView);
        fillListView();

        LV.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                sendMessage(arg1,position);
                }
        });


    }

    private void orderAndRemoveEvents() {
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())),
            month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())),
            day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        for(int i = 0; i < Events.size(); i++){
            if(!Events.get(i).showEvent(month,day,year)){
                Events.remove(i);
                i--;
            }
        }
        //TODO then sort them by date...
    }

    private void fillListView(){
        //TODO Use TSV that is downloaded or installed. Update is manual.
        //TODO Parse TSV to fill listview. Don't forget to add wait progress bar on activity, or as dialoge
        //TODO Fill ListView with titles from parsed TSV
        String [] EventArray = new String [Events.size()];
        for(int i = 0; i < Events.size(); i++){
            EventArray[i] = Events.get(i) + "";
        }
        ListAdapter LA = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,EventArray);
        LV.setAdapter(LA);
    }
    private void update(){
        //TODO: Download latest events to TSV, replacing old
        //TODO: toast if download fails.
        Toast.makeText(this,"Not yet implamented...",Toast.LENGTH_SHORT).show();
        fillListView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_update:
                update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    public void sendMessage(View view, int arrPOS) {
        Intent intent = new Intent(this, eventDummy.class);
        //We will try to launch the activity instead with what was selected from
        intent.putExtra("event",Events.get(arrPOS)); //TODO Replace with selected OBJECT, looked up by position (in arraylist and listview)
        startActivity(intent);
    }
}
