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
import br.com.gruposvb.donachica.Services;

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
            Services service = new Services();

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
            Services service = new Services();

            //faz login na api
            JSONObject obj = service.obterListas(parametros, token);

            //verifica se encontrou dados
            if (obj != null) {
                //grava dados em local
                setLista(obj, nomelista);
            } else {
                //obter dados de local
                JSONObject objLocal = getListaLocal(nomelista);
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
            FileInputStream objLocal = new FileInputStream(nomelista);

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

            FileOutputStream arquivo = new FileOutputStream(nomelista);
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
            retorno.setListas(parseListas(json.getJSONArray("listas")));

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

}
