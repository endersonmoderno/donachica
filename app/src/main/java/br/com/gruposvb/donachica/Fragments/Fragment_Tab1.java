package br.com.gruposvb.donachica.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.gruposvb.donachica.Activies.ItensActivity;
import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Models.ListaModel;
import br.com.gruposvb.donachica.Models.LoginModel;
import br.com.gruposvb.donachica.R;


public class Fragment_Tab1 extends Fragment {

    private Entities.Lista LISTA = new Entities.Lista();
    private List<Entities.Categoria> CATEGORIAS = new ArrayList<>();
    private Entities.Login TOKEN;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private View myFragmentView;

    public Fragment_Tab1() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //carregar dados
        CarregarCategorias();
    }

    //carrega dados
    private void CarregarCategorias() {
        //carrega modelo
        LoginModel model = new LoginModel(getContext());

        //verifica se há modelo
        if (model != null) {

            //recupera token
            TOKEN = model.obterToken();
            if (TOKEN != null) {

                //obter categoias
                getCategorias execCategorias = new getCategorias();
                if (execCategorias == null || execCategorias.getStatus() != AsyncTask.Status.RUNNING) {
                    execCategorias = new getCategorias();
                    execCategorias.execute();
                }
            }
        }
    }

    //carrega categorias
    class getCategorias extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            //define erro
            Integer result = 0;

            try {
                //carregar parametros
                JSONObject parametros = new JSONObject();
                parametros.put("revisao", LISTA.getRevisao());
                parametros.put("modulo", LISTA.getModulo());

                //carregar modelo
                ListaModel model = new ListaModel(getContext());

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

                                    //atualiza a iten parametrizada (atualiza módulo e revisão)
                                    LISTA = lista;

                                    //verifica itens
                                    List<Entities.Categoria> categorias = lista.getCategorias();
                                    if (categorias != null) {

                                        //TODO: filtrar categorias que possuem itens não checados

                                        //define acerto
                                        result = 1;

                                        //carrega iten para tela
                                        CATEGORIAS = categorias;
                                    }
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

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                mAdapter = new MyAdapter(CATEGORIAS);
                recyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getContext(), "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //recupera iten parametrizada
        LISTA = (Entities.Lista) getArguments().getSerializable("iten");

        //determina tab
        myFragmentView = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.rw_categoria_item_tab1);

        //carrega iten da tela com dados
        mAdapter = new MyAdapter(CATEGORIAS);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(myFragmentView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //carergando
        Toast.makeText(getContext(), "Carregango... " + LISTA.getNome(), Toast.LENGTH_SHORT).show();

        return myFragmentView;
    }

    //adpter de dados
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<Entities.Categoria> categorias;

        //contrutor
        public MyAdapter(List<Entities.Categoria> categoriaList) {
            this.categorias = categoriaList;
        }

        //holder view
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public final View myview = getView();
            public TextView title;
            public ImageView btn_expand_toggle;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                btn_expand_toggle = (ImageView) view.findViewById(R.id.btn_expand_toggle);

                //TODO: criar botão para edição da categoria em modal
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_categoria, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            //captura categoria
            final Entities.Categoria categoria = categorias.get(position);

            //define título da categoria
            holder.title.setText(categoria.getNome());

            //determina clique de botão aberto/fechado e expande iten
            holder.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //abrir itens
                    Intent intent = new Intent(getActivity(), ItensActivity.class);
                    Bundle parametros = new Bundle();
                    parametros.putSerializable("lista", LISTA);
                    parametros.putSerializable("categoria", categoria);
                    intent.putExtras(parametros);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }
    }
}
