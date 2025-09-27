package net.h4bbo.echo.storage.models.user;

import io.netty.util.AttributeKey;
import lombok.*;
import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity(table = "users")
@ToString
@EqualsAndHashCode
public class UserData {
    public static final AttributeKey<UserData> DATA_KEY = AttributeKey.valueOf(UserData.class.getCanonicalName());
    @Getter
    @Id(auto = true)
    private int id;

    @Getter
    @Setter
    @Column(name = "username", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(name = "figure", nullable = false)
    private String figure;

    @Getter
    @Setter
    @Column(name = "sex", nullable = false, length = 1)
    private String sex;

    @Getter
    @Setter
    @Column(name = "figure_pool")
    private String figurePool;

    @Getter
    @Setter
    @Column(name = "rank", nullable = false)
    private int rank;

    @Getter
    @Setter
    @Column(name = "birthday", nullable = false, defaultValue = "'01.01.1970'")
    private String birthday;

    @Getter
    @Setter
    @Column(name = "motto", nullable = false, defaultValue = "''")
    private String motto;

    @Getter
    @Setter
    @Column(name = "console_mission", nullable = false, defaultValue = "''")
    private String consoleMission;

    @Getter
    @Setter
    @Column(name = "credit_balance", nullable = false, defaultValue = "0")
    private int credits;

    @Getter
    @Setter
    @Column(name = "ticket_balance", nullable = false, defaultValue = "0")
    private int tickets;

    @Getter
    @Setter
    @Column(name = "film_balance", nullable = false, defaultValue = "0")
    private int film;

    @Getter
    @Setter
    @Column(name = "is_online", nullable = false, defaultValue = "0")
    private boolean isOnline;

    @Getter
    @Setter
    @Column(name = "direct_mail", nullable = false, defaultValue = "0")
    private boolean directMail;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false, defaultValue = "(CURRENT_TIMESTAMP)")
    private LocalDateTime createdAt;
}
