SELECT
    *
FROM
    EMPLOYEES
WHERE
	/*TEST1*/false = ID
	AND /*TEST2*/AAA_BBB = NAME
	AND /*TEST3*/106-0001 = ZIP_CODE
