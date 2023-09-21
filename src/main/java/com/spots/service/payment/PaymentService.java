package com.spots.service.payment;

import com.spots.domain.Payment;
import com.spots.repository.PaymentRepository;
import com.spots.service.auth.JwtService;
import com.spots.service.spots.InvalidPaymentIdException;
import com.spots.service.user.UserService;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PaymentService {
    public static AtomicLong paymentId = new AtomicLong(1L);
    private final PaymentRepository paymentRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final String url = "https://api.opennode.com/v1/charges/";
    private final String urlv2Api = "https://api.opennode.com/v2/charge/";
    private final String apiKey = "e92064ab-0799-467c-8876-25bb1a393422";

    public long initiatePayment(int sats, String jwt) throws IOException {
        final var user = userService.getUser(jwtService.extractEmail(jwt));
        PaymentRequest paymentRequest = new PaymentRequest(1);
        WebClient webClient = WebClient.create();
        final var paymentCharge =
                webClient
                        .post()
                        .uri(url)
                        .body(BodyInserters.fromValue(paymentRequest))
                        .header("Content-Type", "application/json")
                        .header("accept", "application/json")
                        .header("Authorization", apiKey)
                        .retrieve()
                        .bodyToMono(PaymentCharge.class)
                        .block();

        final var payment =
                Payment.builder()
                        .id(paymentId.get())
                        .opennodeId(paymentCharge.getData().getId())
                        .userId(user.getId())
                        .status(paymentCharge.getData().getStatus())
                        .sats(sats)
                        .uri(paymentCharge.getData().getUri())
                        .lightningInvoice(paymentCharge.getData().getLightning_invoice().getPayreq())
                        .build();
        paymentRepository.insert(payment);
        paymentId.incrementAndGet();
        return payment.getId();
    }

    public Payment getPayment(Long paymentId) throws IOException {
        final var payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() -> new InvalidPaymentIdException("Invalid payment id"));

        WebClient webClient = WebClient.create();
        final var paymentData =
                webClient
                        .get()
                        .uri(urlv2Api + payment.getOpennodeId())
                        .header("Content-Type", "application/json")
                        .header("accept", "application/json")
                        .header("Authorization", apiKey)
                        .retrieve()
                        .bodyToMono(PaymentData.class)
                        .block();
        payment.setStatus(paymentData.getData().getStatus());
        return payment;
    }
}
