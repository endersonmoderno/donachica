package br.com.gruposvb.donachica.Models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.gruposvb.donachica.Entities;
import br.com.gruposvb.donachica.Helper;
import br.com.gruposvb.donachica.Services.DonaChicaApi;

/**
 * Created by ender on 29/06/2016.
 */
public class LoginModel {

    //region variáveis globais da classe
    private static final String JSON_NAME = "login"; //json
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
    public Entities.Login getLogin(String login, String senha) {

        try {
            //carrega usuario online
            JSONObject obj = getUsuarioOnline(login, senha);

            //verifica se não encontrou usuário
            if (obj == null)
                return null;

            //carrega obj retornado
            JSONObject retorno = obj.getJSONObject("retorno");

            //verifica se há retorno
            if (retorno != null) {

                //carrega dados
                JSONObject dados = retorno.getJSONObject("dados");

                if (dados != null) {

                    //carrega status
                    String status = retorno.getString("status");

                    //converter json em Entity
                    Entities.Login entity = new Entities.Login();
                    entity.setStatus(status);

                    //verifica se retornou ok
                    if (status.equals("ok")) {
                        entity.setToken(dados.getString("token"));

                        //grava usuário em local
                        setToken(obj);
                    } else {
                        entity.setErro(dados.getString("erro"));
                    }

                    return entity;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    //faz logout
    public boolean getLogout() {

        try {

            //limpa dados de login local
            FileOutputStream arquivo = new FileOutputStream(JSON_ARQUIVO);
            arquivo.write(("").getBytes());
            arquivo.close();

            //esvazia sessão API
            DonaChicaApi model = new DonaChicaApi();
            model.getLogout();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Entities.Login obterToken(){
        //recupera token
        JSONObject objToken = getToken();
        if (objToken != null) {
            try {
                //carrega retorno
                JSONObject retorno = objToken.getJSONObject("retorno");
                if (retorno != null) {

                    //carrega dados
                    JSONObject dados = retorno.getJSONObject("dados");
                    if (dados != null) {

                        //converter json em Entity
                        Entities.Login entity = new Entities.Login();
                        entity.setStatus("local");
                        entity.setToken(dados.getString("token"));

                        return entity;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //endregion

    //region métodos internos

    //retorna token
    private JSONObject getToken() {
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

        //retorno padrão
        JSONObject obj = new JSONObject();

        try {

            //carregar serviço API
            DonaChicaApi service = new DonaChicaApi();

            //carrega parâmtros
            JSONObject parametros = new JSONObject();
            parametros.put("login", login);
            parametros.put("senha", senha);

            //faz login na api
            obj = service.getLogin(parametros);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    //endregion
}
