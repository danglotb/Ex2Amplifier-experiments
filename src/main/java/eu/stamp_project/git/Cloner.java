package eu.stamp_project.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Cloner {

    /**
     * this method clone the repository twice: the base and the head of the pr
     * It then, reset hard using the given sha inside the eu.stamp_project.git.PullRequestJSON object
     * The base version is in out/id_pr/ and the head version is ins out/id_pr_modified
     * id_pr is the value in the field id of the given eu.stamp_project.git.PullRequestJSON object
     *
     * @param pr  eu.stamp_project.git.PullRequestJSON that contains all the needed information
     * @param out prefix for output
     */
    public static void cloneBothVersionOf(PullRequestJSON pr, String out) {
        try {
            final String outputPath = out + "/" + pr.id;
            final File outputFile = new File(outputPath);
            final File outputFileModified = new File(outputPath + SUFFIX_VERSION_2);
            if (outputFile.exists()) {
                return;
            }
            clone(pr.baseUrl, pr.baseRef, outputFile);
            // we add the pr remote and fetch it, in order to make the diff
            addRemoteAndFetchIt(pr.headUrl, outputFile);
            clone(pr.headUrl, pr.headRef, outputFileModified);
            resetHard(pr.baseSha, outputFile);
            resetHard(pr.headSha, outputFileModified);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addRemoteAndFetchIt(String uri, File output) throws IOException, GitAPIException, URISyntaxException {
        final RemoteAddCommand remoteAddCommand = Git.open(output)
                .remoteAdd();
        remoteAddCommand.setName("pr");
        remoteAddCommand.setUri(new URIish(uri));
        remoteAddCommand.call();
        Git.open(output)
                .fetch()
                .setRemote("pr")
                .call();
    }

    private static void resetHard(String sha, File output) throws IOException, GitAPIException {
        Git.open(output)
                .reset()
                .setMode(ResetCommand.ResetType.HARD)
                .setRef(sha)
                .call();
    }

    private static void clone(String uri, String branch, File output) throws GitAPIException {
        Git.cloneRepository()
                .setURI(uri)
                .setDirectory(output)
                .setBranch(branch)
                .call();
    }

    public static final String SUFFIX_VERSION_2 = "_modified";

}