package com.codec.system;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.codec.system.common.Generated;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.codec")
@Generated(reason = "Not testing logs")
public class CodecSystemApplicationApp {

  private static final Logger log = LoggerFactory.getLogger(CodecSystemApplicationApp.class);

//  @Bean
//  FirebaseMessaging firebaseMessaging() throws IOException {
//    GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
//      new ClassPathResource("serviceAccountKey.json").getInputStream()
//    );
//    FirebaseOptions firebaseOptions = FirebaseOptions.builder()
//      .setCredentials(googleCredentials).build();
//    FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "spp");
//    return FirebaseMessaging.getInstance(app);
//  }

  public static void main(String[] args) throws IOException {
    Environment env = SpringApplication.run(CodecSystemApplicationApp.class, args).getEnvironment();

    if (log.isInfoEnabled()) {
      log.info(ApplicationStartupTraces.of(env));
    }


  }
}
