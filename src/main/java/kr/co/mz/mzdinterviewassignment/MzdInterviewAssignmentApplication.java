package kr.co.mz.mzdinterviewassignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MzdInterviewAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MzdInterviewAssignmentApplication.class, args);
    }

}
