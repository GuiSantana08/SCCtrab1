package scc.utils;

public enum Constants {
    conURL58201("https://cosmos-scc.documents.azure.com:443/"),

    dbKey58201("3yJ9ApcLNiJpbJJEPHDS04cbSBZygR3mmcUfeCGnJeJNP1opXQ4Nsofyec5WUtIGxN9NlxszDSZ4ACDb38RUqg=="),
    dbName58201("scc-58201"),

    conURL60182("https://scc232460182.documents.azure.com:443/"),
    dbKey60182("S7xFx9UhYoBVAQ4wTEBz9W7KtRGGDiSdcrckwFKlGql3LfUHZ3CPBtrzvy3924hQbRopSU0DcV9gACDbL2FjNw=="),
    scc232460182("scc2324"),
    deletedUser("Deleted User");

    private final String string;

    Constants(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
