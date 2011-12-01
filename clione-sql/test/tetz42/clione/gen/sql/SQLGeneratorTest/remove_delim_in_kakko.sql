SELECT
	*
FROM
	DUAL
WHERE
	ID = 'AAA'
	AND (
		( /* &isEnglish */
			FOO = 'FOO'
			AND BAR = 'BAR'
		)
		OR ( /* &isJapanese */
			HOGE = 'HOGE'
			AND FUGA = 'FUGA'
		)
	)