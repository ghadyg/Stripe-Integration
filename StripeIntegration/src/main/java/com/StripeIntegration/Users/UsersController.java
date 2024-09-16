package com.StripeIntegration.Users;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("api/v1")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/signup")
    public void signup(@RequestBody UsernamePassword usernamePassword, HttpServletRequest request) throws UnirestException {
        usersService.addUser(usernamePassword,getSiteURL(request));
    }
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (usersService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody UsernamePassword usernamePassword)
    {
        String token = usersService.login(usernamePassword);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,token).build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String>  getStripeEvents(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        return usersService.handleStripeEvent(payload,sigHeader);
    }

    @GetMapping("/test")
    public String testing()
    {
        return "hello";
    }
}
