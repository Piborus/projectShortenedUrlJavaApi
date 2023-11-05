package com.example.shorterUrl.repository;

import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.model.ShorterUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ShorterUrlRepositoryTest {
    @Autowired
    private ShorterUrlRepository shorterUrlRepository;

    @Test
    @DisplayName("Should return ShorterUrl if it exists by short URL")
    void findByShortUrl_ShouldReturnShorterUrlIfExists() {
        // Cenário 1: Short URL existente no banco de dados
        ShorterUrl shorterUrl = new ShorterUrl("short", "long");
        shorterUrlRepository.save(shorterUrl);
        ShorterUrl foundUrl = shorterUrlRepository.findByShortUrl("short");
        assertNotNull(foundUrl);
        assertEquals("short", foundUrl.getShortUrl());
    }

    @Test
    @DisplayName("Should return null if Short URL does not exist")
    void findByShortUrl_ShouldReturnNullIfNotExists() {
        // Cenário 2: Short URL não existe no banco de dados
        ShorterUrl foundUrl = shorterUrlRepository.findByShortUrl("nonexistent");
        assertNull(foundUrl);
    }

    @Test
    @DisplayName("Should return ShorterUrl if it exists by long URL")
    void findByLongUrl_ShouldReturnShorterUrlIfExists() {
        // Cenário 1: Long URL existente no banco de dados
        ShorterUrl shorterUrl = new ShorterUrl("short", "long");
        shorterUrlRepository.save(shorterUrl);
        ShorterUrl foundUrl = shorterUrlRepository.findByLongUrl("long");
        assertNotNull(foundUrl);
        assertEquals("long", foundUrl.getLongUrl());
    }

    @Test
    @DisplayName("Should return the first 10 ShorterUrls ordered by access number")
    void findFirst10ByOrderByAccessNumberDesc_ShouldReturnFirst10ShorterUrls() {
        // Cenário 1: Existem mais de 10 registros no banco de dados
        for (int i = 0; i < 15; i++) {
            shorterUrlRepository.save(new ShorterUrl("short" + i, "long" + i, (long) i)); // Converta o valor int para Long
        }
        List<ShorterUrl> first10Urls = shorterUrlRepository.findFirst10ByOrderByAccessNumberDesc();
        assertEquals(10, first10Urls.size());
        assertEquals("short14", first10Urls.get(0).getShortUrl());
        assertEquals("short5", first10Urls.get(9).getShortUrl());
    }

    @Test
    @DisplayName("Should return an empty list when there are less than 10 ShorterUrls")
    void findFirst10ByOrderByAccessNumberDesc_ShouldReturnEmptyListIfFewerThan10ShorterUrls() {
        // Cenário 2: Existem menos de 10 registros no banco de dados
        List<ShorterUrl> first10Urls = shorterUrlRepository.findFirst10ByOrderByAccessNumberDesc();
        assertTrue(first10Urls.isEmpty());
    }

    @Test
    @DisplayName("Should return ShorterUrls with valid pagination")
    void findAllProjectedBy_ShouldReturnShorterUrlsWithValidPagination() {
        // Cenário 1: Páginação válida com resultados
        // Insira dados de teste no banco de dados
        for (int i = 1; i <= 10; i++) {
            shorterUrlRepository.save(new ShorterUrl("short" + i, "long" + i));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertEquals(5, resultPage.getNumberOfElements());

        // Verifique se a página possui os resultados esperados.
    }

    @Test
    @DisplayName("Should return an empty page with invalid pagination")
    void findAllProjectedBy_ShouldReturnEmptyPageWithInvalidPagination() {
        // Cenário 2: Páginação válida sem resultados
        // Implemente um cenário de teste com paginação válida, mas sem resultados.
        // Certifique-se de criar dados insuficientes para preencher a página.

        Pageable pageable = PageRequest.of(1, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    @DisplayName("Should return an empty page with invalid pagination parameters")
    void findAllProjectedBy_ShouldReturnEmptyPageWithInvalidPaginationParameters() {
        // Cenário 3: Páginação inválida (por exemplo, página negativa)
        // Implemente um cenário de teste com parâmetros de paginação inválidos, como uma página negativa.
        // Certifique-se de criar dados suficientes para testar a paginação.

        Pageable pageable = PageRequest.of(0, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertTrue(resultPage.isEmpty());
    }
}
