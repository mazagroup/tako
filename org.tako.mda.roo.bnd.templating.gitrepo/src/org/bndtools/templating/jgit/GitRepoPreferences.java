package org.bndtools.templating.jgit;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aQute.bnd.header.Parameters;
import aQute.lib.io.IO;

public class GitRepoPreferences {

    public static final String INITIAL_GITHUB_REPOS;
    static {
        try {
            INITIAL_GITHUB_REPOS = IO.collect(GitRepoPreferences.class.getResourceAsStream("initialrepos.txt"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final String PREF_GITHUB_REPOS = "githubRepos";
    private static final String PREF_GIT_REPOS = "gitRepos";

    private final Bundle bundle = FrameworkUtil.getBundle(GitRepoPreferences.class);

    public GitRepoPreferences() {
    }

    public Parameters getGithubRepos() {
        return new Parameters(INITIAL_GITHUB_REPOS);
    }

    public void setGithubRepos(Parameters params) {
        //store.setValue(PREF_GITHUB_REPOS, params.toString());
    }

    public Parameters getGitRepos() {
        return new Parameters(INITIAL_GITHUB_REPOS);
    }

    public void setGitRepos(Parameters params) {
        // store.setValue(PREF_GIT_REPOS, params.toString());
    }

    public boolean save() {
        return true;
    }

    public static String removeDuplicateMarker(String s) {
        return s.replaceAll("~+$", "");
    }
}
