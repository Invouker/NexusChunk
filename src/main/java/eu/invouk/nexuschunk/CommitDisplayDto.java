package eu.invouk.nexuschunk;


public record CommitDisplayDto(String sha, String message, String committerName, String formattedDate) {

}