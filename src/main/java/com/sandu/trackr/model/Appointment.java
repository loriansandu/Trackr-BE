package com.sandu.trackr.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Random;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
//@EntityListeners(AuditingEntityListener.class)
public class Appointment {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private Long appointmentNumber = 0L;

    private String title;

    private String trainer;

    private Date date;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private User user;
}
