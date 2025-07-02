package com.empresa.contabilidade.alvara_monitor.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class CsvDataProvider {

    public InputStream lerDadosDaUrl(String urlString) throws IOException {
        final var url = new URL(urlString);
        return url.openStream();
    }
}