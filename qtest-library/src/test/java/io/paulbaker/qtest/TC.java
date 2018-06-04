package io.paulbaker.qtest;

import kotlin.Pair;

import java.util.List;

/**
 * Created by Paul N. Baker on 04/13/2018
 */
public class TC {

    public static void main(String[] args) {
        QTestClient qTestClient = new QTestClient("wgu", new Pair<>("paul.baker@wgu.edu", "xJ9D1dcyT3J1"));
        ProjectClient projectClient = qTestClient.projectClient();
        List<Project> projects = projectClient.projects();
        projects.forEach(System.out::println);

        List<User> users = projectClient.users(49099);
//        UserClient userClient = qTestClient.createUserClient();
//        users.forEach(user -> {
//            User altUser = userClient.fromId(user.getId());
//            System.out.println(user.toString() + "  " + altUser.toString());
//        });

        projectClient.projects().forEach(project -> {
            Project altProject = projectClient.fromId(project.getId());
            System.out.printf("%s =? %s\n", project, altProject);
        });

        projectClient.projects().forEach(project -> {
            ReleaseClient releaseClient = qTestClient.releaseClient(project.getId());
            List<Release> releases = releaseClient.releases();
            releases.forEach(System.out::println);
        });

//
//        projectClient
//                .projects()
//                .forEach(
//                        project -> {
//                            Project altProject = projectClient.fromId(project.getId());
//                            System.out.println(project + " " + altProject);
//                        });
//
//        Project project = projectClient.projects().get(0);
//        ReleaseClient releaseClient = clientProducer.createReleaseClient();
//        releaseClient
//                .releases(project.getId())
//                .forEach(
//                        release -> {
//                            System.out.println(release);
//                        });
    }
}
