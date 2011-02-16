SELECT
        *
    FROM
        person
    WHERE
        age = /*$age*/31
        AND name LIKE /*$namePart*/'%Y%'
