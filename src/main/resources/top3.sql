WITH a AS (SELECT click_product_id,city_id FROM user_visit_action),
    b AS (SELECT product_id,product_name from product_info),
    c AS (SELECT city_id,area FROM city_info)
SELECT
a.click_product_id,
c.area,
b.product_name
FROM a
LEFT JOIN b
ON a.click_product_id=b.product_id
LEFT JOIN C
ON a.city_id=c.city_id;