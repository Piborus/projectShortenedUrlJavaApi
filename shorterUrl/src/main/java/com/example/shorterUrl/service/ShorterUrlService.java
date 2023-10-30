package com.example.shorterUrl.service;

import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.dto.UrlShortenerResponse;
import com.example.shorterUrl.model.ShorterUrl;
import com.example.shorterUrl.repository.ShorterUrlRepository;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class ShorterUrlService {


    @Autowired
    private ShorterUrlRepository shorterUrlRepository;
    private String serviceUrl;

    @Value("${get.request.path}")
    String getMappingRequestPath;
    @Autowired
    private HttpServletRequest httpServletRequest;


    private void persist(ShorterUrl shorterUrl) {
        this.shorterUrlRepository.save(shorterUrl);
    }

    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        this.serviceUrl = httpServletRequest.getHeader("host") + httpServletRequest.getRequestURI().split(getMappingRequestPath)[0];
    }

    public UrlShortenerResponse generateShortUrl(String longUrl) {
        UrlShortenerResponse urlShortenerResponse = new UrlShortenerResponse();
        String shortUrl = generateValidatedShortUrl(longUrl);
        urlShortenerResponse.setShortUrl(shortUrl);
        //Saving to database before returning
        ShorterUrl shortenerMappings = new ShorterUrl();
        shortenerMappings.setLongUrl(longUrl);
        shortenerMappings.setShortUrl(shortUrl);
        if (!shorterUrlRepository.existsByLongUrl(longUrl)) {
            persist(shortenerMappings);
        }
        return urlShortenerResponse;
    }

    public String generateValidatedShortUrl(String longUrl) {
        if (shorterUrlRepository.existsByLongUrl(longUrl))
            return shorterUrlRepository.findByLongUrl(longUrl).getShortUrl();
        String shortUrlKey = generateShortKey(longUrl);
        while (shorterUrlRepository.existsByShortUrl(serviceUrl + "/" + shortUrlKey)) {
            shortUrlKey = generateValidatedShortUrl(longUrl);
        }
        return serviceUrl + "/" + shortUrlKey;
    }

    public static String generateShortKey(String longUrl) {
        if(longUrl == null) return "";
        longUrl = getWebsiteName(longUrl);
        if (longUrl.length() <= 2) {
            return longUrl.toLowerCase();
        }

        String shortUrl = "";

        shortUrl += (longUrl.substring(0, 2) + longUrl.substring(longUrl.length() - 1)).toLowerCase();

        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            shortUrl += String.valueOf(random.nextInt(10));
        }
        return shortUrl;
    }

//    public String shortenUrl(String longUrl) {
//        ShorterUrl existingShortUrl = shorterUrlRepository.findByLongUrl(longUrl);
//        if (existingShortUrl != null) {
//            return existingShortUrl.getShortUrl();
//        } else {
//            String shortUrl = generateShortUrl(longUrl);
//            ShorterUrl newShorterUrl = new ShorterUrl();
//            newShorterUrl.setLongUrl(longUrl);
//            newShorterUrl.setShortUrl(shortUrl);
//            newShorterUrl.setAccessNumber(0L);
//            shorterUrlRepository.save(newShorterUrl);
//            return shortUrl;
//        }
//    }

  /*  public String expandUrl(String shortUrl) {
        ShorterUrl shorterUrl = shorterUrlRepository.findByShortUrl(shortUrl);
        if (shorterUrl != null) {
            return shorterUrl.getLongUrl().trim();
        } else {
            return null;
        }
    }*/

    public String expandUrl(String shortUrl) {
        boolean shortUrlExist = shorterUrlRepository.existsByShortUrl(shortUrl);
        if (!shortUrlExist) {
            return null;
        }
        String longUrl = shorterUrlRepository.findByShortUrl(shortUrl).getLongUrl();
        return longUrl.trim();
    }

//    public String expandUrl(String shortUrl) {
//        ShorterUrl shorterUrl = shorterUrlRepository.findByShortUrl(shortUrl);
//        if (shorterUrl != null) {
//            return cleanAndFormatUrl(shorterUrl.getLongUrl());
//        } else {
//            return null;
//        }
//    }

    public boolean isShortUrlPresent(String shortUrl) {
        return shorterUrlRepository.existsByShortUrl(shortUrl);
    }

//    public String generateShortUrl() {
//        Random random = new Random();
//        byte[] randomBytes = new byte[6];
//        random.nextBytes(randomBytes);
//        String shortUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
//        return shortUrl;
//    }

    public String generateShortUrl1(String longUrl) {
        // Gere um código curto aleatório
        Random random = new Random();
        byte[] randomBytes = new byte[6];
        random.nextBytes(randomBytes);
        String shortUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Obtenha o nome do site a partir do longUrl
        String websiteName = getWebsiteName(longUrl);

        // Combine o nome do site com o código curto gerado
        String combinedShortUrl = websiteName + "/" + shortUrl;

        return combinedShortUrl;
    }

    private static String getWebsiteName(String websiteName) {
        websiteName = websiteName.toLowerCase();
        if (websiteName.contains("http") || websiteName.contains("www")) {
            websiteName = websiteName.substring(websiteName.indexOf(".") + 1);
        }
        return websiteName;
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
            String newShortUrl = generateShortUrl1(longUrl);
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



