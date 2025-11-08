package eu.invouk.nexuschunk.admin.github.dtos;


public record CommitDisplayDto(String sha, String message, String committerName, String formattedDate) {

}