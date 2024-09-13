package com.library.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a library member with associated information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    private Long id;
    private String name;
    private String membershipId;
    private String contactInfo;
    private LocalDateTime createdAt;

    public Member(String name, String membershipId, String contactInfo) {
        this.name = name;
        this.membershipId = membershipId;
        this.contactInfo = contactInfo;
        this.createdAt = LocalDateTime.now(); // Set createdAt to the current timestamp
    }

    @Override
    public String toString() {
        return String.format(
                "Member{id=%d, name='%s', membershipId='%s', contactInfo='%s', createdAt=%s}",
                id, name, membershipId, contactInfo, createdAt
        );
    }

    public void updateWith(Member updatedMember) {
        this.name = updatedMember.getName();
        this.membershipId = updatedMember.getMembershipId();
        this.contactInfo = updatedMember.getContactInfo();
    }
}
