package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {
            HttpGet request = new HttpGet(
                    "https://api.nasa.gov/planetary/apod?api_key=aQcCFQRDYUg7XHgbzmZai5xCTeBQjXUA4KGkiT7O"
            );
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                NASAfile nasaFile = mapper.readValue(
                        response.getEntity().getContent(),
                        new TypeReference<>(){});
                System.out.println(nasaFile);
                HttpGet requestNasa = new HttpGet(nasaFile.getUrl());
                try (CloseableHttpResponse responseNasa = httpClient.execute(requestNasa)) {
                    File file = new File("NASA.jpg");
                    BufferedImage image = ImageIO.read(responseNasa.getEntity().getContent());
                    ImageIO.write(image, "jpg", file);
                } catch (IOException e) {
                    e.getStackTrace();
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}