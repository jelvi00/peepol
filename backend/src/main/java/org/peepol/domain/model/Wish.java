package org.peepol.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.peepol.domain.enums.WishStatus;

import java.util.Objects;

@Entity
@Table(name = "wishes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public final class Wish extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WishStatus wishStatus;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wish wish)) return false;
        return Objects.equals(normalize(this.name), normalize(wish.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalize(name));
    }

    private String normalize(String name) {
        return name.substring(0, 10).concat(id.toString());
    }


}
