package br.com.gruposvb.donachica;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ender on 29/06/2016.
 */
public class Services {

    //region variáveis globais da classe
    private static final String Api = "http://www.gruposvb.com.br/DonaChicaApi/Simples";
    private static final String POST = "POST";
    private static final String GET = "GET";
    //endregion

    //cosntrutor
    public Services() {

    }

    //Posta dados para API e recebe dados de API
    private String send(String url, String json, String tipo) {

        //Retorno padrão
        StringBuilder responseOutput = new StringBuilder();

        try {

            //carrega URI com url
            URL uri = new URL(url);

            //abre conexão
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();

            //determina o tipo de método GET/POST
            connection.setRequestMethod(tipo);
            connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");

            //se for post
            if(tipo == POST) {
                //encapsula os dados
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(json);
                dStream.flush();
                dStream.close();
            }

            //captura código de retorno
            int responseCode = connection.getResponseCode();

            //verifica se retornou código 200
            if(responseCode == 200) {

                //varre o buffer e captura retorno
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                //carrega retorno
                String line;
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
            }

            //fecha conexão se não for nula
            if (connection != null) {
                connection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseOutput.toString();
    }

    //region API - mod_login

    //invoca API mod_login/login
    public JSONObject getLogin(JSONObject parametro) {

        //define retorno padrão
        JSONObject obj = null;

        try {
            //define url api
            String url = Api + "/mod_login/api.php/login";

            //posta dados para api e recebe json
            String json = send(url, parametro.toString(), POST);

            //verifica se há resposta e converte para obj
            if(json != "")
                obj = new JSONObject(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    //invoca API mod_login/logout
    public JSONObject getLogout() {

        //define retorno padrão
        JSONObject obj = null;

        try {
            //define url api
            String url = Api + "/mod_login/api.php/logout";

            //posta dados para api e recebe json
            String json = send(url, null, GET);

            //verifica se há resposta e converte para obj
            if(json != "")
                obj = new JSONObject(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    //endregion

    //region API - mod_donachica

    //invoca API mod_login/login

    public JSONObject obterListas(JSONObject parametro, String token) {

        //define retorno padrão
        JSONObject obj = null;

        try {
            //define url api
            String url = Api + "/mod_donachica/api.php/conteudo/obter/" + token;

            //posta dados para api e recebe json
            String json = send(url, parametro.toString(), POST);

            //verifica se há resposta e converte para obj
            if(json != "")
                obj = new JSONObject(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }

    //endregion

}
