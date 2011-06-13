select
    *
from
    aaa
where
	/*?ID*/2 = id
	or shain_no in(
		/*$NO1*/100000
		,/*$NO2*/100003
	)
