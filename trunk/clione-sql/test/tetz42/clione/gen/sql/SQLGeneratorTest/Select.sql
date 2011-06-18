SELECT
        *
    FROM
        people
    WHERE
        age = /*$age*/31
    -- %if $age
        AND name LIKE /*$namePart*/'%Y%'
