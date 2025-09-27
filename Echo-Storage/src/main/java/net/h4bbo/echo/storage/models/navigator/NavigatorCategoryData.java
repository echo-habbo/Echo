package net.h4bbo.echo.storage.models.navigator;

import io.netty.util.AttributeKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity(table = "navigator_categories")
@ToString
@EqualsAndHashCode
public class NavigatorCategoryData {
    public static final AttributeKey<NavigatorCategoryData> DATA_KEY = AttributeKey.valueOf(NavigatorCategoryData.class.getCanonicalName());
    @Getter
    @Setter
    @Id(auto = false)
    @Column(name = "id", nullable = false)
    private int id;

    @Getter
    @Setter
    @Column(name = "order_id", nullable = false, defaultValue = "0")
    private int orderId;

    @Getter
    @Setter
    @Column(name = "parent_id", nullable = false, defaultValue = "0")
    private int parentId;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "rank_id", nullable = false, defaultValue = "1")
    private int rankId;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false, defaultValue = "(CURRENT_TIMESTAMP)")
    private LocalDateTime createdAt;
}
