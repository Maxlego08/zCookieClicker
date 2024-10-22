package fr.maxlego08.cookie.dto;

import fr.maxlego08.cookie.CookieUpgrade;

import java.math.BigDecimal;
import java.util.UUID;

public record CookieUpgradeDTO(UUID unique_id, CookieUpgrade upgrade, BigDecimal amount) {
}
