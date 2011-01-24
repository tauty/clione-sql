SELECT
    *
FROM
    EMPLOYEES
WHERE
	ABC IN /* $FISH */('tako', 'ika', 'namako')
	AND EFG IN /* FISH */(100, 200, 300)
