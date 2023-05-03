package de.yggdrasil128.factorial;

import de.yggdrasil128.factorial.model.Fraction;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FractionConverter implements AttributeConverter<Fraction, String> {
    @Override
    public String convertToDatabaseColumn(Fraction fraction) {
        return fraction.toString();
    }

    @Override
    public Fraction convertToEntityAttribute(String s) {
        return Fraction.of(s);
    }
}
