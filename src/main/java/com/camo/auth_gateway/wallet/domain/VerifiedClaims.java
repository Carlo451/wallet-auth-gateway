package com.camo.auth_gateway.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class VerifiedClaims {

    @Column(name = "vc_subject")
    private String subject;

    @Column(name = "vc_given_name")
    private String givenName;

    @Column(name = "vc_family_name")
    private String familyName;

    @Column(name = "vc_birth_date")
    private LocalDate birthDate;

    @Column(name = "vc_age_over_18")
    private Boolean ageOver18;

    @Column(name = "vc_issuer")
    private String issuer;

    protected VerifiedClaims() {
    }

    public VerifiedClaims(String subject,
                          String givenName,
                          String familyName,
                          LocalDate birthDate,
                          Boolean ageOver18,
                          String issuer) {
        this.subject = subject;
        this.givenName = givenName;
        this.familyName = familyName;
        this.birthDate = birthDate;
        this.ageOver18 = ageOver18;
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Boolean getAgeOver18() {
        return ageOver18;
    }

    public String getIssuer() {
        return issuer;
    }
}