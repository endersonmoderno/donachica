package br.com.gruposvb.donachica.Activies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Models.ListaModel;
import br.com.gruposvb.donachica.Models.LoginModel;
import br.com.gruposvb.donachica.R;

public class ItensActivity extends AppCompatActivity {

    private Context CONTEXTO = this;
    private Entities.Lista LISTA;
    private Entities.Categoria CATEGORIA;
    private List<Entities.Iten> ITENS;
    private Entities.Login TOKEN;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itens);

        //inicializa toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //define botão voltar (manifest possui linha parent)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //determina clique no botão voltar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //inicializa loading
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        //recupera parâmetros
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {
            LISTA = (Entities.Lista) getIntent().getSerializableExtra("lista");
            CATEGORIA = (Entities.Categoria) getIntent().getSerializableExtra("categoria");

            //carrega modelo
            LoginModel model = new LoginModel(this);

            //verifica se há modelo
            if (model != null) {

                //recupera token
                TOKEN = model.obterToken();
                if (TOKEN != null) {

                    //define título
                    setTitle(CATEGORIA.getNome() + " - " + LISTA.getNome());

                    //carregar Lista
                    CarregarItens();
                }
            }

            //carergando
            Toast.makeText(ItensActivity.this, "Carregango... " + CATEGORIA.getNome() + " - " + LISTA.getNome(), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implementar modal com cadastro de item
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //carrega dados
    private void CarregarItens() {

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

                //obter itens
                getItens execItens = new getItens();
                if (execItens == null || execItens.getStatus() != AsyncTask.Status.RUNNING) {
                    execItens.execute();
                }
            }
        }
    }

    //carrega dados
    private void GravarIten(Entities.Iten iten) {

        //carrega modelo
        LoginModel model = new LoginModel(CONTEXTO);

        //verifica se há modelo
        if (model != null) {

            //se não há TOKEN
            if (TOKEN == null) {
                //recupera token
                TOKEN = model.obterToken();
            }

            //se há token e há iten
            if (TOKEN != null && iten != null) {

                //gravar iten
                setIten execIten = new setIten();
                if (execIten == null || execIten.getStatus() != AsyncTask.Status.RUNNING) {
                    execIten.myItem = iten;
                    execIten.execute();
                }
            }
        }
    }

    //carrega Itens
    class getItens extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {

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

                //carregar retorno
                result = CarregarRetorno(retorno);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerAdapter(ITENS);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(ItensActivity.this, "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //salvar iten
    class setIten extends AsyncTask<String, Void, Integer> {

        public Entities.Iten myItem;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {

            //define erro
            Integer result = 0;

            //verifica se informou item
            if (myItem != null) {

                //carregar modelo
                ListaModel model = new ListaModel(CONTEXTO);

                //prepara dados para salvar LISTA
                PrepararItemSalvar(myItem);

                //ajusta revisão
                //LISTA.setRevisao(LISTA.getRevisao() + 1);

                //TODO: não está gravando, não é a revisão, verificar acentos no JSON, deve ser isso

                //salvar lista
                Entities.Retorno retorno = model.salvarLista(LISTA, LISTA.getModulo(), TOKEN.getToken());

                //carregar retorno
                result = CarregarRetorno(retorno);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new MyRecyclerAdapter(ITENS);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(ItensActivity.this, "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //carrega dados de retorno
    private Integer CarregarRetorno(Entities.Retorno retorno) {

        Integer result = 0;

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

                            //verifica categorias
                            if (lista.getCategorias() != null) {

                                //varre categorias
                                for (Entities.Categoria categoria : lista.getCategorias()) {

                                    //verifica se é categoria informada
                                    if (categoria.getId() == CATEGORIA.getId()) {

                                        //atualiza categoria parametrizada
                                        CATEGORIA = categoria;

                                        //verifica itens
                                        if (categoria.getItens() != null) {

                                            //define acerto
                                            result = 1;

                                            //TODO: filtrar apenas itens não checados

                                            //carrega itens para tela
                                            ITENS = CATEGORIA.getItens();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    //preparar dados para Salvar
    private void PrepararItemSalvar(Entities.Iten myIten) {

        //verifica se tem lista
        if (LISTA != null) {

            //verifica categorias
            if (LISTA.getCategorias() != null) {

                //define novos itens
                //List<Entities.Categoria> categoriasNew = LISTA.getCategorias();

                //varre categorias
                for (Entities.Categoria categoria : LISTA.getCategorias()) {

                    //verifica se é categoria informada
                    if (categoria.getId() == CATEGORIA.getId()) {

                        //verifica itens
                        if (categoria.getItens() != null) {

                            //define novos itens
                            //List<Entities.Iten> itensNew = categoria.getItens();

                            //varre itens
                            for (Entities.Iten iten : categoria.getItens()) {

                                //verifica se é o iten informada
                                if (iten.getId() == myIten.getId()) {

                                    //carrega iten com alterações
                                    iten.setCheck(myIten.getCheck());
                                    iten.setQtde(myIten.getQtde());
                                    iten.setTexto(myIten.getTexto());
                                    iten.setQtate(myIten.getQtde());
                                    iten.setId(myIten.getId());
                                    iten.setNaoachou(myIten.getNaoachou());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //adapter iten tela
    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {

        //itens
        private List<Entities.Iten> _listIten;

        //contrutor
        public MyRecyclerAdapter(List<Entities.Iten> itens) {

            //carregar ites informados
            this._listIten = itens;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            //determina o layout
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_item, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
            final Entities.Iten iten = _listIten.get(i);

            //carregar iten para holder
            customViewHolder.iten = iten;

            //define texto do item
            String texto = iten.getTexto() + " - " + iten.getQtde() + "/" + iten.getQtate();

            //passar dados
            customViewHolder.textView.setText(Html.fromHtml(texto));
            customViewHolder.checkBox.setChecked(iten.getCheck());

            //define ação de clique em selecionado
            customViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iten.setCheck(!iten.getCheck());
                    GravarIten(iten);
                    Toast.makeText(ItensActivity.this, "Gravando...", Toast.LENGTH_SHORT).show();
                }
            });

            //define ação de clique configuração item
            customViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: abrir cadastro de item
                    Toast.makeText(ItensActivity.this, "Teste Ok", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != _listIten ? _listIten.size() : 0);
        }

        //holder item
        public class CustomViewHolder extends RecyclerView.ViewHolder {
            protected TextView textView;
            protected CheckBox checkBox;
            protected ImageView imageView;
            protected Entities.Iten iten;

            public CustomViewHolder(View view) {
                super(view);
                this.textView = (TextView) view.findViewById(R.id.title);
                this.checkBox = (CheckBox) view.findViewById(R.id.selecionado);
                this.imageView = (ImageView) view.findViewById(R.id.btconfig);
            }
        }
    }
}
