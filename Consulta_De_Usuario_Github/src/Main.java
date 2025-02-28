import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static javax.swing.JOptionPane.*;

public class Main {
    public static void main(String[] args) {

        int resultado = YES_OPTION;
        String username = showInputDialog("Bem vindo ao programa de busca de usuários!\nInforme o nome de um usuário para começar");
        usernameValido(username);
        while (resultado == YES_OPTION) {
            buscaUsuario(username);

            usernameValido(username);

            resultado = showConfirmDialog(
                    null,
                    "Deseja procurar outro usuário?",
                    "Pesquisar novamente?",
                    YES_NO_OPTION);
            if (resultado != YES_OPTION) {
                break;
            }
            username = showInputDialog(null, "Informe o nome de um usuário para a busca");
        }
        showMessageDialog(null, "Fim do programa!");
    }

    private static void exibirJanela(ImageIcon icon, GithubUser githubUser) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Personalização da Janela
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(imageLabel);

        JLabel nameLabel = new JLabel(
                "<html>" +
                "<ul>" +
                "<li><b>Nome:</b> " + githubUser.name() + "</li>" +
                "<li><b>Repositórios Públicos:</b> " + githubUser.public_repos() + "</li>" +
                "<li><b>Seguidores:</b> " + githubUser.followers() + "</li>" +
                "<li><b>Link do Perfil:</b> <a href='" + githubUser.html_url() + "'>" + githubUser.html_url() + "</a></li>" +
                "</ul>" +
                "</html>"
        );
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);

        nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URL(githubUser.html_url()).toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        );

        showMessageDialog(null, panel, "Perfil do Github de " + githubUser.login(), PLAIN_MESSAGE);
    }

    private static void buscaUsuario(String username) {
        String adress = "https://api.github.com/users/" + username;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(adress))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build();
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            if (response.statusCode() == 404) {
                showMessageDialog(null, "Usuário não encontrado");
            }
            Gson gson = new GsonBuilder()
                    .create();
            GithubUser githubUser = gson.fromJson(json, GithubUser.class);
            ImageIcon icon = new ImageIcon(new URL(githubUser.avatar_url()));
            Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            exibirJanela(icon, githubUser);
        } catch (IOException | InterruptedException e) {
            System.out.println("Houve um erro durante a consulta à API do GitHub.");
        } catch (ErroConsultaGitHubException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void usernameValido (String username) {
        if (username == null || username.trim().isEmpty()) {
            showMessageDialog(null, "Operação cancelada. Encerrando o programa.");
            System.exit(0);
        }
    }
}
