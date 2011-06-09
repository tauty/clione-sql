SELECT
        *
    FROM
        people
    WHERE
        age = /*$age*/31
        AND name LIKE /*$namePart*/'%Y%'
