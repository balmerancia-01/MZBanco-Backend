
package mz.banco.wsd.dto;

public class LoginResponse {

    private Integer idCliente;
    private Integer idFuncionario;
    private String nome;
    private String email;
    private String mensagem;
    private String token;
    private String tipo;
    private String cargo;

    public LoginResponse() {
    }

    // Resposta para clientes
    public LoginResponse(Integer idCliente, String nome, String email,
                         String mensagem, String token) {

        this.idCliente = idCliente;
        this.nome = nome;
        this.email = email;
        this.mensagem = mensagem;
        this.token = token;
        this.tipo = "CLIENTE";
        this.cargo = "CLIENTE";
    }

    // Resposta para funcionários
    public LoginResponse(Integer idFuncionario, String nome, String email,
                         String mensagem, String token,
                         String tipo, String cargo) {

        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.email = email;
        this.mensagem = mensagem;
        this.token = token;
        this.tipo = tipo;
        this.cargo = cargo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Integer idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
