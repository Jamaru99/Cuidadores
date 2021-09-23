package com.delicia.cuidadores;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;

import static com.delicia.cuidadores.MainActivity.page;

public class AllTasks extends AppCompatActivity {

    DbHelper dbHelper; DbHelper2 dbHelper2; DbHelper3 dbHelper3;
    ArrayAdapter<String> taskAdapter;
    ListView lstTask;
    public String taskName;
    ArrayList<String> weekdays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        dbHelper = new DbHelper(this);
        dbHelper2 = new DbHelper2(this);
        dbHelper3 = new DbHelper3(this);
        lstTask = (ListView)findViewById(R.id.list);

        loadTaskList();
    }

    private void loadTaskList() {
        ArrayList<String> taskList;
        if(page==2) taskList = dbHelper2.getAllTaskList();
        else if(page==1) taskList = dbHelper.getAllTaskList();
        else taskList = dbHelper3.getAllTaskList();
        if(taskAdapter==null){
            taskAdapter = new ArrayAdapter<String>(this,R.layout.row_all_tasks,R.id.task_title,taskList);
            lstTask.setAdapter(taskAdapter);
        }
        else{
            taskAdapter.clear();
            taskAdapter.addAll(taskList);
            taskAdapter.notifyDataSetChanged();
        }
    }

    public void updateTask(final String oldTask){
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
                        ArrayList<String> nameList = dbHelper.getNameList();

                            //CAIXA 2 - FREQUÊNCIA
                            AlertDialog date = new AlertDialog.Builder(AllTasks.this)
                                    .setTitle("Adicionar procedimento")
                                    .setMessage(("Selecione a frequência que deve ser realizada a tarefa"))
                                    .setView(radios)
                                    .setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            final int id = radios.getCheckedRadioButtonId();
                                            taskSpec(id, oldTask);

                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .create();
                            date.show();
                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        title.show();
    }

    public void taskSpec(final int id, final String oldTask){
        switch(id){
            case 1:
                final EditText dateEditText = new EditText(this);
                AlertDialog box1 = new AlertDialog.Builder(this).setTitle("Adicionar procedimento")
                        .setMessage("Informe a data (dd/mm/aaaa)").setView(dateEditText)
                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String date = String.valueOf(dateEditText.getText());
                                if(page==1)dbHelper.updateTask(oldTask, taskName, 1, id, date, 0);
                                if(page==2)dbHelper2.updateTask(oldTask, taskName, 1, id, date, 0);
                                if(page==3)dbHelper3.updateTask(oldTask, taskName, 1, id, date, 0);
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
                                final EditText periodEdit = new EditText(AllTasks.this);
                                AlertDialog period = new AlertDialog.Builder(AllTasks.this).setTitle("Adicionar procedimento")
                                        .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                                " branco caso não esteja definido")
                                        .setView(periodEdit)
                                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int period = 0;
                                                if(!String.valueOf(periodEdit.getText()).equals(""))
                                                    period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                                if(page==1) dbHelper.updateTask(oldTask, taskName, 1, id, frequency, period);
                                                if(page==2) dbHelper2.updateTask(oldTask, taskName, 1, id, frequency, period);
                                                if(page==3) dbHelper3.updateTask(oldTask, taskName, 1, id, frequency, period);
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
                                final EditText periodEdit = new EditText(AllTasks.this);
                                AlertDialog period = new AlertDialog.Builder(AllTasks.this).setTitle("Adicionar procedimento")
                                        .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                                " branco caso não esteja definido")
                                        .setView(periodEdit)
                                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int period = 0;
                                                if(!String.valueOf(periodEdit.getText()).equals(""))
                                                    period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                                if(page==1) dbHelper.updateTask(oldTask, taskName, 1, id, days, period);
                                                if(page==2) dbHelper2.updateTask(oldTask, taskName, 1, id, days, period);
                                                if(page==3) dbHelper3.updateTask(oldTask, taskName, 1, id, days, period);
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
                                final EditText periodEdit = new EditText(AllTasks.this);
                                AlertDialog period = new AlertDialog.Builder(AllTasks.this).setTitle("Adicionar procedimento")
                                        .setMessage("Informe o período (em dias) que deve ser realizado o procedimento. Deixe em" +
                                                " branco caso não esteja definido")
                                        .setView(periodEdit)
                                        .setPositiveButton("Adicionar", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                int period = 0;
                                                if(!String.valueOf(periodEdit.getText()).equals(""))
                                                    period = Integer.parseInt(String.valueOf(periodEdit.getText()));
                                                if(page==1) dbHelper.updateTask(oldTask, taskName, 1, id, dayMonth, period);
                                                if(page==2) dbHelper2.updateTask(oldTask, taskName, 1, id, dayMonth, period);
                                                if(page==3) dbHelper3.updateTask(oldTask, taskName, 1, id, dayMonth, period);
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

    public void deleteTask(View view){
        View parent = (View)view.getParent();
        View grandparent = (View)parent.getParent();
        TextView taskTextView = (TextView)grandparent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        String[] name = new String[2];
        name = task.split("-");
        if(page==1) dbHelper.deleteTask(name[0]);
        if(page==2) dbHelper2.deleteTask(name[0]);
        if(page==3) dbHelper3.deleteTask(name[0]);
        loadTaskList();
    }

    public void updateTaskClick(View view){
        View parent = (View)view.getParent();
        View grandparent = (View)parent.getParent();
        TextView taskTextView = (TextView)grandparent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        String[] name = new String[2];
        name = task.split("-");
        updateTask(name[0]);
    }


}
