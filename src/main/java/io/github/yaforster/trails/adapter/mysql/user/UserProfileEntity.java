package io.github.yaforster.trails.adapter.mysql.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserProfileEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureFileName;
    private String profilePictureContentType;
    private OffsetDateTime profilePictureUpdatedAt;
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    @Column(name = "profile_picture_content")
    private byte[] profilePictureContent;

}
