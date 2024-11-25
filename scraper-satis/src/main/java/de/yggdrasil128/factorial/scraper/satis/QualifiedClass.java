package de.yggdrasil128.factorial.scraper.satis;

public record QualifiedClass(String descriptor, QualifiedName name) {

    public static QualifiedClass of(String source) {
        int classSeparator = source.indexOf('\'');
        if (2 > source.length() || '\'' != source.charAt(source.length() - 1) || -1 == classSeparator
                || source.length() - 1 == classSeparator) {
            throw new IllegalArgumentException("bad syntax: " + source);
        }
        String descriptor = source.substring(0, classSeparator);
        String name = source.substring(1 + classSeparator, source.length() - 1);
        return new QualifiedClass(descriptor, QualifiedName.of(name));
    }

}