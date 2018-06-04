package io.paulbaker.qtest;

import java.util.List;

/**
 * Created by Paul N. Baker on 04/13/2018
 */
public class TC {

    public static void main(String[] args) {
        LoginTokenSupplier loginTokenSupplier = new LoginTokenSupplier("wgu", "paul.baker@wgu.edu", "");
        LoginToken loginToken = loginTokenSupplier.get();
        System.out.println(loginToken);

        ClientProducer clientProducer = new ClientProducer("https://wgu.qtestnet.com", loginToken);
        ProjectClient projectClient = clientProducer.createProjectClient();
        List<User> users = projectClient.users(49099);
        UserClient userClient = clientProducer.createUserClient();
        users.forEach(
                user -> {
                    User altUser = userClient.fromId(user.getId());
                    System.out.println(user.toString() + "  " + altUser.toString());
                });

        projectClient
                .projects()
                .forEach(
                        project -> {
                            Project altProject = projectClient.fromId(project.getId());
                            System.out.println(project + " " + altProject);
                        });

        Project project = projectClient.projects().get(0);
        ReleaseClient releaseClient = clientProducer.createReleaseClient();
        releaseClient
                .releases(project.getId())
                .forEach(
                        release -> {
                            System.out.println(release);
                        });
    }
}
