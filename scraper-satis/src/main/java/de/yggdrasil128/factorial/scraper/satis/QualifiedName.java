package de.yggdrasil128.factorial.scraper.satis;

public record QualifiedName(String qualified, String simple) {

    public static QualifiedName of(String qualified) {
        int separator = qualified.indexOf('.');
        String simple = -1 == separator ? qualified : qualified.substring(1 + separator);
        return new QualifiedName(qualified, simple);
    }

    public static QualifiedName unqualified(String simple) {
        return new QualifiedName(simple, simple);
    }

}
