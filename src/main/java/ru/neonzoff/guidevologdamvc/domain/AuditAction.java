package ru.neonzoff.guidevologdamvc.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "audit_actions")
public class AuditAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "action", nullable = false, length = 2048)
    private String action;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    public AuditAction(Long userId, String username, String action, Date timestamp) {
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
    }
}




