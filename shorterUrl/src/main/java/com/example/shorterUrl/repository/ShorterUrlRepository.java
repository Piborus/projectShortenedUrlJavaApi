package com.example.shorterUrl.repository;

import com.example.shorterUrl.dto.DetalhaUrlDto;
import com.example.shorterUrl.model.ShorterUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShorterUrlRepository extends JpaRepository<ShorterUrl, Long> {

    ShorterUrl findByShortUrl(String shortUrl);

    ShorterUrl findByLongUrl(String longUrl);

    List<ShorterUrl> findFirst10ByOrderByAccessNumberDesc();

    Page<DetalhaUrlDto> findAllProjectedBy(Pageable paginacao);

    boolean existsByShortUrl(String shortUrl);

    boolean existsByLongUrl(String longUrl);
}
