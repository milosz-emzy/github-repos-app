package org.emzy.client;

public record GithubBranchResponse(
        String name,
        GithubCommit commit
) {
    public record GithubCommit(
            String sha
    ) {
    }
}
