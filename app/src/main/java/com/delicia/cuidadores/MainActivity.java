package com.delicia.cuidadores;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.CheckBox;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper; DbHelper2 dbHelper2; DbHelper3 dbHelper3;
    ArrayAdapter<String> taskAdapter;
    ListView lstTask;
    SharedPreferences settings;
    public String taskName;
    public String p1, p2, p3;
    public static int page;
    ArrayList<String> weekdays = new ArrayList<>();
    Menu menu;
    MenuItem menuItem;
    TextView nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        dbHelper2 = new DbHelper2(this);
        dbHelper3 = new DbHelper3(this);
        page=1;

        nome = (TextView) findViewById(R.id.nome);

        lstTask = (ListView)findViewById(R.id.list);

        Calendar calendar = Calendar.getInstance();
        int currentday = calendar.get(Calendar.DAY_OF_MONTH);
        settings = getSharedPreferences("PREFS", 0);
        int lastDay = settings.getInt("day", 0);
        p1 = settings.getString("paciente1", "Paciente 1");
        p2 = settings.getString("paciente2", "Paciente 2");
        p3 = settings.getString("paciente3", "Paciente 3");
        nome.setText(p1);

        if(currentday != lastDay){
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day", currentday);
            editor.commit();
            dbHelper.resetDay();
            dbHelper2.resetDay();
            dbHelper3.resetDay();
        }

        loadTaskList();
    }

    @Override
    protected void onResume(){
        loadTaskList();
        super.onResume();
    }

    private void loadTaskList() {
        ArrayList<String> taskList;
        if(page==2) taskList = dbHelper2.getTaskDayList();
        else if(page==1) taskList = dbHelper.getTaskDayList();
        else taskList = dbHelper3.getTaskDayList();

        if(taskAdapter==null){
            taskAdapter = new ArrayAdapter<String>(this,R.layout.row,R.id.task_title,taskList);
            lstTask.setAdapter(taskAdapter);
        }
        else{
            taskAdapter.clear();
            taskAdapter.addAll(taskList);
            taskAdapter.notifyDataSetChanged();
        }
    }

    public void writeTask(){
        final EditText taskEditText = new EditText(this);

        final RadioButton radio1 = new RadioButton(this);
        final RadioButton radio2 = new RadioButton(this);
        final RadioButton radio3 = new RadioButton(this);
        final RadioButton radio4 = new RadioButton(this);
        final RadioGroup radios = new RadioGroup(this);
        radios.addView(radio1); radios.addView(radio2); radios.addView(radio3); radios.addView(radio4);

        final int ID_UNICO = 1;
        radio1.setId(ID_UNICO);
        radio1.setText("Uma vez");
        radio1.setChecked(true);

        final int ID_DIARIO = 2;
        radio2.setId(ID_DIARIO);
        radio2.setText("diário");

        final int ID_SEMANAL = 3;
        radio3.setId(ID_SEMANAL);
        radio3.setText("Semanal");

        final int ID_MENSAL = 4;
        radio4.setId(ID_MENSAL);
        radio4.setText("Mensal");

        //CAIXA 1 - TITULO
        AlertDialog title = new AlertDialog.Builder(this)
                .setTitle("Adicionar procedimento")
                .setMessage("Insira o título do procedimento")
                .setView(taskEditText)
                .setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskName = String.valueOf(taskEditText.getText());
                        ArrayList<String> nameList;
                        if(page==1) nameList = dbHelper.getNameList();
                        else if(page==2) nameList = dbHelper2.getNameList();
                        else nameList = dbHelper3.getNameList();
                        if(nameList.contains(taskName) == false){
                            //CAIXA 2 - FREQUÊNCIA
                            AlertDialog date = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Adicionar procedimento")
                                    .setMessage(("Selecione a frequência que deve ser realizada a tarefa"))
                                    .setView(radios)
                                    .setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            final int id = radios.getCheckedRadioButtonId();
                                            taskSpec(id);

                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .create();
                            date.show();
                        } else Toast.makeText(MainActivity.this, "Essa tarefa já existe", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        title.show();
    }

    public void taskSpec(final int id){
        switch(id){
            case 1:
                final EditText dateEditText = new EditText(this);
                AlertDialog box1 = new AlertDialog.Builder(this).setTitle("Adicionar procedimento")
                        .setMessage("Informe a data (dd/mm/aaaa)").setView(dateEditText)
                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                    String date = String.valueOf(dateEditText.getText());
                                    if(page==1) dbHelper.insertNewTask(taskName, 1, id, date, 0);
                                    if(page==2) dbHelper2.insertNewTask(taskName, 1, id, date, 0);
                                    if(page==3) dbHelper3.insertNewTask(taskName, 1, id, date, 0);
                                    loadTaskList();
                               }
                }).create();
                box1.show();
                break;
            case 2:
                final EditText freqEditText = new EditText(this);
                AlertDialog box2 = new AlertDialog.Builder(this).setTitle("Adicionar procedimento")
                        .setMessage("Informe a frequência diária").setView(freqEditText)
                       .setPositiveButton("Próximo", new DialogInterface.OnClickListener(){
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   final String frequency = String.valueOf(freqEditText.getText());
                                   final EditText periodEdit = new EditText(MainActivity.this);
                                   AlertDialog period = new AlertDialog.Builder(MainActivity.this).setTitle("Adicionar procedimento")
                                           .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                                   " branco caso não esteja definido")
                                           .setView(periodEdit)
                                           .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   int period = 0;
                                                   if(!String.valueOf(periodEdit.getText()).equals(""))
                                                        period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                                   if(page==1) dbHelper.insertNewTask(taskName, 1, id, frequency, period);
                                                   if(page==2) dbHelper2.insertNewTask(taskName, 1, id, frequency, period);
                                                   if(page==3) dbHelper3.insertNewTask(taskName, 1, id, frequency, period);

                                                   loadTaskList();
                                               }
                                           }).create();
                                    period.show();
                               }
                }).create();
                box2.show();
                break;
            case 3:
                AlertDialog box3 = new AlertDialog.Builder(this).setTitle("Adicionar procedimento")
                     .setMessage("Informe os dias:")
                     .setView(R.layout.weekdays)
                     .setPositiveButton("Próximo", new DialogInterface.OnClickListener(){
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             final String days = weekdays.toString();
                             final EditText periodEdit = new EditText(MainActivity.this);
                             AlertDialog period = new AlertDialog.Builder(MainActivity.this).setTitle("Adicionar procedimento")
                                     .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                             " branco caso não esteja definido")
                                     .setView(periodEdit)
                                     .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {
                                             int period = 0;
                                             if(!String.valueOf(periodEdit.getText()).equals(""))
                                                period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                             if(page==1) dbHelper.insertNewTask(taskName, 1, id, days, period);
                                             if(page==2) dbHelper2.insertNewTask(taskName, 1, id, days, period);
                                             if(page==3) dbHelper3.insertNewTask(taskName, 1, id, days, period);
                                             loadTaskList();
                                         }
                                     }).create();
                             period.show();
                         }
                }).setNegativeButton("Cancelar", null).create();
                box3.show();
                break;
            case 4:
                final EditText dayEditText = new EditText(this);
                AlertDialog box4 = new AlertDialog.Builder(this).setTitle("Adicionar procedimento")
                        .setMessage("Informe o dia do mês que o procedimento deve ser executado").setView(dayEditText)
                        .setPositiveButton("Próximo", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String dayMonth = String.valueOf(dayEditText.getText());
                                final EditText periodEdit = new EditText(MainActivity.this);
                                AlertDialog period = new AlertDialog.Builder(MainActivity.this).setTitle("Adicionar procedimento")
                                        .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                                " branco caso não esteja definido")
                                        .setView(periodEdit)
                                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int period = 0;
                                                if(!String.valueOf(periodEdit.getText()).equals(""))
                                                    period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                                if(page==1) dbHelper.insertNewTask(taskName, 1, id, dayMonth, period);
                                                if(page==2) dbHelper2.insertNewTask(taskName, 1, id, dayMonth, period);
                                                if(page==3) dbHelper3.insertNewTask(taskName, 1, id, dayMonth, period);
                                                loadTaskList();
                                            }
                                        }).create();
                                period.show();
                            }
                        }).create();
                box4.show();
                break;
        }
    }

    public void onCheckboxClicked(View view){
        if(((CheckBox) view).isChecked()) weekdays.add(((CheckBox) view).getText().toString());
        else weekdays.remove(((CheckBox) view).getText().toString());
    }

    String result;
    public void concludeTask(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        String[] columns = new String[2];
        columns = task.split("-");
        final String name = columns[0];
        final EditText edit = new EditText(this);
        AlertDialog alert = new AlertDialog.Builder(this).setTitle("Tarefa concluída")
                .setMessage("Deseja incluir algum resultado/anotação? (opcional)").setView(edit)
                .setPositiveButton("Concluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        result = String.valueOf(edit.getText());
                        if(page==1) dbHelper.concludeTask(name, result);
                        if(page==2) dbHelper2.concludeTask(name, result);
                        if(page==3) dbHelper3.concludeTask(name, result);
                        loadTaskList();
                    }
                }).create();
        alert.show();
    }

    public void startHistory(View view){
        startActivity(new Intent(this, History.class));
    }
    public void startAllTasks(View view) { startActivity(new Intent(this, AllTasks.class)); }

    public void changeName(View view){
        final EditText edit = new EditText(this);
        AlertDialog a = new AlertDialog.Builder(this).setTitle("Trocar nome").setMessage("Insira o nome do paciente:")
                .setView(edit).setPositiveButton("Concluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = String.valueOf(edit.getText());
                        if(page==1) {
                            menuItem = menu.findItem(R.id.paciente1);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("paciente1", name);
                            editor.commit();
                        }
                        else if(page==2) {
                            menuItem = menu.findItem(R.id.paciente2);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("paciente2", name);
                            editor.commit();
                        }
                        else {
                            menuItem = menu.findItem(R.id.paciente3);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("paciente3", name);
                            editor.commit();
                        }
                        menuItem.setTitle(name);
                        nome.setText(name);
                    }
                }).create();
        a.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        this.menu = menu;
        MenuItem m1 = menu.findItem(R.id.paciente1);
        MenuItem m2 = menu.findItem(R.id.paciente2);
        MenuItem m3 = menu.findItem(R.id.paciente3);

        m1.setTitle(p1);
        m2.setTitle(p2);
        m3.setTitle(p3);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_task:
                writeTask();
                return true;
            case R.id.paciente1:
                page = 1;
                loadTaskList();
                menuItem = menu.findItem(R.id.paciente1);
                nome.setText(menuItem.getTitle().toString());
                return true;
            case R.id.paciente2:
                page = 2;
                loadTaskList();
                menuItem = menu.findItem(R.id.paciente2);
                nome.setText(menuItem.getTitle());
                return true;
            case R.id.paciente3:
                page = 3;
                loadTaskList();
                menuItem = menu.findItem(R.id.paciente3);
                nome.setText(menuItem.getTitle());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}