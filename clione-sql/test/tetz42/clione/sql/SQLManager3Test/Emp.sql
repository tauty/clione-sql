select
	*
from
	employees
where
	address like /* %C '%', $namePrefix */'%o.com'
