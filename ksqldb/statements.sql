CREATE STREAM hotel_duration
        (hotel_id BIGINT, duration_category VARCHAR)
WITH (KAFKA_ТОРІС='expedia_ext', VALUE_FORMAT='json');

CREATE TABLE duration_hotels_agg AS
    SELECT duration_category,
           COUNT(hotel_id) AS total_hotels,
           COUNT_DISTINCT(hotel_id) AS distinct_hotels
    FROM hotel_duration
    GROUP BY duration_category
    EMIT CHANGES;

SELECT * FROM duration_hotels_agg EMIT CHANGES;
