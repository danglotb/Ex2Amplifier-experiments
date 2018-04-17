package eu.stamp.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.stamp.project.git.ProjectJSON;
import eu.stamp.project.git.PullRequestJSON;
import org.junit.Test;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 16/04/18
 */
public class MainCloner {

    public static void main(String[] args) throws Exception {

        final String nameOfRepository;
        final int idOfPullRequest;
        try {
            nameOfRepository = args[0];
            idOfPullRequest = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Usage: <owner/project> <id>");
            System.err.println("Example: square/javapoet 550");
            throw e;
        }
//        final String nameOfRepository = "square/javapoet";
//        final String nameOfRepository = "xwiki/xwiki-rendering";
//        final String nameOfRepository = "prestodb/pcccresto";
//        final String nameOfRepository = "protostuff/protostuff";
//        final String nameOfRepository = "mybatis/mybatis-3";

        final GitHub gitHub = GitHub.connectAnonymously();
        final GHRepository repository = gitHub.getRepository(nameOfRepository);
        final GHPullRequest ghPullRequest = repository.getPullRequest(idOfPullRequest);
        final String fileName = "dataset/january-2018" + "/" + repository.getName() + ".json";
        final PullRequestJSON pullRequestJSON = new PullRequestJSON(ghPullRequest.getNumber(),
                ghPullRequest.getBase().getRepository().getGitTransportUrl(),
                ghPullRequest.getBase().getRef(),
                ghPullRequest.getBase().getCommit().getSHA1(),
                ghPullRequest.getHead().getRepository().getGitTransportUrl(),
                ghPullRequest.getHead().getRef(),
                ghPullRequest.getHead().getCommit().getSHA1()
        );
        final ProjectJSON projectJSON;
        if (new File(fileName).exists()) {
            Gson gson = new Gson();
            projectJSON = gson.fromJson(new FileReader(fileName), ProjectJSON.class);
            projectJSON.pullRequests.add(pullRequestJSON);
        } else {
            projectJSON = new ProjectJSON(repository.getName(),
                    repository.getGitTransportUrl(),
                    Collections.singletonList(pullRequestJSON));
        }
        try (Writer writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(projectJSON, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}