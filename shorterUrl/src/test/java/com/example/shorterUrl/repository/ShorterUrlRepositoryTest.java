package com.example.shorterUrl.repository;

import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.model.ShorterUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Short URL existente no banco de dados")
    void findByShortUrl_ShouldReturnShorterUrlIfExists() {
        // Cenário 1:
        ShorterUrl shorterUrl = new ShorterUrl("short", "long");
        shorterUrlRepository.save(shorterUrl);
        ShorterUrl foundUrl = shorterUrlRepository.findByShortUrl("short");
        assertNotNull(foundUrl);
        assertEquals("short", foundUrl.getShortUrl());
    }

    @Test
    @DisplayName("Short URL não existe no banco de dados")
    void findByShortUrl_ShouldReturnNullIfNotExists() {
        // Cenário 2:
        ShorterUrl foundUrl = shorterUrlRepository.findByShortUrl("nonexistent");
        assertNull(foundUrl);
    }

    @Test
    @DisplayName("Long URL existente no banco de dados")
    void findByLongUrl_ShouldReturnShorterUrlIfExists() {
        // Cenário 1:
        ShorterUrl shorterUrl = new ShorterUrl("short", "long");
        shorterUrlRepository.save(shorterUrl);
        ShorterUrl foundUrl = shorterUrlRepository.findByLongUrl("long");
        assertNotNull(foundUrl);
        assertEquals("long", foundUrl.getLongUrl());
    }

    @Test
    @DisplayName("Existem mais de 10 registros no banco de dados")
    void findFirst10ByOrderByAccessNumberDesc_ShouldReturnFirst10ShorterUrls() {
        for (int i = 0; i < 15; i++) {
            shorterUrlRepository.save(new ShorterUrl("short" + i, "long" + i, (long) i)); // Converta o valor int para Long
        }
        List<ShorterUrl> first10Urls = shorterUrlRepository.findFirst10ByOrderByAccessNumberDesc();
        assertEquals(10, first10Urls.size());
        assertEquals("short14", first10Urls.get(0).getShortUrl());
        assertEquals("short5", first10Urls.get(9).getShortUrl());
    }

    @Test
    @DisplayName("Existem menos de 10 registros no banco de dados")
    void findFirst10ByOrderByAccessNumberDesc_ShouldReturnEmptyListIfFewerThan10ShorterUrls() {
        List<ShorterUrl> first10Urls = shorterUrlRepository.findFirst10ByOrderByAccessNumberDesc();
        assertTrue(first10Urls.isEmpty());
    }

    @Test
    @DisplayName("Páginação válida com resultados")
    void findAllProjectedBy_ShouldReturnShorterUrlsWithValidPagination() {
        for (int i = 1; i <= 10; i++) {
            shorterUrlRepository.save(new ShorterUrl("short" + i, "long" + i));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertEquals(5, resultPage.getNumberOfElements());

    }

    @Test
    @DisplayName("Páginação válida sem resultados")
    void findAllProjectedBy_ShouldReturnEmptyPageWithInvalidPagination() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    @DisplayName("Páginação inválida (por exemplo, página negativa)")
    void findAllProjectedBy_ShouldReturnEmptyPageWithInvalidPaginationParameters() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<DetalhaUrlDto> resultPage = shorterUrlRepository.findAllProjectedBy(pageable);
        assertTrue(resultPage.isEmpty());
    }


}
