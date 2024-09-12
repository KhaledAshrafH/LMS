package com.library.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String membershipId;
    private String contactInfo;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", membershipId='" + membershipId + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
