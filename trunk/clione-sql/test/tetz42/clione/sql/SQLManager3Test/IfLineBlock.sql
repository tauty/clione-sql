SELECT
    *
FROM
    employees
WHERE
	-- %IF cond
		ADDRESS like /*%L '%' cond */'%.com'
	-- %!IF cond :
		-- :ID /+ ids +/= 3
