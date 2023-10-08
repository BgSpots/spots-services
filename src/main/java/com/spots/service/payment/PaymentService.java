package com.spots.service.payment;

import com.spots.domain.Payment;
import com.spots.repository.PaymentRepository;
import com.spots.service.auth.JwtService;
import com.spots.service.common.SequenceGeneratorService;
import com.spots.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final JwtService jwtService;
    private final UserService userService;
    private final String url = "https://api.opennode.com/v1/charges/";
    private final String urlv2Api = "https://api.opennode.com/v2/charge/";
    private final String apiKey = "e92064ab-0799-467c-8876-25bb1a393422";

    @Transactional
    public long initiatePayment(int sats, boolean isAd, String jwt) {
        final var user = userService.getUser(jwtService.extractEmail(jwt));
        if (isAd) {
            final var paymentOptional = paymentRepository.findPaymentByUserId(user.getId());
            if (paymentOptional.isPresent()) {
                if (!paymentOptional.get().isUsed())
                    throw new InvalidPaymentIdException("This user already initiated payment!");
                else
                    throw new InvalidPaymentIdException(
                            "Payment is already finalised. New random spot is generated already");
            }
            final var payment =
                    Payment.builder()
                            .id(sequenceGeneratorService.generateSequence(Payment.SEQUENCE_NAME))
                            .userId(user.getId())
                            .isAdWatched(true)
                            .build();
            paymentRepository.insert(payment);
            return payment.getId();

        } else {
            final var paymentOptional = paymentRepository.findPaymentByUserId(user.getId());
            if (paymentOptional.isPresent()) {
                if (!paymentOptional.get().isUsed())
                    throw new InvalidPaymentIdException("This user already initiated payment!");
                else
                    throw new InvalidPaymentIdException(
                            "Payment is already finalised. New random spot is generated already");
            }
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
                            .id(sequenceGeneratorService.generateSequence(Payment.SEQUENCE_NAME))
                            .opennodeId(paymentCharge.getData().getId())
                            .userId(user.getId())
                            .status(paymentCharge.getData().getStatus())
                            .sats(sats)
                            .uri(paymentCharge.getData().getUri())
                            .lightningInvoice(paymentCharge.getData().getLightning_invoice().getPayreq())
                            .build();
            paymentRepository.insert(payment);
            return payment.getId();
        }
    }

    @Transactional
    public Payment getPayment(Long userId) {
        final var payment =
                paymentRepository
                        .findPaymentByUserId(userId)
                        .orElseThrow(() -> new InvalidPaymentIdException("Invalid user id"));

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
        paymentRepository.save(payment);
        return payment;
    }
}
