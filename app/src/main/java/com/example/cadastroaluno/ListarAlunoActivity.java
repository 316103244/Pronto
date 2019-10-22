package com.example.cadastroaluno;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class ListarAlunoActivity extends AppCompatActivity {    // Listando Aluno

    private ListView listView;
    private AlunoDAO dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) { // instânciando lista e puxando dados cadastrados

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_aluno);

        listView = findViewById(R.id.lista_alunos);
        dao = new AlunoDAO(this);
        alunos = dao.obterTodos();
        alunosFiltrados.addAll(alunos);
        //ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this ,android.R.layout.simple_list_item_1, alunosFiltrados);
        AlunoAdapter adaptador = new AlunoAdapter(this, alunosFiltrados);
        listView.setAdapter(adaptador);
        registerForContextMenu(listView);

    }

        public boolean onCreateOptionsMenu(Menu menu){  // criando menu pesquisa
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_principal, menu);
            SearchView sv = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    procuraAluno(s);
                    return false;
                }
            });

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { // pesquisando alunos e filtrando
        super.onCreateContextMenu(menu,v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }
    public  void  procuraAluno(String nome) {
        alunosFiltrados.clear();
        for (Aluno a : alunos) {
            if (a.getNome().toLowerCase().contains(nome.toLowerCase())) {
                alunosFiltrados.add(a);

            }
        }
        listView.invalidateViews();
    }

    public void excluir(MenuItem item){ // excluindo aluno
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
       final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Atenção")
                .setMessage("Realmente deseja excluir o aluno?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                       alunosFiltrados.remove(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        dao.excluir(alunoExcluir);
                        listView.invalidateViews();

                    }
                }).create();
        dialog.show();
    }
     public void atualizar (MenuItem item){ // atualizando aluno
         AdapterView.AdapterContextMenuInfo menuInfo =
                 (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
         final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
         Intent it = new Intent(this, MainActivity.class);
         it.putExtra("aluno", alunoAtualizar);
         startActivity(it);
     }
    public void cadastrar(MenuItem item){ // Atualizando aluno já cadastrado puxando class intent

        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);

    }

    @Override
    public void onResume(){ // limpando lista de pesquisa
        super.onResume();
        alunos = dao.obterTodos();
        alunosFiltrados.clear();
        alunosFiltrados.addAll(alunos);
        listView.invalidateViews();
    }
}
