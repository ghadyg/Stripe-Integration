package com.StripeIntegration.Users;

import com.StripeIntegration.authentication.JWTUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    @Value("${mailgun.DOMAIN}")
    private String YOUR_DOMAIN_NAME;

    @Value("${mailgun.APIKEY}")
    private String MailGun_APIKEY;

    @Value("${stripe.WebhookKey}")
    private String Stripe_WebhookKey;

    @Value("${Stripe.apiKey}")
    public String stripeKey;
    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager  authenticationManager;
    private final JWTUtil jwtUtil;

    public UsersService(UsersRepository repository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,JWTUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    public UsersEntity getUser(String username)
    {
        return repository.findById(username).orElseThrow(()->new UsernameNotFoundException("not found"));
    }

    public String login(UsernamePassword usernamePassword)
    {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                usernamePassword.username(),
                usernamePassword.password()
        ));
        UsersEntity usersEntity = (UsersEntity) authenticate.getPrincipal();
        return jwtUtil.issueToken(usersEntity.getUsername(),usersEntity.getAuthorities());

    }

    public void addUser(UsernamePassword user, String siteURL) throws UnirestException {
        UsersEntity usersEntity = new UsersEntity(user.username(),passwordEncoder.encode(user.password()));
        usersEntity.setEnabled(false);
        String randomCode = RandomString.make(64);
        usersEntity.setVerificationCode(randomCode);
        repository.save(usersEntity);
        sendVerificationEmail(usersEntity, siteURL);
    }
    private void sendVerificationEmail(UsersEntity user, String siteURL) throws UnirestException {
        String toAddress = user.getUsername();
        String fromAddress = "ghoshghady25@gmail.com";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";


        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/api/v1/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", MailGun_APIKEY)
                .field("from", fromAddress)
                .field("to", toAddress)
                .field("subject", subject)
                .field("html", content)
                .asJson();
        System.out.println(request.getBody());

    }

    public boolean verify(String verificationCode) {
        UsersEntity user = repository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            repository.save(user);

            return true;
        }

    }

    public ResponseEntity<String> handleStripeEvent(String payload, String header) throws StripeException {
        Event event = null;
        try {
            event = Webhook.constructEvent(payload, header, Stripe_WebhookKey);
        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(stripeObject);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionDelete(stripeObject);
                break;
            default:
                // Unexpected event type
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    private void handleSubscriptionDelete(StripeObject stripeObject) {
        Stripe.apiKey= stripeKey;
        Subscription subscription = (Subscription) stripeObject;
        String customerId = subscription.getCustomer();
        Optional<UsersEntity> optionalUsersEntity = repository.findByStripeIDLike(customerId);
        if(optionalUsersEntity.isEmpty())
            return;

        UsersEntity user = optionalUsersEntity.get();
        user.setProduct(null);
        repository.save(user);
    }

    private void handleCheckoutSessionCompleted(StripeObject stripeObject) throws StripeException {

        Stripe.apiKey= stripeKey;
        Session session = (Session) stripeObject;
        SessionListLineItemsParams params = SessionListLineItemsParams.builder().build();
        String customerId = session.getCustomer();
        Customer customer = Customer.retrieve(customerId);
        Optional<UsersEntity> optionalUsersEntity = repository.findById(customer.getEmail());
        String product = session.listLineItems().getData().get(0).getDescription();
        UsersEntity user;
        if(optionalUsersEntity.isPresent())
        {
            user = optionalUsersEntity.get();

            //send email confirming
        }
        else
        {
            user = new UsersEntity(customer.getEmail(), passwordEncoder.encode("password"));
            user.setEnabled(true);

            //Send Email Welcoming and giving credentials to change them

        }
        user.setProduct(product);
        user.setStripeID(customerId);
        repository.save(user);


    }

}
