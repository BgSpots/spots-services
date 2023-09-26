package com.spots.service.auth;

import static org.mockito.Mockito.*;

import com.spots.common.input.RegisterBody;
import com.spots.domain.VerificationCode;
import com.spots.repository.UserRepository;
import com.spots.repository.VerificationCodeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationServiceTest {
    @InjectMocks private AuthenticationService authenticationService;
    @Mock private JavaMailSender mailSender;

    @Mock private VerificationCodeRepository verificationCodeRepository;

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    public AuthenticationServiceTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegister() throws MessagingException {
        RegisterBody registerBody = new RegisterBody();
        registerBody.setEmail("test@example.com");
        registerBody.setPassword("password");

        when(userRepository.existsUserByEmail(registerBody.getEmail())).thenReturn(false);
        VerificationCode verificationCodeMock =
                mock(VerificationCode.class); // Adjust the class name accordingly
        when(verificationCodeRepository.save(any(VerificationCode.class)))
                .thenReturn(verificationCodeMock);

        MimeMessage mimeMessageMock = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageMock);

        authenticationService.register(registerBody);

        verify(userRepository, times(1)).existsUserByEmail(registerBody.getEmail());
        verify(passwordEncoder, times(1)).encode(registerBody.getPassword());
    }
}
