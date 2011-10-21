SELECT
    *
FROM
    employees
WHERE
	-- %if $cond
		ADDRESS like /*%L '%' cond */'%.com'
	-- %if $!cond
		-- :ID /+ ids +/= 3
