SELECT
    *
FROM
    aaa
WHERE
	/*?ID*/2 = ID
	OR SHAIN_NO IN(
		/*$NO1*/100000
		,/*$NO2*/100003
	)
