package br.com.gruposvb.donachica;

import java.util.List;

/**
 * Created by ender on 02/07/2016.
 */
public class Entities {

    public static class Login {
        private String token;

        public String getToken() {
            return this.token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        private String status;

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        private String erro;

        public String getErro() {
            return this.erro;
        }

        public void setErro(String erro) {
            this.erro = erro;
        }
    }

    public static class Iten {
        private int id;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private String texto;

        public String getTexto() {
            return this.texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }

        private boolean check;

        public boolean getCheck() {
            return this.check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        private boolean naoachou;

        public boolean getNaoachou() {
            return this.naoachou;
        }

        public void setNaoachou(boolean naoachou) {
            this.naoachou = naoachou;
        }

        private int qtde;

        public int getQtde() {
            return this.qtde;
        }

        public void setQtde(int qtde) {
            this.qtde = qtde;
        }

        private int qtate;

        public int getQtate() {
            return this.qtate;
        }

        public void setQtate(int qtate) {
            this.qtate = qtate;
        }
    }

    public static class Categoria {
        private int id;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private String nome;

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        private List<Iten> itens;

        public List<Iten> getItens() {
            return this.itens;
        }

        public void setItens(List<Iten> itens) {
            this.itens = itens;
        }
    }

    public static class Pessoa {
        private int id;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private String nome;

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        private String email;

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Lista {
        private int revisao;

        public int getRevisao() {
            return this.revisao;
        }

        public void setRevisao(int revisao) {
            this.revisao = revisao;
        }

        private String modulo;

        public String getModulo() {
            return this.modulo;
        }

        public void setModulo(String modulo) {
            this.modulo = modulo;
        }

        private int id;

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private String nome;

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        private String descricao;

        public String getDescricao() {
            return this.descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        private String cor;

        public String getCor() {
            return this.cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }

        private int checados;

        public int getChecados() {
            return this.checados;
        }

        public void setChecados(int checados) {
            this.checados = checados;
        }

        private int naochecados;

        public int getNaochecados() {
            return this.naochecados;
        }

        public void setNaochecados(int naochecados) {
            this.naochecados = naochecados;
        }

        private String dtalteracao;

        public String getDtalteracao() {
            return this.dtalteracao;
        }

        public void setDtalteracao(String dtalteracao) {
            this.dtalteracao = dtalteracao;
        }

        private String icone;

        public String getIcone() {
            return this.icone;
        }

        public void setIcone(String icone) {
            this.icone = icone;
        }

        private List<Categoria> categorias;

        public List<Categoria> getCategorias() {
            return this.categorias;
        }

        public void setCategorias(List<Categoria> categorias) {
            this.categorias = categorias;
        }

        private List<Pessoa> pessoas;

        public List<Pessoa> getPessoas() {
            return this.pessoas;
        }

        public void setPessoas(List<Pessoa> pessoas) {
            this.pessoas = pessoas;
        }
    }

    public static class Conteudo {
        private int revisao;

        public int getRevisao() {
            return this.revisao;
        }

        public void setRevisao(int revisao) {
            this.revisao = revisao;
        }

        private String modulo;

        public String getModulo() {
            return this.modulo;
        }

        public void setModulo(String modulo) {
            this.modulo = modulo;
        }

        private List<Lista> listas;

        public List<Lista> getListas() {
            return this.listas;
        }

        public void setListas(List<Lista> listas) {
            this.listas = listas;
        }
    }

    public static class Dados {
        private Conteudo conteudo;

        public Conteudo getConteudo() {
            return this.conteudo;
        }

        public void setConteudo(Conteudo conteudo) {
            this.conteudo = conteudo;
        }
    }

    public static class Retorno {
        private String status;

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        private Dados dados;

        public Dados getDados() {
            return this.dados;
        }

        public void setDados(Dados dados) {
            this.dados = dados;
        }
    }
}
