package com.example.accountingassistant.model.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity(name = "faq")
public class Faq {

    @Id
    @Column(name = "faq_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faqId;
    @Column(name = "question")
    private String question;

    @Column(name = "answer", columnDefinition="TEXT")
    private String answer;

    @Column(name = "filePath")
    private String filePath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faq faq = (Faq) o;
        return Objects.equals(faqId, faq.faqId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(faqId);
    }

    @Override
    public String toString() {
        return "Faq{" +
                "faqId=" + faqId +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
