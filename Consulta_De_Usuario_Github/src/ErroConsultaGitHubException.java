public class ErroConsultaGitHubException extends RuntimeException {
    private String mensagem;

    public ErroConsultaGitHubException(String mensagem) {
        super(mensagem);
    }

    @Override
    public String getMessage() {
        return this.mensagem;
    }
}
