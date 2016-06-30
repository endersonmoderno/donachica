package br.com.gruposvb.donachica;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ender on 29/06/2016.
 */
public class Helper {

    //retorna conteudo de arquivo
    public static String getStringArquivo(FileInputStream arquivo) {

        String retorno = "";

        try {
            int c;

            //varre arquivo
            while ((c = arquivo.read()) != -1) {
                //carrega conteúdo do arquivo
                retorno = retorno + Character.toString((char) c);
            }
            return retorno;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retorno;
    }

    //retorna data formatada
    public static String getDataFormatada(Date data){
        //formato de data
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        return df.format(data);
    }

    //retorna data atual formatada
    public static String getDataAtual(){
        return getDataFormatada(Calendar.getInstance().getTime());
    }

}
