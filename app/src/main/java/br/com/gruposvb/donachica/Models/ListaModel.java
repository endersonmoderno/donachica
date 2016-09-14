package br.com.gruposvb.donachica.Models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Helper;
import br.com.gruposvb.donachica.Services.DonaChicaApi;

/**
 * Created by ender on 01/07/2016.
 */
public class ListaModel {

    //region variáveis globais da classe
    private static final String JSON_NAME = "listas"; //json
    private static Context CTX;
    private static String JSON_ARQUIVO;
    //endregion

    //construtor
    public ListaModel(Context ctx) {
        try {
            //define o contexto
            CTX = ctx;

            //caminho
            JSON_ARQUIVO = CTX.getFilesDir().getPath() + "/" + JSON_NAME;

            //verifica se arquivo de dados não exite
            File arquivo = new File(JSON_ARQUIVO);
            if (!arquivo.exists()) {
                //cria novo arquivo
                arquivo.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //obtem listas
    public Entities.Retorno obterListas(JSONObject parametros, String token) {
        try {
            //carregar serviço API
            DonaChicaApi service = new DonaChicaApi();

            //faz login na api
            JSONObject obj = service.obterListas(parametros, token);

            //verifica se encontrou dados
            if (obj != null) {
                //grava dados em local
                setListas(obj);
            } else {
                //obter dados de local
                JSONObject objLocal = getListasLocal();
                obj = objLocal;
            }

            //carrega obj retornado
            Entities.Retorno retorno = parseRetorno(obj.getJSONObject("retorno"));

            return retorno;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //obtem lista
    public Entities.Retorno obterLista(JSONObject parametros, String nomelista, String token) {
        try {
            //carregar serviço API
            DonaChicaApi service = new DonaChicaApi();

            //recupera dados da api
            //JSONObject obj = service.obterListas(parametros, token);
            JSONObject obj = null;//TODO: força os dados locais

            //determina retorno
            Entities.Retorno retorno = null;

            //verifica se encontrou dados
            if (obj != null) {
                //grava dados em local
                setLista(obj, nomelista);

                //carrega obj retornado
                retorno = parseRetorno(obj.getJSONObject("retorno"));
            } else {
                //obter dados de local
                JSONObject objLocal = getListaLocal(nomelista);

                //carrega obj retornado (constroi estrutura pois API retorna diferente)
                retorno = new Entities.Retorno();
                retorno.setStatus("ok");
                Entities.Dados dados = new Entities.Dados();
                Entities.Conteudo conteudo = new Entities.Conteudo();
                conteudo.setModulo(nomelista);
                conteudo.setRevisao(1);
                conteudo.setLista(parseLista(objLocal));
                dados.setConteudo(conteudo);
                retorno.setDados(dados);
            }

            return retorno;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //salvar lista
    public Entities.Retorno salvarLista(Entities.Lista object, String nomelista, String token) {
        //carregar serviço API
        DonaChicaApi service = new DonaChicaApi();

        //parse json to object
        JSONObject jsonObject = parseLista(object);

        //salva dados na api
        //JSONObject obj = service.salvarListas(jsonObject, token);
        JSONObject obj = jsonObject; //TODO: força o uso de dados locais

        //verifica se encontrou dados
        if (obj != null) {
            //grava dados em local
            setLista(obj, nomelista);
        } else {
            //obter dados de local
            JSONObject objLocal = getListaLocal(nomelista);
            obj = objLocal;
        }

        //carrega obj retornado (constroi estrutura pois API retorna diferente)
        Entities.Retorno retorno = new Entities.Retorno();
        retorno.setStatus("ok");
        Entities.Dados dados = new Entities.Dados();
        Entities.Conteudo conteudo = new Entities.Conteudo();
        conteudo.setModulo(nomelista);
        conteudo.setRevisao(object.getRevisao());
        conteudo.setLista(parseLista(obj));
        dados.setConteudo(conteudo);
        retorno.setDados(dados);

        return retorno;
    }

    //region Métodos expostos

    //retorna dados locais
    private JSONObject getListasLocal() {
        try {
            JSONObject obj = null;

            //recuperar dados de arquivo
            FileInputStream objLocal = new FileInputStream(JSON_ARQUIVO);

            //capturar dados de arquivo
            String strObj = Helper.getStringArquivo(objLocal);

            //verifica se possui informação
            if (strObj != "") {

                //converter dados para json
                obj = new JSONObject(strObj);
            }

            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //retorna dados locais
    private JSONObject getListaLocal(String nomelista) {
        try {
            JSONObject obj = null;

            //recuperar dados de arquivo
            FileInputStream objLocal = new FileInputStream(CTX.getFilesDir().getPath() + "/" + nomelista);

            //capturar dados de arquivo
            String strObj = Helper.getStringArquivo(objLocal);

            //verifica se possui informação
            if (strObj != "") {

                //converter dados para json
                obj = new JSONObject(strObj);
            }

            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //grava dados em local
    private void setListas(JSONObject obj) {
        try {
            //converter para string
            String strObj = obj.toString();

            FileOutputStream arquivo = new FileOutputStream(JSON_ARQUIVO);
            arquivo.write(strObj.getBytes());
            arquivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //grava dados em local
    private void setLista(JSONObject obj, String nomelista) {
        try {
            //converter para string
            String strObj = obj.toString();

            FileOutputStream arquivo = new FileOutputStream(CTX.getFilesDir().getPath() + "/" + nomelista);
            arquivo.write(strObj.getBytes());
            arquivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Parses Json to Entity

    //Parse Retorno
    public Entities.Retorno parseRetorno(JSONObject json) {

        Entities.Retorno retorno = new Entities.Retorno();

        try {
            retorno.setStatus(json.getString("status"));
            retorno.setDados(parseDados(json.getJSONObject("dados")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //Parse Dados
    public Entities.Dados parseDados(JSONObject json) {

        Entities.Dados retorno = new Entities.Dados();

        try {

            retorno.setConteudo(parseConteudo(json.getJSONObject("conteudo")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //Parse Conteúdo
    public Entities.Conteudo parseConteudo(JSONObject json) {

        Entities.Conteudo retorno = new Entities.Conteudo();

        try {

            retorno.setModulo(json.getString("modulo"));
            retorno.setRevisao(json.getInt("revisao"));

            //verifica se tipo de retorno é iten
            if(retorno.getModulo().equals("listas")) {
                //carrega iten
                retorno.setListas(parseListas(json.getJSONArray("listas")));
            }else{
                //carrega objeto
                retorno.setLista(parseLista(json));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //Parse Listas
    public List<Entities.Lista> parseListas(JSONArray array) {
        List<Entities.Lista> listas = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            listas.add(parseLista(array.optJSONObject(i)));
        }
        return listas;
    }

    //Parse Lista
    public Entities.Lista parseLista(JSONObject json) {
        Entities.Lista lista = new Entities.Lista();
        try {
            lista.setRevisao(json.getInt("revisao"));
            lista.setModulo(json.getString("modulo"));
            lista.setCategorias(parseCategorias(json.getJSONArray("categorias")));
            lista.setChecados(json.getInt("checados"));
            lista.setCor(json.getString("cor"));
            lista.setDescricao(json.getString("descricao"));
            lista.setDtalteracao(json.getString("dtalteracao"));
            lista.setIcone(json.getString("icone"));
            lista.setId(json.getInt("id"));
            lista.setNaochecados(json.getInt("naochecados"));
            lista.setNome(json.getString("nome"));
            lista.setPessoas(parsePessoas(json.getJSONArray("pessoas")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Parse Categorias
    public List<Entities.Categoria> parseCategorias(JSONArray array) {
        List<Entities.Categoria> categorias = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            categorias.add(parseCategoria(array.optJSONObject(i)));
        }
        return categorias;
    }

    //Parse Pessoas
    public List<Entities.Pessoa> parsePessoas(JSONArray array) {
        List<Entities.Pessoa> pessoas = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            pessoas.add(parsePessoa(array.optJSONObject(i)));
        }
        return pessoas;
    }

    //Parse Categoria
    public Entities.Categoria parseCategoria(JSONObject json) {
        Entities.Categoria categoria = new Entities.Categoria();
        try {
            categoria.setId(json.getInt("id"));
            categoria.setNome(json.getString("nome"));
            categoria.setItens(parseItens(json.getJSONArray("itens")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoria;
    }

    //Parse Pessoa
    public Entities.Pessoa parsePessoa(JSONObject json) {
        Entities.Pessoa pessoa = new Entities.Pessoa();
        try {
            pessoa.setId(json.getInt("id"));
            pessoa.setNome(json.getString("nome"));
            pessoa.setEmail(json.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pessoa;
    }

    //Parse Itens
    public List<Entities.Iten> parseItens(JSONArray array) {
        List<Entities.Iten> itens = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            itens.add(parseIten(array.optJSONObject(i)));
        }
        return itens;
    }

    //Parse Iten
    public Entities.Iten parseIten(JSONObject json) {
        Entities.Iten iten = new Entities.Iten();
        try {
            iten.setId(json.getInt("id"));
            iten.setCheck(json.getBoolean("check"));
            iten.setNaoachou(json.getBoolean("naoachou"));
            iten.setQtate(json.getInt("qtate"));
            iten.setQtde(json.getInt("qtde"));
            iten.setTexto(json.getString("texto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return iten;
    }

    //endregion

    //region Parses Entity to Json

    //Parse Retorno
    public JSONObject parseRetorno(Entities.Retorno retorno) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("status", retorno.getStatus());
            jsonObject.put("dados", parseDados(retorno.getDados()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    //Parse Dados
    public JSONObject parseDados(Entities.Dados dados) {

        JSONObject retorno = new JSONObject();

        try {
            retorno.put("conteudo", parseConteudo(dados.getConteudo()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //Parse Conteúdo
    public JSONObject parseConteudo(Entities.Conteudo conteudo) {

        JSONObject retorno = new JSONObject();

        try {

            retorno.put("modulo", conteudo.getModulo());
            retorno.put("revisao", conteudo.getRevisao());

            //verifica se tipo de retorno é iten
            if(conteudo.getModulo().equals("listas")) {
                //carrega iten
                retorno.put("listas",parseListas(conteudo.getListas()));
            }else{
                //carrega objeto
                retorno.put("lista",parseLista(conteudo.getLista()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //Parse Listas
    public JSONArray parseListas(List<Entities.Lista> listas) {
        JSONArray array = new JSONArray();
        for (Entities.Lista lista : listas) {
            array.put(parseLista(lista));
        }
        return array;
    }

    //Parse Lista
    public JSONObject parseLista(Entities.Lista obj) {
        JSONObject lista = new JSONObject();
        try {
            lista.put("revisao", obj.getRevisao());
            lista.put("modulo", obj.getModulo());
            lista.put("categorias", parseCategorias(obj.getCategorias()));
            lista.put("checados", obj.getChecados());
            lista.put("cor", obj.getCor());
            lista.put("descricao", obj.getDescricao());
            lista.put("dtalteracao", obj.getDtalteracao());
            lista.put("icone", obj.getIcone());
            lista.put("id", obj.getId());
            lista.put("naochecados", obj.getNaochecados());
            lista.put("nome", obj.getNome());
            lista.put("pessoas", parsePessoas(obj.getPessoas()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Parse Categorias
    public JSONArray parseCategorias(List<Entities.Categoria> categorias) {
        JSONArray array = new JSONArray();
        for (Entities.Categoria categoria : categorias) {
            array.put(parseCategoria(categoria));
        }
        return array;
    }

    //Parse Pessoas
    public JSONArray parsePessoas(List<Entities.Pessoa> pessoas) {
        JSONArray array = new JSONArray();
        for (Entities.Pessoa pessoa : pessoas) {
            array.put(parsePessoa(pessoa));
        }
        return array;
    }

    //Parse Categoria
    public JSONObject parseCategoria(Entities.Categoria categoria) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", categoria.getId());
            jsonObject.put("nome", categoria.getNome());
            jsonObject.put("itens", parseItens(categoria.getItens()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //Parse Pessoa
    public JSONObject parsePessoa(Entities.Pessoa pessoa) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", pessoa.getId());
            jsonObject.put("nome", pessoa.getNome());
            jsonObject.put("email", pessoa.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //Parse Itens
    public JSONArray parseItens(List<Entities.Iten> itens) {
        JSONArray jsonArray = new JSONArray();
        for (Entities.Iten iten : itens) {
            jsonArray.put(parseIten(iten));
        }
        return jsonArray;
    }

    //Parse Iten
    public JSONObject parseIten(Entities.Iten iten) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", iten.getId());
            jsonObject.put("check", iten.getCheck());
            jsonObject.put("naoachou", iten.getNaoachou());
            jsonObject.put("qtate", iten.getQtate());
            jsonObject.put("qtde", iten.getQtde());
            jsonObject.put("texto", iten.getTexto());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //endregion
}
