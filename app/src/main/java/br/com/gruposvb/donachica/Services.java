package br.com.gruposvb.donachica;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ender on 29/06/2016.
 */
public class Services {

    //region variáveis globais da classe
    private static final String Api = "http://www.gruposvb.com.br/DonaChicaApi/Simples";
    //endregion

    //cosntrutor
    public Services() {

    }

    //Posta dados para API
    private String post(String url, String parametros) {

        StringBuilder responseOutput = new StringBuilder();

        try {

            URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(parametros);
            dStream.flush();
            dStream.close();
            int responseCode = connection.getResponseCode();

            //verifica se retornou código 200
            if(responseCode == 200) {
                //varre o bffer e captura retorno
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseOutput.toString();
    }

    //region API - mod_login

    //invoca API mod_login
    public JSONObject getLogin(JSONObject parametro) {

        //define retorno padrão
        JSONObject obj = null;

        try {
            //define url api
            String url = Api + "/mod_login/api.php/login";

            //posta dados para api e recebe json
            String json = post(url, parametro.toString());

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
