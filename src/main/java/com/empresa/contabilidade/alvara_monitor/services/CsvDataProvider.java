package com.empresa.contabilidade.alvara_monitor.services;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class CsvDataProvider {

    public InputStream readDataFromUrl(String urlString) throws IOException {
        final var url = new URL(urlString);
        return url.openStream();
    }
}