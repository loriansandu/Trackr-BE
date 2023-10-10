package com.sandu.trackr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Random;

@Entity
@Table(name="confirmationToken")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="confirmation_token")
    private Integer confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    //add no args and all args constructer and getter/setter

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id")
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        this.createdDate = new Date();
        Random random = new Random();
        this.confirmationToken  = 10000 + random.nextInt(90000);
    }
}
