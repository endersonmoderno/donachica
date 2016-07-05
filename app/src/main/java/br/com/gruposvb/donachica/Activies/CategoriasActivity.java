package br.com.gruposvb.donachica.Activies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Fragments.Fragment_Tab1;
import br.com.gruposvb.donachica.Fragments.Fragment_Tab2;
import br.com.gruposvb.donachica.Fragments.Fragment_Tab3;
import br.com.gruposvb.donachica.Models.ListaModel;
import br.com.gruposvb.donachica.Models.LoginModel;
import br.com.gruposvb.donachica.R;

public class CategoriasActivity extends AppCompatActivity {

    private Context CONTEXTO = this;
    private Entities.Lista LISTA;
    private Entities.Login TOKEN;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //define botão voltar (manifest possui linha parent)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recupera parâmetros
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {
            LISTA = (Entities.Lista) getIntent().getSerializableExtra("iten");

            //carrega modelo
            LoginModel model = new LoginModel(this);

            //verifica se há modelo
            if (model != null) {

                //recupera token
                TOKEN = model.obterToken();
                if (TOKEN != null) {

                    //define título
                    setTitle(LISTA.getNome());

                    //carregar views de tabs
                    viewPager = (ViewPager) findViewById(R.id.viewpager);
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);

                    //carregar Lista
                    CarregarLista();
                }
            }

            //carergando
            Toast.makeText(CategoriasActivity.this, "Carregango... " + LISTA.getNome(), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implementar modal com cadastro de categoria
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //carrega dados
    private void CarregarLista() {

        //carrega modelo
        LoginModel model = new LoginModel(CONTEXTO);

        //verifica se há modelo
        if (model != null) {

            //se não há TOKEN
            if (TOKEN == null) {
                //recupera token
                TOKEN = model.obterToken();
            }

            //se há token
            if (TOKEN != null) {

                //obter iten
                getLista execLista = new getLista();
                if (execLista == null || execLista.getStatus() != AsyncTask.Status.RUNNING) {
                    execLista = new getLista();
                    execLista.execute();
                }
            }
        }
    }

    //carrega tabs
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //carregar parametros
        Bundle bundle = new Bundle();
        bundle.putSerializable("iten", LISTA);

        Fragment_Tab1 fragobj1 = new Fragment_Tab1();
        fragobj1.setArguments(bundle);
        adapter.addFragment(fragobj1, "Comprar");

        Fragment_Tab2 fragobj2 = new Fragment_Tab2();
        fragobj2.setArguments(bundle);
        adapter.addFragment(fragobj2, "Comprado");

        Fragment_Tab3 fragobj3 = new Fragment_Tab3();
        fragobj3.setArguments(bundle);
        adapter.addFragment(fragobj3, "Não Achei");

        viewPager.setAdapter(adapter);
    }

    //adapter de tabs
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //carrega iten
    class getLista extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            //define erro
            Integer result = 0;

            try {
                //carregar parametros
                JSONObject parametros = new JSONObject();
                parametros.put("revisao", "0");//forçar recuperação de dados na api
                parametros.put("modulo", LISTA.getModulo());

                //carregar modelo
                ListaModel model = new ListaModel(CONTEXTO);

                //carregar iten
                Entities.Retorno retorno = model.obterLista(parametros, LISTA.getModulo(), TOKEN.getToken());

                //verifica se há retorno
                if (retorno != null) {

                    //verifica se retornou ok
                    if (retorno.getStatus().equals("ok")) {

                        //carrega dados
                        Entities.Dados dados = retorno.getDados();
                        if (dados != null) {

                            //verifica conteudo
                            Entities.Conteudo conteudo = dados.getConteudo();
                            if (conteudo != null) {

                                //verifica iten
                                Entities.Lista lista = conteudo.getLista();
                                if (lista != null) {

                                    //define acerto
                                    result = 1;

                                    //carrega iten para tela
                                    LISTA = lista;
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
}
