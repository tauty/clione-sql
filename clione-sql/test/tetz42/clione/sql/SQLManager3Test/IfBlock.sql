SELECT
    *
FROM
    employees
WHERE
	/*%IFLN cond */
		ADDRESS like /*%L '%' cond */'%.com'
	/*%!IFLN cond :
		ID /+ ids +/= 3 */
