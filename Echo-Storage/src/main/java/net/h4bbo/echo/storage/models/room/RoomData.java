package net.h4bbo.echo.storage.models.room;

import io.netty.util.AttributeKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;
import org.oldskooler.entity4j.annotations.NotMapped;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity(table = "rooms")
@ToString
@EqualsAndHashCode
public class RoomData {
    public static final AttributeKey<RoomData> DATA_KEY = AttributeKey.valueOf(RoomData.class.getCanonicalName());
    @Getter
    @Id(auto = true)
    private int id;

    @Getter
    @Setter
    @Column(name = "owner_id")
    private Integer ownerId;

    @Getter
    @Setter
    @Column(name = "category_id")
    private int categoryId;

    @Getter
    @Setter
    @Column(name = "name", nullable = false, defaultValue = "''")
    private String name;

    @Getter
    @Setter
    @Column(name = "owner_name", ignore = true)
    private String ownerName;

    @Getter
    @Setter
    @Column(name = "description", nullable = false, defaultValue = "''")
    private String description;

    @Getter
    @Setter
    @Column(name = "model", nullable = false, defaultValue = "'model_a'")
    private String model;

    @Getter
    @Setter
    @Column(name = "ccts")
    private String ccts;

    @Getter
    @Setter
    @Column(name = "visitors_now", nullable = false, defaultValue = "0")
    private int visitorsNow;

    @Getter
    @Setter
    @Column(name = "visitors_max", nullable = false, defaultValue = "25")
    private int visitorsMax;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false, defaultValue = "(CURRENT_TIMESTAMP)")
    private LocalDateTime createdAt;

}
