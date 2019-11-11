
package br.com.fiap.perfilcar.kafka.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.fiap.perfilcar.kafka.domain.MensagemSlack;

@Service
public class Consumer {

    private final RestTemplate restTemplate;

    @Value("${urlSlack}")
    private String urlSlack;

    public Consumer(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @KafkaListener(topics = "PerfilCarLog",  groupId = "group_id")
    public void consume(String message) {
 
        MensagemSlack mensagem = new MensagemSlack();
        mensagem.setText(message);

        ObjectMapper jsonMensagem = new ObjectMapper();
        
        String requestJson = "";
        try {
            requestJson = jsonMensagem.writerWithDefaultPrettyPrinter().writeValueAsString(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(requestJson, headers);

        ResponseEntity<MensagemSlack> responseEntity = restTemplate.exchange(urlSlack, HttpMethod.POST, request,
        MensagemSlack.class);
        
    }
}