package fr.maxlego08.cookie.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CookiePlayerDTO(UUID unique_id, BigDecimal cookie) {
}
