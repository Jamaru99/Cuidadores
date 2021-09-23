package com.delicia.cuidadores;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import static com.delicia.cuidadores.MainActivity.page;

public class History extends AppCompatActivity {

    DbHelper dbHelper; DbHelper2 dbHelper2; DbHelper3 dbHelper3;
    ArrayAdapter<String> taskAdapter;
    ListView lstTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DbHelper(this);
        lstTask = (ListView)findViewById(R.id.hist_list);
        dbHelper = new DbHelper(this);
        dbHelper2 = new DbHelper2(this);
        dbHelper3 = new DbHelper3(this);

        loadTaskList();
    }

    private void loadTaskList() {
        ArrayList<String> taskList;
        if(page==2) taskList = dbHelper2.getHistoryList();
        else if(page==1) taskList = dbHelper.getHistoryList();
        else taskList = dbHelper3.getHistoryList();
        if(taskAdapter==null){
            taskAdapter = new ArrayAdapter<String>(this,R.layout.row_history,R.id.task_name,taskList);
            lstTask.setAdapter(taskAdapter);
        }
        else{
            taskAdapter.clear();
            taskAdapter.addAll(taskList);
            taskAdapter.notifyDataSetChanged();
        }
        Log.e("Lista",taskList.toString());
    }

    public void clearHistory(){
        if(page==1) dbHelper.clearHistory();
        if(page==2) dbHelper2.clearHistory();
        if(page==3) dbHelper3.clearHistory();
        loadTaskList();
    }

    public void deleteHistory(View view){
        View parent = (View)view.getParent();
        View grandparent = (View)parent.getParent();
        TextView taskTextView = (TextView)grandparent.findViewById(R.id.task_name);
        String task = String.valueOf(taskTextView.getText());
        String[] name = new String[2];
        name = task.split("-");
        if(page==1) dbHelper.deleteHistory(name[0]);
        if(page==2) dbHelper2.deleteHistory(name[0]);
        if(page==3) dbHelper3.deleteHistory(name[0]);
        loadTaskList();
    }

    String date, time, result, nominho;
    public void updateHistory(View view){
        final EditText edit1 = new EditText(this);
        final EditText edit2 = new EditText(this);
        final EditText edit3 = new EditText(this);
        View parent = (View)view.getParent();
        View grandparent = (View)parent.getParent();
        TextView taskTextView = (TextView)grandparent.findViewById(R.id.task_name);
        String task = String.valueOf(taskTextView.getText());
        String[] name = new String[2];
        name = task.split("-");
        nominho = name[0];
        AlertDialog box1 = new AlertDialog.Builder(this).setTitle("Data").setMessage("Insira a data em que o procedimento foi concluído")
                .setView(edit1).setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        date = String.valueOf(edit1.getText());
                        AlertDialog box2 = new AlertDialog.Builder(History.this).setView(edit2).setMessage("Insira o horário em que o procedimento foi concluído")
                                .setTitle("Horário").setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        time = String.valueOf(edit2.getText());
                                        AlertDialog box3 = new AlertDialog.Builder(History.this).setTitle("Resultados/anotações")
                                                .setView(edit3).setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        result = String.valueOf(edit3.getText());
                                                        if(page==1) dbHelper.updateHistory(nominho, date, time, result);
                                                        if(page==2) dbHelper2.updateHistory(nominho, date, time, result);
                                                        if(page==3) dbHelper3.updateHistory(nominho, date, time, result);
                                                        loadTaskList();
                                                    }
                                                }).create();
                                        box3.show();
                                    }
                                }).create();
                        box2.show();
                    }
                }).create();
        box1.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history,menu);

        //Change menu icon color
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_history:
                clearHistory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

