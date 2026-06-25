package sk.drake.test;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate>
{
    @Override
    public LocalDate unmarshal(String string) throws Exception {
        return LocalDate.parse(string, formatter);
    }

    @Override
    public String marshal(LocalDate date) throws Exception {
        return date.toString();
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
