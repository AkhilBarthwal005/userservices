package com.example.userservice.service;

import com.example.userservice.config.KafkaProducerClient;
import com.example.userservice.dto.SendEmailDTO;
import com.example.userservice.exception.InvalidPasswordException;
import com.example.userservice.exception.InvalidTokenException;
import com.example.userservice.models.Role;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repository.TokenRepository;
import com.example.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private TokenRepository tokenRepository;

    private KafkaProducerClient kafkaProducerClient;

    private ObjectMapper objectMapper;  // we use this to convert object to json string and vice versa this process is called serialization and deserialization

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, TokenRepository tokenRepository, KafkaProducerClient kafkaProducerClient, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaProducerClient = kafkaProducerClient;
        this.objectMapper = objectMapper;
    }

    public User signUp(String email , String password , String username) throws JsonProcessingException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            // user is already there in the DB.
            return optionalUser.get();
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        // we will send a message to kafka to send a welcome email to the users email
        SendEmailDTO sendEmailDTO = new SendEmailDTO();
        sendEmailDTO.setTo(email);
        sendEmailDTO.setFrom("akhil.barthwal005@gamil.com");
        sendEmailDTO.setSubject("Welcome to our platform");
        sendEmailDTO.setBody("Welcome to our platform, we are glad to have you here");

        kafkaProducerClient.sendEvent("sendEmail",objectMapper.writeValueAsString(sendEmailDTO));
        return userRepository.save(newUser);
    }

    public Token logIn(String email, String password){
        /*
         1> check if user exist in db or not
         2> if not throw exception and redirect to signup page
         3> if yes then compare the password , if match login success and if not through exception password mismatch
         */

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            // user does not exist in the db with given email
            return null;
        }
        User user = optionalUser.get();

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new InvalidPasswordException("Invalid password");
        }

        // login success and generate new token
        Token token = generateToken(user);
        tokenRepository.save(token);
        return token;


    }


    private Token generateToken(User user){
        LocalDate currentTime = LocalDate.now(); // for current time
        LocalDate thirtyDayFromCurrentTime = currentTime.plusDays(30);

        Date expiryDate =  Date.from(thirtyDayFromCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setExpiredAt(expiryDate);
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));
        token.setUser(user);
        return  token;
    }

    public void logOut(String token){
        // Validate if token exist in the db and is deleted as false
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndDeleted(token, false);

        if(optionalToken.isEmpty()){
            // throw exception
            throw new InvalidTokenException("Invalid token");
        }

        Token token1 = optionalToken.get();
        token1.setDeleted(true);
        tokenRepository.save(token1);
    }


    public User validateToken(String tokenValue){
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndDeleted(tokenValue,false);
        if(optionalToken.isEmpty()){
            throw new InvalidTokenException("Invalid token");
        }
        Token token = optionalToken.get();
        return token.getUser();

    }
}
