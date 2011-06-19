SELECT
		*
	FROM
		employees
    WHERE
		ADDRESS like /*%L '%' $cond1 */'%.com'
 	   	OR ADDRESS like /*%L '%' $cond2 */'%.com'
