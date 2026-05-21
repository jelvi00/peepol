package org.peepol.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.peepol.dto.PersonDTO;

@Entity
@Table(name = "persons")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public final class Person extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, length = 40)
    private String phoneNumber;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String bio;

    public PersonDTO.Response toDTOResponse() {
        return new PersonDTO.Response(id, name, phoneNumber, bio, this.getStatus().toString());
    }
}
