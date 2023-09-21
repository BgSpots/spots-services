package com.spots.controller;

import com.spots.common.input.InitiatePaymentBody;
import com.spots.domain.Payment;
import com.spots.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
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
            @RequestBody InitiatePaymentBody initiatePaymentBody, HttpServletRequest request)
            throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var jwt = authHeader.substring(7);
        final var paymentId = paymentService.initiatePayment(initiatePaymentBody.getAmount(), jwt);
        return ResponseEntity.ok(paymentId);
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by id", description = "Returns a payment.")
    public ResponseEntity<?> getPayment(@PathVariable Long paymentId, HttpServletRequest request)
            throws IOException {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
}
