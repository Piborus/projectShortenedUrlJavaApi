package com.example.shorterUrl.controller;

import com.example.shorterUrl.dto.AtualizarUrlDto;
import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.dto.UrlShortenerRequest;
import com.example.shorterUrl.dto.UrlShortenerResponse;
import com.example.shorterUrl.model.ShorterUrl;
import com.example.shorterUrl.service.ShorterUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ShorterUrlControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<UrlShortenerRequest> urlShortenerRequestJacksonTester;

    @Autowired
    private JacksonTester<UrlShortenerResponse> urlShortenerResponseJacksonTester;

    @MockBean
    private ShorterUrlService shorterUrlService;

    @InjectMocks
    private ShorterUrlController shorterUrlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Deveria devolver codigo http400 quando informações estão invalidos")
    void cenario1() throws Exception {
        var response = mvc.perform(post("/api/shorten"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http200 quando informações estão válidas")
    void cenario2() throws Exception {
        UrlShortenerRequest urlTeste = new UrlShortenerRequest();
        urlTeste.setLongUrl("https://www.youtube.com/watch?v=GNJtPFXUnm4");

        var urlShortenerResponse = new UrlShortenerResponse();
        urlShortenerResponse.setShortUrl("/api/yo48745");

        when(shorterUrlService.generateShortUrl(any())).thenReturn(urlShortenerResponse);

        var response = mvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlShortenerRequestJacksonTester.write(urlTeste).getJson())
                )
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var jsonEsperado = urlShortenerResponseJacksonTester.write(urlShortenerResponse).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }


    @Test
    @DisplayName("Deveria devolver 404 quando a URL curta não estiver presente")
    void cenario3() throws Exception {
        String key = "non-existent-key";
        String shortUrl = "http://localhost/" + key;

        // Defina o comportamento esperado do serviço aqui:
        when(shorterUrlService.expandUrl(eq(shortUrl))).thenReturn(null);

        var response = mvc.perform(MockMvcRequestBuilders.get("/" + key))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Teste detalhaUrlPorId - URL encontrada")
    void cenario4() {
        Long id = 1L;
        DetalhaUrlDto detalhaUrl = new DetalhaUrlDto(id, "http://short.url/abc123", "http://www.example.com", 5L);
        when(shorterUrlService.detalhaUrlPorId(id)).thenReturn(detalhaUrl);

        ResponseEntity<DetalhaUrlDto> response = shorterUrlController.detalhaUrlPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(detalhaUrl, response.getBody());
    }

    @Test
    @DisplayName("Teste detalhaUrlPorId - URL não encontrada")
    void cenario5() {
        Long id = 1L;
        when(shorterUrlService.detalhaUrlPorId(id)).thenReturn(null);

        ResponseEntity<DetalhaUrlDto> response = shorterUrlController.detalhaUrlPorId(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    @DisplayName("Teste listar url")
    void cenario6() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<DetalhaUrlDto> page = mock(Page.class);
        when(shorterUrlService.listarPaginado(pageRequest)).thenReturn(page);

        ResponseEntity<Page<DetalhaUrlDto>> response = shorterUrlController.listarUrl(pageRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(page);
    }

    @Test
    @DisplayName("Teste excluirUrl")
    void cenario7() {
        Long id = 1L;
        ResponseEntity<Void> response = shorterUrlController.excluirUrl(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Teste atualizarLongUrl - URL encontrada")
    void cenario8() {
        Long id = 1L;
        String newLongUrl = "http://www.new-example.com";
        AtualizarUrlDto atualizarUrl = new AtualizarUrlDto();
        atualizarUrl.setId(id);
        atualizarUrl.setLongUrl(newLongUrl);

        ShorterUrl updatedShorterUrl = new ShorterUrl();
        when(shorterUrlService.atualizarLongUrl(id, newLongUrl)).thenReturn(updatedShorterUrl);

        ResponseEntity<String> response = shorterUrlController.atualizarLongUrl(atualizarUrl);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Teste atualizarLongUrl - URL não encontrada")
    void cenario9() {
        Long id = 1L;
        String newLongUrl = "http://www.new-example.com";
        AtualizarUrlDto atualizarUrl = new AtualizarUrlDto();
        when(shorterUrlService.atualizarLongUrl(id, newLongUrl)).thenReturn(null);

        ResponseEntity<String> response = shorterUrlController.atualizarLongUrl(atualizarUrl);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Teste getTop10Urls")
    void cenario10() {
        // Simule o retorno de uma lista de URLs
        List<ShorterUrl> top10Urls = createTop10Urls();
        when(shorterUrlService.getTop10Urls()).thenReturn(top10Urls);

        List<ShorterUrl> response = shorterUrlController.getTop10Urls();

        assertThat(response).isEqualTo(top10Urls);
    }

    private List<ShorterUrl> createTop10Urls() {
        List<ShorterUrl> urls = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ShorterUrl url = new ShorterUrl();
            url.setId((long) i);
            url.setShortUrl("http://short.url/" + i);
            url.setLongUrl("http://www.example.com/" + i);
            urls.add(url);
        }
        return urls;
    }


}