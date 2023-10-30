package com.example.shorterUrl.controller;

import com.example.shorterUrl.dto.AtualizarUrlDto;
import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.dto.UrlShortenerRequest;
import com.example.shorterUrl.dto.UrlShortenerResponse;
import com.example.shorterUrl.model.ShorterUrl;
import com.example.shorterUrl.service.ShorterUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ShorterUrlController {

    @Autowired
    private ShorterUrlService shorterUrlService;

    @Value("${get.request.path}")
    String getMappingRequestPath;

    @PostMapping("${get.request.path}")
    public UrlShortenerResponse shortenUrl(@RequestBody UrlShortenerRequest urlShortenerRequest, HttpServletRequest httpServletRequest, String shortUrl) {
        String longUrl = urlShortenerRequest.getLongUrl().trim();
        shorterUrlService.setServletRequest(httpServletRequest);
        return shorterUrlService.generateShortUrl(longUrl);
    }

//    @PostMapping
//    @Transactional
//    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl, UriComponentsBuilder uriBuilder) {
//        String shortUrl = shorterUrlService.shortenUrl(longUrl);
//        var urlLocation = uriBuilder.path("/{shortUrl}").buildAndExpand(shortUrl).toUri();
//        return ResponseEntity.created(urlLocation).body(shortUrl);
//    }

//    @GetMapping("/{shortUrl}")
//    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String shortUrl, HttpServletResponse response) {
//        String longUrl = shorterUrlService.expandUrl(shortUrl);
//
//        if (longUrl != null) {
//            longUrl = shorterUrlService.cleanAndFormatUrl(longUrl);
//            // Formate a URL, se necessário
//            return ResponseEntity.ok(longUrl);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


    @GetMapping("{key}")
    public ModelAndView forwardToLongUrl(@PathVariable("key") String key, HttpServletRequest httpServletRequest, HttpServletResponse resp) throws IOException {
        String shortUrl = (httpServletRequest.getHeader("host") + httpServletRequest.getRequestURI()).trim();
        shorterUrlService.setServletRequest(httpServletRequest);
        String longUrl = shorterUrlService.expandUrl(shortUrl);
        if (!(longUrl == null)) {
            shorterUrlService.incrementAccessCount(shortUrl);
            return new ModelAndView("redirect:" + longUrl);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }


    @GetMapping("/isPresent/{shortUrl}")
    public ResponseEntity<String> isShortUrlPresent(@PathVariable String shortUrl) {
        boolean isPresent = shorterUrlService.isShortUrlPresent(shortUrl);
        if (isPresent) {
            return ResponseEntity.ok("Short URL is present in the database.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/detalhar/{id}")
    public ResponseEntity<DetalhaUrlDto> detalhaUrlPorId(@PathVariable Long id) {
        DetalhaUrlDto detalhaUrl = shorterUrlService.detalhaUrlPorId(id);

        if (detalhaUrl != null) {
            return ResponseEntity.ok(detalhaUrl);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<DetalhaUrlDto>> listarUrl(@PageableDefault(size = 10, sort = "id") Pageable paginacao) {
        Page<DetalhaUrlDto> page = shorterUrlService.listarPaginado(paginacao);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUrl(@PathVariable Long id) {
        shorterUrlService.deleteUrlPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> atualizarLongUrl(@RequestBody AtualizarUrlDto atualizarUrl) {
        Long id = atualizarUrl.getId();
        String newLongUrl = atualizarUrl.getLongUrl();
        ShorterUrl updatedShorterUrl = shorterUrlService.atualizarLongUrl(id, newLongUrl);

        if (updatedShorterUrl != null) {
            return ResponseEntity.ok("Long URL updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top-10")
    public List<ShorterUrl> getTop10Urls() {
        return shorterUrlService.getTop10Urls();
    }
}