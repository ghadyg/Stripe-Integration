package com.StripeIntegration.Users;

public record StripeRequest(
Long amount,String email,String productName
) {
}
