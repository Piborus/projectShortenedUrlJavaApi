package com.example.shorterUrl.service;

import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.model.ShorterUrl;
import com.example.shorterUrl.repository.ShorterUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class ShorterUrlService {

    @Autowired
    private ShorterUrlRepository shorterUrlRepository;

    public String shortenUrl(String longUrl) {
        ShorterUrl existingShortUrl = shorterUrlRepository.findByLongUrl(longUrl);
        if (existingShortUrl != null) {
            return existingShortUrl.getShortUrl();
        } else {
            String shortUrl = generateShortUrl();
            ShorterUrl newShorterUrl = new ShorterUrl();
            newShorterUrl.setLongUrl(longUrl);
            newShorterUrl.setShortUrl(shortUrl);
            shorterUrlRepository.save(newShorterUrl);
            return shortUrl;
        }
    }

    public String expandUrl(String shortUrl) {
        ShorterUrl shorterUrl = shorterUrlRepository.findByShortUrl(shortUrl);
        if (shorterUrl != null) {
            return shorterUrl.getLongUrl();
        } else {
            return null;
        }
    }

    public boolean isShortUrlPresent(String shortUrl) {
        ShorterUrl shorterUrl = shorterUrlRepository.findByShortUrl(shortUrl);
        return shorterUrl != null;
    }

    private String generateShortUrl() {
        Random random = new Random();
        byte[] randomBytes = new byte[6]; // Tamanho personalizável
        random.nextBytes(randomBytes);
        String shortUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return shortUrl;
    }

    public DetalhaUrlDto detalhaUrlPorId(Long id) {
        ShorterUrl shorterUrl = shorterUrlRepository.findById(id).orElse(null);
        if (shorterUrl != null) {
            return new DetalhaUrlDto(shorterUrl);
        } else {
            return null;
        }
    }

    public Page<DetalhaUrlDto> listarPaginado(Pageable paginacao) {
        return shorterUrlRepository.findAllProjectedBy(paginacao);
    }

    public void deleteUrlPorId(Long id) {
        shorterUrlRepository.deleteById(id);
    }

    public ShorterUrl atualizarLongUrl(Long id, String longUrl) {
        ShorterUrl existingShorterUrl = shorterUrlRepository.findById(id).orElse(null);

        if (existingShorterUrl != null) {
            existingShorterUrl.setLongUrl(longUrl);
            String newShortUrl = generateShortUrl();
            existingShorterUrl.setShortUrl(newShortUrl);
            return shorterUrlRepository.save(existingShorterUrl);
        } else {
            return null;
        }
    }

    public List<ShorterUrl> getTop10Urls() {
        return shorterUrlRepository.findFirst10ByOrderByAccessNumberDesc();
    }

    public void incrementAccessCount(String shortUrl) {
        ShorterUrl shorterUrl = shorterUrlRepository.findByShortUrl(shortUrl);
        if (shorterUrl != null) {
            Long accessNumber = shorterUrl.getAccessNumber();
            if (accessNumber == null) {
                accessNumber = 0L;
            } else {
                accessNumber++;
            }
            shorterUrl.setAccessNumber(accessNumber);
            shorterUrlRepository.save(shorterUrl);
        }
    }
}
