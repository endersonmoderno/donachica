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

import br.com.gruposvb.donachica.Helper;

/**
 * Created by ender on 29/06/2016.
 */
public class LoginModel {

    //region variáveis globais da classe
    private static final String JSON_NAME = "usuarios"; //json
    private static Context CTX;
    private static String JSON_ARQUIVO;
    //endregion

    //region métodos públicos

    //construtor
    public LoginModel(Context ctx) {
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

    //faz login
    public JSONObject getLogin(String login, String senha) {
        //carrega usuario online
        JSONObject obj = getUsuarioOnline(login, senha);

        //verifica se não encontrou usuário
        if(obj == null)
            return null;

        //grava usuário em local
        setToken(obj);

        return obj;
    }

    //retorna usuário
    public JSONObject getToken() {
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

    //endregion

    //region métodos internos

    //grava dados em local
    private void setToken(JSONObject obj) {
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

    //retorna usuário online
    private JSONObject getUsuarioOnline(String login, String senha) {
        try {

            //TODO: trocar por consulta na API

            //objeto padrão
            JSONObject obj = new JSONObject();
            obj.put("status", "ok");

            //dados
            JSONObject dados = new JSONObject();
            dados.put("token", "xxxxxxxx");

            //Array dados
            JSONArray arrDados = new JSONArray();
            arrDados.put(dados);

            obj.put("dados", arrDados);

            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //endregion
}
