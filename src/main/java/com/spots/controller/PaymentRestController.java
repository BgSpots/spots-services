package com.spots.controller;

import com.spots.common.input.InitiatePaymentBody;
import com.spots.domain.Payment;
import com.spots.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
class PaymentRestController {
    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment.", description = "Initiate payment.")
    public ResponseEntity<?> initiatePayment(
            @RequestBody InitiatePaymentBody initiatePaymentBody, HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var jwt = authHeader.substring(7);
        final var paymentId =
                paymentService.initiatePayment(
                        initiatePaymentBody.getAmount(), initiatePaymentBody.isAd(), jwt);
        return ResponseEntity.ok(paymentId);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get payment by id", description = "Returns a payment.")
    public ResponseEntity<?> getPayment(@PathVariable Long userId, HttpServletRequest request) {
        Payment payment = paymentService.getPayment(userId);
        return ResponseEntity.ok(payment);
    }
}
