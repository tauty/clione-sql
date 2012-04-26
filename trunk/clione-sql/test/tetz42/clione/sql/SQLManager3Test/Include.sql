select
	*
from (
	select
		sub.*,
		ROWNUM num
	from
		/* %if(isEmp) %include('./Emp.sql') */(
			select
				*
			from
				PEOPLE
			where
				name like /* %C $namePrefix, '%' */'T%'
		) sub
)
where
	name like /* %C $namePrefix, '%' */'T%'
	and 2 <= num and num <= 3
