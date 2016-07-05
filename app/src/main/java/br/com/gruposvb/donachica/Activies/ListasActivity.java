package br.com.gruposvb.donachica.Activies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Entities.Login;
import br.com.gruposvb.donachica.Models.ListaModel;
import br.com.gruposvb.donachica.Models.LoginModel;
import br.com.gruposvb.donachica.R;

public class ListasActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context CONTEXTO = this;
    private List<Entities.Lista> LISTAS;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //carrega modelo
        LoginModel model = new LoginModel(CONTEXTO);

        //verifica se há modelo
        if (model != null) {

            //recupera token
            Login objToken = model.obterToken();

            if (objToken != null) {

                setContentView(R.layout.activity_listas);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                // Initialize recycler view
                mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);

                //obter listas
                getListas execListas = new getListas();
                if (execListas == null || execListas.getStatus() != AsyncTask.Status.RUNNING) {
                    execListas = new getListas();
                    execListas.execute(objToken.getToken());
                }
            } else {
                Intent intent = new Intent(ListasActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send) {
            new getLogout().execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //executa logout na API
    class getLogout extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            //carrega modelo
            LoginModel model = new LoginModel(CONTEXTO);

            if (model != null) {
                if (model.getLogout())
                    finish();
            }

            return true;
        }
    }

    //carrega listas
    class getListas extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {

            //define erro
            Integer result = 0;

            try {
                //recupera token
                String token = params[0];

                //carregar parametros
                JSONObject parametros = new JSONObject();
                parametros.put("revisao", "0");
                parametros.put("modulo", "listas");

                //carregar modelo
                ListaModel model = new ListaModel(CONTEXTO);

                //carregar iten
                Entities.Retorno retorno = model.obterListas(parametros, token);

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

                                //verifica listas
                                List<Entities.Lista> listas = conteudo.getListas();
                                if (listas != null) {

                                    //define acerto
                                    result = 1;

                                    //carrega listas para tela
                                    LISTAS = listas;
                                }
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerAdapter(ListasActivity.this, LISTAS);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(ListasActivity.this, "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
        private List<Entities.Lista> _listLista;
        private Context mContext;

        public MyRecyclerAdapter(Context context, List<Entities.Lista> lista) {
            this._listLista = lista;
            this.mContext = context;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //determina o layout
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_lista, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            Entities.Lista lista = _listLista.get(i);

            //carregar iten para holder
            customViewHolder.lista = lista;

            //passar dados
            customViewHolder.textView.setText(Html.fromHtml(lista.getNome()));
            customViewHolder.textView2.setText(Html.fromHtml(lista.getDescricao()));

            int cor; //determina cor
            int icone; //determina ícone

            //verifica o tipo de iten
            switch (lista.getModulo()) {
                case ("laranja"):
                    cor = getColor(R.color.laranja);
                    icone = R.drawable.ic_icone_laranja;
                    break;
                case ("verde"):
                    cor = getColor(R.color.verde);
                    icone = R.drawable.ic_icone_verde;
                    break;
                default:
                    cor = getColor(R.color.azul);
                    icone = R.drawable.ic_icone_azul;
                    break;
            }

            //define cor
            customViewHolder.imageView.setBackgroundColor(cor);

            //define ícone
            customViewHolder.imageView2.setImageDrawable(getDrawable(icone));
        }

        @Override
        public int getItemCount() {
            return (null != _listLista ? _listLista.size() : 0);
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected ImageView imageView;
            protected ImageView imageView2;
            protected TextView textView;
            protected TextView textView2;
            protected Entities.Lista lista;

            public CustomViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
                this.imageView2 = (ImageView) view.findViewById(R.id.icone);
                this.textView = (TextView) view.findViewById(R.id.title);
                this.textView2 = (TextView) view.findViewById(R.id.descricao);

                //definir método click
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ListasActivity.this, CategoriasActivity.class);
                        Bundle parametros = new Bundle();
                        parametros.putSerializable("iten", lista);
                        intent.putExtras(parametros);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
