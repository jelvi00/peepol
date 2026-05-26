package org.peepol.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.peepol.dto.PersonDTO;

import java.util.Objects;

@Entity
@Table(name = "persons")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public final class Person extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, length = 40)
    @Pattern(regexp = "^[0-9+\\s]+$", message = "Phone number can only contain digits, +, and spaces")
    private String phoneNumber;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String bio;

    public PersonDTO.Response toDTOResponse() {
        return new PersonDTO.Response(id, name, phoneNumber, bio, this.getStatus().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(normalize(this.phoneNumber), normalize(person.phoneNumber));
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalize(phoneNumber));
    }

    private String normalize(String phone) {
        return phone == null ? null : phone.replace("+", "").replace(" ", "");
    }
}
