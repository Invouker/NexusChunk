package eu.invouk.nexuschunk.admin.github.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommitDto(
        String sha,
        @JsonProperty("commit") CommitDetails commitDetails
) {
    public record CommitDetails(
            String message,
            Committer committer
    ) {}

    public record Committer(
            String name,
            String date
    ) {}
}