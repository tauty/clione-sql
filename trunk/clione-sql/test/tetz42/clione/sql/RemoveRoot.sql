SELECT
    *
FROM
    EMPLOYEES
WHERE
	-- ROOT  /* $ROOT */dummy
		AAA = true
		AND BBB = false
		AND CCC = 'abcdefg'
	AND DDD = true
		