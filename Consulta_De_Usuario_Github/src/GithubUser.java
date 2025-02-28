import javax.swing.*;

public record GithubUser(String login, String name, int public_repos, int followers, String avatar_url, String html_url) {

}
